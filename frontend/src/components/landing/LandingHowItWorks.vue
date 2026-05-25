<template>
  <section class="section section-blue">
    <div class="container">
      <div class="text-center mb-5">
        <h2 class="section-title">Kuidas see toimib</h2>
        <p class="section-subtitle">Kolm sammu kogukonnaüritusteni</p>
      </div>

      <div class="row g-4">
        <div v-for="step in steps" :key="step.number" class="col-md-4">
          <div class="card lift-card h-100">
            <div class="card-body">
              <div class="step-number" :class="step.color">{{ step.number }}</div>
              <h5 class="card-title mb-2">{{ step.title }}</h5>
              <p class="mb-0">{{ step.text }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
export default {
  name: 'LandingHowItWorks',
  props: {
    steps: {
      type: Array,
      default: () => [],
    },
  },
}
</script>

<style scoped>
.step-number {
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Archivo Black', sans-serif;
  font-size: 1.35rem;
  color: var(--nb-white);
  border-radius: 50%;
  margin-bottom: 1.1rem;
}

.step-blue { background: var(--corp-blue); }
.step-pink { background: var(--corp-teal); }
.step-green { background: #2563eb; color: var(--nb-white); }
</style>
