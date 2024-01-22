<template>
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

  min-width: 512px;

  @media (max-width: 512px) {
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
