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
              <router-link class="nav-link" to="/overview">
                {{ $t("header.overview") }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/classes">
                {{ $t("header.classes") }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/players">
                {{ $t("header.players") }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/draw">
                {{ $t("header.draws") }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/schedule">
                {{ $t("header.schedule") }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/results">
                {{ $t("header.results") }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/billing">
                {{ $t("header.billing") }}
              </router-link>
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
  mounted() {
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
