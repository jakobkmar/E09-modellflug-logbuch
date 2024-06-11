<script setup lang="ts">
import { ref, computed } from 'vue'
import { activeProtocols } from '@/store'
import ProtocolCard from '@/components/ProtocolCard.vue'
import { useRouter } from 'vue-router'
import { getTimeString } from '@/utils/timeutil'

const protocol = computed(() => {
  const active = activeProtocols.value
  if (active.length > 0) {
    return active[0]
  } else {
    return null
  }
})

const router = useRouter()

const additionalNotes = ref<string | null>(null)

function completeProtocol() {
  const current = protocol.value
  if (current == null) {
    console.warn("Cannot complete protocol because it is null")
    return
  }

  current.active = false
  current.endTime = getTimeString()

  const notes = additionalNotes.value
  if (notes != null && notes.length > 0) {
    current.notes = notes
  }

  router.push('/')
}
</script>

<template>
  <h1>Flugprotokoll abschließen</h1>
  <div v-if="protocol != null">
    <div class="card-subtitle mt-3 mb-2">Du kannst das folgende Protokoll abschließen:</div>
    <ProtocolCard :protocol="protocol" />
    <div class="card-subtitle mt-3 mb-2">Gab es besondere Ereignisse während des Flugbetriebs?</div>
    <textarea v-model="additionalNotes" class="form-control" placeholder="Notizen"/>
    <button class="btn btn-teal mt-3" @click="completeProtocol">Abschließen</button>
  </div>
  <div v-else>
    <p>Du kannst kein Protokoll abschließen, da kein offenes Protokoll mit deinem Account assoziiert ist.</p>
    <RouterLink to="/" class="btn btn-primary">
      Zurück zur Startseite
    </RouterLink>
  </div>
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
}
</style>
