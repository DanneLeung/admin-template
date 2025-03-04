package com.xcesys.template.admin.controller;

import com.xcesys.template.admin.common.Result;
import com.xcesys.template.admin.dto.CompanyDto;
import com.xcesys.template.admin.entity.Company;
import com.xcesys.template.admin.mapper.CompanyMapper;
import com.xcesys.template.admin.service.CompanyService;
import jakarta.validation.Valid;
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
  private final CompanyMapper companyMapper;

  /**
   * 获取公司详情
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('system:company:list')")
  public Result<CompanyDto> getCompany(@PathVariable Long id) {
    Company company = companyService.findById(id);
    return Result.success(companyMapper.toDto(company));
  }

  /**
   * 分页查询公司列表
   */
  @GetMapping
  @PreAuthorize("hasAuthority('system:company:list')")
  public Result<Page<CompanyDto>> listCompanies(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Boolean enabled,
      Pageable pageable) {
    Page<Company> page = companyService.findCompanies(name, enabled, pageable);
    return Result.success(page.map(companyMapper::toDto));
  }

  /**
   * 创建公司
   */
  @PostMapping
  @PreAuthorize("hasAuthority('system:company:add')")
  public Result<CompanyDto> createCompany(@Valid @RequestBody CompanyDto companyDto) {
    // 检查公司编码是否已存在
    if (companyService.isCodeExists(companyDto.getCode())) {
      return Result.error(400, "公司编码已存在");
    }

    // 检查域名是否已存在
    if (companyDto.getDomain() != null && companyService.isDomainExists(companyDto.getDomain())) {
      return Result.error(400, "域名已存在");
    }

    // 创建公司
    Company company = new Company();
    companyMapper.toEntity(company, companyDto);
    Company createdCompany = companyService.createCompany(company);

    return Result.success("创建成功", companyMapper.toDto(createdCompany));
  }

  /**
   * 更新公司
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('system:company:edit')")
  public Result<CompanyDto> updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyDto companyDto) {
    // 检查公司是否存在
    Company existingCompany = companyService.findById(id);

    // 检查公司编码是否已存在
    if (!existingCompany.getCode().equals(companyDto.getCode()) && companyService.isCodeExists(companyDto.getCode())) {
      return Result.error(400, "公司编码已存在");
    }

    // 检查域名是否已存在
    if (companyDto.getDomain() != null && 
        !companyDto.getDomain().equals(existingCompany.getDomain()) && 
        companyService.isDomainExists(companyDto.getDomain())) {
      return Result.error(400, "域名已存在");
    }

    // 更新公司
    companyMapper.toEntity(existingCompany, companyDto);
    Company updatedCompany = companyService.updateCompany(existingCompany);

    return Result.success("更新成功", companyMapper.toDto(updatedCompany));
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
  @PutMapping("/status")
  @PreAuthorize("hasAuthority('system:company:edit')")
  public Result<String> updateStatus(@RequestParam("id") Long id, @RequestParam("enabled") Boolean enabled) {
    companyService.updateEnabled(id, enabled);
    return Result.success("更新状态成功");
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