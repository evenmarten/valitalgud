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

  navigateToRegisterSuccess(fullName) {
    router.push({ name: 'register-success', state: { registered: true, fullName } })
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

  navigateToEditEvent(eventId) {
    router.push(`/events/${eventId}/edit`)
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

  navigateToContact() {
    router.push('/contact')
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
