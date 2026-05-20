<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid" style="padding-left: 44px; padding-right: 44px;">
      <a class="navbar-brand p-0" href="#" @click.prevent="goToHome">
        <img :src="logo" alt="Valitalgud" height="84" />
      </a>
      <div class="d-flex gap-3 align-items-center">
        <a href="#" @click.prevent="goToHome" class="nav-link text-white">Homepage</a>
        <a v-if="isLoggedIn" href="#" @click.prevent="goToProfile" class="nav-link text-white">Profile</a>
        <a href="#" @click.prevent="goToEvents" class="nav-link text-white">Events</a>
        <a v-if="isLoggedIn" href="#" @click.prevent="goToMyEvents" class="nav-link text-white">My Events</a>
        <a v-if="isLoggedIn" href="#" @click.prevent="goToCalendar" class="nav-link text-white">Calendar</a>
        <a href="#" @click.prevent="goToShop" class="nav-link text-white">Shop</a>
        <a href="#" @click.prevent="goToCart" class="nav-link text-white">Cart</a>
        <a v-if="isLoggedIn" href="#" @click.prevent="logout" class="nav-link text-white">Logout</a>
        <a v-else href="#" @click.prevent="goToLogin" class="nav-link text-white">Login</a>
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
  },
  beforeMount() {
    this.isLoggedIn = AuthHelper.isLoggedIn()
  },
}
</script>