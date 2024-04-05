<template>
<div class="card group-card">
    <div style="display: flex; flex-direction: column; gap: 2em">
        <div class="card group-card">
            <h2 style="padding-top: 1.5em; padding-left: 1.4em; padding-bottom: 1.5em;"> Melde dich mit deinem Pilot Account an </h2>
        </div>
        <div class="card group-card" style="align-self: center;">
            <div class="1h-1" style="align-self: center;">
                <div style="padding-left: 1.5em; padding-right: 1.5em; padding-top: 0.5em; padding-bottom: 0.5em;">Bitte gib hier deine Email ein.</div>
                <div style="padding-left: 1.5em; padding-bottom: 1.5em;">
                    <input style="padding-top: 1em; padding-bottom: 1em;" class="btn btn-white" type="text" v-model="email" placeholder="Enter your Email" />
                </div>
            </div>
        </div>
        <div class="card group-card" style="align-self: center;">
            <div class="1h-1" style="align-self: center;">
                <div style="padding-left: 1em; padding-right: 1em; padding-top: 0.5em; padding-bottom: 0.5em;">Bitte gib hier dein Passwort ein.</div>
                <div style="padding-left: 1.5em; padding-bottom: 1em;">
                    <input style="padding-top: 1em; padding-bottom: 1em;" class="btn btn-white" type="text" v-model="password" placeholder="Enter Password" />
                </div>
            </div>
        </div>
        <div style="padding-bottom: 1em; padding-left: 7em;">
            <button  class="1h-1" style="padding-left: 6.9em; padding-right: 5em;padding-bottom: 3.5; padding-top: 0em;">
                    <div style="padding-top: 1.5em; padding-right: 1.8em; padding-bottom: 1.5em;">
                        <h3>Login</h3>
                    </div>
            </button>
        </div>
    </div>
</div>


</template>

<script>
export default{
    name:'Login'
}
</script>