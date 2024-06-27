<script setup lang="ts">
import PageWrapper from './components/PageWrapper.vue'
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <PageWrapper v-if="!$route.fullPath.startsWith('/protocol/day')">
    <RouterView />
  </PageWrapper>
  <RouterView v-else />
</template>
