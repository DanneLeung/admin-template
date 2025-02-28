import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { useUserStore } from '../store/user'
import { getToken } from '../utils/auth'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/auth-redirect']

export function setupRouterGuard(router) {
  router.beforeEach(async (to, from, next) => {
    
    // 获取用户Token
    const hasToken = getToken()
    const userStore = useUserStore()
    try {
      // 开始加载进度条
      NProgress.start()
      
      // 设置页面标题
      const title = to.meta.title ? `${to.meta.title} - 费用管理系统` : '费用管理系统'
      document.title = title
      
      // 处理已登录状态
      if (hasToken) {
        if (to.path === '/login') {
          next({ path: '/' })
          return
        }
        
        // 检查用户信息和路由权限
        if (!userStore.username) {
          await userStore.getUserInfo()
          const routes = await userStore.generateRoutes()
          routes.forEach(route => router.addRoute(route))
          next({ ...to, replace: true })
          return
        }
        
        // 处理动态路由刷新丢失
        if (to.matched.length === 0) {
          next('/404')
          return
        }
        
        next()
        return
      }
      
      // 处理未登录状态
      if (whiteList.includes(to.path)) {
        next()
        return
      }
      
      next(`/login?redirect=${encodeURIComponent(to.path)}`)
    } catch (error) {
      console.error('路由导航错误:', error)
      await userStore?.logout()
      next(`/login?redirect=${encodeURIComponent(to.path)}`)
    } finally {
      NProgress.done()
    }
  })
  
  // 路由加载后处理
  router.afterEach(() => {
    NProgress.done()
  })
  
  // 路由错误统一处理
  router.onError((error) => {
    console.error('路由加载错误:', error)
    NProgress.done()
  })
}