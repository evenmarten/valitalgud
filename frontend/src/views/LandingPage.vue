<template>
  <div>
    <AppNavbar />

    <!-- ===== HERO ===== -->
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
                <button class="btn btn-primary btn-lg" @click="goToRegister">Loo tasuta konto</button>
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

    <!-- ===== KUIDAS SEE TOIMIB ===== -->
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

    <!-- ===== KAHELE SIHTRÜHMALE ===== -->
    <section class="section">
      <div class="container">
        <div class="row g-4">
          <div class="col-md-6">
            <div class="card lift-card h-100 audience-card audience-blue">
              <div class="card-body">
                <span class="audience-tag">Osalejatele</span>
                <h3 class="audience-heading">Leia oma järgmine sündmus</h3>
                <ul class="feature-list">
                  <li v-for="feature in participantFeatures" :key="feature">{{ feature }}</li>
                </ul>
                <button v-if="!isLoggedIn" class="btn btn-primary mt-2" @click="goToRegister">
                  Liitu kogukonnaga
                </button>
                <button v-else class="btn btn-primary mt-2" @click="browseEvents">
                  Sirvi sündmusi
                </button>
              </div>
            </div>
          </div>

          <div class="col-md-6">
            <div class="card lift-card h-100 audience-card audience-pink">
              <div class="card-body">
                <span class="audience-tag">Korraldajatele</span>
                <h3 class="audience-heading">Korralda oma üritus</h3>
                <ul class="feature-list">
                  <li v-for="feature in organizerFeatures" :key="feature">{{ feature }}</li>
                </ul>
                <button v-if="!isLoggedIn" class="btn btn-danger mt-2" @click="goToRegister">
                  Alusta korraldamist
                </button>
                <button v-else class="btn btn-danger mt-2" @click="goToCreateEvent">
                  Loo sündmus
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== EELSEISVAD SÜNDMUSED ===== -->
    <section class="section section-yellow">
      <div class="container">
        <div class="d-flex flex-wrap justify-content-between align-items-end mb-4 gap-3">
          <div>
            <h2 class="section-title mb-1">Tutvu eelseisvate sündmustega</h2>
            <p class="section-subtitle mb-0">Näide sellest, mis kogukonnas toimub</p>
          </div>
          <button class="btn btn-dark" @click="browseEvents">
            {{ isLoggedIn ? 'Vaata kõiki sündmusi' : 'Loo konto ja osale' }}
          </button>
        </div>

        <div class="row g-4">
          <div
            v-for="event in demoEvents"
            :key="event.eventId"
            class="col-sm-6 col-md-4"
          >
            <div class="card lift-card h-100">
              <div class="card-img-top demo-banner" :style="bannerStyle(event)">
                <img :src="event.image" :alt="event.title" class="demo-banner-img" />
              </div>

              <div class="card-body d-flex flex-column">
                <h5 class="card-title">{{ event.title }}</h5>
                <p class="text-muted small mb-2">
                  {{ event.eventDate }} · {{ event.city }}
                </p>
                <p class="card-text text-truncate-3">{{ event.description }}</p>

                <div class="mb-2">
                  <span
                    v-for="tag in event.skillTags"
                    :key="tag"
                    class="badge bg-info text-dark me-1"
                  >{{ tag }}</span>
                </div>

                <p class="text-muted small mb-0 mt-auto">
                  Osalejaid: {{ event.currentParticipants }} / {{ event.maxParticipants }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== E-POOD ===== -->
    <section class="section section-alt">
      <div class="container">
        <div class="text-center mb-5">
          <h2 class="section-title">Tutvu meie e-poega</h2>
          <p class="section-subtitle">Toeta kogukonda ja kanna seda uhkusega</p>
        </div>

        <div class="row g-4">
          <div
            v-for="product in demoProducts"
            :key="product.productId"
            class="col-sm-6 col-md-4"
          >
            <div class="card h-100 shop-card" @click="goToShop">
              <div class="card-img-top demo-banner" :style="bannerStyle(product)">
                <img :src="product.image" :alt="product.name" class="demo-banner-img" />
              </div>

              <div class="card-body d-flex flex-column">
                <h5 class="card-title">{{ product.name }}</h5>
                <p class="text-muted small mb-2">{{ product.category }}</p>
                <p class="card-text text-truncate-3">{{ product.description }}</p>

                <p class="fw-bold fs-5 mb-0 mt-auto">{{ product.price }} €</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== LÕPP-CTA ===== -->
    <section class="section">
      <div class="container">
        <div class="cta-banner">
          <template v-if="isLoggedIn">
            <h2 class="cta-title">Korralda oma esimene sündmus</h2>
            <p class="cta-text">Loo sündmus või talgud ja too kogukond kokku juba täna.</p>
            <button class="btn btn-secondary btn-lg" @click="goToCreateEvent">Loo sündmus</button>
          </template>
          <template v-else>
            <h2 class="cta-title">Valmis alustama?</h2>
            <p class="cta-text">Loo tasuta konto ja avasta sündmusi, mis su kogukonda elavdavad.</p>
            <button class="btn btn-secondary btn-lg" @click="goToRegister">Loo tasuta konto</button>
          </template>
        </div>
      </div>
    </section>

    <!-- ===== AI VESTLUSROBOT (ainult landing lehel) ===== -->
    <ChatbotWidget />
  </div>
</template>

<script>
import AppNavbar from '@/navigation/AppNavbar.vue'
import ChatbotWidget from '@/components/common/ChatbotWidget.vue'
import AuthHelper from '@/auth/auth.js'
import NavigationService from '@/navigation/NavigationService.js'
import heroImage from '@/assets/hero/hero.png'

export default {
  name: 'LandingPage',
  components: { AppNavbar, ChatbotWidget },
  data() {
    return {
      isLoggedIn: false,
      heroImage,
      steps: [
        {
          number: 1,
          title: 'Avasta',
          text: 'Sirvi eelseisvaid sündmusi ja talguid. Leia linna ja oskuste järgi see, mis sind kõnetab.',
          color: 'step-blue',
        },
        {
          number: 2,
          title: 'Liitu',
          text: 'Märgi end osalejaks ühe klikiga — lähen, võib-olla või ei lähe. Kõik valikud koonduvad kalendrisse.',
          color: 'step-pink',
        },
        {
          number: 3,
          title: 'Korralda',
          text: 'Loo ise sündmus või talgud, kutsu kogukond kokku ja jälgi osalejaid reaalajas.',
          color: 'step-green',
        },
      ],
      participantFeatures: [
        'Avasta sündmusi ja talguid linna ning oskuste järgi',
        'Registreeru ühe klikiga: lähen, võib-olla või ei lähe',
        'Hoia kõik oma sündmused ühes kalendris koos',
        'Aruta ja küsi küsimusi sündmuse kommentaarides',
      ],
      organizerFeatures: [
        'Loo sündmus mugava sammhaaval vormiga',
        'Lisa kirjeldus, asukoht, oskuse-tagid ja banner',
        'Halda osalejaid ja jälgi huvi reaalajas',
        'Muuda või tühista oma sündmusi igal ajal',
      ],
      demoEvents: [
        {
          eventId: 'demo-1',
          title: 'Suur Tehnoloogiakonverents',
          description: 'Aastane konverents, mis toob kokku tehnoloogiamaailma tipud, idufirmade asutajad ja arendajad. Loengud, töötoad ja võrgustumine.',
          eventDate: '26.10.2026',
          city: 'Tallinn',
          bannerColor: 'linear-gradient(135deg, #4f46e5 0%, #06b6d4 100%)',
          image: 'https://picsum.photos/seed/tehnokonverents/600/360',
          skillTags: ['IT', 'JavaScript'],
          currentParticipants: 87,
          maxParticipants: 100,
        },
        {
          eventId: 'demo-2',
          title: 'Pärnu Jazz Festival',
          description: 'Kolmepäevane jazzmuusika festival kuulsate artistidega rannapargis. Live esinemised, toidualad ja meeleolukad õhtud.',
          eventDate: '15.07.2026',
          city: 'Pärnu',
          bannerColor: 'linear-gradient(135deg, #f59e0b 0%, #ef4444 100%)',
          image: 'https://picsum.photos/seed/jazzfestival/600/360',
          skillTags: ['Muusika'],
          currentParticipants: 142,
          maxParticipants: 200,
        },
        {
          eventId: 'demo-3',
          title: 'Tartu Maraton',
          description: 'Traditsiooniline maraton nii profidele kui harrastajatele. Erinevad distantsid, soe vastuvõtt ja ilus rada läbi linna.',
          eventDate: '03.09.2026',
          city: 'Tartu',
          bannerColor: 'linear-gradient(135deg, #10b981 0%, #3b82f6 100%)',
          image: 'https://picsum.photos/seed/maraton/600/360',
          skillTags: ['Sport'],
          currentParticipants: 318,
          maxParticipants: 500,
        },
      ],
      demoProducts: [
        {
          productId: 'demo-product-1',
          name: 'Ürituse T-särk',
          description: 'Pehme puuvillane T-särk valitalgud logoga. Sobib nii üritusele kui igapäevaseks kandmiseks.',
          category: 'Riided',
          price: '19.90',
          bannerColor: 'linear-gradient(135deg, #ec4899 0%, #8b5cf6 100%)',
          image: '/images/products/t-shirt.jpg',
        },
        {
          productId: 'demo-product-2',
          name: 'Joogipudel',
          description: 'Roostevabast terasest joogipudel mahuga 500 ml. Hoia jook käeulatuses kogu ürituse vältel.',
          category: 'Jooginõud',
          price: '15.99',
          bannerColor: 'linear-gradient(135deg, #0ea5e9 0%, #6366f1 100%)',
          image: '/images/products/water-bottle.jpg',
        },
        {
          productId: 'demo-product-3',
          name: 'Seljakott',
          description: 'Kerge ja vastupidav seljakott igapäevaseks kasutuseks ning matkadeks.',
          category: 'Reisitarbed',
          price: '45.00',
          bannerColor: 'linear-gradient(135deg, #f97316 0%, #eab308 100%)',
          image: '/images/products/seljakott1.jpg',
        },
      ],
    }
  },
  methods: {
    bannerStyle(item) {
      return {
        background: item.bannerColor,
      }
    },
    browseEvents() {
      if (this.isLoggedIn) {
        NavigationService.navigateToEvents()
      } else {
        NavigationService.navigateToRegister()
      }
    },
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
    goToShop() {
      NavigationService.navigateToShop()
    },
  },
  beforeMount() {
    this.isLoggedIn = AuthHelper.isLoggedIn()
  },
}
</script>

<style scoped>
/* ---------- Hero ---------- */
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

/* ============================================================
   SISUSEKTSIOONID — rahulik korporatiivne (Corporate) toon.
   Hero, navbar ja footer jäävad neobrutalistlikuks; siin on
   pehme lõuend, mida elavdavad üksikud julged aktsendid (nupud,
   lõpu-CTA). Kõik allolev on scoped — globaalset teemat ei muudeta.
   ============================================================ */
.section {
  /* Korporatiivne palett, mis kehtib ainult sisusektsioonides */
  --corp-blue: #1e3a8a;
  --corp-blue-soft: #eef2fb;
  --corp-teal: #0e7490;
  --corp-ink: #1f2937;
  --corp-muted: #6b7280;
  --corp-line: #e5e7eb;
  --corp-surface: #f4f6fa;
  --corp-shadow: 0 1px 2px rgba(16, 24, 40, 0.04), 0 10px 30px rgba(16, 24, 40, 0.06);
  --corp-shadow-lg: 0 8px 16px rgba(16, 24, 40, 0.08), 0 22px 48px rgba(16, 24, 40, 0.12);

  padding: 4.5rem 0;
  background: var(--nb-white);
  border-top: 1px solid var(--corp-line);
}

/* Vahelduvad pehmed taustad annavad rütmi ilma valju värviplokita */
.section-blue,
.section-yellow {
  background: var(--corp-surface);
}

.section-alt {
  background: var(--nb-white);
}

/* Pehme hierarhia: tume pealkiri, summutatud alapealkiri */
.section-title {
  font-size: 2.1rem;
  margin-bottom: 0.4rem;
  color: var(--corp-ink);
}

.section-subtitle {
  font-size: 1.1rem;
  color: var(--corp-muted);
  font-weight: 500;
}

/* Kaardid: pehmed ääred, ümarad nurgad, hajus vari (mitte kõva nihe) */
.section .card {
  border: 1px solid var(--corp-line) !important;
  border-radius: 14px !important;
  box-shadow: var(--corp-shadow);
  background-color: var(--nb-white);
  overflow: hidden;
}

.section .card-title {
  color: var(--corp-ink);
}

.section .card-img-top {
  border-bottom: 1px solid var(--corp-line);
}

/* Skill-tagid → pehmed pill-sildid */
.section .badge {
  border: none !important;
  border-radius: 999px !important;
  background-color: var(--corp-blue-soft) !important;
  color: var(--corp-blue) !important;
  text-transform: none !important;
  letter-spacing: 0 !important;
  font-weight: 600;
  padding: 0.4em 0.85em;
}

/* Nupud jäävad julgeks (sild neobrutalismi juurde), kuid värv on rahulik */
.section .btn-primary {
  background-color: var(--corp-blue) !important;
}

.section .btn-danger {
  background-color: var(--corp-teal) !important;
}

/* ---------- Kaartide pehme hõljutus ---------- */
.lift-card {
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.lift-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--corp-shadow-lg) !important;
}

/* ---------- Sammud: pehmed ümarad numbrid ---------- */
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

/* ---------- Sihtrühma kaardid: peen ülaserv aktsendina ---------- */
.section .audience-card.audience-blue {
  border-top: 4px solid var(--corp-blue) !important;
}

.section .audience-card.audience-pink {
  border-top: 4px solid var(--corp-teal) !important;
}

.audience-card .card-body {
  padding: 2rem;
}

.audience-tag {
  display: inline-block;
  background: var(--corp-blue-soft);
  color: var(--corp-blue);
  border-radius: 999px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  font-size: 0.74rem;
  padding: 0.3rem 0.85rem;
  margin-bottom: 0.85rem;
}

.audience-heading {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: var(--corp-ink);
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0 0 1.5rem;
}

.feature-list li {
  position: relative;
  padding-left: 1.7rem;
  margin-bottom: 0.65rem;
  font-weight: 500;
  color: var(--corp-ink);
}

.feature-list li::before {
  content: '✓';
  position: absolute;
  left: 0;
  font-weight: 900;
  color: var(--corp-blue);
}

/* ---------- Sündmuste / poe kaardid ---------- */
.demo-banner {
  height: 180px;
  overflow: hidden;
}

.demo-banner-img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.text-truncate-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.shop-card {
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.shop-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--corp-shadow-lg) !important;
}

/* ---------- Lõpp-CTA: rahulik korporatiivne paneel ---------- */
.cta-banner {
  background: linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%);
  border: none;
  border-radius: 18px;
  box-shadow: 0 20px 45px rgba(30, 58, 138, 0.28);
  color: var(--nb-white);
  text-align: center;
  padding: 3.25rem 1.5rem;
}

.cta-title {
  color: var(--nb-white);
  font-size: 2.1rem;
  margin-bottom: 0.75rem;
}

.cta-text {
  font-size: 1.15rem;
  max-width: 560px;
  margin: 0 auto 1.75rem;
  opacity: 0.92;
}

@media (max-width: 992px) {
  .hero-title {
    font-size: 2.3rem;
  }
}
</style>
