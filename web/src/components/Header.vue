<template>
  <header>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <div class="container-fluid">
        <div id="banner">
          <router-link id="main" to="landing" :key="$route.fullPath" class="navbar-brand">
            Let's Compete!
          </router-link>
        </div>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
          <ul class="navbar-nav mr-auto">
            <li class="nav-item">
              <!-- Note one exclamation point here for competition, all others have two -->
              <router-link class="nav-link" to="/new-competition" v-if="isLoggedIn && !competition">
                {{ $t("header.newCompetition") }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/overview" v-if="isLoggedIn && !!competition">
                {{ $t("header.overview") }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/classes" v-if="isLoggedIn && !!competition">
                {{ $t("header.classes") }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/players" v-if="isLoggedIn && !!competition">
                {{ $t("header.players") }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/draw" v-if="isLoggedIn && !!competition">
                {{ $t("header.draws") }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/schedule" v-if="isLoggedIn && !!competition">
                {{ $t("header.schedule") }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/results" v-if="isLoggedIn && !!competition">
                {{ $t("header.results") }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/billing" v-if="isLoggedIn && !!competition">
                {{ $t("header.billing") }}
              </router-link>
            </li>
            <li class="nav-item" v-if="isLoggedIn">
              <button class="btn btn-secondary" @click="logout"> {{ $t("header.logout") }}</button>
            </li>
          </ul>
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
    isLoggedIn : function(){ return this.$store.getters.isLoggedIn},
    user: function(){ return this.$store.getters.user.username},
    competition: function(){ return this.$store.getters.competition},
  },
  methods: {
    logout() {
      this.$store.commit("logout")
      if (!this.$route.name.includes("landing")) {
        this.$router.push('/landing')
      }
    },
  }
}
</script>

<style scoped>
header, nav {
  width: 100%;
}

header {
  border-bottom: whitesmoke 2px solid;
  margin-bottom: 40px;
}


.navbar {
  background: white !important;
  min-height: 80px;
}

.navbar-nav {
  justify-content: center;
}

ul li {
  margin-right: 25px;
}

@media screen and (max-width: 699px) {
  ul li:hover {
    margin-right: 25px;
    background-color: ghostwhite;
  }
}


#main:hover {
  text-decoration: underline;
}

#main {
  color: var(--emphasis-color);
  font-size: 24px;
  font-weight: 700;
  margin-left: 20px;
}

.navbar-collapse {
  justify-content: center;
}

</style>
