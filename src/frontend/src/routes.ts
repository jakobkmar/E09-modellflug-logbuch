import HomePage from './pages/HomePage.vue'
import CreateProtocolPage from './pages/CreateProtocolPage.vue'
import ListProtocolsPage from './pages/ListProtocolsPage.vue'
import CompleteProtocolPage from './pages/CompleteProtocolPage.vue'
import RequestProtocol from './pages/RequestProtocol.vue'
import LoginPage from './pages/LoginPage.vue'
import RegistrationPage from './pages/RegistrationPage.vue'
import ManageUsersPage from './pages/admin/ManageUsersPage.vue'
import PageNotFound from '@/pages/PageNotFound.vue'

export const routes = [
  { path: '/', component: HomePage },
  { path: '/login', component: LoginPage },
  { path: '/register', component: RegistrationPage },
  {
    path: '/flight',
    children: [
      { path: 'create', component: CreateProtocolPage },
      { path: 'complete', component: CompleteProtocolPage },
      { path: 'list-active', component: ListProtocolsPage },
      { path: 'list-mine' },
    ],
  },
  {
    path: '/flightdirector',
    children: [
      { path: 'login' },
      { path: 'logout' },
      { path: 'current' },
      { path: 'history' },
    ],
  },
  {
    path: '/admin',
    children: [
      { path: 'manage-users', component: ManageUsersPage },
      { path: 'manage-models' },
    ],
  },
  { path: '/:pathMatch(.*)*', component: PageNotFound },
]
