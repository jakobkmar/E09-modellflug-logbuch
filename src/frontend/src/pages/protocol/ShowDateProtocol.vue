<script setup lang="ts">
import { computed, onMounted, onUpdated, ref } from 'vue'
import { DayProtocolData, type FlightData, FlightDirectorData, GetDayProtocolRequest } from 'modellflug-logbuch-common-data'
import { backendRequest } from '@/networking'

const props = defineProps<{
  date: string,
}>()

const protocolData = ref<DayProtocolData | null | undefined>(null)

const flights = computed<FlightData[]>(() => {
  return protocolData.value?.sortedFlights ?? []
})
const flightDirectors = computed<FlightDirectorData[]>(() => {
  return protocolData.value?.sortedFlightDirectors ?? []
})

async function requestDayProtocol() {
  const requestData: GetDayProtocolRequest = {
    date: props.date,
  }

  const response = await backendRequest('/api/v1/protocol/day', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestData),
  })

  if (!response.ok) {
    console.error(`Failed to request day protocol: ${response.status} ${response.statusText}`)
    protocolData.value = undefined
    return
  }

  protocolData.value = (await response.json()) as DayProtocolData

  setTimeout(() => {
    window.print()
  }, 500)
}

requestDayProtocol()
</script>

<template>
  <div>
    <h2 style="margin-bottom: 0.4em;">Tagesprotokoll für {{ (new Date(props.date)).toLocaleDateString() }}</h2>

    <div v-if="protocolData === null" class="card">
      <div class="spinner-grow text-indigo" role="status"></div>
    </div>

    <div v-else-if="protocolData === undefined">
      <p>Es kann kein Tagesprotokoll für das angegebene Datum gefunden werden, da ein Fehler aufgetreten ist.</p>
    </div>

    <div v-else>
      <h3>Flüge</h3>
      <ul>
        <li v-for="flight in flights" :key="flight.flightId" style="margin: 0.2em;">
          <div style="display: flex; flex-direction: row; gap: 1em;">
            <div>
              <div>
                <strong>Pilot:</strong> {{ flight.fullPilotName }}
              </div>
              <div>
                <strong>Modelltyp:</strong> {{ flight.modelType }}
              </div>
              <div>
                <strong>Zeitraum:</strong> {{ flight.timePeriodString }}
              </div>
            </div>
            <div v-if="flight.signature != null">
              <img
                :src="flight.signature" alt="Unterschrift"
                style="max-width: 200px;" />
            </div>
          </div>
        </li>
      </ul>

      <h3>Flugleiter</h3>
      <ul>
        <li v-for="flightDirector in flightDirectors" :key="flightDirector.username" style="margin: 0.2em;">
          <div>
            <strong>Name:</strong> {{ flightDirector.fullName }}
          </div>
          <div>
            <strong>Zeitraum:</strong> {{ `${flightDirector.startTime} ➔ ${flightDirector.endTime}` }}
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>
