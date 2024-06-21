<script setup lang="ts">
import { onMounted, ref } from 'vue'
import MouseCanvas from '@/components/MouseCanvas.vue'
import { useRouter } from 'vue-router'
import { getDateStringToday, getDateStringYesterday, getTimeString } from '@/utils/timeutil'
import { backendRequest } from '@/networking'
import { CreateFlightLogRequest } from 'modellflug-logbuch-common-data'
import { IconEngine, IconCarTurbine, IconBatteryAutomotive, IconDetails, IconAlertTriangle } from '@tabler/icons-vue'

const router = useRouter()

function showDatePicker() {
  (document.getElementById('datepicker') as HTMLInputElement).showPicker()
}

const dateInput = ref(getDateStringToday())
const timeInput = ref(getTimeString())
const checkedFirstAid = ref<boolean>()
const modelType = ref<string | null>(null)

const signatureCanvas = ref<InstanceType<typeof MouseCanvas>>()

const submitting = ref(false)

async function submitProtocol() {
  if (modelType.value == null) {
    console.error('Cannot submit protocol because model type is not set')
    return
  }

  const requestData: CreateFlightLogRequest = {
    date: dateInput.value,
    flightStart: timeInput.value,
    checkedFirstAid: checkedFirstAid.value!,
    modelType: modelType.value,
    signature: signatureCanvas.value!.getDataUrl(),
  }

  submitting.value = true
  const response = await backendRequest('/api/v1/flightlog/create', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestData),
  })
  submitting.value = false

  if (response.status == 201 /* Created */) {
    const flightId = (await response.json()) as number
    console.debug(`Created flight log with id ${flightId}`)
  } else if (response.ok) {
    console.warn(`Received unexpected status '${response.status} ${response.statusText}' when creating flight log`)
  } else {
    console.error(`Failed to create flight log: ${response.status} ${response.statusText}`)
    return
  }

  await router.push('/protocol/list')
}

onMounted(() => {
  checkedFirstAid.value = true
})
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
            @click="dateInput = getDateStringToday()"
            class="btn" :class="{ 'btn-azure': dateInput == getDateStringToday() }"
            style="flex-grow: 1;"
          >Heute</button>
          <button
            @click="dateInput = getDateStringYesterday()"
            class="btn" :class="{ 'btn-azure': dateInput == getDateStringYesterday() }"
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
            <div class="btn">manuell abmelden</div>
            <!-- TODO time select bei flug in vergangenheit -->
          </div>
          <div style="font-size: 0.8em">bis</div>
        </div>
      </div>
    </fieldset>

    <!-- Metadaten -->
    <fieldset class="form-fieldset" style="display: flex; flex-direction: column; gap: 1.5em;">
      <div>
        <label class="form-label">Flugzeugtyp</label>
        <p class="card-subtitle">Welches Modell fliegst du heute?</p>
        <div class="form-selectgroup plane-type-select">
          <label class="form-selectgroup-item">
            <input type="radio" v-model="modelType" value="Kolben" class="form-selectgroup-input">
            <span class="form-selectgroup-label"><IconEngine/> Kolben</span>
          </label>
          <label class="form-selectgroup-item">
            <input type="radio" v-model="modelType" value="Turbine" class="form-selectgroup-input" />
            <span class="form-selectgroup-label"><IconCarTurbine/> Turbine</span>
          </label>
          <label class="form-selectgroup-item">
            <input type="radio" v-model="modelType" value="Elektro" class="form-selectgroup-input" />
            <span class="form-selectgroup-label"><IconBatteryAutomotive/> Elektro</span>
          </label>
          <label class="form-selectgroup-item">
            <input type="radio" v-model="modelType" value="Segler" class="form-selectgroup-input" />
            <span class="form-selectgroup-label"><IconDetails/> Segler</span>
          </label>
        </div>
      </div>

      <div>
        <label class="form-label">Erste Hilfe</label>
        <p class="card-subtitle">Ist die Erste Hilfe-Ausrüstung vorhanden und geprüft?</p>
        <div style="display: flex; flex-direction: row; align-items: center; gap: 0.5em;">
          <input type="checkbox" class="form-check-input" id="first-aid-checkbox" v-model="checkedFirstAid" />
          <label for="first-aid-checkbox">Ja, überprüft</label>
        </div>
        <div v-if="checkedFirstAid == false"
             class="alert alert-warning" role="alert" style="margin-top: 1em; margin-bottom: 0;">
          <div class="d-flex">
            <div style="margin-right: 1em;">
              <IconAlertTriangle size="2em"/>
            </div>
            <div>
              <h4 class="alert-title">Bitte demnächst nachholen!</h4>
              <div class="text-secondary">Erste Hilfe-Ausrüstung ist gesetzlich vorgeschrieben.</div>
            </div>
          </div>
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

    <!-- Abschicken / Beenden -->
    <fieldset class="form-fieldset">
      <label class="form-label">Abschließen</label>
      <div style="display: flex; gap: 0.5em">
        <button @click="submitProtocol" class="btn btn-primary" type="button" :disabled="submitting">
          <span v-if="submitting" class="spinner-border spinner-border-sm me-2" role="status"></span>
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

  .plane-type-select {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(10em, 1fr));

    .form-selectgroup-label {
      display: flex;
      flex-direction: row;
      align-items: center;
      gap: 0.5em;

      .tabler-icon {
        height: 1.5em;
      }
    }
  }
}
</style>
