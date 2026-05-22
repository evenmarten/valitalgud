<template>
  <div>
    <AppNavbar />

    <div class="hero-banner">
      <div class="container text-center">
        <p class="hero-label">Tere tulemast kogukonda</p>
        <h1 class="hero-title">Konto loodud!</h1>
      </div>
    </div>

    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="success-card text-center">
            <div class="success-check">✓</div>

            <h2 class="success-title">{{ greeting }}</h2>
            <p class="success-text">
              Sinu konto on edukalt loodud. Logi sisse, et hakata sündmusi
              avastama ja korraldama.
            </p>

            <div class="d-flex gap-3 justify-content-center flex-wrap">
              <button class="btn btn-primary btn-lg" @click="goToLogin">Logi sisse</button>
              <button class="btn btn-outline-secondary btn-lg" @click="goToHome">Avalehele</button>
            </div>
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
  name: 'RegisterSuccessView',
  components: { AppNavbar },
  data() {
    return {
      fullName: '',
    }
  },
  computed: {
    greeting() {
      return this.fullName ? `Tere, ${this.fullName}!` : 'Registreerimine õnnestus'
    },
  },
  methods: {
    goToLogin() {
      NavigationService.navigateToLogin()
    },
    goToHome() {
      NavigationService.navigateToHome()
    },
  },
  beforeMount() {
    // Leht kuvatakse ainult pärast edukat registreerumist (vt RegisterView).
    // Otse URL-iga sisenedes suuname tagasi registreerumisvormi.
    if (!window.history.state.registered) {
      NavigationService.navigateToRegister()
      return
    }
    this.fullName = window.history.state.fullName ?? ''
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
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--corp-blue);
  margin: 0 0 0.2rem;
}

.hero-title {
  font-size: 3rem;
  color: var(--corp-ink);
  margin: 0;
  line-height: 1;
}

.success-card {
  background: var(--nb-white);
  border: 1px solid var(--corp-line);
  border-radius: var(--corp-radius);
  box-shadow: var(--corp-shadow);
  padding: 2.5rem 2rem;
}

.success-check {
  width: 72px;
  height: 72px;
  margin: 0 auto 1.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--nb-green);
  color: #fff;
  font-size: 2.2rem;
  font-weight: 700;
}

.success-title {
  font-size: 1.6rem;
  color: var(--corp-ink);
  margin-bottom: 0.75rem;
}

.success-text {
  color: var(--corp-muted);
  font-size: 1.05rem;
  margin-bottom: 1.75rem;
}
</style>
