package com.xcesys.extras.feemgmt.exception;

import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 构造函数
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造函数
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    /**
     * 参数错误
     *
     * @param message 错误消息
     * @return 业务异常
     */
    public static BusinessException validationError(String message) {
        return new BusinessException(400, message);
    }

    /**
     * 未授权错误
     *
     * @param message 错误消息
     * @return 业务异常
     */
    public static BusinessException unauthorizedError(String message) {
        return new BusinessException(401, message);
    }

    /**
     * 权限不足错误
     *
     * @param message 错误消息
     * @return 业务异常
     */
    public static BusinessException forbiddenError(String message) {
        return new BusinessException(403, message);
    }

    /**
     * 资源不存在错误
     *
     * @param message 错误消息
     * @return 业务异常
     */
    public static BusinessException notFoundError(String message) {
        return new BusinessException(404, message);
    }

    /**
     * 资源冲突错误
     *
     * @param message 错误消息
     * @return 业务异常
     */
    public static BusinessException conflictError(String message) {
        return new BusinessException(409, message);
    }
}