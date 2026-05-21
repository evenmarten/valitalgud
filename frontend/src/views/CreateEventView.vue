<template>
  <div>
    <AppNavbar />

    <div class="container py-4">
      <h2 class="mb-4">Loo uus sündmus</h2>

      <div class="d-flex mb-4 gap-2">
        <div
          v-for="step in stepDefinitions"
          :key="step.id"
          class="flex-fill p-2 rounded text-center"
          :class="stepClass(step.id)"
        >
          <small class="d-block fw-bold">{{ step.id }}. samm</small>
          <span>{{ step.label }}</span>
        </div>
      </div>

      <AlertError :error-message="errorMessage" />

      <div v-if="currentStep === 1" class="card">
        <div class="card-body">
          <h5 class="card-title mb-3">Sündmuse andmed</h5>

          <div class="mb-3">
            <label class="form-label">Pealkiri *</label>
            <input v-model="createEventDto.title" type="text" class="form-control" />
          </div>

          <div class="mb-3">
            <label class="form-label">Kirjeldus</label>
            <textarea v-model="createEventDto.description" rows="3" class="form-control"></textarea>
          </div>

          <div class="row g-3">
            <div class="col-md-4">
              <label class="form-label">Maakond *</label>
              <select v-model="selectedCountyId" class="form-select" @change="onCountyChange">
                <option :value="null">-- Vali maakond --</option>
                <option v-for="county in counties" :key="county.id" :value="county.id">{{ county.name }}</option>
              </select>
            </div>
            <div class="col-md-4">
              <label class="form-label">Linn *</label>
              <select v-model="createEventDto.cityId" class="form-select" :disabled="!selectedCountyId">
                <option :value="null">-- Vali linn --</option>
                <option v-for="city in filteredCities" :key="city.id" :value="city.id">{{ city.name }}</option>
              </select>
            </div>
            <div class="col-md-4">
              <label class="form-label">Aadress *</label>
              <input v-model="createEventDto.address" type="text" class="form-control" />
            </div>
          </div>

          <div class="row g-3 mt-1">
            <div class="col-md-4">
              <label class="form-label">Kuupäev *</label>
              <input v-model="createEventDto.date" type="date" class="form-control" />
            </div>
            <div class="col-md-4">
              <label class="form-label">Algusaeg *</label>
              <input v-model="createEventDto.startTime" type="time" class="form-control" />
            </div>
            <div class="col-md-4">
              <label class="form-label">Lõpuaeg *</label>
              <input v-model="createEventDto.endTime" type="time" class="form-control" />
            </div>
          </div>

          <div class="row g-3 mt-1">
            <div class="col-md-6">
              <label class="form-label">Max osalejaid *</label>
              <input v-model.number="createEventDto.maxParticipants" type="number" min="1" class="form-control" />
            </div>
            <div class="col-md-6">
              <label class="form-label">Oskuse-tagid</label>
              <SkillTagMultiSelect
                :tags="skillTags"
                :selected-ids="createEventDto.skillTagIds"
                @event-tags-changed="createEventDto.skillTagIds = $event"
              />
            </div>
          </div>

          <div class="d-flex justify-content-end mt-4 gap-2">
            <button class="btn btn-secondary" @click="cancel">Tühista</button>
            <button class="btn btn-primary" @click="goToStep2">Edasi</button>
          </div>
        </div>
      </div>

      <div v-if="currentStep === 2" class="card">
        <div class="card-body">
          <h5 class="card-title mb-3">Banner-pilt</h5>
          <p class="text-muted small">Sisesta pildi URL või jäta tühjaks ning kasuta vaikepilti.</p>

          <div class="mb-3">
            <label class="form-label">Banner-pildi URL</label>
            <input
              v-model="createEventDto.bannerImageUrl"
              type="url"
              class="form-control"
              placeholder="https://example.com/pilt.jpg"
            />
          </div>

          <div v-if="createEventDto.bannerImageUrl" class="mb-3">
            <small class="text-muted">Eelvaade:</small>
            <img
              :src="createEventDto.bannerImageUrl"
              alt="Banner eelvaade"
              class="img-fluid rounded mt-1"
              style="max-height: 220px; object-fit: cover; width: 100%;"
            />
          </div>

          <div class="d-flex justify-content-between mt-4">
            <button class="btn btn-outline-secondary" @click="goToStep1">Tagasi</button>
            <button class="btn btn-primary" @click="goToStep3">Edasi</button>
          </div>
        </div>
      </div>

      <div v-if="currentStep === 3" class="card">
        <div class="card-body">
          <h5 class="card-title mb-3">Kinnita andmed</h5>

          <dl class="row mb-0">
            <dt class="col-sm-3">Pealkiri</dt>
            <dd class="col-sm-9">{{ createEventDto.title }}</dd>

            <dt class="col-sm-3">Kirjeldus</dt>
            <dd class="col-sm-9">{{ createEventDto.description || '—' }}</dd>

            <dt class="col-sm-3">Linn</dt>
            <dd class="col-sm-9">{{ selectedCityName }}</dd>

            <dt class="col-sm-3">Maakond</dt>
            <dd class="col-sm-9">{{ selectedCountyName }}</dd>

            <dt class="col-sm-3">Aadress</dt>
            <dd class="col-sm-9">{{ createEventDto.address }}</dd>

            <dt class="col-sm-3">Kuupäev</dt>
            <dd class="col-sm-9">{{ createEventDto.date }}</dd>

            <dt class="col-sm-3">Algus / Lõpp</dt>
            <dd class="col-sm-9">{{ createEventDto.startTime }} – {{ createEventDto.endTime }}</dd>

            <dt class="col-sm-3">Max osalejaid</dt>
            <dd class="col-sm-9">{{ createEventDto.maxParticipants }}</dd>

            <dt class="col-sm-3">Oskuse-tagid</dt>
            <dd class="col-sm-9">{{ selectedSkillTagNames || '—' }}</dd>

            <dt class="col-sm-3">Banner</dt>
            <dd class="col-sm-9">{{ createEventDto.bannerImageUrl || '—' }}</dd>
          </dl>

          <div class="d-flex justify-content-between mt-4">
            <button class="btn btn-outline-secondary" @click="goToStep2">Tagasi</button>
            <button class="btn btn-success" @click="createEvent">Loo sündmus</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AppNavbar from '@/navigation/AppNavbar.vue'
import AlertError from '@/components/common/AlertError.vue'
import SkillTagMultiSelect from '@/components/forms/SkillTagMultiSelect.vue'
import CityService from '@/api-services/CityService.js'
import CountyService from '@/api-services/CountyService.js'
import SkillTagService from '@/api-services/SkillTagService.js'
import EventService from '@/api-services/EventService.js'
import AuthHelper from '@/auth/auth.js'
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'CreateEventView',
  components: { AppNavbar, AlertError, SkillTagMultiSelect },
  data() {
    return {
      currentStep: 1,
      stepDefinitions: [
        { id: 1, label: 'Andmed' },
        { id: 2, label: 'Pilt' },
        { id: 3, label: 'Kinnitus' },
      ],
      // AJUTINE: eeltäidetud testväärtused kiiremaks testimiseks — eemalda enne tootmist
      selectedCountyId: 1,
      createEventDto: {
        title: 'Test Sündmus',
        description: 'Test sündmuse kirjeldus, mis tutvustab üritust.',
        cityId: 1,
        address: 'Testi tänav 5',
        date: '2026-12-15',
        startTime: '10:00',
        endTime: '14:00',
        maxParticipants: 30,
        bannerImageUrl: 'https://picsum.photos/600/300',
        skillTagIds: [1],
      },
      cities: [],
      counties: [],
      skillTags: [],
      errorMessage: '',
    }
  },
  computed: {
    selectedCityName() {
      const city = this.cities.find((c) => c.id === this.createEventDto.cityId)
      return city ? city.name : '—'
    },
    selectedCountyName() {
      const county = this.counties.find((c) => c.id === this.selectedCountyId)
      return county ? county.name : '—'
    },
    filteredCities() {
      if (!this.selectedCountyId) return []
      return this.cities.filter((city) => city.countyId === this.selectedCountyId)
    },
    selectedSkillTagNames() {
      return this.skillTags
        .filter((tag) => this.createEventDto.skillTagIds.includes(tag.id))
        .map((tag) => tag.name)
        .join(', ')
    },
  },
  methods: {
    getCities() {
      CityService.sendGetCitiesRequest()
        .then((response) => this.handleGetCitiesResponse(response.data))
        .catch(() => NavigationService.navigateToErrorView())
        .finally()
    },

    handleGetCitiesResponse(cities) {
      this.cities = cities
    },

    getCounties() {
      CountyService.sendGetCountiesRequest()
        .then((response) => this.handleGetCountiesResponse(response.data))
        .catch(() => NavigationService.navigateToErrorView())
        .finally()
    },

    handleGetCountiesResponse(counties) {
      this.counties = counties
    },

    onCountyChange() {
      const cityStillValid = this.filteredCities.some((city) => city.id === this.createEventDto.cityId)
      if (!cityStillValid) {
        this.createEventDto.cityId = null
      }
    },

    getSkillTags() {
      SkillTagService.sendGetSkillTagsRequest()
        .then((response) => this.handleGetSkillTagsResponse(response.data))
        .catch(() => NavigationService.navigateToErrorView())
        .finally()
    },

    handleGetSkillTagsResponse(skillTags) {
      this.skillTags = skillTags
    },

    goToStep1() {
      this.errorMessage = ''
      this.currentStep = 1
    },

    goToStep2() {
      if (!this.validateStep1Fields()) return
      this.errorMessage = ''
      this.currentStep = 2
    },

    goToStep3() {
      this.errorMessage = ''
      this.currentStep = 3
    },

    validateStep1Fields() {
      const dto = this.createEventDto
      if (!dto.title || !this.selectedCountyId || !dto.cityId || !dto.address || !dto.date || !dto.startTime || !dto.endTime || !dto.maxParticipants) {
        this.errorMessage = 'Palun täitke kõik kohustuslikud väljad (*)'
        return false
      }
      if (dto.endTime <= dto.startTime) {
        this.errorMessage = 'Lõpuaeg peab olema hilisem kui algusaeg'
        return false
      }
      if (dto.maxParticipants <= 0) {
        this.errorMessage = 'Max osalejate arv peab olema suurem kui 0'
        return false
      }
      return true
    },

    createEvent() {
      const userId = AuthHelper.getUser()?.userId
      EventService.sendCreateEventRequest(this.createEventDto, userId)
        .then(() => this.handleCreateEventSuccess())
        .catch((error) => this.handleCreateEventError(error))
        .finally()
    },

    handleCreateEventSuccess() {
      NavigationService.navigateToMyEvents()
    },

    handleCreateEventError(error) {
      const statusCode = error.response?.status
      const errorCode = error.response?.data?.code

      if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
        return
      }
      if (statusCode === 400) {
        this.errorMessage = error.response?.data?.message || 'Palun täitke kõik väljad õigesti'
        this.jumpToStepForError(errorCode)
        return
      }
      NavigationService.navigateToErrorView()
    },

    jumpToStepForError(errorCode) {
      const step1Errors = [
        'INVALID_EVENT_DATA',
        'INVALID_EVENT_DATE',
        'INVALID_EVENT_TIME_RANGE',
        'INVALID_PARTICIPANTS_COUNT',
        'CITY_NOT_FOUND',
        'SKILL_TAG_NOT_FOUND',
      ]
      if (step1Errors.includes(errorCode)) {
        this.currentStep = 1
      }
    },

    cancel() {
      NavigationService.navigateToEvents()
    },

    stepClass(stepId) {
      if (stepId === this.currentStep) return 'bg-primary text-white'
      if (stepId < this.currentStep) return 'bg-success text-white'
      return 'bg-light text-muted'
    },
  },
  beforeMount() {
    this.getCities()
    this.getCounties()
    this.getSkillTags()
  },
}
</script>
