import axios from 'axios'

export default {
  sendGetMyEventsRequest(userId, filter, extraParams = {}) {
    return axios.get('/api/my-events', { params: { userId, filter, ...extraParams } })
  },
}
