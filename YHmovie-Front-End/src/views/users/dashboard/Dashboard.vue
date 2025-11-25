<template>
  <div class="dashboard">
    <!-- filepath: /Users/y/Desktop/java/projects/YHmovie/YHmovie-Front-End/src/views/users/dashboard/Dashboard.vue -->
    <!-- 顶部轮播图 -->
    <section class="banner-section">
      <el-carousel :interval="4000" type="card" height="460px" :autoplay="true" indicator-position="none" arrow="always"
        class="custom-carousel">
        <el-carousel-item v-for="movie in topBoxOfficeMovies" :key="movie.movieId">
          <div class="banner-item" @click="goToMovieDetail(movie.movieId)">
            <div class="banner-image-container">
              <img :src="movie.moviePosterUrl" :alt="movie.movieName" class="banner-image" />
              <div class="banner-overlay"></div>
            </div>
            <div class="banner-info">
              <h3 class="banner-title">{{ movie.movieName }}</h3>
              <div class="banner-desc">
                <div class="rating-container">
                  <el-icon>
                    <Star />
                  </el-icon>
                  <span class="rating">{{ movie.movieRating || '暂无评分' }}</span>
                </div>
                <div class="box-office-container">
                  <el-icon>
                    <Ticket />
                  </el-icon>
                  <span class="box-office">票房: {{ formatBoxOffice(movie.movieBoxOffice) }}</span>
                </div>
              </div>
              <el-button type="danger" class="banner-button" round @click.stop="goToCinemasMovie(movie.movieId)">
                立即购票
              </el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>
    <!-- 主要内容区域 -->
    <div class="main-container">
      <!-- 正在热映区域 -->
      <section class="movie-section">
        <div class="section-header">
          <h2 class="section-title">正在热映</h2>
          <router-link to="/users/movies" class="view-more">
            全部 <el-icon>
              <ArrowRight />
            </el-icon>
          </router-link>
        </div>

        <div v-loading="showingLoading">
          <div class="movie-list" v-if="displayShowingMovies.length > 0">
            <div v-for="movie in displayShowingMovies" :key="movie.movieId" class="movie-card">
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
      </section>

      <!-- 即将上映区域 -->
      <section class="movie-section">
        <div class="section-header">
          <h2 class="section-title">即将上映</h2>
          <router-link to="/users/movies?type=coming" class="view-more">
            全部 <el-icon>
              <ArrowRight />
            </el-icon>
          </router-link>
        </div>

        <div v-loading="comingLoading">
          <div class="movie-list" v-if="displayComingMovies.length > 0">
            <div v-for="movie in displayComingMovies" :key="movie.movieId" class="movie-card">
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
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { ArrowRight, Check, Star, Ticket } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { showingMovies, comingMovies, boxOfficeRanking } from '@/api/movies'

const router = useRouter()

// 电影数据
const showingMoviesList = ref([])
const comingMoviesList = ref([])
const boxOfficeRankingList = ref([])

// 显示数据（每行5个，最多10个）
const displayShowingMovies = computed(() => {
  const list = showingMoviesList.value.slice(0, 10)
  // 为每个电影添加随机想看人数（实际项目中应从后端获取）
  return list.map(movie => ({
    ...movie,
    starRating: movie.movieRating ? movie.movieRating / 2 : 0,
  }))
})

const displayComingMovies = computed(() => {
  const list = comingMoviesList.value.slice(0, 10)
  // 为每个电影添加随机想看人数（实际项目中应从后端获取）
  return list.map(movie => ({
    ...movie,
    isWanted: false,
    wantCount: Math.floor(Math.random() * 10000) + 1000
  }))
})

const topBoxOfficeMovies = computed(() => {
  return boxOfficeRankingList.value.slice(0, 5).map(movie => ({
    ...movie,
    starRating: movie.movieRating ? movie.movieRating / 2 : 0,
  }))
})

// 加载状态
const showingLoading = ref(false)
const comingLoading = ref(false)
const rankingLoading = ref(false)

// 当前城市ID
const currentCityId = ref('500100') // 默认重庆

// 获取正在热映电影
const fetchShowingMovies = async () => {
  showingLoading.value = true
  try {
    const response = await showingMovies(1,10)
    if (response.code === 200 && response.data && response.data.rows) {
      console.log('正在热映电影数据:', response.data.rows)
      showingMoviesList.value = response.data.rows
    } else {
      throw new Error(response.msg || '获取热映电影失败')
    }
  } catch (error) {
    console.error('获取热映电影失败:', error)
    ElMessage.error('获取热映电影失败')
  } finally {
    showingLoading.value = false
  }
}

// 获取即将上映电影
const fetchComingMovies = async () => {
  comingLoading.value = true
  try {
    const response = await comingMovies(1, 10)
    if (response.code === 200 && response.data && response.data.rows) {
      console.log('即将上映电影数据:', response.data.rows)
      comingMoviesList.value = response.data.rows
    } else {
      throw new Error(response.msg || '获取即将上映电影失败')
    }
  } catch (error) {
    console.error('获取即将上映电影失败:', error)
    ElMessage.error('获取即将上映电影失败')
  } finally {
    comingLoading.value = false
  }
}

// 获取票房榜单
const fetchBoxOfficeRanking = async () => {
  rankingLoading.value = true
  try {
    const response = await boxOfficeRanking(1,5)
    if (response.code === 200 && response.data) {
      boxOfficeRankingList.value = response.data.rows || []
    } else {
      throw new Error(response.msg || '获取票房榜单失败')
    }
  } catch (error) {
    console.error('获取票房榜单失败:', error)
    ElMessage.error('获取票房榜单失败')
  } finally {
    rankingLoading.value = false
  }
}

// 跳转到电影详情页
const goToMovieDetail = (movieId) => {
  router.push({
    path: `/users/movieInfo/${movieId}`
    
  })
}

// 跳转到电影购票页
const goToCinemasMovie = (movieId) => {
  router.push(`/users/cinemas?movieId=${movieId}`)
}

// 想看电影
const handleWantWatch = (movie) => {
  movie.isWanted = !movie.isWanted
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

// 格式化票房
const formatBoxOffice = (boxOffice) => {
  if (!boxOffice) return '未知'

  if (boxOffice >= 100000000) {
    return (boxOffice / 100000000).toFixed(2) + '亿'
  } else if (boxOffice >= 10000) {
    return (boxOffice / 10000).toFixed(2) + '万'
  } else {
    return boxOffice + '元'
  }
}

// 监听城市变化
const handleCityChange = (event) => {
  const city = event.detail
  if (city && city.cityId) {
    currentCityId.value = city.cityId
    // 刷新电影数据
    fetchShowingMovies()
    fetchComingMovies()
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

  // 获取数据
  fetchShowingMovies()
  fetchComingMovies()
  fetchBoxOfficeRanking()

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
.dashboard {
  width: 100%;
  background-color: #f5f6f7;
  min-height: 100vh;
}

/* 轮播图相关样式 */
.banner-section {
  width: 100%;
  margin: 20px 0 40px;
  padding: 0 20px;
}

/* 自定义轮播图样式 */
.custom-carousel :deep(.el-carousel__arrow) {
  width: 50px;
  height: 50px;
  background-color: rgba(31, 45, 61, 0.7);
  border-radius: 50%;
  font-size: 22px;
  transition: all 0.3s;
}

.custom-carousel :deep(.el-carousel__arrow:hover) {
  background-color: #ff5c38;
  transform: scale(1.1);
}

.custom-carousel :deep(.el-carousel__arrow--left) {
  left: 20px;
}

.custom-carousel :deep(.el-carousel__arrow--right) {
  right: 20px;
}

.custom-carousel :deep(.el-carousel__item) {
  border-radius: 12px;
  overflow: hidden;
}

.custom-carousel :deep(.el-carousel__item--card) {
  border-radius: 12px;
}

/* 轮播项目内容 */
.banner-item {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  height: 100%;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  transition: transform 0.3s ease;
}

.banner-item:hover {
  transform: translateY(-5px);
}

.banner-image-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    to top,
    rgba(0, 0, 0, 0.95) 0%,
    rgba(0, 0, 0, 0.8) 20%,
    rgba(0, 0, 0, 0.5) 40%,
    rgba(0, 0, 0, 0.1) 70%,
    transparent 100%
  );
}

.banner-item:hover .banner-image {
  transform: scale(1.05);
}

.banner-info {
  position: relative;
  z-index: 2;
  padding: 30px;
  color: white;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.banner-title {
  margin: 0 0 15px;
  font-size: 28px;
  font-weight: bold;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  max-width: 80%;
}

.banner-desc {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 20px;
}

.rating-container,
.box-office-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating {
  color: #ffb400;
  font-weight: bold;
  font-size: 18px;
}

.box-office {
  color: #fff;
  font-size: 16px;
}

.banner-button {
  margin-top: 5px;
  font-size: 16px;
  padding: 12px 25px;
  background-color: #ff5c38;
  border-color: #ff5c38;
  transition: all 0.3s;
}

.banner-button:hover {
  background-color: #ff7d63;
  border-color: #ff7d63;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(255, 92, 56, 0.3);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .banner-section {
    margin: 15px 0 30px;
  }
  
  .custom-carousel :deep(.el-carousel__arrow) {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
  
  .banner-title {
    font-size: 22px;
    margin-bottom: 10px;
  }
  
  .rating {
    font-size: 16px;
  }
  
  .box-office {
    font-size: 14px;
  }
  
  .banner-button {
    padding: 10px 20px;
    font-size: 14px;
  }
}

@media (max-width: 576px) {
  .banner-section {
    margin: 10px 0 20px;
    padding: 0 10px;
  }
  
  .custom-carousel :deep(.el-carousel__arrow) {
    width: 36px;
    height: 36px;
    font-size: 16px;
  }
  
  .banner-info {
    padding: 20px;
  }
  
  .banner-title {
    font-size: 18px;
    margin-bottom: 8px;
  }
  
  .banner-desc {
    margin-bottom: 15px;
    gap: 10px;
  }
  
  .rating, .box-office {
    font-size: 14px;
  }
  
  .banner-button {
    padding: 8px 16px;
    font-size: 12px;
  }
}

/* 主内容区域 */
.main-container {
  width: 1200px;
  max-width: 100%;
  padding: 0 20px;
  margin: 0 auto;
}

/* 电影区域通用样式 */
.movie-section {
  margin-bottom: 50px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 26px;
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

.view-more {
  display: flex;
  align-items: center;
  color: #666;
  font-size: 16px;
  text-decoration: none;
  transition: color 0.2s ease;
}

.view-more:hover {
  color: #ff5c38;
}

.view-more .el-icon {
  margin-left: 4px;
  font-size: 14px;
}

/* 电影列表 */
.movie-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 30px;
}

.movie-card {
  transition: transform 0.3s ease;
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
}

/* 响应式 */
@media (max-width: 1200px) {
  .banner-content {
    width: 90%;
  }

  .movie-list {
    grid-template-columns: repeat(5, 1fr);
    gap: 20px;
  }

  .section-title {
    font-size: 24px;
  }
}

@media (max-width: 992px) {
  .banner-poster {
    width: 200px;
    height: 280px;
    margin-right: 30px;
  }

  .banner-title {
    font-size: 30px;
  }

  .banner-desc {
    font-size: 16px;
  }

  .movie-list {
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .banner-content {
    align-items: flex-end;
    padding-bottom: 30px;
  }

  .banner-poster {
    width: 150px;
    height: 210px;
    margin-right: 20px;
  }

  .banner-title {
    font-size: 24px;
    margin-bottom: 10px;
  }

  .rating-score {
    font-size: 24px;
  }

  .banner-desc {
    font-size: 14px;
    margin-bottom: 15px;
  }

  .banner-btn {
    height: 36px;
    font-size: 14px;
    padding: 0 20px;
  }

  .movie-list {
    grid-template-columns: repeat(3, 1fr);
    gap: 15px;
  }

  .section-title {
    font-size: 22px;
  }
}

@media (max-width: 576px) {
  .banner-content {
    flex-direction: column;
    align-items: center;
    justify-content: flex-end;
    padding-bottom: 20px;
  }

  .banner-poster {
    width: 120px;
    height: 168px;
    margin-right: 0;
    margin-bottom: 15px;
  }

  .banner-title {
    font-size: 20px;
    text-align: center;
    margin-bottom: 5px;
  }

  .banner-rating {
    justify-content: center;
    margin-bottom: 10px;
  }

  .banner-desc {
    text-align: center;
    margin-bottom: 10px;
  }

  .movie-list {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }

  .movie-name {
    font-size: 14px;
  }

  .buy-btn,
  .want-btn {
    font-size: 14px;
  }

  .section-title {
    font-size: 20px;
  }
}
</style>