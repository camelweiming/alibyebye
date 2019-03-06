package com.abb.bye.web;

import com.abb.bye.client.domain.SiteConfigsDO;
import com.abb.bye.client.domain.SiteDO;
import com.abb.bye.client.service.SiteConfigsService;
import com.abb.bye.client.service.SiteService;
import com.abb.bye.utils.Tracer;
import com.alibaba.boot.velocity.annotation.VelocityLayout;
import org.apache.commons.lang3.StringUtils;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cenpeng.lwm
 * @since 2018/10/2
 */
@Controller
public class SiteConfigsController {
    private static final Logger logger = LoggerFactory.getLogger(SiteConfigsController.class);
    @Resource
    private SiteConfigsService siteConfigsService;
    @Resource
    private SiteService siteService;

    @RequestMapping(value = "site_configs.htm", method = {RequestMethod.POST, RequestMethod.GET})
    @VelocityLayout("/velocity/layout/index.vm")
    String category(Model model,
                    @RequestParam int site,
                    @RequestParam(required = false) String env,
                    @RequestParam(required = false) Integer status
    ) {
        try {
            String vm = "site_configs";
            SiteDO appDO = siteService.getBySiteFromDB(site);
            if (StringUtils.isBlank(env) || StringUtils.equals("all", env)) {
                env = null;
            }
            if (status == null) {
                status = 1;
            }
            int _status = status;
            String _env = env;
            List<SiteConfigsDO> list = siteConfigsService.list(site);
            list = list.stream()
                .filter(siteConfigsDO -> _status == siteConfigsDO.getStatus())
                .filter(siteConfigsDO -> _env == null || _env.equals(siteConfigsDO.getEnv()))
                .collect(Collectors.toList());
            model.addAttribute("list", list);
            model.addAttribute("site", site);
            model.addAttribute("app", appDO);
            model.addAttribute("env", env);
            model.addAttribute("status", status);
            model.addAttribute("allPlatforms", new String[] {"android", "ios"});
            return vm;
        } catch (Throwable e) {
            logger.error("", e);
            throw e;
        }
    }

    @RequestMapping(value = "edit_site_configs.htm", method = {RequestMethod.POST, RequestMethod.GET})
    @VelocityLayout("/velocity/layout/index.vm")
    String edit(Model model, HttpServletRequest request, @RequestParam(required = false) Long id) {
        String vm = "add_site_configs";
        SiteConfigsDO siteConfig = siteConfigsService.get(id);
        model.addAttribute("id", siteConfig.getId());
        model.addAttribute("name", siteConfig.getName());
        model.addAttribute("site", siteConfig.getSite());
        model.addAttribute("configKey", siteConfig.getConfigKey());
        model.addAttribute("domains", siteConfig.getDomains());
        model.addAttribute("content", siteConfig.getContent());
        model.addAttribute("env", siteConfig.getEnv());
        model.addAttribute("status", siteConfig.getStatus());
        String[] platforms = StringUtils.split(siteConfig.getPlatform(), ",");
        model.addAttribute("platforms", platforms);
        model.addAttribute("allPlatforms", new String[] {"android", "ios"});
        SiteDO appDO = siteService.getBySiteFromDB(siteConfig.getSite());
        model.addAttribute("app", appDO);
        return vm;
    }

    @RequestMapping(value = "add_site_configs.htm", method = {RequestMethod.POST, RequestMethod.GET})
    @VelocityLayout("/velocity/layout/index.vm")
    ModelAndView add(Model model, HttpServletRequest request,
                     @RequestParam(required = false) Long id,
                     @RequestParam(required = false) Integer act,
                     @RequestParam(required = false) String configKey,
                     @RequestParam(required = false) String name,
                     @RequestParam int site,
                     @RequestParam(required = false) String content,
                     @RequestParam(required = false) String env,
                     @RequestParam(required = false) String domains,
                     @RequestParam(required = false) Integer status,
                     @RequestParam(required = false) String platform
    ) {
        model.addAttribute("site", site);
        SiteDO appDO = siteService.getBySiteFromDB(site);
        model.addAttribute("app", appDO);
        model.addAttribute("allPlatforms", new String[] {"android", "ios"});
        try {
            if (act != null) {
                SiteConfigsDO siteConfigsDO = new SiteConfigsDO();
                siteConfigsDO.setConfigKey(configKey);
                siteConfigsDO.setName(StringUtils.isBlank(name) ? configKey : name);
                siteConfigsDO.setSite(site);
                siteConfigsDO.setContent(StringUtils.replace(content, "\r", ""));
                siteConfigsDO.setEnv(env);
                siteConfigsDO.setDomains(domains);
                siteConfigsDO.setStatus(status);
                siteConfigsDO.setPlatform(platform);
                try {
                    if (id == null) {
                        siteConfigsService.insert(siteConfigsDO);
                    } else {
                        siteConfigsDO.setId(id);
                        SiteConfigsDO old = siteConfigsService.get(id);
                        new Tracer("OLD_SITE_CONFIG_UPDATE").setEntityId(site).trace("id:" + id + "#" + old.getContent());
                        siteConfigsService.update(siteConfigsDO);
                    }
                } catch (Throwable e) {
                    logger.error("Error insert siteConfig:" + siteConfigsDO, e);
                    model.addAttribute("errorMsg", e.getMessage());
                    return new ModelAndView("add_site_configs");
                }
                return new ModelAndView("redirect:/site_configs.htm").addObject("site", site);
            }
            return new ModelAndView("add_site_configs");
        } catch (Throwable e) {
            logger.error("", e);
            throw e;
        }
    }
}
