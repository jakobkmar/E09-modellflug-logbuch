<script setup lang="ts">
import { useRouter } from 'vue-router'
import AlertCard from '@/components/AlertCard.vue'
import { ref } from 'vue'
import { backendRequest } from '@/networking'

const router = useRouter()

const errorAlert = ref<InstanceType<typeof AlertCard>>()

async function loginFlightDirector() {
  const response = await backendRequest('/api/v1/flightdirector/login', {
    method: 'POST',
  })

  if (!response.ok) {
    console.error(`Failed to login as flight director: ${response.status} ${response.statusText}`)
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
  <h2 style="margin-bottom: 0.6em;">Flugleiter werden</h2>
  <AlertCard title="Ein Problem ist aufgetreten" type="danger" ref="errorAlert" />
  <p>Als Flugleiter bist du für die <strong>Sicherheit und Organisation</strong> des Flugbetriebs verantwortlich.</p>
  <p>Insbesondere die <strong>Koordination</strong> zwischen mehreren Piloten muss durch den Flugleiter gewährleistet werden.</p>
  <div style="display: flex; flex-direction: row; gap: 0.8em;">
    <button @click="loginFlightDirector()" class="btn btn-primary">Ja, anmelden</button>
    <RouterLink to="/" class="btn btn-outline-danger">Nein, zurück</RouterLink>
  </div>
</template>
