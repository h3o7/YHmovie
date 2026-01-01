
<!-- filepath: /Users/y/Desktop/java/projects/YHmovie/YHmovie-Front-End/src/views/users/user/User.vue -->
<template>
    <div class="user-profile-container">
      <!-- 简化的页面头部，移除多余装饰 -->
      <div class="profile-header">
        <div class="container">
          <div class="profile-header-content">
            <div class="avatar-section">
              <div class="avatar-container" @click="showAvatarEditModal = true">
                <img :src="userInfo.userAvatarUrl || defaultAvatar" :alt="userInfo.userName" class="user-avatar" />
                <div class="edit-avatar-overlay">
                  <el-icon :size="24"><EditPen /></el-icon>
                </div>
              </div>
            </div>
            <div class="basic-info">
              <h1 class="user-name">{{ userInfo.userName || '加载中...' }}</h1>
              <div class="user-signature">{{ userInfo.userSignature || '这个人很懒，什么都没写~' }}</div>
              <div class="user-id">ID: {{ userInfo.userId }}</div>
            </div>
          </div>
        </div>
      </div>
  
      <!-- 主体内容区 -->
      <div class="profile-content">
        <div class="container">
          <!-- 个人信息编辑区域 -->
          <div class="info-section">
            <div class="section-header">
              <h2>个人信息</h2>
              <el-button 
                text
                :icon="editing ? Check : EditPen" 
                @click="toggleEditing"
                class="edit-btn"
                size="large"
              >
                {{ editing ? '保存' : '编辑' }}
              </el-button>
            </div>
            
            <!-- 个人信息表单 -->
            <div class="user-info-form">
              <el-form 
                ref="userFormRef" 
                :model="userForm" 
                label-position="top"
                :disabled="!editing"
              >
                <div class="form-row">
                  <el-form-item label="用户名">
                    <el-input 
                      v-model="userForm.userName" 
                      placeholder="请输入用户名"
                      :disabled="!editing"
                      class="borderless-input"
                    />
                  </el-form-item>
                  <el-form-item label="手机号码">
                    <el-input 
                      v-model="userForm.userPhone" 
                      placeholder="请输入手机号码"
                      :disabled="true"  
                      class="borderless-input"
                    />
                  </el-form-item>
                </div>
  
                <div class="form-row">
                  <el-form-item label="性别">
                    <el-select 
                      v-model="userForm.userGender" 
                      placeholder="请选择性别"
                      :disabled="!editing"
                      class="borderless-select"
                    >
                      <el-option label="男" value="男" />
                      <el-option label="女" value="女" />
                      <el-option label="其他" value="其他" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="出生日期">
                    <el-date-picker
                      v-model="userForm.userBirthDate"
                      type="date"
                      placeholder="请选择出生日期"
                      format="YYYY-MM-DD"
                      value-format="YYYY-MM-DD"
                      :disabled="!editing"
                      class="borderless-datepicker"
                    />
                  </el-form-item>
                </div>
  
                <el-form-item label="个性签名">
                  <el-input
                    v-model="userForm.userSignature"
                    type="textarea"
                    :rows="3"
                    placeholder="说点什么吧..."
                    :disabled="!editing"
                    maxlength="100"
                    show-word-limit
                    class="borderless-textarea"
                  />
                </el-form-item>
              </el-form>
            </div>
          </div>
  
          <!-- 订单摘要区域 -->
          <div class="info-section">
            <div class="section-header">
              <h2>最近订单</h2>
              <el-button 
                text
                @click="router.push('/users/orders')"
                class="view-all-btn"
                size="large"
              >
                <el-icon><Tickets /></el-icon>
                查看全部
              </el-button>
            </div>
            
            <div class="recent-orders">
              <!-- 加载中 -->
              <div class="loading-container" v-if="loadingOrders">
                <el-icon class="loading-icon">
                  <Loading />
                </el-icon>
                <span>正在加载订单...</span>
              </div>
              
              <!-- 空状态 -->
              <div class="empty-data" v-else-if="recentOrders.length === 0">
                <el-empty description="暂无订单记录" :image-size="80">
                  <el-button type="primary" @click="router.push('/users/movies')">去购票</el-button>
                </el-empty>
              </div>
              
              <!-- 订单列表 -->
              <div class="orders-simple-list" v-else>
                <div 
                  v-for="order in recentOrders" 
                  :key="order.orderId" 
                  class="order-simple-item"
                  @click="router.push('/users/orders')"
                >
                  <div class="order-simple-content">
                    <div class="movie-poster">
                      <img :src="order.moviePosterUrl" :alt="order.movieName" />
                    </div>
                    
                    <div class="order-simple-info">
                      <h3 class="movie-name">{{ order.movieName }}</h3>
                      
                      <div class="showtime-info">
                        
                        {{ formatShowDate(order.showtimeShowDate) }} {{ formatShowTime(order.showtimeStartTime) }}
                      </div>
                      
                      <div class="cinema-info">
                        
                        {{ order.cinemaName }}
                      </div>
                      
                      <div class="seats-info">
                        
                        <div class="seats-list">
                          <span class="seat-item" v-for="(seat, index) in order.seatNumbers" :key="index">
                            {{ seat }}
                          </span>
                        </div>
                      </div>
                    </div>
                    
                    <div class="order-simple-status">
                      <div class="status-tag" :class="{ 'can-refund': order.orderStatus }">
                        {{ order.orderStatus ? '待观影' : '已观影' }}
                      </div>
                      <div class="price-info">
                        <span class="price-value">¥{{ order.orderTotalPrice.toFixed(2) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- 查看更多 -->
                <div class="view-more" v-if="hasMoreOrders" @click="router.push('/users/orders')">
                  <span>查看更多订单</span>
                  <el-icon><ArrowRight /></el-icon>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
  
      <!-- 头像编辑弹窗 -->
      <el-dialog
        v-model="showAvatarEditModal"
        title="更换头像"
        width="420px"
        center
        destroy-on-close
        :show-close="false"
        class="borderless-dialog"
      >
        <div class="avatar-edit-content">
          <div class="current-avatar">
            <img :src="userInfo.userAvatarUrl || defaultAvatar" :alt="userInfo.userName" />
          </div>
          <el-form class="avatar-form">
            <el-form-item label="图像URL">
              <el-input 
                v-model="newAvatarUrl" 
                placeholder="请输入图像URL地址" 
                clearable
                class="borderless-input"
              />
            </el-form-item>
            <div class="avatar-tips">
              <p>提示: 请输入有效的图像URL地址</p>
              <p>推荐尺寸: 200x200像素或以上的正方形图像</p>
            </div>
          </el-form>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button text @click="showAvatarEditModal = false">取消</el-button>
            <el-button type="primary" plain @click="updateAvatar" :loading="updating">
              确认更换
            </el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </template>
  <script>
  import { ref, reactive, onMounted, computed } from 'vue'
  import { useRouter } from 'vue-router'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { 
    UserFilled, 
    EditPen,
    Check, 
    Tickets, 
    Calendar,
    Loading,Location, ArrowRight
  } from '@element-plus/icons-vue'
  import {ordersList} from '@/api/orders'
  import { getUser, updateUser } from '@/api/users'
  
  export default {
    name: 'UserProfile',
    components: {
      UserFilled,
      EditPen,
      Check,
      Tickets,
      Calendar
    },
    setup() {
  
      const router = useRouter()
      const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
      
      // 用户信息
      const userInfo = ref({})
      const userFormRef = ref(null)
      const userForm = reactive({
        userId: '',
        userName: '',
        userSignature: '',
        userPhone: '',
        userGender: '',
        userBirthDate: '',
        userAvatarUrl: ''
      })
      const loadingOrders = ref(true)
      const recentOrders = ref([])
      const hasMoreOrders = ref(false)
      
      // 编辑状态
      const editing = ref(false)
      const updating = ref(false)
      
      // 头像编辑相关
      const showAvatarEditModal = ref(false)
      const newAvatarUrl = ref('')
      
      // 获取当前登录用户ID
      const getCurrentUserId = () => {
        const loginData = localStorage.getItem('login')
        if (loginData) {
          try {
            const loginInfo = JSON.parse(loginData)
            return loginInfo.id
          } catch (e) {
            console.error('解析登录信息失败:', e)
            return null
          }
        }
        return null
      }
      // 获取最近订单
  const fetchRecentOrders = async () => {
    loadingOrders.value = true
    
    try {
      const response = await ordersList()
      if (response.code === 200) {
        // 最多显示3条最近订单
        recentOrders.value = response.data.slice(0, 3)
        hasMoreOrders.value = response.data.length > 3
      } else {
        console.error('获取订单列表失败：', response.msg)
      }
    } catch (error) {
      console.error('获取最近订单出错:', error)
    } finally {
      loadingOrders.value = false
    }
  }
  // 格式化日期和时间的函数
  const formatShowDate = (dateStr) => {
    if (!dateStr) return ''
  
    const date = new Date(dateStr)
    const now = new Date()
  
    const month = (date.getMonth() + 1).toString().padStart(2, '0')
    const day = date.getDate().toString().padStart(2, '0')
  
    // 如果是今天
    if (date.toDateString() === now.toDateString()) {
      return `今天 ${month}月${day}日`
    }
  
    // 如果是明天
    const tomorrow = new Date(now)
    tomorrow.setDate(now.getDate() + 1)
    if (date.toDateString() === tomorrow.toDateString()) {
      return `明天 ${month}月${day}日`
    }
  
    // 获取星期几
    const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    return `${weekdays[date.getDay()]} ${month}月${day}日`
  }
  
  const formatShowTime = (timeStr) => {
    if (!timeStr) return ''
    const timeParts = timeStr.split(':')
    if (timeParts.length >= 2) {
      return `${timeParts[0]}:${timeParts[1]}`
    }
    return timeStr
  }
      // 获取用户信息
      const fetchUserInfo = async () => {
        try {
          const userId = getCurrentUserId()
          if (!userId) {
            ElMessage.error('未获取到用户ID，请重新登录')
            router.push('/login')
            return
          }
          
          const response = await getUser(userId)
          if (response.code === 200 && response.data) {
            userInfo.value = response.data
            
            // 填充表单数据
            userForm.userId = response.data.userId
            userForm.userName = response.data.userName
            userForm.userSignature = response.data.userSignature
            userForm.userPhone = response.data.userPhone
            userForm.userGender = response.data.userGender
            userForm.userBirthDate = response.data.userBirthDate
            userForm.userAvatarUrl = response.data.userAvatarUrl
          } else {
            ElMessage.error(response.msg || '获取用户信息失败')
          }
        } catch (error) {
          console.error('获取用户信息失败:', error)
          ElMessage.error('获取用户信息失败，请稍后再试')
        }
      }
      
      // 切换编辑模式
      const toggleEditing = async () => {
        if (editing.value) {
          // 保存更改
          await saveUserInfo()
        } else {
          // 进入编辑模式
          editing.value = true
        }
      }
      
      // 保存用户信息
      const saveUserInfo = async () => {
        try {
          updating.value = true
          
          // 准备更新的数据
          const updateData = {
            userId: userForm.userId // userId是必传的
          }
          
          // 只添加已修改的字段
          if (userForm.userName !== userInfo.value.userName) {
            updateData.userName = userForm.userName
          }
          
          if (userForm.userSignature !== userInfo.value.userSignature) {
            updateData.userSignature = userForm.userSignature
          }
          
          if (userForm.userGender !== userInfo.value.userGender) {
            updateData.userGender = userForm.userGender
          }
          
          if (userForm.userBirthDate !== userInfo.value.userBirthDate) {
            updateData.userBirthDate = userForm.userBirthDate
          }
          
          // 检查是否有字段更新
          const hasChanges = Object.keys(updateData).length > 1
          
          if (!hasChanges) {
            editing.value = false
            ElMessage.info('未检测到信息变更')
            return
          }
          
          const response = await updateUser(updateData)
          if (response.code === 200) {
            ElMessage.success('个人信息更新成功')
            editing.value = false
            // 重新获取用户信息
            await fetchUserInfo()
          } else { 
            ElMessage.error(response.msg || '更新个人信息失败')
          }
        } catch (error) {
          console.error('更新用户信息失败:', error)
          ElMessage.error('更新个人信息失败，请稍后再试')
        } finally {
          updating.value = false
        }
      }
      
      // 更新头像
      const updateAvatar = async () => {
        if (!newAvatarUrl.value) {
          ElMessage.warning('请输入有效的图像URL')
          return
        }
        
        try {
          updating.value = true
          
          // 准备更新的数据
          const updateData = {
            userId: userForm.userId,
            userAvatarUrl: newAvatarUrl.value
          }
          
          const response = await updateUser(updateData)
          if (response.code === 200) {
            ElMessage.success('头像更新成功')
            
            // 更新本地数据
            userInfo.value.userAvatarUrl = newAvatarUrl.value
            userForm.userAvatarUrl = newAvatarUrl.value
            
            // 关闭弹窗
            showAvatarEditModal.value = false
            newAvatarUrl.value = ''
          } else {
            ElMessage.error(response.msg || '更新头像失败')
          }
        } catch (error) {
          console.error('更新头像失败:', error)
          ElMessage.error('更新头像失败，请稍后再试')
        } finally {
          updating.value = false
        }
      }
      
      onMounted(async () => {
        await fetchUserInfo()
        await fetchRecentOrders()
      })
      
      return {
        userInfo,
        userForm,
        userFormRef,
        defaultAvatar,
        editing,
        updating,
        showAvatarEditModal,
        newAvatarUrl,
        toggleEditing,
        updateAvatar,
        router,
        loadingOrders,
        recentOrders,
        hasMoreOrders,
        formatShowDate,
        formatShowTime,
        // Icons
        EditPen,
        Check,
        Loading,
        Location,
        ArrowRight
      }
    }
  }
  </script>
  
  <style scoped>
  /* 订单列表样式 */
  .loading-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 30px 0;
    color: #999;
  }
  
  .loading-icon {
    font-size: 30px;
    margin-bottom: 16px;
    animation: spin 1s linear infinite;
  }
  
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
  
  .orders-simple-list {
    background: white;
  }
  
  .order-simple-item {
    cursor: pointer;
    transition: all 0.3s;
  }
  
  .order-simple-item:hover {
    background-color: #fafafa;
  }
  
  .order-simple-content {
    display: flex;
    padding: 16px 0;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .order-simple-item:last-child .order-simple-content {
    border-bottom: none;
  }
  
  .movie-poster {
    width: 70px;
    height: 100px;
    border-radius: 4px;
    overflow: hidden;
    margin-right: 16px;
    flex-shrink: 0;
  }
  
  .movie-poster img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .order-simple-info {
    flex: 1;
    min-width: 0;
  }
  
  .movie-name {
    margin: 0 0 10px;
    font-size: 16px;
    font-weight: 500;
    color: #333;
    line-height: 1.2;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .showtime-info, .cinema-info, .seats-info {
    display: flex;
    align-items: center;
    margin-bottom: 6px;
    color: #666;
    font-size: 13px;
  }
  
  .showtime-info .el-icon,
  .cinema-info .el-icon,
  .seats-info .el-icon {
    margin-right: 6px;
    color: #999;
    font-size: 14px;
  }
  
  .seats-info {
    display: flex;
  }
  
  .seats-list {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
    flex: 1;
    min-width: 0;
  }
  
  .seat-item {
    background-color: #f5f5f5;
    border-radius: 4px;
    padding: 2px 6px;
    font-size: 12px;
    white-space: nowrap;
  }
  
  .order-simple-status {
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
    text-align: center;
  }
  
  .status-tag.can-refund {
    background-color: #1c9f67;
  }
  
  .price-info {
    font-size: 14px;
    margin-top: auto;
  }
  
  .price-value {
    color: #e54847;
    font-weight: 600;
    font-size: 16px;
  }
  
  .view-more {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 16px;
    color: #e54847;
    cursor: pointer;
    font-size: 14px;
  }
  
  .view-more .el-icon {
    margin-left: 4px;
  }
  
  /* 响应式调整 */
  @media screen and (max-width: 768px) {
    .movie-poster {
      width: 50px;
      height: 70px;
    }
    
    .order-simple-content {
      padding: 12px 0;
    }
    
    .seats-list {
      max-width: 150px;
    }
  }
  .user-profile-container {
    min-height: 100vh;
    background-color: #fff;
    color: #333;
  }
  
  .profile-header {
    padding: 60px 0 30px;
    background-color: #fafafa;
  }
  
  .container {
    width: 85%; /* 统一宽度 */
    max-width: 900px;
    margin: 0 auto;
    padding: 0; /* 清除可能的内边距 */
  }
  
  /* 确保内容区也遵循相同的布局规则 */
  .profile-content .container {
    background-color: #fff; /* 保持内容区的背景色 */
  }
  
  /* 头部容器不应该有背景色，它应该应用在整个header区域 */
  .profile-header .container {
    background-color: transparent; /* 移除头部容器的背景色 */
  }
  
  /* 其他样式保持不变... */
  .profile-header-content {
    display: flex;
    align-items: center;
    gap: 32px;
  }
  
  .avatar-container {
    position: relative;
    width: 120px;
    height: 120px;
    border-radius: 50%;
    overflow: hidden;
    cursor: pointer;
    background-color: #f0f0f0;
  }
  
  .user-avatar {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: all 0.3s ease;
  }
  
  .edit-avatar-overlay {
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: all 0.3s ease;
    color: white;
  }
  
  .avatar-container:hover .user-avatar {
    filter: blur(1px);
  }
  
  .avatar-container:hover .edit-avatar-overlay {
    opacity: 1;
  }
  
  .basic-info {
    padding-top: 10px;
  }
  
  .user-name {
    font-size: 28px;
    font-weight: 600;
    margin: 0 0 8px 0;
  }
  
  .user-signature {
    font-size: 15px;
    color: #666;
    margin-bottom: 8px;
    max-width: 500px;
  }
  
  .user-id {
    font-size: 14px;
    color: #999;
  }
  
  .profile-content {
    padding: 40px 0;
  }
  
  .info-section {
    margin-bottom: 40px;
  }
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .section-header h2 {
    font-size: 22px;
    font-weight: 600;
    margin: 0;
  }
  
  .edit-btn, .view-all-btn {
    font-size: 16px;
  }
  
  .user-info-form {
    padding: 8px 0;
  }
  
  .form-row {
    display: flex;
    flex-wrap: wrap;
    gap: 40px;
  }
  
  .form-row .el-form-item {
    flex: 1;
    min-width: 240px;
  }
  
  .el-form-item :deep(.el-form-item__label) {
    font-weight: 500;
    padding-bottom: 8px;
    font-size: 15px;
  }
  
  /* 无边框表单元素 */
  .borderless-input :deep(.el-input__wrapper) {
    box-shadow: none !important;
    border-radius: 0;
    border-bottom: 1px solid #eee;
    background-color: transparent;
    padding-left: 0;
  }
  
  .borderless-input:hover :deep(.el-input__wrapper) {
    border-bottom-color: #ddd;
  }
  
  .borderless-textarea :deep(.el-textarea__inner) {
    border: none;
    border-radius: 0;
    background-color: #f9f9f9;
    padding: 12px;
  }
  
  .borderless-select :deep(.el-input__wrapper) {
    box-shadow: none !important;
    border-radius: 0;
    border-bottom: 1px solid #eee;
    background-color: transparent;
    padding-left: 0;
  }
  
  .borderless-datepicker :deep(.el-input__wrapper) {
    box-shadow: none !important;
    border-radius: 0;
    border-bottom: 1px solid #eee;
    background-color: transparent;
    padding-left: 0;
  }
  
  /* 头像编辑样式 */
  .avatar-edit-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 24px;
  }
  
  .current-avatar {
    width: 140px;
    height: 140px;
    border-radius: 50%;
    overflow: hidden;
  }
  
  .current-avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .avatar-form {
    width: 100%;
  }
  
  .avatar-tips {
    margin-top: 8px;
    font-size: 13px;
    color: #999;
  }
  
  .avatar-tips p {
    margin: 4px 0;
  }
  
  .dialog-footer {
    margin-top: 16px;
    text-align: right;
  }
  
  /* 无边框对话框 */
  :deep(.borderless-dialog .el-dialog) {
    border-radius: 8px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
    overflow: hidden;
  }
  
  :deep(.borderless-dialog .el-dialog__header) {
    padding: 20px;
    margin: 0;
    background-color: #fafafa;
  }
  
  :deep(.borderless-dialog .el-dialog__title) {
    font-size: 18px;
    font-weight: 500;
  }
  
  :deep(.borderless-dialog .el-dialog__body) {
    padding: 24px 20px;
  }
  
  :deep(.borderless-dialog .el-dialog__footer) {
    padding: 16px 20px;
    border-top: 1px solid #f0f0f0;
  }
  
  /* 空数据区域 */
  .empty-data {
    padding: 40px 0;
  }
  
  /* 响应式调整 */
  @media screen and (max-width: 768px) {
    .profile-header {
      padding: 40px 0 20px;
    }
    
    .profile-header-content {
      flex-direction: column;
      text-align: center;
      gap: 20px;
    }
    
    .avatar-container {
      margin: 0 auto;
    }
    
    .basic-info {
      padding-top: 0;
    }
    
    .user-signature {
      margin: 8px auto;
    }
    
    .form-row {
      flex-direction: column;
      gap: 0;
    }
  }
  </style>