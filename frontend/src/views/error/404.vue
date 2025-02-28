<template>
  <div class="error-container">
    <div class="error-content">
      <h1 class="error-code">404</h1>
      <div class="error-message">
        <h2>页面未找到</h2>
        <p>抱歉，您访问的页面不存在</p>
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.error-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgb(243 244 246);
  padding: 1rem;
}

.error-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  max-width: 56rem;
  width: 100%;
}

@media (min-width: 768px) {
  .error-content {
    flex-direction: row;
  }
}

.error-code {
  font-size: 6rem;
  line-height: 1;
  font-weight: 700;
  margin-right: 2rem;
  margin-bottom: 1rem;
  color: #409EFF;
}

@media (min-width: 768px) {
  .error-code {
    font-size: 8rem;
    margin-bottom: 0;
  }
}

.error-message {
  text-align: center;
}

@media (min-width: 768px) {
  .error-message {
    text-align: left;
  }
}

h2 {
  font-size: 1.5rem;
  line-height: 2rem;
  font-weight: 700;
  color: rgb(31 41 55);
  margin-bottom: 0.5rem;
}

@media (min-width: 768px) {
  h2 {
    font-size: 1.875rem;
    line-height: 2.25rem;
  }
}

p {
  color: rgb(75 85 99);
  margin-bottom: 1.5rem;
}
</style>