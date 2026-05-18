const USER_KEY = 'user'

export default {
  saveUser(user) {
    localStorage.setItem(USER_KEY, JSON.stringify(user))
  },

  getUser() {
    const stored = localStorage.getItem(USER_KEY)
    return stored ? JSON.parse(stored) : null
  },

  isLoggedIn() {
    return this.getUser() !== null
  },

  clearUser() {
    localStorage.removeItem(USER_KEY)
  },
}
