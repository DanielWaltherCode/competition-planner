<template>
  <main v-if="competition" class="mb-4">
     <h1 class="p-4">{{ getString("newCompetition.title") }}
       <i @click="$router.push('/classes')" class="fas fa-arrow-right" style="float: right"></i>
     </h1>

    <div class="container-fluid">
      <div class="row">
        <form class="row justify-content-center bg-white mt-3">
          <div class="col-md-7">

          <div class="form-group mb-4">
            <div class="d-flex align-items-center mb-2">
              <i class="fas fa-location-arrow me-2"></i>
              <label for="competition-location" class="form-label mb-0">{{
                  getString("newCompetition.location")
                }}</label>
            </div>
            <input type="text" class="form-control" id="competition-location" v-model="competition.location.name">
          </div>
          <div class="mb-4">
            <div class="d-flex align-items-center mb-2">
              <i class="fas fa-user-friends me-2"></i>
              <label for="competition-name" class="form-label mb-0">{{ getString("newCompetition.name") }}</label>
            </div>
            <input type="text" class="form-control" id="competition-name" v-model="competition.name">
          </div>
          <div class="mb-4">
            <div class="d-flex align-items-center mb-2">
              <i class="fas fa-file-signature me-2"></i>
              <label for="info" class="form-label mb-0">{{ getString("newCompetition.info") }}</label>
            </div>
            <textarea class="form-control" id="info" v-model="competition.welcomeText" placeholder=""/>
          </div>
          <div class="mb-4">
            <div class="d-flex align-items-center mb-2">
              <i class="fas fa-calendar-day me-2"></i>
              <label for="start-date" class="form-label mb-0">{{ getString("newCompetition.startDate") }}</label>
            </div>
            <input type="date" class="form-control" id="start-date" v-model="competition.startDate">
          </div>
          <div class="mb-4">
            <div class="d-flex align-items-center mb-2">
              <i class="fas fa-calendar-day me-2"></i>
              <label for="end-date" class="form-label mb-0">{{ getString("newCompetition.endDate") }}</label>
            </div>
            <input type="date" class="form-control" id="end-date" v-model="competition.endDate">
          </div>
          <div class="d-flex justify-content-end">
            <button class="btn btn-light" type="button" @click="save">{{ getString("general.saveChanges") }}</button>
          </div>
          </div>
        </form>
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
  computed: {
    isLoggedIn: function () {
      return this.$store.getters.isLoggedIn
    },
    competition: function () {
      return this.$store.getters.competition
    },
  },
  methods: {
    getString(string) {
      return this.$t(string)
    },
    save() {
      const objectToSave = {
        "location": {
          "name": this.competition.location.name,
        },
        "name": this.competition.name,
        "welcomeText": this.competition.welcomeText,
        "startDate": this.competition.startDate,
        "endDate": this.competition.endDate,
      }

      CompetitionService.updateCompetition(objectToSave, this.competition.id).then(res => {
        this.$store.commit("set_competition", res.data)
        this.competitionUpdated = true
        this.$toasted.show(this.$tc("newCompetition.competitionUpdated")).goAway(3000)
      }).catch(err => {
            console.log("Couldn't add competition", err)
          }
      )
    }
  }

}
</script>

<style scoped>

h1 {
  background-color: var(--clr-primary-100);
}



</style>