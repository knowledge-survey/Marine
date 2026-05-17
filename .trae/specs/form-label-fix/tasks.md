# 表单标签文字显示问题修复 - 任务分解

## [ ] Task 1: 修复 DiseaseControl.vue 样式
- **优先级**: P0
- **依赖**: 无
- **描述**: 
  - 分析当前 DiseaseControl.vue 中的样式问题
  - 使用更精确和高优先级的样式选择器确保表单标签可见
  - 确保所有对话框元素都有正确的深色主题样式
- **验收标准**: AC-1, AC-5
- **测试要求**:
  - human-judgment: 打开病害对话框，检查标签是否可见
  - human-judgment: 检查对话框整体样式是否一致
- **备注**: 使用 !important 确保样式优先级，使用更具体的选择器

## [ ] Task 2: 修复 AquacultureRecord.vue 样式
- **优先级**: P0
- **依赖**: 无
- **描述**: 
  - 检查并修复 AquacultureRecord.vue 中的三个对话框样式
  - 确保所有表单标签在深色背景下清晰可见
  - 统一对话框样式
- **验收标准**: AC-2, AC-3, AC-4, AC-5
- **测试要求**:
  - human-judgment: 打开苗种投放对话框检查标签
  - human-judgment: 打开饲料投喂对话框检查标签
  - human-judgment: 打开捕捞记录对话框检查标签
  - human-judgment: 检查样式一致性
- **备注**: 参考 DiseaseControl.vue 的修复方法保持一致

## [ ] Task 3: 验证修复效果
- **优先级**: P1
- **依赖**: Task 1, Task 2
- **描述**: 
  - 在浏览器中测试所有对话框
  - 验证表单标签在各种情况下都能正常显示
  - 确保没有引入其他样式问题
- **验收标准**: AC-1, AC-2, AC-3, AC-4, AC-5
- **测试要求**:
  - human-judgment: 完整测试所有对话框功能
  - human-judgment: 检查不同屏幕尺寸下的显示效果
- **备注**: 如果可能，运行前端开发服务器进行实际测试
