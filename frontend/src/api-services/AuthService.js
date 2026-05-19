import axios from 'axios'

export default {
  sendLoginRequest(loginDto) {
    return axios.post('/api/login', loginDto)
  },

  sendRegisterRequest(registerDto) {
    return axios.post('/api/register', registerDto)
  },
}
