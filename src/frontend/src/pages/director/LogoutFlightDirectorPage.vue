<script setup lang="ts">
import { useRouter } from 'vue-router'
import AlertCard from '@/components/AlertCard.vue'
import { ref } from 'vue'
import { backendRequest } from '@/networking'

const router = useRouter()

const errorAlert = ref<InstanceType<typeof AlertCard>>()

async function logoutFlightDirector() {
  const response = await backendRequest('/api/v1/flightdirector/logout', {
    method: 'POST',
  })

  if (!response.ok) {
    console.error(`Failed to logout as flight director: ${response.status} ${response.statusText}`)
    let text = await response.text()
    if (text.length > 0) {
      text = `: ${text}`
    }
    errorAlert.value?.show(`${response.status} ${response.statusText}${text}`)
    return
  }

  await router.push('/')
}
</script>

<template>
  <h2 style="margin-bottom: 0.6em;">Als Flugleiter abmelden</h2>
  <AlertCard title="Ein Problem ist aufgetreten" type="danger" ref="errorAlert" />
  <p>Informiere bitte die anderen anwesenden Piloten darüber.</p>
  <p>Gegebenenfalls muss <strong>ein neuer Flugleiter bestimmt werden</strong>, dies sollte unter Absprache passieren.</p>
  <div style="display: flex; flex-direction: row; gap: 0.8em;">
    <button @click="logoutFlightDirector()" class="btn btn-primary">Ja, abmelden</button>
    <RouterLink to="/" class="btn btn-outline-danger">Nein, zurück</RouterLink>
  </div>
</template>
