<script setup lang="ts">
import { useLoginSessionStore } from '@/session'
import { computed, onMounted, onUnmounted, ref } from 'vue'
import type { AppState } from 'modellflug-logbuch-common-data'
import { backendRequest } from '@/networking'

const sessionStore = useLoginSessionStore()

const appState = ref<AppState | null>(null)
const activePilots = computed((): number[] => {
  return appState.value?.activePilots ?? []
})
const flightLogCount = ref<number | null | undefined>(null)

let appStateSocket: WebSocket

async function requestInfo() {
  const countRequest = await backendRequest('/api/v1/flightlog/count', {
    method: 'GET',
  })

  if (countRequest.ok) {
    flightLogCount.value = (await countRequest.json()) as number
  } else {
    console.error(`Failed to load flight log count: ${countRequest.status} ${countRequest.statusText}`)
    flightLogCount.value = undefined
  }
}

requestInfo()

onMounted(() => {
  appStateSocket = new WebSocket('ws://localhost:8080/api/v1/appstate/live')
  appStateSocket.onopen = () => {
    // console.debug("Opened WebSocket connection to app state")
  }
  appStateSocket.onerror = () => {
    console.error("AppState websocket failed")
  }
  appStateSocket.onmessage = (event) => {
    appState.value = JSON.parse(event.data) as AppState
  }
})

onUnmounted(() => {
  appStateSocket.close()
})
</script>

<template>
  <div v-if="sessionStore.loginSession == null">
    <div class="card" style="padding: 1em;">
      <p>Du bist aktuell nicht angemeldet.</p>
      <RouterLink to="/login">Jetzt anmelden</RouterLink>
    </div>
  </div>
  <div v-else style="display: flex; flex-direction: column; gap: 1em;">
    <div class="card group-card">
      <h1 style="align-self: center;">Flüge</h1>
      <div class="btn-column loading-block" :class="{ 'loading': appState == null }">
        <RouterLink v-if="!activePilots.includes(sessionStore.loginSession.sessionData.userId)"
                    to="/flight/create" class="btn btn-indigo">
          <div style="padding-top: 1em; padding-bottom: 1em;">
            <h2>Als Pilot anmelden</h2>
            <div>Damit beginnst du einen neuen Flug.</div>
            <div style="font-size: 0.9em;">
              <span style="opacity: 70%;">Anmelden als </span>
              <span>{{ sessionStore.loginSession.sessionData.username }}</span>
            </div>
          </div>
        </RouterLink>
        <RouterLink to="/flight/complete" class="btn btn-orange" v-else>
          <div style="padding-top: 1em; padding-bottom: 1em;">
            <h2>Als Pilot abmelden</h2>
            <div>Damit schließt du den aktuellen Flug ab.</div>
          </div>
        </RouterLink>
        <div style="display: flex; gap: 0.5em; margin-top: 0.8em; flex-wrap: wrap;">
          <RouterLink to="/flight/list-active" class="btn" style="flex-grow: 1;">
            Aktive Piloten
            <span v-if="activePilots.length > 0" class="badge bg-green text-white ms-2">
              {{ activePilots.length }}
            </span>
            <span v-else class="badge bg-dark text-white ms-2">0</span>
          </RouterLink>
          <RouterLink to="/flight/list-mine" class="btn"
                      style="flex-grow: 1; display: inline-flex; flex-direction: row; gap: 0.4em;">
            <span>Meine Flüge</span>
            <span class="badge badge-pill bg-gray-800 text-dark">
              <span v-if="flightLogCount === null"><span class="animated-dots"></span></span>
              <span v-else-if="flightLogCount === undefined">?</span>
              <span v-else>{{ flightLogCount }}</span>
            </span>
          </RouterLink>
        </div>
      </div>
    </div>
    <div class="card group-card">
      <h1 style="align-self: center;">Flugleiter</h1>
      <div class="btn-column loading-block" :class="{ 'loading': appState == null }">
        <RouterLink v-if="appState?.currentFlightDirector != sessionStore.loginSession.sessionData.userId"
                    to="/flightdirector/login" class="btn btn-azure">
          <div style="padding-top: 1em; padding-bottom: 1em;">
            <h2 style="margin-bottom: 0.3em;">Flugleiter werden</h2>
            <div>Damit meldest du dich als verantwortlicher Flugleiter an.</div>
          </div>
        </RouterLink>
        <RouterLink to="/flightdirector/logout" class="btn btn-orange" v-else>
          <div style="padding-top: 1em; padding-bottom: 1em;">
            <h2>Als Flugleiter abmelden</h2>
            <div>Bitte informiere die anderen Piloten über diesen Schritt!</div>
          </div>
        </RouterLink>
        <div style="display: flex; gap: 0.5em; margin-top: 0.8em; flex-wrap: wrap;">
          <RouterLink to="/flightdirector/current" class="btn" style="flex-grow: 1;">
            Aktueller Flugleiter
            <span v-if="appState?.currentFlightDirector == null" class="badge bg-orange text-white ms-2">fehlt</span>
            <span v-else class="badge bg-green text-white ms-2">präsent</span>
          </RouterLink>
          <RouterLink to="/flightdirector/history"
                      class="btn"
                      :class = "{ 'disabled': !sessionStore.canSeeAllLogs() }"
                      style="flex-grow: 1;">
            Historie
          </RouterLink>
        </div>
      </div>
    </div>
    <div v-if="sessionStore.loginSession.sessionData.isAdminUnsafe" class="card group-card">
      <h1 style="align-self: center;">Administration</h1>
      <RouterLink to="/admin/manage-users" class="btn btn-outline-pink"  style="margin-top: 0.5em;">
        <div style="padding-top: 0.4em; padding-bottom: 0.4em;">
          <h3>Nutzerverwaltung</h3>
          <div>Nutzerprofile erstellen und anpassen.</div>
        </div>
      </RouterLink>
      <RouterLink to="/admin/manage-models" class="btn btn-outline-purple disabled" style="margin-top: 1em;">
        <div style="padding-top: 0.4em; padding-bottom: 0.4em;">
          <h3>Modellverwaltung</h3>
          <div>Flugmodelle erstellen und verwalten.</div>
        </div>
      </RouterLink>
    </div>
  </div>
</template>

<style scoped>
.group-card {
  display: flex;
  flex-direction: column;
  padding: 1em 2em 2em;

  @media (max-width: 512px) {
    padding: 0.8em;
  }
}

.btn {
  white-space: normal;
}

.btn-column {
  display: flex;
  flex-direction: column;
}

.loading-block {
  position: relative;

  &.loading {
    & > * {
      visibility: hidden;
    }
  }

  &.loading::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    border-radius: 10px;
    background: linear-gradient(to right, #e2eff3, white, #e2eff3);
    background-size: 200%;
    animation: loading-gradient 2s linear infinite;
  }
}

@keyframes loading-gradient {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: -100% 50%;
  }
  100% {
    background-position: -200% 50%;
  }
}
</style>
