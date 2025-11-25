<template>
  <div class="admin-dashboard">
    <!-- 页面标题 -->
  
    <!-- 主要统计数据 - 4个均匀分布的卡片 -->
    <div class="main-stats">
      <el-row :gutter="24">
        <el-col :span="6">
          <el-card class="stat-card movie-total" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="32"><VideoCamera /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ adminData.movieCount || 0 }}</div>
                <div class="stat-label">总电影数</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="stat-card movie-showing" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="32"><Star /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ adminData.movieShowingCount || 0 }}</div>
                <div class="stat-label">正在上映</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="stat-card movie-upcoming" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="32"><Clock /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ adminData.movieUpcomingCount || 0 }}</div>
                <div class="stat-label">即将上映</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card class="stat-card user-count" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon size="32"><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ adminData.userCount || 0 }}</div>
                <div class="stat-label">注册用户</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 次要统计数据 - 2个宽卡片 -->
    <div class="secondary-stats">
      <el-row :gutter="24">
        <el-col :span="12">
          <el-card class="wide-card cinema-card" shadow="hover">
            <div class="wide-content">
              <div class="wide-left">
                <div class="wide-icon">
                  <el-icon size="40"><OfficeBuilding /></el-icon>
                </div>
                <div class="wide-info">
                  <div class="wide-number">{{ adminData.cinemaCount || 0 }}</div>
                  <div class="wide-label">合作影院数</div>
                </div>
              </div>
              <div class="wide-right">
                <el-tag type="success" size="large">全部在线</el-tag>
                <div class="growth-text">
                  <el-icon><TrendCharts /></el-icon>
                  <span>持续增长</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="12">
          <el-card class="wide-card system-status" shadow="hover">
            <div class="wide-content">
              <div class="wide-left">
                <div class="wide-icon">
                  <el-icon size="40"><Monitor /></el-icon>
                </div>
                <div class="wide-info">
                  <div class="wide-number">99.9%</div>
                  <div class="wide-label">系统可用性</div>
                </div>
              </div>
              <div class="wide-right">
                <el-tag type="success" size="large">运行正常</el-tag>
                <div class="growth-text">
                  <el-icon><Check /></el-icon>
                  <span>稳定运行</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 系统信息 -->
    <div class="system-info-section">
      <el-card class="system-info-card" shadow="never">
        <template #header>
          <div class="info-header">
            <el-icon><Setting /></el-icon>
            <span>系统信息</span>
          </div>
        </template>
        <div class="system-grid">
          <div class="info-item">
            <span class="info-label">系统名称</span>
            <span class="info-value">{{ adminData.systemName || 'CineFlow 管理系统' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">服务器IP</span>
            <span class="info-value">{{ adminData.systemIP || '加载中...' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">最后更新</span>
            <span class="info-value">{{ currentTime }}</span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <el-card class="actions-card" shadow="never">
        <template #header>
          <div class="actions-header">
            <el-icon><Operation /></el-icon>
            <span>快捷操作</span>
          </div>
        </template>
        <div class="actions-grid">
          <el-button type="primary" size="large" @click="handleQuickAction('add-movie')">
            <el-icon><Plus /></el-icon>
            <span>添加电影</span>
          </el-button>
          <el-button type="success" size="large" @click="handleQuickAction('view-users')">
            <el-icon><View /></el-icon>
            <span>查看用户</span>
          </el-button>
          <el-button type="warning" size="large" @click="handleQuickAction('cinema-manage')">
            <el-icon><OfficeBuilding /></el-icon>
            <span>影院管理</span>
          </el-button>
          <el-button type="info" size="large" @click="handleQuickAction('reports')">
            <el-icon><Document /></el-icon>
            <span>生成报表</span>
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { 
  VideoCamera,
  Star,
  Clock,
  User, 
  OfficeBuilding, 
  Monitor, 
  Setting,
  Operation, 
  TrendCharts,
  Check,
  Plus,
  View,
  Document
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getAdminVOInfo } from '@/api/dashboard'

// 响应式数据
const adminData = ref({
  movieCount: 0,
  movieUpcomingCount: 0,
  movieShowingCount: 0,
  userCount: 0,
  cinemaCount: 0,
  systemName: '',
  systemIP: ''
})

const loading = ref(false)

// 计算当前时间
const currentTime = computed(() => {
  return new Date().toLocaleString('zh-CN')
})

// 获取管理员数据
const fetchAdminData = async () => {
  loading.value = true
  try {
    const response = await getAdminVOInfo()
    adminData.value = response.data || response
    ElMessage.success('数据加载成功')
  } catch (error) {
    console.error('获取管理员数据失败:', error)
    ElMessage.error('数据加载失败，请重试')
  } finally {
    loading.value = false
  }
}

// 快捷操作处理
const handleQuickAction = (action) => {
  const actions = {
    'add-movie': '添加电影',
    'view-users': '用户管理',
    'cinema-manage': '影院管理',
    'reports': '报表管理'
  }
  ElMessage.info(`跳转到${actions[action]}页面`)
}

// 组件挂载时获取数据
onMounted(() => {
  fetchAdminData()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 2.8rem;
  color: #2c3e50;
  margin: 0 0 12px 0;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 1.2rem;
  color: #7f8c8d;
  margin: 0;
  font-weight: 500;
}

/* 主要统计卡片 */
.main-stats {
  margin-bottom: 32px;
}

.stat-card {
  border-radius: 16px;
  border: none;
  transition: all 0.3s ease;
  height: 140px;
  background: white;
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 20px;
}

.stat-icon {
  margin-right: 20px;
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.movie-total .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.movie-showing .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.movie-upcoming .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.user-count .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 1rem;
  color: #7f8c8d;
  font-weight: 500;
}

/* 次要统计卡片 */
.secondary-stats {
  margin-bottom: 32px;
}

.wide-card {
  border-radius: 16px;
  border: none;
  height: 120px;
  background: white;
  transition: all 0.3s ease;
}

.wide-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
}

.wide-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 20px;
}

.wide-left {
  display: flex;
  align-items: center;
}

.wide-icon {
  margin-right: 20px;
  width: 72px;
  height: 72px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cinema-card .wide-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: white;
}

.system-status .wide-icon {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  color: #2c3e50;
}

.wide-info {
  flex: 1;
}

.wide-number {
  font-size: 2.2rem;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 8px;
}

.wide-label {
  font-size: 1rem;
  color: #7f8c8d;
  font-weight: 500;
}

.wide-right {
  text-align: right;
}

.growth-text {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 8px;
  color: #27ae60;
  font-size: 0.9rem;
  font-weight: 500;
}

.growth-text span {
  margin-left: 4px;
}

/* 系统信息 */
.system-info-section {
  margin-bottom: 32px;
}

.system-info-card {
  border-radius: 16px;
  background: white;
}

.info-header {
  display: flex;
  align-items: center;
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
}

.info-header span {
  margin-left: 8px;
}

.system-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
  padding: 8px 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #ecf0f1;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-weight: 600;
  color: #34495e;
  font-size: 1rem;
}

.info-value {
  color: #7f8c8d;
  font-size: 1rem;
  font-weight: 500;
}

/* 快捷操作 */
.quick-actions {
  margin-bottom: 32px;
}

.actions-card {
  border-radius: 16px;
  background: white;
}

.actions-header {
  display: flex;
  align-items: center;
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
}

.actions-header span {
  margin-left: 8px;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  padding: 8px 0;
}

.actions-grid .el-button {
  height: 56px;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.actions-grid .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.actions-grid .el-button span {
  margin-left: 8px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .main-stats .el-col {
    margin-bottom: 16px;
  }
}

@media (max-width: 768px) {
  .admin-dashboard {
    padding: 16px;
  }
  
  .page-title {
    font-size: 2.2rem;
  }
  
  .main-stats .el-row,
  .secondary-stats .el-row {
    display: flex;
    flex-direction: column;
  }
  
  .main-stats .el-col,
  .secondary-stats .el-col {
    width: 100%;
    margin-bottom: 16px;
  }
  
  .stat-content,
  .wide-content {
    padding: 16px;
  }
  
  .stat-number,
  .wide-number {
    font-size: 2rem;
  }
  
  .system-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .actions-grid {
    grid-template-columns: 1fr;
  }
  
  .stat-content {
    flex-direction: column;
    text-align: center;
  }
  
  .stat-icon {
    margin-right: 0;
    margin-bottom: 12px;
  }
}
</style>