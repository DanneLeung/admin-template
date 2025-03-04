package com.xcesys.template.admin.security;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 租户解析器接口
 * 用于从HTTP请求中解析租户信息
 */
public interface TenantResolver {
    /**
     * 从请求中解析租户ID
     *
     * @param request HTTP请求
     * @return 租户ID，如果无法解析则返回null
     */
    Long resolveTenantId(HttpServletRequest request);
}