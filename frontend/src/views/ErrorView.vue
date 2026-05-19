<template>
  <div>
    <AppNavbar />

    <div class="container py-5 text-center">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <h1 class="display-1 fw-bold text-muted mb-2">404</h1>
          <h2 class="mb-3">Lehte ei leitud</h2>
          <p class="text-muted mb-4">Vabandame, otsitud lehte ei eksisteeri.</p>
          <button class="btn btn-primary" @click="goToHome">Home</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AppNavbar from '@/navigation/AppNavbar.vue'
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'ErrorView',
  components: { AppNavbar },
  methods: {
    goToHome() {
      NavigationService.navigateToHome()
    },
  },
}
</script>
