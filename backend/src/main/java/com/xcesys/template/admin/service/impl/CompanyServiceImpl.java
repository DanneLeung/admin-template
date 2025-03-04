package com.xcesys.template.admin.service.impl;

import com.xcesys.template.admin.entity.Company;
import com.xcesys.template.admin.exception.BusinessException;
import com.xcesys.template.admin.repository.CompanyRepository;
import com.xcesys.template.admin.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * 公司服务实现类
 */
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFoundError("公司不存在: ID=" + id));
    }

    @Override
    public Optional<Company> findByCode(String code) {
        return companyRepository.findByCode(code);
    }

    @Override
    public Optional<Company> findByDomain(String domain) {
        return companyRepository.findByDomain(domain);
    }

    @Override
    public Page<Company> findCompanies(String name, Boolean enabled, Pageable pageable) {
        if (StringUtils.hasText(name)) {
            return companyRepository.findByNameContainingAndEnabled(name, enabled, pageable);
        } else if (enabled != null) {
            return companyRepository.findByEnabled(enabled, pageable);
        } else {
            return companyRepository.findAll(pageable);
        }
    }

    @Override
    @Transactional
    public Company createCompany(Company company) {
        // 检查公司编码是否已存在
        if (isCodeExists(company.getCode())) {
            throw BusinessException.conflictError("公司编码已存在: " + company.getCode());
        }

        // 检查域名是否已存在
        if (StringUtils.hasText(company.getDomain()) && isDomainExists(company.getDomain())) {
            throw BusinessException.conflictError("域名已存在: " + company.getDomain());
        }

        // 设置默认状态为启用
        if (company.getEnabled() == null) {
            company.setEnabled(true);
        }

        return companyRepository.save(company);
    }

    @Override
    @Transactional
    public Company updateCompany(Company company) {
        Company existingCompany = findById(company.getId());

        // 检查公司编码是否已存在
        if (!existingCompany.getCode().equals(company.getCode()) && isCodeExists(company.getCode())) {
            throw BusinessException.conflictError("公司编码已存在: " + company.getCode());
        }

        // 检查域名是否已存在
        if (StringUtils.hasText(company.getDomain()) &&
            !company.getDomain().equals(existingCompany.getDomain()) &&
            isDomainExists(company.getDomain())) {
            throw BusinessException.conflictError("域名已存在: " + company.getDomain());
        }

        return companyRepository.save(company);
    }

    @Override
    @Transactional
    public void deleteCompany(Long id) {
        Company company = findById(id);
        companyRepository.delete(company);
    }

    @Override
    @Transactional
    public void updateEnabled(Long id, Boolean enabled) {
        Company company = findById(id);
        company.setEnabled(enabled);
        companyRepository.save(company);
    }

    @Override
    public boolean isCodeExists(String code) {
        return companyRepository.existsByCode(code);
    }

    @Override
    public boolean isDomainExists(String domain) {
        return companyRepository.existsByDomain(domain);
    }
}