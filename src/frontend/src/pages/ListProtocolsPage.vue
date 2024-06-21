<script setup lang="ts">
import FlightCard from '@/components/FlightCard.vue'
import { backendRequest } from '@/networking'
import { FlightData } from 'modellflug-logbuch-common-data'
import { ref } from 'vue'

const activeFlights = ref<FlightData[] | null | undefined>(null)
const finishedFlights = ref<FlightData[] | null | undefined>(null)

async function loadFlights() {
  const activePromise = backendRequest('/api/v1/flightlog/active/allUsers', {
    method: 'GET',
  })
  const completedPromise = backendRequest('/api/v1/flightlog/completed/allUsers/today', {
    method: 'GET',
  })
  const activeResponse = await activePromise
  const completedResponse = await completedPromise

  if (!activeResponse.ok) {
    activeFlights.value = undefined
    console.error(`Failed to load active flights: ${activeResponse.status} ${activeResponse.statusText}`)
  } else {
    activeFlights.value = (await activeResponse.json()) as FlightData[]
  }
  if (!completedResponse.ok) {
    finishedFlights.value = undefined
    console.error(`Failed to load finished flights: ${completedResponse.status} ${completedResponse.statusText}`)
  } else {
    finishedFlights.value = (await completedResponse.json()) as FlightData[]
  }
}

loadFlights()
</script>

<template>
  <h2 style="margin-bottom: 0.2em;">Aktive Piloten</h2>
  <p class="card-subtitle">Die folgenden Flugeinträge gehören zu <strong>aktuell aktiven</strong> Piloten.</p>
  <div v-if="activeFlights === undefined">
    Es ist ein Fehler beim Laden der aktiven Piloten aufgetreten.
  </div>
  <div v-else-if="activeFlights === null">
    <div class="spinner-grow text-indigo" role="status"></div>
  </div>
  <div v-else-if="activeFlights.length > 0" style="display: flex; flex-direction: column; gap: 1em;">
    <div v-for="flight in activeFlights" :key="flight.flightId">
      <FlightCard :flight="flight" :active="true" />
    </div>
  </div>
  <div v-else class="card" style="padding: 1em;">
    Aktuell sind keine aktiven Piloten registriert.
  </div>

  <h2 style="margin-top: 1em; margin-bottom: 0.2em;">Heute abgeschlossene Flüge</h2>
  <p class="card-subtitle">Die folgenden Flugeinträge wurden <strong>heute</strong> erstellt.</p>
  <div v-if="finishedFlights === undefined">
    Es ist ein Fehler beim Laden der abgeschlossenen Protokolle aufgetreten.
  </div>
  <div v-else-if="finishedFlights === null">
    <div class="spinner-grow text-indigo" role="status"></div>
  </div>
  <div v-else-if="finishedFlights.length > 0" style="display: flex; flex-direction: column; gap: 1em;">
    <div v-for="flight in finishedFlights" :key="flight.flightId">
      <FlightCard :flight="flight" :active="false" />
    </div>
  </div>
  <div v-else class="card" style="padding: 1em;">
    Es sind keine abgeschlossenen Protokolle vorhanden.
  </div>
</template>
