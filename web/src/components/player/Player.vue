<template>
  <main class="container-fluid">
    <div class="row">
      <h1 class="p-4">
        <i @click="$router.push('/classes')" class="fas fa-arrow-left" style="float: left"></i>
        {{ $t("player.title") }}
        <i @click="$router.push('/draw')" class="fas fa-arrow-right" style="float: right"></i>
      </h1>

      <!-- Sidebar -->
      <div class="sidebar col-md-3">
        <div class="sidebar-header">
          <h4> {{ $t("player.sidebar.title") }}</h4>
        </div>
        <ul class="list-group list-group-flush">
          <li class="list-group-item" @click="$router.push('/players/overview')"
              :class="$route.path.includes('overview') ? 'active' : ''">
            {{ $t("player.sidebar.overview") }}
          </li>
          <li class="list-group-item" @click="$router.push('/players/add')"
              :class="$route.path.includes('add') ? 'active' : ''">
            {{ $t("player.sidebar.addNew") }}
          </li>
          <li class="list-group-item" @click="$router.push('/players/detail/0')"
              :class="$route.path.includes('detail') ? 'active' : ''">
            {{ $t("player.sidebar.detail") }}
          </li>
          <li class="list-group-item" @click="$router.push('/players/create')"
              :class="$route.path.includes('create') ? 'active' : ''">
            {{ $t("player.sidebar.create") }}
          </li>
        </ul>
      </div>

      <!-- Main content -->
      <div id="main" class="col-md-9 mx-auto">
        <router-view></router-view>
      </div>
    </div>
  </main>
</template>

<script>

export default {
  name: "Player",
  data() {
    return {
      displayChoice: "overview",
    }
  },
  mounted() {
    this.$router.push('/players/overview')
  }
}
</script>

<style scoped>

h1 {
  background-color: var(--clr-primary-100);
  margin-bottom: 0;
}

</style>