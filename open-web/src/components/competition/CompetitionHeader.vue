<template>
  <div id="competition-header">
    <div id="nav-top" class="d-flex align-items-center justify-content-center pt-3">
      <i class="fas fa-tag fa-2x text-white me-2"></i>
      <h2 class="text-white">{{ competition.name }}</h2>
    </div>
    <header class="header">
      <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container-fluid">
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#subNav"
                  aria-controls="subNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
          </button>
            <div class="collapse navbar-collapse" id="subNav">
              <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                  <router-link class="nav-link" :to="`/competition/${competition.id}/landing`"
                               :class="activeRoute.includes('/landing') ? 'active' : ''">
                    {{ $t("competition.nav.overview") }}
                  </router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" :to="`/competition/${competition.id}/categories`"
                               :class="activeRoute.includes('categories') ? 'active' : ''">
                    {{ $t("competition.nav.categories") }}
                  </router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" :to="`/competition/${competition.id}/matches`"
                               :class="activeRoute.includes('matches') ? 'active' : ''">
                    {{ $t("competition.nav.matches") }}
                  </router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" :to="`/competition/${competition.id}/players`"
                               :class="activeRoute.includes('players') ? 'active' : ''">
                    {{ $t("competition.nav.players") }}
                  </router-link>
                </li>
              </ul>
          </div>
        </div>
      </nav>
    </header>
  </div>
</template>

<script>
export default {
  name: "CompetitionHeader",
  data() {
    return {
      activeRoute: ""
    }
  },
  props: {
    competition: Object,
  },
  watch: {
    $route() {
      this.activeRoute = this.$router.currentRoute.path
    }
  }
}
</script>

<style scoped>

#competition-header {
  background-color: var(--main-color);
}

nav {
  box-shadow: 0 3px #efefef;
}

.navbar {
  background-color: var(--main-color) !important;
}

.nav-link {
  color: white !important;
}

.active {
  font-weight: bold;
}

#navbar button {
  margin-left: 10px;
}


</style>