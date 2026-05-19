<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid px-4">
      <a class="navbar-brand fw-bold" href="#" @click.prevent="goToHome">LOGO</a>
      <div class="d-flex gap-3 align-items-center">
        <a href="#" @click.prevent="goToHome" class="nav-link text-white">Homepage</a>
        <a href="#" @click.prevent="goToProfile" class="nav-link text-white">Profile</a>
        <a href="#" @click.prevent="goToEvents" class="nav-link text-white">Events</a>
        <a href="#" @click.prevent="goToMyEvents" class="nav-link text-white">My Events</a>
        <a href="#" @click.prevent="goToCalendar" class="nav-link text-white">Calendar</a>
        <a href="#" @click.prevent="logout" class="nav-link text-white">Logout</a>
        <a href="#" @click.prevent="goToCart" class="nav-link text-white">Cart</a>
      </div>
    </div>
  </nav>
</template>

<script>
import AuthHelper from '@/auth/auth.js'
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'AppNavbar',
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
      if (!AuthHelper.isLoggedIn()) {
        NavigationService.navigateToUnauthorized()
        return
      }
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
    goToCart() {
      NavigationService.navigateToCart()
    },
    logout() {
      AuthHelper.clearUser()
      NavigationService.navigateToLogin()
    },
  },
}
</script>