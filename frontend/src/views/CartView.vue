<template>
  <div>
    <AppNavbar />

    <div class="container py-4">
      <h2 class="text-center mb-4">Your Shopping Cart</h2>

      <div v-if="isEmpty" class="text-center py-5">
        <p class="text-muted fs-5">Ostukorv on tühi</p>
        <button class="btn btn-primary" @click="goToShop">Jätka ostlemist</button>
      </div>

      <div v-else class="row g-4">
        <div class="col-lg-8">
          <table class="table align-middle">
            <thead>
              <tr>
                <th>Toode</th>
                <th>Hind</th>
                <th>Kogus</th>
                <th>Kokku</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in items" :key="item.productId">
                <td>
                  <div
                    class="d-flex align-items-center gap-3 product-link"
                    @click="openDetails(item.productId)"
                  >
                    <img
                      v-if="item.imageUrl"
                      :src="item.imageUrl"
                      :alt="item.name"
                      style="width: 60px; height: 60px; object-fit: contain; background: #f8f9fa;"
                    />
                    <div v-else style="width: 60px; height: 60px; background: #f8f9fa;"></div>
                    <span class="text-primary text-decoration-underline">{{ item.name }}</span>
                  </div>
                </td>
                <td>${{ Number(item.price).toFixed(2) }}</td>
                <td>
                  <div class="d-flex align-items-center gap-2">
                    <button class="btn btn-outline-secondary btn-sm" @click="decrementQuantity(item)">-</button>
                    <span>{{ item.quantity }}</span>
                    <button class="btn btn-outline-secondary btn-sm" @click="incrementQuantity(item)">+</button>
                  </div>
                </td>
                <td>
                  <div class="d-flex align-items-center gap-2">
                    <span>${{ Number(item.lineTotal).toFixed(2) }}</span>
                    <button class="btn btn-sm text-danger p-0" @click="removeItem(item)">✕</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="col-lg-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title mb-3">Order Summary</h5>
              <div class="d-flex justify-content-between mb-2">
                <span>Subtotal:</span>
                <span>${{ subtotal.toFixed(2) }}</span>
              </div>
              <div class="d-flex justify-content-between mb-2">
                <span>Shipping:</span>
                <span>${{ shipping.toFixed(2) }}</span>
              </div>
              <div class="d-flex justify-content-between mb-2">
                <span>Tax (8%):</span>
                <span>${{ tax.toFixed(2) }}</span>
              </div>
              <hr />
              <div class="d-flex justify-content-between fw-bold fs-5 mb-3">
                <span>Total:</span>
                <span>${{ total.toFixed(2) }}</span>
              </div>
              <button class="btn btn-primary w-100" @click="goToCheckout">
                Proceed to Checkout
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div v-if="isPanelOpen" class="panel-overlay" @click.self="closePanel">
    <div class="panel-content p-4">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="mb-0">Toote detailid</h5>
        <button type="button" class="btn-close" @click="closePanel"></button>
      </div>

      <img
        v-if="selectedProduct.imageUrl"
        :src="selectedProduct.imageUrl"
        :alt="selectedProduct.name"
        class="img-fluid rounded mb-3"
        style="height: 200px; object-fit: contain; width: 100%; background: #f8f9fa;"
      />
      <div v-else class="bg-secondary rounded mb-3" style="height: 200px;"></div>

      <h5>{{ selectedProduct.name }}</h5>
      <p class="text-muted">{{ selectedProduct.description }}</p>
      <p class="fs-5 fw-bold">${{ Number(selectedProduct.price).toFixed(2) }}</p>
      <p class="text-muted small">Laoseis: {{ selectedProduct.stockQuantity }}</p>
    </div>
  </div>
</template>

<script>
import AppNavbar from '@/navigation/AppNavbar.vue'
import ProductService from '@/api-services/ProductService.js'
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'CartView',
  components: { AppNavbar },
  data() {
    return {
      items: [],
      selectedProduct: {
        productId: 0,
        name: '',
        description: '',
        price: 0,
        imageUrl: null,
        stockQuantity: 0,
      },
      isPanelOpen: false,
    }
  },
  computed: {
    isEmpty() {
      return this.items.length === 0
    },
    subtotal() {
      return this.items.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0)
    },
    shipping() {
      return this.isEmpty ? 0 : 5.0
    },
    tax() {
      return Math.round(this.subtotal * 0.08 * 100) / 100
    },
    total() {
      return this.subtotal + this.shipping + this.tax
    },
  },
  methods: {
    loadCart() {
      this.items = JSON.parse(localStorage.getItem('cart') || '[]')
    },

    saveCart() {
      localStorage.setItem('cart', JSON.stringify(this.items))
    },

    incrementQuantity(item) {
      item.quantity++
      item.lineTotal = Number((Number(item.price) * item.quantity).toFixed(2))
      this.saveCart()
    },

    decrementQuantity(item) {
      if (item.quantity === 1) {
        this.removeItem(item)
      } else {
        item.quantity--
        item.lineTotal = Number((Number(item.price) * item.quantity).toFixed(2))
        this.saveCart()
      }
    },

    removeItem(item) {
      this.items = this.items.filter((i) => i.productId !== item.productId)
      this.saveCart()
    },

    openDetails(productId) {
      ProductService.sendGetProductDetailsRequest(productId)
        .then((response) => this.handleGetProductDetailsResponse(response.data))
        .catch(() => NavigationService.navigateToErrorView())
        .finally()
    },

    handleGetProductDetailsResponse(product) {
      this.selectedProduct = product
      this.isPanelOpen = true
    },

    closePanel() {
      this.isPanelOpen = false
    },

    goToCheckout() {
      NavigationService.navigateToCheckout()
    },

    goToShop() {
      NavigationService.navigateToShop()
    },
  },
  beforeMount() {
    this.loadCart()
  },
}
</script>

<style scoped>
.panel-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 1050;
  display: flex;
  justify-content: flex-end;
}

.panel-content {
  background: white;
  width: 380px;
  height: 100%;
  overflow-y: auto;
}

.product-link {
  cursor: pointer;
}
</style>