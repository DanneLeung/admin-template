// src/user.js
import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - 登录信息
 * @param {String} data.username - 用户名
 * @param {String} data.password - 密码
 * @returns {Promise} Promise对象
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户注销
 * @returns {Promise} Promise对象
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 获取用户信息
 * @returns {Promise} Promise对象
 */
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}


/**
 * 重置密码
 * @param {Number|String} id - 用户ID
 * @returns {Promise} Promise对象
 */
export function resetUserPassword(id) {
  return request({
    url: `/user/${id}/password/reset`,
    method: 'put'
  })
}

/**
 * 修改密码
 * @param {Object} data - 密码信息
 * @param {String} data.oldPassword - 原密码
 * @param {String} data.newPassword - 新密码
 * @returns {Promise} Promise对象
 */
export function updatePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}

/**
 * 为用户分配角色
 * @param {Number|String} userId - 用户ID
 * @param {Array} roleIds - 角色ID数组
 * @returns {Promise} Promise对象
 */
export function assignRoles(userId, roleIds) {
  return request({
    url: `/user/${userId}/roles`,
    method: 'post',
    data: { roleIds }
  })
}