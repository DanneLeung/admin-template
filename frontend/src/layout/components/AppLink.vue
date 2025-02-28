<template>
  <component :is="linkType" v-bind="linkProps">
    <slot />
  </component>
</template>

<script setup>
import { computed } from 'vue'
import { isExternal } from '../../utils'

const props = defineProps({
  to: {
    type: String,
    required: true
  }
})

// 确定链接类型：外部链接用a标签，内部链接用router-link
const linkType = computed(() => {
  return isExternal(props.to) ? 'a' : 'router-link'
})

// 根据链接类型计算不同的属性
const linkProps = computed(() => {
  if (isExternal(props.to)) {
    return {
      href: props.to,
      target: '_blank',
      rel: 'noopener'
    }
  }
  return {
    to: props.to
  }
})
</script>