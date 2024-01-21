import HomePage from './pages/HomePage.vue'
import CreateProtocolPage from './pages/CreateProtocolPage.vue'
const AboutPage = { template: '<div>About</div>' }

export const routes = [
  { path: '/', component: HomePage },
  { path: '/about', component: AboutPage },
  {
    path: '/protocol',
    children: [
      { path: 'create', component: CreateProtocolPage }
    ]
  }
]
