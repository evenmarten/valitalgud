<template>
  <div>
    <AppNavbar />

    <div class="container py-4" style="max-width: 860px;">
      <h2 class="mb-4">Arveldus &amp; transport</h2>

      <AlertError :error-message="errorMessage" />

      <form @submit.prevent="submitOrder" class="fs-5">
        <div class="row mb-4">
          <div class="col-6">
            <label class="form-label fw-semibold">Eesnimi <span class="text-danger">*</span></label>
            <input v-model="form.firstName" type="text" class="form-control form-control-lg" />
          </div>
          <div class="col-6">
            <label class="form-label fw-semibold">Perekonnanimi <span class="text-danger">*</span></label>
            <input v-model="form.lastName" type="text" class="form-control form-control-lg" />
          </div>
        </div>

        <div class="mb-4">
          <label class="form-label fw-semibold">Ettevõtte nimi (valikuline)</label>
          <input v-model="form.companyName" type="text" class="form-control form-control-lg" />
        </div>

        <div class="mb-4">
          <label class="form-label fw-semibold">Riik / piirkond <span class="text-danger">*</span></label>
          <select v-model="form.country" class="form-select form-select-lg">
            <option value="Eesti">Eesti</option>
            <option value="Läti">Läti</option>
            <option value="Leedu">Leedu</option>
            <option value="Soome">Soome</option>
            <option value="Rootsi">Rootsi</option>
          </select>
        </div>

        <div class="row mb-4">
          <div class="col-8">
            <label class="form-label fw-semibold">Tänav ja majanumber <span class="text-danger">*</span></label>
            <input v-model="form.street" type="text" class="form-control form-control-lg" placeholder="Tänav ja majanumber" />
          </div>
          <div class="col-4">
            <label class="form-label fw-semibold">Postiindeks <span class="text-danger">*</span></label>
            <input v-model="form.postalCode" type="text" class="form-control form-control-lg" />
          </div>
        </div>

        <div class="row mb-4">
          <div class="col-8">
            <label class="form-label fw-semibold">Linn / Alevik <span class="text-danger">*</span></label>
            <input v-model="form.city" type="text" class="form-control form-control-lg" />
          </div>
          <div class="col-4">
            <label class="form-label fw-semibold">Telefon <span class="text-danger">*</span></label>
            <input v-model="form.phone" type="text" class="form-control form-control-lg" />
          </div>
        </div>

        <div class="mb-4">
          <label class="form-label fw-semibold">E-posti aadress <span class="text-danger">*</span></label>
          <input v-model="form.email" type="text" class="form-control form-control-lg" />
        </div>

        <p class="text-muted mb-4">
          Sinu e-post ja telefon on salvestatud selleks, et saaksime sulle tellimuse kohta teavitusi saata.
        </p>

        <p class="fw-semibold mb-2">Maksa pangalingiga:</p>
        <div class="d-flex gap-3 flex-wrap mb-3">
          <button
            v-for="bank in banks"
            :key="bank.id"
            type="button"
            class="bank-btn"
            :class="{ 'bank-btn--selected': selectedBank === bank.id }"
            @click="selectBank(bank.id)"
          >
            <img :src="bank.logo" :alt="bank.name" class="bank-logo" />
            <span v-if="selectedBank === bank.id" class="bank-check">✓</span>
          </button>
        </div>

        <button type="submit" class="btn btn-primary w-100 btn-lg">Maksma</button>
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
import swedbankLogo from '@/assets/banks/swedbank.svg'
import sebLogo from '@/assets/banks/seb.svg'
import lhvLogo from '@/assets/banks/lhv.svg'
import luminorLogo from '@/assets/banks/luminor.svg'
import coopLogo from '@/assets/banks/coop.svg'

export default {
  name: 'CheckoutView',
  components: { AppNavbar, AlertError },
  data() {
    return {
      banks: [
        { id: 'swedbank', name: 'Swedbank', logo: swedbankLogo },
        { id: 'seb', name: 'SEB', logo: sebLogo },
        { id: 'lhv', name: 'LHV', logo: lhvLogo },
        { id: 'luminor', name: 'Luminor', logo: luminorLogo },
        { id: 'coop', name: 'Coop Pank', logo: coopLogo },
      ],
      selectedBank: '',
      // AJUTINE: eeltäidetud testväärtused kiiremaks testimiseks — eemalda enne tootmist
      form: {
        firstName: 'Mari',
        lastName: 'Maasikas',
        companyName: 'Test OÜ',
        country: 'Eesti',
        street: 'Testi tänav 5',
        postalCode: '10115',
        city: 'Tallinn',
        phone: '+372 5555 1234',
        email: 'mari.maasikas@example.com',
      },
      errorMessage: '',
    }
  },
  methods: {
    selectBank(bankId) {
      this.selectedBank = bankId
    },

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

<style scoped>
.bank-btn {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 132px;
  height: 60px;
  padding: 8px 18px;
  background: var(--nb-white);
  border: var(--nb-border);
  border-radius: 0;
  box-shadow: 3px 3px 0 var(--nb-black);
  cursor: pointer;
  transition: transform 0.08s ease, box-shadow 0.08s ease;
}

.bank-btn:hover {
  transform: translate(-1px, -1px);
  box-shadow: 5px 5px 0 var(--nb-black);
}

.bank-btn--selected {
  border-color: var(--nb-blue);
  box-shadow: 5px 5px 0 var(--nb-blue);
}

.bank-logo {
  height: 28px;
  width: auto;
  display: block;
}

.bank-check {
  position: absolute;
  top: -10px;
  right: -10px;
  width: 24px;
  height: 24px;
  background: var(--nb-blue);
  color: var(--nb-white);
  border: 2px solid var(--nb-black);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
}
</style>