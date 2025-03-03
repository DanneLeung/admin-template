package com.xcesys.extras.feemgmt.repository;

import com.xcesys.extras.feemgmt.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

  boolean existsByCode(String code);

  boolean existsByDomain(String domain);

  /**
   * 根据公司编码查询公司
   *
   * @param code 公司编码
   * @return 公司信息
   */
  Optional<Company> findByCode(String code);

  /**
   * 根据域名查询公司
   *
   * @param domain 域名
   * @return 公司信息
   */
  Optional<Company> findByDomain(String domain);

  Page<Company> findByNameContainingAndStatus(String name, Integer status, Pageable pageable);

  Page<Company> findByStatus(Integer status, Pageable pageable);
}