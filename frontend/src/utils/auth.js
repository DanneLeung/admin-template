// src/utils/auth.js
import Cookies from 'js-cookie'

const TOKEN_KEY = 'auth_token'
const TOKEN_EXPIRES_DAYS = 7

/**
 * 获取Token
 * @returns {String} token值
 */
export function getToken() {
  return Cookies.get(TOKEN_KEY)
}

/**
 * 设置Token
 * @param {String} token - token值
 */
export function setToken(token) {
  return Cookies.set(TOKEN_KEY, token, { expires: TOKEN_EXPIRES_DAYS })
}

/**
 * 移除Token
 */
export function removeToken() {
  return Cookies.remove(TOKEN_KEY)
}

/**
 * 检查权限
 * @param {Array} permissions - 当前用户的权限列表
 * @param {String|Array} permission - 需要检查的权限，可以是单个权限字符串或权限数组
 * @returns {Boolean} 是否拥有权限
 */
export function checkPermission(permissions, permission) {
  if (!permissions || !permission) {
    return false
  }
  
  // 管理员权限
  if (permissions.includes('admin')) {
    return true
  }
  
  // 检查是否有指定的所有权限
  if (Array.isArray(permission)) {
    return permission.every(p => permissions.includes(p))
  }
  
  // 检查单个权限
  return permissions.includes(permission)
}

/**
 * 检查是否拥有角色
 * @param {Array} userRoles - 当前用户的角色列表
 * @param {String|Array} role - 需要检查的角色，可以是单个角色或角色数组
 * @returns {Boolean} 是否拥有角色
 */
export function checkRole(userRoles, role) {
  if (!userRoles || !role) {
    return false
  }

  // 超级管理员角色
  if (userRoles.includes('admin')) {
    return true
  }
  
  // 检查是否有指定的任意角色
  if (Array.isArray(role)) {
    return role.some(r => userRoles.includes(r))
  }
  
  // 检查单个角色
  return userRoles.includes(role)
}