<template>
  <div v-if="this.competition !== null">
    <h2>{{ $t("competitionLanding.welcomeTo") }} {{this.competition.name}}</h2>
    <div>
    <h4>{{ $t("competitionLanding.about") }}</h4>
    <p>{{this.competition.welcomeText}}</p>
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
    ApiService.getCompetition(this.competitionId).then( res => {
      this.competition = res.data
    })
  }
}
</script>

<style scoped>

</style>