<script setup lang="ts">
import { ref } from 'vue'
import { Protocol, protocols } from '@/store'
import MouseCanvas from '@/components/MouseCanvas.vue'
import { useRouter } from 'vue-router'
import { getDateToday, getDateYesterday, getTimeString } from '@/utils/timeutil'

const router = useRouter()

function showDatePicker() {
  (document.getElementById("datepicker") as HTMLInputElement).showPicker()
}

const dateInput = ref(getDateToday())
const timeInput = ref(getTimeString())

const signatureCanvas = ref<InstanceType<typeof MouseCanvas> | null>(null)

function submitProtocol() {
  const signatureUrl = signatureCanvas.value?.getDataUrl()
  if (signatureUrl == null) {
    console.warn("Missing signature URL, cannot submit protocol")
    return
  }
  protocols.value.unshift(new Protocol(dateInput.value, timeInput.value, signatureUrl))
  router.push('/protocol/list')
}
</script>

<template>
  <h2>Flugprotokoll beginnen</h2>
  <form class="column" style="--tblr-body-bg: #F7F8FA;">
    <!-- Zeitdaten -->
    <fieldset class="form-fieldset">
      <label class="form-label">Datum</label>
      <p class="card-subtitle">Für welchen Tag gilt dieses Protokoll? Du kannst auch ein vergessenes Protokoll nachtragen.</p>
      <div class="column" style="gap: 0.5em; margin-bottom: 1em;">
        <div style="display: flex; gap: 0.4em; justify-content: center;">
          <input type="date" v-model="dateInput" class="form-control" id="datepicker">
          <button
              class="btn btn-secondary column"
              @click="showDatePicker"
              style="font-size: 0.7em; padding-top: 0.05em; padding-bottom: 0.05em; justify-self: end;"
            >
            <div>Datum</div>
            <div>wählen</div>
          </button>
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
          <input type="time" v-model="timeInput" class="form-control" style="min-width: 8em; text-align: center;">
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
          <div style="font-size: 0.8em;">bis</div>
        </div>
      </div>
    </fieldset>

    <!-- Unterschreiben -->
    <fieldset class="form-fieldset">
      <label class="form-label">Unterschrift</label>
      <p class="card-subtitle">Eine Unterschrift ist gesetzlich vorgeschrieben. Male sie dazu mit dem Finger oder der Maus.</p>
      <div style="border: 1px solid rgb(214, 214, 214); border-radius: 5px;">
        <MouseCanvas :width=600 :height=200 style-width="100%" ref="signatureCanvas"/>
      </div>
    </fieldset>

    <!-- Flugzeugdaten -->


    <!-- Abschicken / Beenden -->
    <fieldset class="form-fieldset">
      <label class="form-label">Abschließen</label>
      <div style="display: flex; gap: 0.5em;">
        <button class="btn btn-primary" @click="submitProtocol">Protokoll erstellen</button>
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
