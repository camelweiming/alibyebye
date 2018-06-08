package com.abb.bye.dao;

import com.abb.bye.client.domain.ItemDO;
import com.abb.bye.client.domain.query.ItemQueryDO;
import java.util.List;

/**
 * @author camelweiming@163.com
 * @since 2018/6/7
 */
public interface ItemDAO {

    long insert(ItemDO itemDO);

    ItemDO get(long id);

    List<ItemDO> list(ItemQueryDO query);
}
