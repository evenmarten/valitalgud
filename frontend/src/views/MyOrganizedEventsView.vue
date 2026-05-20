<template>
  <div>
    <AppNavbar />

    <div class="container py-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0">Minu loodud sündmused</h2>
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
                <option v-for="city in cities" :key="city.id" :value="city.id">{{ city.name }}</option>
              </select>
            </div>
            <div class="col-md-4">
              <label for="skillTagFilter" class="form-label">Oskuse-tag</label>
              <select id="skillTagFilter" v-model="filter.skillTagId" class="form-select">
                <option :value="null">-- Kõik tagid --</option>
                <option v-for="tag in skillTags" :key="tag.id" :value="tag.id">{{ tag.name }}</option>
              </select>
            </div>
            <div class="col-md-3">
              <label for="dateFilter" class="form-label">Kuupäev</label>
              <input id="dateFilter" v-model="filter.date" type="date" class="form-control" />
            </div>
            <div class="col-md-1 d-flex align-items-end">
              <button class="btn btn-primary w-100" @click="getMyOrganizedEvents">Filtreeri</button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="organizedEvents.length === 0" class="text-center py-5">
        <p class="text-muted fs-5">Sa pole veel ühtegi sündmust loonud</p>
      </div>

      <div v-else class="card">
        <div class="table-responsive">
          <table class="table mb-0 align-middle">
            <thead class="table-light">
              <tr>
                <th>Pealkiri</th>
                <th>Kuupäev</th>
                <th>Linn</th>
                <th>Staatus</th>
                <th>Osalejaid</th>
                <th class="text-end">Tegevused</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="organizedEvent in organizedEvents" :key="organizedEvent.eventId">
                <td>{{ organizedEvent.title }}</td>
                <td>{{ formatDate(organizedEvent.date) }}</td>
                <td>{{ organizedEvent.city }}</td>
                <td>
                  <span class="badge" :class="statusBadgeClass(organizedEvent.status)">
                    {{ organizedEvent.status }}
                  </span>
                </td>
                <td>{{ formatParticipants(organizedEvent) }}</td>
                <td class="text-end">
                  <div class="d-flex gap-2 justify-content-end">
                    <button class="btn btn-sm btn-outline-primary" @click="goToEventDetails(organizedEvent.eventId)">Detail</button>
                    <button class="btn btn-sm btn-outline-secondary" @click="goToEditEvent(organizedEvent.eventId)">Muuda</button>
                    <button class="btn btn-sm btn-outline-danger" @click="openDeleteModal(organizedEvent)">Kustuta sündmus</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div v-if="isDeleteModalOpen" class="modal-overlay" @click.self="closeDeleteModal">
      <div class="modal-content p-4">
        <h5 class="mb-3">Kinnita kustutamine</h5>
        <p class="mb-4">Kas oled kindel, et soovid sündmuse "{{ eventToDelete?.title }}" tühistada?</p>
        <div class="d-flex justify-content-end gap-2">
          <button class="btn btn-secondary" @click="closeDeleteModal">Loobu</button>
          <button class="btn btn-danger" @click="deleteEvent">Jah, kustuta</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AppNavbar from '@/navigation/AppNavbar.vue'
import AlertError from '@/components/common/AlertError.vue'
import MyOrganizedEventsService from '@/api-services/MyOrganizedEventsService.js'
import EventService from '@/api-services/EventService.js'
import CityService from '@/api-services/CityService.js'
import SkillTagService from '@/api-services/SkillTagService.js'
import AuthHelper from '@/auth/auth.js'
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'MyOrganizedEventsView',
  components: { AppNavbar, AlertError },
  data() {
    return {
      organizedEvents: [],
      cities: [],
      skillTags: [],
      filter: {
        cityId: null,
        skillTagId: null,
        date: '',
      },
      isDeleteModalOpen: false,
      eventToDelete: null,
      errorMessage: '',
    }
  },
  methods: {
    getMyOrganizedEvents() {
      const userId = AuthHelper.getUser()?.userId
      MyOrganizedEventsService.sendGetMyOrganizedEventsRequest(userId, this.buildQueryParams())
        .then((response) => this.handleGetMyOrganizedEventsResponse(response.data))
        .catch((error) => this.handleGetMyOrganizedEventsError(error))
        .finally()
    },

    handleGetMyOrganizedEventsResponse(events) {
      this.organizedEvents = events
      this.errorMessage = ''
    },

    handleGetMyOrganizedEventsError(error) {
      const statusCode = error.response?.status
      if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
      } else if (statusCode === 400) {
        this.errorMessage = error.response?.data?.message || 'Vigane päring'
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    buildQueryParams() {
      const params = {}
      if (this.filter.cityId) params.cityId = this.filter.cityId
      if (this.filter.skillTagId) params.skillTagId = this.filter.skillTagId
      if (this.filter.date) params.date = this.filter.date
      return params
    },

    getCities() {
      CityService.sendGetCitiesRequest()
        .then((response) => (this.cities = response.data))
        .catch(() => NavigationService.navigateToErrorView())
        .finally()
    },

    getSkillTags() {
      SkillTagService.sendGetSkillTagsRequest()
        .then((response) => (this.skillTags = response.data))
        .catch(() => NavigationService.navigateToErrorView())
        .finally()
    },

    goToEventDetails(eventId) {
      NavigationService.navigateToEventDetails(eventId)
    },

    goToEditEvent(eventId) {
      NavigationService.navigateToEditEvent(eventId)
    },

    goToCreateEvent() {
      NavigationService.navigateToCreateEvent()
    },

    openDeleteModal(organizedEvent) {
      this.eventToDelete = organizedEvent
      this.isDeleteModalOpen = true
    },

    closeDeleteModal() {
      this.isDeleteModalOpen = false
      this.eventToDelete = null
    },

    deleteEvent() {
      const userId = AuthHelper.getUser()?.userId
      const eventId = this.eventToDelete.eventId
      EventService.sendDeleteEventRequest(eventId, userId)
        .then(() => this.handleDeleteSuccess())
        .catch((error) => this.handleDeleteError(error))
        .finally()
    },

    handleDeleteSuccess() {
      this.closeDeleteModal()
      this.getMyOrganizedEvents()
    },

    handleDeleteError(error) {
      this.closeDeleteModal()
      const statusCode = error.response?.status
      if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
        return
      }
      if (statusCode === 403 || statusCode === 404) {
        this.errorMessage = error.response?.data?.message || 'Sündmuse kustutamine ebaõnnestus'
        return
      }
      NavigationService.navigateToErrorView()
    },

    statusBadgeClass(status) {
      const classes = {
        'Aktiivne': 'bg-success',
        'Lõppenud': 'bg-secondary',
        'Tühistatud': 'bg-danger',
      }
      return classes[status] ?? 'bg-info'
    },

    formatDate(isoDate) {
      const [year, month, day] = isoDate.split('-')
      return `${day}.${month}.${year}`
    },

    formatParticipants(organizedEvent) {
      if (organizedEvent.maxParticipants == null) {
        return String(organizedEvent.currentParticipants)
      }
      return `${organizedEvent.currentParticipants}/${organizedEvent.maxParticipants}`
    },
  },
  beforeMount() {
    this.getCities()
    this.getSkillTags()
    this.getMyOrganizedEvents()
  },
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1050;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background: white;
  width: 460px;
  border-radius: 8px;
}
</style>
