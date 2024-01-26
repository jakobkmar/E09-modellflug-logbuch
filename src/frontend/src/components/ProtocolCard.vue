<script setup lang="ts">
import { Protocol } from '@/store'
import { computed, toRefs } from 'vue'

const props = defineProps<{
  protocol: Protocol
}>()

const { protocol } = toRefs(props)

const timeString = computed(() => {
  const currProtocol = protocol.value
  if (currProtocol.endTime == null) {
    return `${currProtocol.startTime} Uhr`
  } else {
    return `${currProtocol.startTime} - ${currProtocol.endTime} Uhr`
  }
})
</script>

<template>
  <div class="card" style="display: flex; flex-direction: column; padding: 1em;">
    <div style="display: flex; gap: 1em;">
      <h3>{{ protocol.user }}</h3>
      <span v-if="protocol.active" class="status status-green">
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
            <div class="datagrid-content">{{ protocol.getGermanDateString() }}</div>
          </div>
          <div>
            <div class="datagrid-title">Uhrzeit</div>
            <div class="datagrid-content">{{ timeString }}</div>
          </div>
        </div>
      </div>
      <div class="datagrid-item">
        <label class="datagrid-title">Unterschrift</label>
        <img
          :src="protocol.signatureDataUrl"
          class="card"
          style="max-width: 200px;" />
      </div>
      <div
        v-if="protocol.notes != null && protocol.notes.length > 0"
        class="datagrid-item">
        <label class="datagrid-title">Anmerkungen</label>
        <div class="datagrid-content">{{ protocol.notes }}</div>
      </div>
    </div>
  </div>
</template>
