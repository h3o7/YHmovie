# 🎬 YH电影网 (YHmovie)

[![Vue 3](https://img.shields.io/badge/Vue-3.5.13-4FC08D? logo=vue. js)](https://vuejs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-6DB33F?logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Element Plus](https://img.shields.io/badge/Element%20Plus-2.10.2-409EFF)](https://element-plus.org/)
[![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5.12-red)](https://baomidou.com/)
[![Java](https://img.shields.io/badge/Java-17-orange? logo=openjdk)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-MIT-blue. svg)](LICENSE)

> 一个基于 **Spring Boot 3** + **Vue 3** + **Vite** + **Element Plus** 构建的全栈电影资源管理与展示平台

---

## 📖 项目简介

**YH电影网** 是一个现代化的电影信息管理系统，采用前后端分离架构设计。项目集成了电影信息爬取、数据管理、用户交互等功能，适合作为全栈开发学习项目或二次开发基础。

### ✨ 核心功能

- 🎥 **电影信息管理**：电影数据的增删改查、分类管理、标签筛选
- 👤 **用户系统**：用户注册/登录、个人信息管理、JWT 身份认证
- 💬 **评论互动**：电影评论、评分系统
- 🖼️ **云存储集成**：阿里云 OSS 集成，支持电影海报、演员头像等资源的云端存储
- 🔍 **搜索功能**：电影搜索、分类筛选
- 📊 **数据爬取**：Python 爬虫脚本，用于获取电影和演员数据
- 📱 **响应式设计**：适配桌面端和移动端

---

## 🛠️ 技术栈

### 前端技术

| 技术 | 版本 | 说明 |
|: ---|:---|:---|
| **Vue 3** | 3.5.13 | 渐进式 JavaScript 框架 |
| **Vite** | 6.2.4 | 新一代前端构建工具 |
| **Element Plus** | 2.10.2 | 基于 Vue 3 的组件库 |
| **Vue Router** | 4.5.0 | Vue. js 官方路由管理器 |
| **Axios** | 1.10.0 | HTTP 客户端 |

### 后端技术

| 技术 | 版本 | 说明 |
|:---|:---|:---|
| **Spring Boot** | 3.5.5 | 企业级 Java 开发框架 |
| **MyBatis Plus** | 3.5.12 | MyBatis 增强工具 |
| **Knife4j** | 4.4.0 | 接口文档增强工具（Swagger UI） |
| **JWT** | 0.9.1 | JSON Web Token 身份验证 |
| **Hutool** | 5.8.39 | Java 工具类库 |
| **Aliyun OSS** | 3.17.4 | 阿里云对象存储 SDK |

### 数据与工具

- **数据库**：MySQL 5.7+
- **数据爬取**：Python 3.x（爬虫脚本）
- **构建工具**：Maven 3.6+

---

## 📂 项目结构

```
YHmovie/
├── YHmovie-Back-End/              # 后端项目（Spring Boot）
│   ├── yhmovie-common/            # 公共模块（工具类、配置）
│   ├── yhmovie-pojo/              # 实体类模块（Entity、DTO、VO）
│   ├── yhmovie-service/           # 服务模块（Controller、Service、Mapper）
│   │   └── src/
│   │       ├── main/
│   │       │   ├── java/          # Java 源代码
│   │       │   └── resources/     # 配置文件（application.yml）
│   │       └── test/              # 测试代码
│   ├── pom.xml                    # Maven 父工程配置
│   └── yh_movie. sql               # 数据库初始化脚本
│
├── YHmovie-Front-End/             # 前端项目（Vue 3 + Vite）
│   ├── public/                    # 静态资源
│   ├── src/
│   │   ├── api/                   # API 接口封装
│   │   ├── assets/                # 静态资源（图片、样式）
│   │   ├── components/            # 公共组件
│   │   ├── router/                # 路由配置
│   │   ├── utils/                 # 工具函数
│   │   ├── views/                 # 页面视图
│   │   ├── App.vue                # 根组件
│   │   └── main.js                # 入口文件
│   ├── index.html                 # HTML 模板
│   ├── vite.config.js             # Vite 配置文件
│   └── package. json               # 前端依赖配置
│
└── utils/                         # Python 工具脚本
    ├── getMoviesAndActorsData.py  # 爬取电影和演员数据
    ├── generateUsersAndComments.py # 生成测试用户和评论数据
    ├── getAvatarUrl.py            # 获取演员头像链接
    └── actors_avatar_url.csv      # 演员头像数据
```

---

## 🚀 快速开始

### 📋 前置要求

在开始之前，请确保您的开发环境已安装以下工具：

- **JDK 17** 或更高版本(最低Java8)
- **Maven 3.6+**
- **Node.js 16+** 和 npm/yarn
- **MySQL 5.7+** 或 **MySQL 8.0**
- **Python 3.8+**（可选，如需运行爬虫脚本）

---

### 1️⃣ 克隆项目

```bash
git clone https://github.com/h3o7/YHmovie. git
cd YHmovie
```

---

### 2️⃣ 数据库配置

#### 导入数据库

找到项目根目录下的 `YHmovie-Back-End/yh_movie.sql` 文件，执行以下命令导入：

```bash
mysql -u root -p < YHmovie-Back-End/yh_movie.sql
```

---

### 3️⃣ 后端启动

#### ⚠️ 重要配置：阿里云 OSS 环境变量

**本项目使用阿里云 OSS 进行图片资源存储（电影海报、演员头像、用户头像等）。**

在修改或上传图像时，**必须**在您的系统环境变量中配置以下两个参数：

| 环境变量名 | 说明 |
|:---|:---|
| `OSS_ACCESS_KEY_ID` | 阿里云 OSS AccessKey ID |
| `OSS_ACCESS_KEY_SECRET` | 阿里云 OSS AccessKey Secret |

**配置方式：**

**Linux / macOS：**
```bash
export OSS_ACCESS_KEY_ID="你的AccessKeyId"
export OSS_ACCESS_KEY_SECRET="你的AccessKeySecret"
```

**Windows PowerShell：**
```powershell
$env:OSS_ACCESS_KEY_ID="你的AccessKeyId"
$env: OSS_ACCESS_KEY_SECRET="你的AccessKeySecret"
```

**IntelliJ IDEA / Eclipse：**

在 Run/Debug Configurations → Environment Variables 中添加上述两个键值对。

> ⚠️ **安全提醒**：切勿将 AccessKey 硬编码到代码中或上传到公开仓库！

#### 修改配置文件

打开 `YHmovie-Back-End/yhmovie-service/src/main/resources/application.yml`，修改以下配置：

```yaml
spring:
  datasource:
    url: jdbc: mysql://localhost:3306/yh_movie? useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root          # 修改为您的数据库用户名
    password: your_password # 修改为您的数据库密码
    driver-class-name: com. mysql.cj.jdbc.Driver

aliyun:
  oss: 
    endpoint: oss-cn-hangzhou. aliyuncs.com  # 修改为您的 OSS Endpoint
    bucket-name: your-bucket-name           # 修改为您的 Bucket 名称
    # accessKeyId 和 accessKeySecret 从环境变量读取
```

#### 启动后端服务

```bash
cd YHmovie-Back-End
mvn clean install
cd yhmovie-service
mvn spring-boot:run
```

后端服务默认运行在 **http://localhost:8080**

接口文档地址：**http://localhost:8080/doc.html** (Knife4j)

---

### 4️⃣ 前端启动

```bash
cd YHmovie-Front-End

# 安装依赖
npm install
# 或使用 yarn
yarn install

# 启动开发服务器
npm run dev
```

前端服务默认运行在 **http://localhost:5173**

---

### 5️⃣ Python 数据脚本（可选）

如果需要使用 Python 脚本获取电影数据或生成测试数据：

```bash
cd utils

pip3 install Faker    

# 运行爬虫脚本
python3 getMoviesAndActorsData.py

# 生成测试用户和评论
python3 generateUsersAndComments. py
```

---

## ⚙️ 详细配置说明

### 阿里云 OSS 配置详解

#### 1. 开通服务

登录 [阿里云控制台](https://oss.console.aliyun.com/)，开通对象存储 OSS 服务。

#### 2. 创建 Bucket

- **Bucket 名称**：自定义（例如：`yhmovie-resources`）
- **读写权限**：公共读（Public Read）
- **存储类型**：标准存储

#### 3. 获取 AccessKey

进入 **访问控制 RAM**，创建一个新用户并授予 `AliyunOSSFullAccess` 权限，获取 **AccessKey ID** 和 **AccessKey Secret**。

#### 4. 配置后端

在 `application.yml` 中配置：

```yaml
aliyun:
  oss: 
    endpoint: oss-cn-hangzhou.aliyuncs.com  # 根据您的 Bucket 所在地域修改
    bucket-name: yhmovie-resources          # 修改为您创建的 Bucket 名称
```

#### 5. 设置环境变量

按照前文所述方式设置 `OSS_ACCESS_KEY_ID` 和 `OSS_ACCESS_KEY_SECRET` 环境变量。

---

### 数据库配置说明

本项目使用 MySQL 作为关系型数据库，推荐版本：

- **MySQL 5.7** 或 **MySQL 8.0**
- 字符集：`utf8mb4`
- 排序规则：`utf8mb4_unicode_ci`

数据库初始化脚本包含以下表：

- `movie`（电影信息表）
- `actor`（演员信息表）
- `user`（用户表）
- `comment`（评论表）
- `category`（分类表）
- 等... 

---

## 📖 接口文档

项目集成了 **Knife4j** 接口文档工具，启动后端服务后访问：

```
http://localhost:8080/doc.html
```

可以在线测试所有 RESTful API 接口。

---

## 🧪 功能模块

### 用户端功能

- ✅ 用户注册/登录
- ✅ 用户信息修改
- ✅ 电影浏览与搜索
- ✅ 电影详情查看
- ✅ 电影评分与评论
- ✅ 收藏电影

### 管理端功能

- ✅ 电影信息管理（增删改查）
- ✅ 演员信息管理
- ✅ 用户管理
- ✅ 评论管理
- ✅ 分类管理

---

## 🤝 贡献指南

欢迎提交 Issue 或 Pull Request 来完善这个项目！

1. Fork 本仓库
2. 新建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

## 📝 常见问题 (FAQ)

### Q1: 启动后端时报错 `Access denied for user 'root'@'localhost'`

**A:** 请检查 `application.yml` 中的数据库用户名和密码是否正确。

### Q2: 图片上传失败

**A:** 请确认已正确配置阿里云 OSS 的环境变量 `OSS_ACCESS_KEY_ID` 和 `OSS_ACCESS_KEY_SECRET`，并检查 `application.yml` 中的 `endpoint` 和 `bucket-name` 是否正确。

### Q3: 前端请求后端接口报 CORS 错误

**A:** 后端已配置 CORS 跨域支持，请检查前端 `vite.config.js` 中的代理配置是否正确指向后端地址。

### Q4: Python 脚本无法运行

**A:** 请确保已安装 Python 3.8+ 以及相关依赖库（如 `requests`、`pymysql` 等）。

---

## 📄 开源协议

本项目采用 [MIT License](LICENSE) 开源协议。

---

## 👨‍💻 作者

**h3o7**

- GitHub: [@h3o7](https://github.com/h3o7)

---

## 🌟 Star History

如果这个项目对您有帮助，欢迎给个 ⭐️ Star！

---

**Happy Coding!  🎉**
