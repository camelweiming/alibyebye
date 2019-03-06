package com.abb.bye.web;

import com.abb.bye.client.domain.CategoryDO;
import com.abb.bye.client.service.CategoryService;
import com.alibaba.boot.velocity.annotation.VelocityLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2018/10/2
 */
@Controller
public class CategoryController {
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    @Resource
    private CategoryService categoryService;
    private Comparator<CategoryDO> PRIORITY_COMPARATOR = Comparator.comparingInt(CategoryDO::getSort);

    @RequestMapping(value = "category.htm", method = {RequestMethod.POST, RequestMethod.GET})
    @VelocityLayout("/velocity/layout/index.vm")
    String category(Model model, @RequestParam(required = false) List<Long> tags, @RequestParam(required = false) Byte status) {
        String vm = "category";
        List<CategoryDO> list = categoryService.list();
        Collections.sort(list, PRIORITY_COMPARATOR);
        model.addAttribute("list", list);
        return vm;
    }

    @RequestMapping(value = "edit_category.htm", method = {RequestMethod.POST, RequestMethod.GET})
    @VelocityLayout("/velocity/layout/index.vm")
    String editApp(Model model, HttpServletRequest request, @RequestParam(required = false) Long id) {
        String vm = "add_category";
        CategoryDO category = categoryService.get(id);
        model.addAttribute("category", category);
        model.addAttribute("id", category.getId());
        model.addAttribute("name", category.getName());
        model.addAttribute("sort", category.getSort());
        return vm;
    }

    @RequestMapping(value = "add_category.htm", method = {RequestMethod.POST, RequestMethod.GET})
    @VelocityLayout("/velocity/layout/index.vm")
    ModelAndView addCategory(Model model, HttpServletRequest request,
                             @RequestParam(required = false) Long id,
                             @RequestParam(required = false) Integer act,
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) Integer sort) {
        if (act != null) {
            CategoryDO categoryDO = new CategoryDO();
            categoryDO.setName(name);
            categoryDO.setSort(sort);
            try {
                if (id == null) {
                    categoryService.insert(categoryDO);
                } else {
                    categoryDO.setId(id);
                    categoryService.update(categoryDO);
                }
            } catch (Throwable e) {
                logger.error("Error insert category:" + categoryDO, e);
                model.addAttribute("errorMsg", e.getMessage());
                return new ModelAndView("add_category");
            }
            return new ModelAndView("redirect:/category.htm");
        }
        return new ModelAndView("add_category");
    }
}
