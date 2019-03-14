package com.abb.bye.service;

import com.abb.bye.client.domain.ProxyDO;
import com.abb.bye.client.service.ProxyService;
import com.abb.bye.mapper.ProxyMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/13
 */
@Service("proxyService")
public class ProxyServiceImpl implements ProxyService, InitializingBean {
    @Resource
    private ProxyMapper proxyMapper;

    @Override
    public List<ProxyDO> list(int count, double successRate) {
        return proxyMapper.list(count, successRate);
    }

    @Override
    public ProxyDO get() {
        List<ProxyDO> list = list(1, 0);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
