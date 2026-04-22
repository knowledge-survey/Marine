# 任务分解清单

## 任务一：历史数据折叠列表

### T1.1 后端：WaterQuality 按日期查询优化
- 修改 WaterQualityMapper，增加按日期范围查询的方法
- 确保 recordTime 字段有正确的索引

### T1.2 前端：WaterQuality.vue 历史数据改造
- 移除现有的 el-table 展示方式
- 使用 el-collapse 替换历史数据展示
- 实现按日期分组逻辑（computed 属性）
- 实现每组统计信息计算（平均水温、最大/最小值、记录数）
- 保持深色主题样式一致性
- 测试折叠/展开功能正常

## 任务二：养殖记录管理问题修复

### T2.1 后端：SeedlingRecord 增加批次号字段
- 在 SeedlingRecord.java 实体中增加 batchNo 字段
- 更新数据库表结构（执行 ALTER TABLE 添加 batch_no 列）
- 在 SeedlingRecordMapper 中增加按批次号查询的方法
- 在 SeedlingRecordController 中增加按批次号查询的接口

### T2.2 后端：修复记录查询排序
- 确保所有按区域查询记录的方法按时间倒序排列
- 检查 SeedlingRecordService、FeedingRecordService、HarvestRecordService

### T2.3 前端：AquacultureRecord.vue 修复
- 修复 ResizeObserver 错误（确保组件正确挂载/卸载）
- 添加 zoneId 校验，zoneId 为空时禁用添加按钮
- 苗种投放表单增加批次号字段（自动生成：区域ID + 日期 + 序号）
- 所有列表增加按时间倒序排序
- 表格增加备注列显示
- 优化表格列宽和内容展示
- 测试所有 Tab 页签切换和添加记录功能

## 任务三：溯源管理功能完善

### T3.1 后端：TraceabilityCode 数据模型扩展
- TraceabilityCode.java 增加 batchNo、zoneId、qrCodeUrl 字段
- 更新数据库表结构
- 在 TraceabilityCodeMapper 中增加相关查询方法

### T3.2 后端：溯源码生成逻辑
- 新增 POST /api/traceability/batch/{batchNo} 接口
- 实现 generateCodeForBatch 方法：根据苗种批次号生成溯源码
- 溯源码格式：TRC + 批次号 + UUID 前缀
- 确保溯源码与苗种批次号一对一关联

### T3.3 后端：二维码生成
- 在 build.gradle 中添加 ZXing 依赖
- 创建 QrCodeGenerator 工具类
- 新增 GET /api/traceability/qrcode/{code} 接口
- 二维码内容：溯源展示页面的完整 URL
- 返回图片格式（PNG）

### T3.4 后端：完善溯源信息查询
- 增强 TraceabilityService.getTraceabilityInfo() 方法
- 返回数据包含：苗种信息、养殖区域、投喂记录、疾病记录、水质数据（近30天）、捕捞信息
- 新增 GET /api/traceability/public/{code} 接口（公开访问，无需认证）

### T3.5 后端：苗种批次列表接口
- 新增 GET /api/seedlings/batches 接口
- 返回所有苗种批次号列表（去重），包含区域信息、投放日期、数量

### T3.6 前端：TraceManage.vue 改造
- 生成溯源码面板改造：
  - 使用 el-select 下拉选择苗种批次（从 /api/seedlings/batches 获取）
  - 选中批次后显示批次详情（区域、日期、数量）
  - 点击生成溯源码按钮
  - 生成后显示溯源码和二维码（使用 qrcode.vue 库）
- 溯源查询面板改造：
  - 输入溯源码查询后，以时间线形式展示完整溯源信息
  - 使用 el-timeline 组件展示从苗种投入到捕捞的全过程
  - 每个节点显示：类型图标、时间、关键信息摘要
- 安装 qrcode.vue 依赖

### T3.7 前端：TraceabilityPublic.vue 公开溯源页面
- 创建新的 Vue 页面 TraceabilityPublic.vue
- 路由配置：/traceability/:code（公开路由，无需登录）
- 页面布局：
  - 顶部：标题 + 溯源码 + 二维码
  - 主体：时间线展示完整溯源链路
    - 苗种投放信息（品种、数量、来源、日期）
    - 养殖区域信息（名称、位置、面积）
    - 饲料投喂记录列表（时间线节点）
    - 疾病防治记录列表（时间线节点）
    - 水质数据趋势（简化的 ECharts 图表）
    - 捕捞信息（日期、数量、重量）
  - 底部：系统信息
- 样式：适合手机浏览的响应式设计
- 无需登录即可访问

### T3.8 前端：路由配置更新
- 在 router/index.js 中添加 /traceability/:code 公开路由
- 确保该路由不经过登录验证中间件

## 任务四：测试与验证

### T4.1 功能测试
- 测试历史数据折叠功能
- 测试养殖记录添加和展示
- 测试溯源码生成和二维码展示
- 测试公开溯源页面访问

### T4.2 兼容性测试
- 测试不同浏览器下的显示效果
- 测试移动端溯源页面显示效果

### T4.3 数据库迁移
- 执行数据库变更 SQL
- 验证现有数据迁移正确
