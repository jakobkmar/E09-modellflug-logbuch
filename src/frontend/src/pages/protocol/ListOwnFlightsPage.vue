<script setup lang="ts">
import { ref, watch } from 'vue'
import { getDateStringMonthAgo, getDateStringToday, getDateStringWeekAgo } from '@/utils/timeutil'
import { backendRequest } from '@/networking'
import { FlightData, FlightLogFilterRequest } from 'modellflug-logbuch-common-data'
import FlightCard from '@/components/FlightCard.vue'

const startDate = ref(getDateStringWeekAgo())
const endDate = ref(getDateStringToday())

const flights = ref<FlightData[] | null | undefined>(null)

async function loadFlights() {
  const filterRequest: FlightLogFilterRequest = {
    startDate: startDate.value,
    endDate: endDate.value,
  }

  const response = await backendRequest('/api/v1/flightlog/all/filtered', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(filterRequest),
  })

  if (!response.ok) {
    console.error(`Failed to load flights: ${response.status} ${response.statusText}`)
    flights.value = undefined
    return
  }

  flights.value = (await response.json()) as FlightData[]
}

watch([startDate, endDate], () => {
  loadFlights()
})

loadFlights()
</script>

<template>
  <h2 style="margin-bottom: 0.4em;">Deine Flüge</h2>
  <p class="card-subtitle">Hier findest du alle Flüge, die du bisher eingetragen hast.</p>

  <div style="display: flex; flex-direction: row; gap: 1em; flex-wrap: wrap;">
    <div style="flex-grow: 1;">
      <label class="form-label">von <span style="font-weight: normal">Datum</span></label>
      <input type="date" v-model="startDate" class="form-control" />
    </div>
    <div style="flex-grow: 1;">
      <label class="form-label">bis <span style="font-weight: normal">Datum</span></label>
      <input type="date" v-model="endDate" class="form-control" />
    </div>
  </div>
  <div style="display: flex; flex-direction: row; gap: 0.3em; margin-top: 0.6em;">
    <button type="button" class="btn" :class="{ 'btn-indigo': startDate == getDateStringToday() && endDate == getDateStringToday() }"
            @click="startDate = getDateStringToday(); endDate = getDateStringToday();">
      Heute
    </button>
    <button type="button" class="btn" :class="{ 'btn-indigo': startDate == getDateStringWeekAgo() && endDate == getDateStringToday() }"
            @click="startDate = getDateStringWeekAgo(); endDate = getDateStringToday();">
      Diese Woche
    </button>
    <button type="button" class="btn" :class="{ 'btn-indigo': startDate == getDateStringMonthAgo() && endDate == getDateStringToday() }"
            @click="startDate = getDateStringMonthAgo(); endDate = getDateStringToday();">
      Diesen Monat
    </button>
  </div>

  <div style="margin-top: 2em;">
    <div v-if="flights === undefined">
      Es ist ein Fehler beim Laden der Flüge aufgetreten.
    </div>
    <div v-else-if="flights === null">
      <div class="spinner-grow text-indigo" role="status"></div>
    </div>
    <div v-else-if="flights.length == 0">
      <div class="card" style="padding: 1em;">
        Im eingegebenen Zeitraum wurden keine Flugeinträge gefunden.
      </div>
    </div>
    <div v-else style="display: flex; flex-direction: column; gap: 1em;">
      <div v-for="flight in flights" :key="flight.flightId">
        <FlightCard :flight="flight" />
      </div>
    </div>
  </div>
</template>
