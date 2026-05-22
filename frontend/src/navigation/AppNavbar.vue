<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid" style="padding-left: 44px; padding-right: 44px;">
      <a class="navbar-brand p-0" href="#" @click.prevent="goToHome">
        <img :src="logo" alt="Valitalgud" height="84" />
      </a>
      <div class="d-flex gap-3 align-items-center">
        <a href="#" @click.prevent="goToHome" class="nav-link text-white">Avaleht</a>
        <div class="dropdown">
          <a
            href="#"
            class="nav-link text-white dropdown-toggle"
            role="button"
            data-bs-toggle="dropdown"
            aria-expanded="false"
          >Mängi</a>
          <ul class="dropdown-menu">
            <li>
              <a class="dropdown-item" href="https://play-cs.com/en/" target="_blank" rel="noopener noreferrer">Counter Strike 1.6</a>
            </li>
            <li>
              <a class="dropdown-item" href="https://dos.zone/doom-dec-1993/" target="_blank" rel="noopener noreferrer">Doom</a>
            </li>
            <li>
              <a class="dropdown-item" href="https://dos.zone/mp/?lobby=q3" target="_blank" rel="noopener noreferrer">Quake</a>
            </li>
            <li>
              <a class="dropdown-item" href="https://dos.zone/mp/?lobby=ut" target="_blank" rel="noopener noreferrer">Unreal Tournament</a>
            </li>
          </ul>
        </div>
        <a v-if="isLoggedIn" href="#" @click.prevent="goToProfile" class="nav-link text-white">Profiil</a>
        <div class="dropdown">
          <a
            href="#"
            class="nav-link text-white dropdown-toggle"
            role="button"
            data-bs-toggle="dropdown"
            aria-expanded="false"
          >Sündmused</a>
          <ul class="dropdown-menu">
            <li>
              <a class="dropdown-item" href="#" @click.prevent="goToEvents">Kõik sündmused</a>
            </li>
            <li v-if="isLoggedIn">
              <a class="dropdown-item" href="#" @click.prevent="goToMyEvents">Minu sündmused</a>
            </li>
            <li v-if="isLoggedIn">
              <a class="dropdown-item" href="#" @click.prevent="goToCalendar">Kalender</a>
            </li>
          </ul>
        </div>
        <a href="#" @click.prevent="goToShop" class="nav-link text-white">e-pood</a>
        <a href="#" @click.prevent="goToContact" class="nav-link text-white">Kontakt</a>
        <a href="#" @click.prevent="goToCart" class="nav-link text-white cart-link" aria-label="Ostukorv" title="Ostukorv">
          <i class="bi bi-cart nav-icon"></i>
          <span v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</span>
        </a>
        <a v-if="isLoggedIn" href="#" @click.prevent="logout" class="nav-link text-white" aria-label="Logi välja" title="Logi välja">
          <i class="bi bi-box-arrow-right nav-icon"></i>
        </a>
        <a v-else href="#" @click.prevent="goToLogin" class="nav-link text-white">Logi sisse</a>
      </div>
    </div>
  </nav>
</template>

<script>
import AuthHelper from '@/auth/auth.js'
import NavigationService from '@/navigation/NavigationService.js'
import logo from '@/assets/logo/logo.png'

export default {
  name: 'AppNavbar',
  data() {
    return {
      isLoggedIn: false,
      cartCount: 0,
      logo,
    }
  },
  methods: {
    goToHome() {
      NavigationService.navigateToHome()
    },
    goToProfile() {
      if (!AuthHelper.isLoggedIn()) {
        NavigationService.navigateToUnauthorized()
        return
      }
      NavigationService.navigateToProfile()
    },
    goToEvents() {
      NavigationService.navigateToEvents()
    },
    goToMyEvents() {
      if (!AuthHelper.isLoggedIn()) {
        NavigationService.navigateToUnauthorized()
        return
      }
      NavigationService.navigateToMyEvents()
    },
    goToCalendar() {
      if (!AuthHelper.isLoggedIn()) {
        NavigationService.navigateToUnauthorized()
        return
      }
      NavigationService.navigateToCalendar()
    },
    goToShop() {
      NavigationService.navigateToShop()
    },
    goToContact() {
      NavigationService.navigateToContact()
    },
    goToCart() {
      NavigationService.navigateToCart()
    },
    goToLogin() {
      NavigationService.navigateToLogin()
    },
    logout() {
      AuthHelper.clearUser()
      this.isLoggedIn = false
      NavigationService.navigateToLogin()
    },
    loadCartCount() {
      const cart = JSON.parse(localStorage.getItem('cart') || '[]')
      this.cartCount = cart.reduce((sum, item) => sum + item.quantity, 0)
    },
  },
  beforeMount() {
    this.isLoggedIn = AuthHelper.isLoggedIn()
    this.loadCartCount()
    window.addEventListener('cart-updated', this.loadCartCount)
  },
  beforeUnmount() {
    window.removeEventListener('cart-updated', this.loadCartCount)
  },
}
</script>

<style scoped>
.dropdown-menu {
  background-color: var(--nb-black);
  border: 3px solid var(--nb-yellow);
  border-radius: 0;
  box-shadow: var(--nb-shadow);
  padding: 0.3rem;
  margin-top: 0.6rem;
}

.dropdown-item {
  color: var(--nb-white);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  padding: 0.4rem 0.8rem;
  border: 2px solid transparent;
}

.dropdown-item:hover,
.dropdown-item:focus {
  background-color: var(--nb-yellow);
  color: var(--nb-black);
  border: 2px solid var(--nb-black);
}

.cart-link {
  position: relative;
}

/* Navbari ikoonid (ostukorv, logi välja) — pisut suuremad kui tekst */
.nav-icon {
  font-size: 1.3rem;
  vertical-align: -0.15em;
}

.cart-badge {
  position: absolute;
  top: -6px;
  right: -10px;
  background-color: var(--nb-pink);
  color: var(--nb-white);
  font-family: 'Archivo Black', sans-serif;
  font-size: 0.65rem;
  font-weight: 900;
  min-width: 18px;
  height: 18px;
  padding: 0 4px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 2px solid var(--nb-black);
  box-shadow: 2px 2px 0 var(--nb-black);
}
</style>