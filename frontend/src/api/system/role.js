import request from '@/utils/request'

// Role API endpoints
const roleApi = {
  list: '/role',
  detail: '/role/',
  create: '/role',
  update: '/role',
  delete: '/role/',
  batchDelete: '/role/batch',
  updateStatus: '/role/status',
  assignPermissions: '/role/permissions'
}

// Get role list with pagination and filters
export function getRoleList(params) {
  return request({
    url: roleApi.list,
    method: 'get',
    params
  })
}

// Get role detail
export function getRoleDetail(id) {
  return request({
    url: roleApi.detail + id,
    method: 'get'
  })
}

// Create new role
export function createRole(data) {
  return request({
    url: roleApi.create,
    method: 'post',
    data
  })
}

// Update role
export function updateRole(data) {
  return request({
    url: roleApi.update + `/${data.id}`,
    method: 'put',
    data
  })
}

// Delete role
export function deleteRole(id) {
  return request({
    url: roleApi.delete + id,
    method: 'delete'
  })
}

// Batch delete roles
export function batchDeleteRoles(ids) {
  return request({
    url: roleApi.batchDelete,
    method: 'delete',
    data: { ids }
  })
}

// Update role status
export function updateRoleStatus(id, enabled) {
  return request({
    url: roleApi.updateStatus,
    method: 'put',
    params: { id, enabled}
  })
}

// Assign permissions to role
export function assignRolePermissions(data) {
  return request({
    url: roleApi.assignPermissions,
    method: 'post',
    data
  })
}