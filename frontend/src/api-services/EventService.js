import axios from 'axios'

export default {
  sendGetEventsRequest(filters) {
    return axios.get('/api/events', { params: filters })
  },

  sendGetEventDetailsRequest(eventId, userId) {
    return axios.get(`/api/events/${eventId}`, { params: { userId } })
  },

  sendRegisterRequest(eventId, userId, registrationDto) {
    return axios.post(`/api/events/${eventId}/register`, registrationDto, { params: { userId } })
  },

  sendCancelRegistrationRequest(eventId, userId) {
    return axios.delete(`/api/events/${eventId}/register`, { params: { userId } })
  },

  sendGetCommentsRequest(eventId, userId) {
    return axios.get(`/api/events/${eventId}/comments`, { params: { userId } })
  },

  sendAddCommentRequest(eventId, userId, createCommentDto) {
    return axios.post(`/api/events/${eventId}/comments`, createCommentDto, { params: { userId } })
  },
}
