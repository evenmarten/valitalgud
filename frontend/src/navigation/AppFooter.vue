<template>
  <footer class="app-footer">
    <div class="container py-5">
      <div class="row g-4">
        <div class="col-md-5">
          <h4 class="footer-brand mb-2">VALITALGUD</h4>
          <p class="mb-0">
            Platvorm sündmuste ja talgute korraldamiseks ning avastamiseks.
            Too kogukond kokku — leia, registreeru ja korralda.
          </p>
        </div>
        <div class="col-md-3">
          <h6 class="footer-heading">Avasta</h6>
          <ul class="footer-links">
            <li><a href="#" @click.prevent="browseEvents">Sündmused</a></li>
            <li><a href="#" @click.prevent="goToShop">e-pood</a></li>
          </ul>
        </div>
        <div class="col-md-4">
          <h6 class="footer-heading">Alusta</h6>
          <ul class="footer-links">
            <li v-if="!isLoggedIn"><a href="#" @click.prevent="goToRegister">Loo konto</a></li>
            <li v-if="!isLoggedIn"><a href="#" @click.prevent="goToLogin">Logi sisse</a></li>
            <li v-if="isLoggedIn"><a href="#" @click.prevent="goToCreateEvent">Loo sündmus</a></li>
            <li v-if="isLoggedIn"><a href="#" @click.prevent="goToMyEvents">Minu sündmused</a></li>
          </ul>
        </div>
      </div>
      <hr class="footer-divider" />
      <p class="footer-bottom mb-0">© 2026 Valitalgud · Sündmused ja talgud kogukonnale</p>
    </div>
  </footer>
</template>

<script>
import AuthHelper from '@/auth/auth.js'
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'AppFooter',
  data() {
    return {
      isLoggedIn: false,
    }
  },
  methods: {
    browseEvents() {
      if (this.isLoggedIn) {
        NavigationService.navigateToEvents()
      } else {
        NavigationService.navigateToRegister()
      }
    },
    goToRegister() {
      NavigationService.navigateToRegister()
    },
    goToLogin() {
      NavigationService.navigateToLogin()
    },
    goToCreateEvent() {
      NavigationService.navigateToCreateEvent()
    },
    goToMyEvents() {
      NavigationService.navigateToMyEvents()
    },
    goToShop() {
      NavigationService.navigateToShop()
    },
  },
  beforeMount() {
    this.isLoggedIn = AuthHelper.isLoggedIn()
  },
}
</script>

<style scoped>
.app-footer {
  background: var(--nb-black);
  border-top: 6px solid var(--nb-yellow);
  color: var(--nb-white);
}

.footer-brand {
  font-family: 'Archivo Black', sans-serif;
  color: var(--nb-yellow);
  letter-spacing: 1px;
}

.footer-heading {
  text-transform: uppercase;
  color: var(--nb-yellow);
  font-weight: 700;
  margin-bottom: 0.75rem;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 0.4rem;
}

.footer-links a {
  color: var(--nb-white);
  text-decoration: none;
  font-weight: 600;
}

.footer-links a:hover {
  color: var(--nb-yellow);
}

.footer-divider {
  border-color: var(--nb-yellow);
  opacity: 0.5;
  margin: 2rem 0 1rem;
}

.footer-bottom {
  font-size: 0.9rem;
  color: #bbb;
}
</style>