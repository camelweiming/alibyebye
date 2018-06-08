package com.abb.bye.dao.impl;

import com.abb.bye.client.domain.ItemDO;
import com.abb.bye.client.domain.query.ItemQueryDO;
import com.abb.bye.dao.ItemDAO;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author camelweiming@163.com
 * @since 2018/6/7
 */
@Service
public class ItemDAOImpl extends SqlSessionDaoSupport implements ItemDAO {
    @Override
    public long insert(ItemDO itemDO) {
        Date current = new Date();
        itemDO.setGmtCreate(current);
        itemDO.setGmtModified(current);
        getSqlSession().insert("item.insert", itemDO);
        return itemDO.getId();
    }

    @Override
    public ItemDO get(long id) {
        return getSqlSession().selectOne("item.get", id);
    }

    @Override
    public List<ItemDO> list(ItemQueryDO query) {
        return getSqlSession().selectList("item.list", query);
    }
}
