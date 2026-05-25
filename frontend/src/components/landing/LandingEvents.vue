<template>
  <section class="section section-yellow">
    <div class="container">
      <div class="text-center mb-5">
        <h2 class="section-title">Tutvu eelseisvate sündmustega</h2>
        <p class="section-subtitle">Näide sellest, mis kogukonnas toimub</p>
      </div>

      <div class="row g-4">
        <div
          v-for="event in events"
          :key="event.eventId"
          class="col-sm-6 col-md-4"
        >
          <div class="card lift-card h-100 event-card" @click="goToEvents">
            <div class="card-img-top demo-banner" :style="bannerStyle(event)">
              <img :src="event.image" :alt="event.title" class="demo-banner-img" />
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
    </div>
  </section>
</template>

<script>
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'LandingEvents',
  props: {
    events: {
      type: Array,
      default: () => [],
    },
  },
  methods: {
    bannerStyle(item) {
      return {
        background: item.bannerColor,
      }
    },
    goToEvents() {
      NavigationService.navigateToEvents()
    },
  },
}
</script>

<style scoped>
.demo-banner {
  height: 180px;
  overflow: hidden;
}

.demo-banner-img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.text-truncate-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Klikitav sündmusekaart landingul — viib events vaatesse */
.event-card {
  cursor: pointer;
}
</style>
