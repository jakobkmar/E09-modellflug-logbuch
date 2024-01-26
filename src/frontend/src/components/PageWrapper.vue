<template>
  <div class="flex-center">
    <div
      style="display: flex; flex-direction: row; gap: 0.8em;
      padding-left: 1em; padding-right: 1em; padding-top: 0.4em; padding-bottom: 0.4em;
      margin-top: 1em; max-width: 512px; width: 100%;"
      class="card mb-2">
      <RouterLink to="/">Startseite</RouterLink>
      <RouterLink to="/protocol/list">Protokolle</RouterLink>
    </div>
  </div>
  <div class="flex-center">
    <div class="content-wrapper">
      <slot/>
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
</style>

<style>
:root {
  font-size: 1.1em;
  --tblr-font-sans-serif: 'Inter var', 'Segoe UI', sans-serif;
}
</style>
