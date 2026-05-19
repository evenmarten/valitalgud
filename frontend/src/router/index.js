import { createRouter, createWebHistory } from 'vue-router'
import LandingPage from '@/views/LandingPage.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import UnauthorizedView from '@/views/UnauthorizedView.vue'
import ShopView from '@/views/ShopView.vue'
import CartView from '@/views/CartView.vue'
import CheckoutView from '@/views/CheckoutView.vue'
import OrderSuccessView from '@/views/OrderSuccessView.vue'
import EventsView from '@/views/EventsView.vue'
import EventDetailsView from '@/views/EventDetailsView.vue'
import CreateEventView from '@/views/CreateEventView.vue'
import EditEventView from '@/views/EditEventView.vue'
import MyEventsView from '@/views/MyEventsView.vue'
import CalendarView from '@/views/CalendarView.vue'
import MyOrganizedEventsView from '@/views/MyOrganizedEventsView.vue'
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
    name: 'landing',
    component: LandingPage,
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
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
    path: '/events',
    name: 'events',
    component: EventsView,
  },
  {
    path: '/events/create',
    name: 'event-create',
    component: CreateEventView,
  },
  {
    path: '/events/:id/edit',
    name: 'event-edit',
    component: EditEventView,
  },
  {
    path: '/events/:id',
    name: 'event-details',
    component: EventDetailsView,
  },
  {
    path: '/my-events',
    name: 'my-events',
    component: MyEventsView,
  },
  {
    path: '/my-organized-events',
    name: 'my-organized-events',
    component: MyOrganizedEventsView,
  },
  {
    path: '/calendar',
    name: 'calendar',
    component: CalendarView,
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
