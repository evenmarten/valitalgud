<template>
  <div class="container py-5 text-center">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <h2 class="mb-4">Event list can't be viewed because not being logged in</h2>
        <div class="d-flex gap-2 justify-content-center">
          <button class="btn btn-primary" @click="goToLogin">Login</button>
          <button class="btn btn-secondary" @click="goToRegister">Create User</button>
          <button class="btn btn-outline-secondary" @click="goToHome">Home</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'UnauthorizedView',
  methods: {
    goToLogin() {
      NavigationService.navigateToLogin()
    },
    goToRegister() {
      NavigationService.navigateToRegister()
    },
    goToHome() {
      NavigationService.navigateToHome()
    },
  },
}
</script>