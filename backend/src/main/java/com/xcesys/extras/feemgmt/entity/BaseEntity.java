package com.xcesys.extras.feemgmt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体基类，包含所有实体共有的字段和审计信息
 */
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = -158808243487997030L;

  /**
   * 主键ID
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 创建时间
   */
  @CreatedDate
  @Column(name = "create_time", nullable = false, updatable = false)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime createTime;

  /**
   * 更新时间
   */
  @LastModifiedDate
  @Column(name = "update_time")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime updateTime;

  /**
   * 创建人
   */
  @CreatedBy
  @Column(name = "create_by", updatable = false)
  private String createBy;

  /**
   * 更新人
   */
  @LastModifiedBy
  @Column(name = "update_by")
  private String updateBy;

  /**
   * 版本号，用于乐观锁控制
   */
  @Version
  @Column(name = "version")
  private Integer version = 0;

  /**
   * 备注
   */
  @Column(name = "remark", length = 500)
  private String remark;


}