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

  sendGetEventForEditRequest(eventId, userId) {
    return axios.get(`/api/events/${eventId}/edit`, { params: { userId } })
  },

  sendCreateEventRequest(createEventDto, userId) {
    return axios.post('/api/events', createEventDto, { params: { userId } })
  },

  sendUpdateEventRequest(eventId, updateEventDto, userId) {
    return axios.put(`/api/events/${eventId}`, updateEventDto, { params: { userId } })
  },

  sendDeleteEventRequest(eventId, userId) {
    return axios.delete(`/api/events/${eventId}`, { params: { userId } })
  },
}
