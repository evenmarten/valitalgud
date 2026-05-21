<template>
  <div>
    <AppNavbar />

    <div class="step-bar">
      <div class="container">
        <div class="step-list">
          <div class="step step--done">
            <div class="step-circle">✓</div>
            <span class="step-label">Ostukorv</span>
          </div>
          <div class="step-line"></div>
          <div class="step" :class="currentStep === 2 ? 'step--active' : 'step--done'">
            <div class="step-circle">{{ currentStep > 2 ? '✓' : '2' }}</div>
            <span class="step-label">Tarneviis</span>
          </div>
          <div class="step-line"></div>
          <div class="step" :class="currentStep === 3 ? 'step--active' : 'step--upcoming'">
            <div class="step-circle">3</div>
            <span class="step-label">Maksmine</span>
          </div>
        </div>
      </div>
    </div>

    <div class="container py-4" style="max-width: 1100px;">
      <AlertError :error-message="errorMessage" />

      <form @submit.prevent="submitOrder">
        <div class="row g-5 align-items-start">

          <div class="col-lg-7">
            <div class="section-block mb-4">
              <h5 class="section-title">Tarneandmed</h5>

              <div class="row mb-3">
                <div class="col-6">
                  <label class="form-label">Eesnimi <span class="text-danger">*</span></label>
                  <input v-model="form.firstName" type="text" class="form-control" />
                </div>
                <div class="col-6">
                  <label class="form-label">Perekonnanimi <span class="text-danger">*</span></label>
                  <input v-model="form.lastName" type="text" class="form-control" />
                </div>
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

              <div class="mb-0">
                <label class="form-label">E-posti aadress <span class="text-danger">*</span></label>
                <input v-model="form.email" type="text" class="form-control" />
              </div>
            </div>

            <div class="section-block mb-4">
              <h5 class="section-title">Tarneviis</h5>

              <div class="d-flex flex-column gap-3">
                <div
                  v-for="method in deliveryMethods"
                  :key="method.id"
                  class="delivery-option"
                  :class="{ 'delivery-option--selected': selectedDelivery === method.id }"
                  @click="selectedDelivery = method.id"
                >
                  <div class="delivery-option-info">
                    <span class="delivery-option-name">{{ method.name }}</span>
                    <span class="delivery-option-desc">{{ method.description }}</span>
                  </div>
                  <span class="delivery-option-price">{{ method.price.toFixed(2) }} €</span>
                </div>
              </div>
            </div>

            <div class="section-block">
              <h5 class="section-title">Makse</h5>

              <p class="text-muted small mb-3">
                Sinu e-post ja telefon on salvestatud selleks, et saaksime sulle tellimuse kohta teavitusi saata.
              </p>

              <div class="d-flex gap-3 flex-wrap">
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
            </div>
          </div>

          <div class="col-lg-5">
            <div class="summary-card">
              <h5 class="section-title">Tellimuse kokkuvõte</h5>

              <div class="summary-items">
                <div
                  v-for="item in cartItems"
                  :key="item.productId"
                  class="summary-item"
                >
                  <img
                    v-if="item.imageUrl"
                    :src="item.imageUrl"
                    :alt="item.name"
                    class="summary-item-img"
                  />
                  <div v-else class="summary-item-img summary-item-img--empty"></div>
                  <div class="summary-item-info">
                    <span class="summary-item-name">{{ item.name }}</span>
                    <span class="summary-item-qty">{{ item.quantity }} tk</span>
                  </div>
                  <span class="summary-item-price">{{ item.lineTotal.toFixed(2) }} €</span>
                </div>
              </div>

              <div class="summary-totals">
                <div class="summary-row">
                  <span>Vahesumma</span>
                  <span>{{ subtotal.toFixed(2) }} €</span>
                </div>
                <div class="summary-row">
                  <span>Transport</span>
                  <span>{{ shipping.toFixed(2) }} €</span>
                </div>
                <div class="summary-row">
                  <span>Käibemaks (8%)</span>
                  <span>{{ tax.toFixed(2) }} €</span>
                </div>
                <div class="summary-row summary-row--total">
                  <span>Kokku</span>
                  <span>{{ total.toFixed(2) }} €</span>
                </div>
              </div>

              <button type="submit" class="btn btn-primary w-100 mt-4">Maksma</button>
            </div>
          </div>

        </div>
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
      cartItems: [],
      banks: [
        { id: 'swedbank', name: 'Swedbank', logo: swedbankLogo },
        { id: 'seb', name: 'SEB', logo: sebLogo },
        { id: 'lhv', name: 'LHV', logo: lhvLogo },
        { id: 'luminor', name: 'Luminor', logo: luminorLogo },
        { id: 'coop', name: 'Coop Pank', logo: coopLogo },
      ],
      selectedBank: '',
      selectedDelivery: 'omniva',
      deliveryMethods: [
        { id: 'omniva',     name: 'Omniva pakiautomaat',  description: 'Lähim pakiautomaat Sinu valikul',   price: 3.99 },
        { id: 'dpd',        name: 'DPD pakiautomaat',     description: 'Lähim pakiautomaat Sinu valikul',   price: 3.49 },
        { id: 'smartpost',  name: 'SmartPost pakiautomaat', description: 'Lähim pakiautomaat Sinu valikul', price: 3.49 },
        { id: 'courier',    name: 'Kullerteenus ukseni',  description: 'Tarne otse Sinu ukse taha 1-2 tööpäeva',  price: 6.99 },
      ],
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
  computed: {
    currentStep() {
      return this.selectedBank ? 3 : 2
    },

    subtotal() {
      return this.cartItems.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0)
    },
    shipping() {
      if (this.cartItems.length === 0) return 0
      const method = this.deliveryMethods.find((m) => m.id === this.selectedDelivery)
      return method ? method.price : 0
    },
    tax() {
      return Math.round(this.subtotal * 0.08 * 100) / 100
    },
    total() {
      return this.subtotal + this.shipping + this.tax
    },
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
        items: this.cartItems.map((item) => ({ productId: item.productId, quantity: item.quantity })),
      }
    },

    handleCreateOrderResponse(order) {
      localStorage.removeItem('cart')
      window.dispatchEvent(new CustomEvent('cart-updated'))
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
    this.cartItems = JSON.parse(localStorage.getItem('cart') || '[]')
    if (this.cartItems.length === 0) {
      NavigationService.navigateToShop()
    }
  },
}
</script>

<style scoped>
.step-bar {
  background: var(--nb-bg);
  border-bottom: var(--nb-border);
  box-shadow: 0 4px 0 var(--nb-black);
  padding: 1.25rem 0;
}

.step-list {
  display: flex;
  align-items: center;
  justify-content: center;
}

.step {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.step-circle {
  width: 38px;
  height: 38px;
  border: 2.5px solid var(--nb-black);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Archivo Black', sans-serif;
  font-size: 0.95rem;
  flex-shrink: 0;
}

.step-label {
  font-family: 'Space Grotesk', sans-serif;
  font-weight: 700;
  font-size: 1rem;
}

.step--done .step-circle {
  background: var(--nb-black);
  color: var(--nb-yellow);
  border-color: var(--nb-black);
}

.step--done .step-label {
  color: var(--nb-black);
  opacity: 0.5;
}

.step--active .step-circle {
  background: var(--nb-black);
  color: #fff;
  border-color: var(--nb-black);
}

.step--active .step-label {
  color: var(--nb-black);
  font-weight: 800;
}

.step--upcoming .step-circle {
  background: transparent;
  color: #aaa;
  border-color: #ccc;
}

.step--upcoming .step-label {
  color: #aaa;
}

.step-line {
  width: 80px;
  height: 2px;
  background: #ccc;
  margin: 0 1rem;
}

.section-block {
  border: var(--nb-border);
  box-shadow: var(--nb-shadow);
  background: var(--nb-white);
  padding: 1.75rem;
}

.section-title {
  font-family: 'Archivo Black', sans-serif;
  text-transform: uppercase;
  font-size: 1rem;
  border-bottom: var(--nb-border);
  padding-bottom: 0.75rem;
  margin-bottom: 1.25rem;
}

.summary-card {
  border: var(--nb-border);
  box-shadow: var(--nb-shadow-lg);
  background: var(--nb-white);
  padding: 1.75rem;
  position: sticky;
  top: 20px;
}

.summary-items {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 1.25rem;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.summary-item-img {
  width: 56px;
  height: 56px;
  object-fit: contain;
  background: #f0ede0;
  border: 2px solid var(--nb-black);
  flex-shrink: 0;
}

.summary-item-img--empty {
  display: block;
}

.summary-item-info {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.summary-item-name {
  font-weight: 700;
  font-size: 0.9rem;
}

.summary-item-qty {
  font-size: 0.8rem;
  color: #666;
}

.summary-item-price {
  font-family: 'Archivo Black', sans-serif;
  font-size: 0.95rem;
  flex-shrink: 0;
}

.summary-totals {
  border-top: var(--nb-border);
  padding-top: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  font-size: 0.95rem;
  color: #555;
}

.summary-row--total {
  border-top: var(--nb-border);
  padding-top: 0.75rem;
  margin-top: 0.25rem;
  font-family: 'Archivo Black', sans-serif;
  font-size: 1.2rem;
  color: var(--nb-black);
}

.delivery-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 0.9rem 1.1rem;
  background: var(--nb-white);
  border: var(--nb-border);
  box-shadow: 3px 3px 0 var(--nb-black);
  cursor: pointer;
  transition: transform 0.08s ease, box-shadow 0.08s ease;
}

.delivery-option:hover {
  transform: translate(-1px, -1px);
  box-shadow: 5px 5px 0 var(--nb-black);
}

.delivery-option--selected {
  background-color: var(--nb-yellow);
  box-shadow: 5px 5px 0 var(--nb-black);
}

.delivery-option-info {
  display: flex;
  flex-direction: column;
  gap: 0.15rem;
}

.delivery-option-name {
  font-weight: 800;
  font-size: 0.95rem;
  text-transform: uppercase;
  letter-spacing: 0.2px;
}

.delivery-option-desc {
  font-size: 0.8rem;
  color: #555;
  font-weight: 500;
}

.delivery-option--selected .delivery-option-desc {
  color: var(--nb-black);
}

.delivery-option-price {
  font-family: 'Archivo Black', sans-serif;
  font-size: 1rem;
  flex-shrink: 0;
}

.bank-btn {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 120px;
  height: 56px;
  padding: 8px 16px;
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
  background-color: var(--nb-yellow);
  box-shadow: 5px 5px 0 var(--nb-black);
}

.bank-logo {
  height: 26px;
  width: auto;
  display: block;
}

.bank-check {
  position: absolute;
  top: -9px;
  right: -9px;
  width: 22px;
  height: 22px;
  background: var(--nb-black);
  color: var(--nb-yellow);
  border: 2px solid var(--nb-black);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 900;
}
</style>
