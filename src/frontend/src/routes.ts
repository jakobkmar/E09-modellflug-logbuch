import HomePage from './pages/HomePage.vue'
import CreateProtocolPage from './pages/protocol/CreateProtocolPage.vue'
import ListProtocolsPage from './pages/protocol/ListProtocolsPage.vue'
import CompleteProtocolPage from './pages/protocol/CompleteProtocolPage.vue'
import LoginPage from './pages/LoginPage.vue'
import ManageUsersPage from './pages/admin/ManageUsersPage.vue'
import PageNotFound from '@/pages/PageNotFound.vue'
import CreateUserPage from './pages/admin/CreateUserPage.vue'
import ListOwnFlightsPage from '@/pages/protocol/ListOwnFlightsPage.vue'
import FlightDirectorHistoryPage from '@/pages/director/FlightDirectorHistoryPage.vue'
import ShowFlightDirectorPage from './pages/director/ShowFlightDirectorPage.vue'
import LoginFlightDirectorPage from '@/pages/director/LoginFlightDirectorPage.vue'
import LogoutFlightDirectorPage from './pages/director/LogoutFlightDirectorPage.vue'
import ConfigPage from '@/pages/ConfigPage.vue'

export const routes = [
  { path: '/', component: HomePage },
  { path: '/login', component: LoginPage },
  {
    path: '/flight',
    children: [
      { path: 'create', component: CreateProtocolPage },
      { path: 'complete', component: CompleteProtocolPage },
      { path: 'list-active', component: ListProtocolsPage },
      { path: 'list-mine', component: ListOwnFlightsPage },
    ],
  },
  {
    path: '/flightdirector',
    children: [
      { path: 'login', component: LoginFlightDirectorPage },
      { path: 'logout', component: LogoutFlightDirectorPage },
      { path: 'current', component: ShowFlightDirectorPage },
      { path: 'history', component: FlightDirectorHistoryPage },
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
  { path: '/config', component: ConfigPage },
  { path: '/:pathMatch(.*)*', component: PageNotFound },
]
