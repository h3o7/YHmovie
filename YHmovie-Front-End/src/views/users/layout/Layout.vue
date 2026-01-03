<template>
  <div class="layout">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-container">
        <!-- 左侧Logo和主导航 -->
        <div class="header-left">
          <!-- Logo区域 -->
          <div class="logo" @click="navigateTo('/users/dashboard')">
            <el-icon class="logo-icon" :size="28">
              <VideoPlay />
            </el-icon>
            <span class="logo-text">YH电影</span>
          </div>

          <!-- 位置选择器 -->
          <div class="location-selector">
            <el-popover placement="bottom-start" :width="550" trigger="click" popper-class="city-popover"
              v-model:visible="cityPopoverVisible">
              <template #reference>
                <div class="current-city" @click="showCityPopover">
                  <span>{{ currentCity }}</span>
                  <el-icon class="location-icon">
                    <LocationFilled />
                  </el-icon>
                </div>
              </template>

              <template #default>
                <div class="city-selector-container">
                  <!-- 搜索框 -->
                  <div class="city-search">
                    <el-input v-model="citySearchQuery" placeholder="输入城市名称搜索" clearable @input="handleCitySearch">
                      <template #prefix>
                        <el-icon>
                          <Search />
                        </el-icon>
                      </template>
                    </el-input>
                  </div>

                  <!-- 搜索结果 -->
                  <div v-if="showSearchResults && searchResults.length > 0" class="search-results">
                    <div class="results-title">搜索结果</div>
                    <div class="results-list">
                      <div v-for="city in searchResults" :key="city.cityId" class="city-item" @click="selectCity(city)">
                        {{ city.cityName }}
                      </div>
                    </div>
                  </div>
                  <div v-else-if="showSearchResults && searchResults.length === 0" class="no-results">
                    没有找到匹配的城市
                  </div>

                  <!-- 省份和城市列表 -->
                  <div v-if="!showSearchResults" class="province-city-container">
                    <!-- 省份列表 -->
                    <div class="province-list">
                      <div class="list-title">选择省份</div>
                      <div class="province-scroll">
                        <div v-for="province in provinces" :key="province.provinceId" class="province-item"
                          :class="{ active: selectedProvinceId === province.provinceId }"
                          @click="selectProvince(province)">
                          {{ province.provinceName }}
                        </div>
                      </div>
                    </div>

                    <!-- 城市列表 -->
                    <div class="city-list">
                      <div class="list-title">选择城市</div>
                      <div v-if="loading" class="loading-cities">
                        <el-icon class="is-loading">
                          <Loading />
                        </el-icon>
                        <span>加载中...</span>
                      </div>
                      <div v-else class="city-scroll">
                        <div v-for="city in cities" :key="city.cityId" class="city-item" @click="selectCity(city)">
                          {{ city.cityName }}
                        </div>
                        <div v-if="cities.length === 0" class="no-city">
                          请先选择一个省份
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 热门城市 -->
                  <div v-if="!showSearchResults" class="hot-cities">
                    <div class="list-title">热门城市</div>
                    <div class="hot-cities-list">
                      <div v-for="city in hotCities" :key="city.cityId" class="hot-city-item" @click="selectCity(city)">
                        {{ city.cityName }}
                      </div>
                    </div>
                  </div>
                </div>
              </template>
            </el-popover>
          </div>

          <!-- 主导航 -->
          <nav class="main-nav">
            <div v-for="item in navItems" :key="item.path" :class="['nav-item', { active: $route.path === item.path }]"
              @click="navigateTo(item.path)">
              {{ item.name }}
            </div>
          </nav>
        </div>

        <!-- 右侧用户中心 -->
        <div class="header-right">
          <div class="search-box">
            <el-input v-model="searchQuery" placeholder="搜索电影、影院" clearable @keyup.enter="handleSearch">
              <template #suffix>
                <el-icon class="search-icon" @click="handleSearch">
                  <Search />
                </el-icon>
              </template>
            </el-input>
          </div>

          <el-dropdown trigger="hover" @command="handleUserCommand">
            <div class="user-center">
              <el-avatar :src="userInfo.userAvatarUrl" :size="40" class="user-avatar">
                {{ userInfo.userName ? userInfo.userName.charAt(0) : 'U' }}
              </el-avatar>
              <span class="user-name">{{ userInfo.userName || '用户' }}</span>
              <el-icon class="dropdown-icon" :size="12">
                <ArrowDown />
              </el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="orders">
                  <el-icon>
                    <Tickets />
                  </el-icon>
                  订单信息
                </el-dropdown-item>
                <el-dropdown-item command="profile">
                  <el-icon>
                    <User />
                  </el-icon>
                  个人信息
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon>
                    <SwitchButton />
                  </el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主内容区域 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-links">
          <a href="#" class="footer-link">关于我们</a>
          <span class="divider">|</span>
          <a href="#" class="footer-link">帮助中心</a>
          <span class="divider">|</span>
          <a href="#" class="footer-link">联系我们</a>
          <span class="divider">|</span>
          <a href="#" class="footer-link">用户协议</a>
          <span class="divider">|</span>
          <a href="#" class="footer-link">隐私政策</a>
        </div>
        <div class="copyright">
          &copy; {{ new Date().getFullYear() }} YH电影网 版权所有
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import {
  VideoPlay,
  ArrowDown,
  User,
  Tickets,
  SwitchButton,
  LocationFilled,
  Search,
  Loading
} from '@element-plus/icons-vue'
import { getUser } from '@/api/users'
import { generateShowtimes } from '@/api/showtimes'
import { provincesList, citiesList, searchCities } from '@/api/login'

const router = useRouter()

// 导航菜单项
const navItems = [
  { name: '首页', path: '/users/dashboard' },
  { name: '电影', path: '/users/movies' },
  { name: '影院', path: '/users/cinemas' },
  { name: '排行', path: '/users/ranking' }
]

// 用户信息
const userInfo = ref({
  userId: '',
  userName: '',
  userAvatarUrl: ''
})

// 搜索框
const searchQuery = ref('')

// 城市选择相关
const cityPopoverVisible = ref(false)
const provinces = ref([])
const cities = ref([])
const selectedProvinceId = ref('')
const currentCity = ref('重庆市')
const currentCityId = ref('500100') // 默认重庆
const loading = ref(false)
const citySearchQuery = ref('')
const searchResults = ref([])
const showSearchResults = ref(false)

// 热门城市列表
const hotCities = ref([
  { cityId: '110100', cityName: '北京市' },
  { cityId: '310100', cityName: '上海市' },
  { cityId: '440100', cityName: '广州市' },
  { cityId: '440300', cityName: '深圳市' },
  { cityId: '330100', cityName: '杭州市' },
  { cityId: '320100', cityName: '南京市' },
  { cityId: '500100', cityName: '重庆市' },
  { cityId: '420100', cityName: '武汉市' },
  { cityId: '510100', cityName: '成都市' },
  { cityId: '610100', cityName: '西安市' }
])

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const loginData = localStorage.getItem('login')
    if (!loginData) {
      ElMessage.error('用户未登录，请先登录')
      router.push('/login')
      return
    }

    const login = JSON.parse(loginData)
    const userId = login.id
    if (!userId) {
      ElMessage.error('获取用户ID失败')
      return
    }

    const response = await getUser(userId)
    if (response.code === 200 && response.data) {
      userInfo.value = {
        userId: response.data.userId,
        userName: response.data.userName,
        userAvatarUrl: response.data.userAvatarUrl || ''
      }
    } else {
      console.error('获取用户信息失败:', response)
    }
  } catch (error) {
    console.error('获取用户信息异常:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 加载省份列表
const loadProvinces = async () => {
  try {
    const response = await provincesList()
    if (response.code === 200 && response.data) {
      provinces.value = response.data
    } else {
      console.error('获取省份列表失败:', response)
    }
  } catch (error) {
    console.error('加载省份异常:', error)
    ElMessage.error('获取省份列表失败')
  }
}

// 加载城市列表
const loadCities = async (provinceId) => {
  if (!provinceId) return

  try {
    loading.value = true
    const response = await citiesList(provinceId)
    if (response.code === 200 && response.data) {
      cities.value = response.data
    } else {
      console.error('获取城市列表失败:', response)
      cities.value = []
    }
  } catch (error) {
    console.error('加载城市异常:', error)
    ElMessage.error('获取城市列表失败')
    cities.value = []
  } finally {
    loading.value = false
  }
}

// 搜索城市
const handleCitySearch = async () => {
  if (citySearchQuery.value.trim() === '') {
    showSearchResults.value = false
    return
  }

  try {
    showSearchResults.value = true
    loading.value = true
    const response = await searchCities(citySearchQuery.value.trim())
    if (response.code === 200 && response.data) {
      searchResults.value = response.data
    } else {
      searchResults.value = []
    }
  } catch (error) {
    console.error('搜索城市异常:', error)
    searchResults.value = []
  } finally {
    loading.value = false
  }
}

// 选择省份
const selectProvince = (province) => {
  selectedProvinceId.value = province.provinceId
  loadCities(province.provinceId)
}

// 选择城市
const selectCity = async (city) => {
  currentCity.value = city.cityName
  currentCityId.value = city.cityId
  cityPopoverVisible.value = false

  // 保存当前城市到本地存储
  localStorage.setItem('currentCity', JSON.stringify({
    cityId: city.cityId,
    cityName: city.cityName
  }))

  // 询问是否生成场次数据
  try {
    await ElMessageBox.confirm(
      '是否产生场次数据?',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info',
      }
    )

    // 用户点击确定，调用生成接口
    try {
      // 1. 开启 Loading 服务 (防止重复点击)
      const loadingInstance = ElLoading.service({
        lock: true,
        text: '正在提交生成任务...', // 文案微调：是“提交任务”不是“生成数据”
        background: 'rgba(0, 0, 0, 0.7)',
      })

      // 2. 请求接口 (现在这个请求会很快返回，通常<1秒)
      const res = await generateShowtimes(city.cityId)

      // 3. 关闭 Loading
      loadingInstance.close()

      if (res.code === 200) {
        // === 核心修改部分 ===

        // 使用 ElMessageBox 显示后端返回的 res.msg
        // 并补充说明后台正在运行
        loadingInstance.close()

        ElMessageBox.alert(
          `
    <div style="text-align: center;">
      <p style="font-size: 16px; font-weight: bold; color: #303133; margin-bottom: 10px;">
        ${res.data}
      </p>
      <div style="text-align: left; background: #f4f4f5; padding: 10px; border-radius: 4px; color: #606266; font-size: 13px;">
        <p style="margin: 0;">⏳ 预计耗时：<b>一分钟左右</b></p>
        <p style="margin: 5px 0 0 0;">💡 温馨提示：数据生成在后台运行，您无需停留在当前页面。</p>
      </div>
    </div>
    `,
          '提交成功',
          {
            confirmButtonText: '好的，我去看看别的',
            dangerouslyUseHTMLString: true,
            customClass: 'my-message-box', // 如果需要进一步写CSS类
            type: 'success',
            center: true, // Element Plus 自带的居中属性
            callback: () => {
              router.go(0)
            }
          }
        )
      } else {
        // 异常情况 (如无影院、限流等)
        ElMessageBox.alert(
          res.msg || '任务提交失败',
          '提示',
          {
            confirmButtonText: '确定',
            type: 'warning',
            callback: () => {
              router.go(0)
            }
          }
        )
      }
    } catch (error) {
      console.error('请求异常:', error)
      ElMessage.error('网络请求失败，请检查连接')
      // 出错时最好也把 loading 关掉，防止页面卡死
      const loadingInstance = ElLoading.service()
      loadingInstance.close()
      router.go(0)
    }
  } catch (e) {
    // 用户取消或关闭弹窗，不做生成操作，但仍需刷新页面以更新城市视图
    console.log('用户取消生成场次数据')
    router.go(0)
  }
}

// 显示城市选择器
const showCityPopover = () => {
  // 重置搜索状态
  citySearchQuery.value = ''
  showSearchResults.value = false

  // 如果还没有加载过省份数据，加载一次
  if (provinces.value.length === 0) {
    loadProvinces()
  }
}

// 导航处理
const navigateTo = (path) => {
  router.push(path)
}

// 搜索处理
const handleSearch = () => {
  if (!searchQuery.value.trim()) return

  router.push({
    path: '/users/searchs',
    query: { search: searchQuery.value.trim() }
  })

  searchQuery.value = ''
}

// 用户操作处理
const handleUserCommand = (command) => {
  switch (command) {
    case 'orders':
      router.push('/users/orders')
      break
    case 'profile':
      router.push('/users/user')
      break
    case 'logout':
      localStorage.removeItem('login')
      localStorage.removeItem('currentCity')
      localStorage.removeItem('selectedCity')
      ElMessage.success('退出成功')
      router.push('/login')
      break
  }
}

// 初始化
const initialize = () => {
  // 从本地存储恢复城市选择
  const savedCity = localStorage.getItem('currentCity')
  if (savedCity) {
    try {
      const cityData = JSON.parse(savedCity)
      currentCity.value = cityData.cityName
      currentCityId.value = cityData.cityId
    } catch (error) {
      console.error('解析保存的城市信息失败:', error)
      // 解析失败恢复默认
      setDefaultCity()
    }
  } else {
    // 没有保存的城市，设置默认
    setDefaultCity()
  }

  // 加载用户信息
  fetchUserInfo()
}

// 设置默认城市
const setDefaultCity = () => {
  currentCity.value = '重庆市'
  currentCityId.value = '500100'
  localStorage.setItem('currentCity', JSON.stringify({
    cityId: '500100',
    cityName: '重庆市'
  }))
}

// 监听城市搜索输入框变化
watch(citySearchQuery, (val) => {
  if (val.trim() === '') {
    showSearchResults.value = false
    searchResults.value = []
  }
})

// 组件挂载
onMounted(() => {
  initialize()
})
</script>

<style scoped>
/* 全局布局 */
.layout {
  min-height: 100vh;
  background-color: #f7f7f7;
  display: flex;
  flex-direction: column;
}

/* 头部导航 */
.header {
  background: #ffffff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
  padding: 0 24px;
}

/* 左侧区域 */
.header-left {
  display: flex;
  align-items: center;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  user-select: none;
  margin-right: 24px;
}

.logo-icon {
  color: #e54847;
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  color: #e54847;
  letter-spacing: 0.5px;
}

/* 位置选择器 */
.location-selector {
  margin-right: 32px;
}

.current-city {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.3s;
}

.current-city:hover {
  background-color: #f0f0f0;
}

.location-icon {
  color: #e54847;
  font-size: 16px;
}

/* 城市选择器弹出框 */
.city-selector-container {
  padding: 16px;
}

.city-search {
  margin-bottom: 16px;
}

/* 省份和城市列表容器 */
.province-city-container {
  display: grid;
  grid-template-columns: 1fr 3fr;
  gap: 16px;
  margin-bottom: 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
}

/* 省份列表 */
.province-list {
  height: 300px;
  border-right: 1px solid #ebeef5;
  background-color: #f8f8f8;
}

.list-title {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  padding: 8px 16px;
  border-bottom: 1px solid #ebeef5;
}

.province-scroll {
  height: calc(100% - 37px);
  overflow-y: auto;
}

.province-item {
  padding: 8px 16px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.province-item:hover {
  background-color: #ecf5ff;
  color: #409eff;
}

.province-item.active {
  background-color: #ecf5ff;
  color: #409eff;
  font-weight: 500;
}

/* 城市列表 */
.city-list {
  height: 300px;
  background-color: #ffffff;
}

.city-scroll {
  height: calc(100% - 37px);
  overflow-y: auto;
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  padding: 8px;
}

.city-item {
  padding: 6px 12px;
  margin: 4px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  border-radius: 4px;
  display: inline-block;
}

.city-item:hover {
  background-color: #ecf5ff;
  color: #409eff;
}

/* 热门城市 */
.hot-cities {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
}

.hot-cities-list {
  display: flex;
  flex-wrap: wrap;
  padding: 8px;
  background-color: #ffffff;
}

.hot-city-item {
  padding: 6px 12px;
  margin: 4px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  border-radius: 4px;
}

.hot-city-item:hover {
  background-color: #ecf5ff;
  color: #409eff;
}

/* 搜索结果 */
.search-results {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 16px;
}

.results-title {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  padding: 8px 16px;
  border-bottom: 1px solid #ebeef5;
}

.results-list {
  display: flex;
  flex-wrap: wrap;
  padding: 8px;
  background-color: #ffffff;
  max-height: 200px;
  overflow-y: auto;
}

.no-results,
.no-city,
.loading-cities {
  padding: 20px;
  text-align: center;
  color: #909399;
  width: 100%;
}

.loading-cities {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

/* 主导航 */
.main-nav {
  display: flex;
  align-items: center;
  height: 100%;
}

.nav-item {
  font-size: 16px;
  font-weight: 500;
  color: #333333;
  padding: 0 16px;
  height: 64px;
  line-height: 64px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.nav-item:hover {
  color: #e54847;
}

.nav-item.active {
  color: #e54847;
  font-weight: 600;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 16px;
  right: 16px;
  height: 3px;
  background-color: #e54847;
}

/* 右侧用户中心 */
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 搜索框 */
.search-box {
  width: 200px;
}

.search-icon {
  cursor: pointer;
  color: #909399;
}

.search-icon:hover {
  color: #e54847;
}

/* 用户中心下拉菜单 */
.user-center {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-center:hover {
  background-color: #f5f5f5;
}

.user-avatar {
  background-color: #e54847;
  color: white;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #333333;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-icon {
  color: #909399;
  transition: transform 0.3s ease;
}

.user-center:hover .dropdown-icon {
  transform: rotate(180deg);
}

/* 主内容区域 */
.main-content {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 20px 24px;
}

/* 页脚 */
.footer {
  background-color: #2c3e50;
  padding: 24px;
  color: #ffffff;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}

.footer-links {
  margin-bottom: 12px;
}

.footer-link {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  margin: 0 12px;
  font-size: 14px;
  transition: color 0.3s;
}

.footer-link:hover {
  color: #ffffff;
}

.divider {
  color: rgba(255, 255, 255, 0.4);
}

.copyright {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
}

/* 下拉菜单自定义样式 */
:deep(.el-dropdown-menu) {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  font-size: 14px;
  color: #333333;
}

:deep(.el-dropdown-menu__item:hover) {
  background-color: #f5f5f5;
  color: #e54847;
}

:deep(.el-dropdown-menu__item.is-divided) {
  border-top: 1px solid #ebeef5;
}

:deep(.el-dropdown-menu__item .el-icon) {
  color: #606266;
  font-size: 16px;
}

:deep(.el-dropdown-menu__item:hover .el-icon) {
  color: #e54847;
}

/* 城市选择器自定义样式 */
:deep(.city-popover) {
  padding: 0;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .header-container {
    padding: 0 16px;
  }

  .search-box {
    width: 150px;
  }
}

@media (max-width: 768px) {
  .header-left {
    gap: 12px;
  }

  .logo-text {
    font-size: 18px;
  }

  .nav-item {
    padding: 0 10px;
    font-size: 14px;
  }

  .user-name {
    display: none;
  }

  .search-box {
    width: 120px;
  }

  .province-city-container {
    grid-template-columns: 1fr;
  }

  .province-list {
    height: 150px;
  }

  .city-list {
    height: 200px;
  }
}

@media (max-width: 576px) {
  .location-selector {
    margin-right: 12px;
  }

  .nav-item {
    padding: 0 6px;
    font-size: 12px;
  }

  .search-box {
    width: 100px;
  }

  .logo-text {
    font-size: 16px;
  }
}
</style>