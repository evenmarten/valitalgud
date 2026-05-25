<template>
  <section class="section section-alt">
    <div class="container">
      <div class="text-center mb-5">
        <h2 class="section-title">Tutvu meie e-poega</h2>
      </div>

      <div class="row g-4">
        <div
          v-for="product in products"
          :key="product.productId"
          class="col-sm-6 col-md-4"
        >
          <div class="card h-100 shop-card" @click="goToShop">
            <img
              :src="product.image"
              :alt="product.name"
              class="card-img-top demo-product-img"
            />
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'LandingShop',
  props: {
    products: {
      type: Array,
      default: () => [],
    },
  },
  methods: {
    goToShop() {
      NavigationService.navigateToShop()
    },
  },
}
</script>

<style scoped>
/* Toote pilt landingul — täissuuruses nagu e-poe lehel (ei kärbita) */
.demo-product-img {
  height: 240px;
  object-fit: contain;
  background: #ffffff;
  padding: 16px;
}

.shop-card {
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.shop-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--corp-shadow-lg) !important;
}
</style>
