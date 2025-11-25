# YH电影网 🎬

一个基于 Vue.js + Java 的全栈电影网站项目

## 📋 项目简介

YH电影网是一个现代化的电影信息展示和管理平台，提供电影浏览、搜索、详情查看等功能。项目采用前后端分离架构，前端使用 Vue.js 构建，后端基于 Java 开发。

## 🛠️ 技术栈

### 前端技术
- **Vue.js** - 渐进式 JavaScript 框架
- **Vite** - 新一代前端构建工具
- **JavaScript/ES6+** - 现代 JavaScript 特性

### 后端技术
- **Java** - 后端主要开发语言
- **Maven** - 项目管理和构建工具

## 📁 项目结构

```
YHmovie/
├── YHmovie-Front-End/          # 前端项目
│   ├── src/                    # 源代码目录
│   ├── public/                 # 静态资源
│   ├── package.json            # 前端依赖配置
│   ├── vite.config.js          # Vite 配置文件
│   └── index.html              # 入口 HTML 文件
│
└── YHmovie-Back-End/           # 后端项目
    ├── yhmovie-common/         # 公共模块
    ├── yhmovie-pojo/           # 实体类模块
    ├── yhmovie-service/        # 业务逻辑模块
    └── pom.xml                 # Maven 配置文件
```

## 🚀 快速开始

### 环境要求

- Node.js 14+ 
- npm 或 yarn
- JDK 8+
- Maven 3.6+

### 前端安装与运行

```bash
# 进入前端目录
cd YHmovie-Front-End

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

### 后端安装与运行

```bash
# 进入后端目录
cd YHmovie-Back-End

# 使用 Maven 编译项目
mvn clean install

# 运行项目
mvn spring-boot:run
```

## 💡 功能特性

- 🎥 电影信息浏览
- 🔍 电影搜索功能
- 📝 电影详情展示
- 🎯 分类筛选
- 📱 响应式设计

## 🤝 贡献

欢迎提交 Issue 或 Pull Request 来改进这个项目！

## 📄 许可证

本项目采用 MIT 许可证

## 👤 作者

[@h3o7](https://github.com/h3o7)

## 📞 联系方式

如有问题或建议，欢迎通过 Issue 或 Pull Request 与我联系。

---

⭐ 如果这个项目对您有帮助，欢迎 Star！
