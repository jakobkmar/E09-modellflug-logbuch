import './assets/main.css'

import { createApp } from 'vue'
import { createRouter, createWebHashHistory } from 'vue-router'
import { createPinia } from "pinia";

import { routes } from './routes'

const router = createRouter({
  history: createWebHashHistory(),
  routes: routes
})
const pinia = createPinia()

import App from './App.vue'

const app = createApp(App)
app.use(router)
app.use(pinia)
app.mount('#app')
