<script setup lang="ts">
import { ref } from 'vue'
import { loginSession, UserSession } from '@/store'
import { useRouter } from 'vue-router'

const router = useRouter()

const username = ref('')
const password = ref('')
const showPassword = ref(false)

function togglePassword() {
  showPassword.value = !showPassword.value
}

function login() {
  loginSession.value = new UserSession(username.value)
  router.push('/')
}
</script>

<template>
  <h2>Login</h2>
  <form class="column" style="--tblr-body-bg: #f7f8fa">
    <fieldset class="form-fieldset">
      <label class="form-label">Melde dich mit deinem Account an</label>
      <div class="column" style="gap: 0.5em; margin-bottom: 1em">
        <div style="display: flex; gap: 0.4em; justify-content: center">
          <input
            class="form-control"
            text-align="center"
            type="text"
            v-model="username"
            placeholder="Dein Nutzername"
          />
        </div>
      </div>
      <div class="column" style="gap: 0.5em; margin-bottom: 1em">
        <div style="display: flex; gap: 0.4em; justify-content: center">
          <input
            class="form-control"
            :type="showPassword ? 'text' : 'password'"
            id="password"
            name="password"
            v-model="password"
            required
            placeholder="Dein Passwort"
          />
          <button type="button" class="btn btn-secondary" @click="togglePassword">
            {{ showPassword ? 'Verstecken' : 'Zeigen' }}
          </button>
        </div>
      </div>
      <div style="display: flex; gap: 0.5em">
        <button class="btn btn-primary" @click="login">Einloggen</button>
        <a href="#" class="btn btn-outline-danger">Abbrechen</a>
      </div>
    </fieldset>
  </form>
</template>
