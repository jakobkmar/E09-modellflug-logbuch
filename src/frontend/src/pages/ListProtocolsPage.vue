<script setup lang="ts">
import FlightCard from '@/components/FlightCard.vue';
import { activeProtocols, finishedProtocols } from '@/store'
</script>

<template>
  <h2>Aktive Piloten</h2>
  <div v-if="activeProtocols.length > 0" style="display: flex; flex-direction: column; gap: 1em;">
    <div v-for="protocol in activeProtocols" :key="protocol.uniqueId">
      <FlightCard :flight="protocol"/>
    </div>
  </div>
  <div v-else>
    Aktuell sind keine aktiven Piloten registriert.
  </div>
  <h2 style="margin-top: 1em;">Abgeschlossene Protokolle</h2>
  <div v-if="finishedProtocols.length > 0" style="display: flex; flex-direction: column; gap: 1em;">
    <div v-for="protocol in finishedProtocols" :key="protocol.uniqueId">
      <FlightCard :flight="protocol"/>
    </div>
  </div>
  <div v-else>
    Es gibt bisher keine abgeschlossenen Protokolleinträge.
  </div>
</template>
