import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import './style.css'

// 创建Vue应用实例
const app = createApp(App)

// 注册所有Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用插件
app.use(createPinia()) // 状态管理
app.use(router) // 路由
app.use(ElementPlus, { size: 'default', zIndex: 3000 }) // UI组件库

// 错误处理
app.config.errorHandler = (err, instance, info) => {
  console.error('Vue Error:', err)
  console.error('Error Info:', info)
}

// 全局属性
app.config.globalProperties.$ELEMENT = { size: 'medium' }

// 挂载应用
app.mount('#app')

console.log('Admin Template启动成功')