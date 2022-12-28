<template>
  <div>
    <h1>{{$t("admin.addClub")}}</h1>
    <div class="col-md-6 mx-auto pt-4">
      <div class="mb-3">
        <label for="clubName" class="form-label">{{ $t("admin.clubName") }}</label>
        <input id="clubName" v-model="clubName" type="text" class="form-control" placeholder="">
      </div>
      <div class="mb-3">
        <label for="clubAddress" class="form-label">{{ $t("admin.clubAddress") }}</label>
        <input id="clubAddress" v-model="clubAddress" type="text" class="form-control" placeholder="">
      </div>
      <button type="button" class="btn btn-primary" @click="addClub">
        <span>{{ $t("general.save") }} </span>
      </button>
    </div>
  </div>
</template>

<script>
import AdminService from "@/common/api-services/admin.service";

export default {
  name: "newClub",
  data() {
    return {
      clubName: "",
      clubAddress: "",
    }
  },
  methods: {
    addClub() {
      const body = {
        name: this.clubName,
        address: this.clubAddress
      }
      AdminService.addClub(body).then(() => {
        this.clubName = ""
        this.clubAddress = ""
        this.$toasted.success("Club added").goAway(3000)
      })
    }
  }
}
</script>

<style scoped>

</style>