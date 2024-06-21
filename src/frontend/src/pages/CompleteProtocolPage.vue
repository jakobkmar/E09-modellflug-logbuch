<script setup lang="ts">
import { onMounted, ref } from 'vue'
import FlightCard from '@/components/FlightCard.vue'
import { useRouter } from 'vue-router'
import { backendRequest } from '@/networking'
import { CompleteFlightLogRequest, FlightData } from 'modellflug-logbuch-common-data'

const router = useRouter()

const flightData = ref<FlightData | null | undefined>(null)
const additionalNotes = ref<string | null>(null)

async function loadProtocol() {
  const response = await backendRequest('/api/v1/flightlog/getActive', {
    method: 'GET',
  })
  if (!response.ok) {
    console.error(`Failed to load active protocol: ${response.status} ${response.statusText}`)
    flightData.value = undefined
    return
  }
  flightData.value = await response.json()
}

const submitting = ref(false)

async function completeProtocol() {
  const flight = flightData.value
  if (flight == null) {
    console.error("Cannot complete protocol because flight data is null")
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
    console.error(`Failed to complete protocol: ${response.status} ${response.statusText}`)
    return
  }

  await router.push('/')
}

onMounted(() => {
  loadProtocol()
})
</script>

<template>
  <h1>Flugprotokoll abschließen</h1>
  <div v-if="flightData != null">
    <div class="card-subtitle mt-3 mb-2">Du kannst das folgende Protokoll abschließen:</div>
    <FlightCard :flight="flightData" :active="true" />
    <div class="card-subtitle mt-3 mb-2">Gab es besondere Ereignisse während des Flugbetriebs?</div>
    <textarea v-model="additionalNotes" class="form-control" placeholder="Notizen"/>
    <button class="btn btn-teal mt-3" @click="completeProtocol">Abschließen</button>
  </div>
  <div v-else-if="flightData === null">
    <div class="text-center">
      <div class="spinner-grow text-indigo" role="status"></div>
    </div>
  </div>
  <div v-else-if="flightData === undefined">
    <p>Du kannst kein Protokoll abschließen, da kein offenes Protokoll mit deinem Account assoziiert ist.</p>
    <RouterLink to="/" class="btn btn-primary">
      Zurück zur Startseite
    </RouterLink>
  </div>
</template>
