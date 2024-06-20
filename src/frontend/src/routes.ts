import HomePage from './pages/HomePage.vue'
import CreateProtocolPage from './pages/CreateProtocolPage.vue'
import ListProtocolsPage from './pages/ListProtocolsPage.vue'
import CompleteProtocolPage from './pages/CompleteProtocolPage.vue'
import LoginPage from './pages/LoginPage.vue'
import RegistrationPage from './pages/RegistrationPage.vue'
import ManageUsersPage from './pages/admin/ManageUsersPage.vue'
import PageNotFound from '@/pages/PageNotFound.vue'

export const routes = [
  { path: '/', component: HomePage },
  { path: '/login', component: LoginPage },
  { path: '/register', component: RegistrationPage },
  {
    path: '/protocol',
    children: [
      { path: 'create', component: CreateProtocolPage },
      { path: 'complete', component: CompleteProtocolPage },
      { path: 'list', component: ListProtocolsPage },
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
