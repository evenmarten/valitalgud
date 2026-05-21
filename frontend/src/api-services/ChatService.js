import axios from 'axios'

export default {
  sendChatMessage(message, history) {
    return axios.post('/api/chat', { message, history })
  },
}
