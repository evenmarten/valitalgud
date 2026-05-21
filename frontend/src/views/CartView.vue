<template>
  <div>
    <AppNavbar />

    <div class="hero-banner">
      <div class="container">
        <div class="hero-left">
          <p class="hero-label">Sinu valikud</p>
          <h1 class="hero-title">Ostukorv</h1>
        </div>
        <div class="hero-right" v-if="!isEmpty">
          <span class="hero-count">{{ totalItems }}</span>
          <span class="hero-count-label">toodet</span>
          <span class="hero-divider">·</span>
          <span class="hero-total">{{ subtotal.toFixed(2) }} €</span>
        </div>
      </div>
    </div>

    <div class="container py-4">
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
                    <span class="fw-bold">{{ item.name }}</span>
                  </div>
                </td>
                <td class="py-3">{{ Number(item.price).toFixed(2) }} €</td>
                <td class="py-3">
                  <div class="d-flex align-items-center gap-2">
                    <button class="btn btn-secondary btn-sm" @click="decrementQuantity(item)">–</button>
                    <input
                      type="number"
                      class="qty-input"
                      :value="item.quantity"
                      min="1"
                      @change="updateQuantity(item, $event.target.value)"
                    />
                    <button class="btn btn-secondary btn-sm" @click="incrementQuantity(item)">+</button>
                  </div>
                </td>
                <td class="py-3">
                  <div class="d-flex align-items-center gap-3">
                    <span class="fw-semibold">{{ Number(item.lineTotal).toFixed(2) }} €</span>
                    <button class="btn btn-outline-danger btn-sm" @click="removeItem(item)">✕</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>

          <div class="d-flex justify-content-start mt-1">
            <button class="btn-clear-cart" @click="clearCart">Tühjenda korv</button>
          </div>
        </div>

        <div class="col-lg-4">
          <div class="card">
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
                <span>{{ total.toFixed(2) }} €</span>
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

        <div class="card-img-wrapper mb-3">
          <img
            v-if="selectedProduct.imageUrl"
            :src="selectedProduct.imageUrl"
            :alt="selectedProduct.name"
            class="img-fluid"
            style="height: 280px; object-fit: contain; width: 100%; background: #ffffff; padding: 16px;"
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

        <div v-if="panelItem" class="panel-cart-control">
          <button class="panel-cart-btn" @click="decrementQuantity(panelItem)">–</button>
          <div class="panel-cart-info">
            <span class="panel-cart-qty">{{ panelItem.quantity }}</span>
            <span class="panel-cart-label">korvis</span>
          </div>
          <button class="panel-cart-btn" @click="incrementQuantity(panelItem)">+</button>
        </div>
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
    totalItems() {
      return this.items.reduce((sum, item) => sum + item.quantity, 0)
    },
    panelItem() {
      return this.items.find((i) => i.productId === this.selectedProduct.productId) || null
    },
  },
  methods: {
    loadCart() {
      this.items = JSON.parse(localStorage.getItem('cart') || '[]')
    },

    saveCart() {
      localStorage.setItem('cart', JSON.stringify(this.items))
      window.dispatchEvent(new CustomEvent('cart-updated'))
    },

    incrementQuantity(item) {
      item.quantity++
      this.recalculateLineTotal(item)
      this.saveCart()
    },

    decrementQuantity(item) {
      if (item.quantity === 1) {
        this.removeItem(item)
        this.closePanel()
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

    clearCart() {
      this.items = []
      this.saveCart()
    },

    updateQuantity(item, value) {
      const qty = Math.max(1, parseInt(value, 10) || 1)
      item.quantity = qty
      this.recalculateLineTotal(item)
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
.hero-banner {
  background-color: var(--nb-black);
  border-bottom: 6px solid var(--nb-yellow);
  padding: 2rem 0;
}

.hero-banner .container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 2rem;
}

.hero-left {
  display: flex;
  flex-direction: column;
}

.hero-label {
  font-size: 0.8rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--nb-yellow);
  opacity: 0.7;
  margin: 0 0 0.2rem;
}

.hero-title {
  font-size: 3rem;
  color: var(--nb-yellow);
  margin: 0;
  line-height: 1;
}

.hero-right {
  display: flex;
  align-items: baseline;
  gap: 0.6rem;
}

.hero-count {
  font-family: 'Archivo Black', sans-serif;
  font-size: 3.5rem;
  color: var(--nb-white);
  line-height: 1;
}

.hero-count-label {
  font-size: 1rem;
  font-weight: 700;
  color: var(--nb-white);
  opacity: 0.6;
  text-transform: uppercase;
}

.hero-divider {
  font-size: 2rem;
  color: var(--nb-yellow);
  opacity: 0.4;
}

.hero-total {
  font-family: 'Archivo Black', sans-serif;
  font-size: 2.2rem;
  color: var(--nb-yellow);
  line-height: 1;
}


.product-link {
  cursor: pointer;
}

.qty-input {
  width: 60px;
  text-align: center;
  border: var(--nb-border);
  box-shadow: 2px 2px 0 var(--nb-black);
  font-family: 'Space Grotesk', sans-serif;
  font-weight: 700;
  font-size: 0.95rem;
  padding: 0.25rem 0.4rem;
  background-color: var(--nb-bg);
  -moz-appearance: textfield;
}

.qty-input::-webkit-outer-spin-button,
.qty-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
}

.qty-input:focus {
  outline: none;
  box-shadow: 4px 4px 0 var(--nb-black);
}

.btn-clear-cart {
  background: none;
  border: var(--nb-border);
  box-shadow: 2px 2px 0 var(--nb-black);
  font-family: 'Space Grotesk', sans-serif;
  font-weight: 700;
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.3px;
  padding: 0.4rem 0.9rem;
  cursor: pointer;
  color: var(--nb-pink);
  transition: background-color 0.08s ease, color 0.08s ease;
}

.btn-clear-cart:hover {
  background-color: var(--nb-pink);
  color: var(--nb-black);
}

.product-price {
  font-family: 'Archivo Black', sans-serif;
  font-size: 1.4rem;
  color: var(--nb-black);
}

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

.panel-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1050;
  display: flex;
  justify-content: flex-end;
}

.panel-content {
  background: var(--nb-bg);
  border-left: var(--nb-border);
  width: 440px;
  height: 100%;
  overflow-y: auto;
}

.panel-cart-control {
  display: flex;
  align-items: stretch;
  border: var(--nb-border);
  box-shadow: var(--nb-shadow);
}

.panel-cart-btn {
  background-color: var(--nb-yellow);
  border: none;
  border-right: var(--nb-border);
  font-family: 'Archivo Black', sans-serif;
  font-size: 1.4rem;
  font-weight: 900;
  width: 56px;
  cursor: pointer;
  flex-shrink: 0;
}

.panel-cart-btn:last-child {
  border-right: none;
  border-left: var(--nb-border);
}

.panel-cart-btn:hover {
  background-color: var(--nb-black);
  color: var(--nb-yellow);
}

.panel-cart-info {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: var(--nb-green);
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
</style>
