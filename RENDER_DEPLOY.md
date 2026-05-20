# Render 部署指南

## 项目结构

- **fronted/** - Vue.js 前端
- **backed/** - Spring Boot 后端

## 部署步骤

### 方法一：使用 render.yaml 自动部署（推荐）

1. 确保你的代码已推送到 GitHub
2. 访问 [Render](https://render.com) 并登录
3. 点击 "New +" → "Blueprint"
4. 选择你的 GitHub 仓库
5. Render 会自动检测 `render.yaml` 并创建两个服务：
   - `marine-backend` - 后端服务
   - `marine-frontend` - 前端静态站点

### 方法二：手动分别部署

#### 部署后端

1. 在 Render 中点击 "New +" → "Web Service"
2. 选择你的 GitHub 仓库
3. 配置：
   - **Name**: `marine-backend`
   - **Environment**: `Docker`
   - **Dockerfile Path**: `./backed/Dockerfile`
   - **Region**: `Singapore`
   - **Plan**: `Free`
4. 点击 "Create Web Service"

#### 部署前端

1. 在 Render 中点击 "New +" → "Static Site"
2. 选择你的 GitHub 仓库
3. 配置：
   - **Name**: `marine-frontend`
   - **Root Directory**: `fronted`
   - **Build Command**: `npm install && npm run build`
   - **Publish Directory**: `dist`
   - **Region**: `Singapore`
   - **Plan**: `Free`
4. 点击 "Create Static Site"

## 重要说明

### 数据库问题 ⚠️

当前项目使用 SQLite 数据库。在 Render 上部署时需要注意：

- **问题**: Render 的免费实例文件系统是临时的，实例休眠或重启后数据会丢失
- **解决方案（推荐）**: 使用 Render PostgreSQL 数据库

### 配置前端连接后端

部署后需要更新前端的 API 地址：

1. 获取后端的 URL（例如：`https://marine-backend.onrender.com`）
2. 修改 `fronted/src/api/index.js` 中的 `getBaseURL()` 函数，或者设置环境变量

### 防止实例休眠

Render 免费实例 15 分钟无请求会休眠。可以使用：

- [UptimeRobot](https://uptimerobot.com) - 免费监控服务
- 设置每 10 分钟访问一次后端服务

## 环境变量

### 后端环境变量

- `SERVER_PORT`: 服务端口（默认 8081）

## 本地开发

```bash
# 后端
cd backed
./gradlew bootRun

# 前端
cd fronted
npm install
npm run serve
```

## 注意事项

1. **数据持久化**: 目前使用 SQLite，建议迁移到 PostgreSQL
2. **实例休眠**: 免费实例会休眠，需要外部服务保持活跃
3. **性能**: 免费实例资源有限，生产环境建议升级计划

## 技术支持

- Render 文档: https://render.com/docs
- 项目仓库: https://github.com/knowledge-survey/Marine
