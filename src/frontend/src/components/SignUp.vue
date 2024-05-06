<template>
<h1>Sign Up</h1>
<div class="register">
    <input type="text" v-model="name" placeholder="Enter Name" />
    <input type="text" v-model="email" placeholder="Enter Email" />
    <input type="password" v-model="password" placeholder="Enter Password" />
    <button v-on:click="signUp" >SignUp</button>
</div>
</template>

<script>
export default{
   name:'SignUp'
}
</script>