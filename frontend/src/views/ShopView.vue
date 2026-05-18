<template>
  <div>
    <AppNavbar />

    <div class="container py-4">
      <h2 class="mb-4">Our Products</h2>

      <AlertError :error-message="errorMessage" />
      <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>

      <div class="row g-4">
        <div
          v-for="product in products"
          :key="product.productId"
          class="col-sm-6 col-md-4"
        >
          <div class="card h-100">
            <img
              v-if="product.imageUrl"
              :src="product.imageUrl"
              :alt="product.name"
              class="card-img-top"
              style="height: 180px; object-fit: contain; background: #f8f9fa;"
            />
            <div v-else class="bg-secondary" style="height: 180px;"></div>
            <div class="card-body d-flex flex-column">
              <h6 class="card-title">{{ product.name }}</h6>
              <p class="card-text text-muted">${{ Number(product.price).toFixed(2) }}</p>
              <div class="d-flex gap-2 mt-auto">
                <button class="btn btn-success btn-sm" @click="addToCart(product.productId, 1)">
                  Add to Cart
                </button>
                <button class="btn btn-outline-primary btn-sm" @click="openDetails(product.productId)">
                  Details
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
          <h5 class="mb-0">Product Details</h5>
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

        <AlertError :error-message="panelErrorMessage" />

        <div class="d-flex align-items-center gap-3 mb-4">
          <span>Quantity:</span>
          <div class="d-flex align-items-center gap-2">
            <button
              class="btn btn-outline-secondary btn-sm"
              :disabled="quantity <= 1"
              @click="decrementQuantity"
            >-</button>
            <span class="px-2">{{ quantity }}</span>
            <button
              class="btn btn-outline-secondary btn-sm"
              :disabled="quantity >= selectedProduct.stockQuantity"
              @click="incrementQuantity"
            >+</button>
          </div>
        </div>

        <button class="btn btn-success w-100" @click="addToCartFromPanel">Add to Cart</button>
      </div>
    </div>
  </div>
</template>

<script>
import AppNavbar from '@/navigation/AppNavbar.vue'
import AlertError from '@/components/common/AlertError.vue'
import ProductService from '@/api-services/ProductService.js'
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'ShopView',
  components: { AppNavbar, AlertError },
  data() {
    return {
      products: [],
      selectedProduct: {
        productId: 0,
        name: '',
        description: '',
        price: 0,
        imageUrl: null,
        stockQuantity: 0,
      },
      isPanelOpen: false,
      quantity: 1,
      errorMessage: '',
      panelErrorMessage: '',
      successMessage: '',
    }
  },
  methods: {
    getProducts() {
      ProductService.sendGetProductsRequest()
        .then((response) => this.handleGetProductsResponse(response.data))
        .catch(() => NavigationService.navigateToErrorView())
        .finally()
    },

    handleGetProductsResponse(products) {
      this.products = products
    },

    openDetails(productId) {
      this.panelErrorMessage = ''
      this.quantity = 1
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

    incrementQuantity() {
      this.quantity++
    },

    decrementQuantity() {
      this.quantity--
    },

    addToCart(productId, quantity) {
      const product = this.products.find((p) => p.productId === productId)
      this.addToCartLocalStorage(product, quantity)
    },

    addToCartFromPanel() {
      this.addToCartLocalStorage(this.selectedProduct, this.quantity)
      this.closePanel()
    },

    addToCartLocalStorage(product, quantity) {
      const cart = JSON.parse(localStorage.getItem('cart') || '[]')
      const existingItem = cart.find((item) => item.productId === product.productId)
      if (existingItem) {
        existingItem.quantity += quantity
        existingItem.lineTotal = Number((Number(existingItem.price) * existingItem.quantity).toFixed(2))
      } else {
        cart.push({
          productId: product.productId,
          name: product.name,
          price: Number(product.price),
          imageUrl: product.imageUrl,
          quantity: quantity,
          lineTotal: Number((Number(product.price) * quantity).toFixed(2)),
        })
      }
      localStorage.setItem('cart', JSON.stringify(cart))
      this.successMessage = 'Toode lisatud ostukorvi!'
      setTimeout(() => (this.successMessage = ''), 2000)
    },
  },
  beforeMount() {
    this.getProducts()
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
</style>