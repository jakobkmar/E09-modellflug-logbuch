<template>
    <h2>Nutzer anlegen</h2>
    <form class="column" style="--tblr-body-bg: #F7F8FA">
        <fieldset class="form-fieldset">
            <label class="form-label">Gib den Namen des neuen Nutzers ein</label>
            <div class="column" style="gap: 0.5em; margin-bottom: 1em;">
                <div style="display: flex; gap: 0.4em; justify-content: center;">
                        <input
                        class="form-control"
                        required
                        placeholder="Gib deinen Namen ein"
                        />
                </div>
            </div>
<!--            <label class="form-label">Gib die Adresse des neuen Nutzers ein</label>-->
<!--            <div class="column" style="gap: 0.5em; margin-bottom: 1em;">-->
<!--                <div style="display: flex; gap: 0.4em; justify-content: center;">-->
<!--                        <input-->
<!--                        class="form-control"-->
<!--                        required-->
<!--                        placeholder="Gib deine Adresse ein"-->
<!--                        />-->
<!--                </div>-->
<!--            </div>-->
<!--            <label class="form-label">Gib die Postleitzahl des neuen Nutzers ein</label>-->
<!--            <div class="column" style="gap: 0.5em; margin-bottom: 1em;">-->
<!--                <div style="display: flex; gap: 0.4em; justify-content: center;">-->
<!--                        <input-->
<!--                        class="form-control"-->
<!--                        required-->
<!--                        placeholder="Gib deine Postleitzahl ein"-->
<!--                        />-->
<!--                </div>-->
<!--            </div>-->
            <label class="form-label">Gib die Email-Adresse des neuen Nutzers ein</label>
            <div class="column" style="gap: 0.5em; margin-bottom: 1em;">
                <div style="display: flex; gap: 0.4em; justify-content: center;">
                        <input
                        class="form-control"
                        required
                        placeholder="Gib deine Emailadresse ein"
                        />
                </div>
            </div>
            <label class="form-label">Gib das Passwort des neuen Nutzers ein</label>
            <div class="column" style="gap: 0.5em; margin-bottom: 1em;">
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
                            {{ showPassword ? 'Verstecken' : 'Zeigen' }}
                        </button>
                </div>
            </div>
            <label class="form-label">Wiederhole das Passwort</label>
            <div class="column" style="gap: 0.5em; margin-bottom: 1em;">
                <div style="display: flex; gap: 0.4em; justify-content: center;">
                        <input
                        class="form-control"
                        :type="showPassword2 ? 'text' : 'password'"
                        id="password"
                        name="password"
                        v-model="password2"
                        required
                        placeholder="Wiederhole dein Password"
                        />
                        <button type="button" class="btn btn-secondary" @click="toggle_Password">
                            {{ showPassword2 ? 'Verstecken' : 'Zeigen' }}
                        </button>
                </div>
            </div>
            <label class="form-label">Soll der neue Nutzer Admin-Rechte erhalten?</label>
            <div class="column" style="gap: 0.5em; margin-bottom: 1em;">
                <div style="display: flex; gap: 0.4em; align-items: center;">
                    <input type="radio" id="adminYes" name="admin" value="true" v-model="isAdmin" />
                    <label for="adminYes">Ja</label>
                    <input type="radio" id="adminNo" name="admin" value="false" v-model="isAdmin" />
                    <label for="adminNo">Nein</label>
                </div>
            </div>
            <div style="display: flex; gap: 0.5em;">
                <button class="btn btn-primary" @click="submitRegistration"> Registrieren </button>
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
    const password2 = ref('')
    const showPassword = ref(false);
    const showPassword2 = ref(false);
    const isAdmin = ref(false);

    const togglePassword = () => {
      showPassword.value = !showPassword.value;
    };

    const toggle_Password = () => {
        showPassword2.value = !showPassword2.value;
    };

    function submitRegistration () {
        if (password.value !== password2.value) {
            console.warn("Die Passwörter stimmen nicht überein. Registrierung kann nicht abgeschlossen werden")
            return
        }
        if (samePassword()) {
            // Handle login logic here
        }
    };


    const samePassword = () => {
        if (password.value === password2.value) {
            return true;
        } else {
            return false;
        }
    };

    const login = () => {
      // Handle login logic here
    };

    return {
      email,
      password,
      showPassword,
      password2,
      showPassword2,
      togglePassword,
      toggle_Password,
      login,
      submitRegistration,
      isAdmin,
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