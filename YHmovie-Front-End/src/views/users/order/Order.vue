<template>
  <div class="orders-container">
    <div class="page-header">

      <div class="filter-tabs">
        <!-- filepath: /Users/y/Desktop/java/projects/YHmovie/YHmovie-Front-End/src/views/users/order/Order.vue -->
        <div class="filter-tabs">
          <div class="tab-item" :class="{ active: activeTab === 'all' }" @click="setActiveTab('all')">
            全部订单
          </div>
          <div class="tab-item" :class="{ active: activeTab === 'upcoming' }" @click="setActiveTab('upcoming')">
            待观影
          </div>
          <div class="tab-item" :class="{ active: activeTab === 'completed' }" @click="setActiveTab('completed')">
            已观影
          </div>
        </div>
      </div>
    </div>

    <div class="orders-content">
      <!-- Loading state -->
      <div class="loading-container" v-if="loading">
        <el-icon class="loading-icon">
          <Loading />
        </el-icon>
        <span>正在加载订单...</span>
      </div>

      <!-- Empty state -->
      <div class="empty-container" v-else-if="filteredOrders.length === 0">
        <el-empty description="暂无订单记录" :image-size="120">
          <el-button type="primary" @click="goToMovies">去购票</el-button>
        </el-empty>
      </div>

      <!-- Orders list -->
      <div class="orders-list" v-else>
        <div class="order-item" v-for="order in orders" :key="order.orderId">
          <div class="order-header">
            <span class="order-number">订单号: {{ formatOrderId(order.orderId) }}</span>
            <span class="order-date">{{ formatOrderDate(order.createTime) }}</span>
          </div>

          <div class="order-content">
            <div class="movie-poster">
              <img :src="order.moviePosterUrl" :alt="order.movieName" />
            </div>

            <div class="order-info">
              <h3 class="movie-name">{{ order.movieName }}</h3>

              <div class="cinema-info">
                <div class="cinema-name">
                  <i class="iconfont icon-cinema"></i>
                  {{ order.cinemaName }}
                </div>
                <div class="hall-info">
                  <i class="iconfont icon-hall"></i>
                  {{ order.movieHallNumber }}号厅
                </div>
              </div>

              <div class="showtime-info">
                <i class="iconfont icon-time"></i>
                {{ formatShowDate(order.showtimeShowDate) }} {{ formatShowTime(order.showtimeStartTime) }}
              </div>

              <div class="seats-info">
                <i class="iconfont icon-seat"></i>
                <div class="seats-list">
                  <span class="seat-item" v-for="(seat, index) in order.seatNumbers" :key="index">
                    {{ seat }}
                  </span>
                </div>
              </div>

              <div class="price-info">
                <span class="price-label">总价：</span>
                <span class="price-value">¥{{ order.orderTotalPrice.toFixed(2) }}</span>
              </div>
            </div>

            <div class="order-actions">
              <div class="status-tag" :class="{ 'can-refund': order.orderStatus }">
                {{ order.orderStatus ? '待观影' : '已观影' }}
              </div>
              <el-button v-if="order.orderStatus" type="danger" size="small" @click="handleReturnTickets(order)"
                :loading="returningOrderId === order.orderId">
                退票
              </el-button>
              <div class="action-placeholder" v-else></div>
            </div>
          </div>
        </div>

        <!-- No more orders indicator -->
        <div class="no-more-orders" v-if="filteredOrders.length > 0">
          <el-divider>没有更多订单了</el-divider>
        </div>
      </div>
    </div>

    <!-- Confirmation dialog for ticket return -->
    <el-dialog v-model="returnDialogVisible" title="退票确认" width="350px" center align-center>
      <div class="return-dialog-content">
        <el-icon class="warning-icon">
          <Warning />
        </el-icon>
        <p>确定要退票吗？退票后将无法恢复。</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="returnDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmReturnTickets" :loading="confirmingReturn">
            确认退票
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted ,computed} from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, Warning } from '@element-plus/icons-vue'
import { ordersList, returnTickets } from '@/api/orders'

export default {
  name: 'Orders',
  components: {
    Loading,
    Warning
  },
  setup() {
    const router = useRouter()

    // Data
    const loading = ref(true)
    const orders = ref([])
    const activeTab = ref('all') // 默认选中"全部订单"
    const returnDialogVisible = ref(false)
    const selectedOrder = ref(null)
    const returningOrderId = ref(null)
    const confirmingReturn = ref(false)

    // 过滤订单的计算属性
    const filteredOrders = computed(() => {
      if (activeTab.value === 'all') {
        return orders.value
      } else if (activeTab.value === 'upcoming') {
        return orders.value.filter(order => order.orderStatus)
      } else if (activeTab.value === 'completed') {
        return orders.value.filter(order => !order.orderStatus)
      }
      return orders.value
    })
    
    // 添加选项卡切换函数
    const setActiveTab = (tab) => {
      activeTab.value = tab
    }
    // Methods
    const fetchOrders = async () => {
      loading.value = true

      try {
        const response = await ordersList()
        if (response.code === 200) {
          orders.value = response.data
        } else {
          ElMessage.error('获取订单列表失败：' + (response.msg || '未知错误'))
        }
      } catch (error) {
        console.error('获取订单列表出错:', error)
        ElMessage.error('获取订单列表失败，请检查网络连接')
      } finally {
        loading.value = false
      }
    }

    const handleReturnTickets = (order) => {
      selectedOrder.value = order
      returnDialogVisible.value = true
    }

    const confirmReturnTickets = async () => {
      if (!selectedOrder.value) return

      confirmingReturn.value = true
      returningOrderId.value = selectedOrder.value.orderId

      try {
        const response = await returnTickets(selectedOrder.value.orderId)

        if (response.code === 200) {
          ElMessage.success('退票成功')
          returnDialogVisible.value = false

          // Update the local orders list to reflect the change
          await fetchOrders()
        } else {
          ElMessage.error('退票失败：' + (response.msg || '未知错误'))
        }
      } catch (error) {
        console.error('退票出错:', error)
        ElMessage.error('退票失败，请稍后重试')
      } finally {
        confirmingReturn.value = false
        returningOrderId.value = null
      }
    }

    const formatOrderId = (orderId) => {
      if (!orderId) return ''
      // Show only the last 8 characters of the order ID
      return orderId.length > 8 ? '...' + orderId.slice(-8) : orderId
    }

    const formatOrderDate = (dateTime) => {
      if (!dateTime) return ''

      const date = new Date(dateTime)
      const now = new Date()

      // Format the time part
      const hours = date.getHours().toString().padStart(2, '0')
      const minutes = date.getMinutes().toString().padStart(2, '0')
      const timeStr = `${hours}:${minutes}`

      // If same day, return only the time
      if (date.toDateString() === now.toDateString()) {
        return `今天 ${timeStr}`
      }

      // If yesterday
      const yesterday = new Date(now)
      yesterday.setDate(now.getDate() - 1)
      if (date.toDateString() === yesterday.toDateString()) {
        return `昨天 ${timeStr}`
      }

      // Otherwise return full date
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      return `${date.getFullYear()}-${month}-${day} ${timeStr}`
    }

    const formatShowDate = (dateStr) => {
      if (!dateStr) return ''

      const date = new Date(dateStr)
      const now = new Date()

      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')

      // If today
      if (date.toDateString() === now.toDateString()) {
        return `今天 ${month}月${day}日`
      }

      // If tomorrow
      const tomorrow = new Date(now)
      tomorrow.setDate(now.getDate() + 1)
      if (date.toDateString() === tomorrow.toDateString()) {
        return `明天 ${month}月${day}日`
      }

      // Get day of week
      const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      return `${weekdays[date.getDay()]} ${month}月${day}日`
    }

    const formatShowTime = (timeStr) => {
      if (!timeStr) return ''
      // Extract hours and minutes from time string like "03:46:01"
      const timeParts = timeStr.split(':')
      if (timeParts.length >= 2) {
        return `${timeParts[0]}:${timeParts[1]}`
      }
      return timeStr
    }

    const goToMovies = () => {
      router.push('/users/movies')
    }

    onMounted(() => {
      fetchOrders()
    })

    return {
      loading,
      orders,
      filteredOrders,
      activeTab,
      setActiveTab,
      returnDialogVisible,
      returningOrderId,
      confirmingReturn,
      handleReturnTickets,
      confirmReturnTickets,
      formatOrderId,
      formatOrderDate,
      formatShowDate,
      formatShowTime,
      goToMovies
    }
  }
}
</script>

<style scoped>
.orders-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 160px);
  background-color: #f5f5f5;
}

.page-header {
  display: flex;
  flex-direction: column;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 16px 0;
  font-size: 24px;
  color: #333;
  font-weight: 600;
}

.filter-tabs {
  display: flex;
  border-bottom: 1px solid #e4e4e4;
}

.tab-item {
  padding: 12px 24px;
  font-size: 16px;
  cursor: pointer;
  color: #666;
  position: relative;
  transition: all 0.3s ease;
}

.tab-item:hover {
  color: #e54847;
}

.tab-item.active {
  color: #e54847;
  font-weight: 500;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 25%;
  width: 50%;
  height: 3px;
  background-color: #e54847;
  border-radius: 2px;
}

.orders-content {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: #999;
}

.loading-icon {
  font-size: 40px;
  margin-bottom: 16px;
  color: #e54847;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

.empty-container {
  padding: 60px 0;
}

.orders-list {
  padding: 0;
}

.order-item {
  border-bottom: 1px solid #f0f0f0;
}

.order-item:last-child {
  border-bottom: none;
}

.order-header {
  display: flex;
  justify-content: space-between;
  padding: 12px 16px;
  background-color: #f9f9f9;
  font-size: 14px;
  color: #666;
}

.order-number {
  font-family: Consolas, monospace;
}

.order-date {
  color: #999;
}

.order-content {
  padding: 16px;
  display: flex;
  position: relative;
}

.movie-poster {
  width: 90px;
  height: 140px;
  border-radius: 4px;
  overflow: hidden;
  margin-right: 16px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.movie-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.order-info {
  flex: 1;
  min-width: 0;
}

.movie-name {
  margin: 0 0 12px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  line-height: 1.2;
}

.cinema-info {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 16px;
  margin-bottom: 8px;
  color: #333;
  font-size: 14px;
}

.cinema-name,
.hall-info {
  display: flex;
  align-items: center;
}

.showtime-info {
  margin-bottom: 8px;
  color: #333;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.seats-info {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.seats-list {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.seat-item {
  background-color: #f5f5f5;
  border-radius: 4px;
  padding: 2px 6px;
  font-size: 12px;
}

.price-info {
  font-size: 14px;
  margin-top: 12px;
}

.price-label {
  color: #666;
}

.price-value {
  color: #e54847;
  font-weight: 600;
  font-size: 16px;
}

.order-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
  padding-left: 16px;
  min-width: 80px;
}

.status-tag {
  background: #999;
  color: white;
  border-radius: 12px;
  padding: 2px 12px;
  font-size: 12px;
  margin-bottom: 12px;
  text-align: center;
}

.status-tag.can-refund {
  background-color: #1c9f67;
}

.iconfont {
  margin-right: 6px;
  color: #666;
}

/* Fallback icons using emoji */
.icon-cinema::before {
  content: '🏢 ';
}

.icon-hall::before {
  content: '🎦 ';
}

.icon-time::before {
  content: '⏰ ';
}

.icon-seat::before {
  content: '💺 ';
}

.no-more-orders {
  color: #999;
  font-size: 14px;
  padding: 16px;
}

.warning-icon {
  font-size: 24px;
  color: #e54847;
  margin-bottom: 12px;
}

.return-dialog-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 20px 0;
}

.return-dialog-content p {
  margin-top: 8px;
  font-size: 16px;
  text-align: center;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .orders-container {
    padding: 16px 12px;
  }

  .order-content {
    flex-direction: column;
  }

  .movie-poster {
    width: 60px;
    height: 90px;
    position: absolute;
    top: 16px;
    left: 16px;
  }

  .order-info {
    padding-left: 76px;
    min-height: 90px;
  }

  .movie-name {
    font-size: 16px;
  }

  .order-actions {
    flex-direction: row;
    margin-top: 16px;
    padding-left: 0;
    width: 100%;
    justify-content: space-between;
    align-items: center;
  }

  .status-tag {
    margin-bottom: 0;
  }

  .action-placeholder {
    width: 52px;
  }
}

@media (max-width: 480px) {
  .page-header h2 {
    font-size: 20px;
  }

  .tab-item {
    padding: 10px 12px;
    font-size: 14px;
  }
}
</style>