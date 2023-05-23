<template>
  <header id="header" class="header">
    <nav class="navbar navbar-expand-lg navbar-light px-2">
      <div class="container-fluid">
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarTogglerDemo01"
                aria-controls="navbarTogglerDemo01" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <router-link to="landing" :key="$route.fullPath" class="navbar-brand">
          <h2 id="brand">CompetitionPlanner</h2>
        </router-link>
        <div class="collapse navbar-collapse" id="navbarTogglerDemo01">
            <div v-if="!!competition" class="text-dark">
              <p class="mb-0 ms-3"> {{ $t("header.chosenCompetition") }} <span
                  class="fw-bolder"> {{ competition.name }}</span></p>
            </div>
            <div class="d-flex align-items-center">
              <ul id="main-ul" class="navbar-nav ms-4 me-1 mb-2 mb-lg-0">
                <li class="nav-item">
                  <router-link class="nav-link" to="/overview" v-if="isLoggedIn && !!competition"
                               :class="$route.path === '/overview' ? 'active ' : ''">
                    {{ $t("header.overview") }}
                  </router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/classes" v-if="isLoggedIn && !!competition"
                               :class="$route.path === '/classes' ? 'active ' : ''">
                    {{ $t("header.classes") }}
                  </router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/players/overview" v-if="isLoggedIn && !!competition"
                               :class="$route.path.includes('/players') ? 'active ' : ''">
                    {{ $t("header.players") }}
                  </router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/draw" v-if="isLoggedIn && !!competition"
                               :class="$route.path === '/draw' ? 'active ' : ''">
                    {{ $t("header.draws") }}
                  </router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/schedule" v-if="isLoggedIn && !!competition"
                               :class="$route.path === '/schedule' ? 'active ' : ''">
                    {{ $t("header.schedule") }}
                  </router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/schedule-manual" v-if="isLoggedIn && !!competition"
                               :class="$route.path === '/schedule-manual' ? 'active ' : ''">
                    {{ $t("header.scheduleManual") }}
                  </router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/results" v-if="isLoggedIn && !!competition"
                               :class="$route.path === '/results' ? 'active ' : ''">
                    {{ $t("header.results") }}
                  </router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/billing" v-if="isLoggedIn && !!competition"
                               :class="$route.path === '/billing' ? 'active ' : ''">
                    {{ $t("header.billing") }}
                  </router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" to="/externalRegistration" v-if="isLoggedIn & !competition"
                               :class="$route.path === '/externalRegistration' ? 'active ' : ''">
                    {{ $t("header.externalRegistration") }}
                  </router-link>
                </li>

                <li class="nav-item">
                  <router-link class="nav-link" to="/admin" v-if="(isLoggedIn && isAdmin)"
                               :class="$route.path === '/admin' ? 'active ' : ''">
                    {{ $t("header.admin") }}
                  </router-link>
                </li>
                <li class="nav-item ms-2 d-flex justify-content-end" v-if="isLoggedIn">
                  <button class="btn btn-light" @click="logout"> {{ $t("header.logout") }}</button>
                </li>
              </ul>
            </div>
        </div>
      </div>
    </nav>
  </header>
</template>

<script>

export default {
  name: "NavHeader",
  data() {
    return {}
  },
  computed: {
    isLoggedIn: function () {
      return this.$store.getters.isLoggedIn
    },
    user: function () {
      return this.$store.getters.user.username
    },
    competition: function () {
      return this.$store.getters.competition
    },
    isAdmin: function () {
      return this.$store.getters.isAdmin
    }
  },
  methods: {
    logout() {
      this.$store.commit("logout")
      if (!this.$route.path.includes("landing")) {
        this.$router.push('/landing')
      }
    },
  }
}
</script>

<style scoped>

.navbar-collapse {
  justify-content: space-between;
}

#navbar button {
  margin-left: 10px;
}

/*@media screen and (min-width: 700px) {
    #main-ul > li {
      display: flex;
      align-items: center;
    }
}*/


</style>
