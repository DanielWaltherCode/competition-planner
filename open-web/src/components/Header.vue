<template>
  <header id="header" class="header">
    <div class="d-flex justify-content-around align-items-center">
      <div>
        <p class="mb-0 ms-1">
          {{ $t("header.firstText") }}
        </p>
      </div>
      <router-link to="/landing" :key="$route.fullPath" class="navbar-brand">
        <h2 id="brand" class="m-3">LET'S COMPETE</h2>
      </router-link>
    </div>
    <div>
      <input id="searchBox" type="text" class="form-control rounded mb-2 mx-auto" :placeholder="$t('header.search')">
    </div>
  </header>
</template>

<script>

export default {
  name: "Header",
}
</script>

<style scoped>

#header {
  background: url('~@/assets/hero-bg.png') top center no-repeat;
  background-size: cover;
}

nav {
  box-shadow: 0 3px #efefef;
}

#brand {
  color: var(--main-color);
}

.navbar {
  background-color: white !important;
}

#searchBox {
  width: 50%;
}

@media only screen and (max-width: 600px) {
  #searchBox {
     min-width: 75%;
  }
}


/*
#search {
  min-width: 350px;
}*/

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
