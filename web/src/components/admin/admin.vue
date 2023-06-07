<template>
  <main class="container-fluid">
    <div class="row">
      <h1 class="p-4 mb-2">
        Admin
      </h1>
      <div v-if="!isLoggedIn || !isAdmin">
        <h4 class="text-center"> {{ $t("admin.preamble") }}</h4>
      </div>

      <div v-if="isLoggedIn && isAdmin" class="row">

        <!-- Sidebar -->
        <div class="sidebar col-md-3">
          <div class="sidebar-header">
            <h4> {{ $t("admin.sidebarTitle") }}</h4>
          </div>
          <ul class="list-group list-group-flush">
            <li class="list-group-item">
              <router-link style="color: black" class="text-decoration-none" to="newClub">
                {{ $t("admin.addClub") }}
              </router-link>
            </li>
            <li class="list-group-item">
              <router-link style="color: black" class="text-decoration-none" to="newUser">
                {{ $t("admin.addUser") }}
              </router-link>
            </li>
            <li class="list-group-item">
              <router-link style="color: black" class="text-decoration-none " to="competitionAdmin">
                {{ $t("admin.handleCompetition") }}
              </router-link>
            </li>
          </ul>
        </div>

        <!-- Main content -->
        <div class="col-md-9">
          <router-view></router-view>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import {generalErrorHandler} from "@/common/util";

export default {
  // eslint-disable-next-line
  name: "AdminComponent",
  data() {
    return {
      username: "",
      password: ""
    }
  },
  computed: {
    isLoggedIn: function () {
      return this.$store.getters.isLoggedIn
    },
    isAdmin: function () {
      return this.$store.getters.isAdmin
    }
  },
  mounted() {
    if (this.isLoggedIn && this.isAdmin) {
      this.$router.push("competitionAdmin")
    }
  },
  methods: {
    errorhandler: generalErrorHandler,
    getString(string) {
      return this.$t(string)
    },
  }
}
</script>

<style scoped>
h1 {
  background-color: var(--clr-primary-100);
  margin-bottom: 0;
}
</style>