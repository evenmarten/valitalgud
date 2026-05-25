<template>
  <section class="hero">
    <div class="container py-5 position-relative">
      <div class="row align-items-center g-5">
        <div class="col-lg-5">
          <span class="hero-eyebrow">Sündmused · Talgud · Kogukond</span>
          <h1 class="hero-title">
            Too inimesed
            <span class="highlight highlight-yellow">kokku.</span>
            <br />
            Korralda.
            <span class="highlight highlight-pink">Osale.</span>
          </h1>
          <p class="hero-lead">
            Valitalgud on koht, kus avastad põnevaid sündmusi ja talguid, registreerud
            osalejaks ühe klikiga ning korraldad ise oma üritusi — kõik ühes kohas.
          </p>

          <div class="d-flex flex-wrap gap-3 mb-4">
            <template v-if="isLoggedIn">
              <button class="btn btn-success btn-lg" @click="goToCreateEvent">Loo sündmus</button>
              <button class="btn btn-secondary btn-lg" @click="goToMyEvents">Minu sündmused</button>
            </template>
            <template v-else>
              <button class="btn btn-primary btn-lg" @click="goToRegister">Loo konto</button>
              <button class="btn btn-secondary btn-lg" @click="goToLogin">Logi sisse</button>
            </template>
          </div>

          <div class="d-flex flex-wrap gap-2">
            <span class="pill">Avasta sündmusi</span>
            <span class="pill">Registreeru hetkega</span>
            <span class="pill">Korralda ise</span>
          </div>
        </div>

        <div class="col-lg-7">
          <img :src="heroImage" alt="Valitalgud kogukond" class="hero-img" />
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'LandingHero',
  props: {
    isLoggedIn: {
      type: Boolean,
      default: false,
    },
    heroImage: {
      type: String,
      default: '',
    },
  },
  methods: {
    goToRegister() {
      NavigationService.navigateToRegister()
    },
    goToLogin() {
      NavigationService.navigateToLogin()
    },
    goToCreateEvent() {
      NavigationService.navigateToCreateEvent()
    },
    goToMyEvents() {
      NavigationService.navigateToMyEvents()
    },
  },
}
</script>

<style scoped>
.hero {
  /* Hero hoiab oma soojema bränditooni, samal ajal kui muu leht on rahulik neutraalne */
  background: var(--nb-bg);
  border-bottom: var(--nb-border);
  position: relative;
  overflow: hidden;
}

.hero::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image: radial-gradient(var(--nb-black) 1.6px, transparent 1.6px);
  background-size: 22px 22px;
  opacity: 0.08;
  pointer-events: none;
}

.hero-eyebrow {
  display: inline-block;
  background: var(--nb-blue);
  color: var(--nb-white);
  border: 2px solid var(--nb-black);
  box-shadow: 3px 3px 0 var(--nb-black);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-size: 0.8rem;
  padding: 0.3rem 0.7rem;
  margin-bottom: 1.25rem;
  transform: rotate(-2deg);
}

.hero-title {
  font-size: 3.6rem;
  line-height: 1.02;
  margin-bottom: 1.25rem;
}

.highlight {
  padding: 0 0.25rem;
  box-shadow: 4px 4px 0 var(--nb-black);
}

.highlight-yellow {
  background: var(--nb-yellow);
  color: var(--nb-black);
}

.highlight-pink {
  background: var(--nb-pink);
  color: var(--nb-white);
}

.hero-lead {
  font-size: 1.2rem;
  max-width: 560px;
  margin-bottom: 1.75rem;
}

.pill {
  background: var(--nb-white);
  border: 2px solid var(--nb-black);
  box-shadow: 2px 2px 0 var(--nb-black);
  font-weight: 700;
  font-size: 0.9rem;
  padding: 0.3rem 0.8rem;
}

.hero-img {
  display: block;
  width: 100%;
  height: auto;
}

@media (max-width: 992px) {
  .hero-title {
    font-size: 2.3rem;
  }
}
</style>
