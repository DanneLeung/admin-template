package com.xcesys.template.admin.security;

/**
 * 租户感知接口
 * 用于获取当前租户ID
 */
public interface TenantAware {
    /**
     * 获取当前租户ID
     *
     * @return 当前租户ID
     */
    Long getCurrentTenantId();

    /**
     * 设置当前租户ID
     *
     * @param tenantId 租户ID
     */
    void setCurrentTenantId(Long tenantId);
}