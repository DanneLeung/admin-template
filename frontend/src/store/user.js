// src/store/user.js
import { defineStore } from 'pinia'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { login, logout, getUserInfo } from '@/api/user'
import { generateAsyncRoutes } from '../router'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    id: '',
    username: '',
    nickname: '',
    avatar: '',
    roles: [],
    permissions: [],
    routes: []
  }),

  getters: {
    hasRole: (state) => (role) => {
      return state.roles.includes(role)
    },
    hasPermission: (state) => (permission) => {
      return state.permissions.includes(permission) || state.permissions.includes('admin')
    }
  },

  actions: {
    // 登录
    async login(userInfo) {
      const { username, password } = userInfo
      try {
        const response = await login({ username: username.trim(), password })
        const { token } = response.data
        setToken(token)
        this.token = token
        return Promise.resolve()
      } catch (error) {
        return Promise.reject(error)
      }
    },

    // 获取用户信息
    async getUserInfo() {
      try {
        const response = await getUserInfo()
        const user  = response.data
        
        if (!user) {
          return Promise.reject('验证失败，请重新登录')
        }

        // 处理用户信息
        const { id, username, nickname, avatar, roles, permissions } = user
        
        // 验证返回的roles是否是一个非空数组
        if (!roles || roles.length === 0) {
          return Promise.reject('用户角色必须是一个非空数组!')
        }

        this.id = id
        this.username = username
        this.nickname = nickname
        this.avatar = avatar || ''
        this.roles = roles
        this.permissions = permissions || []

        return Promise.resolve(user)
      } catch (error) {
        return Promise.reject(error)
      }
    },

    // 生成路由
    async generateRoutes() {
      try {
        // 调用路由过滤函数，生成可访问的路由表
        const accessRoutes = await generateAsyncRoutes(this.roles, this.permissions)
        
        if (!accessRoutes || accessRoutes.length === 0) {
          return Promise.reject('No accessible routes generated')
        }

        this.routes = accessRoutes
        return Promise.resolve(accessRoutes)
      } catch (error) {
        console.error('Failed to generate routes:', error)
        return Promise.reject(error)
      }
    },

    // 登出
    async logout() {
      try {
        await logout()
      } catch (error) {
        console.error('注销时发生错误', error)
      } finally {
        // 无论如何清除本地状态
        this.resetState()
        removeToken()
        ElMessage.success('已安全退出系统')
        return Promise.resolve()
      }
    },

    // 重置状态
    resetState() {
      this.token = ''
      this.id = ''
      this.name = ''
      this.avatar = ''
      this.roles = []
      this.permissions = []
      this.routes = []
    }
  }
})