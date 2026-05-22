<template>
  <div>
    <AppNavbar />

    <div class="hero-banner">
      <div class="container text-center">
        <p class="hero-label">Makse õnnestus</p>
        <h1 class="hero-title">Tellimus edastatud!</h1>
      </div>
    </div>

    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-6">

          <div class="text-center mb-4">
            <img src="/images/doggy gif.webp" alt="" class="doggy" />
          </div>

          <p class="text-center fw-bold mb-4" style="font-size: 1.05rem;">
            Saatsime kinnituse e-postile. Pakk on teel!
          </p>

          <div class="d-flex gap-3 justify-content-center">
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
  data() {
    return {
      orderId: 0,
    }
  },
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
      return
    }
    this.orderId = window.history.state.orderId
  },
}
</script>

<style scoped>
.hero-banner {
  background-color: var(--corp-blue-soft);
  border-bottom: 1px solid var(--corp-line);
  padding: 2.5rem 0 2rem;
}

.hero-label {
  font-size: 0.8rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--corp-blue);
  opacity: 0.6;
  margin: 0 0 0.2rem;
}

.hero-title {
  font-size: 3rem;
  color: var(--corp-ink);
  margin: 0;
  line-height: 1;
}


.doggy {
  width: 300px;
  border: 1px solid var(--corp-line);
  box-shadow: var(--corp-shadow-lg);
  border-radius: var(--corp-radius);
}
</style>