package com.abb.bye.test;

import com.abb.bye.client.domain.ItemDO;
import com.abb.bye.client.domain.query.ItemQueryDO;
import com.abb.bye.dao.ItemDAO;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author camelweiming@163.com
 * @since 2018/6/7
 */
public class ItemDAOTest extends BaseDAOTest {
    @Resource
    private ItemDAO itemDAO;

    @Test
    public void insert() {
        ItemDO itemDO = new ItemDO();
        itemDO.setTitle("测试2");
        itemDO.setPics("");
        itemDO.setPriority(100);
        itemDO.setStatus((byte) 0);
        long id = itemDAO.insert(itemDO);
        System.out.println(id);
    }

    @Test
    public void testGet() {
        System.out.println(itemDAO.get(1));
    }

    @Test
    public void testQuery(){
        ItemQueryDO q=new ItemQueryDO();
        q.setLength(10);
        System.out.println(itemDAO.list(q));
    }
}
