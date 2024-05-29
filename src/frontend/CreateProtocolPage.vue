<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { protocols, Protocol } from '@/store';
import MouseCanvas from '@/components/MouseCanvas.vue';
import { getDateToday, getTimeString } from '@/utils/timeutil';

const router = useRouter();

function showDatePicker() {
  (document.getElementById("datepicker") as HTMLInputElement).showPicker();
}

const dateInput = ref(getDateToday());
const startTimeInput = ref(getTimeString());
const endTimeInput = ref('');
const flightLeaderName = ref('');
const firstAidChecked = ref(false);
const pilotNames = ref('');
const flightStartEnd = ref('');
const channel = ref('');
const type = ref('');
const modelName = ref('');
const guestNames = ref('');
const noiseCertificateFile = ref<File | null>(null);
const signatureCanvas = ref<InstanceType<typeof MouseCanvas> | null>(null);

const errors = ref<string[]>([]);

function validateProtocolForm() {
  errors.value = [];
  if (!dateInput.value) errors.value.push('Datum ist erforderlich');
  if (!startTimeInput.value) errors.value.push('Dienstbeginn ist erforderlich');
  if (!endTimeInput.value) errors.value.push('Dienstende ist erforderlich');
  if (!flightLeaderName.value) errors.value.push('Flugleitername ist erforderlich');
  if (!pilotNames.value) errors.value.push('Pilotennamen sind erforderlich');
  if (!flightStartEnd.value) errors.value.push('Flugbeginn/-ende ist erforderlich');
  if (!channel.value) errors.value.push('Kanal ist erforderlich');
  if (!type.value) errors.value.push('Typ ist erforderlich');
  if (!modelName.value) errors.value.push('Modellbezeichnung ist erforderlich');
  if (!guestNames.value) errors.value.push('Tagesmitgliednamen sind erforderlich');
  if (!noiseCertificateFile.value) errors.value.push('Lärmpass ist erforderlich');
  if (!signatureCanvas.value?.getDataUrl()) errors.value.push('Unterschrift Flugleiter ist erforderlich');
  return errors.value.length === 0;
}

function submitProtocol() {
  if (validateProtocolForm()) {
    const signatureUrl = signatureCanvas.value?.getDataUrl();
    const reader = new FileReader();
    reader.onload = () => {
      protocols.value.unshift(new Protocol(
        dateInput.value,
        startTimeInput.value,
        signatureUrl,
        {
          endTime: endTimeInput.value,
          flightLeaderName: flightLeaderName.value,
          firstAidChecked: firstAidChecked.value,
          pilotNames: pilotNames.value,
          flightStartEnd: flightStartEnd.value,
          channel: channel.value,
          type: type.value,
          modelName: modelName.value,
          guestNames: guestNames.value,
          noiseCertificateFile: reader.result,
        }
      ));
      router.push('/protocol/list');
    };
    reader.readAsDataURL(noiseCertificateFile.value);
  } else {
    alert('Bitte füllen Sie alle erforderlichen Felder aus.');
  }
}
</script>

<template>
  <h2>Protokoll anlegen</h2>
  <form class="column" style="--tblr-body-bg: #F7F8FA;">
    <fieldset class="form-fieldset">
      <label class="form-label">Datum</label>
      <input type="date" v-model="dateInput" class="form-control" id="datepicker">
      <label class="form-label">Dienstbeginn</label>
      <input type="time" v-model="startTimeInput" class="form-control">
      <label class="form-label">Dienstende</label>
      <input type="time" v-model="endTimeInput" class="form-control">
    </fieldset>

    <fieldset class="form-fieldset">
      <label class="form-label">Flugleitername</label>
      <input type="text" v-model="flightLeaderName" class="form-control">
      <label class="form-label">Erste-Hilfe-Einrichtung überprüft</label>
      <input type="checkbox" v-model="firstAidChecked" class="form-control">
    </fieldset>

    <fieldset class="form-fieldset">
      <label class="form-label">Pilotennamen</label>
      <input type="text" v-model="pilotNames" class="form-control">
      <label class="form-label">Flugbeginn/-ende</label>
      <input type="text" v-model="flightStartEnd" class="form-control">
      <label class="form-label">Kanal</label>
      <input type="text" v-model="channel" class="form-control">
      <label class="form-label">Typ</label>
      <input type="text" v-model="type" class="form-control">
      <label class="form-label">Modellbezeichnung</label>
      <input type="text" v-model="modelName" class="form-control">
    </fieldset>

    <fieldset class="form-fieldset">
      <label class="form-label">Tagesmitgliednamen</label>
      <input type="text" v-model="guestNames" class="form-control">
      <label class="form-label">Lärmpass</label>
      <input type="file" @change="e => noiseCertificateFile.value = e.target.files[0]" class="form-control">
    </fieldset>

    <fieldset class="form-fieldset">
      <label class="form-label">Unterschrift Flugleiter</label>
      <div style="border: 1px solid rgb(214, 214, 214); border-radius: 5px;">
        <MouseCanvas ref="signatureCanvas" :width="900" :height="300" style="width: 100%;"/>
      </div>
    </fieldset>

    <fieldset class="form-fieldset">
      <label class="form-label">Abschließen</label>
      <div style="display: flex; gap: 0.5em;">
        <button type="button" class="btn btn-primary" @click="submitProtocol">Protokoll erstellen</button>
        <button type="button" class="btn btn-outline-danger" @click="router.push('/')">Abbrechen</button>
      </div>
    </fieldset>

    <div v-if="errors.length" class="errors">
      <ul>
        <li v-for="error in errors" :key="error">{{ error }}</li>
      </ul>
    </div>
  </form>
</template>

<style scoped>
.column {
  display: flex;
  flex-direction: column;
}

form {
  .form-fieldset {
    margin-bottom: 1em;
  }
}

.errors {
  background: #f8d7da;
  color: #721c24;
  padding: 0.5em;
  margin-top: 1em;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
}
</style>
