<template>
  <div>
    <AppNavbar />

    <div class="container py-4">
      <AlertError :error-message="errorMessage" />

      <div v-if="event.eventId">
        <img
          v-if="event.bannerImageUrl"
          :src="event.bannerImageUrl"
          :alt="event.title"
          class="w-100 rounded mb-4"
          style="max-height: 320px; object-fit: cover;"
        />
        <div v-else class="bg-secondary rounded mb-4" style="height: 200px;"></div>

        <div class="mb-4">
          <h2>{{ event.title }}</h2>
          <p class="text-muted mb-1">
            {{ formatDate(event.eventDate) }}
            <span v-if="event.startTime">
              · {{ event.startTime }}<span v-if="event.endTime">–{{ event.endTime }}</span>
            </span>
          </p>
          <p class="text-muted mb-0">
            {{ event.city }}<span v-if="event.address"> · {{ event.address }}</span>
          </p>
        </div>

        <div v-if="event.description" class="mb-4">
          <p>{{ event.description }}</p>
        </div>

        <div v-if="event.skillTags && event.skillTags.length" class="mb-4">
          <span
            v-for="tag in event.skillTags"
            :key="tag"
            class="badge bg-info text-dark me-1"
          >{{ tag }}</span>
        </div>

        <p class="text-muted mb-4">
          Osalejaid: {{ event.currentParticipants }}{{ event.maxParticipants ? ` / ${event.maxParticipants}` : '' }}
        </p>

        <div class="mb-4">
          <p class="mb-1"><strong>Korraldaja:</strong> {{ event.organizerName }}</p>
          <p class="mb-0 text-muted">{{ event.organizerEmail }}</p>
        </div>

        <div class="card mb-4">
          <div class="card-body">
            <h5 class="card-title">Registreerimine</h5>

            <template v-if="isLoggedIn">
              <div class="mb-3">
                <label class="form-label">Minu osavõtuplaan</label>
                <select v-model="selectedStatus" class="form-select" style="max-width: 280px;">
                  <option value="LAHEB">Lähen</option>
                  <option value="VOIB_OLLA">Võib-olla</option>
                  <option value="EI_LAHE">Ei lähe</option>
                </select>
              </div>
              <div class="d-flex gap-2">
                <button class="btn btn-primary" @click="register">
                  {{ isRegistered ? 'Muuda valikut' : 'Kinnita' }}
                </button>
                <button v-if="isRegistered" class="btn btn-outline-danger" @click="cancelRegistration">
                  Tühista registreerimine
                </button>
              </div>
            </template>

            <template v-else>
              <p class="mb-3">Sündmusele registreerimiseks pead olema sisse logitud.</p>
              <button class="btn btn-primary" @click="goToLogin">Logi sisse</button>
            </template>
          </div>
        </div>

        <div class="mb-4">
          <h4 class="mb-3">Kommentaarid</h4>

          <div v-if="isLoggedIn" class="mb-3">
            <textarea
              v-model="newComment"
              class="form-control mb-2"
              rows="3"
              placeholder="Lisa kommentaar..."
              maxlength="1000"
            ></textarea>
            <button class="btn btn-secondary" @click="addComment">Lisa kommentaar</button>
          </div>
          <p v-else class="text-muted mb-3">Kommentaari lisamiseks logi sisse.</p>

          <div v-if="comments.length === 0" class="text-muted py-2">
            Kommentaare pole
          </div>

          <div v-for="comment in comments" :key="comment.commentId" class="card mb-2">
            <div class="card-body py-2">
              <p class="mb-1">
                <strong>{{ comment.authorName || 'Kasutaja' }}</strong>
                <span class="text-muted small ms-2">{{ formatDateTime(comment.createdAt) }}</span>
              </p>
              <p class="mb-0">{{ comment.content }}</p>
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
import AuthHelper from '@/auth/auth.js'

export default {
  name: 'EventDetailsView',
  components: { AppNavbar, AlertError },
  data() {
    return {
      event: {
        eventId: null,
        title: '',
        description: '',
        city: '',
        address: '',
        eventDate: '',
        startTime: '',
        endTime: '',
        maxParticipants: null,
        currentParticipants: 0,
        skillTags: [],
        bannerImageUrl: '',
        organizerId: null,
        organizerName: '',
        organizerEmail: '',
        userRegistrationStatus: null,
      },
      comments: [],
      selectedStatus: 'LAHEB',
      newComment: '',
      errorMessage: '',
      userId: null,
    }
  },
  computed: {
    isLoggedIn() {
      return this.userId !== null
    },
    isRegistered() {
      return this.event.userRegistrationStatus !== null
    },
  },
  methods: {
    getEventDetails() {
      const eventId = this.$route.params.id
      EventService.sendGetEventDetailsRequest(eventId, this.userId)
        .then((response) => this.handleGetEventDetailsResponse(response.data))
        .catch((error) => this.handleGetEventDetailsError(error))
        .finally()
    },

    handleGetEventDetailsResponse(eventDetails) {
      this.event = eventDetails
      if (eventDetails.userRegistrationStatus) {
        this.selectedStatus = eventDetails.userRegistrationStatus
      }
    },

    handleGetEventDetailsError(error) {
      const statusCode = error.response?.status
      if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
      } else if (statusCode === 404) {
        this.errorMessage = 'Sündmust ei leitud'
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    getComments() {
      const eventId = this.$route.params.id
      EventService.sendGetCommentsRequest(eventId, this.userId)
        .then((response) => this.handleGetCommentsResponse(response.data))
        .catch((error) => this.handleGetCommentsError(error))
        .finally()
    },

    handleGetCommentsResponse(comments) {
      this.comments = comments
    },

    handleGetCommentsError(error) {
      const statusCode = error.response?.status
      if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    register() {
      const eventId = this.$route.params.id
      EventService.sendRegisterRequest(eventId, this.userId, { status: this.selectedStatus })
        .then(() => this.handleRegisterResponse())
        .catch((error) => this.handleRegisterError(error))
        .finally()
    },

    handleRegisterResponse() {
      this.errorMessage = ''
      this.getEventDetails()
    },

    handleRegisterError(error) {
      const statusCode = error.response?.status
      if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
      } else if (statusCode === 409) {
        this.errorMessage = error.response?.data?.message || 'Registreerimine ebaõnnestus'
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    cancelRegistration() {
      const eventId = this.$route.params.id
      EventService.sendCancelRegistrationRequest(eventId, this.userId)
        .then(() => this.handleCancelRegistrationResponse())
        .catch((error) => this.handleCancelRegistrationError(error))
        .finally()
    },

    handleCancelRegistrationResponse() {
      this.event.userRegistrationStatus = null
      this.selectedStatus = 'LAHEB'
      this.errorMessage = ''
      this.getEventDetails()
    },

    handleCancelRegistrationError(error) {
      const statusCode = error.response?.status
      if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    addComment() {
      if (!this.newComment.trim()) {
        this.errorMessage = 'Kommentaar ei saa olla tühi'
        return
      }
      const eventId = this.$route.params.id
      EventService.sendAddCommentRequest(eventId, this.userId, { content: this.newComment })
        .then((response) => this.handleAddCommentResponse(response.data))
        .catch((error) => this.handleAddCommentError(error))
        .finally()
    },

    handleAddCommentResponse(comment) {
      this.comments.unshift(comment)
      this.newComment = ''
      this.errorMessage = ''
    },

    handleAddCommentError(error) {
      const statusCode = error.response?.status
      if (statusCode === 400) {
        this.errorMessage = error.response?.data?.message || 'Kommentaari sisu on nõutud'
      } else if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    formatDate(isoDate) {
      if (!isoDate) return ''
      const [year, month, day] = isoDate.split('-')
      return `${day}.${month}.${year}`
    },

    formatDateTime(dateTimeStr) {
      if (!dateTimeStr) return ''
      const date = new Date(dateTimeStr)
      const day = String(date.getDate()).padStart(2, '0')
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const year = date.getFullYear()
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${day}.${month}.${year} ${hours}:${minutes}`
    },

    goToLogin() {
      NavigationService.navigateToLogin()
    },
  },
  beforeMount() {
    this.userId = AuthHelper.getUser()?.userId ?? null
    this.getEventDetails()
    this.getComments()
  },
}
</script>
