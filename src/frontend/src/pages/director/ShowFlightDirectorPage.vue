<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { backendRequest } from '@/networking'
import { FlightDirectorResponse } from 'modellflug-logbuch-common-data'
import { IconEdit, IconTrash } from '@tabler/icons-vue'

const router = useRouter()

const director = ref<FlightDirectorResponse | null | undefined>(undefined)
const errorResponse = ref<string | null>(null)

async function requestFlightDirector() {
  const response = await backendRequest('/api/v1/flightdirector', {
    method: 'GET',
  })

  if (!response.ok) {
    if (response.status === 404) {
      director.value = null
      return
    }

    const errorText = `${response.status} ${response.statusText}: ${await response.text()}`
    console.error(errorText)
    errorResponse.value = errorText
    director.value = undefined
    return
  }

  director.value = (await response.json()) as FlightDirectorResponse
}

requestFlightDirector()
</script>

<template>
  <h2 style="margin-bottom: 0.6em;">Aktueller Flugleiter</h2>
  <div v-if="errorResponse != null">
    <p>Es ist ein Fehler aufgetreten:</p>
    <p>{{ errorResponse }}</p>
  </div>
  <div v-else-if="director === undefined">
    <div class="spinner-grow text-indigo" role="status"></div>
  </div>
  <div v-else-if="director === null">
    <p>Es ist aktuell kein Flugleiter angemeldet.</p>
    <RouterLink to="/flightdirector/login" class="btn btn-indigo">Flugleiter werden</RouterLink>
  </div>
  <div v-else>
    <div class="card" style="padding: 1em; display: flex; flex-direction: column; gap: 0.6em;">
      <div style="display: flex; flex-direction: row; gap: 0.6em; align-items: center;">
        <span class="status-dot status-dot-animated status-purple"></span>
        <strong>Aktiver Account</strong>
      </div>
      <div class="datagrid-item">
        <div class="datagrid-title">Name</div>
        <div class="datagrid-content">{{ director.fullName }}</div>
      </div>
      <div class="datagrid" style="--tblr-datagrid-item-width: 9em; --tblr-datagrid-padding: 0.5em;">
        <div class="datagrid-item">
          <div class="datagrid-title">Nutzername</div>
          <div class="datagrid-content strong">{{ director.username }}</div>
        </div>
        <div class="datagrid-item">
          <div class="datagrid-title">Nutzer ID</div>
          <div class="datagrid-content">
            <span class="id-display">{{ director.userId }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
