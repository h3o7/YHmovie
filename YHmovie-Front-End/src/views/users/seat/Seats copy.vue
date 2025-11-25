<template>
    <!-- {{showtimeInfo}} -->
    <div class="seat-selection-page">
      <!-- 电影信息头部 -->
      <div class="movie-info-section">
        <div class="movie-info-container">
          <div class="movie-poster">
            <img :src="movieInfo.posterUrl" alt="电影海报" />
          </div>
          <div class="movie-details">
            <h1 class="movie-title">{{ movieInfo.movieName }}</h1>
            <p class="movie-title-en">{{ movieInfo.movieNameEn }}</p>
            <div class="movie-meta">
              <div class="meta-item">
                <span class="meta-label">类型：</span>
                <span class="meta-value">{{ movieInfo.typeNames.join(' ') }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">时长：</span>
                <span class="meta-value">{{ movieInfo.duration }}分钟</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">影院：</span>
                <span class="meta-value">CGV影城 (U城IMAX店)</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">影厅：</span>
                <span class="meta-value">激光{{ showtimeInfo.hallNumber }}厅</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">版本：</span>
                <span class="meta-value">中文2D</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">场次：</span>
                <span class="meta-value">{{ formatShowDate(showtimeInfo.showDate) }} {{ formatTime(showtimeInfo.startTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
  
      <!-- 座位图图例说明 -->
      <div class="seat-legend-section">
        <div class="legend-container">
          <div class="legend-item">
            <div class="seat-icon available"></div>
            <span>可选座位</span>
          </div>
          <div class="legend-item">
            <div class="seat-icon sold"></div>
            <span>已售座位</span>
          </div>
          <div class="legend-item">
            <div class="seat-icon selected"></div>
            <span>已选座位</span>
          </div>
          
        </div>
      </div>
      
      <!-- 座位选择区 -->
      <div class="seat-selection-section">
        <!-- 银幕 -->
        <div class="screen-container">
          <div class="screen">
            <span>银幕中央</span>
          </div>
        </div>
  
        <!-- 座位区域 -->
        <div class="seats-container">
          <div class="row-labels">
            <div 
              v-for="row in totalRows" 
              :key="row" 
              class="row-label"
            >
              <!-- {{ row }} -->
            </div>
          </div>
          
          <div class="seats-grid">
            <div 
              v-for="row in totalRows" 
              :key="'row-' + row" 
              class="seat-row"
            >
              <div 
                v-for="col in totalCols" 
                :key="'seat-' + row + '-' + col" 
                class="seat-cell"
              >
                <!-- 判断是否为走廊位置 -->
                <template v-if="!isAisle(row, col)">
                  <div 
                    class="seat" 
                    :class="{
                      'sold': isSold(row, col),
                      'selected': isSelected(row, col),
                      'available': !isSold(row, col) && !isSelected(row, col)
                    }"
                    @click="toggleSeat(row, col)"
                  >
                    {{ col }}
                  </div>
                </template>
                <template v-else>
                  <!-- 走廊空位 -->
                  <div class="aisle"></div>
                </template>
              </div>
            </div>
          </div>
        </div>
      </div>
  
      <!-- 选座信息和确认 -->
      <div class="seat-confirmation-section">
        <div class="confirmation-container">
          <div class="selected-info">
            <div class="user-info">
              <span class="info-label">手机号：</span>
              <span class="info-value">{{ maskedPhone }}</span>
            </div>
            <div class="seat-info">
              <span class="info-label">座位：</span>
              <span class="info-value">
                <el-tag 
                  v-for="(seat, index) in selectedSeats" 
                  :key="index" 
                  class="seat-tag"
                >
                  {{ seat.row }}排{{ seat.col }}座
                </el-tag>
              </span>
            </div>
            <div class="price-info">
              <span class="info-label">总价：</span>
              <span class="info-price">¥{{ totalPrice }}</span>
            </div>
          </div>
          <div class="confirm-action">
            <el-button 
              type="primary" 
              class="confirm-button" 
              :disabled="selectedSeats.length === 0" 
              @click="confirmSelection"
            >
              确认选座
            </el-button>
          </div>
        </div>
      </div>
  
      <!-- 确认对话框 -->
      <el-dialog
        v-model="dialogVisible"
        title="确认选座"
        width="30%"
        center
      >
        <span>您已选择了 {{ selectedSeats.length }} 个座位，总价为 ¥{{ totalPrice }}，确认提交吗？</span>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitSeats">确认</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, computed, onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import { getUser } from '@/api/users'
  import { getSeats, getShowtimeSeats, addShowtime,getShowtime } from '@/api/showtimes'
  import { getMovieById } from '@/api/movies'
  
  const route = useRoute()
  const router = useRouter()
  
  // 获取路由参数
  const showtimesId = ref(route.params.showtimesId)
  const cinemasId = ref(route.params.cinemasId)
  const moviesId = ref(route.params.moviesId)
  
  // 对话框控制
  const dialogVisible = ref(false)
  
  // 电影信息
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
  
  // 场次信息
  const showtimeInfo = ref({
    showtimesId: '',
    moviesId: '',
    cinemasId: '',
    hallNumber: 0,
    showDate: '',
    startTime: '',
    ticketPrice: 0,
  })
  
  // 用户信息
  const userInfo = ref({
    usersId: '',
    phone: '',
  })
  
  // 座位信息
  const allSeats = ref([])
  const soldSeats = ref([])
  const selectedSeats = ref([])
  
  // 座位布局配置
  const totalRows = ref(10)
  const totalCols = ref(15)
  
  // 从本地存储获取用户ID
  const getUserIdFromLocalStorage = () => {
    const loginData = localStorage.getItem('login')
        if (loginData) {
          const login = JSON.parse(loginData)
          if (login && login.id) {
            // 如果后端期望 token 字段，保留这行
            return login.id
          }
        }
  }
  
  // 获取用户信息
  const fetchUserInfo = async () => {
    const userId = getUserIdFromLocalStorage()
    if (!userId) {
      ElMessage.warning('请先登录')
      return
    }
    
    try {
      const response = await getUser(userId)
      userInfo.value = response.data
    } catch (error) {
      console.error('获取用户信息失败:', error)
      ElMessage.error('获取用户信息失败')
    }
  }
  
  // 获取电影信息
  const fetchMovieInfo = async () => {
    try {
      const response = await getMovieById(moviesId.value)
      movieInfo.value = response.data
    } catch (error) {
      console.error('获取电影信息失败:', error)
      ElMessage.error('获取电影信息失败')
    }
  }
  
  // 获取场次信息（假设在路由跳转时已传递场次信息，或在此调用接口获取）
  const fetchShowtimeInfo = async () => {
    // 实际开发时，应调用API获取场次详情
    // 这里简单模拟场次信息
    const response = await getShowtime(showtimesId.value)
    showtimeInfo.value = response.data

  }
  
  // 获取所有座位
  const fetchSeats = async () => {
    try {
    
      const response = await getSeats(showtimeInfo.value.hallNumber)
      allSeats.value = response.data || []
      
      // 根据获取的座位数确定影厅大小
      if (allSeats.value.length === 49) { // 7x7
        totalRows.value = 7
        totalCols.value = 7
      } else if (allSeats.value.length === 30) { // 5x6
        totalRows.value = 5
        totalCols.value = 6
      }
    } catch (error) {
      console.error('获取座位信息失败:', error)
      ElMessage.error('获取座位信息失败')
    }
  }
  
  // 获取已售座位
  const fetchSoldSeats = async () => {
    try {
      const response = await getShowtimeSeats(showtimesId.value)
      soldSeats.value = response.data
    } catch (error) {
      console.error('获取已售座位信息失败:', error)
      ElMessage.error('获取已售座位信息失败')
    }
  }
  
  // 格式化日期
  const formatShowDate = (dateString) => {
    if (!dateString) return ''
    
    const date = new Date(dateString)
    const today = new Date()
    
    if (date.toDateString() === today.toDateString()) {
      return `今天 ${date.getMonth() + 1}月${date.getDate()}日`
    } else if (date.toDateString() === new Date(today.getTime() + 86400000).toDateString()) {
      return `明天 ${date.getMonth() + 1}月${date.getDate()}日`
    } else {
      const days = ['日', '一', '二', '三', '四', '五', '六']
      return `周${days[date.getDay()]} ${date.getMonth() + 1}月${date.getDate()}日`
    }
  }
  
  // 格式化时间
  const formatTime = (timeString) => {
    if (!timeString) return ''
    return timeString.substring(0, 5)
  }
  
  // 生成掩码电话号码
  const maskedPhone = computed(() => {
    const phone = userInfo.value.phone || ''
    if (phone.length >= 11) {
      return phone.substring(0, 3) + '****' + phone.substring(7)
    }
    return phone
  })
  
  // 计算总价
  const totalPrice = computed(() => {
    return selectedSeats.value.length * showtimeInfo.value.ticketPrice
  })
  
  // 判断是否为走廊位置
  const isAisle = (row, col) => {
    // 定义走廊位置 (在图中，中间有一条过道，两侧各有座位)
    // 适配图片中的布局，第5列和第11列为走廊
    return col === 5 || col === 11
  }
  
  // 判断座位是否已售
  // ...existing code...
// 判断座位是否已售
const isSold = (row, col) => {
  if (!soldSeats.value || soldSeats.value.length === 0) return false;
  
  // 根据allSeats中的数据，找到对应的seatId
  const matchingSeat = allSeats.value.find(seat => 
    seat.seatRow === row && seat.seatCol === col
  );
  
  // 如果找到了对应的座位，检查它的seatsId是否在soldSeats中
  if (matchingSeat && matchingSeat.seatsId) {
    return soldSeats.value.some(soldSeat => 
      soldSeat.seatsId === matchingSeat.seatsId
    );
  }
  
  // 另一种可能：尝试解析S开头的编号格式
  // 假设S后面的数字前两位是行，后两位是列
  // 例如：S0102 表示 第1行第2列
  return soldSeats.value.some(soldSeat => {
    if (!soldSeat.seatsId || !soldSeat.seatsId.startsWith('S')) return false;
    
    // 尝试用正则匹配行列号
    // 例如从"S011"提取行列号
    const seatCode = soldSeat.seatsId.substring(1); // 去掉S前缀
    
    if (seatCode.length <= 2) {
      // 如果只有两位数，假设是按顺序编号的
      console.log(`座位编码 ${soldSeat.seatsId} 需要特殊处理`);
      return false;
    } else {
      // 假设格式是：前1-2位是行号，后1-2位是列号
      const extractedRow = parseInt(seatCode.slice(0, -2), 10);
      const extractedCol = parseInt(seatCode.slice(-2), 10);
      return extractedRow === row && extractedCol === col;
    }
  });
}
// ...existing code...
  
  // 判断座位是否已选
  const isSelected = (row, col) => {
    return selectedSeats.value.some(seat => 
      seat.row === row && seat.col === col
    )
  }
  
  // 选择或取消选择座位
  const toggleSeat = (row, col) => {
    // 如果座位已售，不能选择
    if (isSold(row, col)) return
    
    const seatIndex = selectedSeats.value.findIndex(
      seat => seat.row === row && seat.col === col
    )
    
    if (seatIndex > -1) {
      // 如果已选择，则取消选择
      selectedSeats.value.splice(seatIndex, 1)
    } else {
      // 否则添加到已选择列表
      const seat = allSeats.value.find(
        s => s.seatRow === row && s.seatCol === col
      ) || { seatsId: `${row}-${col}`, seatRow: row, seatCol: col }
      
      selectedSeats.value.push({
        seatsId: seat.seatsId,
        row: row,
        col: col
      })
    }
  }
  
  // 确认选座，显示对话框
  const confirmSelection = () => {
    if (selectedSeats.value.length === 0) {
      ElMessage.warning('请至少选择一个座位')
      return
    }
    
    dialogVisible.value = true
  }
  
  // 提交选座
  const submitSeats = async () => {
    try {
      const showtimeFormList = selectedSeats.value.map(seat => ({
        showtimesId: showtimesId.value,
        seatsId: seat.seatsId,
        usersId: userInfo.value.usersId
      }))
      
      await addShowtime(showtimeFormList)
      
      ElMessage.success('选座成功')
      dialogVisible.value = false
      selectedSeats.value = []
      await fetchSoldSeats()
      // 跳转到订单页或其他页面
      // router.push('/path/to/order/page')
    } catch (error) {
      console.error('提交选座失败:', error)
      ElMessage.error('提交选座失败')
    }
  }
  
  // 页面加载时获取数据
  onMounted(async () => {
    try {
      await Promise.all([
        fetchUserInfo(),
        fetchMovieInfo(),
        fetchShowtimeInfo()
      ])
      
      await fetchSeats()
      await fetchSoldSeats()
    } catch (error) {
      console.error('初始化数据失败:', error)
      ElMessage.error('初始化数据失败')
    }
  })
</script>
  
<style scoped>
  .seat-selection-page {
    background: #f5f5f5;
    min-height: 100vh;
    padding-bottom: 40px;
  }
  
  /* 电影信息区域 */
  .movie-info-section {
    background: #fff;
    padding: 20px 0;
    border-bottom: 1px solid #e8e8e8;
  }
  
  .movie-info-container {
    display: flex;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    gap: 20px;
  }
  
  .movie-poster {
    flex-shrink: 0;
    width: 120px;
    height: 160px;
    border-radius: 6px;
    overflow: hidden;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  }
  
  .movie-poster img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .movie-details {
    flex-grow: 1;
  }
  
  .movie-title {
    font-size: 24px;
    font-weight: bold;
    color: #333;
    margin: 0 0 5px 0;
  }
  
  .movie-title-en {
    font-size: 14px;
    color: #666;
    margin: 0 0 15px 0;
  }
  
  .movie-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 15px 30px;
  }
  
  .meta-item {
    display: flex;
    align-items: center;
  }
  
  .meta-label {
    color: #666;
    margin-right: 5px;
  }
  
  .meta-value {
    color: #333;
    font-weight: 500;
  }
  
  /* 座位图例说明区域 */
  .seat-legend-section {
    background: #fff;
    padding: 15px 0;
    margin: 15px 0;
  }
  
  .legend-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    justify-content: center;
    gap: 40px;
    flex-wrap: wrap;
  }
  
  .legend-item {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .seat-icon {
    width: 20px;
    height: 20px;
    border-radius: 3px;
  }
  
  .seat-icon.available {
    border: 1px solid #ccc;
    background: #fff;
  }
  
  .seat-icon.sold {
    background: #d6a400;
  }
  
  .seat-icon.selected {
    background: #19ada7;
  }
  
  .seat-icon.couple {
    border: 1px solid #ccc;
    background: #fff;
    position: relative;
  }
  
  .seat-icon.couple::after {
    content: '❤';
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    font-size: 10px;
    color: #e91e63;
  }
  
  /* 座位选择区域 */
  .seat-selection-section {
    background: #fff;
    padding: 20px 0 40px;
    margin-bottom: 15px;
  }
  
  .screen-container {
    max-width: 1000px;
    margin: 0 auto 30px;
    padding: 0 20px;
  }
  
  .screen {
    background: #f0f0f0;
    padding: 10px;
    text-align: center;
    border-radius: 5px;
    color: #666;
    position: relative;
  }
  
  .screen::after {
    content: '';
    position: absolute;
    bottom: -15px;
    left: 0;
    right: 0;
    height: 15px;
    background: linear-gradient(to bottom, rgba(0, 0, 0, 0.05), transparent);
  }
  
  .seats-container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    align-items: flex-start;
  }
  
  .row-labels {
    display: flex;
    flex-direction: column;
    margin-right: 15px;
  }
  
  .row-label {
    height: 40px;
    width: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #666;
    font-weight: 500;
  }
  
  .seats-grid {
    flex-grow: 1;
  }
  
  .seat-row {
    display: flex;
    margin-bottom: 10px;
  }
  
  .seat-cell {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 5px;
  }
  
  .seat {
    width: 35px;
    height: 35px;
    border-radius: 5px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s ease;
  }
  
  .seat.available {
    border: 1px solid #ccc;
    background: #fff;
  }
  
  .seat.available:hover {
    border-color: #19ada7;
    transform: scale(1.05);
  }
  
  .seat.sold {
    background: #d6a400;
    color: #fff;
    cursor: not-allowed;
  }
  
  .seat.selected {
    background: #19ada7;
    color: #fff;
  }
  
  .aisle {
    width: 35px;
    height: 35px;
  }
  
  /* 选座信息和确认区域 */
  .seat-confirmation-section {
    background: #fff;
    padding: 20px 0;
    position: sticky;
    bottom: 0;
    box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  }
  
  .confirmation-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .selected-info {
    display: flex;
    flex-wrap: wrap;
    gap: 20px 40px;
  }
  
  .user-info, .seat-info, .price-info {
    display: flex;
    align-items: center;
  }
  
  .info-label {
    color: #666;
    margin-right: 10px;
  }
  
  .info-value {
    display: flex;
    gap: 5px;
    flex-wrap: wrap;
  }
  
  .seat-tag {
    margin-right: 5px;
  }
  
  .info-price {
    font-size: 20px;
    font-weight: bold;
    color: #e54d42;
  }
  
  .confirm-button {
    background: #e54d42;
    border-color: #e54d42;
    padding: 10px 30px;
    font-size: 16px;
  }
  
  .confirm-button:hover {
    background: #d43b30;
    border-color: #d43b30;
  }
  
  .confirm-button:disabled {
    background: #f8b9b5;
    border-color: #f8b9b5;
  }
  
  /* 响应式设计 */
  @media (max-width: 768px) {
    .movie-info-container {
      flex-direction: column;
      align-items: center;
      text-align: center;
    }
  
    .movie-meta {
      justify-content: center;
    }
  
    .confirmation-container {
      flex-direction: column;
      gap: 20px;
    }
  
    .selected-info {
      flex-direction: column;
      gap: 15px;
      width: 100%;
    }
  }
</style>