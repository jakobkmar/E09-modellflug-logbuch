<script setup lang="ts">
import { ref } from 'vue'
import { useLoginSessionStore } from '@/session'
import { useRouter } from 'vue-router'
import AlertCard from '@/components/AlertCard.vue'
import { IconExclamationCircle } from '@tabler/icons-vue'

const username = ref('')
const password = ref('')
const showPassword = ref(false)

const loading = ref(false)
const errorAlert = ref<InstanceType<typeof AlertCard>>()

const router = useRouter()
const sessionStore = useLoginSessionStore()

function togglePassword() {
  showPassword.value = !showPassword.value
}

function login() {
  loading.value = true
  sessionStore.login(
    username.value,
    password.value,
    () => router.push('/'),
    (response) => {
      loading.value = false
      if (response.status == 401) {
        errorAlert.value?.show('Nutzername oder Passwort falsch')
      } else if (response.status == 400) {
        response.text().then((text) => errorAlert.value?.show(text))
      } else {
        errorAlert.value?.show(`${response.status} - ${response.statusText}`)
      }
    },
  )
}
</script>

<template>
  <h2>Login</h2>
  <form class="column" style="--tblr-body-bg: #f7f8fa">
    <AlertCard title="Ein Problem ist aufgetreten" type="danger" ref="errorAlert">
      <IconExclamationCircle/>
    </AlertCard>
    <fieldset class="form-fieldset">
      <label class="form-label">Melde dich mit deinem Account an</label>
      <div class="column" style="gap: 0.5em; margin-bottom: 1em">
        <div style="display: flex; gap: 0.4em; justify-content: center">
          <input
            name="username"
            v-model="username"
            class="form-control"
            type="text"
            placeholder="Nutzername"
            required
          />
        </div>
      </div>
      <div class="column" style="gap: 0.5em; margin-bottom: 1em">
        <div style="display: flex; gap: 0.4em; justify-content: center">
          <input
            name="password"
            v-model="password"
            class="form-control"
            :type="showPassword ? 'text' : 'password'"
            required
            placeholder="Passwort"
          />
          <button type="button" class="btn btn-secondary" @click="togglePassword">
            {{ showPassword ? 'Verstecken' : 'Zeigen' }}
          </button>
        </div>
      </div>
      <div style="display: flex; gap: 0.5em">
        <button type="button" class="btn btn-primary" @click="login" :disabled="loading">
          <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
          Einloggen
        </button>
        <a href="#" class="btn btn-outline-danger">Abbrechen</a>
      </div>
    </fieldset>
  </form>
</template>
