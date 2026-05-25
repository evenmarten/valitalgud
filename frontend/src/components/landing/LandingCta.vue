<template>
  <section class="section">
    <div class="container">
      <div class="cta-banner">
        <template v-if="isLoggedIn">
          <h2 class="cta-title">Korralda oma esimene sündmus</h2>
          <p class="cta-text">Loo sündmus või talgud ja too inimesed kokku juba täna.</p>
          <button class="btn btn-secondary btn-lg" @click="goToCreateEvent">Loo sündmus</button>
        </template>
        <template v-else>
          <h2 class="cta-title">Valmis alustama?</h2>
          <p class="cta-text">Loo konto ja avasta sündmusi, mis su kogukonda elavdavad.</p>
          <button class="btn btn-secondary btn-lg" @click="goToRegister">Loo konto</button>
        </template>
      </div>
    </div>
  </section>
</template>

<script>
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'LandingCta',
  props: {
    isLoggedIn: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    goToCreateEvent() {
      NavigationService.navigateToCreateEvent()
    },
    goToRegister() {
      NavigationService.navigateToRegister()
    },
  },
}
</script>

<style scoped>
.cta-banner {
  background: linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%);
  border: none;
  border-radius: 18px;
  box-shadow: 0 20px 45px rgba(30, 58, 138, 0.28);
  color: var(--nb-white);
  text-align: center;
  padding: 3.25rem 1.5rem;
}

.cta-title {
  color: var(--nb-white);
  font-size: 2.1rem;
  margin-bottom: 0.75rem;
}

.cta-text {
  font-size: 1.15rem;
  max-width: 560px;
  margin: 0 auto 1.75rem;
  opacity: 0.92;
}
</style>
