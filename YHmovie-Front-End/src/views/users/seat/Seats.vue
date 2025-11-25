<template>
  <div class="seats-container">
    <div v-if="loading" class="loading">
      <el-skeleton :rows="10" animated />
    </div>
    <div v-else class="content">
      <div class="seats-header">
        <h1>{{ showtimeInfo.movieName }}</h1>
        <div class="showtime-info">
          <span>{{ formatDate(showtimeInfo.showtimeShowDate) }}</span>
          <span>{{ formatTime(showtimeInfo.showtimeStartTime) }} - {{ formatTime(showtimeInfo.showtimeEndTime) }}</span>
          <span>{{ showtimeInfo.movieHallNumber }}号厅</span>
          <span>{{ showtimeInfo.movieLanguage }}</span>
        </div>
      </div>

      <div class="main-content">
        <div class="left-section">
          <div class="seats-layout">
            <div class="screen-container">
              <div class="screen">荧幕</div>
            </div>

            <div class="seats-legend">
              <div class="legend-item">
                <div class="seat available"></div>
                <span>可选</span>
              </div>
              <div class="legend-item">
                <div class="seat selected"></div>
                <span>已选</span>
              </div>
              <div class="legend-item">
                <div class="seat sold"></div>
                <span>已售</span>
              </div>
            </div>

            <div class="seats-map">
              <div class="row-indicators">
                <div v-for="row in rowCount" :key="`row-${row}`" class="row-number">{{ row }}</div>
              </div>
              <div class="seats-grid">
                <div v-for="row in seatingArrangement" :key="row.rowNum" class="seat-row">
                  <div v-for="seat in row.seats" :key="seat.cinemaHallSeatId" :class="[
                    'seat',
                    { 'available': !seat.isEmpty && !seat.soldStatus },
                    { 'sold': seat.soldStatus },
                    { 'selected': selectedSeats.includes(seat) },
                    { 'empty': seat.isEmpty }
                  ]" @click="toggleSeatSelection(seat)">
                    <span v-if="!seat.isEmpty">{{ seat.seatCol }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="order-summary">
          <div class="movie-info">
            <img :src="showtimeInfo.moviePosterUrl" alt="Movie Poster" class="movie-poster" />
            <div class="movie-details">
              <h3 class="movie-title">{{ showtimeInfo.movieName }}</h3>
              <div class="movie-meta-container">
                <div class="meta-item">
                  <i class="el-icon-time"></i>
                  <span>时长：{{ showtimeInfo.movieDuration }}分钟</span>
                </div>
                
                <div class="meta-item">
                  <i class="el-icon-film"></i>
                  <span>类型：{{ showtimeInfo.movieTypes?.join(' / ') || '暂无' }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="cinema-info">
            <div class="info-row">
              <span class="label">影院 : </span>
              <span class="value">{{ cinemaName }}</span>
            </div>
            <div class="info-row">
              <span class="label">影厅 : </span>
              <span class="value">{{ showtimeInfo.movieHallNumber }}号厅</span>
            </div>
            <div class="info-row">
              <span class="label">版本 : </span>
              <span class="value">{{ showtimeInfo.movieLanguage }}2D</span>
            </div>
            <div class="info-row">
              <span class="label">场次 : </span>
              <span class="value">今天 {{ formatMonthDay(showtimeInfo.showtimeShowDate) }} {{
                formatTime(showtimeInfo.showtimeStartTime) }}</span>
            </div>
            <div class="info-row">
              <span class="label">票价 : </span>
              <span class="value price-value">¥{{ showtimeInfo.showtimeTicketPrice }}/张</span>
            </div>
          </div>

          <div class="selected-seats">
            <h3>已选座位</h3>
            <div v-if="selectedSeats.length > 0" class="seats-list">
              <div v-for="(seat, index) in selectedSeats" :key="index" class="seat-tag">
                {{ seat.seatNumber }}
              </div>
            </div>
            <div v-else class="no-seats">
              请选择座位
            </div>
          </div>

          <div class="price-summary">
            <div class="price-row">
              <span>票价</span>
              <span>¥{{ showtimeInfo.showtimeTicketPrice }} × {{ selectedSeats.length }}</span>
            </div>
            <div class="price-row total">
              <span>总计</span>
              <span>¥{{ totalPrice }}</span>
            </div>
          </div>

          <div class="action-buttons">
            <el-button type="primary" :disabled="selectedSeats.length === 0" @click="confirmPurchase"
              class="purchase-btn">
              确认购票
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 确认购买对话框 -->
    <el-dialog v-model="purchaseDialogVisible" title="确认购票" width="30%" center>
      <div class="purchase-confirmation">
        <p>您已选择 {{ selectedSeats.length }} 个座位</p>
        <div class="seats-summary">
          <span v-for="(seat, index) in selectedSeats" :key="index" class="seat-tag">
            {{ seat.seatNumber }}
          </span>
        </div>
        <p class="total-price">总价: ¥{{ totalPrice }}</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="purchaseDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="buyTickets" :loading="purchaseLoading" class="purchase-btn">
            确认支付
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { showtimesInfo, seatsList } from '@/api/showtimes';
import { generateOrders } from '@/api/orders';

const route = useRoute();
const router = useRouter();

// 从路由参数获取信息
const movieId = ref(route.query.movieId);
const cinemaId = ref(route.query.cinemaId);
const movieHallId = ref(route.query.movieHallId);
const showtimeShowDate = ref(route.query.showtimeShowDate);
const showtimeStartTime = ref(route.query.showtimeStartTime);
const showtimesLeastPrice = ref(route.query.showtimesLeastPrice);

const loading = ref(true);
const seatsData = ref([]);
const showtimeInfo = ref({});
const selectedSeats = ref([]);
const purchaseDialogVisible = ref(false);
const purchaseLoading = ref(false);
const cinemaName = ref('ACTO梦空间影城（南坪步行街店）');

// 座位布局
const seatingArrangement = ref([]);
const rowCount = ref(0);
const colCount = ref(0);

// 获取座位信息
const fetchSeats = async () => {
  try {
    const showtimesDto = {
      movieId: movieId.value,
      cinemaId: cinemaId.value,
      movieHallId: movieHallId.value,
      showtimeShowDate: showtimeShowDate.value,
      showtimeStartTime: showtimeStartTime.value
    };

    const response = await seatsList(showtimesDto);
    if (response.code === 200) {
      seatsData.value = response.data;
      // 处理座位布局
      processSeatingArrangement();
    }
  } catch (error) {
    console.error('Error fetching seats:', error);
    ElMessage.error('获取座位信息失败');
  }
};

// 获取场次详情
const fetchShowtimeInfo = async () => {
  try {
    const showtimesDto = {
      movieId: movieId.value,
      cinemaId: cinemaId.value,
      movieHallId: movieHallId.value,
      showtimeShowDate: showtimeShowDate.value,
      showtimeStartTime: showtimeStartTime.value
    };

    const response = await showtimesInfo(showtimesDto);
    if (response.code === 200) {
      showtimeInfo.value = response.data;
      // 如果接口返回了影院名称，则更新
      if (response.data.cinemaName) {
        cinemaName.value = response.data.cinemaName;
      }
    }
  } catch (error) {
    console.error('Error fetching showtime info:', error);
    ElMessage.error('获取场次信息失败');
  } finally {
    loading.value = false;
  }
};

// 处理座位布局
const processSeatingArrangement = () => {
  // 解析座位号格式，如 "1排1座" => { row: 1, col: 1 }
  const seatMap = new Map();
  const rows = new Set();
  const cols = new Set();

  seatsData.value.forEach(seat => {
    const match = seat.seatNumber.match(/(\d+)排(\d+)座/);
    if (match) {
      const row = parseInt(match[1], 10);
      const col = parseInt(match[2], 10);

      rows.add(row);
      cols.add(col);

      // 添加更多信息到座位对象
      const enrichedSeat = {
        ...seat,
        seatRow: row,
        seatCol: col,
        isEmpty: false
      };

      const key = `${row}-${col}`;
      seatMap.set(key, enrichedSeat);
    }
  });

  // 获取行数和列数
  const maxRow = Math.max(...rows);
  const maxCol = Math.max(...cols);
  rowCount.value = maxRow;
  colCount.value = maxCol;

  // 创建座位排列
  const arrangement = [];
  for (let r = 1; r <= maxRow; r++) {
    const rowSeats = [];
    for (let c = 1; c <= maxCol; c++) {
      const key = `${r}-${c}`;
      if (seatMap.has(key)) {
        rowSeats.push(seatMap.get(key));
      } else {
        // 添加空座位以保持网格结构
        rowSeats.push({ isEmpty: true, seatRow: r, seatCol: c });
      }
    }
    arrangement.push({ rowNum: r, seats: rowSeats });
  }

  seatingArrangement.value = arrangement;
};

// 选择/取消选择座位
const toggleSeatSelection = (seat) => {
  if (seat.isEmpty || seat.soldStatus) return;

  const index = selectedSeats.value.findIndex(s => s.cinemaHallSeatId === seat.cinemaHallSeatId);

  if (index === -1) {
    selectedSeats.value.push(seat);
  } else {
    selectedSeats.value.splice(index, 1);
  }
};

// 计算总价
const totalPrice = computed(() => {
  if (selectedSeats.value.length === 0 || !showtimeInfo.value.showtimeTicketPrice) {
    return 0;
  }
  const price = selectedSeats.value.length * showtimeInfo.value.showtimeTicketPrice;
  return price.toFixed(2);
});

// 确认购买
const confirmPurchase = () => {
  if (selectedSeats.value.length === 0) {
    ElMessage.warning('请至少选择一个座位');
    return;
  }
  purchaseDialogVisible.value = true;
};

// 购买票
const buyTickets = async () => {
  try {
    purchaseLoading.value = true;
    const showtimeIds = selectedSeats.value.map(seat => seat.showtimeId);
    const response = await generateOrders(showtimeIds);

    if (response.code === 200) {
      ElMessage.success('购票成功！');
      purchaseDialogVisible.value = false;
      // 可以跳转到订单页或其他页面
      // 刷新该页面
      window.location.reload();
      // router.push('/users/orders');
    } else {
      ElMessage.error(response.msg || '购票失败');
    }
  } catch (error) {
    console.error('Error buying tickets:', error);
    ElMessage.error('购票失败，请稍后重试');
  } finally {
    purchaseLoading.value = false;
  }
};

// 格式化日期为"YYYY-MM-DD"
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  return dateStr;
};

// 格式化月日为"MM月DD"
const formatMonthDay = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getMonth() + 1}月${date.getDate()}`;
};

// 格式化时间为"HH:MM"
const formatTime = (timeStr) => {
  if (!timeStr) return '';
  const parts = timeStr.split(':');
  return `${parts[0]}:${parts[1]}`;
};

onMounted(() => {
  Promise.all([fetchSeats(), fetchShowtimeInfo()]);
});
</script>

<style scoped>
.seats-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  color: #333;
  background-color: #f5f5f5;
}

.loading {
  padding: 40px;
}

.content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.seats-header {
  background-color: #fff;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.seats-header h1 {
  margin: 0 0 10px;
  font-size: 22px;
  color: #333;
  font-weight: 600;
}

.showtime-info {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  color: #666;
}

.main-content {
  display: flex;
  gap: 20px;
}

.left-section {
  flex: 3;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.seats-layout {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.screen-container {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.screen {
  width: 80%;
  height: 30px;
  background: linear-gradient(to bottom, #ccc, #eee);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 5px;
  color: #666;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  position: relative;
}

.screen::after {
  content: '';
  position: absolute;
  bottom: -10px;
  width: 100%;
  height: 10px;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.1), transparent);
}

.seats-legend {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-bottom: 25px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.legend-item .seat {
  width: 20px;
  height: 20px;
  cursor: default;
}

.seats-map {
  display: flex;
  justify-content: center;
  padding: 0 20px;
}

.row-indicators {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  padding-right: 12px;
}

.row-number {
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  font-size: 14px;
  font-weight: bold;
}

.seats-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.seat-row {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.seat {
  width: 30px;
  height: 30px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 12px;
  color: #333;
  transition: all 0.2s ease;
  border: 1px solid #ddd;
}

/* 将可选座位改为白色底配灰色边框 */
.seat.available {
  background-color: #ffffff;
  border: 1px solid #ccc;
  color: #333;
}

.seat.selected {
  background-color: #ff5f16;
  border: 2px solid #ff5f16;
  color: #fff;
}

.seat.sold {
  background-color: #999;
  border-color: #999;
  cursor: not-allowed;
  color: #fff;
}

.seat.empty {
  background-color: transparent;
  border-color: transparent;
  cursor: default;
}

.seat:not(.empty):not(.sold):hover {
  transform: scale(1.1);
  border-color: #ff5f16;
  border-width: 2px;
}

.order-summary {
  flex: 2;
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.movie-info {
  display: flex;
  gap: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.movie-poster {
  width: 90px;
  height: 120px;
  object-fit: cover;
  border-radius: 4px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.movie-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.movie-details h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.movie-meta {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.cinema-info {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.info-row {
  display: flex;
  margin-bottom: 10px;
  font-size: 14px;
  line-height: 1.6;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-row .label {
  color: #666;
  min-width: 50px;
  font-weight: 500;
}

.info-row .value {
  flex: 1;
  color: #333;
}

.price-value {
  color: #ff5f16;
  font-weight: 500;
}

.selected-seats {
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.selected-seats h3 {
  margin: 0 0 12px;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.seats-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.seat-tag {
  padding: 5px 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
  font-size: 14px;
  border: 1px solid #eee;
}

.no-seats {
  color: #999;
  font-size: 14px;
}

.price-summary {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.price-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #666;
}

.price-row.total {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-top: 5px;
}

.price-row.total span:last-child {
  color: #ff5f16;
  font-size: 20px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}

.purchase-btn {
  width: 100%;
  background-color: #ff5f16;
  border-color: #ff5f16;
  height: 42px;
  font-size: 16px;
}

.purchase-btn:hover:not(:disabled) {
  background-color: #ff7c3e;
  border-color: #ff7c3e;
}

.purchase-confirmation {
  text-align: center;
}

.seats-summary {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
  margin: 15px 0;
}

.total-price {
  font-size: 18px;
  font-weight: bold;
  color: #ff5f16;
  margin-top: 15px;
}

/* Responsive adjustments */
@media (max-width: 992px) {
  .main-content {
    flex-direction: column;
  }

  .seat {
    width: 25px;
    height: 25px;
  }
}
</style>