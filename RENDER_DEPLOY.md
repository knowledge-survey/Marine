# Render 手动部署指南

## 概述

本指南将帮助你手动分别部署前端和后端到 Render。

## 部署前准备

### 1. 确保代码已推送到 GitHub

确认你的代码已经推送到 GitHub 仓库：
```
https://github.com/knowledge-survey/Marine
```

### 2. 注册 Render 账号

访问 [Render.com](https://render.com) 并注册账号（可以用 GitHub 账号登录）

---

## 第一步：部署后端

### 1. 创建新的 Web Service

1. 登录 Render 控制台
2. 点击右上角的 **"New +"** 按钮
3. 选择 **"Web Service"**

### 2. 连接 GitHub 仓库

1. 选择你的 GitHub 仓库：`knowledge-survey/Marine`
2. 如果没有看到仓库，需要先授权 Render 访问你的 GitHub

### 3. 配置后端服务

填写以下配置：

| 配置项 | 值 |
|--------|-----|
| **Name** | `marine-backend`（或你喜欢的名字） |
| **Region** | `Singapore`（新加坡，离中国最近） |
| **Branch** | `master` |
| **Runtime** | `Docker` |
| **Dockerfile Path** | `./backed/Dockerfile` |
| **Root Directory** | 留空（或填写 `./backed`） |
| **Instance Type** | `Free` |

### 4. 配置环境变量

在 **"Environment"** 部分点击 **"Add Environment Variable"**：

| Key | Value |
|-----|-------|
| `SERVER_PORT` | `10000` |

### 5. 完成创建

1. 点击 **"Create Web Service"**
2. 等待部署完成（可能需要 5-10 分钟）
3. 部署成功后，你会得到一个 URL，例如：
   ```
   https://marine-backend.onrender.com
   ```

### 6. 验证后端部署

访问你的后端 URL，应该能看到响应（可能是错误页面或空白页面，这是正常的，因为我们没有根路径的路由）

---

## 第二步：部署前端

### 1. 创建新的 Static Site

1. 回到 Render 控制台
2. 点击 **"New +"** 按钮
3. 选择 **"Static Site"**

### 2. 连接 GitHub 仓库

1. 选择同一个 GitHub 仓库：`knowledge-survey/Marine`

### 3. 配置前端站点

填写以下配置：

| 配置项 | 值 |
|--------|-----|
| **Name** | `marine-frontend`（或你喜欢的名字） |
| **Region** | `Singapore` |
| **Branch** | `master` |
| **Root Directory** | `fronted` |
| **Build Command** | `npm install && npm run build` |
| **Publish Directory** | `dist` |
| **Instance Type** | `Free` |

### 4. 配置环境变量

在 **"Environment"** 部分点击 **"Add Environment Variable"**：

| Key | Value |
|-----|-------|
| `NODE_VERSION` | `18` |
| `VUE_APP_API_URL` | `https://你的后端URL.onrender.com/api` |

**重要**：将 `VUE_APP_API_URL` 的值替换为你第一步部署后端后获得的实际 URL！

### 5. 配置重定向规则（重要！）

在 **"Redirects/Rewrites"** 部分：

1. 点击 **"Add Rule"**
2. 填写：
   - **Source**：`/*`
   - **Destination**：`/index.html`
   - **Action**：`Rewrite`
3. 保存

### 6. 完成创建

1. 点击 **"Create Static Site"**
2. 等待部署完成（可能需要 3-5 分钟）
3. 部署成功后，你会得到一个前端 URL，例如：
   ```
   https://marine-frontend.onrender.com
   ```

---

## 第三步：配置前后端连接（如果还没配置）

### 如果前端需要连接后端

如果部署前端时没有设置 `VUE_APP_API_URL`，需要重新配置：

1. 在 Render 控制台进入你的前端服务
2. 点击 **"Environment"** → **"Environment Variables"**
3. 添加或修改：
   ```
   Key: VUE_APP_API_URL
   Value: https://你的后端URL.onrender.com/api
   ```
4. 点击 **"Save Changes"**，Render 会自动重新部署

---

## 重要说明

### ⚠️ 数据库问题

当前项目使用 SQLite 数据库，在 Render 上有以下限制：

**问题**：
- Render 免费实例的文件系统是临时的
- 实例休眠或重启后，所有数据会丢失
- 每次重新部署，数据库也会重置

**临时解决方案**：
1. 使用 [UptimeRobot](https://uptimerobot.com) 免费监控
2. 设置每 10 分钟访问一次后端 URL，保持实例活跃
3. 这样可以防止实例休眠，数据不会丢失

**推荐的长期方案**：
使用 Render PostgreSQL 数据库（免费额度 1GB）

### 📊 防止实例休眠

1. 访问 [UptimeRobot](https://uptimerobot.com)
2. 注册免费账号
3. 点击 **"Add New Monitor"**
4. 配置：
   - **Monitor Type**: `HTTP(s)`
   - **URL**: 你的后端 URL
   - **Monitoring Interval**: `5 minutes`（或 10 分钟）
5. 保存

### 📝 常用命令

#### 本地测试后端

```bash
cd backed
./gradlew bootRun
```

#### 本地测试前端

```bash
cd fronted
npm install
npm run serve
```

---

## 故障排除

### 后端部署失败

1. 检查 Dockerfile 是否正确
2. 查看 Render 的日志（"Logs" 标签页）
3. 确认 Java 版本（项目使用 Java 19）

### 前端部署失败

1. 检查 `package.json` 的 `build` 脚本
2. 确认 Node 版本设置正确（18.x）
3. 查看 Render 的构建日志

### 前端无法连接后端

1. 确认 `VUE_APP_API_URL` 设置正确
2. 检查后端是否正常运行
3. 查看浏览器控制台的网络请求错误

---

## 获取帮助

- Render 文档：https://render.com/docs
- 项目仓库：https://github.com/knowledge-survey/Marine

---

## 快速检查清单

部署完成后，请确认：

- [ ] 后端已部署并可访问
- [ ] 前端已部署并可访问
- [ ] 前端可以正常连接后端 API
- [ ] 设置了 UptimeRobot 防止实例休眠
- [ ] 测试了主要功能（登录、数据操作等）

祝你部署顺利！🎉
