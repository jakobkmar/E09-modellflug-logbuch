<script setup lang="ts">
import { protocols } from '@/store'
</script>

<template>
  <h2>Protokolle einsehen</h2>
  <div v-if="protocols.length > 0" style="display: flex; flex-direction: column; gap: 1em;">
    <div
      v-for="protocol in protocols" :key="protocol.uniqueId"
      class="card" style="display: flex; flex-direction: column; padding: 1em;">
      <div style="display: flex; gap: 1em;">
        <h3>{{ protocol.user }}</h3>
        <span class="status status-green">
          <span class="status-dot status-dot-animated"></span>
          Aktiv
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
              <div class="datagrid-content">{{ protocol.time }}</div>
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
      </div>
    </div>
  </div>
  <div v-else>
    Es gibt bisher keine Protokolleinträge.
  </div>
</template>
