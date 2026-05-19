import axios from 'axios'

export default {
  sendGetMyOrganizedEventsRequest(userId, filters) {
    return axios.get('/api/my-organized-events', { params: { userId, ...filters } })
  },
}
