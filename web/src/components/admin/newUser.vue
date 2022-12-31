<template>
<div>
  <h1>{{$t("admin.addUser")}}</h1>
  <div class="col-md-6 mx-auto pt-4">
    <div class="mb-3">
      <label for="user-email" class="form-label">{{ $t("admin.username") }}</label>
      <input id="user-email" v-model="userEmail" type="text" class="form-control" placeholder="">
    </div>
    <div class="mb-3">
      <label for="user-password" class="form-label">{{ $t("admin.password") }}</label>
      <input id="user-password" v-model="userPassword" type="text" class="form-control" placeholder="">
    </div>
    <div class="mb-3">
      <label for="club-select" class="form-label"> {{ $t("player.add.club") }}</label>
      <select class="form-select" id="club-select" v-model="userClub">
        <option v-for="club in clubs" :key="club.id" :value="club">
          {{ club.name }}
        </option>
      </select>
    </div>
    <button type="button" class="btn btn-primary" @click="addUser">
      <span>{{ $t("general.save") }} </span>
    </button>
  </div>
</div>
</template>

<script>
import ClubService from "@/common/api-services/club.service";
import adminService from "@/common/api-services/admin.service";
import {generalErrorHandler} from "@/common/util";

export default {
  name: "newUser",
  data() {
    return {
      userEmail: "",
      userPassword: "",
      userClub: "",
      clubs: [],
    }
  },
  mounted() {
    ClubService.getClubs().then(res => {
      this.clubs = res.data
    })
  },
  methods: {
    addUser() {
      const body = {
        username: this.userEmail,
        password: this.userPassword,
        clubId: this.userClub.id
      }
      adminService.addUser(body).then(() => {
        this.$toasted.success("User added").goAway(3000)
        this.userEmail = ""
        this.userPassword = ""
        this.userClub = ""
      })
          .catch(err => {
            this.errorHandler(err)
          })
    },
    errorHandler: generalErrorHandler
  }
}
</script>

<style scoped>

</style>