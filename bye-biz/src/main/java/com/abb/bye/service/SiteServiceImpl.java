package com.abb.bye.service;

import com.abb.bye.client.domain.SiteDO;
import com.abb.bye.client.domain.enums.SiteTag;
import com.abb.bye.client.service.SiteService;
import com.abb.bye.mapper.SiteMapper;
import com.abb.bye.utils.CommonThreadPool;
import com.abb.bye.utils.Tracer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
@Service("siteService")
public class SiteServiceImpl implements SiteService, InitializingBean {
    @Resource
    private SiteMapper siteMapper;
    private Comparator<SiteDO> PRIORITY_COMPARATOR = (o1, o2) -> o2.getPriority() - o1.getPriority();

    public void reload() {
        try {
            SiteMapping.reset(siteMapper.list());
        } catch (Throwable e) {
            new Tracer("APP_RESET").trace("Error reset", e);
        }
    }

    @Override
    public List<SiteDO> listFromDB() {
        return siteMapper.list();
    }

    @Override
    public List<SiteDO> filter(List<SiteDO> list, List<SiteTag> tags, Byte status) {
        return list.stream().filter(appDO -> {
            if (status != null) {
                if (status.byteValue() != appDO.getStatus()) {
                    return false;
                }
            }
            if (CollectionUtils.isNotEmpty(tags)) {
                for (SiteTag tag : tags) {
                    if ((tag.getValue() & appDO.getTags()) != tag.getValue()) {
                        return false;
                    }
                }
            }
            return true;
        }).sorted(PRIORITY_COMPARATOR).collect(Collectors.toList());
    }

    @Override
    public SiteDO getFromDB(long id) {
        return siteMapper.get(id);
    }

    @Override
    public SiteDO getBySiteKeyFromDB(String appKey) {
        return siteMapper.getBySiteKey(appKey);
    }

    @Override
    public SiteDO getBySiteFromDB(int site) {
        return siteMapper.getBySite(site);
    }

    @Override
    public void insert(SiteDO appDO) {
        siteMapper.insert(appDO);
    }

    @Override
    public boolean update(SiteDO appDO) {
        return siteMapper.update(appDO);
    }

    @Override
    public void afterPropertiesSet() {
        SiteMapping.reset(siteMapper.list());
        CommonThreadPool.getScheduledExecutor().scheduleAtFixedRate(() -> reload(), 0, 2, TimeUnit.MINUTES);
    }
}
