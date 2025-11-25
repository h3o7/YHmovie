<template>
    <div class="movie-showtimes-page">
        <!-- 电影海报滚动区域 -->
        <div class="movie-posters-section">
            <div class="posters-container">
                <div class="poster-scroll">
                    <div v-for="movie in sortedMovies" :key="movie.moviesId" class="poster-item" :class="{
                        active: movie.moviesId === currentMovieId,
                        'is-current': movie.moviesId === currentMovieId
                    }" @click="switchMovie(movie.moviesId)">
                        <div class="poster-image">
                            <img :src="movie.posterUrl" :alt="movie.movieName" />
                            <div class="rating-badge">{{ movie.rating }}</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 电影详细信息 -->
        <div class="movie-details-section">
            <div class="movie-info-container">
                <div class="movie-main-info">
                    <h1 class="movie-title">{{ movieInfo.movieName }}</h1>
                    <p class="movie-title-en">{{ movieInfo.movieNameEn }}</p>
                    <div class="movie-rating">
                        <span class="rating-label">暂无评分</span>
                    </div>
                </div>

                <div class="movie-meta">
                    <div class="meta-item">
                        <span class="meta-label">时长：</span>
                        <span class="meta-value">{{ movieInfo.duration }}分钟</span>
                    </div>
                    <div class="meta-item">
                        <span class="meta-label">类型：</span>
                        <span class="meta-value">{{ movieInfo.typeNames.join('') }}</span>
                    </div>
                    <div class="meta-item">
                        <span class="meta-label">主演：</span>
                        <span class="meta-value">{{ movieInfo.director }}</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- 观影时间选择 -->
        <div class="date-selection-section">
            <div class="date-selector">
                <span class="date-label">观影时间：</span>
                <div class="date-buttons">
                    <el-button v-for="date in availableDates" :key="date.value"
                        :type="selectedDate === date.value ? 'primary' : 'default'"
                        :class="{ 'date-active': selectedDate === date.value }" @click="selectDate(date.value)"
                        class="date-btn">
                        {{ date.label }}
                    </el-button>
                </div>
            </div>
        </div>

        <!-- 放映时间表 -->
        <div class="showtimes-section">
            <div class="showtimes-header">
                <div class="header-item">放映时间</div>
                <div class="header-item">语言版本</div>
                <div class="header-item">放映厅</div>
                <div class="header-item">售价（元）</div>
                <div class="header-item">选座购票</div>
            </div>

            <div class="showtimes-list">
                <div v-for="showtime in filteredShowtimes" :key="showtime.showtimesId" class="showtime-item">
                    <div class="time-info">
                        <div class="start-time">{{ formatTime(showtime.startTime) }}</div>
                        <div class="end-time">{{ calculateEndTime(showtime.startTime, movieInfo.duration) }}散场</div>
                    </div>
                    <div class="language-info">
                        <span class="language">国语2D</span>
                    </div>
                    <div class="hall-info">
                        <span class="hall-name">{{ showtime.hallNumber }}号激光巨幕大厅</span>
                    </div>
                    <div class="price-info">
                        <span class="price">¥{{ showtime.ticketPrice }}</span>
                    </div>
                    <div class="booking-info">
                        <el-button class="booking-btn" @click="goToSeatSelection(showtime)">
                            选座购票
                        </el-button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMovieById, getMovies } from '@/api/movies'
import { getShowtimes } from '@/api/showtimes'

const route = useRoute()
const router = useRouter()

const currentMovieId = ref(route.params.moviesId)
const cinemasId = ref(route.params.cinemasId)
const selectedDate = ref('')
const allMovies = ref([])
const movieInfo = ref({
    moviesId: '',
    movieName: '',
    movieNameEn: '',
    director: '',
    movieBeginTime: '',
    duration: 0,
    rating: 0,
    posterUrl: '',
    country: '',
    boxOffice: 0,
    typeNames: [],
})
const showtimes = ref([])

// 排序电影列表，将当前电影放在第一位
const sortedMovies = computed(() => {
    if (!allMovies.value.length) return []

    const currentMovie = allMovies.value.find(movie => movie.moviesId === currentMovieId.value)
    const otherMovies = allMovies.value.filter(movie => movie.moviesId !== currentMovieId.value)

    return currentMovie ? [currentMovie, ...otherMovies] : allMovies.value
})

// 计算可用日期
const availableDates = computed(() => {
    const today = new Date()
    const dates = []

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

// 过滤当前日期的放映场次
const filteredShowtimes = computed(() => {
    if (!selectedDate.value) return []

    return showtimes.value
        .filter(showtime => showtime.showDate === selectedDate.value)
        .sort((a, b) => a.startTime.localeCompare(b.startTime))
})

// 格式化时间
const formatTime = (timeString) => {
    return timeString.substring(0, 5)
}

// 计算散场时间
const calculateEndTime = (startTime, duration) => {
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
const switchMovie = async (moviesId) => {
    if (moviesId === currentMovieId.value) return

    currentMovieId.value = moviesId

    // 更新URL
    router.replace(`/users/showtimes/${cinemasId.value}/${moviesId}`)

    // 重新获取电影详情和放映时间表
    await Promise.all([
        fetchMovieDetails(),
        fetchShowtimes()
    ])
}

// 跳转到选座页面
const goToSeatSelection = (showtime) => {
    router.push(`/users/seats/${showtime.showtimesId}/${cinemasId.value}/${currentMovieId.value}`)
}

// 获取所有电影信息
const fetchAllMovies = async () => {
    try {
        const response = await getMovies()
        allMovies.value = response.data || []
    } catch (error) {
        console.error('获取电影列表失败:', error)
    }
}

// 获取电影详情
const fetchMovieDetails = async () => {
    try {
        const response = await getMovieById(currentMovieId.value)
        movieInfo.value = response.data || {}
    } catch (error) {
        console.error('获取电影详情失败:', error)
    }
}

// 获取放映时间表
const fetchShowtimes = async () => {
    try {
        const response = await getShowtimes(currentMovieId.value, cinemasId.value)
        showtimes.value = response.data || []
    } catch (error) {
        console.error('获取放映时间表失败:', error)
    }
}

onMounted(async () => {
    selectedDate.value = availableDates.value[0]?.value || ''
    await Promise.all([
        fetchAllMovies(),
        fetchMovieDetails(),
        fetchShowtimes()
    ])
})
</script>

<style scoped>
.movie-showtimes-page {
    background: #f5f5f5;
    min-height: 100vh;
}

/* 电影海报区域 */
.movie-posters-section {
    background: #fff;
    padding: 20px 0;
    /* 增加底部空间，防止放大遮挡 */
    border-bottom: 1px solid #e8e8e8;
    position: relative;
    /* 添加相对定位 */
    overflow: visible;
    /* 确保溢出内容可见 */
}

.posters-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 40px;
}

.poster-scroll {
    display: flex;
    gap: 20px;
    overflow-x: auto;
    padding: 20px 0 30px;
    align-items: flex-start;
}

.poster-item {
    flex-shrink: 0;
    cursor: pointer;
    transition: all 0.3s ease;
    transform-origin: center top;
    margin-bottom: 15px;
}

.poster-item:hover {
    transform: translateY(-5px);
}

.poster-item.is-current {
    transform: scale(1.2);
    z-index: 10;
    margin: 0 10px;
}

.poster-item.is-current:hover {
    transform: scale(1.2) translateY(-5px);
}

.poster-image {
    position: relative;
    width: 150px;
    height: 200px;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    border: 2px solid transparent;
    transition: all 0.3s ease;
}

.poster-item.is-current .poster-image {
    border-color: #ff7f00;
    box-shadow: 0 8px 25px rgba(255, 127, 0, 0.3);
}

.poster-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.rating-badge {
    position: absolute;
    bottom: 8px;
    right: 8px;
    background: rgba(0, 0, 0, 0.7);
    color: #ffd700;
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
}

.movie-title {
    font-size: 28px;
    font-weight: bold;
    color: #333;
    margin: 0 0 8px 0;
}

.movie-title-en {
    font-size: 16px;
    color: #666;
    margin: 0 0 15px 0;
}

.movie-rating .rating-label {
    background: linear-gradient(135deg, #1a3c8f 0%, #ff7f00 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    font-weight: bold;
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
    border-color: #ff7f00;
    color: #ff7f00;
}

.date-btn.date-active {
    background: linear-gradient(135deg, #1a3c8f 0%, #ff7f00 100%);
    border-color: transparent;
    color: #fff;
}

/* 放映时间表区域 */
.showtimes-section {
    background: #fff;
    padding: 30px 0;
}

.showtimes-header,
.showtimes-list {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
}

.showtimes-header {
    display: grid;
    grid-template-columns: 1.5fr 1fr 1.5fr 1fr 1.2fr;
    gap: 20px;
    padding: 15px 0;
    border-bottom: 1px solid #e8e8e8;
    margin-bottom: 20px;
}

.header-item {
    font-weight: 500;
    color: #666;
    text-align: center;
}

.showtime-item {
    display: grid;
    grid-template-columns: 1.5fr 1fr 1.5fr 1fr 1.2fr;
    gap: 20px;
    align-items: center;
    padding: 20px 0;
    border-bottom: 1px solid #f0f0f0;
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
    color: #e74c3c;
}

.booking-info {
    text-align: center;
}

.booking-btn {
    background: linear-gradient(135deg, #1a3c8f 0%, #ff7f00 100%);
    border: none;
    color: #fff;
    padding: 10px 25px;
    border-radius: 20px;
    font-weight: 500;
    transition: all 0.3s ease;
}

.booking-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(255, 127, 0, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
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

    .showtime-item>div {
        padding: 10px 0;
        border-bottom: 1px solid #f9f9f9;
    }

    .poster-item.is-current {
        transform: scale(1.1);
        margin: 0 5px;
    }
}
</style>