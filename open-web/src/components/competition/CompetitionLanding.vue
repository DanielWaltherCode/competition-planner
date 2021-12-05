<template>
  <div class="row">
    <div v-if="this.competition !== null" class="custom-card col-sm-8 mx-auto mt-3 bg-white py-5">
      <h2>{{ $t("competitionLanding.welcomeTo") }} {{ this.competition.name }}</h2>
      <div class="text-start px-3">
        <h4 class="text-uppercase">{{ $t("competitionLanding.about") }}</h4>
        <p>{{ this.competition.welcomeText }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import ApiService from "../../api-services/api.service";

export default {
  name: "CompetitionLanding",
  data() {
    return {
      competitionId: 0,
      competition: null
    }
  },
  mounted() {
    this.competitionId = this.$route.params.competitionId
    ApiService.getCompetition(this.competitionId).then(res => {
      this.competition = res.data
    })
  }
}
</script>

<style scoped>

</style>