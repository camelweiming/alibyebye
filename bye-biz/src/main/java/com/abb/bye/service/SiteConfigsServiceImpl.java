package com.abb.bye.service;

import com.abb.bye.client.domain.SiteConfigsDO;
import com.abb.bye.client.service.SiteConfigsService;
import com.abb.bye.mapper.SiteConfigMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
@Service("siteConfigsService")
public class SiteConfigsServiceImpl implements SiteConfigsService {
    @Resource
    private SiteConfigMapper siteConfigMapper;

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
}
