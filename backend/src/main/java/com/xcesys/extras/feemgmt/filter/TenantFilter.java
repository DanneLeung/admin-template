package com.xcesys.extras.feemgmt.filter;

import com.xcesys.extras.feemgmt.security.TenantAware;
import com.xcesys.extras.feemgmt.security.TenantResolver;
import jakarta.persistence.EntityManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 租户过滤器
 * 用于在查询时自动添加租户条件，实现多租户数据隔离
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

  private final EntityManager entityManager;
  private final TenantAware tenantAware;
  private final TenantResolver tenantResolver;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // 从请求中解析租户ID
    Long tenantId = tenantResolver.resolveTenantId(request);

    // 设置当前租户ID
    tenantAware.setCurrentTenantId(tenantId);

    //        if (tenantId != null) {
    // 设置Hibernate Session的租户ID
    Session session = entityManager.unwrap(Session.class);
    session.enableFilter("tenantFilter")
        .setParameter("tenantId", tenantId);
    //        }

    filterChain.doFilter(request, response);
  }
}