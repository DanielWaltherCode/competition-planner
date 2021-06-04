<template>
  <div id="competition-header">
    <div>
      <router-link class="text-white fs-5 d-flex p-2" to="/">{{$t("header.overview")}}</router-link>
    </div>
    <div id="nav-top">
      <i class="bi bi-building text-white"></i>
      <h2 class="text-white">{{ competition.name }}</h2>
    </div>
    <header  class="header">
      <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container-fluid">
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarTogglerDemo01"
                  aria-controls="navbarTogglerDemo01" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
          </button>
            <div>
              <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                  <router-link class="nav-link" :to="`/competition/${competition.id}`"
                               :class="$router.currentRoute.path === '/overview' ? 'active' : ''">
                    {{ $t("competition.nav.overview") }}
                  </router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" :to="`/competition/${competition.id}/categories`"
                               :class="$router.currentRoute.path.includes('categories') ? 'active' : ''">
                    {{ $t("competition.nav.categories") }}
                  </router-link>
                </li>
                <li class="nav-item">
                  <router-link class="nav-link" :to="`/competition/${competition.id}/matches`"
                               :class="$router.currentRoute.path.includes('matches') ? 'active' : ''">
                    {{ $t("competition.nav.matches") }}
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
  props: {
    competition: Object
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

#brand {
  color: white;
}

.navbar {
  background-color: var(--main-color) !important;
}

.nav-link {
  color: white !important;
}

.submenu {
  color: var(--main-color) !important;
}

.navbar-collapse {
  justify-content: space-between;
}

#navbar button {
  margin-left: 10px;
}


</style>