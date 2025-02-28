import Layout from '../layout/index.vue'
// 动态路由配置
export const asyncRoutes = [
  {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'setting' },
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('../views/system/user/index.vue'),
        meta: { title: '用户管理', permissions: ['system:user:list'] }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('../views/system/role/index.vue'),
        meta: { title: '角色管理', permissions: ['system:role:list'] }
      },
      {
        path: 'menu',
        name: 'Menu',
        component: () => import('../views/system/menu/index.vue'),
        meta: { title: '菜单管理', permissions: ['system:menu:list'] }
      },
      {
        path: 'permission',
        name: 'Permission',
        component: () => import('../views/system/permission/index.vue'),
        meta: { title: '权限管理', permissions: ['system:permission:list'] }
      }
    ]
  }
]

/**
 * 判断用户是否有权限访问路由
 * @param {Array} roles 用户角色列表
 * @param {Array} permissions 用户权限列表
 * @param {Object} route 路由对象
 * @returns {boolean}
 */
function hasPermission(roles, permissions, route) {
  if (route.meta && route.meta.roles) {
    return roles.some(role => route.meta.roles.includes(role))
  }
  if (route.meta && route.meta.permissions) {
    return permissions.some(permission => route.meta.permissions.includes(permission))
  }
  return true
}

/**
 * 过滤异步路由表
 * @param {Array} routes 异步路由表
 * @param {Array} roles 用户角色列表
 * @param {Array} permissions 用户权限列表
 * @returns {Array}
 */
function filterAsyncRoutes(routes, roles, permissions) {
  const res = []

  routes.forEach(route => {
    const tmp = { ...route }
    if (hasPermission(roles, permissions, tmp)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, roles, permissions)
      }
      res.push(tmp)
    }
  })

  return res
}

/**
 * 生成动态路由
 * @param {Array} roles 用户角色列表
 * @param {Array} permissions 用户权限列表
 * @returns {Promise<Array>}
 */
export async function generateAsyncRoutes(roles, permissions) {
  try {
    let accessedRoutes = filterAsyncRoutes(asyncRoutes, roles, permissions)
    
    // 添加404路由到最后
    accessedRoutes = accessedRoutes.concat({
      path: '/:pathMatch(.*)*',
      redirect: '/404',
      meta: { hidden: true }
    })

    return accessedRoutes
  } catch (error) {
    console.error('Generate async routes error:', error)
    return []
  }
}