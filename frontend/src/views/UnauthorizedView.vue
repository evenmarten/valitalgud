<template>
  <div>
    <AppNavbar />

    <div class="hero-banner">
      <div class="container text-center">
        <h1 class="hero-title">Ligipääs puudub</h1>
        <p class="hero-sub">See leht on ainult sisselogitud kasutajatele</p>
      </div>
    </div>

    <div class="container py-5">
      <div class="row justify-content-center g-4">

        <div class="col-md-4">
          <div class="action-card">
            <p class="action-tag">Olemasolev konto</p>
            <h3 class="action-title">Logi sisse</h3>
            <p class="action-text">Sul on juba konto olemas? Sisene ja jätka sealt, kus pooleli jäid.</p>
            <button class="btn btn-primary w-100" @click="goToLogin">Logi sisse</button>
          </div>
        </div>

        <div class="col-md-4">
          <div class="action-card">
            <p class="action-tag">Uus kasutaja</p>
            <h3 class="action-title">Loo konto</h3>
            <p class="action-text">Liitu kogukonnaga tasuta — registreeru ja osale sündmustel.</p>
            <button class="btn btn-success w-100" @click="goToRegister">Loo konto</button>
          </div>
        </div>

      </div>

      <div class="text-center mt-4">
        <button class="btn btn-outline-secondary" @click="goToHome">Tagasi avalehele</button>
      </div>
    </div>
  </div>
</template>

<script>
import AppNavbar from '@/navigation/AppNavbar.vue'
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'UnauthorizedView',
  components: { AppNavbar },
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

<style scoped>
.hero-banner {
  background-color: var(--corp-surface);
  border-bottom: 1px solid var(--corp-line);
  padding: 2.5rem 0 2rem;
}


.hero-title {
  font-size: 3rem;
  color: var(--corp-ink);
  margin: 0 0 0.4rem;
  line-height: 1;
}

.hero-sub {
  font-size: 1rem;
  font-weight: 600;
  color: var(--corp-muted);
  opacity: 0.85;
  margin: 0;
}

.action-card {
  border: 1px solid var(--corp-line);
  box-shadow: var(--corp-shadow);
  border-radius: var(--corp-radius);
  background: var(--nb-white);
  padding: 1.75rem;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.action-tag {
  font-size: 0.75rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--corp-muted);
  margin: 0 0 0.3rem;
}

.action-title {
  font-family: 'Archivo Black', sans-serif;
  text-transform: none;
  font-size: 1.6rem;
  margin: 0 0 0.75rem;
}

.action-text {
  font-size: 0.95rem;
  color: #555;
  flex-grow: 1;
  margin-bottom: 1.25rem;
}
</style>
