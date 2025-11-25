<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Clock, Bell, User, ArrowDown, Lock, SwitchButton,
  Odometer, Film, House, Calendar, Tickets, UserFilled,
  HomeFilled, Refresh, FullScreen, Expand, Fold,
  VideoPlay
} from '@element-plus/icons-vue'

const router = useRouter();
const route = useRoute();

// 响应式数据
const isCollapse = ref(false)
const activeMenu = ref('/admins/dashboard')
const currentTime = ref('')
const unreadCount = ref(3)
const adminName = ref('管理员')
const adminAvatar = ref('')

// 面包屑数据
const breadcrumbList = ref([])

// 路由映射表
const routeNameMap = {
  '/admins': '首页',
  '/admins/dashboard': '首页',
  '/admins/locations': '地点管理',
  '/admins/actors': '演员管理',
  '/admins/categories': '类型管理',
  '/admins/cinemas': '影院管理',
  '/admins/movies': '电影管理',
  '/admins/schedules': '排片管理',
  '/admins/orders/list': '订单列表',
  '/admins/orders/refunds': '退票管理',
  '/admins/orders/statistics': '销售统计',
  '/admins/users': '用户管理'
}

// 计算属性
const sidebarWidth = computed(() => isCollapse.value ? '64px' : '260px')

// 方法
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

const toggleFullScreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else if (document.exitFullscreen) {
    document.exitFullscreen()
  }
}

const logout = () => {
  // 退出登录逻辑
  console.log('退出登录')
  router.push('/login')
}

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { 
    hour12: false,
    hour: '2-digit',
    minute: '2-digit'
  })
}

const updateBreadcrumb = (path) => {
  const pathSegments = path.split('/').filter(Boolean)
  breadcrumbList.value = []
  
  if (path === '/admins') {
    return
  }
  
  let currentPath = ''
  pathSegments.forEach((segment, index) => {
    if (index === 0) {
      currentPath = '/admins'
    } else {
      currentPath += '/' + segment
    }
    
    if (routeNameMap[currentPath] && currentPath !== '/admins') {
      breadcrumbList.value.push({
        name: routeNameMap[currentPath],
        path: currentPath
      })
    }
  })
}

// 生命周期
onMounted(() => {
  updateTime()
  const timer = setInterval(updateTime, 1000)
  
  // 检查屏幕尺寸来设置侧边栏初始状态
  const handleResize = () => {
    if (window.innerWidth < 992) {
      isCollapse.value = true
    }
  }
  
  handleResize()
  window.addEventListener('resize', handleResize)
  
  onUnmounted(() => {
    clearInterval(timer)
    window.removeEventListener('resize', handleResize)
  })
})

// 路由监听
watch(() => route.path, (newPath) => {
  activeMenu.value = newPath
  updateBreadcrumb(newPath)
}, { immediate: true })
</script>

<template>
  <div class="admin-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-left">
        <div class="logo-container">
          <div class="film-reel-icon">
            <div class="reel-circle">
              <div class="reel-holes">
                <span v-for="i in 8" :key="i" class="hole"></span>
              </div>
            </div>
          </div>
          <div class="brand-text">
            <h1 class="system-title">翻斗花园</h1>
            <span class="subtitle">翻斗花园电影院</span>
          </div>
        </div>
      </div>
      
      <div class="header-right">
        <div class="time-display">
          <el-icon class="time-icon"><Clock /></el-icon>
          <span class="current-time">{{ currentTime }}</span>
        </div>
        
        <el-badge :value="unreadCount" class="notification-badge">
          <div class="notification-btn">
            <el-icon class="notification-icon"><Bell /></el-icon>
          </div>
        </el-badge>
        
        <el-dropdown trigger="click" class="user-dropdown">
          <div class="user-info">
            <div class="user-avatar">
              <el-avatar :src="adminAvatar" class="avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="status-indicator"></div>
            </div>
            <div class="user-details">
              <span class="username">{{ adminName }}</span>
              <!-- <span class="role">管理员</span> -->
            </div>
            <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu class="user-menu">
              <el-dropdown-item class="menu-item">
                <el-icon><User /></el-icon>
                个人设置
              </el-dropdown-item>
              <el-dropdown-item class="menu-item">
                <el-icon><Lock /></el-icon>
                修改密码
              </el-dropdown-item>
              <el-dropdown-item divided class="menu-item logout" @click="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container class="main-container">
      <!-- 左侧菜单栏 -->
      <el-aside :width="sidebarWidth" class="sidebar">
        <div class="sidebar-header">
          <div class="light-beam"></div>
          <el-button 
            class="collapse-btn" 
            :icon="isCollapse ? Expand : Fold" 
            @click="toggleSidebar"
            circle
          />
        </div>
        
        <el-scrollbar class="sidebar-scrollbar">
          <el-menu
            :default-active="activeMenu"
            :collapse="isCollapse"
            :router="true"
            class="sidebar-menu"
            :collapse-transition="false"
          >
            <!-- 首页 -->
            <el-menu-item index="/admins/dashboard" class="menu-item-custom">
              <div class="menu-icon-wrapper">
                <el-icon class="menu-icon"><Odometer /></el-icon>
                <div class="icon-glow"></div>
              </div>
              <span class="menu-text">首页</span>
              <div class="menu-highlight"></div>
            </el-menu-item>

            <!-- 内容管理 -->
            <el-sub-menu index="content" class="submenu-custom">
              <template #title>
                <div class="menu-icon-wrapper">
                  <el-icon class="menu-icon"><Film /></el-icon>
                  <div class="icon-glow"></div>
                </div>
                <span class="menu-text">内容管理</span>
                <div class="menu-highlight"></div>
              </template>
              <el-menu-item index="/admins/locations" class="submenu-item">
                <div class="submenu-dot"></div>
                <span>地点管理</span>
              </el-menu-item>
              <el-menu-item index="/admins/actors" class="submenu-item">
                <div class="submenu-dot"></div>
                <span>演员管理</span>
              </el-menu-item>
              <el-menu-item index="/admins/categories" class="submenu-item">
                <div class="submenu-dot"></div>
                <span>类型管理</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 影院管理 -->
            <el-menu-item index="/admins/cinemas" class="menu-item-custom">
              <div class="menu-icon-wrapper">
                <el-icon class="menu-icon"><House /></el-icon>
                <div class="icon-glow"></div>
              </div>
              <span class="menu-text">影院管理</span>
              <div class="menu-highlight"></div>
            </el-menu-item>

            <!-- 电影管理 -->
            <el-menu-item index="/admins/movies" class="menu-item-custom">
              <div class="menu-icon-wrapper">
                <el-icon class="menu-icon"><VideoPlay /></el-icon>
                <div class="icon-glow"></div>
              </div>
              <span class="menu-text">电影管理</span>
              <div class="menu-highlight"></div>
            </el-menu-item>

            <!-- 排片管理 -->
            <el-menu-item index="/admins/schedules" class="menu-item-custom">
              <div class="menu-icon-wrapper">
                <el-icon class="menu-icon"><Calendar /></el-icon>
                <div class="icon-glow"></div>
              </div>
              <span class="menu-text">排片管理</span>
              <div class="menu-highlight"></div>
            </el-menu-item>

            <!-- 订单管理 -->
            <el-sub-menu index="orders" class="submenu-custom">
              <template #title>
                <div class="menu-icon-wrapper">
                  <el-icon class="menu-icon"><Tickets /></el-icon>
                  <div class="icon-glow"></div>
                </div>
                <span class="menu-text">订单管理</span>
                <div class="menu-highlight"></div>
              </template>
              <el-menu-item index="/admins/orders/list" class="submenu-item">
                <div class="submenu-dot"></div>
                <span>订单列表</span>
              </el-menu-item>
              <el-menu-item index="/admins/orders/refunds" class="submenu-item">
                <div class="submenu-dot"></div>
                <span>退票管理</span>
              </el-menu-item>
              <el-menu-item index="/admins/orders/statistics" class="submenu-item">
                <div class="submenu-dot"></div>
                <span>销售统计</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 用户管理 -->
            <el-menu-item index="/admins/users" class="menu-item-custom">
              <div class="menu-icon-wrapper">
                <el-icon class="menu-icon"><UserFilled /></el-icon>
                <div class="icon-glow"></div>
              </div>
              <span class="menu-text">用户管理</span>
              <div class="menu-highlight"></div>
            </el-menu-item>
          </el-menu>
        </el-scrollbar>
        
        <!-- 侧边栏底部装饰 -->
        <div class="sidebar-footer">
          <div class="film-strip">
            <div class="strip-hole" v-for="i in 12" :key="i"></div>
          </div>
        </div>
      </el-aside>

      <!-- 主内容区域 -->
      <el-main class="main-content">
        <!-- 面包屑导航 -->
        <div class="breadcrumb-container">
          <div class="spotlight-effect"></div>
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/admins' }">
              <el-icon><HomeFilled /></el-icon>
              首页
            </el-breadcrumb-item>
            <el-breadcrumb-item v-for="item in breadcrumbList" :key="item.path">
              {{ item.name }}
            </el-breadcrumb-item>
          </el-breadcrumb>
          
          <div class="page-actions">
            <el-button class="action-btn" circle>
              <el-icon><Refresh /></el-icon>
            </el-button>
            <el-button class="action-btn" circle @click="toggleFullScreen">
              <el-icon><FullScreen /></el-icon>
            </el-button>
          </div>
        </div>

        <!-- 页面内容 -->
        <div class="page-content">
          <div class="content-wrapper">
            <router-view />
          </div>
          
          <!-- 背景装饰元素 -->
          <div class="background-elements">
            <div class="floating-particles">
              <div class="particle" v-for="i in 15" :key="i"></div>
            </div>
            <div class="grid-overlay"></div>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>



<style scoped>
/* 主布局 */
.admin-layout {
  height: 100vh;
  background: linear-gradient(135deg, #f0f8ff 0%, #e6f3ff 50%, #ddeeff 100%);
  overflow: hidden;
}

/* 顶部导航栏样式 */
.header {
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.95) 100%);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(51, 122, 183, 0.2);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 16px rgba(51, 122, 183, 0.15);
  position: relative;
  overflow: hidden;
  height: 60px;
}

.header::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #1a3c8f, transparent);
  animation: lightSweep 3s infinite;
}

@keyframes lightSweep {
  0% { left: -100%; }
  100% { left: 100%; }
}

.header-left {
  display: flex;
  align-items: center;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 16px;
}

.film-reel-icon {
  width: 48px;
  height: 48px;
  position: relative;
  animation: rotate 8s linear infinite;
}

.reel-circle {
  width: 100%;
  height: 100%;
  border: 3px solid #1a3c8f;
  border-radius: 50%;
  position: relative;
  background: radial-gradient(circle, rgba(51, 122, 183, 0.2) 0%, transparent 70%);
}

.reel-holes {
  position: absolute;
  width: 100%;
  height: 100%;
}

.hole {
  position: absolute;
  width: 6px;
  height: 6px;
  background: #1a3c8f;
  border-radius: 50%;
  box-shadow: 0 0 8px #1a3c8f;
}

.hole:nth-child(1) { top: 2px; left: 50%; transform: translateX(-50%); }
.hole:nth-child(2) { top: 8px; right: 8px; }
.hole:nth-child(3) { top: 50%; right: 2px; transform: translateY(-50%); }
.hole:nth-child(4) { bottom: 8px; right: 8px; }
.hole:nth-child(5) { bottom: 2px; left: 50%; transform: translateX(-50%); }
.hole:nth-child(6) { bottom: 8px; left: 8px; }
.hole:nth-child(7) { top: 50%; left: 2px; transform: translateY(-50%); }
.hole:nth-child(8) { top: 8px; left: 8px; }

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.system-title {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(45deg, #337ab7, #5cb3cc, #337ab7);
  -webkit-background-clip: text;
  background-clip: text;
  color: #1a3c8f;
  margin: 0;
  text-shadow: 0 0 20px rgba(51, 122, 183, 0.3);
}

.subtitle {
  font-size: 12px;
  color: #1a3c8f;
  margin-top: -4px;
  letter-spacing: 2px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.time-display {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #495057;
  font-family: 'Courier New', monospace;
  font-size: 14px;
}

.time-icon {
  color: #337ab7;
}

.notification-badge {
  cursor: pointer;
}

.notification-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(51, 122, 183, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.notification-btn:hover {
  background: rgba(51, 122, 183, 0.2);
  transform: scale(1.1);
}

.notification-icon {
  color: #337ab7;
  font-size: 18px;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 24px;
  background: rgba(51, 122, 183, 0.1);
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(51, 122, 183, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(51, 122, 183, 0.2);
}

.user-avatar {
  position: relative;
}

.avatar {
  width: 36px;
  height: 36px;
  border: 2px solid #337ab7;
  background: #f8f9fa;
}

.status-indicator {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 10px;
  height: 10px;
  background: #28a745;
  border: 2px solid #fff;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.8; }
}

.user-details {
  display: flex;
  flex-direction: column;
}

.username {
  color: #212529;
  font-weight: 600;
  font-size: 14px;
}

.role {
  color: #6c757d;
  font-size: 12px;
}

.dropdown-arrow {
  color: #6c757d;
  transition: transform 0.3s ease;
}

.user-dropdown:hover .dropdown-arrow {
  transform: rotate(180deg);
}

/* 用户菜单样式 */
.user-menu {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(51, 122, 183, 0.2);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(51, 122, 183, 0.2);
}

.menu-item {
  color: #495057;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  transition: all 0.3s ease;
}

.menu-item:hover {
  background: rgba(51, 122, 183, 0.1);
  color: #337ab7;
}

.menu-item.logout:hover {
  background: rgba(220, 53, 69, 0.1);
  color: #dc3545;
}

/* 主容器样式 */
.main-container {
  height: calc(100vh - 60px);
  position: relative;
}

/* 侧边栏样式 */
.sidebar {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.95) 100%);
  backdrop-filter: blur(20px);
  border-right: 1px solid rgba(51, 122, 183, 0.2);
  transition: width 0.3s ease;
  position: relative;
  overflow: visible;
  box-shadow: 2px 0 8px #4facfe(51, 122, 183, 0.1);
}

.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 1px;
  height: 100%;
  background: linear-gradient(180deg, transparent, #337ab7, transparent);
  animation: verticalFlow 4s infinite alternate;
}

@keyframes verticalFlow {
  0% { opacity: 0.3; }
  100% { opacity: 1; }
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  border-bottom: 1px solid rgba(51, 122, 183, 0.1);
}

.light-beam {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, #337ab7, transparent);
  animation: beamMove 2s infinite;
}

@keyframes beamMove {
  0%, 100% { transform: translateX(-100%); }
  50% { transform: translateX(100%); }
}

.collapse-btn {
  background: rgba(51, 122, 183, 0.1);
  border: 1px solid rgba(51, 122, 183, 0.3);
  color: #337ab7;
  transition: all 0.3s ease;
  z-index: 10;
}

.collapse-btn:hover {
  background: rgba(51, 122, 183, 0.2);
  transform: scale(1.1);
  box-shadow: 0 0 15px rgba(51, 122, 183, 0.2);
}

.sidebar-scrollbar {
  height: calc(100% - 120px);
}

.sidebar-menu {
  border: none;
  background: transparent;
  padding: 12px 0;
}

.menu-item-custom {
  margin: 4px 12px;
  border-radius: 12px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.menu-item-custom:hover, .submenu-custom:hover {
  background: rgba(51, 122, 183, 0.1);
  transform: translateX(8px);
}

.menu-item-custom.is-active, .submenu-custom.is-active {
  background: linear-gradient(135deg, rgba(51, 122, 183, 0.15), rgba(51, 122, 183, 0.08));
  box-shadow: 0 4px 15px rgba(51, 122, 183, 0.15);
}

.submenu-custom {
  margin: 4px 12px;
  border-radius: 12px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.menu-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
}

.menu-icon {
  color: #495057;
  font-size: 18px;
  transition: all 0.3s ease;
  z-index: 2;
}

.icon-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 32px;
  height: 32px;
  background: radial-gradient(circle, rgba(51, 122, 183, 0.2), transparent);
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.menu-item-custom:hover .icon-glow,
.submenu-custom:hover .icon-glow {
  opacity: 1;
}

.menu-item-custom:hover .menu-icon,
.submenu-custom:hover .menu-icon {
  color: #337ab7;
  transform: scale(1.2);
}

.menu-text {
  color: #495057;
  font-weight: 500;
  margin-left: 12px;
  transition: color 0.3s ease;
}

.menu-item-custom:hover .menu-text,
.submenu-custom:hover .menu-text {
  color: #212529;
}

.menu-highlight {
  position: absolute;
  top: 0;
  left: 0;
  width: 3px;
  height: 100%;
  background: linear-gradient(180deg, #337ab7, #5cb3cc);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.menu-item-custom.is-active .menu-highlight,
.submenu-custom.is-active .menu-highlight {
  opacity: 1;
}

.submenu-item {
  margin: 2px 0;
  border-radius: 8px;
  position: relative;
  display: flex;
  align-items: center;
  padding-left: 12px;
}

.submenu-dot {
  width: 6px;
  height: 6px;
  background: rgba(108, 117, 125, 0.5);
  border-radius: 50%;
  margin-right: 12px;
  transition: all 0.3s ease;
}

.submenu-item:hover .submenu-dot {
  background: #337ab7;
  box-shadow: 0 0 8px rgba(51, 122, 183, 0.5);
  transform: scale(1.5);
}

.submenu-item:hover {
  background: rgba(51, 122, 183, 0.1);
  color: #337ab7;
}

.sidebar-footer {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid rgba(51, 122, 183, 0.1);
  position: absolute;
  bottom: 0;
  width: 100%;
}

.film-strip {
  display: flex;
  gap: 4px;
  padding: 0 20px;
}

.strip-hole {
  width: 8px;
  height: 12px;
  background: rgba(51, 122, 183, 0.3);
  border-radius: 2px;
  animation: stripFlicker 3s infinite;
}

.strip-hole:nth-child(even) {
  animation-delay: 0.5s;
}

@keyframes stripFlicker {
  0%, 100% { opacity: 0.4; }
  50% { opacity: 1; }
}

/* 主内容区域样式 */
.main-content {
  background: linear-gradient(135deg, rgba(240, 248, 255, 0.8) 0%, rgba(230, 243, 255, 0.8) 100%);
  position: relative;
  overflow: hidden;
  padding: 0;
}

.breadcrumb-container {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(51, 122, 183, 0.1);
  padding: 16px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
}

.spotlight-effect {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(51, 122, 183, 0.05), transparent);
  animation: spotlight 6s infinite;
}

@keyframes spotlight {
  0%, 100% { transform: translateX(-100%); }
  50% { transform: translateX(100%); }
}

.breadcrumb {
  display: flex;
  align-items: center;
  color: #495057;
}

.breadcrumb :deep(.el-breadcrumb__item) {
  color: #495057;
}

.breadcrumb :deep(.el-breadcrumb__item:last-child) {
  color: #337ab7;
}

.page-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: rgba(51, 122, 183, 0.1);
  border: 1px solid rgba(51, 122, 183, 0.2);
  color: #337ab7;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: rgba(51, 122, 183, 0.2);
  border-color: #337ab7;
  color: #337ab7;
  transform: scale(1.1);
}

.page-content {
  height: calc(100% - 64px);
  position: relative;
  overflow: auto;
}

.content-wrapper {
  padding: 24px;
  position: relative;
  z-index: 10;
  min-height: 100%;
}

.background-elements {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 1;
}

.floating-particles {
  position: absolute;
  width: 100%;
  height: 100%;
}

.particle {
  position: absolute;
  width: 3px;
  height: 3px;
  background: #337ab7;
  border-radius: 50%;
  opacity: 0.4;
  animation: float 20s infinite linear;
}

.particle:nth-child(odd) {
  animation-duration: 25s;
}

.particle:nth-child(3n) {
  background: #5cb3cc;
  animation-duration: 15s;
}

@keyframes float {
  0% {
    transform: translateY(100vh) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 0.4;
  }
  90% {
    opacity: 0.4;
  }
  100% {
    transform: translateY(-100px) rotate(360deg);
    opacity: 0;
  }
}

.grid-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(51, 122, 183, 0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(51, 122, 183, 0.02) 1px, transparent 1px);
  background-size: 60px 60px;
  animation: gridMove 30s infinite linear;
}

@keyframes gridMove {
  0% { transform: translate(0, 0); }
  100% { transform: translate(60px, 60px); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header {
    padding: 0 16px;
  }
  
  .header-right {
    gap: 16px;
  }
  
  .user-details {
    display: none;
  }
  
  .time-display {
    display: none;
  }
  
  .content-wrapper {
    padding: 16px;
  }
}

/* 滚动条样式 */
:deep(.el-scrollbar__bar) {
  background: rgba(51, 122, 183, 0.2);
}

:deep(.el-scrollbar__thumb) {
  background: rgba(51, 122, 183, 0.4);
  border-radius: 4px;
}

:deep(.el-scrollbar__thumb:hover) {
  background: rgba(51, 122, 183, 0.6);
}

/* Element Plus 组件样式重写 */
:deep(.el-menu) {
  background: transparent !important;
  border-right: none !important;
}

:deep(.el-menu--collapse) {
  width: 64px;
}

:deep(.el-menu-item) {
  color: #495057 !important;
  height: 50px !important;
  line-height: 50px !important;
}

:deep(.el-menu-item:hover) {
  background: rgba(51, 122, 183, 0.1) !important;
  color: #337ab7 !important;
}

:deep(.el-menu-item.is-active) {
  background: rgba(51, 122, 183, 0.15) !important;
  color: #337ab7 !important;
}

:deep(.el-sub-menu__title) {
  color: #495057 !important;
  height: 50px !important;
  line-height: 50px !important;
}

:deep(.el-sub-menu__title:hover) {
  background: rgba(51, 122, 183, 0.1) !important;
  color: #337ab7 !important;
}

:deep(.el-sub-menu.is-active .el-sub-menu__title) {
  color: #337ab7 !important;
}

:deep(.el-sub-menu .el-menu-item) {
  margin-left: 0 !important;
  min-width: auto !important;
  padding-left: 55px !important;
  height: 40px !important;
  line-height: 40px !important;
}

:deep(.el-sub-menu .el-menu) {
  background-color: rgba(51, 122, 183, 0.05) !important;
}

:deep(.el-menu--collapse .el-sub-menu__title) {
  padding-left: 20px !important;
}
</style>