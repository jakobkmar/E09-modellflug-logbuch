<template>

<div style="display: flex; flex-direction: column; gap: 2em">
    <div class="card group-card">
        <h2 style="padding-top: 1.5em; padding-left: 1.4em; padding-bottom: 1.5em;"> Melde dich mit deinem Pilot Account an </h2>
    </div>
    <div class="card group-card">
        <div style="align-self: center;">Bitte gib hier deine Email ein.</div>
        <input style="padding-top: 1em; padding-bottom: 1em;" class="btn btn-white" type="text" v-model="email" placeholder="Enter your Email" />
    </div>
    <div class="card group-card">
        <div style="align-self: center;">Bitte gib hier dein Passwort ein.</div>
        <input style="padding-top: 1em; padding-bottom: 1em;" class="btn btn-white" type="text" v-model="password" placeholder="Enter Password" />
    </div>
    <div class="card group-card">
        <button  class="btn btn." style="flex-grow: 1;">
            Login
        </button>
    </div>
</div>

</template>

<script>
export default{
    name:'Login'
}
</script>