import request from '@/utils/request'

// 查询公司列表
export function listCompany(query) {
  return request({
    url: '/companies',
    method: 'get',
    params: query
  })
}

// 查询公司详细
export function getCompany(id) {
  return request({
    url: `/companies/${id}`,
    method: 'get'
  })
}

// 新增公司
export function addCompany(data) {
  return request({
    url: '/companies',
    method: 'post',
    data: data
  })
}

// 修改公司
export function updateCompany(id, data) {
  return request({
    url: `/companies/${id}`,
    method: 'put',
    data: data
  })
}

// 删除公司
export function delCompany(id) {
  return request({
    url: `/companies/${id}`,
    method: 'delete'
  })
}

// 修改公司状态
export function changeCompanyStatus(id, enabled) {
  return request({
    url: `/companies/status`,
    method: 'put',
    params: { id, enabled}
  })
}

// 检查公司编码是否存在
export function checkCompanyCode(code) {
  return request({
    url: '/companies/check-code',
    method: 'get',
    params: { code }
  })
}

// 检查域名是否存在
export function checkCompanyDomain(domain) {
  return request({
    url: '/companies/check-domain',
    method: 'get',
    params: { domain }
  })
}