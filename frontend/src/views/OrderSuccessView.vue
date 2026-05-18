<template>
  <div>
    <AppNavbar />
    <div class="container py-4 text-center">
      <h2>Tellimus edukalt esitatud!</h2>
    </div>
  </div>
</template>

<script>
import AppNavbar from '@/navigation/AppNavbar.vue'

export default {
  name: 'OrderSuccessView',
  components: { AppNavbar },
}
</script>
