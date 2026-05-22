import { createRouter, createWebHistory } from 'vue-router'
import LandingPage from '@/views/LandingPage.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import RegisterSuccessView from '@/views/RegisterSuccessView.vue'
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
import ProfileView from '@/views/ProfileView.vue'
import ContactView from '@/views/ContactView.vue'
import ErrorView from '@/views/ErrorView.vue'
import AuthHelper from '@/auth/auth.js'

// Kaitstud route'id route'i NIME järgi. Sündmuste loend ('events') ja detailvaade
// ('event-details') on AVALIKUD — sisselogimata kasutaja näeb neid. Loomine/muutmine jääb kaitstuks.
const protectedRouteNames = [
  'my-events',
  'calendar',
  'profile',
  'event-create',
  'event-edit',
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
    beforeEnter: () => (AuthHelper.isLoggedIn() ? '/' : true),
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    beforeEnter: () => (AuthHelper.isLoggedIn() ? '/' : true),
  },
  {
    path: '/register-success',
    name: 'register-success',
    component: RegisterSuccessView,
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
    path: '/calendar',
    name: 'calendar',
    component: CalendarView,
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView,
  },
  {
    path: '/contact',
    name: 'contact',
    component: ContactView,
  },
  {
    path: '/unauthorized',
    name: 'unauthorized',
    component: UnauthorizedView,
  },
  {
    path: '/404',
    name: 'error',
    component: ErrorView,
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  if (protectedRouteNames.includes(to.name) && !AuthHelper.isLoggedIn()) {
    return '/unauthorized'
  }
})

export default router
