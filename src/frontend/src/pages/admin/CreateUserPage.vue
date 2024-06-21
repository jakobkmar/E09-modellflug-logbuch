<script setup lang="ts">
import { backendRequest } from '@/networking.js'
import { useRouter } from 'vue-router'
import { CreateAccountRequest } from 'modellflug-logbuch-common-data'
import { ref } from 'vue'
import { IconAlertTriangle, IconBaselineDensityMedium, IconId, IconIdBadge2, IconKey, IconUser } from '@tabler/icons-vue'
import AlertCard from '@/components/AlertCard.vue'

const router = useRouter()

const errorAlert = ref<InstanceType<typeof AlertCard>>()

const username = ref('')
const setPassword = ref<boolean>(false)
const password = ref('')
const firstName = ref('')
const lastName = ref('')
const registrationNumber = ref('')
const isAdmin = ref(false)
const canSeeAllLogs = ref(false)

async function createUser() {
  const requestData: CreateAccountRequest = {
    username: username.value,
    password: setPassword ? (password.value ? password.value : null) : null,
    firstName: firstName.value,
    lastName: lastName.value ? lastName.value : null,
    registrationNumber: registrationNumber.value ? registrationNumber.value : null,
    isAdmin: isAdmin.value,
    canSeeAllLogs: canSeeAllLogs.value,
  }

  const response = await backendRequest('/api/v1/account/create', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestData),
  })

  if (!response.ok) {
    console.error(`Failed to create user: ${response.status} ${response.statusText}`)
    if (response.status == 409 /* Conflict */) {
      errorAlert.value?.show('Nutzername bereits vergeben')
    } else if (response.status == 400 /* Bad Request */) {
      errorAlert.value?.show(await response.text())
    } else {
      errorAlert.value?.show(`${response.status} ${response.statusText}`)
    }
    return
  }

  await router.push('/admin/manage-users')
}
</script>

<template>
  <h2>Benutzer erstellen</h2>
  <AlertCard title="Ein Problem ist aufgetreten" type="danger" ref="errorAlert">
    <IconAlertTriangle/>
  </AlertCard>
  <form class="card create-user-card">
    <!-- hidden to prevent autofill -->
    <input type="text" style="display:none;">
    <input type="password" style="display:none;">

    <div style="margin-bottom: 0.4em;">
      <div class="form-label">Anmeldedaten</div>
      <div class="input-icon" style="margin-bottom: 0.6em;">
        <span class="input-icon-addon" style="padding: 0.4em;"><IconUser /></span>
        <input v-model="username" type="text" class="form-control" placeholder="Nutzername" />
      </div>
      <div class="input-icon" style="margin-bottom: 0.6em;">
        <span class="input-icon-addon" style="padding: 0.4em;"><IconKey /></span>
        <input v-model="password" class="form-control" placeholder="Passwort"
               :disabled="!setPassword"
               :type="password.length > 0 ? 'password' : 'text'" /> <!-- no autofill at first -->
      </div>
      <div class="form-check form-switch">
        <input v-model="setPassword" class="form-check-input" type="checkbox">
        <span class="form-check-label">Passwort vordefinieren</span>
      </div>
    </div>

    <div style="margin-bottom: 0.4em;">
      <div class="form-label">Persönliche Daten</div>
      <div class="input-icon" style="margin-bottom: 0.6em;">
        <span class="input-icon-addon" style="padding: 0.4em;"><IconId /></span>
        <input v-model="firstName" type="text" class="form-control" placeholder="Vorname" />
      </div>
      <div class="input-icon">
        <span class="input-icon-addon" style="padding: 0.4em;"><IconBaselineDensityMedium /></span>
        <input v-model="lastName" type="text" class="form-control" placeholder="Nachname" />
      </div>
    </div>

    <div style="margin-bottom: 0.4em;">
      <div class="form-label">Vereinsdaten</div>
      <div class="input-icon">
        <span class="input-icon-addon" style="padding: 0.4em;"><IconIdBadge2 /></span>
        <input v-model="registrationNumber" type="text" class="form-control" placeholder="Mitgliedsnummer" />
      </div>
    </div>

    <div>
      <div class="form-label">Berechtigungen</div>
      <div class="form-check">
        <input v-model="isAdmin" type="checkbox" class="form-check-input">
        <span class="form-check-label">Administrator</span>
        <small class="text-muted">kann jegliche Einstellungen und Daten einsehen und ändern</small>
      </div>
      <div class="form-check">
        <input v-model="canSeeAllLogs" type="checkbox" class="form-check-input">
        <span class="form-check-label">alle Protokolle sehen</span>
        <small class="text-muted">kann alle Protokolle einsehen, auch von anderen Nutzern</small>
      </div>
    </div>

    <div style="display: flex; flex-direction: row; gap: 0.5em; flex-wrap: wrap; flex-grow: 1;">
      <button type="button" class="btn btn-primary" @click="createUser">
        Erstellen
      </button>
      <button type="button" class="btn btn-outline-danger"
           @click="router.push('/admin/manage-users')">
        Abbruch
      </button>
    </div>
  </form>
</template>

<style scoped>
.create-user-card {
  padding: 2em;
  display: flex;
  flex-direction: column;
  gap: 1em;

  @media (max-width: 400px) {
    padding: 1em;
  }
}

.form-check {
  margin-bottom: 0.2em;
}
</style>
