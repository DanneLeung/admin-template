<template>
  <div class="app-wrapper">
    <div class="sidebar-container">
      <Sidebar />
    </div>
    <div class="main-container">
      <div class="navbar-container">
        <Navbar />
      </div>
      <div class="app-main">
        <AppMain />
      </div>
    </div>
  </div>
</template>

<script setup>
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'
import AppMain from './components/AppMain.vue'
</script>

<style scoped>
.app-wrapper {
  @apply flex h-screen overflow-hidden;
}

.sidebar-container {
  @apply w-64 bg-gray-800 text-white transition-all duration-300 ease-in-out;
  min-height: 100vh;
  overflow-y: auto;
  overflow-x: hidden;
}

.main-container {
  @apply flex flex-col flex-1 overflow-hidden;
}

.navbar-container {
  @apply h-14 bg-white border-b border-gray-200 shadow-sm;
}

.app-main {
  @apply flex-1 overflow-auto p-6;
  background-color: #f5f7fa;
}
</style>