<template>
  <div>
    <AppNavbar />

    <div class="container py-4">
      <h2 class="text-center mb-4">Your Shopping Cart</h2>

      <AlertError :error-message="errorMessage" />

      <div v-if="isEmpty" class="text-center py-5">
        <p class="text-muted fs-5">Ostukorv on tühi</p>
        <button class="btn btn-primary" @click="goToShop">Jätka ostlemist</button>
      </div>

      <div v-else class="row g-4">
        <div class="col-lg-8">
          <table class="table align-middle">
            <thead>
              <tr>
                <th>Product</th>
                <th>Price</th>
                <th>Quantity</th>
                <th>Total</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in cart.items" :key="item.cartItemId">
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
                <span>${{ Number(cart.subtotal).toFixed(2) }}</span>
              </div>
              <div class="d-flex justify-content-between mb-2">
                <span>Shipping:</span>
                <span>${{ Number(cart.shipping).toFixed(2) }}</span>
              </div>
              <div class="d-flex justify-content-between mb-2">
                <span>Tax (8%):</span>
                <span>${{ Number(cart.tax).toFixed(2) }}</span>
              </div>
              <hr />
              <div class="d-flex justify-content-between fw-bold fs-5 mb-3">
                <span>Total:</span>
                <span>${{ Number(cart.total).toFixed(2) }}</span>
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
import AlertError from '@/components/common/AlertError.vue'
import CartService from '@/api-services/CartService.js'
import ProductService from '@/api-services/ProductService.js'
import AuthHelper from '@/auth/auth.js'
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'CartView',
  components: { AppNavbar, AlertError },
  data() {
    return {
      cart: {
        cartId: null,
        items: [],
        subtotal: 0,
        shipping: 0,
        tax: 0,
        total: 0,
      },
      selectedProduct: {
        productId: 0,
        name: '',
        description: '',
        price: 0,
        imageUrl: null,
        stockQuantity: 0,
      },
      isPanelOpen: false,
      errorMessage: '',
    }
  },
  computed: {
    isEmpty() {
      return this.cart.items.length === 0
    },
  },
  methods: {
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

    getCart() {
      const user = AuthHelper.getUser()
      CartService.sendGetCartRequest(user.userId)
        .then((response) => this.handleGetCartResponse(response.data))
        .catch((error) => this.handleCartError(error))
        .finally()
    },

    handleGetCartResponse(cart) {
      this.cart = cart
    },

    incrementQuantity(item) {
      this.updateItemQuantity(item, item.quantity + 1)
    },

    decrementQuantity(item) {
      if (item.quantity === 1) {
        this.removeItem(item)
      } else {
        this.updateItemQuantity(item, item.quantity - 1)
      }
    },

    updateItemQuantity(item, newQuantity) {
      this.errorMessage = ''
      const user = AuthHelper.getUser()
      CartService.sendUpdateCartItemRequest(item.cartItemId, { userId: user.userId, quantity: newQuantity })
        .then((response) => this.handleCartUpdated(response.data))
        .catch((error) => this.handleCartError(error))
        .finally()
    },

    removeItem(item) {
      this.errorMessage = ''
      const user = AuthHelper.getUser()
      CartService.sendDeleteCartItemRequest(item.cartItemId, user.userId)
        .then((response) => this.handleCartUpdated(response.data))
        .catch((error) => this.handleCartError(error))
        .finally()
    },

    handleCartUpdated(cart) {
      this.cart = cart
    },

    handleCartError(error) {
      const statusCode = error.response?.status
      if (statusCode === 400 || statusCode === 403 || statusCode === 404) {
        this.errorMessage = error.response.data.message
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    goToCheckout() {
      NavigationService.navigateToCheckout()
    },

    goToShop() {
      NavigationService.navigateToShop()
    },
  },
  beforeMount() {
    this.getCart()
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