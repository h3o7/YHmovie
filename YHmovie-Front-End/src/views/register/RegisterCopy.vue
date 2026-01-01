<template>
    <div class="login-container">
        <!-- 背景装饰 (保持一致) -->
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
                            <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"
                                fill="none" stroke="#ffffff" stroke-width="1" />
                            <path d="M14.5 3a1.5 1.5 0 0 1 1.5 1.5V5h-8v-.5A1.5 1.5 0 0 1 9.5 3h5z" fill="#ffffff" />
                            <path d="M12 11a2 2 0 1 0 0-4 2 2 0 0 0 0 4z" fill="#ffffff" />
                            <path d="M8 16v-2a4 4 0 0 1 8 0v2" fill="none" stroke="#ffffff" stroke-width="1" />
                        </svg>
                    </div>
                    <h1 class="system-title">YH电影网</h1>
                    <p class="system-subtitle">加入我们，开启观影之旅</p>
                </div>
            </div>

            <!-- 注册卡片 -->
            <div class="login-card">
                <el-card shadow="hover" class="login-form-card">
                    <!-- 注册标题 -->
                    <div class="login-title">
                        <h2>注册新账号</h2>
                        <p>请填写以下信息完成注册</p>
                    </div>

                    <!-- 注册表单 -->
                    <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" class="login-form"
                        label-position="top">
                        <!-- 账号信息 -->
                        <el-row :gutter="20">
                            <el-col :span="24">
                                <el-form-item label="用户账号" prop="userId">
                                    <el-input v-model="registerForm.userId" placeholder="请输入账号" size="large" clearable>
                                        <template #prefix>
                                            <el-icon>
                                                <User />
                                            </el-icon>
                                        </template>
                                    </el-input>
                                </el-form-item>
                            </el-col>
                        </el-row>

                        <!-- 基本信息一行两个 -->
                        <el-row :gutter="20">
                            <el-col :span="12">
                                <el-form-item label="用户昵称" prop="userName">
                                    <el-input v-model="registerForm.userName" placeholder="请输入昵称" size="large" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="手机号码" prop="userPhone">
                                    <el-input v-model="registerForm.userPhone" placeholder="请输入手机号" size="large" />
                                </el-form-item>
                            </el-col>
                        </el-row>

                        <el-row :gutter="20">
                            <el-col :span="12">
                                <el-form-item label="性别" prop="userGender">
                                    <el-select v-model="registerForm.userGender" placeholder="选择性别" size="large"
                                        style="width: 100%">
                                        <el-option label="男" value="男" />
                                        <el-option label="女" value="女" />
                                    </el-select>
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="出生日期" prop="userBirthDate">
                                    <el-date-picker v-model="registerForm.userBirthDate" type="date" placeholder="选择日期"
                                        size="large" style="width: 100%" format="YYYY-MM-DD"
                                        value-format="YYYY-MM-DD" />
                                </el-form-item>
                            </el-col>
                        </el-row>

                        <el-form-item label="头像地址" prop="userAvatarUrl">
                            <el-input v-model="registerForm.userAvatarUrl" placeholder="请输入头像URL地址 (选填)" size="large"
                                clearable>
                                <template #prefix>
                                    <el-icon>
                                        <Picture />
                                    </el-icon>
                                </template>
                            </el-input>
                        </el-form-item>

                        <!-- 密码区域 -->
                        <el-form-item label="密码" prop="userPassword">
                            <el-input v-model="registerForm.userPassword" type="password" placeholder="请输入密码"
                                size="large" show-password clearable>
                                <template #prefix>
                                    <el-icon>
                                        <Lock />
                                    </el-icon>
                                </template>
                            </el-input>
                        </el-form-item>

                        <el-form-item label="确认密码" prop="confirmPassword">
                            <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码"
                                size="large" show-password clearable>
                                <template #prefix>
                                    <el-icon>
                                        <Check />
                                    </el-icon>
                                </template>
                            </el-input>
                        </el-form-item>

                        <!-- 操作按钮 -->
                        <div class="form-footer">
                            <el-button type="primary" size="large" class="login-button" :loading="loading"
                                @click="handleRegister">
                                <span v-if="!loading">立即注册</span>
                                <span v-else>注册中...</span>
                            </el-button>
                        </div>

                        <div class="back-to-login">
                            <el-link type="info" :underline="false" @click="router.push('/login')">
                                <el-icon class="el-icon--left">
                                    <Back />
                                </el-icon> 返回登录
                            </el-link>
                        </div>

                    </el-form>
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
    User,
    Lock,
    Check,
    Picture,
    Back
} from '@element-plus/icons-vue'
import { registerUser } from '@/api/login' // 假设 api/login 中已导出该方法

const router = useRouter()
const loading = ref(false)
const registerFormRef = ref()

const registerForm = reactive({
    userId: '',
    userName: '',
    userPhone: '',
    userGender: '',
    userBirthDate: '',
    userAvatarUrl: '',
    userPassword: '',
    confirmPassword: ''
})

// 验证两次密码是否一致
const validatePass2 = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次输入密码'))
    } else if (value !== registerForm.userPassword) {
        callback(new Error('两次输入密码不一致!'))
    } else {
        callback()
    }
}

const registerRules = reactive({
    userId: [
        { required: true, message: '请输入用户账号', trigger: 'blur' },
        { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
    ],
    userName: [
        { required: true, message: '请输入用户昵称', trigger: 'blur' }
    ],
    userPhone: [
        { required: true, message: '请输入手机号码', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码格式', trigger: 'blur' }
    ],
    userGender: [
        { required: true, message: '请选择性别', trigger: 'change' }
    ],
    userBirthDate: [
        { required: true, message: '请选择出生日期', trigger: 'change' }
    ],
    userPassword: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码不能少于6个字符', trigger: 'blur' }
    ],
    confirmPassword: [
        { validator: validatePass2, trigger: 'blur' }
    ]
})

const handleRegister = async () => {
    if (!registerFormRef.value) return

    try {
        const valid = await registerFormRef.value.validate()
        if (valid) {
            loading.value = true

            // 构造 DTO 数据
            const usersDto = {
                userId: registerForm.userId,
                userName: registerForm.userName,
                userPhone: registerForm.userPhone,
                userGender: registerForm.userGender,
                userBirthDate: registerForm.userBirthDate,
                userAvatarUrl: registerForm.userAvatarUrl,
                userPassword: registerForm.userPassword,
                confirmPassword: registerForm.confirmPassword
            }

            console.log('提交注册数据:', usersDto)

            // 调用注册接口
            const response = await registerUser(usersDto)
            if (response.code == 200) {
                ElMessage({
                    type: 'success',
                    message: '注册成功！请登录'
                })

                // 注册成功后跳转回登录页
                setTimeout(() => {
                    router.push('/login')
                }, 1500)
            } else {
            ElMessage({
                type: 'error',
                message: response.msg || '注册失败，请稍后重试'
            })
            }
        }

    } catch (error) {
        console.error('注册失败:', error)
        ElMessage({
            type: 'error',
            message: error.response?.data?.message || '注册失败，请稍后重试'
        })
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
/* 复用 Login.vue 的样式 */
.login-container {
    min-height: 100vh;
    background: linear-gradient(135deg, #66eaa6 0%, #714ba2 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: auto;
    /* 允许小屏幕滚动 */
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

/* ... 其他 float-element 样式与 Login 相同，省略重复代码以保持简洁，实际使用时请完整复制 ... */
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

.main-content {
    position: relative;
    z-index: 1;
    width: 100%;
    max-width: 520px;
    /* 稍微宽一点以适应注册表单 */
    padding: 20px;
    margin-top: 40px;
    /* 避免顶部太挤 */
    margin-bottom: 40px;
}

.header-section {
    text-align: center;
    margin-bottom: 30px;
}

.logo-container {
    animation: fadeInDown 1s ease-out;
}

.deer-logo {
    margin-bottom: 15px;
}

.deer-svg {
    width: 80px;
    /* 稍微调小一点 */
    height: 80px;
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
    font-size: 28px;
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

.login-form-card :deep(.el-card__body) {
    padding: 40px;
}

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

.login-form .el-form-item {
    margin-bottom: 24px;
}

.login-form :deep(.el-form-item__label) {
    font-weight: 500;
    color: #303133;
    padding-bottom: 4px;
}

.login-button {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 500;
    border-radius: 8px;
}

.back-to-login {
    text-align: center;
    margin-top: 20px;
}

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

@media (max-width: 768px) {
    .main-content {
        padding: 16px;
        margin: 0;
    }

    .login-form-card :deep(.el-card__body) {
        padding: 24px;
    }
}
</style>