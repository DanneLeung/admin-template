package com.xcesys.extras.feemgmt.security;

import org.springframework.stereotype.Component;

/**
 * 租户感知接口实现类
 * 使用ThreadLocal存储当前租户ID，确保线程安全
 */
@Component
public class TenantAwareImpl implements TenantAware {

    private static final ThreadLocal<Long> currentTenant = new ThreadLocal<>();

    @Override
    public Long getCurrentTenantId() {
        return currentTenant.get();
    }

    @Override
    public void setCurrentTenantId(Long tenantId) {
        currentTenant.set(tenantId);
    }

    /**
     * 清除当前租户ID
     * 建议在请求完成后调用，避免内存泄漏
     */
    public void clear() {
        currentTenant.remove();
    }
}