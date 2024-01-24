<template>
  <div style="display: flex; flex-direction: column; gap: 1em;">
    <div class="card group-card">
      <h1 style="align-self: center;">Protokollführung</h1>
      <RouterLink to="/protocol/create" class="btn btn-indigo">
        <div style="padding-top: 1em; padding-bottom: 1em;">
          <h2>Als Pilot anmelden</h2>
          <div>Damit beginnst du ein neues Protokoll.</div>
        </div>
      </RouterLink>
      <div style="display: flex; gap: 0.5em; margin-top: 0.8em; flex-wrap: wrap;">
        <RouterLink to="/protocol/list" class="btn btn-info" style="flex-grow: 1;">
          Protokolle einsehen
        </RouterLink>
        <RouterLink to="/protocol/manage" class="btn btn-secondary disabled" style="flex-grow: 1;">
          Protokolle verwalten
        </RouterLink>
      </div>
    </div>
    <div class="card group-card">
      <h1 style="align-self: center;">Administration</h1>
      <RouterLink to="/admin/manage-users" class="btn btn-outline-pink"  style="margin-top: 0.5em;">
        <div style="padding-top: 0.4em; padding-bottom: 0.4em;">
          <h3>Nutzerverwaltung</h3>
          <div>Nutzerprofile erstellen und anpassen.</div>
        </div>
      </RouterLink>
      <RouterLink to="/admin/manage-users" class="btn btn-outline-purple" style="margin-top: 1em;">
        <div style="padding-top: 0.4em; padding-bottom: 0.4em;">
          <h3>Modellverwaltung</h3>
          <div>Flugmodelle erstellen und verwalten.</div>
        </div>
      </RouterLink>
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
</style>
