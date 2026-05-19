<template>
  <div>
    <AppNavbar />

    <div class="container py-4" style="max-width: 600px;">
      <h2 class="mb-4">Arveldus &amp; transport</h2>

      <AlertError :error-message="errorMessage" />

      <form @submit.prevent="submitOrder">
        <div class="mb-3">
          <label class="form-label">Eesnimi <span class="text-danger">*</span></label>
          <input v-model="form.firstName" type="text" class="form-control" />
        </div>

        <div class="mb-3">
          <label class="form-label">Perekonnanimi <span class="text-danger">*</span></label>
          <input v-model="form.lastName" type="text" class="form-control" />
        </div>

        <div class="mb-3">
          <label class="form-label">Ettevõtte nimi (valikuline)</label>
          <input v-model="form.companyName" type="text" class="form-control" />
        </div>

        <div class="mb-3">
          <label class="form-label">Riik / piirkond <span class="text-danger">*</span></label>
          <select v-model="form.country" class="form-select">
            <option value="Eesti">Eesti</option>
            <option value="Läti">Läti</option>
            <option value="Leedu">Leedu</option>
            <option value="Soome">Soome</option>
            <option value="Rootsi">Rootsi</option>
          </select>
        </div>

        <div class="row mb-3">
          <div class="col-8">
            <label class="form-label">Tänav ja majanumber <span class="text-danger">*</span></label>
            <input v-model="form.street" type="text" class="form-control" placeholder="Tänav ja majanumber" />
          </div>
          <div class="col-4">
            <label class="form-label">Postiindeks <span class="text-danger">*</span></label>
            <input v-model="form.postalCode" type="text" class="form-control" />
          </div>
        </div>

        <div class="row mb-3">
          <div class="col-8">
            <label class="form-label">Linn / Alevik <span class="text-danger">*</span></label>
            <input v-model="form.city" type="text" class="form-control" />
          </div>
          <div class="col-4">
            <label class="form-label">Telefon <span class="text-danger">*</span></label>
            <input v-model="form.phone" type="text" class="form-control" />
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">E-posti aadress <span class="text-danger">*</span></label>
          <input v-model="form.email" type="text" class="form-control" />
        </div>

        <p class="text-muted small mb-3">
          Sinu e-post ja telefon on salvestatud selleks, et saaksime sulle tellimuse kohta teavitusi saata.
        </p>

        <button type="submit" class="btn btn-primary w-100">Pay</button>
      </form>
    </div>
  </div>
</template>

<script>
import AppNavbar from '@/navigation/AppNavbar.vue'
import AlertError from '@/components/common/AlertError.vue'
import OrderService from '@/api-services/OrderService.js'
import AuthHelper from '@/auth/auth.js'
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'CheckoutView',
  components: { AppNavbar, AlertError },
  data() {
    return {
      form: {
        firstName: '',
        lastName: '',
        companyName: '',
        country: 'Eesti',
        street: '',
        postalCode: '',
        city: '',
        phone: '',
        email: '',
      },
      errorMessage: '',
    }
  },
  methods: {
    submitOrder() {
      this.errorMessage = ''
      OrderService.sendCreateOrderRequest(this.buildCreateOrderDto())
        .then((response) => this.handleCreateOrderResponse(response.data))
        .catch((error) => this.handleCreateOrderError(error))
        .finally()
    },

    buildCreateOrderDto() {
      const cartItems = JSON.parse(localStorage.getItem('cart') || '[]')
      const user = AuthHelper.getUser()
      return {
        userId: user ? user.userId : null,
        firstName: this.form.firstName,
        lastName: this.form.lastName,
        companyName: this.form.companyName,
        country: this.form.country,
        street: this.form.street,
        postalCode: this.form.postalCode,
        city: this.form.city,
        phone: this.form.phone,
        email: this.form.email,
        items: cartItems.map((item) => ({ productId: item.productId, quantity: item.quantity })),
      }
    },

    handleCreateOrderResponse(order) {
      localStorage.removeItem('cart')
      NavigationService.navigateToOrderSuccess(order.orderId)
    },

    handleCreateOrderError(error) {
      const statusCode = error.response?.status
      if (statusCode === 400) {
        this.errorMessage = error.response.data.message
      } else if (statusCode === 401) {
        NavigationService.navigateToLogin()
      } else {
        NavigationService.navigateToErrorView()
      }
    },
  },
  beforeMount() {
    const cartItems = JSON.parse(localStorage.getItem('cart') || '[]')
    if (cartItems.length === 0) {
      NavigationService.navigateToShop()
    }
  },
}
</script>