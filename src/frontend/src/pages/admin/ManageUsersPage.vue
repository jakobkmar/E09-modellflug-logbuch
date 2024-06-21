<script setup lang="ts">
import { backendRequest } from '@/networking'
import { AccountResponse } from 'modellflug-logbuch-common-data'
import { computed, ref } from 'vue'
import { IconEdit, IconPlus, IconTrash, IconSearch } from '@tabler/icons-vue'

const users = ref<AccountResponse[]>([])
const usernameSearch = ref('')

const filteredUsers = computed(() => {
  if (!usernameSearch.value) {
    return users.value
  }
  return users.value.filter((user) => {
    return usernameSearch.value.toLowerCase().split(' ').every((searchPart) => {
      return user.username.toLowerCase().includes(searchPart) ||
        user.firstName.toLowerCase().includes(searchPart) ||
        user.lastName?.toLowerCase().includes(searchPart)
    })
  })
})

async function loadUsers() {
  const response = await backendRequest('/api/v1/account/all', {
    method: 'GET',
  })
  if (!response.ok) {
    console.error('Failed to load users:', response)
    alert('Failed to load users')
    return
  }

  users.value = (await response.json()) as AccountResponse[]
}

loadUsers()
</script>

<template>
  <div>
    <h2 >Nutzerverwaltung</h2>

    <div style="display: flex; flex-direction: row; gap: 1em; margin-bottom: 1.5em; flex-wrap: wrap;">
      <RouterLink to="/admin/create-user"
                  type="button" class="btn btn-indigo"
                  style="display: inline-flex; gap: 0.5em; flex-grow: 1;">
        <IconPlus size="1.5em"/>
        Neuer Nutzer
      </RouterLink>
      <div class="input-icon" style="flex-grow: 1;">
        <span class="input-icon-addon" style="padding: 0.4em;">
          <IconSearch/>
        </span>
        <input v-model="usernameSearch" type="text" class="form-control" placeholder="Name suchen..." />
      </div>
    </div>

    <h3>Nutzerliste</h3>
    <div v-if="users.length == 0" class="text-center">
      <div class="spinner-grow text-indigo" role="status"></div>
    </div>
    <div style="display: flex; flex-direction: column; gap: 0.7em;">
      <div v-for="user in filteredUsers" :key="user.userId" class="card">
        <div class="datagrid user-card-padding" style="--tblr-datagrid-item-width: 9em; --tblr-datagrid-padding: 0.5em;">
          <div class="datagrid-item">
            <div class="datagrid-title">Nutzername</div>
            <div class="datagrid-content strong">{{ user.username }}</div>
          </div>
          <div class="datagrid-item">
            <div class="datagrid-title">ID</div>
            <div class="datagrid-content">
              <div>
                <span class="user-id-display">
                  {{ user.userId }}
                </span>
              </div>
            </div>
          </div>
          <div class="datagrid-item">
            <div class="datagrid-title">Accounttyp</div>
            <div class="datagrid-content">
              <span v-if="user.isAdminUnsafe" class="badge badge-outline text-red">Admin</span>
              <span v-else-if="user.canSeeAllLogsUnsafe" class="badge badge-outline text-blue">Protokolleur</span>
              <span v-else class="badge badge-outline text-green">Regulär</span>
            </div>
          </div>
          <div class="datagrid-item">
            <div class="datagrid-title">Vereinsnummer</div>
            <div class="datagrid-content" v-if="user.registrationNumber != null">{{ user.registrationNumber }}</div>
            <div class="datagrid-content" v-else style="font-style: italic;">unbekannt</div>
          </div>
          <div class="datagrid-item">
            <div class="datagrid-title">Vorname</div>
            <div class="datagrid-content">{{ user.firstName }}</div>
          </div>
          <div class="datagrid-item">
            <div class="datagrid-title">Nachname</div>
            <div class="datagrid-content" v-if="user.lastName != null">{{ user.lastName }}</div>
            <div class="datagrid-content" v-else style="font-style: italic;">unbekannt</div>
          </div>
        </div>
        <div class="card-footer user-card-footer">
          <RouterLink :to="`/admin/edit-user/${user.userId}`">
            <div style="display: flex; flex-direction: row; gap: 0.3em; align-items: center;">
              <IconEdit size="1.5em" />
              <span style="align-content: center;">Bearbeiten</span>
            </div>
          </RouterLink>
          <RouterLink :to="`/admin/delete-user/${user.userId}`" style="color: red;">
            <div style="display: flex; flex-direction: row; gap: 0.3em; align-items: center; font-size: 0.8em;">
              <IconTrash size="1.5em" />
              <span style="align-content: center;">Löschen</span>
            </div>
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.user-card-padding {
  padding: 1em 1em 0.8em;
}

.user-id-display {
  font-family: monospace;
  color: #05557c;
  background-color: #e4eef3;
  line-height: 1;
  padding-left: 0.3em;
  padding-right: 0.3em;
  border-radius: 5px;
}

.user-card-footer {
  padding: 0.5em 1em;
  display: flex;
  flex-direction: row;
  gap: 1em;
  align-items: center;
  justify-content: space-between;
}
</style>
