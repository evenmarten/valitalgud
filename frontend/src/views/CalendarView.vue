<template>
  <div>
    <AppNavbar />

    <div class="container py-4">
      <AlertError :error-message="errorMessage" />

      <div class="calendar-frame">
        <div class="calendar-header">
          <button class="btn btn-outline-secondary btn-sm" @click="prevMonth">&lsaquo; Eelmine</button>
          <h4 class="mb-0">{{ monthName }} {{ currentYear }}</h4>
          <button class="btn btn-outline-secondary btn-sm" @click="nextMonth">Järgmine &rsaquo;</button>
        </div>

        <div class="calendar-weekdays">
          <div v-for="weekDay in weekDays" :key="weekDay" class="weekday-label">
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
              'calendar-cell--today': day && isToday(day),
              'calendar-cell--selected': day && isSelected(day),
              'calendar-cell--has-events': day && daysWithEvents.includes(day),
            }"
            @click="day && selectDay(day)"
          >
            <span v-if="day" class="day-number">{{ day }}</span>
            <span v-if="day && daysWithEvents.includes(day)" class="event-pip"></span>
          </div>
        </div>
      </div>

      <div v-if="selectedDate" class="mt-4">
        <div class="events-section-header">
          <h5 class="mb-0">{{ formattedSelectedDate }}</h5>
          <span v-if="dayEvents.length > 0" class="events-count-badge">{{ dayEvents.length }} sündmust</span>
        </div>

        <div v-if="loadingEvents" class="loading-box">
          <span class="loading-spinner"></span>
          Laen sündmusi...
        </div>

        <div v-else-if="dayEvents.length === 0" class="no-events-box">
          Sel päeval pole sündmusi
        </div>

        <div v-else class="event-btn-list">
          <div
            v-for="event in dayEvents"
            :key="event.eventId"
            class="event-btn-card"
            @click="goToEventDetails(event.eventId)"
          >
            <div class="event-btn-inner">
              <div class="event-btn-left">
                <div class="event-btn-title">{{ event.title }}</div>
                <div class="event-btn-meta">
                  <span v-if="event.startTime">{{ event.startTime }}</span>
                  <span v-if="event.endTime">–{{ event.endTime }}</span>
                  <span v-if="event.city"> · {{ event.city }}</span>
                </div>
                <div v-if="event.description" class="event-btn-desc">{{ event.description }}</div>
              </div>
              <span class="event-btn-arrow">&rarr;</span>
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
      todayDay: new Date().getDate(),
      todayMonth: new Date().getMonth() + 1,
      todayYear: new Date().getFullYear(),
      daysWithEvents: [],
      selectedDate: null,
      dayEvents: [],
      loadingEvents: false,
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
      this.loadingEvents = true
      this.dayEvents = []
      CalendarService.sendGetDayEventsRequest(date, this.userId)
        .then((response) => this.handleGetDayEventsResponse(response.data))
        .catch((error) => this.handleGetDayEventsError(error))
        .finally(() => { this.loadingEvents = false })
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

    isToday(day) {
      return (
        day === this.todayDay &&
        this.currentMonth === this.todayMonth &&
        this.currentYear === this.todayYear
      )
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
/* ---- Kalender raam ---- */
.calendar-frame {
  border: 1px solid var(--corp-line);
  border-radius: var(--corp-radius);
  box-shadow: var(--corp-shadow);
  background: var(--nb-white);
  overflow: hidden;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.9rem 1.2rem;
  border-bottom: 1px solid var(--corp-line);
  background: var(--corp-surface);
}

/* ---- Nädalapäevad ---- */
.calendar-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: var(--corp-blue);
}

.weekday-label {
  text-align: center;
  padding: 0.5rem 0;
  font-weight: 700;
  font-size: 0.8rem;
  text-transform: uppercase;
  color: #fff;
  letter-spacing: 0.5px;
}

/* ---- Kalendriruudustik ---- */
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
}

.calendar-cell {
  min-height: 62px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-right: 1px solid var(--corp-line);
  border-bottom: 1px solid var(--corp-line);
  padding: 6px 4px;
  gap: 4px;
  background: var(--nb-white);
}

.calendar-cell:nth-child(7n) {
  border-right: none;
}

.calendar-cell--active {
  cursor: pointer;
}

.calendar-cell--active:hover {
  background-color: var(--corp-blue-soft);
}

.calendar-cell--today .day-number {
  background-color: var(--corp-blue-soft);
  color: var(--corp-blue);
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--corp-blue);
  border-radius: 50%;
}

.calendar-cell--selected {
  background-color: var(--corp-blue) !important;
  color: #fff;
}

.calendar-cell--selected:hover {
  background-color: #16306e !important;
}

.day-number {
  font-weight: 700;
  font-size: 1rem;
  line-height: 1;
}

/* ---- Sündmusepunkt ---- */
.event-pip {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: var(--corp-blue);
  border: none;
  display: block;
  flex-shrink: 0;
}

.calendar-cell--selected .event-pip {
  background-color: #fff;
  border-color: #fff;
}

/* ---- Sündmuste jaotis ---- */
.events-section-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  background: var(--corp-surface);
  border: 1px solid var(--corp-line);
  border-radius: var(--corp-radius);
  box-shadow: var(--corp-shadow);
  margin-bottom: 1rem;
}

.events-section-header h5 {
  font-family: 'Archivo Black', sans-serif;
  text-transform: none;
  margin: 0;
}

.events-count-badge {
  background: var(--corp-blue);
  color: #fff;
  font-weight: 700;
  font-size: 0.8rem;
  text-transform: uppercase;
  padding: 0.2em 0.6em;
  letter-spacing: 0.3px;
  border-radius: 999px;
}

.loading-box {
  border: 1px solid var(--corp-line);
  border-radius: var(--corp-radius);
  padding: 1.2rem 1rem;
  font-weight: 600;
  color: #444;
  background: var(--nb-white);
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.loading-spinner {
  display: inline-block;
  width: 18px;
  height: 18px;
  border: 3px solid var(--corp-line);
  border-top-color: var(--corp-blue);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  flex-shrink: 0;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.no-events-box {
  border: 1px solid var(--corp-line);
  border-radius: var(--corp-radius);
  padding: 1.2rem 1rem;
  font-weight: 600;
  color: #555;
  background: var(--nb-white);
}

/* ---- Sündmusepupp ---- */
.event-btn-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.event-btn-card {
  border: 1px solid var(--corp-line);
  border-radius: var(--corp-radius);
  box-shadow: var(--corp-shadow);
  background: var(--nb-white);
  cursor: pointer;
  padding: 1rem 1.1rem;
  transition: transform 0.06s ease, box-shadow 0.06s ease;
}

.event-btn-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--corp-shadow-lg);
  background-color: var(--corp-blue-soft);
}

.event-btn-card:active {
  transform: translateY(0);
  box-shadow: var(--corp-shadow);
}

.event-btn-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.event-btn-left {
  flex: 1;
  overflow: hidden;
}

.event-btn-title {
  font-family: 'Archivo Black', sans-serif;
  font-size: 1rem;
  text-transform: none;
  letter-spacing: -0.3px;
  line-height: 1.2;
  margin-bottom: 0.2rem;
}

.event-btn-meta {
  font-size: 0.83rem;
  font-weight: 600;
  color: #444;
  margin-bottom: 0.2rem;
}

.event-btn-desc {
  font-size: 0.83rem;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.event-btn-arrow {
  font-size: 1.5rem;
  font-weight: 700;
  flex-shrink: 0;
  line-height: 1;
}
</style>
