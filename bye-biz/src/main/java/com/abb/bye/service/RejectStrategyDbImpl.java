package com.abb.bye.service;

import com.abb.bye.client.domain.RejectStrategyConfig;
import com.abb.bye.client.service.ProgrammeSourceService;
import com.abb.bye.client.service.RejectStrategy;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cenpeng.lwm
 * @since 2019/3/8
 */
@Service("rejectStrategy")
public class RejectStrategyDbImpl implements RejectStrategy {
    private static Logger logger = LoggerFactory.getLogger(RejectStrategyDbImpl.class);
    private Map<Integer, Set<String>> sourceIds = new ConcurrentHashMap<>();
    @Resource
    private ProgrammeSourceService programmeSourceService;

    @Override
    public void init(int site, RejectStrategyConfig rejectStrategyConfig) {
        List<String> ids;
        if (rejectStrategyConfig.isOnlyInsert()) {
            ids = programmeSourceService.getSourceIds(site, null);
        } else {
            DateTime dateTime = DateTime.now().plusSeconds(rejectStrategyConfig.getUpdateIntervalSeconds());
            ids = programmeSourceService.getSourceIds(site, dateTime.toDate());
        }
        Set<String> set = new HashSet<>(ids);
        logger.info("init site:" + site + "rejectStrategyConfig:" + rejectStrategyConfig + " ids:" + ids);
        sourceIds.put(site, set);
    }

    @Override
    public void destroy(int site) {
        sourceIds.remove(site);
    }

    @Override
    public boolean reject(int site, String sourceId, RejectStrategyConfig rejectStrategyConfig) {
        Set<String> ids = sourceIds.get(site);
        return ids == null ? false : ids.contains(sourceId);
    }
}
