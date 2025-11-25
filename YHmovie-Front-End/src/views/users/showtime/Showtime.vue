<template>
  <div class="movie-showtimes-page">
    <!-- 电影海报滚动区域 -->
    <div class="movie-posters-section">
      <div class="posters-container">
        <div class="poster-scroll">
          <div 
            v-for="movie in moviesList" 
            :key="movie.movieId" 
            class="poster-item" 
            :class="{'is-current': movie.movieId === movieId}"
            @click="switchMovie(movie.movieId)">
            <div class="poster-image">
              <img v-if="movie.moviePosterUrl" :src="movie.moviePosterUrl" :alt="movie.movieName" />
              <div class="no-poster" v-else>
                <el-icon><Picture /></el-icon>
                <span>暂无海报</span>
              </div>
              <div class="rating-badge" v-if="movie.movieRating">{{ movie.movieRating }}</div>
            </div>
            <div class="movie-name">{{ movie.movieName }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 电影详细信息 -->
    <div class="movie-details-section">
      <div class="movie-info-container">
        <div class="movie-main-info">
          <h1 class="movie-title">{{ currentMovie?.movieName || '加载中...' }}</h1>
          <div class="movie-rating" v-if="currentMovie?.movieRating">
            <span class="rating-label">{{ currentMovie.movieRating }} 分</span>
          </div>
          <div class="movie-rating" v-else>
            <span class="rating-label">暂无评分</span>
          </div>
        </div>

        <div class="movie-meta">
          <div class="meta-item">
            <span class="meta-label">时长：</span>
            <span class="meta-value">{{ currentMovie?.movieDuration || '--' }}分钟</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">类型：</span>
            <span class="meta-value">{{ formatMovieTypes(currentMovie?.movieTypes) }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">主演：</span>
            <span class="meta-value">{{ formatActors(actorsList) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 观影时间选择 -->
    <div class="date-selection-section">
      <div class="date-selector">
        <span class="date-label">观影时间：</span>
        <div class="date-buttons">
          <el-button 
            v-for="date in availableDates" 
            :key="date.value"
            :type="selectedDate === date.value ? 'primary' : 'default'"
            :class="{ 'date-active': selectedDate === date.value }" 
            @click="selectDate(date.value)"
            class="date-btn">
            {{ date.label }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 放映时间表 -->
    <div class="showtimes-section">
      <div class="showtimes-container">
        <div class="showtimes-header">
          <div class="header-item">放映时间</div>
          <div class="header-item">语言版本</div>
          <div class="header-item">放映厅</div>
          <div class="header-item">售价（元）</div>
          <div class="header-item">选座购票</div>
        </div>

        <div class="showtimes-list" v-loading="loading">
          <template v-if="filteredShowtimes.length > 0">
            <div v-for="showtime in filteredShowtimes" :key="showtime.movieHallId" class="showtime-item">
              <div class="time-info">
                <div class="start-time">{{ formatTime(showtime.showtimeStartTime) }}</div>
                <div class="end-time">{{ calculateEndTime(showtime.showtimeStartTime, currentMovie?.movieDuration) }}散场</div>
              </div>
              <div class="language-info">
                <span class="language">{{ showtime.movieLanguage }}2D</span>
              </div>
              <div class="hall-info">
                <span class="hall-name">{{ showtime.movieHallNumber }}号厅</span>
              </div>
              <div class="price-info">
                <span class="price">¥{{ showtime.leastPrice.toFixed(1) }}</span>
              </div>
              <div class="booking-info">
                <el-button class="booking-btn" @click="goToSeatSelection(showtime)">
                  选座购票
                </el-button>
              </div>
            </div>
          </template>
          <div v-else-if="!loading" class="empty-state">
            当前日期暂无场次
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { movieDetailById, moviesByCinemaId, movieActors } from '@/api/movies'
import { showtimesLeastPrice } from '@/api/showtimes'

const route = useRoute()
const router = useRouter()

// 获取路由参数
const cinemaId = ref(route.params.cinemaId)
const movieId = ref(route.query.movieId || null)
const selectedDate = ref('')
const moviesList = ref([])
const currentMovie = ref(null)
const actorsList = ref([])
const showtimesList = ref({})
const loading = ref(false)

// 计算可用日期 - 只取三天
const availableDates = computed(() => {
  const today = new Date()
  const dates = []

  // 只显示三天的日期
  for (let i = 0; i < 3; i++) {
    const date = new Date(today)
    date.setDate(today.getDate() + i)

    const month = date.getMonth() + 1
    const day = date.getDate()

    let label = ''
    if (i === 0) label = `今天 ${month}月${day}日`
    else if (i === 1) label = `明天 ${month}月${day}日`
    else label = `周${['日', '一', '二', '三', '四', '五', '六'][date.getDay()]} ${month}月${day}日`

    dates.push({
      label,
      value: date.toISOString().split('T')[0]
    })
  }

  return dates
})

// 格式化电影类型，用逗号分隔
const formatMovieTypes = (types) => {
  if (!types || !types.length) return '--'
  return types.slice(0, 2).join('、')
}

// 格式化演员列表
const formatActors = (actors) => {
  if (!actors || !actors.length) return '--'
  
  // 使用map提取每个演员对象的actorName属性，然后只取前3个
  return actors.slice(0, 3).map(actor => actor.actorName).join('、')
}

// 过滤当前日期的放映场次
const filteredShowtimes = computed(() => {
  if (!selectedDate.value || !showtimesList.value[selectedDate.value]) return []

  return showtimesList.value[selectedDate.value].sort((a, b) => 
    a.showtimeStartTime.localeCompare(b.showtimeStartTime)
  )
})

// 格式化时间 (把 10:30:30 转为 10:30)
const formatTime = (timeString) => {
  return timeString.substring(0, 5)
}

// 计算散场时间
const calculateEndTime = (startTime, duration = 120) => {
  const [hours, minutes] = startTime.split(':').map(Number)
  const startMinutes = hours * 60 + minutes
  const endMinutes = startMinutes + duration
  const endHours = Math.floor(endMinutes / 60) % 24
  const endMins = endMinutes % 60

  return `${String(endHours).padStart(2, '0')}:${String(endMins).padStart(2, '0')}`
}

// 选择日期
const selectDate = (date) => {
  selectedDate.value = date
}

// 切换电影
const switchMovie = async (newMovieId) => {
  if (newMovieId === movieId.value) return

  movieId.value = newMovieId

  // 更新URL，保留路径参数但修改查询参数
  router.replace({
    path: `/users/showtimes/${cinemaId.value}`,
    query: { movieId: newMovieId }
  })

  await Promise.all([
    fetchMovieDetail(),
    fetchMovieActors(),
    fetchShowtimes()
  ])
}

// 跳转到选座页面
const goToSeatSelection = (showtime) => {
  router.push({
    path: '/users/seats',
    query: {
      movieId: movieId.value,
      cinemaId: cinemaId.value,
      movieHallId: showtime.movieHallId,
      showtimeShowDate: selectedDate.value, // 当前选择的日期
      showtimeStartTime: showtime.showtimeStartTime ,// 场次开始时间
      showtimesLeastPrice: showtime.leastPrice // 场次最低价
    }
  })
}

// 获取该影院所有电影
const fetchMovieList = async () => {
  loading.value = true
  try {
    const response = await moviesByCinemaId(cinemaId.value)
    if (response.code === 200 && response.data) {
      moviesList.value = response.data.rows || []
      
      // 如果没有指定电影ID，使用第一部电影
      if (!movieId.value && moviesList.value.length > 0) {
        movieId.value = moviesList.value[0].movieId
        // 更新URL
        router.replace({
          path: `/users/showtimes/${cinemaId.value}`,
          query: { movieId: movieId.value }
        })
      }
    } else {
      throw new Error(response.msg || '获取电影列表失败')
    }
  } catch (error) {
    console.error('获取电影列表失败:', error)
    ElMessage.error('获取电影列表失败')
  } finally {
    loading.value = false
  }
}

// 获取电影详情
const fetchMovieDetail = async () => {
  if (!movieId.value) return
  
  loading.value = true
  try {
    const response = await movieDetailById(movieId.value)
    if (response.code === 200 && response.data) {
      currentMovie.value = response.data
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

// 获取演员信息
const fetchMovieActors = async () => {
  if (!movieId.value) return
  
  try {
    const response = await movieActors(movieId.value)
    if (response.code === 200 && response.data) {
      actorsList.value = response.data.rows || []
    } else {
      actorsList.value = []
    }
  } catch (error) {
    console.error('获取演员信息失败:', error)
    actorsList.value = []
  }
}

// 获取场次信息
const fetchShowtimes = async () => {
  if (!movieId.value) return
  
  loading.value = true
  try {
    const response = await showtimesLeastPrice(cinemaId.value, movieId.value)
    if (response.code === 200 && response.data) {
      showtimesList.value = response.data || {}
    } else {
      throw new Error(response.msg || '获取场次信息失败')
    }
  } catch (error) {
    console.error('获取场次信息失败:', error)
    ElMessage.error('获取场次信息失败')
  } finally {
    loading.value = false
  }
}

// 监听电影ID变化
watch(() => route.query.movieId, (newVal) => {
  if (newVal) {
    movieId.value = newVal
    Promise.all([
      fetchMovieDetail(),
      fetchMovieActors(),
      fetchShowtimes()
    ])
  }
})

onMounted(async () => {
  // 初始化选择今天的日期
  selectedDate.value = availableDates.value[0]?.value || ''
  
  // 获取数据
  await fetchMovieList()
  await Promise.all([
    fetchMovieDetail(),
    fetchMovieActors(),
    fetchShowtimes()
  ])
})
</script>

<style scoped>
.movie-showtimes-page {
  background: #f5f6f7;
  min-height: 100vh;
}

/* 电影海报区域 */
.movie-posters-section {
  background: #fff;
  padding: 30px 0;
  border-bottom: 1px solid #e8e8e8;
  position: relative;
  overflow: visible;
}

.posters-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
}

.poster-scroll {
  display: flex;
  gap: 25px; /* 增加间距 */
  overflow-x: auto;
  overflow-y: hidden; /* 防止垂直滚动 */
  align-items: center; /* 从flex-start改为center，使海报垂直居中 */
  min-height: 300px; /* 设置最小高度，确保容器足够高 */
}

.poster-scroll::-webkit-scrollbar {
  height: 6px;
}

.poster-scroll::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

.poster-scroll::-webkit-scrollbar-track {
  background-color: rgba(0, 0, 0, 0.05);
  border-radius: 10px;
}

.poster-item {
  flex-shrink: 0;
  cursor: pointer;
  transition: all 0.3s ease;
  transform-origin: center top;
  margin-bottom: 5px;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 150px;
}

.poster-item:hover {
  transform: translateY(-5px);
}

.poster-item.is-current {
  transform: scale(1.15);
  z-index: 10;
  margin: 0 10px;
}

.poster-item.is-current:hover {
  transform: scale(1.15) translateY(-5px);
}

.poster-image {
  position: relative;
  width: 140px;
  height: 196px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 2px solid transparent;
  transition: all 0.3s ease;
  background-color: #f0f0f0;
}

.no-poster {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #999;
}

.no-poster .el-icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.poster-item.is-current .poster-image {
  border-color: #ff5c38;
  box-shadow: 0 8px 25px rgba(255, 92, 56, 0.3);
}

.poster-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.movie-name {
  margin-top: 8px;
  font-size: 14px;
  text-align: center;
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #333;
}

.poster-item.is-current .movie-name {
  color: #ff5c38;
  font-weight: bold;
}

.rating-badge {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: #ffb400;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

/* 电影详情区域 */
.movie-details-section {
  background: #fff;
  padding: 30px 0;
  border-bottom: 1px solid #e8e8e8;
}

.movie-info-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.movie-main-info {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.movie-title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.movie-rating .rating-label {
  color: #ff5c38;
  font-weight: bold;
  font-size: 16px;
}

.movie-meta {
  display: flex;
  gap: 40px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
}

.meta-label {
  color: #666;
  margin-right: 8px;
}

.meta-value {
  color: #333;
  font-weight: 500;
}

/* 日期选择区域 */
.date-selection-section {
  background: #fff;
  padding: 25px 0;
  border-bottom: 1px solid #e8e8e8;
}

.date-selector {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.date-label {
  color: #333;
  font-weight: 500;
  white-space: nowrap;
}

.date-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.date-btn {
  border-radius: 20px;
  padding: 8px 20px;
  border: 1px solid #ddd;
  background: #fff;
  color: #333;
  transition: all 0.3s ease;
}

.date-btn:hover {
  border-color: #ff5c38;
  color: #ff5c38;
}

.date-btn.date-active {
  background: #ff5c38;
  border-color: transparent;
  color: #fff;
}

/* 放映时间表区域 */
.showtimes-section {
  padding: 30px 0;
}

.showtimes-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.showtimes-header {
  display: grid;
  grid-template-columns: 1.5fr 1fr 1.5fr 1fr 1.2fr;
  gap: 20px;
  padding: 15px 20px;
  border-bottom: 1px solid #e8e8e8;
  background-color: #fafafa;
  border-radius: 8px 8px 0 0;
}

.header-item {
  font-weight: 500;
  color: #666;
  text-align: center;
}

.showtimes-list {
  min-height: 100px;
  padding: 10px 0;
}

.showtime-item {
  display: grid;
  grid-template-columns: 1.5fr 1fr 1.5fr 1fr 1.2fr;
  gap: 20px;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.2s ease;
}

.showtime-item:hover {
  background-color: #fafafa;
}

.showtime-item:last-child {
  border-bottom: none;
}

.time-info {
  text-align: center;
}

.start-time {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.end-time {
  font-size: 12px;
  color: #999;
}

.language-info,
.hall-info,
.price-info {
  text-align: center;
}

.language {
  color: #333;
  font-weight: 500;
}

.hall-name {
  color: #333;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #ff5c38;
}

.booking-info {
  text-align: center;
}

.booking-btn {
  background: #ff5c38;
  border: none;
  color: #fff;
  padding: 10px 25px;
  border-radius: 20px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.booking-btn:hover {
  background: #ff4425;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 92, 56, 0.3);
}

/* 空状态 */
.empty-state {
  width: 100%;
  text-align: center;
  padding: 40px 0;
  color: #999;
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .movie-main-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .movie-meta {
    flex-direction: column;
    gap: 15px;
  }

  .date-selector {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .date-buttons {
    flex-wrap: wrap;
  }

  .showtimes-header,
  .showtime-item {
    grid-template-columns: 1fr;
    gap: 10px;
    text-align: center;
  }

  .header-item {
    padding: 5px 0;
    border-bottom: 1px solid #f0f0f0;
  }

  .header-item:last-child {
    border-bottom: none;
  }

  .showtime-item > div {
    padding: 10px 0;
    border-bottom: 1px solid #f9f9f9;
  }

  .showtime-item > div:last-child {
    border-bottom: none;
  }

  .poster-item.is-current {
    transform: scale(1.1);
    margin: 0 5px;
  }

  .poster-item.is-current:hover {
    transform: scale(1.1) translateY(-5px);
  }
}
</style>