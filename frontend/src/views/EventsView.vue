<template>
  <div>
    <AppNavbar />

    <div class="container pt-4 pb-5">
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
              <select id="cityFilter" v-model="filter.cityId" class="form-select">
                <option :value="null">-- Kõik linnad --</option>
                <option v-for="city in cityOptions" :key="city.id" :value="city.id">
                  {{ city.name }}
                </option>
              </select>
            </div>

            <div class="col-md-4">
              <label for="countyFilter" class="form-label">Maakond</label>
              <select id="countyFilter" v-model="filter.countyId" class="form-select">
                <option :value="null">-- Kõik maakonnad --</option>
                <option v-for="county in countyOptions" :key="county.id" :value="county.id">
                  {{ county.name }}
                </option>
              </select>
            </div>

            <div class="col-md-4">
              <label class="form-label">Oskuse-tag</label>
              <SkillTagFilter
                :tags="skillTagOptions"
                :selected-id="filter.skillTagId"
                @event-tag-selected="filter.skillTagId = $event"
              />
            </div>

            <div class="col-md-3">
              <label for="fromDateFilter" class="form-label">Alates kuupäevast</label>
              <input
                id="fromDateFilter"
                v-model="filter.fromDate"
                type="date"
                class="form-control"
              />
            </div>

            <div class="col-md-2 d-flex align-items-end">
              <button class="btn btn-primary w-100" @click="getEvents">Kinnita</button>
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
                {{ formatDate(event.eventDate) }} · {{ event.city }}<span v-if="event.county"> · {{ event.county }}</span>
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
                Näita rohkem
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
import SkillTagFilter from '@/components/forms/SkillTagFilter.vue'
import EventService from '@/api-services/EventService.js'
import SkillTagService from '@/api-services/SkillTagService.js'
import CityService from '@/api-services/CityService.js'
import CountyService from '@/api-services/CountyService.js'
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'EventsView',
  components: { AppNavbar, AlertError, SkillTagFilter },
  data() {
    return {
      events: [],
      cityOptions: [],
      countyOptions: [],
      skillTagOptions: [],
      filter: {
        cityId: null,
        countyId: null,
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
    },

    getSkillTags() {
      SkillTagService.sendGetSkillTagsRequest()
        .then((response) => (this.skillTagOptions = response.data))
        .catch(() => NavigationService.navigateToErrorView())
        .finally()
    },

    getCities() {
      CityService.sendGetCitiesRequest()
        .then((response) => (this.cityOptions = response.data))
        .catch(() => NavigationService.navigateToErrorView())
        .finally()
    },

    getCounties() {
      CountyService.sendGetCountiesRequest()
        .then((response) => (this.countyOptions = response.data))
        .catch(() => NavigationService.navigateToErrorView())
        .finally()
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
      if (this.filter.countyId) params.countyId = this.filter.countyId
      if (this.filter.skillTagId) params.skillTagId = this.filter.skillTagId
      if (this.filter.fromDate) params.fromDate = this.filter.fromDate
      return params
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
    this.getCities()
    this.getCounties()
    this.getSkillTags()
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
