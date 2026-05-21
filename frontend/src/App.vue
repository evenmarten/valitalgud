<template>
  <div class="page-wrapper">
    <router-view />
  </div>
  <AppFooter />
</template>

<script>
import AppFooter from '@/navigation/AppFooter.vue'

export default {
  name: 'App',
  components: { AppFooter },
}
</script>
