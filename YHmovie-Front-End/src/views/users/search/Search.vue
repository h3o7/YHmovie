<!-- filepath: /Users/y/Desktop/java/projects/YHmovie/YHmovie-Front-End/src/views/users/search/Search.vue -->
<template>
  <div class="search-container">
    <!-- 标签导航 -->
    <div class="search-header">
      <div class="tab-wrapper">
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'movies' }"
          @click="switchTab('movies')"
        >
          电影
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'cinemas' }"
          @click="switchTab('cinemas')"
        >
          影院
        </div>
      </div>
    </div>

    <!-- 电影搜索结果 -->
    <div v-if="activeTab === 'movies'" class="search-results-content">
      <div v-if="moviesLoading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>
      
      <div v-else-if="movieResults.length > 0" class="movies-results">
        <div class="movie-grid">
          <div 
            v-for="movie in movieResults" 
            :key="movie.movieId" 
            class="movie-card" 
            @click="goToMovieInfo(movie.movieId)"
          >
            <div class="movie-poster">
              <img :src="movie.moviePosterUrl" :alt="movie.movieName" />
              <div class="movie-rating">
                <el-icon><Star /></el-icon>
                {{ movie.movieRating ? movie.movieRating.toFixed(1) : '暂无' }}
              </div>
            </div>
            <div class="movie-info">
                
               
              
              <h3 class="movie-name">{{ movie.movieName }}</h3>
              
            </div>
          </div>
        </div>
      </div>
      
      <div v-else class="empty-results">
        <el-empty description="未找到相关电影" :image-size="80" />
      </div>
    </div>

    <!-- 影院搜索结果 -->
    <div v-if="activeTab === 'cinemas'" class="search-results-content">
      <div v-if="cinemasLoading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>
      
      <div v-else-if="cinemaResults.length > 0" class="cinemas-results">
        <div 
          v-for="cinema in cinemaResults" 
          :key="cinema.cinemaId" 
          class="cinema-item"
          @click="goToCinemaShowtimes(cinema.cinemaId)"
        >
          <div class="cinema-content">
            <div class="cinema-main">
              <h3 class="cinema-name">
                {{ cinema.cinemaName }}
              </h3>
              <div class="cinema-details">
                <div class="cinema-address">
                  <el-icon><Location /></el-icon>
                  {{ cinema.cinemaAddress }}
                </div>
                <div class="cinema-phone">
                  <el-icon><Phone /></el-icon>
                  {{ cinema.cinemaPhone }}
                </div>
              </div>
            </div>
            
            <div class="cinema-action">
              <div class="price-tag">
                <span class="price-value">¥{{ cinema.lowestPrice.toFixed(1) }}</span>
                <span class="price-text">起</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div v-else class="empty-results">
        <el-empty description="未找到相关影院" :image-size="80" />
      </div>
    </div>
  </div>
</template>
  
  <script setup>
  import { ref, onMounted, watch } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { Star, Location, Phone } from '@element-plus/icons-vue'
  import { ElMessage } from 'element-plus'
  import { searchMovies } from '@/api/movies'
  import { searchCinemas } from '@/api/cinemas'
  
  const route = useRoute()
  const router = useRouter()
  
  // 搜索关键词
  const searchKeyword = ref('')
  
  // 标签切换
  const activeTab = ref('movies')
  
  // 加载状态
  const moviesLoading = ref(false)
  const cinemasLoading = ref(false)
  
  // 搜索结果
  const movieResults = ref([])
  const cinemaResults = ref([])
  
  // 切换标签
  const switchTab = (tab) => {
    activeTab.value = tab
  }
  
  // 城市信息
  const currentCity = ref({
    cityId: '500100',  // 默认重庆市
    cityName: '重庆市'
  })
  
  // 获取当前城市ID
  const initCityInfo = () => {
    try {
      const savedCity = localStorage.getItem('currentCity')
      if (savedCity) {
        currentCity.value = JSON.parse(savedCity)
      }
    } catch (error) {
      console.error('获取城市信息失败:', error)
    }
  }
  
  // 格式化票房数据
  const formatBoxOffice = (value) => {
    if (!value) return '暂无数据'
    
    const units = ['', '万', '亿', '万亿']
    let unitIndex = 0
    let formattedValue = value
    
    while (formattedValue >= 10000 && unitIndex < units.length - 1) {
      formattedValue /= 10000
      unitIndex++
    }
    
    return formattedValue.toFixed(2) + units[unitIndex]
  }
  
  // 搜索电影
  const searchForMovies = async (keyword) => {
    if (!keyword) return
    
    moviesLoading.value = true
    try {
      const response = await searchMovies(keyword)
      if (response && response.code === 200) {
        movieResults.value = response.data.rows || []
      } else {
        throw new Error(response?.msg || '搜索电影失败')
      }
    } catch (error) {
      console.error('搜索电影失败:', error)
      ElMessage.error('搜索电影失败，请稍后再试')
      movieResults.value = []
    } finally {
      moviesLoading.value = false
    }
  }
  
  // 搜索影院
  const searchForCinemas = async (keyword) => {
    if (!keyword) return
    
    cinemasLoading.value = true
    try {
      const response = await searchCinemas(keyword, currentCity.value.cityId)
      if (response && response.code === 200) {
        cinemaResults.value = response.data.rows || []
      } else {
        throw new Error(response?.msg || '搜索影院失败')
      }
    } catch (error) {
      console.error('搜索影院失败:', error)
      ElMessage.error('搜索影院失败，请稍后再试')
      cinemaResults.value = []
    } finally {
      cinemasLoading.value = false
    }
  }
  
  // 跳转到电影详情页
  const goToMovieInfo = (movieId) => {
    router.push({
      path: `/users/movieInfo/${movieId}`
    })
  }
  
  // 跳转到影院场次页面
  const goToCinemaShowtimes = (cinemaId) => {
    router.push({
      path: `/users/showtimes/${cinemaId}`
    })
  }
  
  // 执行搜索
  const performSearch = () => {
    const keyword = route.query.search
    if (keyword) {
      searchKeyword.value = keyword
      searchForMovies(keyword)
      searchForCinemas(keyword)
    } else {
      ElMessage.warning('搜索关键词不能为空')
      router.push('/')
    }
  }
  
  // 监听路由变化
  watch(
    () => route.query.search,
    (newKeyword) => {
      if (newKeyword) {
        searchKeyword.value = newKeyword
        performSearch()
      }
    }
  )
  
  onMounted(() => {
    initCityInfo()
    performSearch()
  })
  </script>
  
  <style scoped>
.search-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
}

.search-header {
  margin-bottom: 24px;
}

/* 标签样式 - 更简洁的设计 */
.tab-wrapper {
  display: flex;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
}

.tab-item {
  position: relative;
  padding: 12px 30px 12px 0;
  margin-right: 20px;
  font-size: 18px;
  cursor: pointer;
  color: #666;
  transition: all 0.2s ease;
}

.tab-item.active {
  color: #ff5f16;
  font-weight: 500;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 30px;
  height: 2px;
  background-color: #ff5f16;
}

.tab-item:hover {
  color: #ff5f16;
}

/* 内容区域 - 无边框无阴影 */
.search-results-content {
  background-color: transparent;
  min-height: 400px;
  padding: 0;
}

.loading-container {
  padding: 20px 0;
}

.empty-results {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  margin-top: 40px;
}

/* 电影网格布局 */
.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 30px;
  margin-top: 10px;
}

.movie-card {
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.movie-card:hover {
  transform: translateY(-5px);
}

.movie-card:hover .movie-poster img {
  opacity: 0.9;
}

.movie-poster {
  position: relative;
  height: 300px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 12px;
}

.movie-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.movie-rating {
  position: absolute;
  top: 8px;
  right: 8px;
  background-color: rgba(0, 0, 0, 0.7);
  color: #ffb400;
  border-radius: 4px;
  padding: 3px 6px;
  font-size: 12px;
  display: flex;
  align-items: center;
}

.movie-rating .el-icon {
  margin-right: 3px;
  font-size: 12px;
}

.movie-info {
  padding: 0;
}

.movie-name {
    text-align: center;
  margin: 0 0 6px;
  font-size: 18px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  
}

.movie-metrics {
  display: flex;
  align-items: center;
}

.box-office {
  color: #ff5f16;
  font-size: 14px;
  background-color: rgba(255, 95, 22, 0.08);
  padding: 2px 8px;
  border-radius: 12px;
  display: inline-block;
}

/* 影院列表 - 更简洁的设计 */
.cinema-item {
  transition: background-color 0.2s;
  cursor: pointer;
  padding: 20px 0;
}

.cinema-item:not(:last-child) {
  border-bottom: 1px solid #f9f9f9;
}

.cinema-item:hover {
  background-color: #f9f9f9;
}

.cinema-content {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.cinema-main {
  flex: 1;
}

.cinema-name {
  font-size: 17px;
  font-weight: 500;
  margin: 0 0 10px;
  color: #333;
}

.cinema-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cinema-address, .cinema-phone {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.cinema-address .el-icon, .cinema-phone .el-icon {
  color: #999;
  font-size: 14px;
  flex-shrink: 0;
}

.cinema-action {
  text-align: right;
  margin-left: 20px;
}

.price-tag {
  display: inline-flex;
  align-items: baseline;
  background-color: #fff0e9;
  padding: 5px 12px;
  border-radius: 4px;
}

.price-value {
  font-size: 18px;
  font-weight: 500;
  color: #ff5c38;
}

.price-text {
  font-size: 13px;
  color: #ff5c38;
  margin-left: 2px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .movie-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 15px;
  }
  
  .movie-poster {
    height: 200px;
  }
  
  .cinema-content {
    flex-direction: column;
  }
  
  .cinema-action {
    margin-left: 0;
    margin-top: 15px;
  }
  
  .cinema-name {
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .movie-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .tab-item {
    padding: 10px 20px 10px 0;
    font-size: 16px;
  }
}
</style>