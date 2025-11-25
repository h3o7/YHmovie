<template>
    <div class="location-management">
      <!-- 页面标题 -->
      
  
      <!-- 标签页切换 -->
      <el-tabs v-model="activeTab" type="card" class="management-tabs">
        <!-- 国家管理标签页 -->
        <el-tab-pane label="国家管理" name="country">
          <div class="tab-content">
            <div class="content-header">
              <div class="header-left">
                <h3>国家列表</h3>
                <span class="count-badge">共 {{ countryList.length }} 个国家</span>
              </div>
              <div class="header-right">
                <el-button type="primary" icon="Plus" @click="openCountryDialog('add')">
                  添加国家
                </el-button>
                <el-button icon="Refresh" @click="refreshCountryData">刷新</el-button>
              </div>
            </div>
  
            <!-- 国家列表 -->
            <el-card class="data-card" shadow="never">
              <div class="country-grid">
                <div 
                  v-for="country in countryList" 
                  :key="country.countrysId"
                  class="country-item"
                >
                  <div class="item-content">
                    <div class="item-info">
                      <h4 class="item-name">{{ country.countryName }}</h4>
                      <span class="item-time">{{ formatDate(country.createTime) }}</span>
                    </div>
                    <div class="item-actions">
                      <el-button 
                        type="primary" 
                        size="small" 
                        icon="Edit"
                        @click="openCountryDialog('edit', country)"
                      >
                        编辑
                      </el-button>
                      <el-button 
                        type="danger" 
                        size="small" 
                        icon="Delete"
                        @click="handleDeleteCountry(country.countrysId)"
                      >
                        删除
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
              
              <el-empty v-if="countryList.length === 0" description="暂无国家数据" />
            </el-card>
          </div>
        </el-tab-pane>
  
        <!-- 地点管理标签页 -->
        <el-tab-pane label="地点管理" name="location">
          <div class="tab-content">
            <div class="content-header">
              <div class="header-left">
                <h3>地点管理</h3>
                <span class="count-badge">共 {{ locationList.length }} 个城市</span>
              </div>
              <div class="header-right">
                <el-button type="primary" icon="Plus" @click="openLocationDialog('add')">
                  添加地点
                </el-button>
                <el-button icon="Refresh" @click="refreshLocationData">刷新</el-button>
              </div>
            </div>
  
            <!-- 地点管理主体 -->
            <div class="location-main">
              <!-- 左侧省份列表 -->
              <el-card class="province-card" shadow="never">
                <template #header>
                  <div class="card-header">
                    <span>省份列表</span>
                    <el-badge :value="uniqueProvinces.length" type="primary" />
                  </div>
                </template>
                
                <div class="province-list">
                  <div 
                    v-for="province in uniqueProvinces" 
                    :key="province"
                    :class="['province-item', { active: selectedProvince === province }]"
                    @click="selectProvince(province)"
                  >
                    <el-icon><Location /></el-icon>
                    <span>{{ province }}</span>
                    <el-badge :value="getCityCountByProvince(province)" type="info" />
                  </div>
                  
                  <el-empty v-if="uniqueProvinces.length === 0" description="暂无省份数据" />
                </div>
              </el-card>
  
              <!-- 右侧城市列表 -->
              <el-card class="city-card" shadow="never">
                <template #header>
                  <div class="card-header">
                    <span>{{ selectedProvince || '全部' }}城市</span>
                    <el-badge :value="filteredCities.length" type="primary" />
                  </div>
                </template>
                
                <div class="city-grid">
                  <div 
                    v-for="location in filteredCities" 
                    :key="location.locationsId"
                    class="city-item"
                  >
                    <div class="city-info">
                      <h4 class="city-name">{{ location.cityName }}</h4>
                      <span class="city-province">{{ location.provinceName }}</span>
                    </div>
                    <div class="city-actions">
                      <el-button 
                        type="primary" 
                        size="small" 
                        icon="Edit"
                        @click="openLocationDialog('edit', location)"
                      >
                        编辑
                      </el-button>
                      <el-button 
                        type="danger" 
                        size="small" 
                        icon="Delete"
                        @click="handleDeleteLocation(location.locationsId)"
                      >
                        删除
                      </el-button>
                    </div>
                  </div>
                </div>
                
                <el-empty v-if="filteredCities.length === 0" description="该省份暂无城市数据" />
              </el-card>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
  
      <!-- 国家对话框 -->
      <el-dialog 
        v-model="countryDialogVisible" 
        :title="countryDialogMode === 'add' ? '添加国家' : '编辑国家'"
        width="500px"
        :before-close="handleCountryDialogClose"
      >
        <el-form 
          ref="countryFormRef" 
          :model="countryForm" 
          :rules="countryRules" 
          label-width="100px"
        >
          <el-form-item 
            v-if="countryDialogMode === 'edit'" 
            label="国家ID" 
            prop="countrysId"
          >
            <el-input 
              v-model="countryForm.countrysId" 
              disabled 
              placeholder="系统自动生成"
            />
          </el-form-item>
          <el-form-item label="国家名称" prop="countryName">
            <el-input 
              v-model="countryForm.countryName" 
              placeholder="请输入国家名称"
              clearable
            />
          </el-form-item>
        </el-form>
        
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="handleCountryDialogClose">取消</el-button>
            <el-button 
              type="primary" 
              @click="handleCountrySubmit"
              :loading="countrySubmitLoading"
            >
              {{ countryDialogMode === 'add' ? '添加' : '更新' }}
            </el-button>
          </div>
        </template>
      </el-dialog>
  
      <!-- 地点对话框 -->
      <el-dialog 
        v-model="locationDialogVisible" 
        :title="locationDialogMode === 'add' ? '添加地点' : '编辑地点'"
        width="500px"
        :before-close="handleLocationDialogClose"
      >
        <el-form 
          ref="locationFormRef" 
          :model="locationForm" 
          :rules="locationRules" 
          label-width="100px"
        >
          <el-form-item 
            v-if="locationDialogMode === 'edit'" 
            label="地点ID" 
            prop="locationsId"
          >
            <el-input 
              v-model="locationForm.locationsId" 
              disabled 
              placeholder="系统自动生成"
            />
          </el-form-item>
          <el-form-item label="省份名称" prop="provinceName">
            <el-select 
              v-model="locationForm.provinceName" 
              placeholder="请选择省份"
              filterable
              clearable
              style="width: 100%"
              @change="handleProvinceChange"
            >
              <el-option 
                v-for="provinceOption in provinceOptionsWithId" 
                :key="provinceOption.value" 
                :label="provinceOption.label" 
                :value="provinceOption.value"
                :data-id="provinceOption.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="城市名称" prop="cityName">
            <el-input 
              v-model="locationForm.cityName" 
              placeholder="请输入城市名称"
              clearable
            />
          </el-form-item>
          
          <!-- 显示省份ID（调试用，也可以隐藏） -->
          <el-form-item 
            v-if="locationForm.provincesId" 
            label="省份ID"
          >
            <el-input 
              v-model="locationForm.provincesId" 
              disabled 
              placeholder="自动获取"
            />
          </el-form-item>
        </el-form>
        
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="handleLocationDialogClose">取消</el-button>
            <el-button 
              type="primary" 
              @click="handleLocationSubmit"
              :loading="locationSubmitLoading"
            >
              {{ locationDialogMode === 'add' ? '添加' : '更新' }}
            </el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, computed, nextTick, watch } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { Plus, Edit, Delete, Refresh, Location } from '@element-plus/icons-vue'
  
  // API 导入
  import { 
    getCountryList, 
    deleteCountry, 
    addCountry, 
    updateCountry 
  } from '@/api/countrys'
  import { 
    getLocationList, 
    deleteLocation, 
    addLocation, 
    updateLocation 
  } from '@/api/locations'
  
  // 响应式数据
  const activeTab = ref('country')
  const countryList = ref([])
  const locationList = ref([])
  const selectedProvince = ref('')
  
  // 国家对话框相关
  const countryDialogVisible = ref(false)
  const countryDialogMode = ref('add')
  const countrySubmitLoading = ref(false)
  const countryFormRef = ref()
  const countryForm = ref({
    countrysId: '',
    countryName: ''
  })
  
  // 地点对话框相关
  const locationDialogVisible = ref(false)
  const locationDialogMode = ref('add')
  const locationSubmitLoading = ref(false)
  const locationFormRef = ref()
  const locationForm = ref({
    locationsId: '',
    provincesId: '',
    provinceName: '',
    cityName: ''
  })
  
  // 表单验证规则
  const countryRules = {
    countryName: [
      { required: true, message: '请输入国家名称', trigger: 'blur' },
      { min: 2, max: 50, message: '国家名称长度在 2 到 50 个字符', trigger: 'blur' }
    ]
  }
  
  const locationRules = {
    provinceName: [
      { required: true, message: '请选择省份名称', trigger: 'change' }
    ],
    cityName: [
      { required: true, message: '请输入城市名称', trigger: 'blur' },
      { min: 2, max: 50, message: '城市名称长度在 2 到 50 个字符', trigger: 'blur' }
    ]
  }
  
  // 计算属性
  const uniqueProvinces = computed(() => {
    const provinces = locationList.value.map(item => item.provinceName)
    return [...new Set(provinces)].filter(Boolean)
  })
  
  const filteredCities = computed(() => {
    if (!selectedProvince.value) {
      return locationList.value
    }
    return locationList.value.filter(item => item.provinceName === selectedProvince.value)
  })
  
  // 修改省份选项，包含ID信息
  const provinceOptionsWithId = computed(() => {
    // 从现有地点数据中提取省份和对应的ID
    const existingProvinces = locationList.value.reduce((acc, location) => {
      if (location.provinceName && !acc.find(p => p.value === location.provinceName)) {
        acc.push({
          label: location.provinceName,
          value: location.provinceName,
          id: location.provincesId || generateProvinceId(location.provinceName)
        })
      }
      return acc
    }, [])
  
    // 预设的常用省份列表
    const commonProvinces = [
      { label: '北京市', value: '北京市', id: 'BJ' },
      { label: '上海市', value: '上海市', id: 'SH' },
      { label: '天津市', value: '天津市', id: 'TJ' },
      { label: '重庆市', value: '重庆市', id: 'CQ' },
      { label: '河北省', value: '河北省', id: 'HB' },
      { label: '山西省', value: '山西省', id: 'SX' },
      { label: '辽宁省', value: '辽宁省', id: 'LN' },
      { label: '吉林省', value: '吉林省', id: 'JL' },
      { label: '黑龙江省', value: '黑龙江省', id: 'HLJ' },
      { label: '江苏省', value: '江苏省', id: 'JS' },
      { label: '浙江省', value: '浙江省', id: 'ZJ' },
      { label: '安徽省', value: '安徽省', id: 'AH' },
      { label: '福建省', value: '福建省', id: 'FJ' },
      { label: '江西省', value: '江西省', id: 'JX' },
      { label: '山东省', value: '山东省', id: 'SD' },
      { label: '河南省', value: '河南省', id: 'HN' },
      { label: '湖北省', value: '湖北省', id: 'HuB' },
      { label: '湖南省', value: '湖南省', id: 'HuN' },
      { label: '广东省', value: '广东省', id: 'GD' },
      { label: '海南省', value: '海南省', id: 'HaiN' },
      { label: '四川省', value: '四川省', id: 'SC' },
      { label: '贵州省', value: '贵州省', id: 'GZ' },
      { label: '云南省', value: '云南省', id: 'YN' },
      { label: '陕西省', value: '陕西省', id: 'ShaanX' },
      { label: '甘肃省', value: '甘肃省', id: 'GS' },
      { label: '青海省', value: '青海省', id: 'QH' },
      { label: '内蒙古自治区', value: '内蒙古自治区', id: 'NMG' },
      { label: '广西壮族自治区', value: '广西壮族自治区', id: 'GX' },
      { label: '西藏自治区', value: '西藏自治区', id: 'XZ' },
      { label: '宁夏回族自治区', value: '宁夏回族自治区', id: 'NX' },
      { label: '新疆维吾尔自治区', value: '新疆维吾尔自治区', id: 'XJ' },
      { label: '香港特别行政区', value: '香港特别行政区', id: 'HK' },
      { label: '澳门特别行政区', value: '澳门特别行政区', id: 'MO' },
      { label: '台湾省', value: '台湾省', id: 'TW' }
    ]
  
    // 合并现有省份和预设省份，去重
    const allProvinces = [...existingProvinces]
    
    commonProvinces.forEach(common => {
      if (!allProvinces.find(existing => existing.value === common.value)) {
        allProvinces.push(common)
      }
    })
  
    return allProvinces.sort((a, b) => a.label.localeCompare(b.label))
  })
  
  // 工具函数
  const formatDate = (dateString) => {
    if (!dateString) return '-'
    const date = new Date(dateString)
    return date.toLocaleDateString('zh-CN')
  }
  
  const getCityCountByProvince = (province) => {
    return locationList.value.filter(item => item.provinceName === province).length
  }
  
  // 生成省份ID的辅助函数（如果后端没有提供的话）
  const generateProvinceId = (provinceName) => {
    // 简单的省份名称到ID的映射
    const provinceIdMap = {
      '北京市': 'BJ',
      '上海市': 'SH',
      '天津市': 'TJ',
      '重庆市': 'CQ',
      '河北省': 'HB',
      '山西省': 'SX',
      '辽宁省': 'LN',
      '吉林省': 'JL',
      '黑龙江省': 'HLJ',
      '江苏省': 'JS',
      '浙江省': 'ZJ',
      '安徽省': 'AH',
      '福建省': 'FJ',
      '江西省': 'JX',
      '山东省': 'SD',
      '河南省': 'HN',
      '湖北省': 'HuB',
      '湖南省': 'HuN',
      '广东省': 'GD',
      '海南省': 'HaiN',
      '四川省': 'SC',
      '贵州省': 'GZ',
      '云南省': 'YN',
      '陕西省': 'ShaanX',
      '甘肃省': 'GS',
      '青海省': 'QH',
      '内蒙古自治区': 'NMG',
      '广西壮族自治区': 'GX',
      '西藏自治区': 'XZ',
      '宁夏回族自治区': 'NX',
      '新疆维吾尔自治区': 'XJ',
      '香港特别行政区': 'HK',
      '澳门特别行政区': 'MO',
      '台湾省': 'TW'
    }
    
    return provinceIdMap[provinceName] || provinceName.substring(0, 2).toUpperCase()
  }
  
  // 处理省份选择变化
  const handleProvinceChange = (provinceName) => {
    if (provinceName) {
      // 从选项中找到对应的省份ID
      const selectedOption = provinceOptionsWithId.value.find(option => option.value === provinceName)
      if (selectedOption) {
        locationForm.value.provincesId = selectedOption.id
        console.log(`选择省份: ${provinceName}, 省份ID: ${selectedOption.id}`)
      }
    } else {
      // 清空省份ID
      locationForm.value.provincesId = ''
    }
  }
  
  // 数据获取方法
  const fetchCountryData = async () => {
    try {
      const response = await getCountryList()
      countryList.value = response.data || response || []
      ElMessage.success('国家数据加载成功')
    } catch (error) {
      console.error('获取国家数据失败:', error)
      ElMessage.error('获取国家数据失败')
    }
  }
  
  const fetchLocationData = async () => {
    try {
      const response = await getLocationList()
      locationList.value = response.data || response || []
      ElMessage.success('地点数据加载成功')
    } catch (error) {
      console.error('获取地点数据失败:', error)
      ElMessage.error('获取地点数据失败')
    }
  }
  
  const refreshCountryData = () => {
    fetchCountryData()
  }
  
  const refreshLocationData = () => {
    fetchLocationData()
    selectedProvince.value = ''
  }
  
  // 省份选择
  const selectProvince = (province) => {
    selectedProvince.value = selectedProvince.value === province ? '' : province
  }
  
  // 国家管理方法
  const openCountryDialog = (mode, country = null) => {
    countryDialogMode.value = mode
    if (mode === 'add') {
      countryForm.value = {
        countrysId: '',
        countryName: ''
      }
    } else {
      countryForm.value = { ...country }
    }
    countryDialogVisible.value = true
  }
  
  const handleCountryDialogClose = () => {
    countryDialogVisible.value = false
    nextTick(() => {
      countryFormRef.value?.resetFields()
    })
  }
  
  const handleCountrySubmit = async () => {
    try {
      await countryFormRef.value.validate()
      countrySubmitLoading.value = true
      
      if (countryDialogMode.value === 'add') {
        await addCountry(countryForm.value.countryName)
        ElMessage.success('国家添加成功')
      } else {
        await updateCountry(countryForm.value)
        ElMessage.success('国家更新成功')
      }
      
      handleCountryDialogClose()
      fetchCountryData()
    } catch (error) {
      console.error('国家操作失败:', error)
      ElMessage.error('操作失败，请重试')
    } finally {
      countrySubmitLoading.value = false
    }
  }
  
  const handleDeleteCountry = async (countrysId) => {
    try {
      await ElMessageBox.confirm('确定要删除这个国家吗？', '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      await deleteCountry(countrysId)
      ElMessage.success('国家删除成功')
      fetchCountryData()
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除国家失败:', error)
        ElMessage.error('删除失败，请重试')
      }
    }
  }
  
  // 地点管理方法
  const openLocationDialog = (mode, location = null) => {
    locationDialogMode.value = mode
    if (mode === 'add') {
      locationForm.value = {
        locationsId: '',
        provincesId: '',
        provinceName: '',
        cityName: ''
      }
    } else {
      locationForm.value = { ...location }
    }
    locationDialogVisible.value = true
  }
  
  const handleLocationDialogClose = () => {
    locationDialogVisible.value = false
    nextTick(() => {
      locationFormRef.value?.resetFields()
    })
  }
  
  const handleLocationSubmit = async () => {
    try {
      await locationFormRef.value.validate()
      locationSubmitLoading.value = true
      
      // 确保提交时包含省份ID
      const submitData = {
        ...locationForm.value
      }
      
      console.log('提交的地点数据:', submitData)
      
      if (locationDialogMode.value === 'add') {
        await addLocation(submitData)
        ElMessage.success('地点添加成功')
      } else {
        await updateLocation(submitData)
        ElMessage.success('地点更新成功')
      }
      
      handleLocationDialogClose()
      fetchLocationData()
    } catch (error) {
      console.error('地点操作失败:', error)
      ElMessage.error('操作失败，请重试')
    } finally {
      locationSubmitLoading.value = false
    }
  }
  
  const handleDeleteLocation = async (locationsId) => {
    try {
      await ElMessageBox.confirm('确定要删除这个地点吗？', '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      await deleteLocation(locationsId)
      ElMessage.success('地点删除成功')
      fetchLocationData()
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除地点失败:', error)
        ElMessage.error('删除失败，请重试')
      }
    }
  }
  
  // 组件挂载时初始化数据
  onMounted(() => {
    fetchCountryData()
    fetchLocationData()
  })
  </script>
  
  <style scoped>
  .location-management {
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
  
  /* 标签页样式 */
  .management-tabs {
    background: white;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  }
  
  .management-tabs :deep(.el-tabs__header) {
    margin-bottom: 32px;
  }
  
  .management-tabs :deep(.el-tabs__item) {
    font-size: 1.1rem;
    font-weight: 600;
    padding: 0 32px;
    height: 48px;
    line-height: 48px;
  }
  
  /* 内容区域 */
  .tab-content {
    min-height: 600px;
  }
  
  .content-header {
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
    gap: 12px;
  }
  
  /* 数据卡片 */
  .data-card {
    border-radius: 12px;
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  }
  
  /* 国家网格 */
  .country-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 16px;
    min-height: 200px;
  }
  
  .country-item {
    background: #f8f9fa;
    border-radius: 8px;
    padding: 20px;
    transition: all 0.3s ease;
    border: 2px solid transparent;
  }
  
  .country-item:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
    border-color: #409eff;
  }
  
  .item-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .item-info {
    flex: 1;
  }
  
  .item-name {
    margin: 0 0 8px 0;
    color: #2c3e50;
    font-size: 1.1rem;
    font-weight: 600;
  }
  
  .item-time {
    color: #7f8c8d;
    font-size: 0.9rem;
  }
  
  .item-actions {
    display: flex;
    gap: 8px;
  }
  
  /* 地点管理主体 */
  .location-main {
    display: grid;
    grid-template-columns: 300px 1fr;
    gap: 24px;
    height: 600px;
  }
  
  /* 省份卡片 */
  .province-card {
    border-radius: 12px;
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
    color: #2c3e50;
  }
  
  .province-list {
    max-height: 500px;
    overflow-y: auto;
  }
  
  .province-item {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    margin-bottom: 8px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
    background: #f8f9fa;
  }
  
  .province-item:hover {
    background: #e3f2fd;
    transform: translateX(4px);
  }
  
  .province-item.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
  }
  
  .province-item span {
    flex: 1;
    margin-left: 8px;
    font-weight: 500;
  }
  
  /* 城市卡片 */
  .city-card {
    border-radius: 12px;
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  }
  
  .city-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 16px;
    max-height: 500px;
    overflow-y: auto;
    padding-right: 8px;
  }
  
  .city-item {
    background: #f8f9fa;
    border-radius: 8px;
    padding: 16px;
    transition: all 0.3s ease;
    border: 2px solid transparent;
  }
  
  .city-item:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
    border-color: #67c23a;
  }
  
  .city-info {
    margin-bottom: 12px;
  }
  
  .city-name {
    margin: 0 0 4px 0;
    color: #2c3e50;
    font-size: 1.1rem;
    font-weight: 600;
  }
  
  .city-province {
    color: #7f8c8d;
    font-size: 0.9rem;
  }
  
  .city-actions {
    display: flex;
    gap: 8px;
  }
  
  /* 对话框 */
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
  
  /* 响应式设计 */
  @media (max-width: 1200px) {
    .location-main {
      grid-template-columns: 250px 1fr;
      gap: 16px;
    }
    
    .country-grid {
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    }
    
    .city-grid {
      grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    }
  }
  
  @media (max-width: 768px) {
    .location-management {
      padding: 16px;
    }
    
    .page-title {
      font-size: 2rem;
    }
    
    .content-header {
      flex-direction: column;
      align-items: stretch;
      gap: 16px;
    }
    
    .header-left,
    .header-right {
      justify-content: center;
    }
    
    .location-main {
      grid-template-columns: 1fr;
      height: auto;
    }
    
    .province-card {
      order: 2;
    }
    
    .city-card {
      order: 1;
    }
    
    .country-grid,
    .city-grid {
      grid-template-columns: 1fr;
    }
    
    .management-tabs {
      padding: 16px;
    }
    
    .item-content {
      flex-direction: column;
      align-items: stretch;
      gap: 12px;
    }
    
    .item-actions {
      justify-content: center;
    }
  }
  
  /* 滚动条样式 */
  .province-list::-webkit-scrollbar,
  .city-grid::-webkit-scrollbar {
    width: 6px;
  }
  
  .province-list::-webkit-scrollbar-track,
  .city-grid::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
  }
  
  .province-list::-webkit-scrollbar-thumb,
  .city-grid::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
  }
  
  .province-list::-webkit-scrollbar-thumb:hover,
  .city-grid::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
  }
  </style>