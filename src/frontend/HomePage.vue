<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { activeProtocols, pilots, Pilot } from '@/store';

const selectedTab = ref<'Pilot' | 'Tagespilot' | 'Gast'>('Pilot');
const lastName = ref('');
const firstName = ref('');
const password = ref('');
const confirmPassword = ref('');
const flightLeaderPermission = ref(false);
const flightLeaderPermissionFile = ref<File | null>(null);

const errors = ref<string[]>([]);
const loginErrors = ref<string[]>([]);
const username = ref('');
const loginPassword = ref('');

const router = useRouter();

function validatePilotForm() {
  errors.value = [];
  if (!lastName.value) errors.value.push('Nachname ist erforderlich');
  if (!firstName.value) errors.value.push('Vorname ist erforderlich');
  if (!password.value || password.value.length < 8) errors.value.push('Passwort muss mindestens 8 Zeichen lang sein');
  if (password.value !== confirmPassword.value) errors.value.push('Passwörter stimmen nicht überein');
  if (selectedTab.value !== 'Gast' && !flightLeaderPermissionFile.value) {
    errors.value.push('Flugleitererlaubnis ist erforderlich');
  }
  return errors.value.length === 0;
}

function registerPilot() {
  if (validatePilotForm()) {
    const reader = new FileReader();
    reader.onload = () => {
      pilots.value.push(new Pilot(
        pilots.value.length + 1,
        lastName.value,
        firstName.value,
        `${firstName.value}.${lastName.value}`.toLowerCase(),
        password.value,
        flightLeaderPermission.value,
        reader.result,
        selectedTab.value
      ));
      router.push('/');
    };
    if (flightLeaderPermissionFile.value) {
      reader.readAsDataURL(flightLeaderPermissionFile.value);
    } else {
      reader.onload();
    }
  }
}

function cancelRegistration() {
  router.push('/');
}

function validateLoginForm() {
  loginErrors.value = [];
  if (!username.value) loginErrors.value.push('Nutzername ist erforderlich');
  if (!loginPassword.value) loginErrors.value.push('Passwort ist erforderlich');
  return loginErrors.value.length === 0;
}

function loginPilot() {
  if (validateLoginForm()) {
    const user = pilots.value.find(pilot => pilot.username === username.value && pilot.password === loginPassword.value);
    if (user) {
      console.log('Pilot logged in:', user);
      router.push('/'); 
    } else {
      loginErrors.value.push('Ungültige Anmeldedaten');
    }
  }
}
</script>

<template>
  <div style="display: flex; flex-direction: column; gap: 1em;">
    <div class="card group-card">
      <h1 style="align-self: center;">Protokollführung</h1>
      <RouterLink to="/protocol/create" class="btn btn-indigo" v-if="activeProtocols.length == 0">
        <div style="padding-top: 1em; padding-bottom: 1em;">
          <h2>Als Pilot anmelden</h2>
          <div>Damit beginnst du ein neues Protokoll.</div>
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
    <div class="card group-card">
      <h1 style="align-self: center;">Administration</h1>
      <RouterLink to="/admin/manage-users" class="btn btn-outline-pink" style="margin-top: 0.5em;">
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
    <div class="card group-card">
      <h2>Neuen Piloten registrieren</h2>
      <div class="tabs">
        <button :class="{ active: selectedTab === 'Pilot' }" @click="selectedTab = 'Pilot'">Pilot</button>
        <button :class="{ active: selectedTab === 'Tagespilot' }" @click="selectedTab = 'Tagespilot'">Tagespilot</button>
        <button :class="{ active: selectedTab === 'Gast' }" @click="selectedTab = 'Gast'">Gast</button>
      </div>
      <form @submit.prevent="registerPilot" class="form">
        <div v-if="errors.length" class="errors">
          <ul>
            <li v-for="error in errors" :key="error">{{ error }}</li>
          </ul>
        </div>
        <div class="form-group">
          <label for="lastName">Nachname</label>
          <input id="lastName" v-model="lastName" type="text" class="form-control" />
        </div>
        <div class="form-group">
          <label for="firstName">Vorname</label>
          <input id="firstName" v-model="firstName" type="text" class="form-control" />
        </div>
        <div class="form-group">
          <label for="password">Passwort</label>
          <input id="password" v-model="password" type="password" class="form-control" />
        </div>
        <div class="form-group">
          <label for="confirmPassword">Passwort bestätigen</label>
          <input id="confirmPassword" v-model="confirmPassword" type="password" class="form-control" />
        </div>
        <div class="form-group" v-if="selectedTab !== 'Gast'">
          <label for="flightLeaderPermission">Flugleitererlaubnis</label>
          <input id="flightLeaderPermission" type="file" @change="e => flightLeaderPermissionFile.value = e.target.files[0]" class="form-control" />
        </div>
        <div class="buttons">
          <button type="button" class="btn btn-secondary" @click="cancelRegistration">Abbrechen</button>
          <button type="submit" class="btn btn-primary">Bestätigen</button>
        </div>
      </form>
    </div>
    <div class="card group-card">
      <h2>Pilot anmelden</h2>
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

.tabs {
  display: flex;
  gap: 0.5em;
  margin-bottom: 1em;
}

.tabs button {
  flex-grow: 1;
  padding: 0.5em;
  border: 1px solid #ccc;
  background: #f7f8fa;
  cursor: pointer;
}

.tabs button.active {
  background: #ccc;
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
