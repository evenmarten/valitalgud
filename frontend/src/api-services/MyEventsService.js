import axios from 'axios'

export default {
  sendGetMyEventsRequest(userId, filter) {
    return axios.get('/api/my-events', { params: { userId, filter } })
  },
}
