package com.xcesys.extras.feemgmt.controller;

import com.xcesys.extras.feemgmt.common.Result;
import com.xcesys.extras.feemgmt.entity.Company;
import com.xcesys.extras.feemgmt.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 公司管理控制器
 */
@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

  private final CompanyService companyService;

  /**
   * 获取公司详情
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('system:company:list')")
  public Result<Company> getCompany(@PathVariable Long id) {
    return Result.success(companyService.findById(id));
  }

  /**
   * 分页查询公司列表
   */
  @GetMapping
  @PreAuthorize("hasAuthority('system:company:list')")
  public Result<Page<Company>> listCompanies(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Integer status,
      Pageable pageable) {
    Page<Company> page = companyService.findCompanies(name, status, pageable);
    return Result.success(page);
  }

  /**
   * 创建公司
   */
  @PostMapping
  @PreAuthorize("hasAuthority('system:company:add')")
  public Result<Company> createCompany(@RequestBody Company company) {
    return Result.success(companyService.createCompany(company));
  }

  /**
   * 更新公司
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('system:company:edit')")
  public Result<Company> updateCompany(@PathVariable Long id, @RequestBody Company company) {
    company.setId(id);
    return Result.success(companyService.updateCompany(company));
  }

  /**
   * 删除公司
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('system:company:delete')")
  public Result<Void> deleteCompany(@PathVariable Long id) {
    companyService.deleteCompany(id);
    return Result.success();
  }

  /**
   * 更新公司状态
   */
  @PutMapping("/{id}/status")
  @PreAuthorize("hasAuthority('system:company:edit')")
  public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
    companyService.updateStatus(id, status);
    return Result.success();
  }

  /**
   * 检查公司编码是否存在
   */
  @GetMapping("/check-code")
  @PreAuthorize("hasAuthority('system:company:list')")
  public Result<Boolean> checkCode(@RequestParam String code) {
    return Result.success(companyService.isCodeExists(code));
  }

  /**
   * 检查域名是否存在
   */
  @GetMapping("/check-domain")
  @PreAuthorize("hasAuthority('system:company:list')")
  public Result<Boolean> checkDomain(@RequestParam String domain) {
    return Result.success(companyService.isDomainExists(domain));
  }
}