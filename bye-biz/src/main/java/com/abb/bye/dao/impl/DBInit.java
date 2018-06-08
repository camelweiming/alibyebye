package com.abb.bye.dao.impl;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author camelweiming@163.com
 * @since 2018/6/7
 */
@Service
public class DBInit extends SqlSessionDaoSupport {
    @PostConstruct
    void init() {
        getSqlSession().update("init.createItemTable");
    }
}
