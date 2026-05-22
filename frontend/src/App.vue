<template>
  <div class="page-wrapper">
    <router-view />
  </div>
  <AppFooter />
  <!-- AI vestlusrobot Pam — kättesaadav igal lehel -->
  <ChatbotWidget />
</template>

<script>
import AppFooter from '@/navigation/AppFooter.vue'
import ChatbotWidget from '@/components/common/ChatbotWidget.vue'

export default {
  name: 'App',
  components: { AppFooter, ChatbotWidget },
}
</script>
