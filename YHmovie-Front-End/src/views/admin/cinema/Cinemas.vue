<template>
  <div class="cinema-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">影院管理</h1>
          <div class="title-badge">
            <span class="badge-icon">🎬</span>
            <span class="badge-text">共 {{ total }} 家影院</span>
          </div>
        </div>
        <div class="header-actions">
          <el-button type="primary" class="add-btn" @click="openAddDialog">
            <el-icon><Plus /></el-icon>
            添加影院
          </el-button>
          <el-button class="refresh-btn" @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
    </div>

    <!-- 搜索区域 -->
   

    <!-- 影院列表 -->
    <div class="cinema-list-container">
      <div class="list-header">
        <div class="list-title">影院列表</div>
        
        <div class="search-input-group">
          <div class="search-item">
            <el-input
              v-model="cinemaDTOForm.cinemaName"
              placeholder="请输入影院名称搜索"
              class="search-input"
              clearable
              @keyup.enter="searchCinemas"
            >
              <template #prefix>
                <el-icon class="search-icon"><Search /></el-icon>
              </template>
            </el-input>
          </div>
          <div class="search-item">
            <el-input
              v-model="cinemaDTOForm.provinceName"
              placeholder="省份筛选"
              class="search-input"
              clearable
              @keyup.enter="searchCinemas"
            >
              <template #prefix>
                <el-icon class="search-icon"><Location /></el-icon>
              </template>
            </el-input>
          </div>
          <div class="search-item">
            <el-input
              v-model="cinemaDTOForm.cityName"
              placeholder="城市筛选"
              class="search-input"
              clearable
              @keyup.enter="searchCinemas"
            >
              <template #prefix>
                <el-icon class="search-icon"><Location /></el-icon>
              </template>
            </el-input>
          </div>

          <div class="search-actions">
            <el-button type="primary" class="search-btn" @click="searchCinemas" :loading="loading">
              搜索
            </el-button>
            <el-button class="reset-btn" @click="resetSearch">
              重置
            </el-button>
          </div>
        </div>
      
    
        <div class="list-stats">
          <span class="stats-item">总计: {{ total }}</span>
          <span class="stats-item">当前页: {{ cinemaDTOForm.pageNum }}</span>
        </div>
      </div>

      <div class="cinema-table-wrapper">
        <el-table
          :data="cinemaList"
          v-loading="loading"
          class="cinema-table"
          :header-cell-style="{ backgroundColor: '#f8fafc', color: '#475569', fontWeight: '600' }"
          empty-text="暂无影院数据"
        >
          <el-table-column type="index" label="序号" width="150" align="center">
            <template #default="{ $index }">
              <div class="index-cell">{{ $index + 1 + (cinemaDTOForm.pageNum - 1) * cinemaDTOForm.pageSize }}</div>
            </template>
          </el-table-column>

          <el-table-column label="影院信息" width="250" >
            <template #default="{ row }">
              <div class="cinema-info">
                <div class="cinema-avatar">
                  <img :src="getCinemaAvatar(row.cinemaName)" :alt="row.cinemaName" />
                </div>
                <div class="cinema-details">
                  <div class="cinema-name">{{ row.cinemaName }}</div>
                  <div class="cinema-id">ID: {{ row.cinemasId }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="位置信息" width="270" >
            <template #default="{ row }">
              <div class="location-info">
                <div class="location-tags">
                  <span class="location-tag province">{{ row.provinceName }}</span>
                  <span class="location-tag city">{{ row.cityName }}</span>
                </div>
                <div class="address">{{ row.address }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="联系方式" width="230">
            <template #default="{ row }">
              <div class="contact-info">
                <div class="phone-number">
                  <el-icon class="phone-icon"><Phone /></el-icon>
                  {{ row.phoneNumber }}
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="更新时间" width="200" align="center">
            <template #default="{ row }">
              <div class="update-time">
                <el-icon class="time-icon"><Clock /></el-icon>
                {{ formatTime(row.updateTime) }}
              </div>
            </template>
          </el-table-column>

          <el-table-column label="操作" min-width="120" align="center" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-tooltip content="编辑影院" placement="top">
                  <el-button
                    type="primary"
                    size="large"
                    circle
                    class="edit-btn"
                    @click="openEditDialog(row)"
                  >
                    <el-icon><Edit /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="删除影院" placement="top">
                  <el-button
                    type="danger"
                    size="large"
                    circle
                    class="delete-btn"
                    @click="handleDelete(row)"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页器 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="cinemaDTOForm.pageNum"
            v-model:page-size="cinemaDTOForm.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            class="custom-pagination"
          />
        </div>
      </div>
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="680px"
      :close-on-click-modal="false"
      destroy-on-close
      class="cinema-dialog"
    >
      <div class="dialog-content">
        <el-form
          ref="cinemaFormRef"
          :model="cinemaForm"
          :rules="formRules"
          label-width="120px"
          class="cinema-form"
        >
          <!-- 影院ID（仅编辑时显示） -->
          <el-form-item v-if="isEdit" label="影院ID">
            <el-input v-model="cinemaForm.cinemasId" disabled class="disabled-input" />
          </el-form-item>

          <el-form-item label="影院名称" prop="cinemaName">
            <el-input
              v-model="cinemaForm.cinemaName"
              placeholder="请输入影院名称"
              class="form-input"
            >
              <template #prefix>
                <el-icon><VideoCamera /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="省份" prop="provinceName">
                <el-input
                  v-model="cinemaForm.provinceName"
                  placeholder="请输入省份"
                  class="form-input"
                >
                  <template #prefix>
                    <el-icon><Location /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="城市" prop="cityName">
                <el-input
                  v-model="cinemaForm.cityName"
                  placeholder="请输入城市"
                  class="form-input"
                >
                  <template #prefix>
                    <el-icon><Location /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="详细地址" prop="address">
            <el-input
              v-model="cinemaForm.address"
              type="textarea"
              :rows="3"
              placeholder="请输入详细地址"
              class="form-textarea"
            />
          </el-form-item>

          <el-form-item label="联系电话" prop="phoneNumber">
            <el-input
              v-model="cinemaForm.phoneNumber"
              placeholder="请输入联系电话"
              class="form-input"
            >
              <template #prefix>
                <el-icon><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button class="cancel-btn" @click="dialogVisible = false">
            取消
          </el-button>
          <el-button type="primary" class="submit-btn" @click="handleSubmit" :loading="submitLoading">
            {{ isEdit ? '更新' : '添加' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, Refresh, Plus, Edit, Delete, Location, Phone, Clock, 
  VideoCamera 
} from '@element-plus/icons-vue'
import { getList, addCinema, updateCinema, deleteCinema } from '@/api/cinemas'

// 搜索表单数据
const cinemaDTOForm = reactive({
  cinemaName: '',
  cityName: '',
  provinceName: '',
  pageNum: 1,
  pageSize: 10
})

// 影院表单数据
const cinemaForm = reactive({
  cinemasId: '',
  locationsId: '',
  cinemaName: '',
  cityName: '',
  provinceName: '',
  address: '',
  phoneNumber: '',
  updateTime: ''
})

// 页面状态
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const cinemaList = ref([])
const total = ref(0)
const cinemaFormRef = ref()

// 对话框标题
const dialogTitle = computed(() => isEdit.value ? '编辑影院信息' : '添加新影院')

// 表单验证规则
const formRules = {
  cinemaName: [
    { required: true, message: '请输入影院名称', trigger: 'blur' },
    { min: 2, max: 50, message: '影院名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  provinceName: [
    { required: true, message: '请输入省份名称', trigger: 'blur' }
  ],
  cityName: [
    { required: true, message: '请输入城市名称', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { min: 5, max: 200, message: '地址长度在 5 到 200 个字符', trigger: 'blur' }
  ],
  phoneNumber: [
    { required: true, message: '请输入电话号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/, message: '请输入正确的电话号码', trigger: 'blur' }
  ]
}

// 获取影院头像
const getCinemaAvatar = (cinemaName) => {
  const colors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7', '#DDA0DD', '#98D8C8']
  const index = cinemaName.charCodeAt(0) % colors.length
  return `https://ui-avatars.com/api/?name=${encodeURIComponent(cinemaName)}&background=${colors[index].substring(1)}&color=fff&size=40`
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  const date = new Date(timeStr)
  return `${date.getFullYear()}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 获取影院列表
const getCinemaList = async () => {
  try {
    loading.value = true
    const response = await getList(cinemaDTOForm)
    cinemaList.value = response.data.rows || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取影院列表失败')
    console.error('获取影院列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索影院
const searchCinemas = () => {
  cinemaDTOForm.pageNum = 1
  getCinemaList()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(cinemaDTOForm, {
    cinemaName: '',
    cityName: '',
    provinceName: '',
    pageNum: 1,
    pageSize: 10
  })
  getCinemaList()
}

// 刷新页面
const handleRefresh = () => {
  getCinemaList()
}

// 分页处理
const handleSizeChange = (size) => {
  cinemaDTOForm.pageSize = size
  cinemaDTOForm.pageNum = 1
  getCinemaList()
}

const handleCurrentChange = (page) => {
  cinemaDTOForm.pageNum = page
  getCinemaList()
}

// 重置表单
const resetForm = () => {
  Object.assign(cinemaForm, {
    cinemasId: '',
    locationsId: '',
    cinemaName: '',
    cityName: '',
    provinceName: '',
    address: '',
    phoneNumber: '',
    updateTime: ''
  })
  cinemaFormRef.value?.clearValidate()
}

// 打开添加对话框
const openAddDialog = () => {
  resetForm()
  isEdit.value = false
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (row) => {
  resetForm()
  Object.assign(cinemaForm, { ...row })
  isEdit.value = true
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  try {
    await cinemaFormRef.value.validate()
    submitLoading.value = true
    
    if (isEdit.value) {
      await updateCinema(cinemaForm)
      ElMessage.success('编辑影院成功')
    } else {
      await addCinema(cinemaForm)
      ElMessage.success('添加影院成功')
    }
    
    dialogVisible.value = false
    getCinemaList()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(isEdit.value ? '编辑影院失败' : '添加影院失败')
      console.error('操作失败:', error)
    }
  } finally {
    submitLoading.value = false
  }
}

// 删除影院
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除影院"${row.cinemaName}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }
    )
    
    await deleteCinema(row.cinemasId)
    ElMessage.success('删除成功')
    getCinemaList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('删除失败:', error)
    }
  }
}

// 页面初始化
onMounted(() => {
  getCinemaList()
})
</script>

<style scoped>
/* 全局样式设置 */
.cinema-management {
  min-height: 100vh;
  background: #f2f3f6; /* 渐变背景 */
  padding: 20px;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif; /* 现代字体 */
}

/* 页面头部样式 */
.page-header {
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.95); /* 半透明白色背景 */
  backdrop-filter: blur(10px); /* 毛玻璃效果 */
  border-radius: 16px;
  padding: 20px 28px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1); /* 柔和阴影 */
}

.title-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
  background: linear-gradient(135deg, #667eea, #764ba2); /* 渐变文字 */
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title-badge {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); /* 渐变徽章 */
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  gap: 6px;
}

.badge-icon {
  font-size: 16px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.add-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); /* 渐变按钮 */
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.add-btn:hover {
  transform: translateY(-2px); /* 悬浮效果 */
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.refresh-btn {
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  padding: 12px 20px;
  color: #64748b;
  transition: all 0.3s ease;
}

.refresh-btn:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
  transform: translateY(-1px);
}

/* 搜索区域样式 */
.search-container {
  margin-bottom: 24px;
}

.search-box {
  background: rgba(255, 255, 255, 0.95); /* 半透明背景 */
  backdrop-filter: blur(10px); /* 毛玻璃效果 */
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.search-input-group {
  display: flex;
  gap: 20px;
  align-items: flex-end;
  flex-wrap: wrap;
}

.search-item {
  flex: 1;
  min-width: 200px;
}

.search-input {
  border-radius: 12px;
}

:deep(.search-input .el-input__wrapper) {
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  box-shadow: none;
  transition: all 0.3s ease;
}

:deep(.search-input .el-input__wrapper:hover) {
  border-color: #cbd5e1;
}

:deep(.search-input .el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-icon {
  color: #94a3b8;
}

.search-actions {
  display: flex;
  gap: 0px;
}

.search-btn {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); /* 渐变搜索按钮 */
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
}

.reset-btn {
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  padding: 12px 24px;
  color: #64748b;
}

/* 影院列表容器样式 */
.cinema-list-container {
  background: rgba(255, 255, 255, 0.95); /* 半透明背景 */
  backdrop-filter: blur(10px); /* 毛玻璃效果 */
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
  background: #f8fafc;
}

.list-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a202c;
}

.list-stats {
  display: flex;
  gap: 16px;
}

.stats-item {
  padding: 6px 12px;
  background: white;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

/* 表格样式 */
.cinema-table-wrapper {
  padding: 0;
}

.cinema-table {
  border-radius: 0;
}

:deep(.cinema-table .el-table__body-wrapper) {
  border-radius: 0;
}

:deep(.cinema-table .el-table__row) {
  transition: all 0.3s ease;
}

:deep(.cinema-table .el-table__row:hover) {
  background-color: #f8fafc; /* 悬浮行背景 */
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.index-cell {
  font-weight: 600;
  color: #64748b;
  background: linear-gradient(135deg, #667eea, #764ba2); /* 渐变序号 */
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 影院信息样式 */
.cinema-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.cinema-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.cinema-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cinema-details {
  flex: 1;
}

.cinema-name {
  font-weight: 600;
  color: #1a202c;
  font-size: 15px;
  margin-bottom: 2px;
}

.cinema-id {
  font-size: 12px;
  color: #94a3b8;
  font-family: 'Monaco', 'Menlo', monospace; /* 等宽字体 */
}

/* 位置信息样式 */
.location-info {
  padding: 8px 0;
}

.location-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 6px;
}

.location-tag {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.location-tag.province {
  background: linear-gradient(135deg, #ffeaa7, #fab1a0); /* 省份标签渐变 */
  color: #d63031;
}

.location-tag.city {
  background: linear-gradient(135deg, #81ecec, #74b9ff); /* 城市标签渐变 */
  color: #0984e3;
}

.address {
  font-size: 13px;
  color: #64748b;
  line-height: 1.4;
}

/* 联系信息样式 */
.contact-info {
  padding: 8px 0;
}

.phone-number {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #475569;
  font-size: 14px;
}

.phone-icon {
  color: #10b981;
}

/* 更新时间样式 */
.update-time {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #64748b;
  font-size: 13px;
  font-family: 'Monaco', 'Menlo', monospace; /* 等宽字体 */
}

.time-icon {
  color: #8b5cf6;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.edit-btn {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); /* 编辑按钮渐变 */
  border: none;
  transition: all 0.3s ease;
}

.edit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(79, 172, 254, 0.4);
}

.delete-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%); /* 删除按钮渐变 */
  border: none;
  transition: all 0.3s ease;
}

.delete-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
}

/* 分页器样式 */
.pagination-container {
  padding: 20px 24px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
}

:deep(.custom-pagination .el-pagination__jump) {
  color: #64748b;
}

:deep(.custom-pagination .el-pagination__total) {
  color: #64748b;
  font-weight: 600;
}

:deep(.custom-pagination .el-pager li) {
  border-radius: 8px;
  margin: 0 2px;
  transition: all 0.3s ease;
}

:deep(.custom-pagination .el-pager li:hover) {
  background: #667eea;
  color: white;
}

:deep(.custom-pagination .el-pager li.is-active) {
  background: linear-gradient(135deg, #667eea, #764ba2); /* 分页器活动页渐变 */
  color: white;
}

/* 对话框样式 */
:deep(.cinema-dialog .el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

:deep(.cinema-dialog .el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); /* 对话框头部渐变 */
  color: white;
  padding: 20px 24px;
  border-bottom: none;
}

:deep(.cinema-dialog .el-dialog__title) {
  color: white;
  font-weight: 700;
  font-size: 18px;
}

:deep(.cinema-dialog .el-dialog__close) {
  color: white;
  font-size: 20px;
}

.dialog-content {
  padding: 24px;
  background: white;
}

.cinema-form {
  margin: 0;
}

:deep(.cinema-form .el-form-item__label) {
  color: #374151;
  font-weight: 600;
}

.form-input, .form-textarea {
  border-radius: 12px;
}

:deep(.form-input .el-input__wrapper) {
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  transition: all 0.3s ease;
}

:deep(.form-input .el-input__wrapper:hover) {
  border-color: #cbd5e1;
}

:deep(.form-input .el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

:deep(.form-textarea .el-textarea__inner) {
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  transition: all 0.3s ease;
}

:deep(.form-textarea .el-textarea__inner:hover) {
  border-color: #cbd5e1;
}

:deep(.form-textarea .el-textarea__inner:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.disabled-input {
  opacity: 0.6;
}

/* 对话框底部按钮 */
.dialog-footer {
  padding: 16px 24px 24px;
  text-align: right;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
}

.cancel-btn {
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  padding: 12px 24px;
  color: #64748b;
  font-weight: 600;
  margin-right: 12px;
}

.submit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); /* 提交按钮渐变 */
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .search-input-group {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-item {
    min-width: auto;
  }
  
  .search-actions {
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .cinema-management {
    padding: 10px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .title-section {
    justify-content: center;
  }
  
  .header-actions {
    justify-content: center;
  }
  
  :deep(.cinema-dialog) {
    width: 95% !important;
    margin: 0 auto;
  }
  
  .location-tags {
    flex-wrap: wrap;
  }
}

/* 加载状态美化 */
:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
}

:deep(.el-loading-spinner) {
  color: #667eea;
}

/* 空状态美化 */
:deep(.el-table__empty-block) {
  padding: 60px 0;
}

:deep(.el-table__empty-text) {
  color: #94a3b8;
  font-size: 16px;
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #cbd5e1, #94a3b8);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #94a3b8, #64748b);
}
</style>