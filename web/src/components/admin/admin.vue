<template>
  <main class="container-fluid">
    <div class="row">
      <h1 class="p-4 mb-2">
        Admin
      </h1>

      <!-- If not logged in -->
      <div v-if="!isLoggedIn || !isAdmin">
        <h4 class="text-center"> {{$t("admin.preamble")}}</h4>
        <div class="col-lg-6 mx-auto">
        <div class="mb-3">
          <label for="username" class="form-label">{{ getString("landing.heading.username") }}</label>
          <input type="text" class="form-control" id="username" v-model="username" placeholder="">
        </div>
        <div class="mb-3">
          <label for="password" class="form-label">{{ getString("landing.heading.password") }}</label>
          <input type="password" class="form-control" id="password" v-model="password" placeholder="">
        </div>
        <button type="button" class=" btn btn-primary"
            @click="login">
          <span>{{ getString("landing.heading.login") }} </span>
        </button>
        </div>
      </div>
      <!-- Sidebar -->
      <div class="sidebar col-md-3" v-if="isLoggedIn && isAdmin">
        <div class="sidebar-header">
          <h4> {{ $t("admin.sidebarTitle") }}</h4>
        </div>
        <ul class="list-group list-group-flush">
          <li class="list-group-item">
            <router-link style="color: black" class="text-decoration-none" to="newClub">
              {{$t("admin.addClub")}}
            </router-link>
          </li>
          <li class="list-group-item">
            <router-link style="color: black" class="text-decoration-none" to="newUser">
              {{$t("admin.addUser")}}
            </router-link>
          </li>
          <li class="list-group-item" >
            <router-link style="color: black" class="text-decoration-none " to="competitionAdmin">
              {{$t("admin.handleCompetition")}}
            </router-link>
          </li>
        </ul>
      </div>

      <!-- Main content -->
      <div class="col-md-9">
        <router-view></router-view>
      </div>
    </div>
  </main>
</template>

<script>
import UserService from "@/common/api-services/user.service";
import {generalErrorHandler} from "@/common/util";

export default {
  name: "admin",
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
    login() {
      UserService.login(this.username, this.password).then(res => {
        this.$store.commit("auth_success", res.data)
        UserService.getUser().then(res => {
          this.$store.commit("set_user", res.data)
          if (this.isAdmin) {
            this.$router.push("competitionAdmin")
          }
          else {
            this.$toasted.error("You need to log in as admin to view this page").goAway(5000)
          }
        })
      })
          .catch(err => {
            this.errorhandler(err)
          })
    },
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