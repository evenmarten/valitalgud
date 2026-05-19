<template>
  <div>
    <AppNavbar />

    <div class="container py-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0">Minu sündmused</h2>
        <button class="btn btn-outline-primary" @click="goToMyOrganizedEvents">Minu loodud sündmused</button>
      </div>

      <AlertError :error-message="errorMessage" />

      <ul class="nav nav-tabs mb-4">
        <li v-for="tab in tabs" :key="tab.value" class="nav-item">
          <a
            href="#"
            class="nav-link"
            :class="{ active: filter === tab.value }"
            @click.prevent="changeFilter(tab.value)"
          >{{ tab.label }}</a>
        </li>
      </ul>

      <div v-if="myEvents.length === 0" class="text-center py-5">
        <p class="text-muted fs-5">Sa pole veel registreerunud ühelegi sündmusele</p>
      </div>

      <div v-else class="row g-4">
        <div
          v-for="myEvent in myEvents"
          :key="myEvent.eventId"
          class="col-md-6 col-lg-4"
        >
          <div class="card h-100">
            <div class="card-body d-flex flex-column">
              <div class="d-flex justify-content-between align-items-start mb-2">
                <h5 class="card-title mb-0">{{ myEvent.title }}</h5>
                <span class="badge" :class="statusBadgeClass(myEvent.userRegistrationStatus)">
                  {{ statusLabel(myEvent.userRegistrationStatus) }}
                </span>
              </div>
              <p class="text-muted small mb-2">
                {{ formatDate(myEvent.date) }} · {{ myEvent.location }}
              </p>
              <p class="card-text text-truncate-3">{{ myEvent.description }}</p>
              <button class="btn btn-outline-primary mt-auto" @click="goToEventDetails(myEvent.eventId)">
                View event
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
import MyEventsService from '@/api-services/MyEventsService.js'
import AuthHelper from '@/auth/auth.js'
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'MyEventsView',
  components: { AppNavbar, AlertError },
  data() {
    return {
      filter: 'THIS_WEEK',
      tabs: [
        { value: 'THIS_WEEK', label: 'Sel nädalal' },
        { value: 'UPCOMING', label: 'Tulevased' },
        { value: 'ALL_FUTURE', label: 'Kõik' },
      ],
      myEvents: [],
      errorMessage: '',
    }
  },
  methods: {
    getMyEvents() {
      const userId = AuthHelper.getUser()?.userId
      MyEventsService.sendGetMyEventsRequest(userId, this.filter)
        .then((response) => this.handleGetMyEventsResponse(response.data))
        .catch((error) => this.handleGetMyEventsError(error))
        .finally()
    },

    handleGetMyEventsResponse(myEvents) {
      this.myEvents = myEvents
      this.errorMessage = ''
    },

    handleGetMyEventsError(error) {
      const statusCode = error.response?.status
      if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
      } else if (statusCode === 400) {
        this.errorMessage = error.response?.data?.message || 'Vigane filter'
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    changeFilter(filter) {
      this.filter = filter
      this.getMyEvents()
    },

    goToEventDetails(eventId) {
      NavigationService.navigateToEventDetails(eventId)
    },

    goToMyOrganizedEvents() {
      NavigationService.navigateToMyOrganizedEvents()
    },

    statusLabel(status) {
      const labels = { LAHEB: 'LÄHEB', VOIB_OLLA: 'VÕIB-OLLA', EI_LAHE: 'EI LÄHE' }
      return labels[status] ?? status
    },

    statusBadgeClass(status) {
      const classes = {
        LAHEB: 'bg-success',
        VOIB_OLLA: 'bg-warning text-dark',
        EI_LAHE: 'bg-secondary',
      }
      return classes[status] ?? 'bg-info'
    },

    formatDate(isoDate) {
      const [year, month, day] = isoDate.split('-')
      return `${day}.${month}.${year}`
    },
  },
  beforeMount() {
    this.getMyEvents()
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
