<template>
  <div class="page-container">
    <PageHead icon="sparkles" title="公开穿搭墙" subtitle="兴趣收藏与穿搭展示" />
    <el-card shadow="never">
      <EmptyState
        icon="sparkles"
        title="穿搭墙正在筹备中"
        description="这里将展示兴趣收藏与穿搭灵感，分享属于你的风格与热爱。"
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