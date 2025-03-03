import request from '@/utils/request'

// Menu API endpoints
const menuApi = {
  list: '/menu/tree',
  detail: '/menu/',
  create: '/menu',
  update: '/menu',
  delete: '/menu/',
  updateStatus: '/menu/status'
}

// Get menu list
export function getMenuList(params) {
  return request({
    url: menuApi.list,
    method: 'get',
    params
  })
}

// Get menu detail
export function getMenuDetail(id) {
  return request({
    url: menuApi.detail + id,
    method: 'get'
  })
}

// Create new menu
export function createMenu(data) {
  return request({
    url: menuApi.create,
    method: 'post',
    data
  })
}

// Update menu
export function updateMenu(data) {
  return request({
    url: menuApi.update,
    method: 'put',
    data
  })
}

// Delete menu
export function deleteMenu(id) {
  return request({
    url: menuApi.delete + id,
    method: 'delete'
  })
}

// Update menu status
export function updateMenuStatus(id, status) {
  return request({
    url: menuApi.updateStatus,
    method: 'patch',
    data: { id, status }
  })
}