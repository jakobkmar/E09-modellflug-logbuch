<script setup lang="ts">
import { computed, ref } from 'vue'
import { getDateStringToday } from '@/utils/timeutil'

const dateInput = ref<string>(getDateStringToday())
const targetRoute = computed(() => `/protocol/day/${dateInput.value}`)
</script>

<template>
  <h2 style="margin-bottom: 0.4em;">Tagesprotokoll erstellen</h2>
  <p class="card-subtitle">Hier kannst du Flugdaten und Flugleiter Einträge aggregieren und ein Tagesprotokoll exportieren.</p>

  <div style="max-width: 15em;">
    <label class="form-label">Datum</label>
    <input type="date" v-model="dateInput" class="form-control" />
  </div>

  <div style="margin-top: 1.8em;">
    <RouterLink :to=targetRoute target="_blank"
                type="button" class="btn btn-primary">Tagesprotokoll anfragen</RouterLink>
  </div>
</template>
