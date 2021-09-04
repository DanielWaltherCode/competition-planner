<template>
  <div>
    <competition-header :competition="competition"> </competition-header>
    <router-view></router-view>
  </div>
</template>

<script>
import CompetitionHeader from "./CompetitionHeader";
import ApiService from "../../api-services/api.service";

export default {
  name: "Competition",
  data() {
    return {
      competition: {}
    }
  },
  components: {CompetitionHeader},
  mounted() {
    const competitionId = this.$route.params.competitionId
    ApiService.getCompetition(competitionId).then(res => {
      this.competition = res.data
    })
  }
}
</script>

<style scoped>

</style>