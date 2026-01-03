<template>
  <div class="movies-page">
    <!-- 电影分类标签 -->
    <div class="movies-tab">
      <div class="tab-container">
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'showing' }"
          @click="switchTab('showing')"
        >
          正在热映
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'coming' }"
          @click="switchTab('coming')"
        >
          即将上映
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-container">
      <!-- 正在热映区域 -->
      <div v-if="activeTab === 'showing'" v-loading="showingLoading">
        <div class="movie-list" v-if="moviesList.length > 0">
          <div v-for="movie in moviesList" :key="movie.movieId" class="movie-card">
            <div class="movie-poster" @click="goToMovieDetail(movie.movieId)">
              <img :src="movie.moviePosterUrl" :alt="movie.movieName" />
              <div class="movie-rating" v-if="movie.movieRating">
                <span>{{ movie.movieRating }}</span>
              </div>
            </div>
            <div class="movie-info">
              <h3 class="movie-name" @click="goToMovieDetail(movie.movieId)">
                {{ movie.movieName }}
              </h3>
              <el-button type="primary" class="buy-btn" @click="goToCinemasMovie(movie.movieId)">
                购票
              </el-button>
            </div>
          </div>
        </div>
        <div v-else-if="!showingLoading" class="empty-state">
          暂无正在热映的电影
        </div>
      </div>

      <!-- 即将上映区域 -->
      <div v-else-if="activeTab === 'coming'" v-loading="comingLoading">
        <div class="movie-list" v-if="moviesList.length > 0">
          <div v-for="movie in moviesList" :key="movie.movieId" class="movie-card">
            <div class="movie-poster" @click="goToMovieDetail(movie.movieId)">
              <img :src="movie.moviePosterUrl" :alt="movie.movieName" />
              <div class="movie-date">
                {{ formatReleaseDate(movie.movieReleaseDate) }}
              </div>
            </div>
            <div class="movie-info">
              <h3 class="movie-name" @click="goToMovieDetail(movie.movieId)">
                {{ movie.movieName }}
              </h3>
              <div class="want-count" v-if="movie.wantCount">{{ movie.wantCount }}人想看</div>
              <el-button :type="movie.isWanted ? 'success' : 'default'" class="want-btn"
                @click="handleWantWatch(movie)">
                <el-icon v-if="movie.isWanted">
                  <Check />
                </el-icon>
                {{ movie.isWanted ? '已想看' : '想看' }}
              </el-button>
            </div>
          </div>
        </div>
        <div v-else-if="!comingLoading" class="empty-state">
          暂无即将上映的电影
        </div>
      </div>

      <!-- 分页器 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[15, 20, 30]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { Check } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { showingMovies, comingMovies } from '@/api/movies'

const router = useRouter()
const route = useRoute()

// 当前标签页
const activeTab = ref('showing')

// 分页相关
const currentPage = ref(1)
const pageSize = ref(15) // 默认每页15部电影 (3行x5列)
const total = ref(0)

// 电影数据
const moviesList = ref([])

// 加载状态
const showingLoading = ref(false)
const comingLoading = ref(false)

// 当前城市ID
const currentCityId = ref('500100') // 默认重庆


// 切换标签页
const switchTab = (tab) => {
  if (activeTab.value !== tab) {
    activeTab.value = tab
    currentPage.value = 1 // 切换标签时重置页码
    
    // 更新URL参数但不重新加载页面
    const query = tab === 'coming' ? { type: 'coming' } : {}
    router.push({ query })
    
    fetchMoviesList()
  }
}

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchMoviesList()
}

// 处理每页条数变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1 // 当每页显示数量变化时，重置为第一页
  fetchMoviesList()
}

// 获取电影列表
const fetchMoviesList = async () => {
  if (activeTab.value === 'showing') {
    await fetchShowingMovies()
  } else {
    await fetchComingMovies()
  }
  
  // 滚动到页面顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 获取正在热映电影
const fetchShowingMovies = async () => {
  showingLoading.value = true
  try {
    const response = await showingMovies(currentPage.value, pageSize.value)
    
    if (response.code === 200 && response.data) {
      console.log('正在热映电影数据:', response.data.rows)
      moviesList.value = response.data.rows.map(movie => ({
        ...movie,
        starRating: movie.movieRating ? movie.movieRating / 2 : 0,
      }))
      total.value = response.data.total || 0
    } else {
      throw new Error(response.msg || '获取热映电影失败')
    }
  } catch (error) {
    console.error('获取热映电影失败:', error)
    ElMessage.error('获取热映电影失败')
    moviesList.value = []
    total.value = 0
  } finally {
    showingLoading.value = false
  }
}

// 获取即将上映电影
const fetchComingMovies = async () => {
  comingLoading.value = true
  try {
    const response = await comingMovies(currentPage.value, pageSize.value)
    
    if (response.code === 200 && response.data) {
      console.log('即将上映电影数据:', response.data.rows)
      moviesList.value = response.data.rows.map(movie => ({
        ...movie,
        isWanted: false, // 默认未想看
        wantCount: movie.wantCount || Math.floor(Math.random() * 10000) + 1000 // 如果后端没提供，随机生成想看人数
      }))
      total.value = response.data.total || 0
    } else {
      throw new Error(response.msg || '获取即将上映电影失败')
    }
  } catch (error) {
    console.error('获取即将上映电影失败:', error)
    ElMessage.error('获取即将上映电影失败')
    moviesList.value = []
    total.value = 0
  } finally {
    comingLoading.value = false
  }
}

// 监听路由参数变化
watch(
  () => route.query,
  (query) => {
    // 从URL参数获取类型
    if (query.type === 'coming') {
      activeTab.value = 'coming'
    } else {
      activeTab.value = 'showing'
    }
    // 重置分页并获取数据
    currentPage.value = 1
    fetchMoviesList()
  },
  { immediate: true }
)

// 跳转到电影详情页
const goToMovieDetail = (movieId) => {
  router.push({
    path: `/users/movieInfo/${movieId}`
  })
}
const goToCinemasMovie = (movieId) => {
  router.push(`/users/cinemas/?movieId=${movieId}`)
}

// 想看电影
const handleWantWatch = (movie) => {
  movie.isWanted = !movie.isWanted
  // 这里应该发起后端请求保存用户想看状态
  // ...
  
  ElMessage({
    type: movie.isWanted ? 'success' : 'info',
    message: movie.isWanted ? '已添加到想看' : '已取消想看',
    duration: 1500
  })
}

// 格式化日期
const formatReleaseDate = (dateStr) => {
  if (!dateStr) return '未定'

  const date = new Date(dateStr)
  const month = date.getMonth() + 1
  const day = date.getDate()

  return `${month}月${day}日上映`
}

// 监听城市变化
const handleCityChange = (event) => {
  const city = event.detail
  if (city && city.cityId) {
    currentCityId.value = city.cityId
    // 刷新电影数据
    currentPage.value = 1
    fetchMoviesList()
  }
}

// 组件挂载
onMounted(() => {
  // 从本地存储获取当前选择的城市
  try {
    const savedCity = localStorage.getItem('currentCity')
    if (savedCity) {
      const city = JSON.parse(savedCity)
      if (city && city.cityId) {
        currentCityId.value = city.cityId
      }
    }
  } catch (error) {
    console.error('获取城市信息失败:', error)
  }
  
  // 监听城市变化事件
  window.addEventListener('city-changed', handleCityChange)
})

// 组件卸载
onUnmounted(() => {
  // 移除事件监听
  window.removeEventListener('city-changed', handleCityChange)
})
</script>

<style scoped>
.movies-page {
  width: 100%;
  background-color: #f5f6f7;
  min-height: 100vh;
  padding-bottom: 40px;
}

/* 标签页优化 */
.movies-tab {
  background-color: white;
  box-shadow: none;
  margin-bottom: 30px;
  padding: 15px 0; /* 增加上下内边距 */
  position: relative;
}

/* 可选：添加一条很浅的分隔线作为底部边界 */
.movies-tab::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 1px;
  background-color: #f0f0f0;
}

.tab-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
}

/* 增强标签设计 */
.tab-item {
  font-size: 20px; /* 增大字体 */
  font-weight: bold;
  color: #666;
  padding: 0 30px;
  cursor: pointer;
  height: 100%;
  display: flex;
  align-items: center;
  position: relative;
  transition: all 0.3s;
  margin: 0 20px;
}

.tab-item:hover {
  color: #ff5c38;
  transform: translateY(-2px); /* 添加悬停时轻微上移效果 */
}

.tab-item.active {
  color: #ff5c38;
}

/* 改进激活状态指示器 */
.tab-item.active::after {
  content: "";
  position: absolute;
  bottom: -15px; /* 调整指示器位置 */
  left: 20%;
  width: 60%;
  height: 3px;
  background-color: #ff5c38;
  border-radius: 3px;
  transition: all 0.3s;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .tab-item {
    font-size: 18px;
    padding: 0 20px;
    margin: 0 10px;
  }
}

@media (max-width: 576px) {
  .tab-item {
    font-size: 16px;
    padding: 0 15px;
    margin: 0 5px;
  }
}

/* 主内容区域 */
.main-container {
  width: 1200px;
  max-width: 100%;
  padding: 0 20px;
  margin: 0 auto;
}

/* 电影列表 */
.movie-list {
  display: grid;
  /* 
   修改点：将 1fr 改为 minmax(0, 1fr)
   原因：在某些浏览器中，如果 Grid Item 内容（如图片）比列宽大，1fr 会被撑开。
   minmax(0, 1fr) 强制让列宽最小可以是0，从而使得图片严格遵循 Grid 宽度，防止变形。
  */
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 30px;
  margin-bottom: 30px;
}

.movie-card {
  transition: transform 0.3s ease;
  /* 确保卡片不会溢出 Grid 单元格 */
  overflow: hidden; 
}

.movie-card:hover {
  transform: translateY(-5px);
}

.movie-poster {
  position: relative;
  padding-bottom: 140%;
  overflow: hidden;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.movie-poster img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.movie-card:hover .movie-poster img {
  transform: scale(1.05);
}

.movie-rating {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: rgba(255, 92, 56, 0.95);
  color: white;
  font-size: 14px;
  font-weight: bold;
  padding: 4px 10px;
  border-radius: 5px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

.movie-date {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 15px 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.85), rgba(0, 0, 0, 0));
  color: white;
  font-size: 14px;
  text-align: center;
  font-weight: 500;
}

.movie-info {
  padding: 12px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.movie-name {
  font-size: 16px;
  margin: 0 0 10px;
  font-weight: 500;
  color: #333;
  cursor: pointer;
  text-align: center;
  width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.movie-name:hover {
  color: #ff5c38;
}

.buy-btn {
  width: 80%;
  padding: 8px 0;
  font-size: 16px;
  border-radius: 20px;
}

.want-count {
  font-size: 14px;
  color: #999;
  margin-bottom: 10px;
}

.want-btn {
  width: 80%;
  padding: 8px 0;
  font-size: 16px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.want-btn .el-icon {
  margin-right: 5px;
}

/* 空状态 */
.empty-state {
  width: 100%;
  text-align: center;
  padding: 60px 0;
  color: #999;
  font-size: 16px;
  background-color: #f9f9f9;
  border-radius: 10px;
  margin-bottom: 30px;
}

/* 分页容器 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 20px 0;
}

/* 响应式 */
@media (max-width: 1200px) {
  .movie-list {
    grid-template-columns: repeat(5, minmax(0, 1fr));
    gap: 20px;
  }
}

@media (max-width: 992px) {
  .movie-list {
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .tab-container {
    height: 50px;
  }
  
  .tab-item {
    font-size: 16px;
    padding: 0 15px;
  }

  .movie-list {
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 15px;
  }
}

@media (max-width: 576px) {
  .movie-list {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 15px;
  }

  .movie-name {
    font-size: 14px;
  }

  .buy-btn,
  .want-btn {
    font-size: 14px;
  }
}
</style>