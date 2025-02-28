<template>
  <div v-if="!item.hidden">
    <!-- 如果只有一个子项并且没有子菜单，则直接渲染 -->
    <template v-if="hasOneShowingChild(item.children, item) && (!onlyOneChild.children || onlyOneChild.noShowingChildren) && !item.alwaysShow">
      <app-link v-if="onlyOneChild.meta" :to="resolvePath(onlyOneChild.path)">
        <el-menu-item :index="resolvePath(onlyOneChild.path)" :class="{'submenu-title-noDropdown': !isNest}">
          <el-icon v-if="onlyOneChild.meta.icon"><component :is="onlyOneChild.meta.icon" /></el-icon>
          <template #title>{{ onlyOneChild.meta.title }}</template>
        </el-menu-item>
      </app-link>
    </template>
    
    <!-- 如果有子菜单则渲染子菜单 -->
    <el-sub-menu v-else :index="resolvePath(item.path)" popper-append-to-body>
      <template #title>
        <el-icon v-if="item.meta && item.meta.icon"><component :is="item.meta.icon" /></el-icon>
        <span>{{ item.meta.title }}</span>
      </template>
      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :item="child"
        :is-nest="true"
        :base-path="resolvePath(child.path)"
        class="nest-menu"
      />
    </el-sub-menu>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { isExternal } from '../../utils'
import AppLink from './AppLink.vue'

const props = defineProps({
  // 当前路由项
  item: {
    type: Object,
    required: true
  },
  // 是否是嵌套的菜单
  isNest: {
    type: Boolean,
    default: false
  },
  // 基本路径
  basePath: {
    type: String,
    default: ''
  }
})

// 唯一子项（当只有一个子项时使用）
const onlyOneChild = ref(null)

/**
 * 判断是否只有一个显示的子菜单
 * @param {Array} children 子菜单列表
 * @param {Object} parent 父菜单
 * @returns {boolean} 是否只有一个显示的子菜单
 */
const hasOneShowingChild = (children = [], parent) => {
  const showingChildren = children.filter(item => {
    if (item.hidden) {
      return false
    } else {
      // 临时设置
      onlyOneChild.value = item
      return true
    }
  })

  // 当只有一个子路由时，默认显示该子路由
  if (showingChildren.length === 1) {
    return true
  }

  // 没有子路由则显示父路由
  if (showingChildren.length === 0) {
    onlyOneChild.value = { ...parent, path: '', noShowingChildren: true }
    return true
  }

  return false
}

/**
 * 解析路径，构建完整的路径
 * @param {string} routePath 路由路径
 * @returns {string} 完整路径
 */
const resolvePath = (routePath) => {
  if (isExternal(routePath)) {
    return routePath
  }
  if (isExternal(props.basePath)) {
    return props.basePath
  }
  return props.basePath + '/' + routePath
}
</script>

<style scoped>
.el-menu-item.is-active {
  background-color: #263445 !important;
}

.submenu-title-noDropdown.is-active {
  background-color: #263445 !important;
}
</style>