package com.xcesys.template.admin.service;

import com.xcesys.template.admin.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * 公司服务接口
 */
public interface CompanyService {

    /**
     * 根据ID查询公司
     *
     * @param id 公司ID
     * @return 公司对象
     */
    Company findById(Long id);

    /**
     * 根据编码查询公司
     *
     * @param code 公司编码
     * @return 公司对象（可能为空）
     */
    Optional<Company> findByCode(String code);

    /**
     * 根据域名查询公司
     *
     * @param domain 域名
     * @return 公司对象（可能为空）
     */
    Optional<Company> findByDomain(String domain);

    /**
     * 分页查询公司列表
     *
     * @param name     公司名称（模糊查询）
     * @param enabled   状态
     * @param pageable 分页参数
     * @return 公司分页列表
     */
    Page<Company> findCompanies(String name, Boolean enabled, Pageable pageable);

    /**
     * 创建公司
     *
     * @param company 公司对象
     * @return 创建后的公司对象
     */
    Company createCompany(Company company);

    /**
     * 更新公司
     *
     * @param company 公司对象
     * @return 更新后的公司对象
     */
    Company updateCompany(Company company);

    /**
     * 删除公司
     *
     * @param id 公司ID
     */
    void deleteCompany(Long id);

    /**
     * 更新公司状态
     *
     * @param id     公司ID
     * @param enabled 状态
     */
    void updateEnabled(Long id, Boolean enabled);

    /**
     * 判断公司编码是否存在
     *
     * @param code 公司编码
     * @return 是否存在
     */
    boolean isCodeExists(String code);

    /**
     * 判断域名是否存在
     *
     * @param domain 域名
     * @return 是否存在
     */
    boolean isDomainExists(String domain);
}