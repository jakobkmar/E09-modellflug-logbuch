<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import MouseCanvas from '@/components/MouseCanvas.vue'
import { useRouter } from 'vue-router'
import { getDateStringToday, getDateStringYesterday, getTimeString } from '@/utils/timeutil'
import { backendRequest } from '@/networking'
import { CreateFlightLogRequest } from 'modellflug-logbuch-common-data'
import {
  IconAlertTriangle,
  IconBatteryAutomotive,
  IconCarTurbine,
  IconClock,
  IconDetails,
  IconEngine,
  IconExclamationCircle,
  IconLogout,
} from '@tabler/icons-vue'
import AlertCard from '@/components/AlertCard.vue'

const router = useRouter()

function showDatePicker() {
  (document.getElementById('datepicker') as HTMLInputElement).showPicker()
}

const dateInput = ref(getDateStringToday())
const timeInput = ref(getTimeString())
const timeInputEnd = ref<string>('')
const checkedFirstAid = ref<boolean>()
const modelType = ref<string | null>(null)

const chooseSpecificTime = ref(false)
const requireSpecificEndTime = computed(() => {
  return dateInput.value != getDateStringToday() || chooseSpecificTime.value
})

const signatureCanvas = ref<InstanceType<typeof MouseCanvas>>()

const errorAlert = ref<InstanceType<typeof AlertCard>>()
const submitting = ref(false)

async function submitFlightlog() {
  const issues: string[] = []
  if (requireSpecificEndTime.value) {
    if (timeInputEnd.value == null || timeInputEnd.value.length == 0) {
      issues.push('Gib einen Zeitpunkt für das Flugende an!')
    }
  }
  if (modelType.value == null) {
    issues.push('Gib einen Flugzeugtyp an!')
  }
  if (signatureCanvas.value?.isEmpty()) {
    issues.push('Du musst unterschreiben!')
  }
  if (issues.length > 0) {
    errorAlert.value?.show(issues.join('\n'))
    return
  }

  const requestData: CreateFlightLogRequest = {
    date: dateInput.value,
    flightStart: timeInput.value,
    flightEnd: requireSpecificEndTime.value ? timeInputEnd.value : null,
    checkedFirstAid: checkedFirstAid.value!,
    modelType: modelType.value!,
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
    if (response.status == 409 /* Conflict */) {
      errorAlert.value?.show('Du hast bereits einen aktiven Flug')
    } else if (response.status == 400 /* Bad Request */) {
      errorAlert.value?.show(await response.text())
    } else {
      errorAlert.value?.show(`${response.status} ${response.statusText}`)
    }
    console.error(`Failed to create flight log: ${response.status} ${response.statusText}`)
    return
  }

  await router.push('/')
}

onMounted(() => {
  checkedFirstAid.value = true
})
</script>

<template>
  <h2>Flugeintrag beginnen</h2>

  <AlertCard title="Ein Problem ist aufgetreten" type="danger" ref="errorAlert">
    <IconExclamationCircle />
  </AlertCard>

  <form class="column" style="--tblr-body-bg: #f7f8fa;">
    <!-- Zeitdaten -->
    <fieldset class="form-fieldset">
      <label class="form-label">Datum</label>
      <p class="card-subtitle">
        Für welchen Tag gilt dieser Flugeintrag? Du kannst auch einen vergessenen Eintrag nachtragen.
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
          <input type="time" v-model="timeInput"
                 class="form-control" style="min-width: 8em; text-align: center;" />
          <div style="font-size: 0.8em;">von</div>
        </div>
        <div class="column" style="align-items: center; gap: 0.1em">
          <div v-if="!requireSpecificEndTime" class="dropdown">
            <div class="btn dropdown-toggle" data-bs-toggle="dropdown">Abmeldung</div>
            <div class="dropdown-menu">
              <span class="dropdown-header">Zeit für Flugende</span>
              <div class="dropdown-item">
                <IconLogout size="1.5em" style="margin-right: 0.5em;" />
                manuelle Abmeldung
              </div>
              <div class="dropdown-item" @click="chooseSpecificTime = true">
                <IconClock size="1.5em" style="margin-right: 0.5em;" />
                Uhrzeit wählen (für Nachtragung)
              </div>
            </div>
          </div>
          <input v-else type="time" v-model="timeInputEnd"
                 class="form-control" style="min-width: 8em; text-align: center;" />
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
        <button @click="submitFlightlog" class="btn btn-primary" type="button" :disabled="submitting">
          <span v-if="submitting" class="spinner-border spinner-border-sm me-2" role="status"></span>
          Flug erstellen
        </button>
        <RouterLink to="/" class="btn btn-outline-danger">Abbrechen</RouterLink>
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

      /*noinspection CssUnusedSymbol*/
      .tabler-icon {
        height: 1.5em;
      }
    }
  }

  .dropdown-item {
    &:hover {
      background-color: #eaf0f3;
    }
  }
}
</style>
