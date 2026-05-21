import axios from 'axios'

export default {
  sendContactMessage(contactDto) {
    return axios.post('/api/contact', contactDto)
  },
}
