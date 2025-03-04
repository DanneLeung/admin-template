package com.xcesys.template.admin.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用响应结果对象
 *
 * @param <T> 数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

  /**
   * 状态码
   */
  private Integer code;

  /**
   * 消息
   */
  private String message;

  /**
   * 数据
   */
  private T data;

  /**
   * 成功响应
   *
   * @param <T> 数据类型
   * @return 成功响应结果
   */
  public static <T> Result<T> success() {
    return success(null);
  }

  /**
   * 成功响应
   *
   * @param data 数据
   * @param <T>  数据类型
   * @return 成功响应结果
   */
  public static <T> Result<T> success(T data) {
    return Result.<T>builder()
        .code(200)
        .message("操作成功")
        .data(data)
        .build();
  }

  /**
   * 成功响应
   *
   * @param message 成功消息
   * @param data    数据
   * @param <T>     数据类型
   * @return 成功响应结果
   */
  public static <T> Result<T> success(String message, T data) {
    return Result.<T>builder()
        .code(200)
        .message(message)
        .data(data)
        .build();
  }

  /**
   * 错误响应
   *
   * @param code    错误码
   * @param message 错误消息
   * @param <T>     数据类型
   * @return 错误响应结果
   */
  public static <T> Result<T> error(Integer code, String message) {
    return Result.<T>builder()
        .code(code)
        .message(message)
        .build();
  }

  /**
   * 错误响应
   *
   * @param code    错误码
   * @param message 错误消息
   * @param data    错误数据
   * @param <T>     数据类型
   * @return 错误响应结果
   */
  public static <T> Result<T> error(Integer code, String message, T data) {
    return Result.<T>builder()
        .code(code)
        .message(message)
        .data(data)
        .build();
  }
}