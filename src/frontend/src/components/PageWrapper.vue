<script setup>
import { useLoginSessionStore } from '@/session'

const sessionStore = useLoginSessionStore()
</script>

<template>
  <div class="flex-center">
    <div class="top-bar card mb-2">
      <RouterLink to="/">Startseite</RouterLink>
      <RouterLink to="/protocol/list">Protokolle</RouterLink>
      <div style="width: 100%"></div>
      <div
        v-if="sessionStore.loginSession != null"
        class="logout-link"
        @click="sessionStore.logout()"
      >
        Logout
      </div>
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
  padding-top: 1em;

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
</style>
