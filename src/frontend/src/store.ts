import { ref, watch } from 'vue'

export const backendUrl = ref<string | null>(localStorage.getItem('backendUrl'))
watch(backendUrl, (newVal) => {
  if (newVal == null) {
    localStorage.removeItem('backendUrl')
  } else {
    localStorage.setItem('backendUrl', newVal)
  }
})
