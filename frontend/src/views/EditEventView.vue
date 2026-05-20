<template>
  <div>
    <AppNavbar />

    <div class="container py-4">
      <h2 class="mb-4">Muuda sündmust</h2>

      <AlertError :error-message="errorMessage" />

      <div class="card">
        <div class="card-body">
          <div class="row g-3">
            <div class="col-12">
              <label class="form-label">Pealkiri *</label>
              <input v-model="updateEventDto.title" type="text" class="form-control" />
            </div>
            <div class="col-12">
              <label class="form-label">Kirjeldus</label>
              <textarea v-model="updateEventDto.description" rows="3" class="form-control"></textarea>
            </div>
          </div>

          <div class="row g-3 mt-1">
            <div class="col-md-6">
              <label class="form-label">Linn (ei saa muuta)</label>
              <select :value="cityId" disabled class="form-select">
                <option v-for="city in cities" :key="city.id" :value="city.id">{{ city.name }}</option>
              </select>
            </div>
            <div class="col-md-6">
              <label class="form-label">Aadress *</label>
              <input v-model="updateEventDto.address" type="text" class="form-control" />
            </div>
          </div>

          <div class="row g-3 mt-1">
            <div class="col-md-4">
              <label class="form-label">Kuupäev *</label>
              <input v-model="updateEventDto.date" type="date" class="form-control" />
            </div>
            <div class="col-md-4">
              <label class="form-label">Algusaeg *</label>
              <input v-model="updateEventDto.startTime" type="time" class="form-control" />
            </div>
            <div class="col-md-4">
              <label class="form-label">Lõpuaeg *</label>
              <input v-model="updateEventDto.endTime" type="time" class="form-control" />
            </div>
          </div>

          <div class="row g-3 mt-1">
            <div class="col-md-6">
              <label class="form-label">Max osalejaid * (hetkel registreerunuid: {{ currentParticipants }})</label>
              <input v-model.number="updateEventDto.maxParticipants" type="number" :min="currentParticipants" class="form-control" />
            </div>
            <div class="col-md-6">
              <label class="form-label">Oskuse-tagid</label>
              <select v-model="updateEventDto.skillTagIds" multiple class="form-select">
                <option v-for="tag in skillTags" :key="tag.id" :value="tag.id">{{ tag.name }}</option>
              </select>
              <small class="text-muted">Hoia Ctrl/Cmd, et valida mitu</small>
            </div>
          </div>

          <div class="mt-3">
            <label class="form-label">Banner-pildi URL</label>
            <input v-model="updateEventDto.bannerImageUrl" type="url" class="form-control" placeholder="https://example.com/pilt.jpg" />
          </div>

          <div v-if="updateEventDto.bannerImageUrl" class="mt-3">
            <small class="text-muted">Eelvaade:</small>
            <img
              :src="updateEventDto.bannerImageUrl"
              alt="Banner eelvaade"
              class="img-fluid rounded mt-1"
              style="max-height: 220px; object-fit: cover; width: 100%;"
            />
          </div>

          <div class="d-flex justify-content-between mt-4">
            <button class="btn btn-outline-danger" @click="openCancelModal">Tühista sündmus</button>
            <div class="d-flex gap-2">
              <button class="btn btn-secondary" @click="goBack">Tagasi</button>
              <button class="btn btn-success" @click="updateEvent">Salvesta</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="isCancelModalOpen" class="modal-overlay" @click.self="closeCancelModal">
      <div class="modal-content p-4">
        <h5 class="mb-3">Kinnita tühistamine</h5>
        <p class="mb-4">Kas oled kindel, et soovid selle sündmuse tühistada? Tegevus on tagasipööratav ainult andmebaasi kaudu.</p>
        <div class="d-flex justify-content-end gap-2">
          <button class="btn btn-secondary" @click="closeCancelModal">Loobu</button>
          <button class="btn btn-danger" @click="deleteEvent">Jah, tühista</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AppNavbar from '@/navigation/AppNavbar.vue'
import AlertError from '@/components/common/AlertError.vue'
import EventService from '@/api-services/EventService.js'
import CityService from '@/api-services/CityService.js'
import SkillTagService from '@/api-services/SkillTagService.js'
import AuthHelper from '@/auth/auth.js'
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'EditEventView',
  components: { AppNavbar, AlertError },
  data() {
    return {
      eventId: 0,
      cityId: null,
      currentParticipants: 0,
      updateEventDto: {
        title: '',
        description: '',
        address: '',
        date: '',
        startTime: '',
        endTime: '',
        maxParticipants: 1,
        skillTagIds: [],
        bannerImageUrl: '',
      },
      cities: [],
      skillTags: [],
      isCancelModalOpen: false,
      errorMessage: '',
    }
  },
  methods: {
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

    getEvent() {
      const userId = AuthHelper.getUser()?.userId
      EventService.sendGetEventForEditRequest(this.eventId, userId)
        .then((response) => this.handleGetEventResponse(response.data))
        .catch((error) => this.handleGetEventError(error))
        .finally()
    },

    handleGetEventResponse(event) {
      this.cityId = event.cityId
      this.currentParticipants = event.currentParticipants ?? 0
      this.updateEventDto = {
        title: event.title ?? '',
        description: event.description ?? '',
        address: event.address ?? '',
        date: event.eventDate ?? '',
        startTime: event.startTime ?? '',
        endTime: event.endTime ?? '',
        maxParticipants: event.maxParticipants ?? 1,
        skillTagIds: event.skillTagIds ?? [],
        bannerImageUrl: event.bannerImageUrl ?? '',
      }
    },

    handleGetEventError(error) {
      const statusCode = error.response?.status
      if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
      } else if (statusCode === 404) {
        this.errorMessage = 'Sündmust ei leitud'
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    updateEvent() {
      if (!this.validateFields()) return
      const userId = AuthHelper.getUser()?.userId
      EventService.sendUpdateEventRequest(this.eventId, this.updateEventDto, userId)
        .then(() => this.handleUpdateSuccess())
        .catch((error) => this.handleUpdateError(error))
        .finally()
    },

    handleUpdateSuccess() {
      NavigationService.navigateToMyEvents()
    },

    handleUpdateError(error) {
      const statusCode = error.response?.status
      if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
        return
      }
      if (statusCode === 400 || statusCode === 403 || statusCode === 404) {
        this.errorMessage = error.response?.data?.message || 'Sündmuse muutmine ebaõnnestus'
        return
      }
      NavigationService.navigateToErrorView()
    },

    validateFields() {
      const dto = this.updateEventDto
      if (!dto.title || !dto.address || !dto.date || !dto.startTime || !dto.endTime || !dto.maxParticipants) {
        this.errorMessage = 'Palun täitke kõik kohustuslikud väljad'
        return false
      }
      if (dto.endTime <= dto.startTime) {
        this.errorMessage = 'Lõpuaeg peab olema hilisem kui algusaeg'
        return false
      }
      if (dto.maxParticipants < this.currentParticipants) {
        this.errorMessage = `Max osalejaid (${dto.maxParticipants}) ei saa olla väiksem kui hetkel registreerunute arv (${this.currentParticipants})`
        return false
      }
      this.errorMessage = ''
      return true
    },

    openCancelModal() {
      this.isCancelModalOpen = true
    },

    closeCancelModal() {
      this.isCancelModalOpen = false
    },

    deleteEvent() {
      const userId = AuthHelper.getUser()?.userId
      EventService.sendDeleteEventRequest(this.eventId, userId)
        .then(() => this.handleDeleteSuccess())
        .catch((error) => this.handleDeleteError(error))
        .finally()
    },

    handleDeleteSuccess() {
      this.closeCancelModal()
      NavigationService.navigateToMyEvents()
    },

    handleDeleteError(error) {
      this.closeCancelModal()
      const statusCode = error.response?.status
      if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
        return
      }
      if (statusCode === 403 || statusCode === 404) {
        this.errorMessage = error.response?.data?.message || 'Sündmuse tühistamine ebaõnnestus'
        return
      }
      NavigationService.navigateToErrorView()
    },

    goBack() {
      NavigationService.navigateToMyEvents()
    },
  },
  beforeMount() {
    this.eventId = Number(this.$route.params.id)
    this.getCities()
    this.getSkillTags()
    this.getEvent()
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
  width: 420px;
  border: 3px solid #000;
  border-radius: 0;
  box-shadow: 8px 8px 0 #000;
}
</style>
