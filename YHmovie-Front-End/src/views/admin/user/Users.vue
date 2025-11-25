<template>
  <div class="user-management">
    <!-- 页面标题 -->
    
    <!-- 功能操作区 -->
    <el-card class="operation-card" shadow="never">
      <div class="operation-header">
        <div class="header-left">
          <h3>用户列表</h3>
          <span class="count-badge">共 {{ filteredUserList.length }} 个用户</span>
        </div>
        
        <div class="header-right">
          <!-- 搜索框 -->
          <el-input
            v-model="searchKeyword"
            placeholder="请输入真实姓名搜索"
            style="width: 300px; margin-right: 16px;"
            clearable
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          
          <!-- 操作按钮 -->
          <el-button type="primary" icon="Plus" @click="openUserDialog('add')">
            添加用户
          </el-button>
          <el-button icon="Refresh" @click="refreshUserData">
            刷新
          </el-button>
        </div>
      </div>

      <!-- 用户表格 -->
      <div class="table-container">
        <el-table
          :data="paginatedUserList"
          stripe
          style="width: 100%"
          v-loading="tableLoading"
          @sort-change="handleSortChange"
        >
          <!-- 序号列 -->
          <el-table-column
            type="index"
            label="序号"
            width="80"
            align="center"
            :index="getTableIndex"
          />
          
          <!-- 头像列 -->
          <el-table-column label="头像" width="170" align="center">
            <template #default="{ row }">
              <el-avatar
                :src="row.avatarUrl"
                :size="50"
                class="user-avatar"
              >
                <el-icon><User /></el-icon>
              </el-avatar>
            </template>
          </el-table-column>

          <el-table-column
            prop="userName"
            label="用户名"
            min-width="140"
            sortable="custom"
          >
            <template #default="{ row }">
              <div class="name-cell">
                <span class="real-name">{{ row.userName }}</span>
              </div>
            </template>
          </el-table-column>
          
          <!-- 真实姓名列 -->
          <el-table-column
            prop="realName"
            label="真实姓名"
            width="170"
            sortable="custom"
          >
            <template #default="{ row }">
              <div class="name-cell">
                <span class="real-name">{{ row.realName }}</span>
              </div>
            </template>
          </el-table-column>
          
          <!-- 性别列 -->
          <el-table-column prop="gender" label="性别" width="120" align="center">
            <template #default="{ row }">
              <el-tag
                :type="row.gender === '男' ? 'primary' : 'success'"
                size="large"
              >
                {{ row.gender }}
              </el-tag>
            </template>
          </el-table-column>
          
          <!-- 用户状态列 -->
          <el-table-column prop="userStatus" label="状态" width="150" align="center">
            <template #default="{ row }">
              <el-tag
                :type="getUserStatusType(row.userStatus)"
                size="small"
              >
                {{ getUserStatusText(row.userStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <!-- 最后登录时间列 -->
          <el-table-column
            prop="lastLoginTime"
            label="最后登录"
            min-width="160"
            sortable="custom"
          >
            <template #default="{ row }">
              <div class="time-cell">
                <el-icon><Clock /></el-icon>
                <span>{{ formatDateTime(row.lastLoginTime) }}</span>
              </div>
            </template>
          </el-table-column>
          
          <!-- 操作列 -->
          <el-table-column label="操作" width="170" align="center" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-tooltip content="编辑用户" placement="top">
                  <el-button
                    type="primary"
                    icon="Edit"
                    size="large"
                    circle
                    @click="openUserDialog('edit', row)"
                  />
                </el-tooltip>
                <el-tooltip content="删除用户" placement="top">
                  <el-button
                    type="danger"
                    icon="Delete"
                    size="large"
                    circle
                    @click="handleDeleteUser(row.usersId, row.realName)"
                  />
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页器 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="filteredUserList.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>

    <!-- 用户对话框 -->
    <el-dialog
      v-model="userDialogVisible"
      :title="userDialogMode === 'add' ? '添加用户' : '编辑用户'"
      width="600px"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userFormRules"
        label-width="100px"
        class="user-form"
      >
        <!-- 头像上传 -->
        <el-form-item label="用户头像">
          <div class="avatar-upload">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleAvatarUpload"
              accept="image/*"
            >
              <div class="avatar-container">
                <img
                  v-if="userForm.avatarUrl"
                  :src="userForm.avatarUrl"
                  class="avatar-preview"
                  alt="用户头像"
                  @error="handleImageError"
                />
                <div v-else class="avatar-placeholder">
                  <el-icon class="avatar-icon"><Plus /></el-icon>
                  <div class="avatar-text">上传头像</div>
                </div>
              </div>
            </el-upload>
            <div class="avatar-tips">
              <p>支持 JPG、PNG 格式，文件大小不超过 2MB</p>
              <el-button
                v-if="userForm.avatarUrl"
                type="danger"
                size="large"
                text
                @click="removeAvatar"
              >
              <el-icon
              size="large" 
              style="margin-right: 4px;">
                <delete />
              </el-icon>
                移除头像
              </el-button>
            </div>
          </div>
        </el-form-item>

        <el-row :gutter="20">
          <!-- 用户名 -->
          <el-col :span="12">
            <el-form-item label="用户名" prop="userName">
              <el-input
                v-model="userForm.userName"
                placeholder="请输入用户名"
                clearable
              />
            </el-form-item>
          </el-col>
          
          <!-- 真实姓名 -->
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input
                v-model="userForm.realName"
                placeholder="请输入真实姓名"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <!-- 电话号码 -->
          <el-col :span="12">
            <el-form-item label="电话号码" prop="phoneNumber">
              <el-input
                v-model="userForm.phoneNumber"
                placeholder="请输入电话号码"
                clearable
              />
            </el-form-item>
          </el-col>
          
          <!-- 性别 -->
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select
                v-model="userForm.gender"
                placeholder="请选择性别"
                style="width: 100%"
              >
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <!-- 出生日期 -->
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="userForm.birthDate"
                type="date"
                placeholder="请选择出生日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          
          <!-- 用户状态 -->
          <el-col :span="12">
            <el-form-item label="用户状态" prop="userStatus">
              <el-select
                v-model="userForm.userStatus"
                placeholder="请选择用户状态"
                style="width: 100%"
              >
                <el-option label="活跃" value="active" />
                <el-option label="非活跃" value="inactive" />
                <el-option label="已禁用" value="banned" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button
            type="primary"
            @click="handleUserSubmit"
            :loading="submitLoading"
          >
            {{ userDialogMode === 'add' ? '添加' : '更新' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Plus,
  Refresh,
  User,
  Edit,
  Delete,
  Clock
} from '@element-plus/icons-vue'

// API 导入
import {
  getUserList,
  deleteUser,
  addUser,
  updateUser,
  getUserInfo,
  uploadImage
} from '@/api/users'

// 响应式数据
const userList = ref([])
const searchKeyword = ref('')
const tableLoading = ref(false)
const submitLoading = ref(false)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(20)

// 对话框相关
const userDialogVisible = ref(false)
const userDialogMode = ref('add')
const userFormRef = ref()

// 用户表单数据
const userForm = ref({
  usersId: '',
  userName: '',
  realName: '',
  phoneNumber: '',
  gender: '',
  birthDate: '',
  avatarUrl: '',
  userStatus: 'active'
})

// 表单验证规则
const userFormRules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '真实姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  phoneNumber: [
    { required: true, message: '请输入电话号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  birthDate: [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ],
  userStatus: [
    { required: true, message: '请选择用户状态', trigger: 'change' }
  ]
}

// 计算属性
const filteredUserList = computed(() => {
  if (!searchKeyword.value.trim()) {
    return userList.value
  }
  return userList.value.filter(user =>
    user.realName?.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

const paginatedUserList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredUserList.value.slice(start, end)
})

// 工具函数
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN')
}

const getUserStatusType = (status) => {
  const statusMap = {
    'active': 'success',
    'inactive': 'warning',
    'banned': 'danger'
  }
  return statusMap[status] || 'info'
}

const getUserStatusText = (status) => {
  const statusMap = {
    'active': '活跃',
    'inactive': '非活跃',
    'banned': '已禁用'
  }
  return statusMap[status] || '未知'
}

const getTableIndex = (index) => {
  return (currentPage.value - 1) * pageSize.value + index + 1
}

// 图片加载错误处理
const handleImageError = (event) => {
  console.log('图片加载失败:', event.target.src)
  // 可以设置默认头像或者其他处理
}

// 数据获取
const fetchUserData = async () => {
  tableLoading.value = true
  try {
    const response = await getUserList()
    userList.value = response.data || response || []
    ElMessage.success('用户数据加载成功')
  } catch (error) {
    console.error('获取用户数据失败:', error)
    ElMessage.error('获取用户数据失败')
  } finally {
    tableLoading.value = false
  }
}

const refreshUserData = () => {
  currentPage.value = 1
  fetchUserData()
}

// 搜索功能
const handleSearch = async () => {
  currentPage.value = 1
  if (searchKeyword.value.trim()) {
    try {
      const response = await getUserInfo(searchKeyword.value.trim())
      if (response.data) {
        // 如果找到特定用户，可以高亮显示或单独处理
        console.log('搜索到用户:', response.data)
      }
    } catch (error) {
      console.log('搜索用户失败:', error)
    }
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

const handleSortChange = ({ prop, order }) => {
  // 排序逻辑
  if (!order) {
    fetchUserData()
    return
  }
  
  userList.value.sort((a, b) => {
    const aVal = a[prop]
    const bVal = b[prop]
    
    if (order === 'ascending') {
      return aVal > bVal ? 1 : -1
    } else {
      return aVal < bVal ? 1 : -1
    }
  })
}

// 对话框操作
const openUserDialog = (mode, user = null) => {
  userDialogMode.value = mode
  
  // 重置表单数据
  if (mode === 'add') {
    userForm.value = {
      usersId: '',
      userName: '',
      realName: '',
      phoneNumber: '',
      gender: '',
      birthDate: '',
      avatarUrl: '',
      userStatus: 'active'
    }
    console.log('添加模式：重置表单')
  } else if (mode === 'edit' && user) {
    // 编辑模式：深拷贝用户数据，确保头像URL正确传递
    userForm.value = {
      usersId: user.usersId || '',
      userName: user.userName || '',
      realName: user.realName || '',
      phoneNumber: user.phoneNumber || '',
      gender: user.gender || '',
      birthDate: user.birthDate || '',
      avatarUrl: user.avatarUrl || '', // 确保头像URL正确赋值
      userStatus: user.userStatus || 'active'
    }
    
    // 调试信息：检查头像URL
    console.log('编辑模式 - 原始用户数据:', user)
    console.log('编辑模式 - 表单头像URL:', userForm.value.avatarUrl)
    
    // 确保头像URL立即可见
    nextTick(() => {
      console.log('nextTick - 表单头像URL:', userForm.value.avatarUrl)
    })
  }

  userDialogVisible.value = true
}

const handleDialogClose = () => {
  userDialogVisible.value = false
  // 不要在关闭时重置字段，保持数据状态
}

// 头像上传
const beforeAvatarUpload = (file) => {
  const isValidType = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'].includes(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isValidType) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleAvatarUpload = async (options) => {
  try {
    console.log('开始上传头像...')
    const response = await uploadImage(options.file)
    console.log('头像上传响应:', response)
    
    // 多种可能的响应结构处理
    let avatarUrl = ''
    if (response.data?.url) {
      avatarUrl = response.data.url
    } else if (response.data?.avatarUrl) {
      avatarUrl = response.data.avatarUrl
    } else if (response.url) {
      avatarUrl = response.url
    } else if (typeof response.data === 'string') {
      avatarUrl = response.data
    } else if (typeof response === 'string') {
      avatarUrl = response
    }
    
    if (avatarUrl) {
      userForm.value.avatarUrl = avatarUrl
      console.log('头像上传成功，URL:', avatarUrl)
      console.log('更新后的表单数据:', userForm.value)
      ElMessage.success('头像上传成功')
      
      // 强制重新渲染
      nextTick(() => {
        console.log('nextTick后的头像URL:', userForm.value.avatarUrl)
      })
    } else {
      console.error('上传响应结构异常:', response)
      throw new Error('上传响应中没有找到头像URL')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败: ' + (error.message || '未知错误'))
  }
}

const removeAvatar = () => {
  userForm.value.avatarUrl = ''
  console.log('头像已移除，当前avatarUrl:', userForm.value.avatarUrl)
  ElMessage.success('头像已移除')
}

// 用户提交
const handleUserSubmit = async () => {
  try {
    await userFormRef.value.validate()
    submitLoading.value = true
    
    // 确保提交的数据包含头像URL
    const submitData = {
      ...userForm.value
    }
    console.log('提交用户数据:', submitData)
    console.log('提交的头像URL:', submitData.avatarUrl)
      
    if (userDialogMode.value === 'add') {
      const result = await addUser(submitData)
      console.log('添加用户结果:', result)
      ElMessage.success('用户添加成功')
    } else {
      const result = await updateUser(submitData)
      console.log('更新用户结果:', result)
      ElMessage.success('用户更新成功')
    }

    handleDialogClose()
    fetchUserData()
  } catch (error) {
    console.error('用户操作失败:', error)
    ElMessage.error('操作失败，请重试: ' + (error.message || '未知错误'))
  } finally {
    submitLoading.value = false
  }
}

// 删除用户
const handleDeleteUser = async (userId, realName) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户"${realName}"吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )

    await deleteUser(userId)
    ElMessage.success('用户删除成功')
    fetchUserData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error('删除用户失败')
    }
  }
}

// 组件挂载
onMounted(() => {
  fetchUserData()
})
</script>

<style scoped>
.user-management {
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-title {
  font-size: 2.5rem;
  color: #2c3e50;
  margin: 0 0 12px 0;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #7f8c8d;
  margin: 0;
  font-weight: 500;
}

/* 操作卡片 */
.operation-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.operation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f1f2f6;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-left h3 {
  margin: 0 16px 0 0;
  color: #2c3e50;
  font-size: 1.3rem;
  font-weight: 600;
}

.count-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.9rem;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
}

/* 表格样式 */
.table-container {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  /* height: 80px; */
}

.user-avatar {
  border: 2px solid #e1e8ed;
  transition: all 0.3s ease;
}

.user-avatar:hover {
  border-color: #409eff;
  transform: scale(1.1);
}

.name-cell {
  display: flex;
  flex-direction: column;
}

.real-name {
  font-weight: 600;
  color: #2c3e50;
  font-size: 1rem;
}

.user-name {
  font-size: 0.85rem;
  color: #7f8c8d;
  margin-top: 2px;
}

.time-cell {
  display: flex;
  align-items: center;
  color: #7f8c8d;
}

.time-cell .el-icon {
  margin-right: 4px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
  background: #fafafa;
}

/* 对话框表单样式 */
.user-form {
  padding: 0 16px;
}

/* 头像上传样式 */
.avatar-upload {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.avatar-uploader .avatar-container {
  width: 120px;
  height: 120px;
  border-radius: 12px;
  border: 2px dashed #d9d9d9;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
}

.avatar-uploader .avatar-container:hover {
  border-color: #409eff;
  background-color: #f5f7fa;
}

.avatar-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 10px;
}

.avatar-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #8c939d;
}

.avatar-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.avatar-text {
  font-size: 14px;
}

.avatar-tips {
  flex: 1;
  color: #7f8c8d;
  font-size: 0.9rem;
}

.avatar-tips p {
  margin: 0 0 8px 0;
}

/* 对话框底部 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-management {
    padding: 16px;
  }

  .page-title {
    font-size: 2rem;
  }

  .operation-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .header-left,
  .header-right {
    justify-content: center;
  }

  .header-right {
    flex-direction: column;
    gap: 12px;
  }

  .avatar-upload {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }
}

/* 表格滚动条 */
.el-table :deep(.el-scrollbar__bar) {
  opacity: 0.6;
}

.el-table :deep(.el-scrollbar__thumb) {
  background-color: #c1c1c1;
  border-radius: 4px;
}
</style>