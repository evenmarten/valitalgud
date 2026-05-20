<template>
  <div>
    <AppNavbar />

    <div class="container py-4" style="max-width: 640px;">
      <AlertError :error-message="errorMessage" />

      <div v-if="successMessage" class="alert alert-success alert-dismissible mb-3" role="alert">
        {{ successMessage }}
        <button type="button" class="btn-close" @click="successMessage = ''"></button>
      </div>

      <div class="card mb-4">
        <div class="card-body">
          <h4 class="card-title mb-3">Minu profiil</h4>

          <dl class="row mb-3">
            <dt class="col-sm-4">Nimi</dt>
            <dd class="col-sm-8">{{ profile.fullName || '—' }}</dd>

            <dt class="col-sm-4">E-post</dt>
            <dd class="col-sm-8">{{ profile.email || '—' }}</dd>

            <dt class="col-sm-4">Telefon</dt>
            <dd class="col-sm-8">{{ profile.phone || '—' }}</dd>

            <template v-if="profile.description">
              <dt class="col-sm-4">Kirjeldus</dt>
              <dd class="col-sm-8">{{ profile.description }}</dd>
            </template>
          </dl>

          <div class="d-flex flex-wrap gap-2">
            <button class="btn btn-primary" @click="openEditForm">Muuda profiili</button>
            <button class="btn btn-outline-secondary" @click="openPasswordForm">Vaheta parool</button>
            <button class="btn btn-outline-danger ms-auto" @click="openDeleteConfirm">Kustuta konto</button>
          </div>
        </div>
      </div>

      <div v-if="isEditMode" class="card mb-4">
        <div class="card-body">
          <h5 class="card-title mb-3">Muuda profiili</h5>

          <div class="row g-3 mb-3">
            <div class="col-md-6">
              <label class="form-label">Eesnimi <span class="text-danger">*</span></label>
              <input v-model="editForm.firstName" type="text" class="form-control" placeholder="Eesnimi" />
            </div>
            <div class="col-md-6">
              <label class="form-label">Perekonnanimi <span class="text-danger">*</span></label>
              <input v-model="editForm.lastName" type="text" class="form-control" placeholder="Perekonnanimi" />
            </div>
            <div class="col-12">
              <label class="form-label">E-post <span class="text-danger">*</span></label>
              <input v-model="editForm.email" type="email" class="form-control" placeholder="E-post" />
            </div>
            <div class="col-12">
              <label class="form-label">Telefon</label>
              <input v-model="editForm.phone" type="text" class="form-control" placeholder="Telefon" />
            </div>
            <div class="col-12">
              <label class="form-label">Kirjeldus</label>
              <textarea v-model="editForm.description" class="form-control" rows="3" placeholder="Kirjeldus..."></textarea>
            </div>
          </div>

          <div class="d-flex gap-2">
            <button class="btn btn-success" @click="updateProfile">Salvesta muudatused</button>
            <button class="btn btn-outline-secondary" @click="isEditMode = false">Tühista</button>
          </div>
        </div>
      </div>

      <div v-if="isPasswordMode" class="card mb-4">
        <div class="card-body">
          <h5 class="card-title mb-3">Vaheta parool</h5>

          <div class="row g-3 mb-3">
            <div class="col-12">
              <label class="form-label">Praegune parool <span class="text-danger">*</span></label>
              <input v-model="passwordForm.oldPassword" type="password" class="form-control" placeholder="Praegune parool" />
            </div>
            <div class="col-12">
              <label class="form-label">Uus parool <span class="text-danger">*</span></label>
              <input v-model="passwordForm.newPassword" type="password" class="form-control" placeholder="Uus parool (min 8 tähemärki)" />
            </div>
            <div class="col-12">
              <label class="form-label">Kinnita uus parool <span class="text-danger">*</span></label>
              <input v-model="passwordForm.confirmNewPassword" type="password" class="form-control" placeholder="Kinnita uus parool" />
            </div>
          </div>

          <div class="d-flex gap-2">
            <button class="btn btn-success" @click="changePassword">Salvesta parool</button>
            <button class="btn btn-outline-secondary" @click="isPasswordMode = false">Tühista</button>
          </div>
        </div>
      </div>

      <div v-if="isDeleteConfirm" class="card mb-4 border-danger">
        <div class="card-body">
          <h5 class="card-title text-danger mb-2">Kustuta konto</h5>
          <p class="mb-3">Kas oled kindel? Konto kustutamine on pöördumatu toiming.</p>
          <div class="d-flex gap-2">
            <button class="btn btn-danger" @click="deleteProfile">Kinnita kustutamine</button>
            <button class="btn btn-outline-secondary" @click="isDeleteConfirm = false">Tühista</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AppNavbar from '@/navigation/AppNavbar.vue'
import AlertError from '@/components/common/AlertError.vue'
import ProfileService from '@/api-services/ProfileService.js'
import NavigationService from '@/navigation/NavigationService.js'
import AuthHelper from '@/auth/auth.js'

export default {
  name: 'ProfileView',
  components: { AppNavbar, AlertError },
  data() {
    return {
      profile: {
        userId: null,
        fullName: '',
        email: '',
        phone: '',
        role: '',
        description: '',
      },
      editForm: {
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        description: '',
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmNewPassword: '',
      },
      isEditMode: false,
      isPasswordMode: false,
      isDeleteConfirm: false,
      errorMessage: '',
      successMessage: '',
      userId: null,
    }
  },
  methods: {
    getProfile() {
      ProfileService.sendGetProfileRequest(this.userId, this.userId)
        .then((response) => this.handleGetProfileResponse(response.data))
        .catch((error) => this.handleGetProfileError(error))
        .finally()
    },

    handleGetProfileResponse(profile) {
      this.profile = profile
    },

    handleGetProfileError(error) {
      const statusCode = error.response?.status
      if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    updateProfile() {
      ProfileService.sendUpdateProfileRequest(this.userId, this.userId, this.editForm)
        .then((response) => this.handleUpdateProfileResponse(response.data))
        .catch((error) => this.handleUpdateProfileError(error))
        .finally()
    },

    handleUpdateProfileResponse(profile) {
      this.profile = profile
      this.isEditMode = false
      this.errorMessage = ''
      this.successMessage = 'Profiil uuendatud'
    },

    handleUpdateProfileError(error) {
      const statusCode = error.response?.status
      if (statusCode === 400 || statusCode === 409) {
        this.errorMessage = error.response?.data?.message || 'Profiili uuendamine ebaõnnestus'
      } else if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    changePassword() {
      ProfileService.sendChangePasswordRequest(this.userId, this.userId, this.passwordForm)
        .then(() => this.handleChangePasswordResponse())
        .catch((error) => this.handleChangePasswordError(error))
        .finally()
    },

    handleChangePasswordResponse() {
      this.passwordForm = { oldPassword: '', newPassword: '', confirmNewPassword: '' }
      this.isPasswordMode = false
      this.errorMessage = ''
      this.successMessage = 'Parool muudetud'
    },

    handleChangePasswordError(error) {
      const statusCode = error.response?.status
      if (statusCode === 400) {
        this.errorMessage = error.response?.data?.message || 'Parooli vahetus ebaõnnestus'
      } else if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    deleteProfile() {
      ProfileService.sendDeleteProfileRequest(this.userId, this.userId)
        .then(() => this.handleDeleteProfileResponse())
        .catch((error) => this.handleDeleteProfileError(error))
        .finally()
    },

    handleDeleteProfileResponse() {
      AuthHelper.clearUser()
      NavigationService.navigateToLogin()
    },

    handleDeleteProfileError(error) {
      const statusCode = error.response?.status
      if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    openEditForm() {
      // Perekonnanimi = viimane sõna, eesnimi = kõik enne seda (lubab tühikuga eesnime nagu "Hanna Grete")
      const parts = (this.profile.fullName || '').trim().split(/\s+/).filter(Boolean)
      this.editForm.firstName = parts.length > 1 ? parts.slice(0, -1).join(' ') : parts[0] || ''
      this.editForm.lastName = parts.length > 1 ? parts[parts.length - 1] : ''
      this.editForm.email = this.profile.email || ''
      this.editForm.phone = this.profile.phone || ''
      this.editForm.description = this.profile.description || ''
      this.isEditMode = true
      this.isPasswordMode = false
      this.isDeleteConfirm = false
      this.errorMessage = ''
      this.successMessage = ''
    },

    openPasswordForm() {
      this.passwordForm = { oldPassword: '', newPassword: '', confirmNewPassword: '' }
      this.isPasswordMode = true
      this.isEditMode = false
      this.isDeleteConfirm = false
      this.errorMessage = ''
      this.successMessage = ''
    },

    openDeleteConfirm() {
      this.isDeleteConfirm = true
      this.isEditMode = false
      this.isPasswordMode = false
      this.errorMessage = ''
      this.successMessage = ''
    },
  },
  beforeMount() {
    this.userId = AuthHelper.getUser()?.userId ?? null
    this.getProfile()
  },
}
</script>
