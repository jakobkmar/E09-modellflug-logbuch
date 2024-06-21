<script setup lang="ts">
import { FlightData } from 'modellflug-logbuch-common-data'

defineProps<{
  flight: FlightData,
  active: boolean,
}>()
</script>

<template>
  <div class="card" style="display: flex; flex-direction: column; padding: 1em;">
    <div style="display: flex; gap: 1em;">
      <h3 style="margin-bottom: 0.4em;">{{ flight.fullPilotName }}</h3>
      <span v-if="active" class="status status-green">
        <span class="status-dot status-dot-animated"></span>
        Aktiv
      </span>
      <span v-else class="status status-indigo">
        <span class="status-dot"></span>
        Abgeschlossen
      </span>
    </div>
    <div class="datagrid" style="gap: 0.5em; margin-top: 0.3em;">
      <div class="datagrid-item">
        <div style="display: flex; gap: 2em;">
          <div>
            <div class="datagrid-title">Anmeldedatum</div>
            <div class="datagrid-content">{{ flight.dateString }}</div>
          </div>
          <div>
            <div class="datagrid-title">Uhrzeit</div>
            <div class="datagrid-content">{{ flight.timePeriodString }}</div>
          </div>
        </div>
      </div>
      <div v-if="flight.signature != null" class="datagrid-item">
        <label class="datagrid-title">Unterschrift</label>
        <img
          :src="flight.signature" alt="Unterschrift"
          class="card"
          style="max-width: 200px;" />
      </div>
      <div
        v-if="flight.remarks != null && flight.remarks.length > 0"
        class="datagrid-item">
        <label class="datagrid-title">Anmerkungen</label>
        <div class="datagrid-content">{{ flight.remarks }}</div>
      </div>
    </div>
  </div>
</template>
