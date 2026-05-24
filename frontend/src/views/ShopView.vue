<template>
  <div>
    <AppNavbar />

    <div class="hero-banner">
      <div class="container">
        <h1 class="hero-title">Valitalgud Merch</h1>
        <p class="hero-sub">Kanna oma kirge — kvaliteetne kraam tõelistele üritussõpradele</p>
      </div>
    </div>

    <div class="container pt-4 pb-5">
      <AlertError :error-message="errorMessage" />

      <div v-if="successMessage" class="alert-success-nb">
        {{ successMessage }}
      </div>

      <div class="d-flex justify-content-end mb-3">
        <select v-model="sortOrder" class="sort-select">
          <option value="default">Vaikejärjestus</option>
          <option value="price-asc">Hind: odavamast kallimale</option>
          <option value="price-desc">Hind: kallimast odavamale</option>
        </select>
      </div>

      <div class="row g-4">
        <div
          v-for="product in sortedProducts"
          :key="product.productId"
          class="col-sm-6 col-md-4"
        >
          <div
            class="card h-100 product-card"
            :class="{ 'in-cart': isInCart(product.productId) }"
          >
            <div class="card-img-wrapper" @click="openDetails(product.productId)">
              <img
                v-if="product.imageUrl"
                :src="product.imageUrl"
                :alt="product.name"
                class="card-img-top product-img"
                style="height: 240px; object-fit: contain; background: #ffffff; padding: 16px;"
              />
              <div
                v-else
                class="d-flex align-items-center justify-content-center"
                style="height: 240px; background: #f0ede0;"
              >
                <span class="text-muted small">Pilt puudub</span>
              </div>

              <span v-if="isInCart(product.productId)" class="badge-in-cart">
                KORVIS ✓
              </span>
            </div>

            <div class="card-body d-flex flex-column">
              <h5 class="card-title">{{ product.name }}</h5>
              <p class="product-price">{{ Number(product.price).toFixed(2) }} €</p>

              <div class="d-flex gap-3 mt-auto align-items-center">
                <button
                  v-if="!isInCart(product.productId)"
                  class="btn btn-outline-secondary btn-sm flex-grow-1"
                  :class="{ 'btn-flash': addedProductId === product.productId }"
                  @click="addToCart(product, 1)"
                >
                  Lisa korvi
                </button>
                <div v-else class="d-flex align-items-center gap-1 flex-grow-1">
                  <button
                    class="btn btn-outline-secondary btn-sm"
                    @click="decrementCartQty(product.productId)"
                  >–</button>
                  <span class="fw-bold px-2">{{ cartQty(product.productId) }}</span>
                  <button
                    class="btn btn-outline-secondary btn-sm"
                    @click="incrementCartQty(product.productId)"
                  >+</button>
                </div>
                <button
                  class="btn btn-outline-secondary btn-sm"
                  @click="openDetails(product.productId)"
                >
                  Detailid
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <button
      v-if="cartCount > 0"
      class="cart-float"
      @click="goToCart"
    >
      🛒 {{ cartCount }} tk &nbsp;·&nbsp; {{ cartTotal }} €
    </button>

    <div v-if="isPanelOpen" class="panel-overlay" @click.self="closePanel">
      <div class="panel-content p-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h5 class="mb-0">Toote detailid</h5>
          <button type="button" class="btn-close" @click="closePanel"></button>
        </div>

        <div class="card-img-wrapper mb-3">
          <img
            v-if="selectedProduct.imageUrl"
            :src="selectedProduct.imageUrl"
            :alt="selectedProduct.name"
            class="img-fluid rounded zoomable-img"
            style="height: 280px; object-fit: contain; width: 100%; background: #ffffff; padding: 16px;"
            title="Vajuta pildi suurendamiseks"
            @click="zoomImage"
          />
          <div
            v-else
            class="d-flex align-items-center justify-content-center"
            style="height: 280px; background: #f0ede0;"
          >
            <span class="text-muted small">Pilt puudub</span>
          </div>
        </div>

        <h4 class="fw-bold mb-2">{{ selectedProduct.name }}</h4>
        <p class="text-muted mb-3">{{ selectedProduct.description }}</p>
        <p class="product-price mb-2">{{ Number(selectedProduct.price).toFixed(2) }} €</p>

        <p
          class="mb-4"
          :class="selectedProduct.stockQuantity <= 5 ? 'stock-low' : 'stock-ok'"
        >
          <span v-if="selectedProduct.stockQuantity <= 5">
            ⚠ Viimased {{ selectedProduct.stockQuantity }} tk laos!
          </span>
          <span v-else>
            Laoseis: {{ selectedProduct.stockQuantity }} tk
          </span>
        </p>

        <div v-if="isInCart(selectedProduct.productId)" class="panel-cart-control mb-4">
          <button
            class="panel-cart-btn"
            @click="decrementCartQty(selectedProduct.productId)"
          >–</button>
          <div class="panel-cart-info">
            <span class="panel-cart-qty">{{ cartQty(selectedProduct.productId) }}</span>
            <span class="panel-cart-label">korvis</span>
          </div>
          <button
            class="panel-cart-btn"
            @click="incrementCartQty(selectedProduct.productId)"
          >+</button>
        </div>

        <div v-else class="d-flex align-items-center gap-3 mb-4">
          <span class="fw-bold text-uppercase" style="font-size: 0.85rem;">Kogus:</span>
          <div class="d-flex align-items-center gap-2">
            <button
              class="btn btn-outline-secondary btn-sm"
              :disabled="quantity <= 1"
              @click="decrementQuantity"
            >–</button>
            <span class="px-3 fw-bold fs-5">{{ quantity }}</span>
            <button
              class="btn btn-outline-secondary btn-sm"
              :disabled="quantity >= selectedProduct.stockQuantity"
              @click="incrementQuantity"
            >+</button>
          </div>
        </div>

        <button
          v-if="!isInCart(selectedProduct.productId)"
          class="btn btn-success w-100"
          @click="addToCartFromPanel"
        >
          Lisa ostukorvi
        </button>
      </div>
    </div>

    <!-- Pildi suurendus (lightbox) — taust, ESC või rist sulgeb; pildil klõps zoomib 2x -->
    <div v-if="isImageZoomed" class="image-lightbox" @click.self="closeZoom">
      <button
        type="button"
        class="lightbox-close"
        aria-label="Sulge"
        @click="closeZoom"
      >✕</button>
      <img
        :src="selectedProduct.imageUrl"
        :alt="selectedProduct.name"
        class="image-lightbox-img"
        :class="{ 'is-magnified': isLightboxMagnified }"
        :title="isLightboxMagnified ? 'Vajuta vähendamiseks' : 'Vajuta detailseks vaateks'"
        @click="toggleMagnify"
      />
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
      cartItems: [],
      selectedProduct: {
        productId: 0,
        name: '',
        description: '',
        price: 0,
        imageUrl: null,
        stockQuantity: 0,
      },
      isPanelOpen: false,
      isImageZoomed: false,
      isLightboxMagnified: false,
      quantity: 1,
      sortOrder: 'default',
      addedProductId: null,
      errorMessage: '',
      successMessage: '',
    }
  },
  computed: {
    cartCount() {
      return this.cartItems.reduce((sum, item) => sum + item.quantity, 0)
    },
    cartTotal() {
      return this.cartItems.reduce((sum, item) => sum + item.lineTotal, 0).toFixed(2)
    },
    sortedProducts() {
      if (this.sortOrder === 'price-asc') {
        return [...this.products].sort((a, b) => Number(a.price) - Number(b.price))
      }
      if (this.sortOrder === 'price-desc') {
        return [...this.products].sort((a, b) => Number(b.price) - Number(a.price))
      }
      return this.products
    },
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
      this.closeZoom()
    },

    zoomImage() {
      if (this.selectedProduct.imageUrl) {
        this.isImageZoomed = true
        this.isLightboxMagnified = false
      }
    },

    closeZoom() {
      this.isImageZoomed = false
      this.isLightboxMagnified = false
    },

    toggleMagnify() {
      this.isLightboxMagnified = !this.isLightboxMagnified
    },

    handleKeydown(event) {
      if (event.key === 'Escape' && this.isImageZoomed) {
        this.closeZoom()
      }
    },

    incrementQuantity() {
      this.quantity++
    },

    decrementQuantity() {
      this.quantity--
    },

    isInCart(productId) {
      return this.cartItems.some((item) => item.productId === productId)
    },

    cartQty(productId) {
      const item = this.cartItems.find((item) => item.productId === productId)
      return item ? item.quantity : 0
    },

    addToCart(product, quantity) {
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
      this.cartItems = cart
      window.dispatchEvent(new CustomEvent('cart-updated'))
      this.addedProductId = product.productId
      setTimeout(() => (this.addedProductId = null), 500)
      this.successMessage = 'Toode lisatud ostukorvi'
      setTimeout(() => (this.successMessage = ''), 2500)
    },

    decrementCartQty(productId) {
      const cart = JSON.parse(localStorage.getItem('cart') || '[]')
      const item = cart.find((i) => i.productId === productId)
      if (!item) return
      if (item.quantity <= 1) {
        const filtered = cart.filter((i) => i.productId !== productId)
        localStorage.setItem('cart', JSON.stringify(filtered))
        this.cartItems = filtered
        window.dispatchEvent(new CustomEvent('cart-updated'))
      } else {
        item.quantity--
        item.lineTotal = Number((item.price * item.quantity).toFixed(2))
        localStorage.setItem('cart', JSON.stringify(cart))
        this.cartItems = cart
        window.dispatchEvent(new CustomEvent('cart-updated'))
      }
    },

    incrementCartQty(productId) {
      const cart = JSON.parse(localStorage.getItem('cart') || '[]')
      const item = cart.find((i) => i.productId === productId)
      if (!item) return
      item.quantity++
      item.lineTotal = Number((item.price * item.quantity).toFixed(2))
      localStorage.setItem('cart', JSON.stringify(cart))
      this.cartItems = cart
      window.dispatchEvent(new CustomEvent('cart-updated'))
    },

    goToCart() {
      NavigationService.navigateToCart()
    },

    loadCart() {
      this.cartItems = JSON.parse(localStorage.getItem('cart') || '[]')
    },
  },
  beforeMount() {
    this.getProducts()
    this.loadCart()
  },
  mounted() {
    document.addEventListener('keydown', this.handleKeydown)
  },
  beforeUnmount() {
    document.removeEventListener('keydown', this.handleKeydown)
  },
}
</script>

<style scoped>
/* Hero */
.hero-banner {
  background-color: var(--corp-surface);
  border-bottom: 1px solid var(--corp-line);
  padding: 2.5rem 0 2rem;
}

.hero-title {
  font-size: 3rem;
  margin-bottom: 0.5rem;
  text-transform: uppercase;
}

.hero-sub {
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0;
  opacity: 0.85;
}

/* Sorteerimine */
.sort-select {
  background-color: var(--nb-white);
  border: 1px solid #d1d5db;
  border-radius: 10px;
  font-family: 'Space Grotesk', sans-serif;
  font-weight: 600;
  font-size: 1rem;
  padding: 0.45rem 0.75rem;
  cursor: pointer;
  appearance: none;
  -webkit-appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8' viewBox='0 0 12 8'%3E%3Cpath d='M1 1l5 5 5-5' stroke='%23000' stroke-width='2' fill='none'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  padding-right: 2.2rem;
}

.sort-select:focus {
  outline: none;
  border-color: var(--corp-blue);
  box-shadow: 0 0 0 3px rgba(30, 58, 138, 0.15);
}

/* Korvi lisamise animatsioon */
@keyframes btn-pop {
  0%   { transform: scale(1); background-color: #198754; }
  40%  { transform: scale(1.08); background-color: var(--nb-green); }
  100% { transform: scale(1); background-color: #198754; }
}

.btn-flash {
  animation: btn-pop 0.45s ease-out;
}

/* Kaardid */
.product-card {
  transition: transform 0.08s ease, box-shadow 0.08s ease;
  cursor: default;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--corp-shadow-lg) !important;
}

.product-card.in-cart {
  border-color: var(--nb-green) !important;
  box-shadow: 0 0 0 2px var(--nb-green) !important;
}

.product-card.in-cart:hover {
  box-shadow: var(--corp-shadow-lg) !important;
}

/* Pildi wrapper */
.card-img-wrapper {
  position: relative;
  cursor: pointer;
}

.product-img {
  display: block;
}

/* "KORVIS ✓" badge pildil */
.badge-in-cart {
  position: absolute;
  top: 10px;
  left: 10px;
  background-color: var(--nb-green);
  color: var(--nb-black);
  font-family: 'Space Grotesk', sans-serif;
  font-weight: 800;
  font-size: 0.7rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  padding: 3px 8px;
  border-radius: 999px;
  box-shadow: var(--corp-shadow);
}

/* Hind */
.product-price {
  font-family: 'Archivo Black', sans-serif;
  font-size: 1.4rem;
  color: var(--nb-black);
  margin-bottom: 0;
}

/* Success teade */
/* Teavitus-toast: ilmub rangelt headeri all, üleval paremas nurgas.
   Navbar on ~125px kõrge + 6px vari, seega top hoiab toasti sellest allpool. */
.alert-success-nb {
  position: fixed;
  top: 140px;
  right: 24px;
  z-index: 1080;
  max-width: 320px;
  background-color: var(--nb-green);
  color: var(--nb-black);
  border: 1px solid var(--corp-line);
  border-radius: 10px;
  box-shadow: var(--corp-shadow-lg);
  padding: 0.85rem 1.25rem;
  font-weight: 700;
  font-size: 0.9rem;
  animation: toast-in 0.25s ease-out;
}

@keyframes toast-in {
  from {
    opacity: 0;
    transform: translateX(16px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* Laoseis */
.stock-low {
  color: var(--nb-pink);
  font-weight: 800;
  text-transform: uppercase;
  font-size: 0.85rem;
}

.stock-ok {
  color: #666;
  font-size: 0.9rem;
}

.panel-cart-control {
  display: flex;
  align-items: stretch;
  border: 1px solid var(--corp-line);
  border-radius: var(--corp-radius);
  box-shadow: var(--corp-shadow);
}

.panel-cart-btn {
  background-color: var(--corp-surface);
  color: var(--corp-blue);
  border: none;
  border-right: 1px solid var(--corp-line);
  font-family: 'Archivo Black', sans-serif;
  font-size: 1.4rem;
  font-weight: 900;
  width: 56px;
  cursor: pointer;
  transition: transform 0.06s ease, box-shadow 0.06s ease;
  flex-shrink: 0;
}

.panel-cart-btn:last-child {
  border-right: none;
  border-left: 1px solid var(--corp-line);
}

.panel-cart-btn:hover {
  background-color: var(--corp-blue);
  color: #fff;
}

.panel-cart-btn:active {
  background-color: var(--corp-blue);
  color: #fff;
}

.panel-cart-info {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: var(--corp-blue-soft);
  color: var(--corp-blue);
  padding: 0.6rem 0;
}

.panel-cart-qty {
  font-family: 'Archivo Black', sans-serif;
  font-size: 1.8rem;
  line-height: 1;
}

.panel-cart-label {
  font-size: 0.7rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  opacity: 0.75;
}

/* Ujuv ostukorv */
.cart-float {
  position: fixed;
  bottom: 28px;
  /* Chatboti nupp on paremas servas (24px + 60px lai), seega hoia banner sellest vasakul, et need ei kattuks */
  right: 96px;
  background-color: var(--corp-blue);
  color: #fff;
  font-family: 'Space Grotesk', sans-serif;
  font-weight: 800;
  font-size: 0.95rem;
  letter-spacing: 0.3px;
  padding: 0.75rem 1.4rem;
  border: none;
  border-radius: 999px;
  box-shadow: 0 10px 25px rgba(30, 58, 138, 0.35);
  cursor: pointer;
  z-index: 999;
  transition: transform 0.06s ease, box-shadow 0.06s ease;
}

.cart-float:hover {
  transform: translateY(-3px);
  box-shadow: 0 14px 32px rgba(30, 58, 138, 0.4);
}

.cart-float:active {
  transform: translateY(0);
  box-shadow: 0 8px 20px rgba(30, 58, 138, 0.3);
}

/* Paneel */
.panel-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1050;
  display: flex;
  justify-content: flex-end;
}

/* Klikitav pilt detailide paneelis — käekursor vihjab, et pilt on klikitav */
.zoomable-img {
  cursor: pointer;
}

/* Pildi suurendus (lightbox) üle terve ekraani.
   overflow: hidden hoiab 2x suurendatud pildi vaateakna piires (kärbib keskele). */
.image-lightbox {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  z-index: 1060;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  overflow: hidden;
  cursor: pointer;
}

.image-lightbox-img {
  max-width: 90vw;
  max-height: 90vh;
  object-fit: contain;
  background: #ffffff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
  cursor: pointer;
  transform-origin: center;
  transition: transform 0.2s ease;
}

/* Lightbox'i lisazoom — klõps suurendab pildi 2x, et detaili lähemalt vaadata */
.image-lightbox-img.is-magnified {
  transform: scale(2);
}

/* Lightbox'i sulgemisrist üleval paremal */
.lightbox-close {
  position: fixed;
  top: 20px;
  right: 24px;
  /* Üle 2x suurendatud pildi, et rist jääks alati klikitavaks */
  z-index: 2;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  color: var(--nb-black);
  font-size: 1.4rem;
  line-height: 1;
  cursor: pointer;
  box-shadow: var(--corp-shadow);
  transition: background-color 0.08s ease, transform 0.08s ease;
}

.lightbox-close:hover {
  background: #fff;
  transform: scale(1.05);
}

.panel-content {
  background: var(--nb-white);
  border-left: 1px solid var(--corp-line);
  width: 440px;
  height: 100%;
  overflow-y: auto;
}
</style>
