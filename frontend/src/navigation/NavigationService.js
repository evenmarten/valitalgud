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

  navigateToEvents() {
    router.push('/events')
  },

  navigateToUnauthorized() {
    router.push('/unauthorized')
  },

  navigateToErrorView() {
    router.push('/404')
  },
}
