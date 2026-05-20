<template>
  <div>
    <AppNavbar />

    <div class="container py-3">
      <div class="row justify-content-center">
        <div class="col-md-5">
          <div class="card shadow">
            <div class="card-body p-4">
              <h2 class="mb-2">Registreeru</h2>
              <p class="text-muted mb-4">Loo uus konto, et hakata sündmusi avastama.</p>

              <AlertError :error-message="errorMessage" />

              <form @submit.prevent="register">
                <div class="mb-3">
                  <label for="fullName" class="form-label">
                    Täisnimi <span class="text-danger">***</span>
                  </label>
                  <input
                    id="fullName"
                    v-model="registerDto.fullName"
                    type="text"
                    class="form-control"
                    placeholder="Nt. Mari Maasikas"
                  />
                </div>

                <div class="mb-3">
                  <label for="email" class="form-label">
                    E-post <span class="text-danger">***</span>
                  </label>
                  <input
                    id="email"
                    v-model="registerDto.email"
                    type="email"
                    class="form-control"
                    placeholder="sinu@email.ee"
                  />
                </div>

                <div class="mb-3">
                  <label for="password" class="form-label">
                    Parool <span class="text-danger">***</span>
                  </label>
                  <input
                    id="password"
                    v-model="registerDto.password"
                    type="password"
                    class="form-control"
                  />
                  <div class="form-text">Vähemalt 8 tähemärki</div>
                </div>

                <div class="mb-3">
                  <label for="repeatPassword" class="form-label">
                    Korda parooli <span class="text-danger">***</span>
                  </label>
                  <input
                    id="repeatPassword"
                    v-model="repeatPassword"
                    type="password"
                    class="form-control"
                  />
                </div>

                <div class="mb-3">
                  <label for="phone" class="form-label">
                    Telefon <span class="text-muted small">(vabatahtlik)</span>
                  </label>
                  <input
                    id="phone"
                    v-model="registerDto.phone"
                    type="text"
                    class="form-control"
                    placeholder="+372 555 1234"
                  />
                </div>

                <div class="mb-4">
                  <label for="description" class="form-label">
                    Kirjeldus <span class="text-muted small">(vabatahtlik)</span>
                  </label>
                  <textarea
                    id="description"
                    v-model="registerDto.description"
                    class="form-control"
                    rows="3"
                    placeholder="Räägi endast paar sõna..."
                  />
                </div>

                <button type="submit" class="btn btn-primary w-100">Registreeru</button>
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
import NavigationService from '@/navigation/NavigationService.js'
import AlertError from '@/components/common/AlertError.vue'

export default {
  name: 'RegisterView',
  components: { AppNavbar, AlertError },
  data() {
    return {
      registerDto: {
        fullName: '',
        email: '',
        password: '',
        phone: '',
        description: '',
      },
      repeatPassword: '',
      errorMessage: '',
    }
  },
  methods: {
    register() {
      if (!this.validateForm()) {
        return
      }
      AuthService.sendRegisterRequest(this.registerDto)
        .then(() => this.handleRegisterResponse())
        .catch((error) => this.handleRegisterError(error))
        .finally()
    },

    validateForm() {
      if (!this.registerDto.fullName.trim()
        || !this.registerDto.email.trim()
        || !this.registerDto.password.trim()) {
        this.errorMessage = 'Palun täitke kõik kohustuslikud väljad'
        return false
      }
      if (this.registerDto.password.length < 8) {
        this.errorMessage = 'Parool peab olema vähemalt 8 tähemärki'
        return false
      }
      if (this.registerDto.password !== this.repeatPassword) {
        this.errorMessage = 'Paroolid ei kattu'
        return false
      }
      this.errorMessage = ''
      return true
    },

    handleRegisterResponse() {
      NavigationService.navigateToLogin()
    },

    handleRegisterError(error) {
      const statusCode = error.response?.status
      if (statusCode === 400) {
        this.errorMessage = 'Palun täitke kõik kohustuslikud väljad'
      } else if (statusCode === 409) {
        this.errorMessage = 'See e-post on juba kasutusel'
      } else {
        NavigationService.navigateToErrorView()
      }
    },
  },
}
</script>
