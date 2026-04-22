# 实施检查清单

## 任务一：历史数据折叠列表

- [x] T1.1.1 WaterQualityMapper 增加按日期查询方法
- [x] T1.1.2 确保 recordTime 字段有索引
- [x] T1.2.1 WaterQuality.vue 移除 el-table 历史数据展示
- [x] T1.2.2 使用 el-collapse 替换历史数据展示
- [x] T1.2.3 实现按日期分组 computed 属性
- [x] T1.2.4 实现每组统计信息计算
- [x] T1.2.5 深色主题样式适配
- [ ] T1.2.6 折叠/展开功能测试

## 任务二：养殖记录管理问题修复

- [x] T2.1.1 SeedlingRecord.java 增加 batchNo 字段
- [x] T2.1.2 数据库执行 ALTER TABLE seedling_record ADD COLUMN batch_no
- [x] T2.1.3 SeedlingRecordMapper 增加按批次号查询方法
- [x] T2.1.4 SeedlingRecordController 增加按批次号查询接口
- [x] T2.2.1 SeedlingRecordService 查询按时间倒序
- [x] T2.2.2 FeedingRecordService 查询按时间倒序
- [x] T2.2.3 HarvestRecordService 查询按时间倒序
- [x] T2.3.1 AquacultureRecord.vue ResizeObserver 错误修复
- [x] T2.3.2 添加 zoneId 校验
- [x] T2.3.3 苗种投放表单增加批次号字段
- [x] T2.3.4 列表按时间倒序展示
- [x] T2.3.5 表格增加备注列
- [ ] T2.3.6 所有 Tab 页签功能测试

## 任务三：溯源管理功能完善

### 后端
- [x] T3.1.1 TraceabilityCode.java 增加 batchNo 字段
- [x] T3.1.2 TraceabilityCode.java 增加 zoneId 字段
- [x] T3.1.3 TraceabilityCode.java 增加 qrCodeUrl 字段
- [x] T3.1.4 数据库执行 ALTER TABLE traceability_code 添加字段
- [x] T3.1.5 TraceabilityCodeMapper 增加相关查询方法
- [x] T3.2.1 新增 POST /api/traceability/batch/{batchNo} 接口
- [x] T3.2.2 实现 generateCodeForBatch 方法
- [x] T3.3.1 build.gradle 添加 ZXing 依赖
- [x] T3.3.2 创建 QrCodeGenerator 工具类
- [x] T3.3.3 新增 GET /api/traceability/qrcode/{code} 接口
- [x] T3.4.1 增强 getTraceabilityInfo() 返回完整溯源数据
- [x] T3.4.2 新增 GET /api/traceability/public/{code} 接口
- [x] T3.5.1 新增 GET /api/seedlings/batches 接口

### 前端
- [x] T3.6.1 安装 qrcode.vue 依赖
- [x] T3.6.2 TraceManage.vue 生成溯源码面板改造（批次选择）
- [x] T3.6.3 TraceManage.vue 二维码展示
- [x] T3.6.4 TraceManage.vue 溯源时间线展示
- [x] T3.7.1 创建 TraceabilityPublic.vue 页面
- [x] T3.7.2 TraceabilityPublic.vue 时间线布局
- [x] T3.7.3 TraceabilityPublic.vue 水质趋势图表
- [x] T3.7.4 TraceabilityPublic.vue 响应式设计
- [x] T3.8.1 router/index.js 添加公开路由 /traceability/:code
- [x] T3.8.2 确保公开路由不经过登录验证

## 任务四：测试与验证

- [ ] T4.1.1 历史数据折叠功能测试
- [ ] T4.1.2 养殖记录添加和展示测试
- [ ] T4.1.3 溯源码生成和二维码展示测试
- [ ] T4.1.4 公开溯源页面访问测试
- [ ] T4.2.1 不同浏览器兼容性测试
- [ ] T4.2.2 移动端溯源页面测试
- [ ] T4.3.1 数据库变更 SQL 执行
- [ ] T4.3.2 现有数据迁移验证
