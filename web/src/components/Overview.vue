<template>
  <div id="overview-main">
    <h2>{{ getString("overview.title") }}</h2>
    <div id="form-holder">
      <div class="mb-3">
        <label for="competition-name" class="form-label">{{ getString("overview.name") }}</label>
        <input type="text" class="form-control" id="competition-name" v-model="competitionName" placeholder="">
      </div>
      <div class="mb-3">
        <label for="info" class="form-label">{{ getString("overview.info") }}</label>
        <input type="text" class="form-control" id="info" v-model="info" placeholder="">
      </div>
      <div class="mb-3">
        <label for="start-date" class="form-label">{{ getString("overview.startDate") }}</label>
        <input type="date" class="form-control" id="start-date" v-model="startDate" placeholder="">
      </div>
      <div class="mb-3">
        <label for="end-date" class="form-label">{{ getString("overview.endDate") }}</label>
        <input type="date" class="form-control" id="end-date" v-model="endDate" placeholder="">
      </div>
      <div class="mb-3">
        <label for="location" class="form-label">{{ getString("overview.location") }}</label>
        <input type="text" class="form-control" id="location" v-model="location" placeholder="">
      </div>
    </div>
    <div id="bottom">
      <div>
        <button class="btn btn-outline-info" @click="save">{{ getString("general.saveChanges") }}</button>
      </div>
      <div v-if="competitionAdded">
        <p> {{ getString("overview.competitionAdded") }}</p>
        <button class="btn btn-outline-info">
          <i class="bi bi-arrow-right"></i>
          {{ getString("general.next") }}
        </button>
      </div>
    </div>

  </div>
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

#overview-main {
  width: 100%;
}

#form-holder {
  width: 60%;
  margin: auto;
}

.form-label {
  width: 100%;
  text-align: left;
  margin-left: 10px;
}

#bottom {
  display: grid;
  grid-template-columns: 1fr 1fr;
}

</style>