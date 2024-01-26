import './assets/main.css'

import { createApp } from 'vue'
import { createRouter, createWebHashHistory } from 'vue-router'

import { routes } from './routes'

const router = createRouter({
  history: createWebHashHistory(),
  routes: routes
})

import App from './App.vue'

const app = createApp(App)
app.use(router)
app.mount('#app')
