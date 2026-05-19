<template>
  <div>
    <AppNavbar />

    <div class="container py-5">
      <div class="text-center mb-5">
        <h1 class="fw-bold mb-3">Welcome to the Event Management App!</h1>
        <p class="text-muted fs-5 mx-auto" style="max-width: 720px;">
          Your ultimate solution for organizing and discovering events. Connect with people,
          explore new opportunities, and manage your schedule with ease.
        </p>
      </div>

      <h3 class="mb-4">Tutvu eelseisvate sündmustega</h3>
      <div class="row g-4">
        <div
          v-for="event in demoEvents"
          :key="event.eventId"
          class="col-sm-6 col-md-4"
        >
          <div class="card h-100">
            <div class="card-img-top demo-banner" :style="bannerStyle(event)">
              <span class="demo-banner-label">{{ event.bannerLabel }}</span>
            </div>

            <div class="card-body d-flex flex-column">
              <h5 class="card-title">{{ event.title }}</h5>
              <p class="text-muted small mb-2">
                {{ event.eventDate }} · {{ event.city }}
              </p>
              <p class="card-text text-truncate-3">{{ event.description }}</p>

              <div class="mb-2">
                <span
                  v-for="tag in event.skillTags"
                  :key="tag"
                  class="badge bg-info text-dark me-1"
                >{{ tag }}</span>
              </div>

              <p class="text-muted small mb-0 mt-auto">
                Osalejaid: {{ event.currentParticipants }} / {{ event.maxParticipants }}
              </p>
            </div>
          </div>
        </div>
      </div>

      <h3 class="mt-5 mb-4">Tutvu meie e-poega</h3>
      <div class="row g-4">
        <div
          v-for="product in demoProducts"
          :key="product.productId"
          class="col-sm-6 col-md-4"
        >
          <div class="card h-100 shop-card" @click="goToShop">
            <div class="card-img-top demo-banner" :style="bannerStyle(product)">
              <span class="demo-banner-label">{{ product.bannerLabel }}</span>
            </div>

            <div class="card-body d-flex flex-column">
              <h5 class="card-title">{{ product.name }}</h5>
              <p class="text-muted small mb-2">{{ product.category }}</p>
              <p class="card-text text-truncate-3">{{ product.description }}</p>

              <p class="fw-bold fs-5 mb-0 mt-auto">{{ product.price }} €</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AppNavbar from '@/navigation/AppNavbar.vue'

export default {
  name: 'LandingPage',
  components: { AppNavbar },
  data() {
    return {
      demoEvents: [
        {
          eventId: 'demo-1',
          title: 'Suur Tehnoloogiakonverents',
          description: 'Aastane konverents, mis toob kokku tehnoloogiamaailma tipud, idufirmade asutajad ja arendajad. Loengud, töötoad ja võrgustumine.',
          eventDate: '26.10.2026',
          city: 'Tallinn',
          bannerLabel: 'TECH',
          bannerColor: 'linear-gradient(135deg, #4f46e5 0%, #06b6d4 100%)',
          skillTags: ['IT', 'JavaScript'],
          currentParticipants: 87,
          maxParticipants: 100,
        },
        {
          eventId: 'demo-2',
          title: 'Pärnu Jazz Festival',
          description: 'Kolmepäevane jazzmuusika festival kuulsate artistidega rannapargis. Live esinemised, toidualad ja meeleolukad õhtud.',
          eventDate: '15.07.2026',
          city: 'Pärnu',
          bannerLabel: 'JAZZ',
          bannerColor: 'linear-gradient(135deg, #f59e0b 0%, #ef4444 100%)',
          skillTags: ['Muusika'],
          currentParticipants: 142,
          maxParticipants: 200,
        },
        {
          eventId: 'demo-3',
          title: 'Tartu Maraton',
          description: 'Traditsiooniline maraton nii profidele kui harrastajatele. Erinevad distantsid, soe vastuvõtt ja ilus rada läbi linna.',
          eventDate: '03.09.2026',
          city: 'Tartu',
          bannerLabel: 'SPORT',
          bannerColor: 'linear-gradient(135deg, #10b981 0%, #3b82f6 100%)',
          skillTags: ['Sport'],
          currentParticipants: 318,
          maxParticipants: 500,
        },
      ],
      demoProducts: [
        {
          productId: 'demo-product-1',
          name: 'Ürituse T-särk',
          description: 'Pehme puuvillane T-särk valitalgud logoga. Sobib nii üritusele kui igapäevaseks kandmiseks.',
          category: 'Riided',
          price: '19.90',
          bannerLabel: 'SHIRT',
          bannerColor: 'linear-gradient(135deg, #ec4899 0%, #8b5cf6 100%)',
        },
        {
          productId: 'demo-product-2',
          name: 'Termokruus',
          description: 'Hoia oma jook soe terve ürituse vältel. Vastupidav terasest termokruus graveeritud logoga.',
          category: 'Aksessuaarid',
          price: '14.50',
          bannerLabel: 'MUG',
          bannerColor: 'linear-gradient(135deg, #0ea5e9 0%, #6366f1 100%)',
        },
        {
          productId: 'demo-product-3',
          name: 'Märkmik',
          description: 'A5 formaadis kõvakaaneline märkmik. Ideaalne ideede, töötubade ja koosolekute märkmete jaoks.',
          category: 'Kontoritarbed',
          price: '9.90',
          bannerLabel: 'NOTE',
          bannerColor: 'linear-gradient(135deg, #f97316 0%, #eab308 100%)',
        },
      ],
    }
  },
  methods: {
    bannerStyle(item) {
      return {
        background: item.bannerColor,
      }
    },
    goToShop() {
      this.$router.push('/shop')
    },
  },
}
</script>

<style scoped>
.demo-banner {
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.demo-banner-label {
  color: white;
  font-weight: 700;
  font-size: 1.5rem;
  letter-spacing: 0.2em;
}

.text-truncate-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.shop-card {
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.shop-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}
</style>
