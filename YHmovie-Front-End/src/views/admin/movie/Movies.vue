<script setup>
  import { ref, reactive, onMounted, computed, nextTick } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { useRouter } from 'vue-router'
  import {
    Search,
    Plus,
    Refresh,
    Film,
    VideoCamera,
    Picture,
    View,
    Edit,
    Delete
  } from '@element-plus/icons-vue'
  
  // API 导入
  import {
    getMoviesList,
    addMovie,
    updateMovie,
    deleteMovie,
    getTypesList,
    uploadPost
  } from '@/api/movies'
  
  // 路由
  const router = useRouter()
  
  // 响应式数据
  const moviesList = ref([])
  const typesList = ref([])
  const tableLoading = ref(false)
  const submitLoading = ref(false)
  const totalCount = ref(0)
  
  // 分页相关
  const currentPage = ref(1)
  const pageSize = ref(20)
  
  // 搜索和筛选
  const searchKeyword = ref('')
  const selectedType = ref('')
  
  // 对话框相关
  const movieDialogVisible = ref(false)
  const movieDialogMode = ref('add')
  const movieFormRef = ref()
  
  // 电影表单数据
  const movieForm = reactive({
    moviesId: '',
    movieName: '',
    movieNameEn: '',
    director: '',
    movieBeginTime: '',
    movieEndTime: '',
    duration: 0,
    rating: 0,
    description: '',
    posterUrl: '',
    language: '',
    country: '',
    boxOffice: 0,
    typeNames: []
  })
  
  // 标签相关
  const selectedTypeNames = ref([])
  const newTag = ref('')
  
  // 表单验证规则
  const movieFormRules = {
    movieName: [
      { required: true, message: '请输入电影名称', trigger: 'blur' },
      { min: 1, max: 100, message: '电影名称长度在 1 到 100 个字符', trigger: 'blur' }
    ],
    movieNameEn: [
      { required: true, message: '请输入英文名称', trigger: 'blur' }
    ],
    director: [
      { required: true, message: '请输入导演姓名', trigger: 'blur' }
    ],
    rating: [
      { required: true, message: '请输入评分', trigger: 'blur' }
    ],
    language: [
      { required: true, message: '请选择语言', trigger: 'change' }
    ],
    country: [
      { required: true, message: '请选择国家', trigger: 'change' }
    ],
    movieBeginTime: [
      { required: true, message: '请选择上映时间', trigger: 'change' }
    ],
    movieEndTime: [
      { required: true, message: '请选择下映时间', trigger: 'change' }
    ],
    description: [
      { required: true, message: '请输入电影描述', trigger: 'blur' },
      { min: 10, max: 500, message: '描述长度在 10 到 500 个字符', trigger: 'blur' }
    ],
    posterUrl: [
      { required: true, message: '请上传电影海报', trigger: 'change' }
    ],
    duration: [
      { required: true, message: '请输入电影时长', trigger: 'blur' }
    ]
  }
  
  // 计算属性
  const filteredMoviesList = computed(() => {
    if (!selectedType.value) {
      return moviesList.value;
    }
    
    return moviesList.value.filter(movie => {
      if (!movie.typeNames) return false;
      return movie.typeNames.includes(selectedType.value);
    });
  })
  
  const availableTypes = computed(() => {
    return typesList.value.filter(type => !selectedTypeNames.value.includes(type.typeName))
  })
  
  // 工具函数
  const getTableIndex = (index) => {
    return (currentPage.value - 1) * pageSize.value + index + 1
  }
  
  const getMovieCountByType = (typeName) => {
    return moviesList.value.filter(movie => 
      movie.typeNames && movie.typeNames.includes(typeName)
    ).length
  }
  
  const getMovieStatusType = (beginTime) => {
    if (!beginTime) return 'info'
    
    const today = new Date()
    const movieDate = new Date(beginTime)
    return movieDate <= today ? 'success' : 'warning'
  }
  
  const getMovieStatusText = (beginTime) => {
    if (!beginTime) return '未知'
    
    const today = new Date()
    const movieDate = new Date(beginTime)
    return movieDate <= today ? '已上映' : '即将上映'
  }
  
  // 数据获取
  const fetchMoviesData = async () => {
    tableLoading.value = true
    try {
      const response = await getMoviesList(currentPage.value, pageSize.value, searchKeyword.value)
      moviesList.value = response.data?.rows || response.records || []
      totalCount.value = response.data?.total || response.total || 0
      
      // 确保类型是数组类型
      moviesList.value = moviesList.value.map(movie => {
        if (movie.typeNames && typeof movie.typeNames === 'string') {
          movie.typeNames = movie.typeNames.split(',').filter(Boolean)
        } else if (!Array.isArray(movie.typeNames)) {
          movie.typeNames = []
        }
        return movie
      })
      
      ElMessage.success('电影数据加载成功')
    } catch (error) {
      console.error('获取电影数据失败:', error)
      ElMessage.error('获取电影数据失败')
    } finally {
      tableLoading.value = false
    }
  }
  
  const fetchTypesData = async () => {
    try {
      const response = await getTypesList()
      typesList.value = response.data || response || []
    } catch (error) {
      console.error('获取类型数据失败:', error)
      ElMessage.error('获取类型数据失败')
    }
  }
  
  const refreshData = () => {
    currentPage.value = 1
    fetchMoviesData()
    fetchTypesData()
  }
  
  // 搜索功能
  const handleSearch = () => {
    currentPage.value = 1
    fetchMoviesData()
  }
  
  // 类型选择
  const selectType = (typeName) => {
    selectedType.value = selectedType.value === typeName ? '' : typeName
  }
  
  // 分页处理
  const handleSizeChange = (size) => {
    pageSize.value = size
    currentPage.value = 1
    fetchMoviesData()
  }
  
  const handleCurrentChange = (page) => {
    currentPage.value = page
    fetchMoviesData()
  }
  
  const handleSortChange = ({ prop, order }) => {
    // 排序逻辑，特别是对评分进行排序
    if (!order) {
      fetchMoviesData()
      return
    }
    
    // 针对评分排序
    if (prop === 'rating') {
      moviesList.value.sort((a, b) => {
        const aRating = Number(a.rating) || 0
        const bRating = Number(b.rating) || 0
        
        if (order === 'ascending') {
          return aRating - bRating
        } else {
          return bRating - aRating
        }
      })
    }
    // 可以根据需要添加其他排序逻辑
  }
  
  // 标签管理
  const addTag = () => {
    if (newTag.value && !selectedTypeNames.value.includes(newTag.value)) {
      selectedTypeNames.value.push(newTag.value)
      newTag.value = ''
    }
  }
  
  const removeTag = (tag) => {
    const index = selectedTypeNames.value.indexOf(tag)
    if (index > -1) {
      selectedTypeNames.value.splice(index, 1)
    }
  }
  
  // 对话框操作
  const openMovieDialog = (mode, movie = null) => {
    movieDialogMode.value = mode
    
    // 重置表单数据
    Object.assign(movieForm, {
      moviesId: '',
      movieName: '',
      movieNameEn: '',
      director: '',
      movieBeginTime: '',
      movieEndTime: '',
      duration: 0,
      rating: 0,
      description: '',
      posterUrl: '',
      language: '',
      country: '',
      boxOffice: 0,
      typeNames: []
    })
    
    selectedTypeNames.value = []
    
    if (mode === 'edit' && movie) {
      // 深拷贝避免直接修改原始数据
      const movieCopy = JSON.parse(JSON.stringify(movie))
      
      // 填充表单数据
      Object.keys(movieForm).forEach(key => {
        if (movieCopy[key] !== undefined) {
          movieForm[key] = movieCopy[key]
        }
      })
      
      // 处理类型数组
      if (Array.isArray(movieCopy.typeNames)) {
        selectedTypeNames.value = [...movieCopy.typeNames]
      } else if (typeof movieCopy.typeNames === 'string') {
        selectedTypeNames.value = movieCopy.typeNames.split(',').filter(Boolean)
      }
      
      console.log('编辑电影，表单数据:', movieForm)
      console.log('选中类型:', selectedTypeNames.value)
    }
    
    // 显示对话框
    movieDialogVisible.value = true
  }
  
  const handleDialogClose = () => {
    movieDialogVisible.value = false
    // 不需要重置，由于添加了destroy-on-close属性，组件会在关闭时销毁
  }
  
  // 海报上传
  const beforePosterUpload = (file) => {
    const isValidType = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'].includes(file.type)
    const isLt5M = file.size / 1024 / 1024 < 5
  
    if (!isValidType) {
      ElMessage.error('上传海报图片只能是 JPG/PNG 格式!')
      return false
    }
    if (!isLt5M) {
      ElMessage.error('上传海报图片大小不能超过 5MB!')
      return false
    }
    return true
  }
  
  const handlePosterUpload = async (options) => {
    try {
      submitLoading.value = true
      console.log('开始上传海报...')
      
      const response = await uploadPost(options.file)
      console.log('海报上传响应:', response)
      
      // 处理各种可能的响应格式
      let posterUrl = ''
      
      if (response.data?.url) {
        posterUrl = response.data.url
      } else if (response.url) {
        posterUrl = response.url
      } else if (typeof response.data === 'string') {
        posterUrl = response.data
      } else if (response.data) {
        posterUrl = response.data
      }
      
      if (posterUrl) {
        movieForm.posterUrl = posterUrl
        console.log('海报URL已设置:', posterUrl)
        ElMessage.success('海报上传成功')
        
        // 确保表单验证更新
        nextTick(() => {
          movieFormRef.value?.validateField('posterUrl')
        })
      } else {
        throw new Error('上传响应中没有找到海报URL')
      }
    } catch (error) {
      console.error('海报上传失败:', error)
      ElMessage.error('海报上传失败: ' + (error.message || '未知错误'))
    } finally {
      submitLoading.value = false
    }
  }
  
  const removePoster = () => {
    movieForm.posterUrl = ''
    ElMessage.success('海报已移除')
  }
  
  // 电影提交
  const handleMovieSubmit = async () => {
    try {
      await movieFormRef.value.validate()
      submitLoading.value = true
      
      console.log('提交表单数据前验证通过')
  
      // 将选中的类型名称数组赋值给表单的typeNames字段
      movieForm.typeNames = [...selectedTypeNames.value]
      
      const submitData = { ...movieForm }
      console.log('提交的电影数据:', submitData)
      
      if (movieDialogMode.value === 'add') {
        const result = await addMovie(submitData)
        console.log('添加电影结果:', result)
        ElMessage.success('电影添加成功')
      } else {
        const result = await updateMovie(submitData)
        console.log('更新电影结果:', result)
        ElMessage.success('电影更新成功')
      }
  
      handleDialogClose()
      refreshData()
    } catch (error) {
      console.error('电影操作失败:', error)
      ElMessage.error('操作失败，请重试: ' + (error.message || '表单验证未通过'))
    } finally {
      submitLoading.value = false
    }
  }
  
  // 查看电影
  const viewMovie = (movieId) => {
    router.push(`/users/movieInfo/${movieId}`)
  }
  
  // 删除电影
  const handleDeleteMovie = async (movieId, movieName) => {
    try {
      await ElMessageBox.confirm(
        `确定要删除电影"${movieName}"吗？此操作不可恢复！`,
        '删除确认',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning',
          confirmButtonClass: 'el-button--danger'
        }
      )
  
      await deleteMovie(movieId)
      ElMessage.success('电影删除成功')
      fetchMoviesData()
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除电影失败:', error)
        ElMessage.error('删除电影失败')
      }
    }
  }
  
  // 组件挂载
  onMounted(() => {
    refreshData()
  })
</script>

<template>
    <div class="movie-management">
      <!-- 页面标题 -->
  
      <!-- 主要内容区域 -->
      <el-card class="main-card" shadow="never">
        <!-- 搜索和操作区域 -->
        <div class="operation-header">
          <div class="header-left">
            <h3>电影列表</h3>
            <span class="count-badge">共 {{ totalCount }} 部电影</span>
          </div>
          
          <div class="header-right">
            <!-- 搜索框 -->
            <el-input
              v-model="searchKeyword"
              placeholder="请输入电影名搜索"
              style="width: 300px; margin-right: 16px;"
              clearable
              @input="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            
            <!-- 操作按钮 -->
            <el-button type="primary" icon="Plus" @click="openMovieDialog('add')">
              添加电影
            </el-button>
            <el-button icon="Refresh" @click="refreshData">
              刷新
            </el-button>
          </div>
        </div>
  
        <!-- 主体内容区域 -->
        <div class="content-main">
          <!-- 左侧类型列表 -->
          <el-card class="types-card" shadow="never">
            <template #header>
              <div class="card-header">
                <span>电影类型</span>
                <el-badge :value="typesList.length" type="primary" />
              </div>
            </template>
            
            <div class="types-list">
              <div 
                :class="['type-item', { active: selectedType === '' }]"
                @click="selectType('')"
              >
                <el-icon><Film /></el-icon>
                <span>全部类型</span>
                <el-badge :value="totalCount" type="info" />
              </div>
              
              <div 
                v-for="type in typesList" 
                :key="type.typesId"
                :class="['type-item', { active: selectedType === type.typeName }]"
                @click="selectType(type.typeName)"
              >
                <el-icon><VideoCamera /></el-icon>
                <span>{{ type.typeName }}</span>
                <el-badge :value="getMovieCountByType(type.typeName)" type="info" />
              </div>
              
              <el-empty v-if="typesList.length === 0" description="暂无类型数据" />
            </div>
          </el-card>
  
          <!-- 右侧电影表格 -->
          <el-card class="movies-card" shadow="never">
            <div class="table-container">
              <el-table
                :data="filteredMoviesList"
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
                
                <!-- 海报列 -->
                <el-table-column label="海报" width="120" align="center">
                  <template #default="{ row }">
                    <div class="poster-container">
                      <el-image
                        :src="row.posterUrl"
                        :preview-src-list="[row.posterUrl]"
                        fit="cover"
                        class="movie-poster"
                        lazy
                      >
                        <template #error>
                          <div class="image-slot">
                            <el-icon><Picture /></el-icon>
                          </div>
                        </template>
                      </el-image>
                    </div>
                  </template>
                </el-table-column>
                
                <!-- 电影名称列 -->
                <el-table-column
                  prop="movieName"
                  label="电影名称"
                  min-width="150"
                  sortable="custom"
                >
                  <template #default="{ row }">
                    <div class="movie-name">
                      <div class="name-cn">{{ row.movieName }}</div>
                      <div class="name-en">{{ row.movieNameEn }}</div>
                    </div>
                  </template>
                </el-table-column>
                
                <!-- 导演列 -->
                <el-table-column
                  prop="director"
                  label="导演"
                  width="120"
                  show-overflow-tooltip
                />
                
                <!-- 国家列 -->
                <el-table-column prop="country" label="国家" width="100" align="center">
                  <template #default="{ row }">
                    <el-tag type="info" size="small">
                      {{ row.country }}
                    </el-tag>
                  </template>
                </el-table-column>
                
                <!-- 语言列 -->
                <el-table-column prop="language" label="语言" width="100" align="center">
                  <template #default="{ row }">
                    <el-tag type="success" size="small">
                      {{ row.language }}
                    </el-tag>
                  </template>
                </el-table-column>
                
                <!-- 类型列 -->
                <el-table-column label="类型" min-width="150">
                  <template #default="{ row }">
                    <div class="type-tags">
                      <el-tag
                        v-for="typeName in (row.typeNames || [])"
                        :key="typeName"
                        size="small"
                        effect="plain"
                        style="margin-right: 4px; margin-bottom: 4px;"
                      >
                        {{ typeName }}
                      </el-tag>
                      <span v-if="!(row.typeNames?.length)">暂无类型</span>
                    </div>
                  </template>
                </el-table-column>
                
                <!-- 评分列 -->
                <el-table-column prop="rating" label="评分" width="180" align="center" sortable="custom">
                  <template #default="{ row }">
                    <div class="rating-cell">
                      <el-rate
                        :model-value="Number(row.rating) / 2"
                        disabled
                        show-score
                        text-color="#ff9900"
                        score-template="${value}"
                      />
                    </div>
                  </template>
                </el-table-column>
                
                <!-- 状态列 -->
                <el-table-column label="状态" width="100" align="center">
                  <template #default="{ row }">
                    <el-tag
                      :type="getMovieStatusType(row.movieBeginTime)"
                      size="small"
                    >
                      {{ getMovieStatusText(row.movieBeginTime) }}
                    </el-tag>
                  </template>
                </el-table-column>
                
                <!-- 操作列 -->
                <el-table-column label="操作" width="180" align="center" fixed="right">
                  <template #default="{ row }">
                    <div class="action-buttons">
                      <el-tooltip content="查看详情" placement="top">
                        <el-button
                          type="info"
                          icon="View"
                          size="small"
                          circle
                          @click="viewMovie(row.moviesId)"
                        />
                      </el-tooltip>
                      <el-tooltip content="编辑电影" placement="top">
                        <el-button
                          type="primary"
                          icon="Edit"
                          size="small"
                          circle
                          @click="openMovieDialog('edit', row)"
                        />
                      </el-tooltip>
                      <el-tooltip content="删除电影" placement="top">
                        <el-button
                          type="danger"
                          icon="Delete"
                          size="small"
                          circle
                          @click="handleDeleteMovie(row.moviesId, row.movieName)"
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
                  :total="totalCount"
                  layout="total, sizes, prev, pager, next, jumper"
                  @size-change="handleSizeChange"
                  @current-change="handleCurrentChange"
                />
              </div>
            </div>
          </el-card>
        </div>
      </el-card>
  
      <!-- 电影对话框 -->
      <el-dialog
        v-model="movieDialogVisible"
        :title="movieDialogMode === 'add' ? '添加电影' : '编辑电影'"
        width="800px"
        :before-close="handleDialogClose"
        :destroy-on-close="true"
      >
        <el-form
          ref="movieFormRef"
          :model="movieForm"
          :rules="movieFormRules"
          label-width="100px"
          class="movie-form"
        >
          <!-- 海报上传 -->
          <el-form-item label="电影海报" prop="posterUrl">
            <div class="poster-upload">
              <el-upload
                class="poster-uploader"
                :show-file-list="false"
                :before-upload="beforePosterUpload"
                :http-request="handlePosterUpload"
                accept="image/*"
              >
                <div class="poster-container-upload">
                  <img
                    v-if="movieForm.posterUrl"
                    :src="movieForm.posterUrl"
                    class="poster-preview"
                    alt="电影海报"
                  />
                  <div v-else class="poster-placeholder">
                    <el-icon class="poster-icon"><Plus /></el-icon>
                    <div class="poster-text">上传海报</div>
                  </div>
                </div>
              </el-upload>
              <div class="poster-tips">
                <p>支持 JPG、PNG 格式，建议尺寸 300x450 像素</p>
                <el-button
                  v-if="movieForm.posterUrl"
                  type="danger"
                  size="small"
                  text
                  @click="removePoster"
                >
                  <el-icon><Delete /></el-icon>
                  移除海报
                </el-button>
              </div>
            </div>
          </el-form-item>
  
          <el-row :gutter="20">
            <!-- 电影名称 -->
            <el-col :span="12">
              <el-form-item label="电影名称" prop="movieName">
                <el-input
                  v-model="movieForm.movieName"
                  placeholder="请输入电影名称"
                  clearable
                />
              </el-form-item>
            </el-col>
            
            <!-- 电影英文名 -->
            <el-col :span="12">
              <el-form-item label="英文名称" prop="movieNameEn">
                <el-input
                  v-model="movieForm.movieNameEn"
                  placeholder="请输入英文名称"
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>
  
          <el-row :gutter="20">
            <!-- 导演 -->
            <el-col :span="12">
              <el-form-item label="导演" prop="director">
                <el-input
                  v-model="movieForm.director"
                  placeholder="请输入导演姓名"
                  clearable
                />
              </el-form-item>
            </el-col>
            
            <!-- 评分 -->
            <el-col :span="12">
              <el-form-item label="评分" prop="rating">
                <el-input-number
                  v-model="movieForm.rating"
                  :min="0"
                  :max="10"
                  :step="0.1"
                  :precision="1"
                  placeholder="0-10分"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
  
          <el-row :gutter="20">
            <!-- 语言 -->
            <el-col :span="12">
              <el-form-item label="语言" prop="language">
                <el-select
                  v-model="movieForm.language"
                  placeholder="请选择语言"
                  style="width: 100%"
                >
                  <el-option label="中文" value="中文" />
                  <el-option label="英语" value="英语" />
                  <el-option label="日语" value="日语" />
                  <el-option label="韩语" value="韩语" />
                  <el-option label="法语" value="法语" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>
            </el-col>
            
            <!-- 国家 -->
            <el-col :span="12">
              <el-form-item label="国家" prop="country">
                <el-select
                  v-model="movieForm.country"
                  placeholder="请选择国家"
                  style="width: 100%"
                >
                  <el-option label="中国" value="中国" />
                  <el-option label="美国" value="美国" />
                  <el-option label="日本" value="日本" />
                  <el-option label="韩国" value="韩国" />
                  <el-option label="法国" value="法国" />
                  <el-option label="英国" value="英国" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
  
          <el-row :gutter="20">
            <!-- 开始时间 -->
            <el-col :span="12">
              <el-form-item label="上映时间" prop="movieBeginTime">
                <el-date-picker
                  v-model="movieForm.movieBeginTime"
                  type="date"
                  placeholder="请选择上映时间"
                  style="width: 100%"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-col>
            
            <!-- 结束时间 -->
            <el-col :span="12">
              <el-form-item label="下映时间" prop="movieEndTime">
                <el-date-picker
                  v-model="movieForm.movieEndTime"
                  type="date"
                  placeholder="请选择下映时间"
                  style="width: 100%"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-col>
          </el-row>
  
          <el-row :gutter="20">
            <!-- 时长 -->
            <el-col :span="12">
              <el-form-item label="时长(分钟)" prop="duration">
                <el-input-number
                  v-model="movieForm.duration"
                  :min="1"
                  :max="999"
                  style="width: 100%"
                  placeholder="请输入电影时长"
                />
              </el-form-item>
            </el-col>
            
            <!-- 票房 -->
            <el-col :span="12">
              <el-form-item label="票房(万元)" prop="boxOffice">
                <el-input-number
                  v-model="movieForm.boxOffice"
                  :min="0"
                  :precision="2"
                  :step="100"
                  style="width: 100%"
                  placeholder="请输入票房"
                />
              </el-form-item>
            </el-col>
          </el-row>
  
          <!-- 电影类型标签 -->
          <el-form-item label="电影类型" prop="typeNames">
            <div class="tags-container">
              <div class="selected-tags">
                <el-tag
                  v-for="typeName in selectedTypeNames"
                  :key="typeName"
                  closable
                  @close="removeTag(typeName)"
                  style="margin-right: 8px; margin-bottom: 8px;"
                >
                  {{ typeName }}
                </el-tag>
                <span v-if="!selectedTypeNames.length" class="no-tags">未选择任何类型</span>
              </div>
              <el-select
                v-model="newTag"
                placeholder="选择电影类型"
                style="width: 200px; margin-top: 10px;"
                @change="addTag"
                clearable
              >
                <el-option
                  v-for="type in availableTypes"
                  :key="type.typesId"
                  :label="type.typeName"
                  :value="type.typeName"
                />
              </el-select>
            </div>
          </el-form-item>
  
          <!-- 描述 -->
          <el-form-item label="电影描述" prop="description">
            <el-input
              v-model="movieForm.description"
              type="textarea"
              :rows="4"
              placeholder="请输入电影描述"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
  
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="handleDialogClose">取消</el-button>
            <el-button
              type="primary"
              @click="handleMovieSubmit"
              :loading="submitLoading"
            >
              {{ movieDialogMode === 'add' ? '添加' : '更新' }}
            </el-button>
          </div>
        </template>
      </el-dialog>
    </div>
</template>
  

  
<style scoped>
  /* 主容器样式 - 整体页面容器 */
  .movie-management {
    padding: 24px;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    min-height: 100vh;
  }
  
  /* 页面标题样式 - 页面顶部标题区域 */
  .page-header {
    text-align: center;
    margin-bottom: 32px;
  }
  
  /* 主标题文字样式 - 大标题 */
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
  
  /* 副标题文字样式 - 标题下方的描述文本 */
  .page-subtitle {
    font-size: 1.1rem;
    color: #7f8c8d;
    margin: 0;
    font-weight: 500;
  }
  
  /* 主卡片样式 - 整个内容区域的卡片容器 */
  .main-card {
    border-radius: 16px;
    border: none;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  }
  
  /* 操作头部样式 - 搜索栏和操作按钮所在区域 */
  .operation-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 2px solid #f1f2f6;
  }
  
  /* 头部左侧样式 - 标题和计数器 */
  .header-left {
    display: flex;
    align-items: center;
  }
  
  /* 头部标题样式 */
  .header-left h3 {
    margin: 0 16px 0 0;
    color: #2c3e50;
    font-size: 1.3rem;
    font-weight: 600;
  }
  
  /* 计数标签样式 - 显示电影总数的标签 */
  .count-badge {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 4px 12px;
    border-radius: 12px;
    font-size: 0.9rem;
    font-weight: 500;
  }
  
  /* 头部右侧样式 - 搜索框和按钮区域 */
  .header-right {
    display: flex;
    align-items: center;
  }
  
  /* 主体内容区域样式 - 左侧类型列表和右侧电影表格的布局 */
  .content-main {
    display: grid;
    grid-template-columns: 220px 1fr;
    gap: 24px;
    min-height: 600px;
  }
  
  /* 类型卡片样式 - 左侧类型列表的卡片容器 */
  .types-card {
    border-radius: 12px;
    border: none;
    box-shadow: 0 2px 12px rgba(81, 193, 237, 0.05);
  }
  
  /* 卡片头部样式 - 类型卡片的标题区域 */
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
    color: #2c3e50;
  }
  
  /* 类型列表样式 - 可滚动的类型列表容器 */
  .types-list {
    max-height: 500px;
    overflow-y: auto;
  }
  
  /* 类型项样式 - 每个类型的项目样式 */
  .type-item {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    margin-bottom: 8px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
    background: #f8f9fa;
  }
  
  /* 类型项悬停效果 */
  .type-item:hover {
    background: #e3f2fd;
    transform: translateX(4px);
  }
  
  /* 活动类型项样式 - 当前选中的类型 */
  .type-item.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
  }
  
  /* 类型项文本样式 */
  .type-item span {
    flex: 1;
    margin-left: 8px;
    font-weight: 500;
  }
  
  /* 电影卡片样式 - 右侧电影表格的卡片容器 */
  .movies-card {
    border-radius: 12px;
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  }
  
  /* 表格容器样式 - 电影列表表格的容器 */
  .table-container {
    background: white;
    border-radius: 12px;
    overflow: hidden;
  }
  
  /* 海报容器样式 - 表格中的电影海报容器 */
  .poster-container {
    width: 60px;
    height: 80px;
    border-radius: 8px;
    overflow: hidden;
    border: 2px solid #e1e8ed;
    transition: all 0.3s ease;
  }
  
  /* 海报容器悬停效果 */
  .poster-container:hover {
    border-color: #409eff;
    transform: scale(1.05);
  }
  
  /* 电影海报图片样式 */
  .movie-poster {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  /* 图片占位样式 - 当海报加载失败时显示的占位符 */
  .image-slot {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    background: #f5f7fa;
    color: #909399;
  }
  
  /* 电影名称样式 - 表格中电影名称的容器 */
  .movie-name {
    display: flex;
    flex-direction: column;
  }
  
  /* 中文名称样式 */
  .name-cn {
    font-weight: 600;
    color: #2c3e50;
    font-size: 1rem;
    margin-bottom: 4px;
  }
  
  /* 英文名称样式 */
  .name-en {
    font-size: 0.85rem;
    color: #7f8c8d;
  }
  
  /* 类型标签容器样式 - 表格中显示电影类型的容器 */
  .type-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
  }
  
  /* 评分单元格样式 - 表格中评分星级的容器 */
  .rating-cell {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  /* 操作按钮样式 - 表格中操作按钮的容器 */
  .action-buttons {
    display: flex;
    gap: 8px;
    justify-content: center;
  }
  
  /* 分页容器样式 - 表格底部分页器的容器 */
  .pagination-container {
    display: flex;
    justify-content: center;
    padding: 20px 0;
    background: #fafafa;
  }
  
  /* 对话框表单样式 - 添加/编辑电影对话框中的表单 */
  .movie-form {
    padding: 0 16px;
  }
  
  /* 海报上传样式 - 对话框中海报上传区域 */
  .poster-upload {
    display: flex;
    align-items: flex-start;
    gap: 20px;
  }
  
  /* 海报上传容器样式 */
  .poster-uploader .poster-container-upload {
    width: 180px;
    height: 240px;
    border-radius: 12px;
    border: 2px dashed #d9d9d9;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s ease;
    overflow: hidden;
  }
  
  /* 海报上传容器悬停效果 */
  .poster-uploader .poster-container-upload:hover {
    border-color: #409eff;
    background-color: #f5f7fa;
  }
  
  /* 海报预览图样式 */
  .poster-preview {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 10px;
  }
  
  /* 海报占位符样式 - 未上传海报时的占位元素 */
  .poster-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    color: #8c939d;
  }
  
  /* 海报图标样式 */
  .poster-icon {
    font-size: 48px;
    margin-bottom: 16px;
  }
  
  /* 海报文本样式 */
  .poster-text {
    font-size: 16px;
  }
  
  /* 海报提示样式 - 上传提示和操作按钮区域 */
  .poster-tips {
    flex: 1;
    color: #7f8c8d;
    font-size: 0.9rem;
  }
  
  /* 提示文本样式 */
  .poster-tips p {
    margin: 0 0 12px 0;
  }
  
  /* 标签容器样式 - 电影类型标签的容器 */
  .tags-container {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  
  /* 已选标签容器样式 */
  .selected-tags {
    min-height: 32px;
    padding: 8px;
    border: 1px solid #dcdfe6;
    border-radius: 6px;
    background: #f5f7fa;
  }
  
  /* 无标签提示样式 */
  .no-tags {
    color: #909399;
    font-style: italic;
  }
  
  /* 对话框底部样式 - 对话框底部按钮区域 */
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
  
  /* 响应式设计 - 大屏幕 */
  @media (max-width: 1200px) {
    .content-main {
      grid-template-columns: 240px 1fr;
      gap: 16px;
    }
  }
  
  /* 响应式设计 - 中小屏幕 */
  @media (max-width: 768px) {
    .movie-management {
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
  
    .content-main {
      grid-template-columns: 1fr;
      height: auto;
    }
  
    .types-card {
      order: 2;
    }
  
    .movies-card {
      order: 1;
    }
  
    .poster-upload {
      flex-direction: column;
      align-items: center;
      text-align: center;
    }
  
    .action-buttons {
      flex-direction: column;
      gap: 4px;
    }
  }
  
  /* 滚动条样式 - 类型列表的滚动条 */
  .types-list::-webkit-scrollbar {
    width: 6px;
  }
  
  .types-list::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
  }
  
  .types-list::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
  }
  
  .types-list::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
  }
  
  /* 表格滚动条样式 - Element Plus表格的滚动条 */
  .el-table :deep(.el-scrollbar__bar) {
    opacity: 0.6;
  }
  
  .el-table :deep(.el-scrollbar__thumb) {
    background-color: #c1c1c1;
    border-radius: 4px;
  }
</style>