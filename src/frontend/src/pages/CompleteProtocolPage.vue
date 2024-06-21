<script setup lang="ts">
import { onMounted, ref } from 'vue'
import FlightCard from '@/components/FlightCard.vue'
import { useRouter } from 'vue-router'
import { backendRequest } from '@/networking'
import { CompleteFlightLogRequest, FlightData } from 'modellflug-logbuch-common-data'

const router = useRouter()

const flightData = ref<FlightData | null | undefined>(null)
const additionalNotes = ref<string | null>(null)

async function loadFlightlog() {
  const response = await backendRequest('/api/v1/flightlog/active', {
    method: 'GET',
  })
  if (!response.ok) {
    console.error(`Failed to load active flightlog: ${response.status} ${response.statusText}`)
    flightData.value = undefined
    return
  }
  flightData.value = await response.json()
}

const submitting = ref(false)

async function completeFlightlog() {
  const flight = flightData.value
  if (flight == null) {
    console.error("Cannot complete flightlog because flight data is null")
    return
  }

  let notes = additionalNotes.value
  if (notes != null) {
    if (notes.length == 0) {
      notes = null
    }
  }

  const requestData: CompleteFlightLogRequest = {
    flightId: flight.flightId,
    remarks: notes,
  }

  submitting.value = true
  const response = await backendRequest('/api/v1/flightlog/complete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestData),
  })
  submitting.value = false

  if (!response.ok) {
    console.error(`Failed to complete flightlog: ${response.status} ${response.statusText}`)
    return
  }

  await router.push('/')
}

onMounted(() => {
  loadFlightlog()
})
</script>

<template>
  <h1>Flug abschließen</h1>
  <div v-if="flightData != null">
    <div class="card-subtitle mt-3 mb-2">Du kannst den folgenden Flugeintrag abschließen:</div>
    <FlightCard :flight="flightData" :active="true" />
    <div class="card-subtitle mt-3 mb-2">Gab es besondere Ereignisse während des Flugbetriebs?</div>
    <textarea v-model="additionalNotes" class="form-control" placeholder="Notizen"/>
    <button class="btn btn-teal mt-3" @click="completeFlightlog">Abschließen</button>
  </div>
  <div v-else-if="flightData === null">
    <div class="text-center">
      <div class="spinner-grow text-indigo" role="status"></div>
    </div>
  </div>
  <div v-else-if="flightData === undefined">
    <p>Du kannst keinen Flug abschließen, da kein offener Flug mit deinem Account assoziiert ist.</p>
    <RouterLink to="/" class="btn btn-primary">
      Zurück zur Startseite
    </RouterLink>
  </div>
</template>
