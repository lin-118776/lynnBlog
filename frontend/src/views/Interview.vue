<template>
  <div class="page-container">
    <PageHead icon="message" title="面试广场" subtitle="公开/私有的面试经验复盘" />
    <el-card shadow="never">
      <EmptyState
        icon="message"
        title="面试广场正在筹备中"
        description="这里将收录公开与私有的面试经验复盘，帮助每一次面试都成为成长的阶梯。"
      >
        <el-button type="primary" @click="$router.push('/')">
          返回首页
        </el-button>
      </EmptyState>
    </el-card>
  </div>
</template>

<script setup>
import PageHead from '../components/PageHead.vue'
import EmptyState from '../components/EmptyState.vue'
</script>