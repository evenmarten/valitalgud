import axios from 'axios'

export default {
  sendGetCalendarRequest(month, year, userId) {
    return axios.get('/api/calendar', { params: { month, year, userId } })
  },

  sendGetDayEventsRequest(date, userId) {
    return axios.get('/api/calendar/day', { params: { date, userId } })
  },
}
