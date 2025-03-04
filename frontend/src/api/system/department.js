import request from '@/utils/request'

// 查询部门列表
export function listDepartments(query) {
  return request({
    url: '/department/tree',
    method: 'get',
    params: query
  })
}

// 查询部门树结构
export function getDepartmentTree(query) {
  return request({
    url: '/department/tree',
    method: 'get',
    params: query
  })
}

// 查询部门详细
export function getDepartment(deptId) {
  return request({
    url: `/department/${deptId}`,
    method: 'get'
  })
}

// 新增部门
export function addDepartment(data) {
  return request({
    url: '/department',
    method: 'post',
    data: data
  })
}

// 修改部门
export function updateDepartment(deptId, data) {
  return request({
    url: `/department/${deptId}`,
    method: 'put',
    data: data
  })
}

// 删除部门
export function delDepartment(deptId) {
  return request({
    url: `/department/${deptId}`,
    method: 'delete'
  })
}

// 更新部门状态
export function updateDepartmentStatus(deptId, status) {
  return request({
    url: `/department/${deptId}/status`,
    method: 'put',
    params: { status }
  })
}

// 检查部门名称是否存在
export function checkDepartmentName(name, parentId) {
  return request({
    url: '/department/check-name',
    method: 'get',
    params: { name, parentId }
  })
}