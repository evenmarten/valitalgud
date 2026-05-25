<template>
  <div class="landing-page">
    <AppNavbar />

    <LandingHero :is-logged-in="isLoggedIn" :hero-image="heroImage" />

    <LandingHowItWorks :steps="steps" />

    <LandingAudience
      :is-logged-in="isLoggedIn"
      :participant-features="participantFeatures"
      :organizer-features="organizerFeatures"
    />

    <LandingEvents :events="demoEvents" />

    <LandingShop :products="demoProducts" />

    <LandingCta :is-logged-in="isLoggedIn" />
  </div>
</template>

<script>
import '@/assets/landing.css'
import AppNavbar from '@/navigation/AppNavbar.vue'
import AuthHelper from '@/auth/auth.js'
import LandingHero from '@/components/landing/LandingHero.vue'
import LandingHowItWorks from '@/components/landing/LandingHowItWorks.vue'
import LandingAudience from '@/components/landing/LandingAudience.vue'
import LandingEvents from '@/components/landing/LandingEvents.vue'
import LandingShop from '@/components/landing/LandingShop.vue'
import LandingCta from '@/components/landing/LandingCta.vue'
import heroImage from '@/assets/hero/hero.png'

export default {
  name: 'LandingPage',
  components: {
    AppNavbar,
    LandingHero,
    LandingHowItWorks,
    LandingAudience,
    LandingEvents,
    LandingShop,
    LandingCta,
  },
  data() {
    return {
      isLoggedIn: false,
      heroImage,
      steps: [
        {
          number: 1,
          title: 'Avasta',
          text: 'Sirvi eelseisvaid sündmusi ja talguid. Leia, mis sind kõnetab.',
          color: 'step-blue',
        },
        {
          number: 2,
          title: 'Liitu',
          text: 'Märgi end osalejaks ühe klikiga ja jälgi üritusi enda kalendris.',
          color: 'step-pink',
        },
        {
          number: 3,
          title: 'Korralda',
          text: 'Loo ise sündmus või talgud.',
          color: 'step-green',
        },
      ],
      participantFeatures: [
        'Avasta sündmusi ja talguid linna ning oskuste järgi',
        'Registreeru ühe klikiga',
        'Jälgi kõiki sündmsui enda kalendris',
      ],
      organizerFeatures: [
        'Loo sündmus mugavalt',
        'Lisa kirjeldus, asukoht, oskuse-tagid ja banner',
        'Jälgi oma loodud sündmusi mugavalt eraldi vaatest',
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
          name: 'Nokamüts',
          description: 'Reguleeritava rihmaga nokamüts Valitalgud logoga — kaitseb päikese eest talgupäeval.',
          category: 'Aksessuaarid',
          price: '18.00',
          image: '/images/products/cap.png',
        },
        {
          productId: 'demo-product-2',
          name: 'Lauamatt',
          description: 'Suur lauamatt Valitalgud logoga — sile pind hiirele ja klaviatuurile, korrastab töölaua.',
          category: 'Aksessuaarid',
          price: '24.00',
          image: '/images/products/mat.png',
        },
        {
          productId: 'demo-product-3',
          name: 'Seljakott',
          description: 'Vastupidav ja avar seljakott talgutarvikute ja varustuse kandmiseks.',
          category: 'Aksessuaarid',
          price: '45.00',
          image: '/images/products/bag.png',
        },
      ],
    }
  },
  beforeMount() {
    this.isLoggedIn = AuthHelper.isLoggedIn()
  },
}
</script>
