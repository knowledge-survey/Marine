# Zeabur 部署指南

## 概述

Zeabur 是一个国内团队开发的部署平台，支持前后端部署，国内访问速度快，不需要信用卡！

项目地址：https://zeabur.com

---

## 部署前准备

### 1. 确保代码已推送到 GitHub

确认你的代码已经推送到 GitHub 仓库：
```
https://github.com/knowledge-survey/Marine
```

### 2. 注册 Zeabur 账号

访问 [Zeabur.com](https://zeabur.com) 并注册账号（可以用 GitHub 账号登录）

---

## 方法一：使用 Zeabur 自动检测（推荐）

### 第一步：部署后端

1. 登录 Zeabur 控制台
2. 点击 **"Create Project"** 创建新项目
3. 项目名称：`marine-aquaculture`（或你喜欢的名字）
4. 点击 **"Create Project"**

5. 在项目中点击 **"Add Service"** → **"Git"**
6. 选择你的 GitHub 仓库：`knowledge-survey/Marine`
7. 点击 **"Configure"**

8. **配置后端服务**：
   - **Service Name**: `marine-backend`
   - **Root Directory**: `backed`
   - **Branch**: `master`
   - **Region**: `Hong Kong`（香港，国内访问快）

9. 点击 **"Add Service"** 或 **"Deploy"**

10. Zeabur 会自动检测到这是一个 Spring Boot 项目并开始部署
11. 等待部署完成（可能需要 5-10 分钟）

### 第二步：部署前端

1. 在同一个项目中，再次点击 **"Add Service"** → **"Git"**
2. 选择同一个 GitHub 仓库

3. **配置前端服务**：
   - **Service Name**: `marine-frontend`
   - **Root Directory**: `fronted`
   - **Branch**: `master`
   - **Region**: `Hong Kong`

4. 点击 **"Add Service"** 或 **"Deploy"**

5. Zeabur 会自动检测到这是一个 Vue.js 项目并开始部署
6. 等待部署完成（可能需要 3-5 分钟）

### 第三步：配置环境变量

#### 配置后端

1. 进入 `marine-backend` 服务
2. 点击 **"Variables"** 或 **"Environment Variables"**
3. 添加：
   - Key: `SERVER_PORT`
   - Value: `10000`
4. 保存，服务会自动重启

#### 配置前端

1. 进入 `marine-frontend` 服务
2. 点击 **"Variables"** 或 **"Environment Variables"**
3. 添加：
   - Key: `VUE_APP_API_URL`
   - Value: `https://你的后端域名.zeabur.app/api`

   **重要**：将上面的值替换为你部署后端后获得的实际域名！

4. 保存，服务会自动重新构建和部署

---

## 方法二：使用 Docker 部署（如果自动检测失败）

### 部署后端

1. 点击 **"Add Service"** → **"Git"**
2. 选择仓库
3. 配置：
   - **Service Name**: `marine-backend`
   - **Root Directory**: `backed`
   - **Build Type**: `Dockerfile`
   - **Dockerfile Path**: `./backed/Dockerfile`

### 部署前端

1. 点击 **"Add Service"** → **"Git"**
2. 选择仓库
3. 配置：
   - **Service Name**: `marine-frontend`
   - **Root Directory**: `fronted`
   - **Build Type**: `Static Site`
   - **Build Command**: `npm install && npm run build`
   - **Output Directory**: `dist`

---

## 重要说明

### ⚠️ 数据库问题

当前项目使用 SQLite 数据库，在 Zeabur 上：

- Zeabur 的免费实例有文件系统限制
- 实例重启后数据可能丢失
- 建议使用 Zeabur 提供的数据库服务（PostgreSQL/MySQL）

### 📊 防止实例休眠

Zeabur 免费实例可能会休眠，可以：

1. 使用 [UptimeRobot](https://uptimerobot.com)
2. 设置每 10 分钟访问一次后端 URL

### 🌐 域名配置

Zeabur 会自动为每个服务分配一个域名：
- 后端：`https://marine-backend.zeabur.app`
- 前端：`https://marine-frontend.zeabur.app`

你也可以绑定自定义域名！

---

## 验证部署

1. **测试后端**：访问后端域名，应该有响应
2. **测试前端**：访问前端域名，应该能看到登录页面
3. **测试连接**：在前端登录，确认能正常连接后端 API

---

## 常用命令

### 本地测试后端

```bash
cd backed
./gradlew bootRun
```

### 本地测试前端

```bash
cd fronted
npm install
npm run serve
```

---

## 故障排除

### 后端部署失败

1. 检查 Zeabur 的日志
2. 确认 Java 版本（项目使用 Java 19）
3. 尝试使用 Docker 方式部署

### 前端部署失败

1. 检查 `package.json` 的 `build` 脚本
2. 确认 Node 版本
3. 查看构建日志

### 前端无法连接后端

1. 确认 `VUE_APP_API_URL` 设置正确
2. 检查后端是否正常运行
3. 查看浏览器控制台错误

---

## 获取帮助

- Zeabur 文档：https://zeabur.com/docs
- 项目仓库：https://github.com/knowledge-survey/Marine

---

## 快速检查清单

- [ ] 后端已部署并可访问
- [ ] 前端已部署并可访问
- [ ] 前端可以正常连接后端 API
- [ ] 配置了环境变量
- [ ] 测试了主要功能

祝你部署顺利！🎉
