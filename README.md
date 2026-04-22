# 海洋养殖管理系统

基于需求规格说明书实现的完整海洋养殖管理系统，包含前后端代码及数据库。

## 技术栈

### 后端

- Java 17+
- Spring Boot 3.x
- Spring Data JPA
- MySQL 8.x
- Lombok

### 前端

- Vue 3
- Element Plus
- ECharts
- Axios
- Vue Router

## 项目结构

```
Marine/
├── backed/                 # 后端项目
│   ├── src/
│   │   └── main/
│   │       ├── java/com/backed/
│   │       │   ├── config/      # 配置类
│   │       │   ├── controller/  # REST控制器
│   │       │   ├── entity/      # JPA实体
│   │       │   ├── repository/  # 数据访问层
│   │       │   └── service/     # 业务逻辑层
│   │       └── resources/
│   └── build.gradle
│
└── fronted/                # 前端项目
    ├── src/
    │   ├── api/              # API接口封装
    │   ├── components/       # 公共组件
    │   ├── views/            # 页面组件
    │   ├── router/           # 路由配置
    │   └── main.js
    └── package.json
```

## 功能模块

### 1. 养殖环境监测

- 实时数据展示（仪表盘）
- 阈值配置与预警
- 历史数据查询
- 趋势图表分析

### 2. 养殖过程管理

- 苗种投放记录
- 饲料投喂记录
- 日常巡查记录
- 捕捞记录
- 养殖日志生成

### 3. 病害防控管理

- 病害记录与跟踪
- 病害知识库查询
- 辅助诊断
- 用药记录

### 4. 产品溯源管理

- 溯源码生成
- 全链路信息关联
- 扫码查询
- 溯源报告导出

### 5. 系统基础管理

- 用户管理
- 角色权限管理
- 数据备份
- 操作日志
- 系统配置

## 快速开始

### 环境准备

1. JDK 17+
2. Node.js 16+
3. MySQL 8.x
4. Gradle 8+

### 数据库配置

1. 创建数据库：

```sql
CREATE DATABASE marine_aquaculture DEFAULT CHARACTER SET utf8mb4;
```

1. 修改 `backed/src/main/resources/application.properties` 中的数据库连接信息：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/marine_aquaculture?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
spring.datasource.username=root
spring.datasource.password=你的密码
```

### 后端启动

```bash
cd backed
./gradlew bootRun
```

后端服务运行在：<http://localhost:8080>

### 前端启动

```bash
cd fronted
npm install
npm run serve
```

前端服务运行在：<http://localhost:8081>

## 默认账号

| 角色    | 用户名    | 密码        |
| ----- | ------ | --------- |
| 超级管理员 | admin  | admin123  |
| 养殖人员  | farmer | farmer123 |

## API文档

系统启动后，可通过访问后端接口进行调试。所有接口统一前缀：`/api`

主要API：

- 用户相关：`/api/users`
- 区域相关：`/api/zones`
- 水质监测：`/api/water-quality`
- 养殖记录：`/api/seedlings`、`/api/feeding`、`/api/harvest`
- 病害管理：`/api/diseases`
- 溯源管理：`/api/traceability`

## 开发说明

### 后端开发

- 遵循 RESTful API 设计规范
- 使用 Spring Data JPA 简化数据库操作
- 配置跨域允许前端访问
- 系统启动时自动初始化示例数据

### 前端开发

- 组件化开发，复用性高
- 使用 Element Plus 实现美观的 UI
- 使用 ECharts 实现数据可视化
- 路由管理实现多页面切换

## 注意事项

1. 首次启动系统会自动创建数据库表结构
2. 会自动初始化两个默认用户和养殖区域数据
3. 确保 MySQL 服务正常运行
4. 如遇到端口冲突，可在配置文件中修改端口号

## 后续优化方向

1. 添加真实的数据权限控制
2. 实现安全的密码加密存储
3. 添加数据导出功能
4. 添加移动端适配
5. 添加物联网设备数据接入
6. 添加更丰富的数据分析功能

