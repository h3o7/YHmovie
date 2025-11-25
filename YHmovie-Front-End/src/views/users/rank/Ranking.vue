<template>
  <div class="movie-ranking-container">
    <div class="tabs-container">
      <div class="tab-wrapper">
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'boxOffice' }"
          @click="switchTab('boxOffice')"
        >
          票房排行榜
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'rating' }"
          @click="switchTab('rating')"
        >
          评分排行榜
        </div>
      </div>
    </div>

    <div class="rankings-content">
      <div v-if="loading" class="loading">
        <div v-for="i in 10" :key="i" class="skeleton-item">
          <el-skeleton :rows="1" animated />
        </div>
      </div>
      
      <div v-else>
        <ul class="ranking-list">
          <li v-for="(movie, index) in currentRankings" :key="movie.movieId" class="movie-item" @click="goToMovieInfo(movie.movieId)">
            <div class="rank-number" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</div>
            <div class="movie-poster">
              <img :src="movie.moviePosterUrl" :alt="movie.movieName" />
            </div>
            <div class="movie-info">
              <h3 class="movie-name">{{ movie.movieName }}</h3>
              <div class="movie-metrics">
                <span v-if="activeTab === 'rating'" class="rating">
                  <i class="el-icon-star-on"></i>
                  {{ movie.movieRating.toFixed(1) }}
                </span>
                <span v-else class="box-office">
                  {{ formatBoxOffice(movie.movieBoxOffice) }}
                </span>
              </div>
            </div>
          </li>
        </ul>
        
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { boxOfficeRanking, ratingRanking } from '@/api/movies';

const router = useRouter();

const activeTab = ref('boxOffice');
const loading = ref(false);
const currentRankings = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 切换标签
const switchTab = (tab) => {
  activeTab.value = tab;
  currentPage.value = 1;
  fetchRankings();
};

// 格式化票房数据
const formatBoxOffice = (value) => {
  if (!value) return '0';
  
  const units = ['', '万', '亿', '万亿'];
  let unitIndex = 0;
  let formattedValue = value;
  
  while (formattedValue >= 10000 && unitIndex < units.length - 1) {
    formattedValue /= 10000;
    unitIndex++;
  }
  
  return formattedValue.toFixed(2) + units[unitIndex];
};

// 获取排行榜数据
const fetchRankings = async () => {
  loading.value = true;
  try {
    let response;
    
    if (activeTab.value === 'boxOffice') {
      response = await boxOfficeRanking(currentPage.value, pageSize.value);
    } else {
      response = await ratingRanking(currentPage.value, pageSize.value);
    }
    
    if (response.code === 200) {
      currentRankings.value = response.data.rows;
      total.value = response.data.total;
    }
  } catch (error) {
    console.error('获取排行榜数据失败:', error);
  } finally {
    loading.value = false;
  }
};

// 页码变化
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchRankings();
};

// 跳转到电影详情页
const goToMovieInfo = (movieId) => {
  router.push(`/users/movieInfo/${movieId}`);
};

// 监听标签变化
watch(activeTab, () => {
  fetchRankings();
});

onMounted(() => {
  fetchRankings();
});
</script>

<style scoped>
.movie-ranking-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.tabs-container {
  margin-bottom: 30px;
}

.tab-wrapper {
  display: flex;
  border-bottom: 1px solid #e5e5e5;
}

.tab-item {
  position: relative;
  padding: 12px 20px;
  font-size: 18px;
  cursor: pointer;
  color: #666;
  transition: all 0.3s ease;
}

.tab-item.active {
  color: #ff5f16;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #ff5f16;
}

.tab-item:hover {
  color: #ff5f16;
}

.rankings-content {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.loading {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.skeleton-item {
  padding: 10px;
}

.ranking-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.movie-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: all 0.3s ease;
}

.movie-item:hover {
  background-color: #f9f9f9;
  transform: translateY(-2px);
}

.movie-item:last-child {
  border-bottom: none;
}

.rank-number {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  color: #999;
  margin-right: 20px;
}

.rank-number.top-three {
  color: #ff5f16;
  font-size: 22px;
}

.movie-poster {
  width: 70px;
  height: 100px;
  overflow: hidden;
  border-radius: 4px;
  margin-right: 20px;
}

.movie-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.movie-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.movie-name {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.movie-metrics {
  display: flex;
  align-items: center;
}

.rating {
  display: flex;
  align-items: center;
  color: #ffb400;
  font-weight: bold;
}

.rating i {
  margin-right: 5px;
}

.box-office {
  color: #ff5f16;
  font-weight: bold;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: #ff5f16;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled):hover) {
  color: #ff5f16;
}

@media (max-width: 768px) {
  .movie-item {
    padding: 10px 0;
  }
  
  .rank-number {
    width: 30px;
    height: 30px;
    font-size: 16px;
    margin-right: 10px;
  }
  
  .movie-poster {
    width: 50px;
    height: 75px;
    margin-right: 15px;
  }
  
  .movie-name {
    font-size: 16px;
  }
}
</style>