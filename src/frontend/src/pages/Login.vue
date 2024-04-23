<template>
    <h2>Login</h2>
    <form class="column" style="--tblr-body-bg: #F7F8FA">
        <fieldset class="form-fieldset">
            <label class="form-label">Melde dich mit deinem Account an</label>
            <div class="column" style="gap: 0.5em; margin-bottom: 1em;">
                <div style="display: flex; gap: 0.4em; justify-content: center;">
                        <input style="scoped" class="form-control" text-align="center" type="text" v-model="email" placeholder="Gib deine Email an">
                </div>
            </div>
            <div class="column" style="gap 0.5em; margin-bottom: 1em;">
                <div style="display: flex; gap: 0.4em; justify-content: center;">
                        <input
                        class="form-control"
                        :type="showPassword ? 'text' : 'password'"
                        id="password"
                        name="password"
                        v-model="password"
                        required
                        placeholder="Gib dein Password an"
                        />
                        <button type="button" class="btn btn-secondary" @click="togglePassword">
                            {{ showPassword ? 'Zeigen' : 'Verstecken' }}
                        </button>
                </div>
            </div>
            <div style="display: flex; gap: 0.5em;">
                <button class="btn btn-primary"> Einloggen </button>
                <a href="#" class="btn btn-outline-danger">Abbrechen</a>
            </div>
        </fieldset>
    </form>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';

export default defineComponent({
  setup() {
    const email = ref('');
    const password = ref('');
    const showPassword = ref(false);

    const togglePassword = () => {
      showPassword.value = !showPassword.value;
    };

    const login = () => {
      // Handle login logic here
    };

    return {
      email,
      password,
      showPassword,
      togglePassword,
      login,
    };
  },
});
</script>

<style scoped>
.password-input {
  display: flex;
  align-items: center;
}
</style>
