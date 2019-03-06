package com.abb.bye.service;

import com.abb.bye.client.domain.CategoryDO;
import com.abb.bye.client.service.CategoryService;
import com.abb.bye.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public CategoryDO get(long id) {
        return categoryMapper.get(id);
    }

    @Override
    public List<CategoryDO> list() {
        return categoryMapper.list();
    }

    @Override
    public void insert(CategoryDO categoryDO) {
        categoryMapper.insert(categoryDO);
    }

    @Override
    public void update(CategoryDO categoryDO) {
        categoryMapper.update(categoryDO);
    }
}
