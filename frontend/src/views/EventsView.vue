<template>
  <div>
    <AppNavbar />

    <div class="container py-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0">Events</h2>
        <button class="btn btn-primary" @click="goToCreateEvent">Loo uus sündmus</button>
      </div>

      <AlertError :error-message="errorMessage" />

      <div class="card mb-4">
        <div class="card-body">
          <div class="row g-3">
            <div class="col-md-4">
              <label for="cityFilter" class="form-label">Linn</label>
              <select id="cityFilter" v-model="filter.cityId" class="form-select" @change="getEvents">
                <option :value="null">-- Kõik linnad --</option>
                <option v-for="city in cityOptions" :key="city.id" :value="city.id">
                  {{ city.name }}
                </option>
              </select>
            </div>

            <div class="col-md-4">
              <label for="tagFilter" class="form-label">Oskuse-tag</label>
              <select id="tagFilter" v-model="filter.skillTagId" class="form-select" @change="getEvents">
                <option :value="null">-- Kõik tagid --</option>
                <option v-for="tag in skillTagOptions" :key="tag.id" :value="tag.id">
                  {{ tag.name }}
                </option>
              </select>
            </div>

            <div class="col-md-4">
              <label for="fromDateFilter" class="form-label">Alates kuupäevast</label>
              <input
                id="fromDateFilter"
                v-model="filter.fromDate"
                type="date"
                class="form-control"
                @change="getEvents"
              />
            </div>
          </div>
        </div>
      </div>

      <div v-if="events.length === 0" class="text-center py-5">
        <p class="text-muted fs-5">Sündmusi ei leitud</p>
      </div>

      <div v-else class="row g-4">
        <div
          v-for="event in events"
          :key="event.eventId"
          class="col-sm-6 col-md-4"
        >
          <div class="card h-100">
            <img
              v-if="event.bannerImageUrl"
              :src="event.bannerImageUrl"
              :alt="event.title"
              class="card-img-top"
              style="height: 180px; object-fit: cover; background: #f8f9fa;"
            />
            <div v-else class="bg-secondary" style="height: 180px;"></div>

            <div class="card-body d-flex flex-column">
              <h5 class="card-title">{{ event.title }}</h5>
              <p class="text-muted small mb-2">
                {{ formatDate(event.eventDate) }} · {{ event.city }}
              </p>
              <p class="card-text text-truncate-3">{{ event.description }}</p>

              <div v-if="event.skillTags.length" class="mb-3">
                <span
                  v-for="tag in event.skillTags"
                  :key="tag"
                  class="badge bg-info text-dark me-1"
                >{{ tag }}</span>
              </div>

              <p class="text-muted small mb-3">
                Osalejaid: {{ event.currentParticipants }}{{ event.maxParticipants ? ` / ${event.maxParticipants}` : '' }}
              </p>

              <button class="btn btn-outline-primary mt-auto" @click="goToEventDetails(event.eventId)">
                View Details
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AppNavbar from '@/navigation/AppNavbar.vue'
import AlertError from '@/components/common/AlertError.vue'
import EventService from '@/api-services/EventService.js'
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'EventsView',
  components: { AppNavbar, AlertError },
  data() {
    return {
      events: [],
      cityOptions: [],
      skillTagOptions: [],
      filter: {
        cityId: null,
        skillTagId: null,
        fromDate: '',
      },
      errorMessage: '',
    }
  },
  methods: {
    getEvents() {
      EventService.sendGetEventsRequest(this.buildQueryParams())
        .then((response) => this.handleGetEventsResponse(response.data))
        .catch((error) => this.handleGetEventsError(error))
        .finally()
    },

    handleGetEventsResponse(events) {
      this.events = events
      this.populateFilterOptions(events)
    },

    handleGetEventsError(error) {
      const statusCode = error.response?.status
      if (statusCode === 400) {
        this.errorMessage = 'Filtri parameeter on vales formaadis'
      } else if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    buildQueryParams() {
      const params = {}
      if (this.filter.cityId) params.cityId = this.filter.cityId
      if (this.filter.skillTagId) params.skillTagId = this.filter.skillTagId
      if (this.filter.fromDate) params.fromDate = this.filter.fromDate
      return params
    },

    populateFilterOptions(events) {
      if (this.cityOptions.length === 0) {
        this.cityOptions = this.collectUniqueCities(events)
      }
      if (this.skillTagOptions.length === 0) {
        this.skillTagOptions = this.collectUniqueSkillTags(events)
      }
    },

    collectUniqueCities(events) {
      const map = new Map()
      events.forEach((event) => map.set(event.cityId, event.city))
      return Array.from(map, ([id, name]) => ({ id, name })).sort((a, b) => a.name.localeCompare(b.name))
    },

    collectUniqueSkillTags(events) {
      const map = new Map()
      events.forEach((event) => {
        event.skillTagIds.forEach((id, index) => map.set(id, event.skillTags[index]))
      })
      return Array.from(map, ([id, name]) => ({ id, name })).sort((a, b) => a.name.localeCompare(b.name))
    },

    formatDate(isoDate) {
      const [year, month, day] = isoDate.split('-')
      return `${day}.${month}.${year}`
    },

    goToEventDetails(eventId) {
      NavigationService.navigateToEventDetails(eventId)
    },

    goToCreateEvent() {
      NavigationService.navigateToCreateEvent()
    },
  },
  beforeMount() {
    this.getEvents()
  },
}
</script>

<style scoped>
.text-truncate-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
