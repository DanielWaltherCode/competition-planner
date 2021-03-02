<template>
  <main class="container-fluid">
    <h2>{{ getString("overview.title") }}</h2>
    <div class="row">
        <div class="row justify-content-center">
          <div class="mb-3 col-md-7 d-block">
            <label for="competition-name" class="form-label">{{ getString("overview.name") }}</label>
            <input type="text" class="form-control" id="competition-name" v-model="competitionName" placeholder="">
          </div>
          <div class="mb-3 col-md-7 d-block">
            <label for="info" class="form-label">{{ getString("overview.info") }}</label>
            <textarea class="form-control" id="info" v-model="info" placeholder="" />
          </div>
          <div class="mb-3 col-md-7">
            <label for="start-date" class="form-label">{{ getString("overview.startDate") }}</label>
            <input type="date" class="form-control" id="start-date" v-model="startDate" placeholder="">
          </div>
          <div class="mb-3 col-md-7">
            <label for="end-date" class="form-label">{{ getString("overview.endDate") }}</label>
            <input type="date" class="form-control" id="end-date" v-model="endDate" placeholder="">
          </div>
          <div class="mb-3 col-md-7">
            <label for="location" class="form-label">{{ getString("overview.location") }}</label>
            <input type="text" class="form-control" id="location" v-model="location" placeholder="">
          </div>
        </div>
    </div>
    <div id="bottom">
      <div>
        <button class="btn btn-secondary" @click="save">{{ getString("general.saveChanges") }}</button>
      </div>
      <div v-if="competitionAdded">
        <p> {{ getString("overview.competitionAdded") }}</p>
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
      "competitionName": "",
      "info": "",
      "startDate": "",
      "endDate": "",
      "location": "",
      "competitionAdded": false,
      "competition": ""
    }
  },
  methods: {
    getString(string) {
      return this.$t(string)
    },
    save() {
      const objectToSave = {
        "location": this.location,
        "welcomeText": this.info,
        "organizingClubId": 129,
        "startDate": this.startDate,
        "endDate": this.endDate,
      }

      console.log("Sending data: ", objectToSave)
      CompetitionService.addCompetition(objectToSave).then(res => {
        this.competition = res.data
        console.log("COmpetition added")
        console.log(this.competition)
        this.competitionAdded = true
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
  background: linear-gradient(to right bottom, white 50%, var(--background2) 50%);
  height: 100vh;
}

.form-label {
  width: 100%;
  text-align: left;
  margin-left: 10px;
}

</style>