import request from '@/utils/request'

// User API endpoints
const userApi = {
  list: '/user',
  detail: '/user/',
  create: '/user',
  update: '/user',
  delete: '/user/',
  batchDelete: '/user/batch',
  updateStatus: '/user/status',
  assignRoles: '/user/roles'
}

// Get user list with pagination and filters
export function getUserList(params) {
  return request({
    url: userApi.list,
    method: 'get',
    params
  })
}

// Get user detail
export function getUserDetail(id) {
  return request({
    url: userApi.detail + id,
    method: 'get'
  })
}

// Create new user
export function createUser(data) {
  return request({
    url: userApi.create,
    method: 'post',
    data
  })
}

// Update user
export function updateUser(data) {
  return request({
    url: userApi.update + `/${data.id}`,
    method: 'put',
    data
  })
}

// Delete user
export function deleteUser(id) {
  return request({
    url: userApi.delete + id,
    method: 'delete'
  })
}

// Batch delete users
export function batchDeleteUsers(ids) {
  return request({
    url: userApi.batchDelete,
    method: 'delete',
    data: { ids }
  })
}

// Update user status
export function updateUserStatus(id, status) {
  return request({
    url: userApi.updateStatus,
    method: 'patch',
    data: { id, status }
  })
}

// Assign roles to user
export function assignUserRoles(data) {
  return request({
    url: userApi.assignRoles,
    method: 'post',
    data
  })
}