<template>
  <div>
    <AppNavbar />

    <div class="container py-5">
    <div class="row justify-content-center">
      <div class="col-md-5">
        <div class="card shadow">
          <div class="card-body p-4">
            <h2 class="text-center mb-4">Logi sisse</h2>

            <AlertError :error-message="errorMessage" />

            <form @submit.prevent="login">
              <div class="mb-3">
                <label for="email" class="form-label">E-post</label>
                <input
                  id="email"
                  v-model="loginDto.email"
                  type="email"
                  class="form-control"
                  placeholder="sinu@email.ee"
                />
              </div>

              <div class="mb-4">
                <label for="password" class="form-label">Parool</label>
                <input
                  id="password"
                  v-model="loginDto.password"
                  type="password"
                  class="form-control"
                  placeholder="********"
                />
              </div>

              <div class="d-flex gap-2 justify-content-center">
                <button type="submit" class="btn btn-primary">Logi sisse</button>
                <button type="button" class="btn btn-secondary" @click="goToRegister">
                  Loo kasutaja
                </button>
              </div>

              <div class="text-center mt-3">
                <a href="#" @click.prevent="goHome">Tagasi avalehele</a>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script>
import AppNavbar from '@/navigation/AppNavbar.vue'
import AuthService from '@/api-services/AuthService.js'
import AuthHelper from '@/auth/auth.js'
import NavigationService from '@/navigation/NavigationService.js'
import AlertError from '@/components/common/AlertError.vue'

export default {
  name: 'LoginView',
  components: { AppNavbar, AlertError },
  data() {
    return {
      // TEMP: prefilled organizer credentials for faster testing — remove before delivery
      loginDto: {
        email: 'organizer@example.com',
        password: 'password',
      },
      errorMessage: '',
    }
  },
  methods: {
    login() {
      AuthService.sendLoginRequest(this.loginDto)
        .then((response) => this.handleLoginResponse(response.data))
        .catch((error) => this.handleLoginError(error))
        .finally()
    },

    handleLoginResponse(loginResponse) {
      AuthHelper.saveUser(loginResponse)
      NavigationService.navigateToEvents()
    },

    handleLoginError(error) {
      const statusCode = error.response?.status
      if (statusCode === 400) {
        this.errorMessage = 'Palun täitke kõik väljad'
      } else if (statusCode === 401) {
        this.errorMessage = 'Vale email või parool'
      } else if (statusCode === 403) {
        this.errorMessage = 'Teie konto on blokeeritud. Pöörduge administraatori poole.'
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    goToRegister() {
      NavigationService.navigateToRegister()
    },

    goHome() {
      NavigationService.navigateToHome()
    },
  },
}
</script>
