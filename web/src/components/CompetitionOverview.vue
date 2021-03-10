<template>
  <main class="container-fluid" v-if="competition">
    <h2>{{ getString("newCompetition.title") }}</h2>
    <div class="row">
        <div class="row justify-content-center">
          <div class="mb-3 col-md-7 d-block">
            <label for="competition-location" class="form-label">{{ getString("newCompetition.location") }}</label>
            <input type="text" class="form-control" id="competition-location" v-model="competition.location">
          </div>
          <div class="mb-3 col-md-7 d-block">
            <label for="competition-name" class="form-label">{{ getString("newCompetition.name") }}</label>
            <input type="text" class="form-control" id="competition-name" v-model="competition.name">
          </div>
          <div class="mb-3 col-md-7 d-block">
            <label for="info" class="form-label">{{ getString("newCompetition.info") }}</label>
            <textarea class="form-control" id="info" v-model="competition.welcomeText" placeholder="" />
          </div>
          <div class="mb-3 col-md-7">
            <label for="start-date" class="form-label">{{ getString("newCompetition.startDate") }}</label>
            <input type="date" class="form-control" id="start-date" v-model="competition.startDate">
          </div>
          <div class="mb-3 col-md-7">
            <label for="end-date" class="form-label">{{ getString("newCompetition.endDate") }}</label>
            <input type="date" class="form-control" id="end-date" v-model="competition.endDate">
          </div>
        </div>
    </div>
    <div id="bottom">
      <div>
        <button class="btn btn-outline-primary" @click="save">{{ getString("general.saveChanges") }}</button>
      </div>
      <div v-if="competitionUpdated">
        <p> {{ getString("newCompetition.competitionUpdated") }}</p>
        <button class="btn btn-secondary">
          <i class="bi bi-arrow-right"></i>
          {{ getString("general.next") }}
        </button>
      </div>
    </div>

  </main>
</template>

<script>
import CompetitionService from "@/common/api-services/competition.service";

export default {
  name: "Overview",
  data() {
    return {
      competitionUpdated: false
    }
  },
  computed : {
    isLoggedIn : function(){ return this.$store.getters.isLoggedIn},
    competition: function(){ return this.$store.getters.competition},
  },
  methods: {
    getString(string) {
      return this.$t(string)
    },
    save() {
      const objectToSave = {
        "location": this.competition.location,
        "name": this.competition.name,
        "welcomeText": this.competition.welcomeText,
        "organizingClubId": this.competition.organizingClub.id,
        "startDate": this.competition.startDate,
        "endDate": this.competition.endDate,
      }

      CompetitionService.updateCompetition(objectToSave).then(res => {
        this.$store.commit("set_competition", res.data)
        this.competitionUpdated = true
      }).catch(err => {
            console.log("Couldn't add competition", err)
          }
      )
    }
  }

}
</script>

<style scoped>

main {
  height: 100vh;
  background: url("../assets/hero-bg.png") top center no-repeat;
  background-size: cover;
}

.form-label {
  width: 100%;
  text-align: left;
}

</style>