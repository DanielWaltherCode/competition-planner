<template>
  <header id="header" class="header">
    <nav class="navbar navbar-expand-lg navbar-dark">
      <div class="container-fluid">
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarTogglerDemo01"
                aria-controls="navbarTogglerDemo01" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarTogglerDemo01">
          <ul class="navbar-nav w-100 d-flex justify-content-around align-items-center">
            <li class="nav-item">
              {{ $t("header.firstText") }}
            </li>
            <li class="d-flex align-items-center">
              <i class="fas fa-trophy me-2"></i>
              <router-link to="/landing" :key="$route.fullPath" class="navbar-brand">
                <h2 id="brand">LET'S COMPETE</h2>
              </router-link>
            </li>
            <li class="nav-item">
              <input type="text" class="form-check" placeholder="Sök på en spelare">
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
  computed: {},
  methods: {}
}
</script>

<style scoped>

nav {
  box-shadow: 0 3px #efefef;
}

#brand {
  color: var(--main-color);
}

.navbar {
  background-color: white !important;
}

.nav-link {
  color: var(--main-color) !important;
}

.submenu {
  color: white !important;
}

.navbar-collapse {
  justify-content: space-between;
}

#navbar button {
  margin-left: 10px;
}

</style>
