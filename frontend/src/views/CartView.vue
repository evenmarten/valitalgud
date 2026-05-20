<template>
  <div>
    <AppNavbar />

    <div class="container py-4">
      <h2 class="text-center mb-4">Minu ostukorv</h2>

      <div v-if="isEmpty" class="text-center py-5">
        <p class="text-muted fs-5">Ostukorv on tühi</p>
        <button class="btn btn-primary" @click="goToShop">Jätka ostlemist</button>
      </div>

      <div v-else class="row g-4">
        <div class="col-lg-8">
          <table class="table table-hover align-middle fs-5">
            <thead class="table-light">
              <tr>
                <th class="py-3">Toode</th>
                <th class="py-3">Hind</th>
                <th class="py-3">Kogus</th>
                <th class="py-3">Kokku</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in items" :key="item.productId">
                <td class="py-3">
                  <div
                    class="d-flex align-items-center gap-4 product-link"
                    @click="openDetails(item.productId)"
                  >
                    <img
                      v-if="item.imageUrl"
                      :src="item.imageUrl"
                      :alt="item.name"
                      style="width: 100px; height: 100px; object-fit: contain; background: #f8f9fa;"
                    />
                    <div v-else style="width: 100px; height: 100px; background: #f8f9fa;"></div>
                    <span class="text-primary text-decoration-underline fs-5">{{ item.name }}</span>
                  </div>
                </td>
                <td class="py-3">{{ Number(item.price).toFixed(2) }} €</td>
                <td class="py-3">
                  <div class="d-flex align-items-center gap-2">
                    <button class="btn btn-outline-secondary" @click="decrementQuantity(item)">-</button>
                    <span class="px-2">{{ item.quantity }}</span>
                    <button class="btn btn-outline-secondary" @click="incrementQuantity(item)">+</button>
                  </div>
                </td>
                <td class="py-3">
                  <div class="d-flex align-items-center gap-3">
                    <span class="fw-semibold">{{ Number(item.lineTotal).toFixed(2) }} €</span>
                    <button class="btn text-danger p-0" @click="removeItem(item)">✕</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="col-lg-4">
          <div class="card shadow-sm">
            <div class="card-body p-4">
              <h4 class="fw-bold mb-4">Tellimuse kokkuvõte</h4>
              <div class="d-flex justify-content-between mb-3 fs-5">
                <span class="text-muted">Vahesumma:</span>
                <span>{{ subtotal.toFixed(2) }} €</span>
              </div>
              <div class="d-flex justify-content-between mb-3 fs-5">
                <span class="text-muted">Transport:</span>
                <span>{{ shipping.toFixed(2) }} €</span>
              </div>
              <div class="d-flex justify-content-between mb-3 fs-5">
                <span class="text-muted">Käibemaks (8%):</span>
                <span>{{ tax.toFixed(2) }} €</span>
              </div>
              <hr />
              <div class="d-flex justify-content-between fw-bold fs-4 mb-4">
                <span>Kokku:</span>
                <span class="text-success">{{ total.toFixed(2) }} €</span>
              </div>
              <button class="btn btn-primary w-100 fs-5" @click="goToCheckout">
                Edasi kassasse
              </button>
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
      this.recalculateLineTotal(item)
      this.saveCart()
    },

    decrementQuantity(item) {
      if (item.quantity === 1) {
        this.removeItem(item)
      } else {
        item.quantity--
        this.recalculateLineTotal(item)
        this.saveCart()
      }
    },

    recalculateLineTotal(item) {
      item.lineTotal = Number((Number(item.price) * item.quantity).toFixed(2))
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