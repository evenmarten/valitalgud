<template>
  <div>
    <AppNavbar />
    <div class="container py-5 text-center">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <h2 class="mb-4">Palju õnne eduka ostu puhul!</h2>
          <img src="/images/doggy gif.webp" alt="" class="mb-4" style="width: 350px;" />
          <div class="d-flex gap-2 justify-content-center mt-4">
            <button class="btn btn-primary" @click="goToShop">Jätka ostlemist</button>
            <button class="btn btn-outline-secondary" @click="goToHome">Avalehele</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AppNavbar from '@/navigation/AppNavbar.vue'
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'OrderSuccessView',
  components: { AppNavbar },
  methods: {
    goToShop() {
      NavigationService.navigateToShop()
    },
    goToHome() {
      NavigationService.navigateToHome()
    },
  },
  beforeMount() {
    if (!window.history.state.orderId) {
      NavigationService.navigateToShop()
    }
  },
}
</script>
