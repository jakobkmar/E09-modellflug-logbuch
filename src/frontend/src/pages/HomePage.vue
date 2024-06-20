<script setup lang="ts">
import { activeProtocols } from '@/store'
import { useLoginSessionStore } from "@/session";

const sessionStore = useLoginSessionStore()
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
      <h1 style="align-self: center;">Protokollführung</h1>
      <RouterLink to="/protocol/create" class="btn btn-indigo" v-if="activeProtocols.length == 0">
        <div style="padding-top: 1em; padding-bottom: 1em;">
          <h2>Als Pilot anmelden</h2>
          <div>Damit beginnst du ein neues Protokoll.</div>
          <div style="font-size: 0.9em; opacity: 70%;">
            Angemeldet als '{{ sessionStore.loginSession.loginResponse.username }}'
          </div>
        </div>
      </RouterLink>
      <RouterLink to="/protocol/complete" class="btn btn-orange" v-else-if="activeProtocols.length > 0">
        <div style="padding-top: 1em; padding-bottom: 1em;">
          <h2>Als Pilot abmelden</h2>
          <div>Damit schließt du dein aktives Protokoll ab.</div>
        </div>
      </RouterLink>
      <div style="display: flex; gap: 0.5em; margin-top: 0.8em; flex-wrap: wrap;">
        <RouterLink to="/protocol/list" class="btn btn-info" style="flex-grow: 1;">
          Protokolle einsehen
        </RouterLink>
        <RouterLink to="/protocol/manage" class="btn btn-secondary disabled" style="flex-grow: 1;">
          Protokolle verwalten
        </RouterLink>
      </div>
    </div>
    <div v-if="sessionStore.loginSession.loginResponse.isAdminUnsafe" class="card group-card">
      <h1 style="align-self: center;">Administration</h1>
      <RouterLink to="/admin/manage-users" class="btn btn-outline-pink"  style="margin-top: 0.5em;">
        <div style="padding-top: 0.4em; padding-bottom: 0.4em;">
          <h3>Nutzerverwaltung</h3>
          <div>Nutzerprofile erstellen und anpassen.</div>
        </div>
      </RouterLink>
      <RouterLink to="/admin/manage-models" class="btn btn-outline-purple" style="margin-top: 1em;">
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
</style>
