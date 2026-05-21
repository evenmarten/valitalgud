<template>
  <div>
    <AppNavbar />

    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-lg-8">
          <div class="card shadow">
            <div class="card-body p-4 p-md-5">
              <h1 class="text-center mb-3">Kontakt</h1>
              <p class="text-muted text-center mb-4">
                Alustame koostööd. Võtke meiega ühendust ja me vastame Teile esimesel võimalusel.
              </p>

              <AlertError :error-message="errorMessage" />
              <div v-if="successMessage" class="alert alert-success" role="alert">
                {{ successMessage }}
              </div>

              <form @submit.prevent="sendMessage">
                <div class="row g-3">
                  <div class="col-md-6">
                    <label for="nameOrCompany" class="form-label">Nimi/Ettevõte</label>
                    <input
                      id="nameOrCompany"
                      v-model="contactForm.nameOrCompany"
                      type="text"
                      class="form-control"
                    />
                  </div>

                  <div class="col-md-6">
                    <label for="email" class="form-label">E-mail</label>
                    <input
                      id="email"
                      v-model="contactForm.email"
                      type="email"
                      class="form-control"
                    />
                  </div>
                </div>

                <div class="mt-3 mb-4">
                  <label for="message" class="form-label">Sõnum</label>
                  <textarea
                    id="message"
                    v-model="contactForm.message"
                    class="form-control"
                    rows="8"
                  ></textarea>
                </div>

                <button type="submit" class="btn btn-dark w-100">Saada</button>
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
import AlertError from '@/components/common/AlertError.vue'

export default {
  name: 'ContactView',
  components: { AppNavbar, AlertError },
  data() {
    return {
      successMessage: '',
      errorMessage: '',
      contactForm: {
        nameOrCompany: '',
        email: '',
        message: '',
      },
    }
  },
  methods: {
    sendMessage() {
      this.successMessage = ''
      this.errorMessage = ''

      if (!this.isFormValid()) {
        return
      }

      // TODO: ühenda Resend API-ga, et päring meiliaadressile saata.
      // Praegu kinnitame esitamise kohe frontendis.
      this.successMessage = 'Aitäh! Sinu sõnum on saadetud — vastame esimesel võimalusel.'
      this.resetForm()
    },

    isFormValid() {
      if (!this.contactForm.nameOrCompany.trim()) {
        this.errorMessage = 'Palun täida nimi või ettevõte.'
        return false
      }
      if (!this.isEmailValid(this.contactForm.email)) {
        this.errorMessage = 'Palun sisesta korrektne e-mail.'
        return false
      }
      if (!this.contactForm.message.trim()) {
        this.errorMessage = 'Palun kirjuta sõnum.'
        return false
      }
      return true
    },

    isEmailValid(email) {
      return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())
    },

    resetForm() {
      this.contactForm.nameOrCompany = ''
      this.contactForm.email = ''
      this.contactForm.message = ''
    },
  },
}
</script>