<template>
  <div>
    <AppNavbar />

    <div class="container py-4">
      <AlertError :error-message="errorMessage" />

      <div class="d-flex justify-content-between align-items-center mb-3">
        <button class="btn btn-outline-secondary" @click="prevMonth">&lsaquo; Eelmine</button>
        <h4 class="mb-0">{{ monthName }} {{ currentYear }}</h4>
        <button class="btn btn-outline-secondary" @click="nextMonth">Järgmine &rsaquo;</button>
      </div>

      <div class="calendar-grid mb-1">
        <div v-for="weekDay in weekDays" :key="weekDay" class="text-center fw-bold text-muted small py-1">
          {{ weekDay }}
        </div>
      </div>

      <div class="calendar-grid">
        <div
          v-for="(day, index) in calendarGrid"
          :key="index"
          class="calendar-cell"
          :class="{
            'calendar-cell--active': !!day,
            'calendar-cell--selected': day && isSelected(day),
            'calendar-cell--has-events': day && daysWithEvents.includes(day),
          }"
          @click="day && selectDay(day)"
        >
          <span v-if="day">{{ day }}</span>
          <span v-if="day && daysWithEvents.includes(day)" class="event-dot"></span>
        </div>
      </div>

      <div v-if="selectedDate" class="mt-4">
        <h5 class="mb-3">{{ formattedSelectedDate }} sündmused</h5>

        <div v-if="dayEvents.length === 0" class="text-muted py-2">
          Sel päeval pole sündmusi
        </div>

        <div v-for="event in dayEvents" :key="event.eventId" class="card mb-2">
          <div class="card-body py-2">
            <div class="d-flex justify-content-between align-items-start gap-3">
              <div class="flex-grow-1 overflow-hidden">
                <h6 class="card-title mb-1">{{ event.title }}</h6>
                <p class="text-muted small mb-1">
                  {{ event.startTime }}<span v-if="event.endTime">–{{ event.endTime }}</span>
                  <span v-if="event.city"> · {{ event.city }}</span>
                </p>
                <p v-if="event.description" class="card-text small text-truncate-2 mb-0">
                  {{ event.description }}
                </p>
              </div>
              <button
                class="btn btn-outline-primary btn-sm flex-shrink-0"
                @click="goToEventDetails(event.eventId)"
              >
                Näita rohkem
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AppNavbar from '@/navigation/AppNavbar.vue'
import AlertError from '@/components/common/AlertError.vue'
import CalendarService from '@/api-services/CalendarService.js'
import NavigationService from '@/navigation/NavigationService.js'
import AuthHelper from '@/auth/auth.js'

const MONTH_NAMES = [
  'Jaanuar', 'Veebruar', 'Märts', 'Aprill', 'Mai', 'Juuni',
  'Juuli', 'August', 'September', 'Oktoober', 'November', 'Detsember',
]

export default {
  name: 'CalendarView',
  components: { AppNavbar, AlertError },
  data() {
    return {
      currentMonth: new Date().getMonth() + 1,
      currentYear: new Date().getFullYear(),
      daysWithEvents: [],
      selectedDate: null,
      dayEvents: [],
      errorMessage: '',
      userId: null,
      weekDays: ['E', 'T', 'K', 'N', 'R', 'L', 'P'],
    }
  },
  computed: {
    monthName() {
      return MONTH_NAMES[this.currentMonth - 1]
    },

    calendarGrid() {
      const firstDayOfWeek = new Date(this.currentYear, this.currentMonth - 1, 1).getDay()
      const daysInMonth = new Date(this.currentYear, this.currentMonth, 0).getDate()
      const startOffset = (firstDayOfWeek + 6) % 7

      const grid = []
      for (let i = 0; i < startOffset; i++) {
        grid.push(null)
      }
      for (let d = 1; d <= daysInMonth; d++) {
        grid.push(d)
      }
      while (grid.length % 7 !== 0) {
        grid.push(null)
      }
      return grid
    },

    formattedSelectedDate() {
      if (!this.selectedDate) return ''
      const [year, month, day] = this.selectedDate.split('-')
      return `${day}.${month}.${year}`
    },
  },
  methods: {
    getCalendar() {
      CalendarService.sendGetCalendarRequest(this.currentMonth, this.currentYear, this.userId)
        .then((response) => this.handleGetCalendarResponse(response.data))
        .catch((error) => this.handleGetCalendarError(error))
        .finally()
    },

    handleGetCalendarResponse(data) {
      this.daysWithEvents = data.daysWithEvents
    },

    handleGetCalendarError(error) {
      const statusCode = error.response?.status
      if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    getDayEvents(date) {
      CalendarService.sendGetDayEventsRequest(date, this.userId)
        .then((response) => this.handleGetDayEventsResponse(response.data))
        .catch((error) => this.handleGetDayEventsError(error))
        .finally()
    },

    handleGetDayEventsResponse(events) {
      this.dayEvents = events
    },

    handleGetDayEventsError(error) {
      const statusCode = error.response?.status
      if (statusCode === 401) {
        NavigationService.navigateToUnauthorized()
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    selectDay(day) {
      this.selectedDate = this.buildDateString(day)
      this.getDayEvents(this.selectedDate)
    },

    prevMonth() {
      if (this.currentMonth === 1) {
        this.currentMonth = 12
        this.currentYear--
      } else {
        this.currentMonth--
      }
      this.selectedDate = null
      this.dayEvents = []
      this.getCalendar()
    },

    nextMonth() {
      if (this.currentMonth === 12) {
        this.currentMonth = 1
        this.currentYear++
      } else {
        this.currentMonth++
      }
      this.selectedDate = null
      this.dayEvents = []
      this.getCalendar()
    },

    goToEventDetails(eventId) {
      NavigationService.navigateToEventDetails(eventId)
    },

    isSelected(day) {
      return this.selectedDate === this.buildDateString(day)
    },

    buildDateString(day) {
      const mm = String(this.currentMonth).padStart(2, '0')
      const dd = String(day).padStart(2, '0')
      return `${this.currentYear}-${mm}-${dd}`
    },
  },
  beforeMount() {
    this.userId = AuthHelper.getUser()?.userId ?? null
    this.getCalendar()
  },
}
</script>

<style scoped>
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 3px;
}

.calendar-cell {
  min-height: 52px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1px solid transparent;
  border-radius: 6px;
  padding: 4px 2px;
  gap: 3px;
}

.calendar-cell--active {
  border-color: #dee2e6;
  cursor: pointer;
}

.calendar-cell--active:hover {
  background-color: #f8f9fa;
}

.calendar-cell--selected {
  background-color: #0d6efd;
  border-color: #0d6efd;
  color: white;
}

.calendar-cell--selected:hover {
  background-color: #0b5ed7;
}

.event-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: #0d6efd;
  display: block;
}

.calendar-cell--selected .event-dot {
  background-color: white;
}

.text-truncate-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
