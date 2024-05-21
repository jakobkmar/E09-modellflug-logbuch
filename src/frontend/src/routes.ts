import HomePage from './pages/HomePage.vue'
import CreateProtocolPage from './pages/CreateProtocolPage.vue'
import ListProtocolsPage from './pages/ListProtocolsPage.vue'
import CompleteProtocolPage from './pages/CompleteProtocolPage.vue'
import LoginPage from './pages/Login.vue'
import RegistrationPage from './pages/RegistrationPage.vue'

export const routes = [
  { path: '/', component: HomePage },
  { path: '/login', component: LoginPage},
  { path: '/register', component: RegistrationPage},
  {
    path: '/protocol',
    children: [
      { path: 'create', component: CreateProtocolPage },
      { path: 'complete', component: CompleteProtocolPage },
      { path: 'list', component: ListProtocolsPage }
    ]
  }
]
