<template>
  <div class="traceability-container">
    <el-card class="query-card">
      <h2>产品溯源查询</h2>
      <el-input v-model="code" placeholder="请输入溯源码或扫描二维码" style="width: 400px; margin-right: 10px" />
      <el-button type="primary" @click="handleQuery">查询</el-button>
      
      <div v-if="traceData" style="margin-top: 30px">
        <el-divider>溯源信息</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="溯源码">{{ traceData.code }}</el-descriptions-item>
          <el-descriptions-item label="描述">{{ traceData.description }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ traceData.createTime }}</el-descriptions-item>
          <el-descriptions-item label="扫描次数">{{ traceData.scanCount }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref } from 'vue'
import { getTraceabilityByCode } from '@/api/record'

export default {
  name: 'Traceability',
  setup() {
    const code = ref('')
    const traceData = ref(null)

    const handleQuery = async () => {
      if (!code.value) {
        alert('请输入溯源码')
        return
      }
      
      try {
        const res = await getTraceabilityByCode(code.value)
        traceData.value = res.data
      } catch (error) {
        alert('未找到溯源信息')
        traceData.value = null
      }
    }

    return {
      code,
      traceData,
      handleQuery
    }
  }
}
</script>

<style scoped>
.traceability-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 50px;
}

.query-card {
  width: 600px;
  text-align: center;
}

.query-card h2 {
  margin: 0 0 20px 0;
  color: #333;
}
</style>
