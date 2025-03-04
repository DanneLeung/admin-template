import request from '@/utils/request'

// Permission API endpoints
const permissionApi = {
  list: '/permission',
  detail: '/permission/',
  create: '/permission',
  update: '/permission',
  delete: '/permission/',
  batchDelete: '/permission/batch',
  updateStatus: '/permission/status'
}

// Get permission list with pagination and filters
export function getPermissionList(params) {
  return request({
    url: permissionApi.list,
    method: 'get',
    params
  })
}

// Get permission detail
export function getPermissionDetail(id) {
  return request({
    url: permissionApi.detail + id,
    method: 'get'
  })
}

// Create new permission
export function createPermission(data) {
  return request({
    url: permissionApi.create,
    method: 'post',
    data
  })
}

// Update permission
export function updatePermission(data) {
  return request({
    url: permissionApi.update,
    method: 'put',
    data
  })
}

// Delete permission
export function deletePermission(id) {
  return request({
    url: permissionApi.delete + id,
    method: 'delete'
  })
}

// Batch delete permissions
export function batchDeletePermissions(ids) {
  return request({
    url: permissionApi.batchDelete,
    method: 'delete',
    data: { ids }
  })
}

// Update permission status
export function updatePermissionStatus(id, enabled) {
  return request({
    url: permissionApi.updateStatus,
    method: 'put',
    params: { id, enabled}
  })
}