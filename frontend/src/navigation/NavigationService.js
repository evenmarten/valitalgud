import router from '@/router'

export default {
  navigateToHome() {
    router.push('/')
  },

  navigateToLogin() {
    router.push('/login')
  },

  navigateToRegister() {
    router.push('/register')
  },

  navigateToShop() {
    router.push('/shop')
  },

  navigateToCart() {
    router.push('/cart')
  },

  navigateToCheckout() {
    router.push('/checkout')
  },

  navigateToEvents() {
    router.push('/events')
  },

  navigateToEventDetails(eventId) {
    router.push(`/events/${eventId}`)
  },

  navigateToCreateEvent() {
    router.push('/events/create')
  },

  navigateToMyEvents() {
    router.push('/my-events')
  },

  navigateToCalendar() {
    router.push('/calendar')
  },

  navigateToProfile() {
    router.push('/profile')
  },

  navigateToUnauthorized() {
    router.push('/unauthorized')
  },

  navigateToOrderSuccess(orderId) {
    router.push({ name: 'order-success', state: { orderId } })
  },

  navigateToErrorView() {
    router.push('/404')
  },
}
