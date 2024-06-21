<script setup lang="ts">
import { computed, ref } from 'vue'

const props = defineProps<{
  type: string,
  title: string,
}>()

const alertClass = computed(() => `alert-${props.type}`)

const messages = ref<string[]>([])
const dismissed = ref(false)

function show(newMessage: string) {
  messages.value = newMessage.split('\n')
  dismissed.value = false
}

function dismiss() {
  dismissed.value = true
  messages.value = []
}

defineExpose({ show })
</script>

<template>
  <div v-if="messages.length > 0 && !dismissed"
       class="alert alert-dismissible" :class="alertClass">
    <div class="d-flex" style="gap: 0.4em;">
      <div>
        <slot/>
      </div>
      <div>
        <h4 class="alert-title">{{ title }}</h4>
        <div v-for="message in messages" class="text-secondary">
          {{ message }}
        </div>
      </div>
    </div>
    <a class="btn-close" @click="dismiss()"></a>
  </div>
</template>
