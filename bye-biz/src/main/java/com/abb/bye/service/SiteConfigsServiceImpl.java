package com.abb.bye.service;

import com.abb.bye.Constants;
import com.abb.bye.SystemEnv;
import com.abb.bye.client.domain.SiteConfigsDO;
import com.abb.bye.client.service.SiteConfigsService;
import com.abb.bye.mapper.SiteConfigMapper;
import com.abb.bye.utils.CommonThreadPool;
import com.abb.bye.utils.Tracer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
@Service("siteConfigsService")
public class SiteConfigsServiceImpl implements SiteConfigsService, InitializingBean {
    @Resource
    private SiteConfigMapper siteConfigMapper;
    @Resource
    private SystemEnv systemEnv;
    private Map<String, List<SiteConfigsDO>> domainConfigMapping = new LinkedHashMap<>();
    private Map<String, Integer> domainSiteMapping = new HashMap<>();
    private Map<Integer, Set<String>> domainMapping = new HashMap<>();
    private Map<Integer, Map<String, String>> siteSystemConfigMapping = new HashMap<>();
    private static Tracer tracer = new Tracer("SITE_CONFIG");

    @Override
    public void insert(SiteConfigsDO siteConfigsDO) {
        siteConfigMapper.insert(siteConfigsDO);
    }

    @Override
    public boolean update(SiteConfigsDO siteConfigsDO) {
        return siteConfigMapper.update(siteConfigsDO);
    }

    @Override
    public List<SiteConfigsDO> list(int site) {
        return siteConfigMapper.list(site);
    }

    @Override
    public SiteConfigsDO get(long id) {
        return siteConfigMapper.get(id);
    }

    @Override
    public String getFromDB(int site, String configKey) {
        SiteConfigsDO configsDO = siteConfigMapper.getConfig(site, configKey, systemEnv.current().name());
        return configsDO == null ? null : configsDO.getContent();
    }

    @Override
    public List<SiteConfigsDO> match(String domain) {
        List<SiteConfigsDO> configs = new ArrayList<>();
        for (Map.Entry<String, List<SiteConfigsDO>> entry : domainConfigMapping.entrySet()) {
            if (domain.contains(entry.getKey())) {
                configs.addAll(entry.getValue());
                break;
            }
        }
        return configs;
    }

    @Override
    public Map<Integer, Set<String>> getDomains() {
        return domainMapping;
    }

    @Override
    public String getSiteSystemConfig(int site, String configKey) {
        Map<String, String> m = siteSystemConfigMapping.get(site);
        String content = m == null ? null : m.get(configKey);
        if (content != null) {
            return content;
        }
        m = siteSystemConfigMapping.get(0);
        return m == null ? null : m.get(configKey);
    }

    @Override
    public Integer matchSite(String domain) {
        for (Map.Entry<String, Integer> entry : domainSiteMapping.entrySet()) {
            if (domain.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void reload() {
        try {
            Map<String, List<SiteConfigsDO>> tmp = new LinkedHashMap<>();
            Map<Integer, Set<String>> tmpDomainMapping = new HashMap<>();
            Map<Integer, Map<String, String>> tmpSiteSystemConfigMapping = new HashMap<>();
            Map<String, Integer> tmpDomainSiteMapping = new HashMap<>();
            List<SiteConfigsDO> siteConfigsDOS = siteConfigMapper.listAll(systemEnv.current().name());
            for (SiteConfigsDO siteConfigsDO : siteConfigsDOS) {
                if (siteConfigsDO.getConfigKey().startsWith(Constants.SYSTEM_CONFIG_PREFIX)) {
                    Map<String, String> m = tmpSiteSystemConfigMapping.get(siteConfigsDO.getSite());
                    if (m == null) {
                        m = new HashMap<>();
                        tmpSiteSystemConfigMapping.put(siteConfigsDO.getSite(), m);
                    }
                    m.put(siteConfigsDO.getConfigKey(), siteConfigsDO.getContent());
                    continue;
                }

                if (StringUtils.isBlank(siteConfigsDO.getDomains())) {
                    continue;
                }
                String[] domains = StringUtils.split(siteConfigsDO.getDomains(), ",");
                for (String domain : domains) {
                    List<SiteConfigsDO> list = tmp.get(domain);
                    tmpDomainSiteMapping.put(domain, siteConfigsDO.getSite());
                    if (list == null) {
                        list = new ArrayList<>();
                        tmp.put(domain, list);
                    }
                    list.add(siteConfigsDO);
                    Set<String> set = tmpDomainMapping.get(siteConfigsDO.getSite());
                    if (set == null) {
                        set = new HashSet<>();
                        tmpDomainMapping.put(siteConfigsDO.getSite(), set);
                    }
                    set.add(domain);
                }
            }
            domainConfigMapping = tmp;
            domainMapping = tmpDomainMapping;
            siteSystemConfigMapping = tmpSiteSystemConfigMapping;
            domainSiteMapping = tmpDomainSiteMapping;
            tracer.trace("loadConfigMapping:" + domainConfigMapping);
            tracer.trace("loadDomainMapping:" + tmpDomainMapping);
            tracer.trace("loadSiteSystemConfigMapping:" + siteSystemConfigMapping);
            tracer.trace("loadDomainSiteMapping:" + domainSiteMapping);
        } catch (Throwable e) {
            tracer.trace("Error reset", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        reload();
        CommonThreadPool.getScheduledExecutor().scheduleAtFixedRate(() -> reload(), 0, 2, TimeUnit.MINUTES);
    }
}
