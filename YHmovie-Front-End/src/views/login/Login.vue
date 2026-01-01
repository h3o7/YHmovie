<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
      <div class="floating-elements">
        <div class="float-element" v-for="n in 8" :key="n"></div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 系统标题区域 -->
      <div class="header-section">
        <div class="logo-container">
          <div class="deer-logo">
            <svg class="deer-svg" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2" fill="none"
                stroke="#ffffff" stroke-width="1" />
              <path d="M14.5 3a1.5 1.5 0 0 1 1.5 1.5V5h-8v-.5A1.5 1.5 0 0 1 9.5 3h5z" fill="#ffffff" />
              <path d="M12 11a2 2 0 1 0 0-4 2 2 0 0 0 0 4z" fill="#ffffff" />
              <path d="M8 16v-2a4 4 0 0 1 8 0v2" fill="none" stroke="#ffffff" stroke-width="1" />
            </svg>
          </div>
          <h1 class="system-title">YH电影网</h1>
          <p class="system-subtitle">您的私人观影助手</p>
        </div>
      </div>

      <!-- 登录卡片 -->
      <div class="login-card">
        <el-card shadow="hover" class="login-form-card">
          <!-- 登录标题 -->
          <div class="login-title">
            <h2>{{ loginType === 'user' ? '用户登录' : '管理员登录' }}</h2>
            <p>欢迎回来，请登录您的账户</p>
          </div>

          <!-- 登录类型切换 -->
          <div class="login-type-switch">
            <el-segmented v-model="loginType" :options="loginOptions" size="large" />
          </div>

          <!-- 用户登录表单 -->
          <el-form v-if="loginType === 'user'" ref="userFormRef" :model="userForm" :rules="userRules" class="login-form"
            label-position="top">
            <el-form-item label="用户账号" prop="id">
              <el-input v-model="userForm.id" placeholder="请输入用户账号" size="large" clearable>
                <template #prefix>
                  <el-icon>
                    <User />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input v-model="userForm.password" type="password" placeholder="请输入密码" size="large" show-password
                clearable>
                <template #prefix>
                  <el-icon>
                    <Lock />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="验证码" prop="captchaCode">
              <div class="captcha-container">
                <el-input v-model="userForm.captchaCode" placeholder="请输入验证码" size="large" clearable
                  class="captcha-input">
                  <template #prefix>
                    <el-icon>
                      <Key />
                    </el-icon>
                  </template>
                </el-input>
                <div class="captcha-image" @click="refreshCaptcha">
                  <img v-if="captchaUrl" :src="captchaUrl" alt="验证码" class="captcha-img" />
                  <div v-else class="captcha-placeholder">
                    <span v-if="captchaLoading"><el-icon>
                        <Loading />
                      </el-icon> 加载中...</span>
                    <span v-else>点击获取验证码</span>
                  </div>
                </div>
              </div>
            </el-form-item>

            <div class="form-footer">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <div class="link-group">
                <el-link type="primary" :underline="false" @click="router.push('/register')">注册账号</el-link>
                <span class="divider">|</span>
                <el-link type="primary" :underline="false">忘记密码?</el-link>
              </div>
            </div>

            <el-button type="primary" size="large" class="login-button" :loading="loading" @click="handleUserLogin">
              <span v-if="!loading">登录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form>

          <!-- 管理员登录表单 -->
          <el-form v-else ref="adminFormRef" :model="adminForm" :rules="adminRules" class="login-form"
            label-position="top">
            <el-form-item label="管理员账号" prop="id">
              <el-input v-model="adminForm.id" placeholder="请输入管理员账号" size="large" clearable>
                <template #prefix>
                  <el-icon>
                    <UserFilled />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input v-model="adminForm.password" type="password" placeholder="请输入密码" size="large" show-password
                clearable>
                <template #prefix>
                  <el-icon>
                    <Lock />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="验证码" prop="captchaCode">
              <div class="captcha-container">
                <el-input v-model="adminForm.captchaCode" placeholder="请输入验证码" size="large" clearable
                  class="captcha-input">
                  <template #prefix>
                    <el-icon>
                      <Key />
                    </el-icon>
                  </template>
                </el-input>
                <div class="captcha-image" @click="refreshCaptcha">
                  <img v-if="captchaUrl" :src="captchaUrl" alt="验证码" class="captcha-img" />
                  <div v-else class="captcha-placeholder">
                    <span v-if="captchaLoading"><el-icon>
                        <Loading />
                      </el-icon> 加载中...</span>
                    <span v-else>点击获取验证码</span>
                  </div>
                </div>
              </div>
            </el-form-item>

            <div class="form-footer">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <div></div> <!-- 为了保持布局一致 -->
            </div>

            <el-button type="primary" size="large" class="login-button" :loading="loading" @click="handleAdminLogin">
              <span v-if="!loading">登录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form>

          <!-- 分割线 -->
          <el-divider>
            <span class="divider-text">其他方式</span>
          </el-divider>

          <!-- 快捷登录 -->
          <div class="quick-login">
            <el-button circle size="large" @click="handleQuickLogin('wechat')">
              <el-icon>
                <ChatDotRound />
              </el-icon>
            </el-button>
            <el-button circle size="large" @click="handleQuickLogin('phone')">
              <el-icon>
                <Iphone />
              </el-icon>
            </el-button>
            <el-button circle size="large" @click="handleQuickLogin('email')">
              <el-icon>
                <Message />
              </el-icon>
            </el-button>
          </div>
        </el-card>
      </div>

      <!-- 页脚 -->
      <div class="footer">
        <p>© {{ new Date().getFullYear() }} YH电影网 - 版权所有</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  User,
  UserFilled,
  Lock,
  Key,
  ChatDotRound,
  Iphone,
  Message,
  Loading
} from '@element-plus/icons-vue'
import axios from 'axios'
import { userLogin, adminLogin } from '@/api/login'
import {deleteShowtimes} from '@/api/showtimes'
import { useRouter } from 'vue-router'

const router = useRouter()

// 登录类型
const loginType = ref('user')
const loginOptions = [
  { label: '用户登录', value: 'user' },
  { label: '管理员登录', value: 'admin' }
]

// 验证码数据
const captchaId = ref('')
const captchaUrl = ref('')
const captchaLoading = ref(false)

// 表单数据
const userForm = ref({
  id: '',
  password: '',
  captchaId: '',
  captchaCode: ''
})

const adminForm = ref({
  id: '',
  password: '',
  captchaId: '',
  captchaCode: ''
})

// 记住我
const rememberMe = ref(false)

// 加载状态
const loading = ref(false)

// 表单引用
const userFormRef = ref()
const adminFormRef = ref()

// 表单验证规则
const userRules = reactive({
  id: [
    { required: true, message: '请输入用户账号', trigger: 'blur' },
    { min: 3, message: '用户账号不能少于3个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码不能少于6个字符', trigger: 'blur' }
  ],
  captchaCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 4, message: '验证码不能少于4个字符', trigger: 'blur' }
  ]
})

const adminRules = reactive({
  id: [
    { required: true, message: '请输入管理员账号', trigger: 'blur' },
    { min: 3, message: '管理员账号不能少于3个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码不能少于6个字符', trigger: 'blur' }
  ],
  captchaCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 4, message: '验证码不能少于4个字符', trigger: 'blur' }
  ]
})

// 用户登录处理
const handleUserLogin = async () => {
  if (!userFormRef.value) return

  // 确保验证码ID设置到表单中
  userForm.value.captchaId = captchaId.value

  try {
    const valid = await userFormRef.value.validate()
    if (valid) {
      loading.value = true
      console.log('提交的登录表单数据:', userForm.value)
      const response = await userLogin(userForm.value)

      // 存储登录信息到localStorage
      if (response.code == 200) {
        console.log('登录响应数据:', response.data)
        localStorage.setItem('login', JSON.stringify(response.data))
        console.log('存储到localStorage的登录数据:', localStorage.getItem('login'))
        ElMessage({
          type: 'success',
          message: '登录成功！'
        })

        // 跳转到用户页面
        await router.push('/users')
      } else {
        ElMessage({
          type: 'error',
          message: response.msg || '登录失败，请检查账号密码'
        })

        // 登录失败时刷新验证码
        refreshCaptcha()
      }
    }
  } catch (error) {
    console.error('用户登录失败:', error)
    ElMessage({
      type: 'error',
      message: error.response?.data?.message || '登录失败，请检查账号密码'
    })

    // 登录失败时刷新验证码
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

// 管理员登录处理
const handleAdminLogin = async () => {
  if (!adminFormRef.value) return

  // 确保验证码ID设置到表单中
  adminForm.value.captchaId = captchaId.value

  try {
    const valid = await adminFormRef.value.validate()
    if (valid) {
      loading.value = true
      console.log('提交的管理员登录表单数据:', adminForm.value)
      const response = await adminLogin(adminForm.value)

      // 存储登录信息到localStorage
      localStorage.setItem('login', JSON.stringify(response.data))

      ElMessage({
        type: 'success',
        message: '管理员登录成功！'
      })

      // 跳转到管理员页面
      await router.push('/admins')
    }
  } catch (error) {
    console.error('管理员登录失败:', error)
    ElMessage({
      type: 'error',
      message: error.response?.data?.message || '登录失败，请检查账号密码'
    })

    // 登录失败时刷新验证码
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}


// 刷新验证码
const refreshCaptcha = async () => {
  try {
    captchaLoading.value = true

    // 释放之前的验证码图片URL
    if (captchaUrl.value) {
      URL.revokeObjectURL(captchaUrl.value)
      captchaUrl.value = ''
    }

    const response = await axios({
      url: '/api/login/captcha',
      method: 'get',
      responseType: 'blob'
    })

    // 从响应头获取验证码ID
    const newCaptchaId = response.headers['captcha-id']

    if (!newCaptchaId) {
      throw new Error('无法从响应头中获取验证码ID')
    }

    // 保存验证码ID到全局变量
    captchaId.value = newCaptchaId

    // 检查响应数据是否为Blob
    if (response.data instanceof Blob) {
      // 创建图片URL
      const blob = new Blob([response.data], { type: 'image/png' })
      const imageUrl = URL.createObjectURL(blob)
      captchaUrl.value = imageUrl

      // 更新当前激活的表单中的captchaId
      if (loginType.value === 'user') {
        userForm.value.captchaId = newCaptchaId
      } else {
        adminForm.value.captchaId = newCaptchaId
      }
    } else {
      throw new Error('验证码响应格式不正确')
    }
  } catch (error) {
    console.error('获取验证码失败:', error)
    ElMessage({
      type: 'error',
      message: '获取验证码失败，请重试'
    })
  } finally {
    captchaLoading.value = false
  }
}

// 快捷登录处理
const handleQuickLogin = (type) => {
  ElMessage({
    type: 'info',
    message: `${type === 'wechat' ? '微信' : type === 'phone' ? '手机' : '邮箱'}登录功能开发中...`
  })
}

// 页面加载时自动获取验证码
onMounted(() => {
  refreshCaptcha()
  deleteShowtimes()
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #66eaa6 0%, #714ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -150px;
  left: -150px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  top: 50%;
  right: -100px;
  animation-delay: -2s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  bottom: -75px;
  left: 20%;
  animation-delay: -4s;
}

@keyframes float {

  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }

  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.floating-elements {
  position: absolute;
  width: 100%;
  height: 100%;
}

.float-element {
  position: absolute;
  width: 8px;
  height: 8px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: floatUp 8s linear infinite;
}

.float-element:nth-child(1) {
  left: 10%;
  animation-delay: 0s;
}

.float-element:nth-child(2) {
  left: 20%;
  animation-delay: -1s;
}

.float-element:nth-child(3) {
  left: 30%;
  animation-delay: -2s;
}

.float-element:nth-child(4) {
  left: 40%;
  animation-delay: -3s;
}

.float-element:nth-child(5) {
  left: 50%;
  animation-delay: -4s;
}

.float-element:nth-child(6) {
  left: 60%;
  animation-delay: -5s;
}

.float-element:nth-child(7) {
  left: 70%;
  animation-delay: -6s;
}

.float-element:nth-child(8) {
  left: 80%;
  animation-delay: -7s;
}

@keyframes floatUp {
  0% {
    transform: translateY(100vh) scale(0);
    opacity: 0;
  }

  10% {
    opacity: 1;
    transform: scale(1);
  }

  90% {
    opacity: 1;
    transform: scale(1);
  }

  100% {
    transform: translateY(-100px) scale(0);
    opacity: 0;
  }
}

/* 主要内容 */
.main-content {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 480px;
  padding: 20px;
}

/* 系统标题区域 */
.header-section {
  text-align: center;
  margin-bottom: 40px;
}

.logo-container {
  animation: fadeInDown 1s ease-out;
}

.deer-logo {
  margin-bottom: 20px;
}

.deer-svg {
  width: 100px;
  height: 100px;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
  animation: logoFloat 3s ease-in-out infinite;
}

@keyframes logoFloat {

  0%,
  100% {
    transform: translateY(0px);
  }

  50% {
    transform: translateY(-10px);
  }
}

.system-title {
  font-size: 32px;
  font-weight: bold;
  color: white;
  margin: 0 0 8px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.system-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
  font-weight: 300;
}

/* 登录卡片 */
.login-card {
  animation: fadeInUp 1s ease-out 0.3s both;
}

.login-form-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
}

.login-form-card .el-card__body {
  padding: 40px;
}

/* 登录标题 */
.login-title {
  text-align: center;
  margin-bottom: 30px;
}

.login-title h2 {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 8px;
}

.login-title p {
  color: #909399;
  margin: 0;
  font-size: 14px;
}

/* 登录类型切换 */
.login-type-switch {
  margin-bottom: 30px;
}

.login-type-switch .el-segmented {
  width: 100%;
}

/* 登录表单 */
.login-form .el-form-item {
  margin-bottom: 24px;
}

.login-form .el-form-item__label {
  font-weight: 500;
  color: #303133;
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.link-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.divider {
  color: #dcdfe6;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  margin-bottom: 20px;
}

/* 验证码容器 */
.captcha-container {
  display: flex;
  gap: 12px;
  align-items: center;
}

.captcha-input {
  flex: 1;
}

.captcha-image {
  width: 120px;
  height: 40px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #f5f7fa;
  overflow: hidden;
}

.captcha-image:hover {
  border-color: #409eff;
}

.captcha-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.captcha-placeholder {
  color: #909399;
  font-size: 12px;
  text-align: center;
  width: 100%;
}

/* 分割线 */
.divider-text {
  color: #909399;
  font-size: 12px;
  padding: 0 16px;
  background: white;
}

/* 快捷登录 */
.quick-login {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 20px;
}

.quick-login .el-button {
  width: 48px;
  height: 48px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s;
}

.quick-login .el-button:hover {
  border-color: #409eff;
  color: #409eff;
  transform: translateY(-2px);
}

/* 页脚 */
.footer {
  text-align: center;
  margin-top: 30px;
  animation: fadeIn 1s ease-out 0.6s both;
}

.footer p {
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
  margin: 0;
}

/* 动画 */
@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    max-width: 100%;
    padding: 16px;
  }

  .login-form-card .el-card__body {
    padding: 24px;
  }

  .system-title {
    font-size: 24px;
  }

  .deer-svg {
    width: 80px;
    height: 80px;
  }
}
</style>