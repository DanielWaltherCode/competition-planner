<template>
  <div class="container-fluid">
    <div class="row">
      <main class="col-12">
        <div id="competition-select">
          <div class="row">
            <div class="custom-card bg-white mt-5 col-sm-6 mx-auto rounded-4">
                <h2 class="text-dark">{{ $t("landing.choice") }}</h2>
              <div class="btn-group py-4" role="group">
                <button type="button" @click="chosenTimePeriod = 'earlier'" :class="chosenTimePeriod === 'earlier' ? 'active' : ''"
                        class="btn btn-outline-primary">{{$t("landing.earlier")}}</button>
                <button type="button" @click="chosenTimePeriod = 'thisWeek'" :class="chosenTimePeriod === 'thisWeek' ? 'active' : ''"
                        class="btn btn-outline-primary">{{$t("landing.thisWeek")}}</button>
                <button type="button" @click="chosenTimePeriod = 'coming'" :class="chosenTimePeriod === 'coming' ? 'active' : ''"
                        class="btn btn-outline-primary">{{$t("landing.coming")}}</button>
              </div>
              <ul class="list-group list-group-flush">
                <li class="list-group-item py-4" v-for="competition in competitions" :key="competition.id">
                  <h5 class="mb-1 list-header fw-bolder" v-on:click="chooseCompetition(competition.id)">
                    {{ competition.name }}</h5>
                  <!-- Smaller font size -->
                  <div class="d-flex justify-content-center">
                    <p class="fs-6 mb-0 me-4">{{ competition.organizerClub.name }}</p>
                    <p class="fs-6 mb-0"> {{ competition.startDate }} - {{ competition.endDate }}</p>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>

import ApiService from "../api-services/api.service";

export default {
  name: "Landing",
  data() {
    return {
      competitions: [],
      chosenTimePeriod: "thisWeek"
    }
  },
  mounted() {
    ApiService.getCompetitions().then(res => {
      this.competitions = res.data
    })
  },
  methods: {
    chooseCompetition(competitionId) {
      this.$router.push("/competition/" + competitionId + "/landing")
    },
  }
}
</script>

<style scoped>

#competition-select {
  width: 100%;
  height: 100vh;
  background: url('~@/assets/hero-bg.png') top center no-repeat;
  background-size: cover;
  border-top: 5px solid var(--main-color);
}

.list-header:hover {
  cursor: pointer;
  opacity: 0.7;
}

</style>