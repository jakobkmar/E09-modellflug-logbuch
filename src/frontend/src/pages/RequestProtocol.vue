<script setup lang="ts">
import { ref} from 'vue'
import { getTimeString, getDateToday, getDateYesterday } from '@/utils/timeutil'


function showDatePicker() {
  (document.getElementById("datepicker") as HTMLInputElement).showPicker()
}

function showDatePicker2() {
  (document.getElementById("datepicker2") as HTMLInputElement).showPicker()
}


function searchProtocoll() {
  if (dateInput > dateInput2){
    console.warn("Falsche Daten ausgewählt")
    return
  }
  
  else{
  // ToDo
  // Handle search logic here
  }
}

const dateInput = ref(getDateToday())
const dateInput2= ref(getDateToday())
const timeInput = ref(getTimeString())
const timeInput2= ref(getTimeString())



</script>

<template>
  <h1>Flugprotokolle ansehen</h1>
  <form class="column" style="--tblr-body-bg: #F7F8FA;">
    <div style="display: flex; flex-direction: column; gap: 1em;">
      Hier kannst du mit verschiedenen Optionen die Protokollabfrage filtern. Um das richtige Protokoll schneller zu finden.
    </div>


    <!--  Anfangszeitpunkt/Endzeitpunkt der Suche wählen  -->
    <fieldset class="form-fieldset">
      <label class="form-label">Datum</label>
      <p class="card-subtitle" style="margin-top: 0.5em;">Ab welchem Zeitpunkt möchtest du die Protokolle sehen?</p>
      <div class="column" style="gap: 0.5em; margin-bottom: 1em;">
        <div style="display: flex; gap: 0.4em; justify-content: center;">
          <input type="date" id="datepicker" v-model="dateInput" class="form-control">
          <button
              class="btn btn-secondary column"
              @click="showDatePicker"
              style="font-size: 0.7em; padding-top: 0.05em; padding-bottom: 0.05em; justify-self: end;"
            >
            <div>Datum wählen</div>
          </button>
        </div>
        <div style="display: flex; gap: 0.4em; margin-top: 0.5em; align-content: stretch;">
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
        <label class="form-label" style="margin-top: 1em;">Uhrzeit</label>
        <p class="card-subtitle">Ab welcher Uhrzeit sollen die Protokolle angezeigt werden?</p>
        <div style="display: felx; gap: 0.5em; align-items: end;">
          <div class="column" style="align-items: center; gap: 0.1em;">
            <input type="time" id="timepicker" v-model="timeInput" class="form-control" style="min-width: 8em; text-align: center;">
          </div>
        </div>
        <p class="card-subtitle" style="margin-top: 0.5em;">Bis zu welchem Zeitpunkt möchtest du die Protokolle sehen?</p>
        <div class="column" style="gap: 0.5em; margin-bottom: 1em;">
        <div style="display: flex; gap: 0.4em; justify-content: center;">
          <input type="date" v-model="dateInput2" class="form-control" id="datepicker2">
          <button
              class="btn btn-secondary column"
              @click="showDatePicker2"
              style="font-size: 0.7em; padding-top: 0.05em; padding-bottom: 0.05em; justify-self: end;"
            >
            <div>Datum wählen</div>
          </button>
        </div>
        <div style="display: flex; gap: 0.4em; margin-top: 0.5em; align-content: stretch;">
          <button
            @click="dateInput2 = getDateToday()"
            class="btn" :class="{ 'btn-azure': dateInput2 == getDateToday() }"
            style="flex-grow: 1;"
          >Heute</button>
          <button
            @click="dateInput2 = getDateYesterday()"
            class="btn" :class="{ 'btn-azure': dateInput2 == getDateYesterday() }"
            style="flex-grow: 1;"
          >Gestern</button>
        </div>
        <label class="form-label" style="margin-top: 1em;">Uhrzeit</label>
        <p class="card-subtitle">Bis zu welcher Uhrzeit sollen die Protokolle angezeigt werden?</p>
        <div style="display: felx; gap: 0.5em; align-items: end;">
          <div class="column" style="align-items: center; gap: 0.1em;">
            <input type="time" id="timepicker" v-model="timeInput2" class="form-control" style="min-width: 8em; text-align: center;">
          </div>
        </div>
      </div>
      </div>
    </fieldset>


    <!--  Name des Erstellers suchen  -->
    <fieldset class="form-fieldset">
      <label class="form-label">Name</label>
      <p class="card-subtitle">Gib einen Namen ein, falls du nach dem Ersteller des Protokolls filtern möchtest</p>
      <div class="column" style="gap: 0.5em; margin-bottom: 1em;">
        <div style="display: flex; gap: 0.4em; justify-content: center;">
          <input
          class="form-control"
          placeholder="Gib einen Namen ein, wenn gewünscht!"
          />
        </div>
      </div>
    </fieldset>


    <!--  Flugzeugmodelsuche  -->
    <fieldset class="form-fieldset">
      <label class="form-label">Model</label>
      <p class="card-subtitle">Gib einen Modelnamen ein, falls du nach dem verwendeten Model filtern möchtest</p>
      <div class="column" style="gap: 0.5em; margin-bottom: 1em;">
        <div style="display: flex; gap: 0.4em; justify-content: center;">
          <input
          class="form-control"
          placeholder="Gib einen Modelnamen ein, wenn gewünscht!"
          />
        </div>
      </div>
    </fieldset>


    <!-- Abschicken/Suchen -->
    <fieldset class="form-fieldset">
      <label class="form-label">Protkollsuche starten</label>
      <div style="display: flex; gap: 0.5em;">
        <button class="btn btn-primary" @click="searchProtocoll">Protokoll suchen</button> <!-- To-Do: Suche umsetzen-->
        <a href="#" class="btn btn-outline-danger">Abbrechen</a>
      </div>
    </fieldset>
  </form>
</template>
