<script setup lang="ts">
import { ref } from 'vue'
import { backendRequest } from '@/networking'
import { FlightDirectorFilterRequest, FlightDirectorResponse } from 'modellflug-logbuch-common-data'

const flightDirectors = ref<FlightDirectorResponse[] | null | undefined>(null)
const requestLimit = 1000

async function loadFlightDirectors() {
  const filterRequest: FlightDirectorFilterRequest = {
    limit: requestLimit,
  }

  const response = await backendRequest('/api/v1/flightdirector/all/filtered', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(filterRequest),
  })

  if (!response.ok) {
    console.error(`Failed to load flight directors: ${response.status} ${response.statusText}`)
    flightDirectors.value = undefined
    return
  }

  flightDirectors.value = (await response.json()) as FlightDirectorResponse[]
}

loadFlightDirectors()
</script>

<template>
  <h2>Flugleiter Historie</h2>
  <p class="card-subtitle">Hier findest die letzten {{ requestLimit }} Flugleiter.</p>

  <div v-if="flightDirectors === null">
    <div class="spinner-grow text-indigo" role="status"></div>
  </div>
  <div v-else-if="flightDirectors === undefined">
    <p>Es ist ein Fehler beim Laden der Flugleiter aufgetreten.</p>
  </div>
  <div v-else-if="flightDirectors.length > 0">
    <div class="table-responsive">
      <table class="table table-vcenter">
        <thead>
        <tr>
          <th>Nutzername</th>
          <th>Datum</th>
          <th>von</th>
          <th>bis</th>
        </tr>
        </thead>
        <tbody>
          <tr v-for="director in flightDirectors" :key="director.username">
            <td>{{ director.username }}</td>
            <td>{{ director.date }}</td>
            <td>{{ director.startTime }}</td>
            <td>{{ director.endTime }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
  <div v-else>
    <p class="card" style="padding: 1em;">Es sind noch keine Einträge vorhanden.</p>
  </div>
</template>
