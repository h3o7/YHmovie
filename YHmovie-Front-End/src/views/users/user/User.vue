<template>
  <div class="user-profile-container">
    <!-- 简化的页面头部 -->
    <div class="profile-header">
      <div class="container">
        <div class="profile-header-content">
          <div class="avatar-section">
            <div class="avatar-container" @click="openAvatarModal">
              <img :src="userInfo.userAvatarUrl || defaultAvatar" :alt="userInfo.userName" class="user-avatar" />
              <div class="edit-avatar-overlay">
                <el-icon :size="24"><Camera /></el-icon>
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

    <!-- 头像编辑弹窗 (已更新为上传模式) -->
    <el-dialog
      v-model="showAvatarEditModal"
      title="更换头像"
      width="400px"
      center
      destroy-on-close
      :show-close="false"
      class="borderless-dialog avatar-dialog"
      @closed="resetUploadState"
    >
      <div class="avatar-edit-content">
        <el-upload
          class="avatar-uploader"
          action="#"
          :show-file-list="false"
          :http-request="customUpload"
          :before-upload="beforeAvatarUpload"
          v-loading="uploadLoading"
          element-loading-text="上传中..."
        >
          <!-- 优先显示临时预览图，其次显示原头像，最后显示默认 -->
          <img 
            v-if="tempAvatarUrl || userInfo.userAvatarUrl" 
            :src="tempAvatarUrl || userInfo.userAvatarUrl" 
            class="avatar-preview" 
          />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          
          <!-- 悬浮遮罩 -->
          <div class="upload-hover-mask">
            <el-icon :size="28"><Camera /></el-icon>
            <span class="upload-text">点击上传图片</span>
          </div>
        </el-upload>

        <div class="avatar-tips">
          <p>支持 JPG, PNG 格式，文件小于 2MB</p>
          <p>上传后预览满意请点击"确认更换"</p>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button text @click="showAvatarEditModal = false">取消</el-button>
          <el-button 
            type="primary" 
            plain 
            @click="confirmUpdateAvatar" 
            :loading="updating"
            :disabled="!tempAvatarUrl && !userInfo.userAvatarUrl"
          >
            确认更换
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  UserFilled, 
  EditPen,
  Check, 
  Tickets, 
  Calendar,
  Loading,
  Location, 
  ArrowRight,
  Plus,
  Camera
} from '@element-plus/icons-vue'
import { ordersList } from '@/api/orders'
// 假设 uploadUser 已经添加到 api/users 中导出，如果没有请手动添加
import { getUser, updateUser} from '@/api/users'
import {uploadUser} from '@/api/upload' // --- IGNORE ---

export default {
  name: 'UserProfile',
  components: {
    UserFilled,
    EditPen,
    Check,
    Tickets,
    Calendar,
    Loading,
    Plus,
    Camera
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
    
    // 订单相关
    const loadingOrders = ref(true)
    const recentOrders = ref([])
    const hasMoreOrders = ref(false)
    
    // 编辑状态
    const editing = ref(false)
    const updating = ref(false)
    
    // 头像上传相关
    const showAvatarEditModal = ref(false)
    const tempAvatarUrl = ref('') // 临时存储上传后的URL用于预览
    const uploadLoading = ref(false)
    
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

    // 打开头像弹窗
    const openAvatarModal = () => {
      showAvatarEditModal.value = true
    }

    // 重置上传状态
    const resetUploadState = () => {
      tempAvatarUrl.value = ''
      uploadLoading.value = false
    }

    // 图片上传前的校验
    const beforeAvatarUpload = (rawFile) => {
      const validTypes = ['image/jpeg', 'image/png', 'image/jpg']
      if (!validTypes.includes(rawFile.type)) {
        ElMessage.error('头像只能是 JPG 或 PNG 格式!')
        return false
      } else if (rawFile.size / 1024 / 1024 > 2) {
        ElMessage.error('头像大小不能超过 2MB!')
        return false
      }
      return true
    }

    // 自定义上传行为
    const customUpload = async (options) => {
      uploadLoading.value = true
      try {
        // 调用传入的API上传文件
        const res = await uploadUser(options.file)
        if (res.code === 200) {
          // 假设后端返回的数据 res.data 即为图片URL
          // 如果返回结构是 { url: '...' } 请改为 res.data.url
          tempAvatarUrl.value = res.data
          ElMessage.success('上传成功，请点击确认保存')
        } else {
          ElMessage.error(res.msg || '图片上传失败')
        }
      } catch (error) {
        console.error('上传出错:', error)
        ElMessage.error('上传出错，请稍后重试')
      } finally {
        uploadLoading.value = false
      }
    }

    // 确认更换头像
    const confirmUpdateAvatar = async () => {
      // 如果没有上传新图片，且没变化，直接关闭
      if (!tempAvatarUrl.value) {
        showAvatarEditModal.value = false
        return
      }
      
      try {
        updating.value = true
        
        // 准备更新的数据
        const updateData = {
          userId: userForm.userId,
          userAvatarUrl: tempAvatarUrl.value
        }
        
        const response = await updateUser(updateData)
        if (response.code === 200) {
          ElMessage.success('头像更换成功')
          
          // 更新本地数据
          userInfo.value.userAvatarUrl = tempAvatarUrl.value
          userForm.userAvatarUrl = tempAvatarUrl.value
          
          // 关闭弹窗
          showAvatarEditModal.value = false
        } else {
          ElMessage.error(response.msg || '保存头像失败')
        }
      } catch (error) {
        console.error('更新头像失败:', error)
        ElMessage.error('更新头像失败，请稍后再试')
      } finally {
        updating.value = false
      }
    }

    // 获取最近订单
    const fetchRecentOrders = async () => {
      loadingOrders.value = true
      try {
        const response = await ordersList()
        if (response.code === 200) {
          recentOrders.value = response.data.slice(0, 3)
          hasMoreOrders.value = response.data.length > 3
        }
      } catch (error) {
        console.error('获取最近订单出错:', error)
      } finally {
        loadingOrders.value = false
      }
    }

    // 格式化日期
    const formatShowDate = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const now = new Date()
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      if (date.toDateString() === now.toDateString()) return `今天 ${month}月${day}日`
      return `${month}月${day}日`
    }

    const formatShowTime = (timeStr) => {
      if (!timeStr) return ''
      const timeParts = timeStr.split(':')
      return timeParts.length >= 2 ? `${timeParts[0]}:${timeParts[1]}` : timeStr
    }

    // 获取用户信息
    const fetchUserInfo = async () => {
      try {
        const userId = getCurrentUserId()
        if (!userId) {
          router.push('/login')
          return
        }
        
        const response = await getUser(userId)
        if (response.code === 200 && response.data) {
          userInfo.value = response.data
          Object.assign(userForm, response.data)
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
      }
    }
    
    const toggleEditing = async () => {
      if (editing.value) {
        await saveUserInfo()
      } else {
        editing.value = true
      }
    }
    
    const saveUserInfo = async () => {
      try {
        updating.value = true
        const updateData = { userId: userForm.userId }
        
        if (userForm.userName !== userInfo.value.userName) updateData.userName = userForm.userName
        if (userForm.userSignature !== userInfo.value.userSignature) updateData.userSignature = userForm.userSignature
        if (userForm.userGender !== userInfo.value.userGender) updateData.userGender = userForm.userGender
        if (userForm.userBirthDate !== userInfo.value.userBirthDate) updateData.userBirthDate = userForm.userBirthDate
        
        if (Object.keys(updateData).length <= 1) {
          editing.value = false
          return
        }
        
        const response = await updateUser(updateData)
        if (response.code === 200) {
          ElMessage.success('保存成功')
          editing.value = false
          await fetchUserInfo()
        } else {
          ElMessage.error(response.msg || '保存失败')
        }
      } catch (error) {
        ElMessage.error('保存失败，请稍后再试')
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
      tempAvatarUrl,
      uploadLoading,
      toggleEditing,
      openAvatarModal,
      resetUploadState,
      customUpload,
      beforeAvatarUpload,
      confirmUpdateAvatar,
      router,
      loadingOrders,
      recentOrders,
      hasMoreOrders,
      formatShowDate,
      formatShowTime,
      EditPen,
      Check,
      Loading,
      Location,
      ArrowRight,
      Plus,
      Camera
    }
  }
}
</script>

<style scoped>
/* 原有样式保留... */
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
  width: 85%;
  max-width: 900px;
  margin: 0 auto;
}

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
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
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
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
  color: white;
}

.avatar-container:hover .user-avatar {
  filter: blur(2px);
  transform: scale(1.05);
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

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 40px;
}

.form-row .el-form-item {
  flex: 1;
  min-width: 240px;
}

/* 无边框表单元素样式 */
.borderless-input :deep(.el-input__wrapper),
.borderless-select :deep(.el-input__wrapper),
.borderless-datepicker :deep(.el-input__wrapper) {
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
  border-radius: 4px;
  background-color: #f9f9f9;
  padding: 12px;
}

/* ------------------- 新增上传组件样式 ------------------- */
.avatar-edit-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 0;
}

.avatar-uploader {
  position: relative;
  display: block;
  width: 150px;
  height: 150px;
  margin-bottom: 20px;
}

.avatar-uploader :deep(.el-upload) {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 2px dashed #dcdfe6;
  background-color: #fafafa;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: #409eff;
  background-color: #f0f7ff;
}

.avatar-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 悬浮遮罩 */
.upload-hover-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
  z-index: 10;
}

.upload-text {
  font-size: 14px;
  margin-top: 8px;
}

/* 当已经有图片或 hover 时显示遮罩 */
.avatar-uploader :deep(.el-upload:hover) .upload-hover-mask {
  opacity: 1;
}

.avatar-tips {
  text-align: center;
  font-size: 13px;
  color: #909399;
  line-height: 1.6;
}

.avatar-tips p {
  margin: 4px 0;
}

.dialog-footer {
  text-align: right;
  padding-top: 10px;
}

/* 对话框样式优化 */
:deep(.borderless-dialog .el-dialog) {
  border-radius: 12px;
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.1);
}

:deep(.borderless-dialog .el-dialog__header) {
  padding: 24px 24px 10px;
  margin-right: 0;
  border-bottom: none;
}

/* 订单部分样式 */
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

.movie-poster {
  width: 70px;
  height: 100px;
  border-radius: 4px;
  overflow: hidden;
  margin-right: 16px;
  flex-shrink: 0;
  background-color: #f0f0f0;
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
}

.showtime-info, .cinema-info, .seats-info {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
  color: #666;
  font-size: 13px;
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
}

.status-tag.can-refund {
  background-color: #1c9f67;
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

.loading-container, .empty-data {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}
.loading-icon {
  font-size: 24px;
  margin-bottom: 10px;
  animation: rotate 1s linear infinite;
}
@keyframes rotate { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

@media screen and (max-width: 768px) {
  .profile-header-content {
    flex-direction: column;
    text-align: center;
  }
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>