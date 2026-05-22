import { createApp } from 'vue'
import { createPinia } from 'pinia'
import axios from 'axios'

import App from './App.vue'
import router from './router'
import AuthHelper from '@/auth/auth.js'

// Bootstrap (laaditakse ENNE main.css-i, et Neobrutalism teema saaks Bootstrapi üle kirjutada)
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.js'
import 'bootstrap-icons/font/bootstrap-icons.css'
import './assets/main.css'

// Auto-logout: kui mõni päring tagastab 401 (NOT_AUTHENTICATED), siis
// localStorage'is olev kasutaja ei kehti enam (nt konto kustutatud) —
// tühjenda sessioon ja suuna login-i. Login-päring ise jäetakse välja,
// et vale parooli korral näeks LoginView veateadet.
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    const isUnauthorized = error.response?.status === 401
    const isLoginRequest = (error.config?.url ?? '').includes('/api/login')
    if (isUnauthorized && !isLoginRequest && AuthHelper.isLoggedIn()) {
      AuthHelper.clearUser()
      router.push('/login')
    }
    return Promise.reject(error)
  },
)

const app = createApp(App)

app.use(createPinia())
app.use(router)

// Axios globaalselt kättesaadavaks
app.config.globalProperties.$axios = axios

app.mount('#app')
