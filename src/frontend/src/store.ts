import { ref, watch } from 'vue'

export const backendUrl = ref<string>(localStorage.getItem('backendUrl') ?? '')
watch(backendUrl, (newVal) => {
  localStorage.setItem('backendUrl', newVal)
})
