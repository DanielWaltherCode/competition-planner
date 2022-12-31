<template>
<div>
  <h1>{{$t("admin.handleCompetition")}}</h1>
  <div>
    <div class="form-group col-sm-6">
      <label class="form-label d-flex justify-content-start ms-1" for="competition-selection"> {{ $t("landing.heading.competitionChoice") }} </label>
      <select id="competition-selection" class="form-select"
              v-on:change="setCompetition" v-model="selectedCompetition">
        <option value="none"> {{ $t("landing.heading.noCompetitionSelected") }}</option>
        <option v-for="comp in competitions" :key="comp.id" :value="comp">
          {{ comp.name }}
        </option>
      </select>
    </div>
  </div>
</div>
</template>

<script>
import AdminService from "@/common/api-services/admin.service";

export default {
  name: "competitionAdmin",
  data() {
    return {
      selectedCompetition: "",
      competitions: []
    }
  },
  mounted() {
    AdminService.getCompetitions().then(res => {
      this.competitions = res.data
    })
  },
  methods: {
    setCompetition() {
      if (this.selectedCompetition === "none") {
        this.$store.commit("set_competition", null)
      } else {
        this.$store.commit("set_competition", this.selectedCompetition)
      }
    }
  }
}
</script>

<style scoped>

</style>