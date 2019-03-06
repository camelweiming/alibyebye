package com.abb.bye.client.domain;

import com.abb.bye.client.domain.enums.SiteTag;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cenpeng.lwm
 * @since 2018/10/2
 */
public class SiteDO implements Serializable {
    private static final long serialVersionUID = 3317973590283613206L;
    public static final byte STATUS_UNABLE = 0;
    public static final byte STATUS_ENABLE = 1;

    public SiteDO() {

    }

    public SiteDO(String name) {
        this.name = name;
    }

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 名称
     */
    private String name;

    /**
     * 图片
     */
    private String logo;

    /**
     * 站点ID
     */
    private Integer site;

    /**
     * h5地址
     */
    private String h5Url;

    /**
     * IOS跳转地址
     */
    private String iosUrl;

    /**
     * 安卓跳转地址
     */
    private String androidUrl;

    /**
     * 附加属性
     */
    private String attributes;

    /**
     * 最低支持版本号
     */
    private String minVersion;

    /**
     * 1:可用,2:无效
     */
    private Byte status;
    /**
     * 唯一值
     */
    private String siteKey;
    /**
     * 标@See SiteTag
     */
    private long tags;

    private Integer priority;

    private String categories;
    private long[] categoryList;
    private SiteConfig siteConfig;

    public SiteConfig getSiteConfig() {
        return siteConfig;
    }

    public void setSiteConfig(SiteConfig siteConfig) {
        this.siteConfig = siteConfig;
    }

    public long[] getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(long[] categoryList) {
        this.categoryList = categoryList;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public long getTags() {
        return tags;
    }

    public void setTags(long tags) {
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Integer getSite() {
        return site;
    }

    public void setSite(Integer site) {
        this.site = site;
    }

    public String getH5Url() {
        return h5Url;
    }

    public void setH5Url(String h5Url) {
        this.h5Url = h5Url;
    }

    public String getIosUrl() {
        return iosUrl;
    }

    public void setIosUrl(String iosUrl) {
        this.iosUrl = iosUrl;
    }

    public String getAndroidUrl() {
        return androidUrl;
    }

    public void setAndroidUrl(String androidUrl) {
        this.androidUrl = androidUrl;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getMinVersion() {
        return minVersion;
    }

    public void setMinVersion(String minVersion) {
        this.minVersion = minVersion;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getSiteKey() {
        return siteKey;
    }

    public void setSiteKey(String siteKey) {
        this.siteKey = siteKey;
    }

    public boolean bitSet(SiteTag tag) {
        return (tags & tag.getValue()) == tag.getValue();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
