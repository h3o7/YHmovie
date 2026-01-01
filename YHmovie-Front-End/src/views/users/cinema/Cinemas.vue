<template>
  <div class="cinemas-page">
    <!-- 电影信息展示区域 (仅当有movieId时显示) -->
    <section v-if="movieId && movieDetail" class="movie-info-section">
      <div class="movie-info-container">
        <div class="movie-poster">
          <img :src="movieDetail.moviePosterUrl" :alt="movieDetail.movieName" />
        </div>
        <div class="movie-details">
          <h1 class="movie-title">{{ movieDetail.movieName }}</h1>
          <div class="movie-meta">
            <div class="movie-rating" v-if="movieDetail.movieRating">
              <el-icon><Star /></el-icon>
              <span class="rating-value">{{ movieDetail.movieRating }}</span>
            </div>
            <div class="movie-type">
              {{ movieDetail.movieTypes ? movieDetail.movieTypes.join(' / ') : '' }}
            </div>
            <div class="movie-length">
              {{ movieDetail.movieDuration }}分钟
            </div>
            <div class="movie-release">
              {{ formatReleaseDate(movieDetail.movieReleaseDate) }}
            </div>
          </div>
          <div class="movie-desc">{{ movieDetail.movieDescription }}</div>
        </div>
      </div>
    </section>

    <!-- 影院展示区域 -->
    <div class="main-container">
      <section class="cinemas-section">
        <div class="section-header">
          <h2 class="section-title">
            {{ movieId ? `《${movieDetail?.movieName || ''}》上映影院` : '全城影院' }}
          </h2>
          
          <!-- 可以添加影院筛选选项 -->
          <div class="filters">
            <el-radio-group v-model="sortBy" size="small">
              <el-radio-button label="default">默认排序</el-radio-button>
              <el-radio-button label="price">价格排序</el-radio-button>
            </el-radio-group>
          </div>
        </div>

        <!-- 影院列表 -->
        <div class="cinemas-container" v-loading="loading">
          <div v-if="cinemasList.length > 0" class="cinemas-list">
            <div v-for="cinema in cinemasList" :key="cinema.cinemaId" class="cinema-item">
              <div class="cinema-content">
                <div class="cinema-info">
                  <h3 class="cinema-name" @click="goToCinemaShowtimes(cinema.cinemaId)">
                    {{ cinema.cinemaName }}
                  </h3>
                  <div class="cinema-address">
                    <el-icon><Location /></el-icon>
                    {{ cinema.cinemaAddress }}
                  </div>
                  <div class="cinema-phone">
                    <el-icon><Phone /></el-icon>
                    {{ cinema.cinemaPhone }}
                  </div>
                </div>
                <div class="cinema-action">
                  <div class="price-tag">
                    <span class="price-value">¥{{ cinema.lowestPrice.toFixed(1) }}</span>
                    <span class="price-text">起</span>
                  </div>
                  <el-button type="danger" @click="goToCinemaShowtimes(cinema.cinemaId)" class="select-btn">
                    选座购票
                  </el-button>
                </div>
              </div>
            </div>
          </div>
          <div v-else-if="!loading" class="empty-state">
            暂无影院信息
          </div>
        </div>

        <!-- 分页器 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 15, 20, 30]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            background
          />
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, Location, Phone } from '@element-plus/icons-vue'
import { cinemasListByCityId, cinemasListByCityIdAndMovieId } from '@/api/cinemas'
import { movieDetailById } from '@/api/movies'

const route = useRoute()
const router = useRouter()

// 获取URL参数
const movieId = computed(() => route.query.movieId)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 数据相关
const loading = ref(false)
const cinemasList = ref([])
const movieDetail = ref(null)
const sortBy = ref('default') // 默认排序

// 获取城市ID
const currentCity = ref({
  cityId: '330100',  // 默认杭州
  cityName: '杭州市'
})

// 初始化城市信息
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

// 获取影院列表
const fetchCinemasList = async () => {
  loading.value = true
  try {
    const cityId = currentCity.value.cityId
    let response
    
    if (movieId.value) {
      response = await cinemasListByCityIdAndMovieId(
        cityId,
        movieId.value,
        currentPage.value,
        pageSize.value
      )
    } else {
      response = await cinemasListByCityId(
        cityId,
        currentPage.value,
        pageSize.value
      )
    }
    
    if (response.code === 200 && response.data) {
      cinemasList.value = response.data.rows || []
      total.value = response.data.total || 0
      
      // 根据排序方式处理影院列表
      if (sortBy.value === 'price') {
        cinemasList.value.sort((a, b) => a.lowestPrice - b.lowestPrice)
      }
    } else {
      ElMessage.error(response.msg || '获取影院列表失败')
    }
  } catch (error) {
    console.error('获取影院列表失败:', error)
    ElMessage.error('获取影院列表失败')
  } finally {
    loading.value = false
  }
}

// 获取电影详情
const fetchMovieDetail = async () => {
  if (!movieId.value) {
    movieDetail.value = null
    return
  }
  
  loading.value = true
  try {
    const response = await movieDetailById(movieId.value)
    if (response.code === 200 && response.data) {
      movieDetail.value = response.data
    } else {
      throw new Error(response.msg || '获取电影详情失败')
    }
  } catch (error) {
    console.error('获取电影详情失败:', error)
    ElMessage.error('获取电影详情失败')
  } finally {
    loading.value = false
  }
}

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchCinemasList()
}

// 处理每页显示数量变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchCinemasList()
}

// 跳转到影院场次页面
const goToCinemaShowtimes = (cinemaId) => {
  // 构建路径，根据是否有movieId参数决定是否添加查询参数
  let path = `/users/showtimes/${cinemaId}`
  let query = {}
  
  if (movieId.value) {
    query.movieId = movieId.value
  }
  
  router.push({ 
    path,
    query: Object.keys(query).length > 0 ? query : undefined
  })
}

// 格式化上映日期
const formatReleaseDate = (dateStr) => {
  if (!dateStr) return '未定'

  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()

  return `${year}年${month}月${day}日上映`
}

// 监听排序方式变化
watch(sortBy, () => {
  fetchCinemasList()
})

// 监听电影ID变化
watch(() => route.query.movieId, () => {
  fetchMovieDetail()
  // 重置分页
  currentPage.value = 1
  fetchCinemasList()
})

// 监听城市变化事件
const handleCityChange = (event) => {
  const city = event.detail
  if (city && city.cityId) {
    currentCity.value = city
    // 重置分页
    currentPage.value = 1
    fetchCinemasList()
  }
}

// 组件挂载
onMounted(() => {
  initCityInfo()
  fetchMovieDetail()
  fetchCinemasList()
  
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
.cinemas-page {
  width: 100%;
  background-color: #f5f6f7;
  min-height: 100vh;
  padding-bottom: 40px;
}

/* 电影信息区域 */
.movie-info-section {
  background: linear-gradient(to bottom, #3d3d3d, #282828);
  padding: 40px 0;
  color: white;
}

.movie-info-container {
  width: 1200px;
  max-width: 100%;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: flex-start;
  gap: 30px;
}

.movie-poster {
  flex-shrink: 0;
  width: 160px;
  height: 220px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.movie-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.movie-details {
  flex: 1;
}

.movie-title {
  font-size: 26px;
  font-weight: bold;
  margin: 0 0 15px;
}

.movie-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.movie-rating {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #ffb400;
  font-weight: bold;
}

.rating-value {
  font-size: 18px;
}

.movie-desc {
  font-size: 14px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.8);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 800px;
}

/* 主内容区域 */
.main-container {
  width: 1200px;
  max-width: 100%;
  padding: 0 20px;
  margin: 0 auto;
}

/* 影院区域 */
.cinemas-section {
  margin-top: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 22px;
  font-weight: bold;
  margin: 0;
  color: #333;
  position: relative;
  padding-left: 16px;
}

.section-title::before {
  content: "";
  position: absolute;
  left: 0;
  top: 20%;
  height: 60%;
  width: 6px;
  background-color: #ff5c38;
  border-radius: 3px;
}

.filters {
  display: flex;
  gap: 20px;
}

/* 影院列表 */
.cinemas-container {
  min-height: 400px;
}

.cinemas-list {
  display: flex;
  flex-direction: column;
}

/* 新的影院样式，参考猫眼风格 */
.cinema-item {
  position: relative;
  border-bottom: 1px solid #f0f0f0;
}

.cinema-item:last-child {
  border-bottom: none;
}

.cinema-content {
  padding: 24px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cinema-info {
  flex: 1;
}

.cinema-name {
  font-size: 18px;
  font-weight: bold;
  margin: 0 0 10px;
  color: #333;
  cursor: pointer;
}

.cinema-name:hover {
  color: #ff5c38;
}

.cinema-address, .cinema-phone {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.cinema-phone {
  margin-bottom: 0;
}

.cinema-address .el-icon, .cinema-phone .el-icon {
  color: #999;
  font-size: 14px;
}

.cinema-action {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
  min-width: 120px;
}

.price-tag {
  display: flex;
  align-items: baseline;
}

.price-value {
  font-size: 20px;
  font-weight: bold;
  color: #ff5c38;
}

.price-text {
  font-size: 14px;
  color: #ff5c38;
  margin-left: 2px;
}

.select-btn {
  min-width: 100px;
  font-size: 14px;
  border-radius: 18px;
  padding: 8px 16px;
}

/* 空状态 */
.empty-state {
  width: 100%;
  text-align: center;
  padding: 80px 0;
  color: #999;
  font-size: 16px;
  border-top: 1px solid #f0f0f0;
}

/* 分页容器 */
.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .movie-info-container {
    padding: 0 15px;
  }

  .movie-poster {
    width: 140px;
    height: 196px;
  }

  .movie-title {
    font-size: 22px;
  }

  .main-container {
    padding: 0 15px;
  }
}

@media (max-width: 768px) {
  .movie-info-section {
    padding: 30px 0;
  }

  .movie-info-container {
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 20px;
  }

  .movie-meta {
    justify-content: center;
  }

  .movie-desc {
    max-width: 100%;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .cinema-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
    padding: 20px 0;
  }

  .cinema-action {
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }
}

@media (max-width: 576px) {
  .movie-poster {
    width: 120px;
    height: 168px;
  }

  .movie-title {
    font-size: 20px;
    margin-bottom: 10px;
  }

  .movie-meta {
    gap: 12px;
    margin-bottom: 15px;
  }

  .section-title {
    font-size: 18px;
  }

  .cinema-name {
    font-size: 16px;
  }

  .price-value {
    font-size: 18px;
  }

  .select-btn {
    min-width: 90px;
    font-size: 12px;
    padding: 6px 12px;
  }
}
</style>