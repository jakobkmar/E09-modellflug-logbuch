<script setup lang="ts">
import { FlightData } from 'modellflug-logbuch-common-data'
import { computed } from 'vue'
import { getDateStringToday, getTimeString } from '@/utils/timeutil'

const props = defineProps<{
  flight: FlightData,
}>()

const active = computed(() => {
  if (props.flight.date != getDateStringToday()) {
    return false
  }
  return props.flight.flightEnd == null ||
    props.flight.flightEnd > getTimeString()
})
</script>

<template>
  <div class="card" style="display: flex; flex-direction: column; padding: 1em;">
    <div style="display: flex; gap: 1em;">
      <h3 style="margin-bottom: 0.4em;">{{ flight.fullPilotName }}</h3>
      <span v-if="active" class="status status-green">
        <span class="status-dot status-dot-animated"></span>
        Aktiv
      </span>
      <span v-else class="status status-teal">
        Abgeschlossen
      </span>
    </div>
    <div class="datagrid"
         style="gap: 0.5em; margin-top: 0.3em; --tblr-datagrid-item-width: 9em; --tblr-datagrid-padding: 0.5em;">
      <div class="datagrid-item">
          <div>
            <div class="datagrid-title">Anmeldedatum</div>
            <div class="datagrid-content">{{ flight.dateString }}</div>
          </div>
      </div>
      <div class="datagrid-item">
        <div>
          <div class="datagrid-title">Uhrzeit</div>
          <div class="datagrid-content">{{ flight.timePeriodString }}</div>
        </div>
      </div>
      <div class="datagrid-item">
        <div>
          <div class="datagrid-title">Erste Hilfe</div>
          <div class="datagrid-content">{{ flight.checkedFirstAid ? 'ja' : 'nein' }}</div>
        </div>
      </div>
      <div class="datagrid-item">
        <div>
          <div class="datagrid-title">Modelltyp</div>
          <div class="datagrid-content">{{ flight.modelType }}</div>
        </div>
      </div>
      <div class="datagrid-item">
        <div>
          <div class="datagrid-title">Flug ID</div>
          <div class="datagrid-content">
            <div>
                <span class="id-display">
                  {{ flight.flightId }}
                </span>
            </div>
          </div>
        </div>
      </div>
      <div class="datagrid-item">
        <div>
          <div class="datagrid-title">Account ID</div>
          <div class="datagrid-content">
            <div>
                <span class="id-display">
                  {{ flight.accountId }}
                </span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="flight.signature != null || flight.remarks != null" style="margin-top: 0.8em;">
      <div class="hr-text" style="margin: 0 0 0.8em; color: #a7b2ba">Anhang</div>
      <div style="display: flex; flex-direction: column; gap: 0.2em;">
        <div
          v-if="flight.remarks != null && flight.remarks.length > 0"
          class="datagrid-item">
          <label class="datagrid-title">Anmerkungen</label>
          <div class="datagrid-content">{{ flight.remarks }}</div>
        </div>
        <div v-if="flight.signature != null" class="datagrid-item">
          <label class="datagrid-title">Unterschrift</label>
          <img
            :src="flight.signature" alt="Unterschrift"
            class="card"
            style="max-width: 200px;" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.datagrid-title {
  margin-bottom: 0;
}
</style>
