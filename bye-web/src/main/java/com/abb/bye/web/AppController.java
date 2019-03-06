package com.abb.bye.web;

import com.abb.bye.client.domain.CategoryDO;
import com.abb.bye.client.domain.SiteDO;
import com.abb.bye.client.domain.enums.SiteTag;
import com.abb.bye.client.service.CategoryService;
import com.abb.bye.client.service.SiteService;
import com.abb.bye.utils.CommonUtils;
import com.alibaba.boot.velocity.annotation.VelocityLayout;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2018/10/2
 */
@Controller
public class AppController {
    private static final Logger logger = LoggerFactory.getLogger(AppController.class);
    @Resource
    private SiteService siteService;
    @Resource
    private CategoryService categoryService;
    private Comparator<SiteDO> PRIORITY_COMPARATOR = (o1, o2) -> o2.getPriority() - o1.getPriority();

    @RequestMapping(value = "site.htm", method = {RequestMethod.POST, RequestMethod.GET})
    @VelocityLayout("/velocity/layout/index.vm")
    String site(Model model, @RequestParam(required = false) List<Long> tags, @RequestParam(required = false) Byte status) {
        String vm = "site";
        model.addAttribute("status", status);
        long t = CommonUtils.bitSetValue(tags);
        model.addAttribute("tags", t);
        List<SiteTag> siteTags = new ArrayList<>();
        if (tags != null) {
            tags.forEach(k -> siteTags.add(SiteTag.valueOf(k)));
        }
        if (status == null) {
            status = SiteDO.STATUS_ENABLE;
        }
        List<SiteDO> list = siteService.filter(siteService.listFromDB(), siteTags, status);
        Collections.sort(list, PRIORITY_COMPARATOR);
        List<SiteVO> vos = new ArrayList<>();
        list.forEach(a -> vos.add(build(a)));
        model.addAttribute("list", vos);
        model.addAttribute("tagsValues", SiteTag.values());
        return vm;
    }

    public SiteVO build(SiteDO a) {
        SiteVO v = new SiteVO();
        CommonUtils.copyPropertiesQuietly(a, v);
        List<Long> ids = CommonUtils.toLongList(a.getCategories(), ",");
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (long id : ids) {
            CategoryDO c = categoryService.get(id);
            if (i != 0) {
                sb.append(",");
            }
            sb.append(c == null ? "NONE" : c.getName());
        }
        v.setCategoryNames(sb.toString());
        return v;
    }

    @RequestMapping(value = "edit_site.htm", method = {RequestMethod.POST, RequestMethod.GET})
    @VelocityLayout("/velocity/layout/index.vm")
    String editSite(Model model, HttpServletRequest request, @RequestParam(required = false) Long id) {
        String vm = "add_site";
        SiteDO siteDO = siteService.getFromDB(id);
        model.addAttribute("siteKey", siteDO.getSiteKey());
        model.addAttribute("name", siteDO.getName());
        model.addAttribute("logo", siteDO.getLogo());
        model.addAttribute("site", siteDO.getSite());
        model.addAttribute("h5Url", siteDO.getH5Url());
        model.addAttribute("iosUrl", siteDO.getIosUrl());
        model.addAttribute("androidUrl", siteDO.getAndroidUrl());
        model.addAttribute("attributes", siteDO.getAttributes());
        model.addAttribute("minVersion", siteDO.getMinVersion());
        model.addAttribute("status", siteDO.getStatus());
        model.addAttribute("tags", siteDO.getTags());
        model.addAttribute("tagsValues", SiteTag.values());
        model.addAttribute("id", siteDO.getId());
        model.addAttribute("priority", siteDO.getPriority());
        model.addAttribute("categories", siteDO.getCategories());
        List<CategoryDO> categoryDOS = categoryService.list();
        model.addAttribute("categoryList", categoryDOS);
        return vm;
    }

    @RequestMapping(value = "add_site.htm", method = {RequestMethod.POST, RequestMethod.GET})
    @VelocityLayout("/velocity/layout/index.vm")
    ModelAndView addSite(Model model, HttpServletRequest request,
                         @RequestParam(required = false) Long id,
                         @RequestParam(required = false) Integer act,
                         @RequestParam(required = false) String siteKey,
                         @RequestParam(required = false) String name,
                         @RequestParam(required = false) String logo,
                         @RequestParam(required = false) Integer site,
                         @RequestParam(required = false) String h5Url,
                         @RequestParam(required = false) String iosUrl,
                         @RequestParam(required = false) String androidUrl,
                         @RequestParam(required = false) String attributes,
                         @RequestParam(required = false) String minVersion,
                         @RequestParam(required = false) Byte status,
                         @RequestParam(required = false) Integer priority,
                         @RequestParam(required = false) String categories,
                         @RequestParam(required = false) List<Long> tags) {
        String vm = "add_site";
        siteKey = CommonUtils.setNullWhenBlank(siteKey);
        name = CommonUtils.setNullWhenBlank(name);
        logo = CommonUtils.setNullWhenBlank(logo);
        h5Url = CommonUtils.setNullWhenBlank(h5Url);
        iosUrl = CommonUtils.setNullWhenBlank(iosUrl);
        androidUrl = CommonUtils.setNullWhenBlank(androidUrl);
        attributes = CommonUtils.setNullWhenBlank(attributes);
        minVersion = CommonUtils.setNullWhenBlank(minVersion);
        if (priority == null) {
            priority = 0;
        }
        model.addAttribute("siteKey", siteKey);
        model.addAttribute("priority", priority);
        model.addAttribute("name", name);
        model.addAttribute("logo", logo);
        model.addAttribute("site", site);
        model.addAttribute("h5Url", h5Url);
        model.addAttribute("iosUrl", iosUrl);
        model.addAttribute("androidUrl", androidUrl);
        model.addAttribute("attributes", attributes);
        model.addAttribute("minVersion", minVersion);
        model.addAttribute("status", status);
        model.addAttribute("categories", categories);
        long t = CommonUtils.bitSetValue(tags);
        model.addAttribute("tags", t);
        model.addAttribute("tagsValues", SiteTag.values());
        List<CategoryDO> categoryDOS = categoryService.list();
        model.addAttribute("categoryList", categoryDOS);
        if (act != null) {
            SiteDO siteDO = new SiteDO();
            siteDO.setSiteKey(siteKey);
            siteDO.setName(name);
            siteDO.setLogo(logo);
            siteDO.setSite(site);
            siteDO.setH5Url(h5Url);
            siteDO.setIosUrl(iosUrl);
            siteDO.setAndroidUrl(androidUrl);
            siteDO.setAttributes(attributes);
            siteDO.setMinVersion(minVersion);
            siteDO.setStatus(status);
            siteDO.setTags(t);
            siteDO.setPriority(priority);
            siteDO.setCategories(categories);
            try {
                if (id == null) {
                    SiteDO old = siteService.getBySiteKey(siteKey);
                    if (old != null) {
                        model.addAttribute("errorMsg", siteKey + "已存在");
                        return new ModelAndView(vm);
                    }
                    siteService.insert(siteDO);
                    return new ModelAndView("redirect:/site.htm");
                } else {
                    siteDO.setId(id);
                    model.addAttribute("id", id);
                    siteService.update(siteDO);
                    return new ModelAndView("redirect:/site.htm");
                }
            } catch (Throwable e) {
                logger.error("Error insert site:" + siteDO, e);
                model.addAttribute("errorMsg", e.getMessage());
            }
        }
        return new ModelAndView(vm);
    }

    public static class SiteVO extends SiteDO {
        private String categoryNames;

        public String getCategoryNames() {
            return categoryNames;
        }

        public void setCategoryNames(String categoryNames) {
            this.categoryNames = categoryNames;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
