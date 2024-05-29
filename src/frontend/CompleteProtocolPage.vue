<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

import { activeProtocols } from '@/store';
import ProtocolCard from '@/components/ProtocolCard.vue';
import { getTimeString } from '@/utils/timeutil';

const protocol = computed(() => {
  const active = activeProtocols.value;
  return active.length > 0 ? active[0] : null;
});

const router = useRouter();

const additionalNotes = ref<string | null>(null);

function completeProtocol() {
  const current = protocol.value;
  if (current == null) {
    console.warn("Cannot complete protocol because it is null");
    return;
  }

  current.active = false;
  current.endTime = getTimeString();

  const notes = additionalNotes.value;
  if (notes != null && notes.length > 0) {
    current.notes = notes;
  }

  router.push('/');
}


const selectedTab = ref<'Pilot' | 'Tagespilot' | 'Gast'>('Pilot');
const lastName = ref('');
const firstName = ref('');
const password = ref('');
const confirmPassword = ref('');
const flightLeaderPermission = ref(false);
const flightLeaderPermissionFile = ref<File | null>(null);

const errors = ref<string[]>([]);

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
      console.log('Pilot registered:', {
        lastName: lastName.value,
        firstName: firstName.value,
        password: password.value,
        flightLeaderPermission: flightLeaderPermission.value,
        flightLeaderPermissionFile: reader.result,
        membershipType: selectedTab.value,
      });
      router.push('/pilot/success'); 
    };
    if (flightLeaderPermissionFile.value) {
      reader.readAsDataURL(flightLeaderPermissionFile.value);
    }
  }
}

function cancelRegistration() {
  router.push('/'); 
}
</script>

<template>
  <h1>Flugprotokoll abschließen</h1>
  <div v-if="protocol != null">
    <div class="card-subtitle mt-3 mb-2">Du kannst das folgende Protokoll abschließen:</div>
    <ProtocolCard :protocol="protocol" />
    <div class="card-subtitle mt-3 mb-2">Gab es besondere Ereignisse während des Flugbetriebs?</div>
    <textarea v-model="additionalNotes" class="form-control" placeholder="Notizen"/>
    <button class="btn btn-teal mt-3" @click="completeProtocol">Abschließen</button>
  </div>
  <div v-else>
    <p>Du kannst kein Protokoll abschließen, da kein offenes Protokoll mit deinem Account assoziiert ist.</p>
    <RouterLink to="/" class="btn btn-primary">
      Zurück zur Startseite
    </RouterLink>
  </div>

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
</template>

<style scoped>
.register-pilot {
  max-width: 600px;
  margin: auto;
  padding: 1em;
  background: #f7f8fa;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
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
