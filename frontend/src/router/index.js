import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import UnauthorizedView from '@/views/UnauthorizedView.vue'
import ShopView from '@/views/ShopView.vue'
import CartView from '@/views/CartView.vue'
import CheckoutView from '@/views/CheckoutView.vue'
import OrderSuccessView from '@/views/OrderSuccessView.vue'
import AuthHelper from '@/auth/auth.js'

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
    path: '/shop',
    name: 'shop',
    component: ShopView,
  },
  {
    path: '/cart',
    name: 'cart',
    component: CartView,
  },
  {
    path: '/checkout',
    name: 'checkout',
    component: CheckoutView,
  },
  {
    path: '/order-success',
    name: 'order-success',
    component: OrderSuccessView,
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

  if (isProtected && !AuthHelper.isLoggedIn()) {
    return '/unauthorized'
  }
})

export default router
