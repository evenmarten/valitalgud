<template>
  <section class="section">
    <div class="container">
      <div class="row g-4">
        <div class="col-md-6">
          <div class="card lift-card h-100 audience-card audience-blue">
            <div class="card-body">
              <span class="audience-tag">Osalejatele</span>
              <h3 class="audience-heading">Leia oma järgmine sündmus</h3>
              <ul class="feature-list">
                <li v-for="feature in participantFeatures" :key="feature">{{ feature }}</li>
              </ul>
              <button v-if="!isLoggedIn" class="btn btn-primary mt-2" @click="goToRegister">
                Liitu kogukonnaga
              </button>
              <button v-else class="btn btn-primary mt-2" @click="browseEvents">
                Sirvi sündmusi
              </button>
            </div>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card lift-card h-100 audience-card audience-pink">
            <div class="card-body">
              <span class="audience-tag">Korraldajatele</span>
              <h3 class="audience-heading">Korralda oma üritus</h3>
              <ul class="feature-list">
                <li v-for="feature in organizerFeatures" :key="feature">{{ feature }}</li>
              </ul>
              <button v-if="!isLoggedIn" class="btn btn-danger mt-2" @click="goToRegister">
                Alusta korraldamist
              </button>
              <button v-else class="btn btn-danger mt-2" @click="goToCreateEvent">
                Loo sündmus
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'LandingAudience',
  props: {
    isLoggedIn: {
      type: Boolean,
      default: false,
    },
    participantFeatures: {
      type: Array,
      default: () => [],
    },
    organizerFeatures: {
      type: Array,
      default: () => [],
    },
  },
  methods: {
    goToRegister() {
      NavigationService.navigateToRegister()
    },
    goToCreateEvent() {
      NavigationService.navigateToCreateEvent()
    },
    browseEvents() {
      if (this.isLoggedIn) {
        NavigationService.navigateToEvents()
      } else {
        NavigationService.navigateToRegister()
      }
    },
  },
}
</script>

<style scoped>
.section .audience-card.audience-blue {
  border-top: 4px solid var(--corp-blue) !important;
}

.section .audience-card.audience-pink {
  border-top: 4px solid var(--corp-teal) !important;
}

.audience-card .card-body {
  padding: 2rem;
}

.audience-tag {
  display: inline-block;
  background: var(--corp-blue-soft);
  color: var(--corp-blue);
  border-radius: 999px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  font-size: 0.74rem;
  padding: 0.3rem 0.85rem;
  margin-bottom: 0.85rem;
}

.audience-heading {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: var(--corp-ink);
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0 0 1.5rem;
}

.feature-list li {
  position: relative;
  padding-left: 1.7rem;
  margin-bottom: 0.65rem;
  font-weight: 500;
  color: var(--corp-ink);
}

.feature-list li::before {
  content: '✓';
  position: absolute;
  left: 0;
  font-weight: 900;
  color: var(--corp-blue);
}
</style>
