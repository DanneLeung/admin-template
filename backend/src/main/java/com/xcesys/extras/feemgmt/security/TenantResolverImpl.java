package com.xcesys.extras.feemgmt.security;

import com.xcesys.extras.feemgmt.entity.Company;
import com.xcesys.extras.feemgmt.repository.CompanyRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * 租户解析器实现类
 * 从请求域名和URL中的company code解析租户信息，或从已登录用户的公司信息中获取租户ID
 */
@Component
@RequiredArgsConstructor
public class TenantResolverImpl implements TenantResolver {

    private final CompanyRepository companyRepository;

    @Override
    public Long resolveTenantId(HttpServletRequest request) {
        // 首先检查当前用户是否已登录
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
          if (userDetails instanceof UserDetailsImpl) {
                // 如果用户已登录，使用用户所属公司ID作为租户ID
                return ((UserDetailsImpl) userDetails).getCompanyId();
            }
        }

        // 如果用户未登录，尝试从请求中解析租户信息
        // 从请求域名中解析租户信息
        String serverName = request.getServerName();
        if (StringUtils.hasText(serverName)) {
            Optional<Company> company = companyRepository.findByDomain(serverName);
            if (company.isPresent()) {
                return company.get().getId();
            }
        }

        // 从URL中的company code参数解析租户信息
        String companyCode = request.getParameter("company");
        if (StringUtils.hasText(companyCode)) {
            Optional<Company> company = companyRepository.findByCode(companyCode);
            if (company.isPresent()) {
                return company.get().getId();
            }
        }

        // 如果无法解析到租户信息，返回null
        return null;
    }
}