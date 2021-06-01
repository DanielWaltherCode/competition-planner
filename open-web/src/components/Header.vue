<template>
  <header id="header" class="header">
    <nav class="navbar navbar-expand-lg navbar-dark">
      <div class="container-fluid">
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarTogglerDemo01" aria-controls="navbarTogglerDemo01" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarTogglerDemo01">
          <div class="d-flex align-items-center">
          <router-link to="landing" :key="$route.fullPath" class="navbar-brand">
            <h2 id="brand">LET'S COMPETE</h2>
          </router-link>
          </div>
          <div>

          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
              <li class="nav-item">
                <router-link class="nav-link" to="/overview"
                   :class="$router.currentRoute.path === '/overview' ? 'active' : ''">
                  {{ $t("header.overview") }}
                </router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/classes"
                             :class="$router.currentRoute.path === '/classes' ? 'active' : ''">
                  {{ $t("header.classes") }}
                </router-link>
              </li>
            <li class="nav-item dropdown" >
              <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                {{$t("header.handle.title")}}
              </a>
              <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                <li class="nav-item">
                  <!-- Note one exclamation point here for competition, all others have two -->
                  <router-link class="nav-link submenu" to="/new-competition"
                     :class="$router.currentRoute.path === '/new-competition' ? 'active' : ''" >
                    {{ $t("header.handle.newCompetition") }}
                  </router-link>
                </li>
                <li class="nav-item">
                  <!-- Note one exclamation point here for competition, all others have two -->
                  <router-link class="nav-link submenu" to="/choose-competition"
                     :class="$router.currentRoute.path === '/choose-competition' ? 'active' : ''">
                    {{ $t("header.handle.administerCompetition") }}
                  </router-link>
                </li>
              </ul>
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
  name: "Header",
  data() {
    return {}
  },
  computed: {

  },
  methods: {

  }
}
</script>

<style scoped>

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
