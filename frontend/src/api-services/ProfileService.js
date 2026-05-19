import axios from 'axios'

export default {
  sendGetProfileRequest(userId, requesterId) {
    return axios.get(`/api/profile/${userId}`, { params: { requesterId } })
  },

  sendUpdateProfileRequest(userId, requesterId, updateProfileDto) {
    return axios.put(`/api/profile/${userId}`, updateProfileDto, { params: { requesterId } })
  },

  sendChangePasswordRequest(userId, requesterId, changePasswordDto) {
    return axios.put(`/api/profile/${userId}/password`, changePasswordDto, { params: { requesterId } })
  },

  sendDeleteProfileRequest(userId, requesterId) {
    return axios.delete(`/api/profile/${userId}`, { params: { requesterId } })
  },
}
