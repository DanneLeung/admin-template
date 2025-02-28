<template>
  <div class="navbar">
    <div class="left-menu">
      <div class="hamburger-container" @click="toggleSidebar">
        <el-icon :size="20">
          <Fold v-if="!isCollapse" />
          <Expand v-else />
        </el-icon>
      </div>
      <breadcrumb class="hidden sm:flex" />
    </div>

    <div class="right-menu">
      <el-tooltip content="全屏" placement="bottom">
        <div class="right-menu-item" @click="toggleFullscreen">
          <el-icon :size="18"><FullScreen /></el-icon>
        </div>
      </el-tooltip>

      <el-dropdown class="avatar-container" trigger="click">
        <div class="avatar-wrapper">
          <el-avatar :size="30" :src="userInfo.avatar || '/avatar-placeholder.jpg'" />
          <span class="ml-2 hidden sm:inline-block">{{ userInfo.name }}</span>
          <el-icon class="el-icon--right"><CaretBottom /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <router-link to="/profile">
              <el-dropdown-item>个人中心</el-dropdown-item>
            </router-link>
            <el-dropdown-item divided @click="handleLogout">
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { Fold, Expand, FullScreen, CaretBottom } from '@element-plus/icons-vue'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()

// 侧边栏折叠状态
const isCollapse = ref(false)

// 获取用户信息
const userInfo = computed(() => {
  return {
    name: userStore.username || '未登录',
    avatar: userStore.avatar
  }
})

// 切换侧边栏折叠状态
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 切换全屏
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
    }
  }
}

// 处理退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await userStore.logout()
    router.push(`/login?redirect=${encodeURIComponent(router.currentRoute.value.fullPath)}`)
  }).catch(() => {})
}
</script>

<style scoped>
.navbar {
  @apply h-full flex items-center justify-between px-4;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.left-menu {
  @apply flex items-center;
}

.hamburger-container {
  @apply flex items-center justify-center h-full w-10 cursor-pointer;
}

.right-menu {
  @apply flex items-center h-full;
}

.right-menu-item {
  @apply flex items-center justify-center h-full px-2 cursor-pointer;
}

.avatar-container {
  @apply ml-4 cursor-pointer;
}

.avatar-wrapper {
  @apply flex items-center;
}
</style>