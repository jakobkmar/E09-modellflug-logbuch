<script setup lang="ts">
import { ref } from 'vue';
import { useRoute } from 'vue-router';
import { protocols, Protocol } from '@/store';

const route = useRoute();
const isManageMode = route.query.manage !== undefined;

const activeProtocols = ref(protocols);

function deleteProtocol(index: number) {
  activeProtocols.value.splice(index, 1);
}
</script>

<template>
  <div class="card group-card">
    <h2 v-if="isManageMode">Protokolle verwalten</h2>
    <h2 v-else>Alle Protokolle</h2>
    <div v-if="activeProtocols.length === 0">Keine Protokolle verfügbar</div>
    <div v-else>
      <ul>
        <li v-for="(protocol, index) in activeProtocols" :key="protocol.id">
          <div>
            <strong>{{ protocol.date }}</strong> - {{ protocol.time }}
            <button v-if="isManageMode" class="btn btn-danger btn-sm" @click="deleteProtocol(index)">Löschen</button>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.group-card {
  display: flex;
  flex-direction: column;
  padding: 2em;
  padding-top: 1em;

  @media (max-width: 512px) {
    padding: 0.8em;
  }
}

ul {
  list-style: none;
  padding: 0;
}

li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5em 0;
  border-bottom: 1px solid #ccc;
}

li button {
  margin-left: 1em;
