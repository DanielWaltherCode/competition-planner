<template>
  <main class="container-fluid">
    <h1 class="p-4">{{ getString("newCompetition.title") }}
      <i @click="$router.push('/classes')" class="fas fa-arrow-right" style="float: right"></i>
    </h1>
    <div class="row">
        <div class="col-md-7 mx-auto">
          <div class="form-group mb-4">
            <div class="d-flex align-items-center mb-2">
              <i class="fas fa-location-arrow me-2"></i>
              <label for="competition-location" class="form-label mb-0">{{
                  getString("newCompetition.location")
                }}</label>
            </div>
            <input type="text" class="form-control" id="competition-location" v-model="competitionLocation" placeholder="">
          </div>
          <div class="mb-4">
            <div class="d-flex align-items-center mb-2">
              <i class="fas fa-user-friends me-2"></i>
              <label for="competition-name" class="form-label mb-0">{{ getString("newCompetition.name") }}</label>
            </div>
            <input type="text" class="form-control" id="competition-name" v-model="competitionName" placeholder="">
          </div>
          <div class="mb-4">
            <div class="d-flex align-items-center mb-2">
              <i class="fas fa-file-signature me-2"></i>
              <label for="info" class="form-label mb-0">{{ getString("newCompetition.info") }}</label>
            </div>
            <textarea class="form-control" id="info" v-model="info" placeholder="" />
          </div>
          <div class="mb-4">
            <div class="d-flex align-items-center mb-2">
              <i class="fas fa-calendar-day me-2"></i>
              <label for="start-date" class="form-label mb-0">{{ getString("newCompetition.startDate") }}</label>
            </div>
            <input type="date" class="form-control" id="start-date" v-model="startDate" placeholder="">
          </div>
          <div class="mb-4">
            <div class="d-flex align-items-center mb-2">
              <i class="fas fa-calendar-day me-2"></i>
              <label for="end-date" class="form-label mb-0">{{ getString("newCompetition.endDate") }}</label>
            </div>
            <input type="date" class="form-control" id="end-date" v-model="endDate" placeholder="">
          </div>
        </div>
    </div>
    <div id="bottom">
      <div>
        <button class="btn btn-primary" type="button" @click="save">{{ getString("general.saveChanges") }}</button>
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
      competitionName: "",
      info: "",
      startDate: "",
      endDate: "",
      competitionLocation: "",
      competitionAdded: false,
      competition: ""
    }
  },
  computed: {
    user: function (){return this.$store.getters.user}
  },
  methods: {
    getString(string) {
      return this.$t(string)
    },
    save() {
      const objectToSave = {
        "location": this.competitionLocation,
        "name": this.competitionName,
        "welcomeText": this.info,
        "organizingClubId": this.user.clubNoAddressDTO.id,
        "startDate": this.startDate,
        "endDate": this.endDate,
      }

      console.log("Sending data: ", objectToSave)
      CompetitionService.addCompetition(objectToSave).then(res => {
        this.competition = res.data
        this.competitionAdded = true
        this.$toasted.show("newCompetition.competitionUpdated").goAway(3000)
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
  background: url("../../assets/hero-bg.png") top center no-repeat;
  background-size: cover;
}

.form-label {
  width: 100%;
  text-align: left;
}

</style>