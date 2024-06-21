<script setup lang="ts">
import { ref } from 'vue'
import MouseCanvas from '@/components/MouseCanvas.vue'
import { useRouter } from 'vue-router'
import { getDateToday, getDateYesterday, getTimeString } from '@/utils/timeutil'
import { backendRequest } from '@/networking'
import { CreateFlightLogRequest } from 'modellflug-logbuch-common-data'

const router = useRouter()

function showDatePicker() {
  (document.getElementById('datepicker') as HTMLInputElement).showPicker()
}

const dateInput = ref(getDateToday())
const timeInput = ref(getTimeString())

const signatureCanvas = ref<InstanceType<typeof MouseCanvas>>()

async function submitProtocol() {
  const requestData: CreateFlightLogRequest = {
    flightStart: `${dateInput.value}T${timeInput.value}`,
    checkedFirstAid: true, // TODO get from user input
    modelType: 'TODO', // TODO get from user input
    // this is the longest, so put it last to avoid other data being truncated in dev tools
    // signature: new Int8Array(signatureCanvas.value!.getImageData()),
    signature: signatureCanvas.value!.getDataUrl(),
  }
  const response = await backendRequest('/api/v1/flightlog/create', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestData),
  })
  if (response.status == 201 /* Created */) {
    const flightId = (await response.json()) as number
    console.debug('Created flight log with id', flightId)
  } else if (response.ok) {
    console.warn(`Received unexpected status '${response.status} ${response.statusText}' when creating flight log`)
  } else {
    console.error(`Failed to create flight log: ${response.status} ${response.statusText}`)
  }
  await router.push('/protocol/list')
}
</script>

<template>
  <h2>Flugprotokoll beginnen</h2>
  <form class="column" style="--tblr-body-bg: #f7f8fa;">
    <!-- Zeitdaten -->
    <fieldset class="form-fieldset">
      <label class="form-label">Datum</label>
      <p class="card-subtitle">
        Für welchen Tag gilt dieses Protokoll? Du kannst auch ein vergessenes Protokoll nachtragen.
      </p>
      <div class="column" style="gap: 0.5em; margin-bottom: 1em;">
        <div style="display: flex; gap: 0.4em; justify-content: center;">
          <input type="date" v-model="dateInput" class="form-control" id="datepicker" />
          <div
            @click="showDatePicker"
            class="btn btn-secondary column"
            style="font-size: 0.7em; padding-top: 0.05em; padding-bottom: 0.05em; justify-self: end;"
          >
            <div>Datum</div>
            <div>wählen</div>
          </div>
        </div>
        <div style="display: flex; gap: 0.4em; align-content: stretch;">
          <button
            @click="dateInput = getDateToday()"
            class="btn" :class="{ 'btn-azure': dateInput == getDateToday() }"
            style="flex-grow: 1;"
          >Heute</button>
          <button
            @click="dateInput = getDateYesterday()"
            class="btn" :class="{ 'btn-azure': dateInput == getDateYesterday() }"
            style="flex-grow: 1;"
          >Gestern</button>
        </div>
      </div>
      <label class="form-label">Zeitraum</label>
      <p class="card-subtitle">In welchem Zeitraum möchtest du fliegen?</p>
      <div style="display: flex; gap: 0.5em; align-items: end;">
        <div class="column" style="align-items: center; gap: 0.1em">
          <input type="time" v-model="timeInput" class="form-control" style="min-width: 8em; text-align: center;" />
          <div style="font-size: 0.8em;">von</div>
        </div>
        <div class="column" style="align-items: center; gap: 0.1em">
          <div class="dropdown">
            <div class="btn dropdown-toggle" data-bs-toggle="dropdown">manuell abmelden</div>
            <div class="dropdown-menu">
              <div class="dropdown-item">manuell abmelden</div>
              <div class="dropdown-item">in 1h</div>
              <div class="dropdown-item">in 2h</div>
              <div class="dropdown-item">in 3h</div>
              <div class="dropdown-item">in 4h</div>
            </div>
          </div>
          <div style="font-size: 0.8em">bis</div>
        </div>
      </div>
    </fieldset>

    <!-- Unterschreiben -->
    <fieldset class="form-fieldset">
      <label class="form-label">Unterschrift</label>
      <p class="card-subtitle">
        Eine Unterschrift ist gesetzlich vorgeschrieben. Male sie dazu mit dem Finger oder der Maus.
      </p>
      <div style="border: 1px solid rgb(214, 214, 214); border-radius: 5px">
        <MouseCanvas :width="900" :height="300" style-width="100%" ref="signatureCanvas" />
      </div>
    </fieldset>

    <!-- Flugzeugdaten -->

    <!-- Abschicken / Beenden -->
    <fieldset class="form-fieldset">
      <label class="form-label">Abschließen</label>
      <div style="display: flex; gap: 0.5em">
        <button @click="submitProtocol" class="btn btn-primary" type="button">
          Protokoll erstellen
        </button>
        <a href="#" class="btn btn-outline-danger">Abbrechen</a>
      </div>
    </fieldset>
  </form>
</template>

<style scoped>
.column {
  display: flex;
  flex-direction: column;
}

form {
  .card-subtitle {
    margin-bottom: 0.8em;
    font-size: 0.9em;
  }
}
</style>
