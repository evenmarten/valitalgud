import axios from 'axios'

export default {
  sendGetEventsRequest(filters) {
    return axios.get('/api/events', { params: filters })
  },
}
