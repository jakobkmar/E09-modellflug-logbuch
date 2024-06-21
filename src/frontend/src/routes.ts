import HomePage from './pages/HomePage.vue'
import CreateProtocolPage from './pages/CreateProtocolPage.vue'
import ListProtocolsPage from './pages/ListProtocolsPage.vue'
import CompleteProtocolPage from './pages/CompleteProtocolPage.vue'
import RequestProtocol from './pages/RequestProtocol.vue'
import LoginPage from './pages/LoginPage.vue'
import RegistrationPage from './pages/RegistrationPage.vue'
import ManageUsersPage from './pages/admin/ManageUsersPage.vue'
import PageNotFound from '@/pages/PageNotFound.vue'
import CreateUserPage from './pages/admin/CreateUserPage.vue'

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
      { path: 'list-mine', component: PageNotFound },
    ],
  },
  {
    path: '/flightdirector',
    children: [
      { path: 'login', component: PageNotFound },
      { path: 'logout', component: PageNotFound },
      { path: 'current', component: PageNotFound },
      { path: 'history', component: PageNotFound },
    ],
  },
  {
    path: '/admin',
    children: [
      { path: 'manage-users', component: ManageUsersPage },
      { path: 'create-user', component: CreateUserPage },
      { path: 'manage-models', component: PageNotFound },
    ],
  },
  { path: '/:pathMatch(.*)*', component: PageNotFound },
]
