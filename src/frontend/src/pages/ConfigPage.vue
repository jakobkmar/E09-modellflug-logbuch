<script setup lang="ts">
import { backendUrl } from '@/store'
</script>

<template>
  <h2>Konfiguration</h2>

  <p class="form-label">Backend-Server URL</p>
  <p class="card-subtitle">
    Die URL des Servers, auf dem die Backend-API läuft.
    <strong>Lasse dieses Feld leer</strong> um Requests an den gleichen Server zu senden, von dem die Webseite geladen wurde.
  </p>
  <p class="card-subtitle">
    Für die Entwicklung kann der Wert auf <strong><code>localhost:8080</code></strong> gesetzt werden.
  </p>
  <p class="card-subtitle">
    Trage die URL ohne das Protokoll ein, z.B. <strong><code>localhost:8080</code></strong> oder <strong><code>example.com</code></strong>.
  </p>
  <input type="text" v-model="backendUrl" class="form-control" placeholder="URL" />
</template>
