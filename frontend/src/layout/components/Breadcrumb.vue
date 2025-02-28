<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="item.path">
        <span v-if="index === breadcrumbs.length - 1 || !item.path" class="no-link">
          {{ item.meta.title }}
        </span>
        <a v-else @click.prevent="handleLink(item)">{{ item.meta.title }}</a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const breadcrumbs = ref([])

// 生成面包屑导航数据
const getBreadcrumbs = () => {
  // 如果当前路由不需要显示面包屑，则返回空数组
  if (route.meta?.hideBreadcrumb) {
    breadcrumbs.value = []
    return
  }
  
  // 生成当前路由匹配记录的面包屑
  const matched = route.matched.filter(item => item.meta && item.meta.title && !item.meta.hidden)
  
  // 如果没有匹配的路由，返回空数组
  if (!matched.length) {
    breadcrumbs.value = []
    return
  }
  
  // 添加首页
  const home = {
    path: '/dashboard',
    meta: { title: '首页' }
  }
  
  // 构建面包屑数据
  breadcrumbs.value = [home].concat(matched)
}

// 处理链接点击
const handleLink = (item) => {
  router.push(item.path)
}

// 监听路由变化，更新面包屑
watch(() => route.path, getBreadcrumbs, { immediate: true })
</script>

<style lang="scss" scoped>
.app-breadcrumb {
  display: inline-block;
  margin-left: 8px;
  line-height: 50px;
  
  .el-breadcrumb__inner,
  .el-breadcrumb__inner a {
    font-weight: normal;
  }

  .no-link {
    color: #97a8be;
    cursor: text;
  }
  
  a {
    color: #666;
    cursor: pointer;
    
    &:hover {
      color: #409EFF;
    }
  }
}

/* 面包屑动画 */
.breadcrumb-enter-active,
.breadcrumb-leave-active {
  transition: all 0.5s;
}

.breadcrumb-enter-from,
.breadcrumb-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

.breadcrumb-leave-active {
  position: absolute;
}
</style>