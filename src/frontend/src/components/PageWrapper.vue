<script setup>
import { useLoginSessionStore } from '@/session'
import { useRouter } from 'vue-router'

const router = useRouter()
const sessionStore = useLoginSessionStore()

function logout() {
  sessionStore.logout(
    () => router.push('/'),
    (response) => alert(`Fehler beim Abmelden: ${response.status} ${response.statusText}`),
  )
}
</script>

<template>
  <div class="flex-center">
    <div class="top-bar card mb-2">
      <RouterLink to="/">Startseite</RouterLink>
      <RouterLink to="/protocol">Protokollierung</RouterLink>
      <div style="width: 100%"></div>
      <div v-if="sessionStore.loginSession != null" class="logout-link" @click="logout">Logout</div>
    </div>
  </div>
  <div class="flex-center">
    <div class="content-wrapper">
      <slot />
    </div>
  </div>
</template>

<style scoped>
.flex-center {
  display: flex;
  justify-content: center;
}

.content-wrapper {
  padding-left: 1em;
  padding-right: 1em;
  padding-top: 0.2em;

  width: 512px;

  @media (max-width: 512px) {
    width: unset;
    min-width: 100%;
  }
}

.top-bar {
  display: flex;
  flex-direction: row;
  gap: 0.8em;
  padding: 0.4em 1em;
  margin-top: 1em;
  max-width: 512px;
  width: 100%;

  @media (max-width: 512px) {
    margin-left: 0.5em;
    margin-right: 0.5em;
  }
}

.logout-link {
  color: red;

  &:hover {
    text-decoration: underline;
    cursor: pointer;
  }
}
</style>

<style>
:root {
  font-size: 1.1em;
  --tblr-font-sans-serif: 'Inter var', 'Segoe UI', sans-serif;
}

#app {
  padding-bottom: 2em;
}
</style>
