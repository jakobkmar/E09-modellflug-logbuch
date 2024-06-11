<script setup lang="ts">
import { ref } from 'vue'
import { activeProtocols, pilots, Pilot } from '@/store'
import { useRouter } from 'vue-router'

const loginErrors = ref<string[]>([])
const username = ref('')
const loginPassword = ref('')

const router = useRouter()

function validateLoginForm() {
  loginErrors.value = []
  if (!username.value) loginErrors.value.push('Nutzername ist erforderlich')
  if (!loginPassword.value) loginErrors.value.push('Passwort ist erforderlich')
  return loginErrors.value.length === 0
}

function loginPilot() {
  if (validateLoginForm()) {
    const user = pilots.value.find(pilot => pilot.username === username.value && pilot.password === loginPassword.value)
    if (user) {
      console.log('Pilot logged in:', user)
      router.push('/protocol/create') // Redirect to the protocol creation page after login
    } else {
      loginErrors.value.push('Ungültige Anmeldedaten')
    }
  }
}
</script>

<template>
  <div style="display: flex; flex-direction: column; gap: 1em;">
    <div class="card group-card">
      <h1 style="align-self: center;">Protokollführung</h1>
      <form @submit.prevent="loginPilot" class="form">
        <div v-if="loginErrors.length" class="errors">
          <ul>
            <li v-for="error in loginErrors" :key="error">{{ error }}</li>
          </ul>
        </div>
        <div class="form-group">
          <label for="username">Nutzername</label>
          <input id="username" v-model="username" type="text" class="form-control" />
        </div>
        <div class="form-group">
          <label for="loginPassword">Passwort</label>
          <input id="loginPassword" v-model="loginPassword" type="password" class="form-control" />
        </div>
        <div class="buttons">
          <button type="submit" class="btn btn-primary">Anmelden</button>
        </div>
      </form>
      <RouterLink to="/protocol/complete" class="btn btn-orange" v-if="activeProtocols.length > 0">
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
    <div class="card group-card">
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
  padding: 2em;
  padding-top: 1em;

  @media (max-width: 512px) {
    padding: 0.8em;
  }
}

.form {
  display: flex;
  flex-direction: column;
}

.form-group {
  margin-bottom: 1em;
}

.errors {
  background: #f8d7da;
  color: #721c24;
  padding: 0.5em;
  margin-bottom: 1em;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
}

.buttons {
  display: flex;
  gap: 0.5em;
}

.btn {
  align-self: flex-start;
}
</style>
