<template>
  <div class="sidebar">
    <div class="logo-container">
      <router-link to="/">
        <h1 class="logo-text">Admin Template</h1>
      </router-link>
    </div>

    <el-scrollbar>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        :unique-opened="true"
        :collapse-transition="false"
        mode="vertical"
      >
        <sidebar-item 
          v-for="route in routes" 
          :key="route.path" 
          :item="route" 
          :base-path="route.path" 
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import SidebarItem from './SidebarItem.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 菜单是否折叠
const isCollapse = ref(false)

// 当前激活的菜单
const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta.activeMenu) {
    return meta.activeMenu
  }
  return path
})

// 可访问的路由
const routes = computed(() => {
  const allRoutes = router.getRoutes()
  const filteredRoutes = allRoutes.filter(route => {
    return !route.meta?.hidden && route.children?.length > 0
  })
  return filteredRoutes
})

onMounted(() => {
  // 获取用户路由权限
  if (userStore.routes.length === 0) {
    userStore.generateRoutes()
  }
})
</script>

<style scoped>
.sidebar {
  @apply h-full flex flex-col;
}

.logo-container {
  @apply h-14 flex items-center justify-center bg-gray-900;
}

.logo-text {
  @apply text-lg font-bold text-white;
}

.el-menu {
  @apply border-none;
}

:deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}
</style>