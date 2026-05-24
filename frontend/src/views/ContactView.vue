<template>
  <div class="contact-view">
    <AppNavbar />

    <div class="container py-5 flex-grow-1 d-flex flex-column">
      <div class="row justify-content-center flex-grow-1">
        <div class="col-lg-8 d-flex">
          <div class="card shadow flex-grow-1">
            <div class="card-body p-4 p-md-5 d-flex flex-column">
              <h1 class="text-center mb-3">Kontakt</h1>
              <p class="text-muted text-center mb-4">
                Alustame koostööd. Võtke meiega ühendust ja me vastame Teile esimesel võimalusel.
              </p>

              <AlertError :error-message="errorMessage" />
              <div v-if="successMessage" class="alert alert-success" role="alert">
                {{ successMessage }}
              </div>

              <form @submit.prevent="sendMessage" class="d-flex flex-column flex-grow-1">
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

                <div class="mt-3 mb-4 flex-grow-1 d-flex flex-column">
                  <label for="message" class="form-label">Sõnum</label>
                  <textarea
                    id="message"
                    v-model="contactForm.message"
                    class="form-control flex-grow-1"
                    rows="8"
                  ></textarea>
                </div>

                <button type="submit" class="btn btn-dark w-100" :disabled="isSending">
                  {{ isSending ? 'Saadan…' : 'Saada' }}
                </button>
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
import ContactService from '@/api-services/ContactService.js'

export default {
  name: 'ContactView',
  components: { AppNavbar, AlertError },
  data() {
    return {
      successMessage: '',
      errorMessage: '',
      isSending: false,
      // TEMP: prefilled test values for faster testing — remove before delivery
      contactForm: {
        nameOrCompany: 'Test Testija',
        email: 'test@example.com',
        message: 'Tere! See on testpäring kontaktivormist.',
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

      this.isSending = true
      ContactService.sendContactMessage(this.contactForm)
        .then(() => this.handleSendSuccess())
        .catch((error) => this.handleSendError(error))
        .finally(() => {
          this.isSending = false
        })
    },

    handleSendSuccess() {
      this.successMessage = 'Aitäh! Sinu sõnum on saadetud — vastame esimesel võimalusel.'
      this.resetForm()
    },

    handleSendError(error) {
      this.errorMessage =
        error.response?.data?.message ?? 'Sõnumi saatmine ebaõnnestus. Proovi hetke pärast uuesti.'
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

<style scoped>
/* Venita vaade page-wrapperi kõrgusele, et vorm täidaks ruumi ja footerini
   jääks ühtlane vahe (sama nagu poe/sündmuste vaates), mitte suur tühi ala. */
.contact-view {
  flex: 1;
  display: flex;
  flex-direction: column;
}
</style>