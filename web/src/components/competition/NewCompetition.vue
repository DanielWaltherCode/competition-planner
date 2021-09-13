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
            <input type="text" class="form-control" id="competition-location" @keyup="noCompetitionLocation = false" v-model="competitionLocation">
            <p class="fs-6 text-danger" v-if="noCompetitionLocation"> {{$t("validations.required")}}</p>
          </div>
          <div class="mb-4">
            <div class="d-flex align-items-center mb-2">
              <i class="fas fa-user-friends me-2"></i>
              <label for="competition-name" class="form-label mb-0">{{ getString("newCompetition.name") }}</label>
            </div>
            <input type="text" class="form-control" id="competition-name" @keyup="noCompetitionName = false" v-model="competitionName">
            <p class="fs-6 text-danger" v-if="noCompetitionName"> {{$t("validations.required")}}</p>
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
            <input type="date" class="form-control" id="start-date" @change="noStartDate = false" v-model="startDate" >
            <p class="fs-6 text-danger" v-if="noStartDate"> {{$t("validations.required")}}</p>
          </div>
          <div class="mb-4">
            <div class="d-flex align-items-center mb-2">
              <i class="fas fa-calendar-day me-2"></i>
              <label for="end-date" class="form-label mb-0">{{ getString("newCompetition.endDate") }}</label>
            </div>
            <input type="date" class="form-control" id="end-date" @change="noEndDate = false" v-model="endDate" >
            <p class="fs-6 text-danger" v-if="noEndDate"> {{$t("validations.required")}}</p>
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
      competitionName: null,
      info: null,
      startDate: null,
      endDate: null,
      competitionLocation: null,
      competitionAdded: false,
      competition: "",
      noCompetitionName: false,
      noStartDate: false,
      noEndDate: false,
      noCompetitionLocation: false,
    }
  },
  computed: {
    user: function (){return this.$store.getters.user}
  },
  methods: {
    getString(string) {
      return this.$t(string)
    },
    validateSubmission() {
      if (!this.competitionName) {
        this.noCompetitionName = true
        return false
      }
      if(!this.competitionLocation) {
        this.noCompetitionLocation = true
        return false
      }
      if (!this.startDate) {
        this.noStartDate = true
        return false
      }
      if (!this.endDate) {
        this.noEndDate = true
        return false
      }
      return true;
    },
    save() {
      if (!this.validateSubmission()) {
        return
      }
      const objectToSave = {
        "location": {
          "name": this.competitionLocation
        },
        "name": this.competitionName,
        "welcomeText": this.info,
        "organizingClubId": this.user.clubNoAddressDTO.id,
        "startDate": this.startDate,
        "endDate": this.endDate,
      }

      CompetitionService.addCompetition(objectToSave).then(res => {
        this.$store.commit("set_competition", res.data)
        this.competition = res.data
        this.competitionAdded = true
        this.$toasted.show(this.$tc("toasts.competitionAdded")).goAway(3000)
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