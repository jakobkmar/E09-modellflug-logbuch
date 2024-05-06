import HomePage from './pages/HomePage.vue'
import CreateProtocolPage from './pages/CreateProtocolPage.vue'
import ListProtocolsPage from './pages/ListProtocolsPage.vue'
import CompleteProtocolPage from './pages/CompleteProtocolPage.vue'
import Login from './pages/Login.vue'

export const routes = [
  { path: '/', component: HomePage },
  { path: '/Login', component: Login},
  {
    path: '/protocol',
    children: [
      { path: 'create', component: CreateProtocolPage },
      { path: 'complete', component: CompleteProtocolPage },
      { path: 'list', component: ListProtocolsPage }
    ]
  }
]
