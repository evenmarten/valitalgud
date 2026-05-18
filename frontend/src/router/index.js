import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import UnauthorizedView from '@/views/UnauthorizedView.vue'

const protectedRoutes = [
  '/events',
  '/my-events',
  '/calendar',
  '/profile',
  '/my-organized-events',
]

const routes = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
  },
  {
    path: '/unauthorized',
    name: 'unauthorized',
    component: UnauthorizedView,
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/unauthorized',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const isProtected = protectedRoutes.includes(to.path) || to.path.startsWith('/events/')
  const userId = localStorage.getItem('userId')

  if (isProtected && !userId) {
    return '/unauthorized'
  }
})

export default router
