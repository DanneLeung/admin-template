package com.xcesys.template.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;

/**
 * 租户实体基类，包含租户隔离所需的字段
 */
@Getter
@Setter
@MappedSuperclass
@Filters(value = {@Filter(name = "tenantFilter")})
public abstract class TenantBaseEntity extends BaseEntity {

  /**
   * 公司ID（租户ID）
   */
  @Column(name = "company_id", nullable = true)
  private Long companyId;

  /**
   * 用户所属公司
   */
  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", insertable = false, updatable = false)
  private Company company;
}