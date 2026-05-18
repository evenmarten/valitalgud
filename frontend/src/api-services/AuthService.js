import axios from 'axios'

export default {
  sendLoginRequest(loginDto) {
    return axios.post('/api/login', loginDto)
  },
}
