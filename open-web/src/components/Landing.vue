<template>
  <div class="container-fluid">
    <div class="row">
      <div class="sidebar col-md-2">
        <div class="sidebar-header">
          <ul class="list-group list-group-flush">
            <li class="list-group-item " @click="makeChoice('competitions')">
              <i class="fas fa-medal"></i>
              {{ $t("landing.sidebar.competition") }}
            </li>
            <li class="list-group-item" @click="makeChoice('series')">
              <i class="fas fa-trophy"></i>
              {{ $t("landing.sidebar.series") }}
            </li>
            <li class="list-group-item" @click="makeChoice('players')">
              <i class="fas fa-user-friends"></i>
              {{ $t("landing.sidebar.players") }}
            </li>
            <li class="list-group-item" @click="makeChoice('clubs')">
              <i class="fas fa-flag-checkered"></i>
              {{ $t("landing.sidebar.clubs") }}
            </li>
          </ul>
        </div>
      </div>
      <main class="col-md-10">
        <div id="competition-select">
          <div class="card">
            <div class="card-header bg-white">
              <h2 class="text-dark">{{ $t("landing.choice") }}</h2>
            </div>
            <ul class="list-group list-group-flush">
              <li class="list-group-item" v-for="competition in competitions" :key="competition.id">
                <h5 class="mb-1 list-header fw-bolder" v-on:click="chooseCompetition(competition.id)">{{ competition.name }}</h5>
                <!-- Smaller font size -->
                <div class="d-flex justify-content-center">

                  <p class="fs-6 mb-0 me-4">{{ competition.organizerClub.name }}</p>
                  <p class="fs-6 mb-0"> {{ competition.startDate }} - {{ competition.endDate }}</p>
                </div>
              </li>
            </ul>
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
      competitions: []
    }
  },
  mounted() {
    ApiService.getCompetitions().then(res => {
      this.competitions = res.data
    })
  },
  methods: {
    chooseCompetition(competitionId) {
      this.$router.push("/competition/" + competitionId)
    },
    makeChoice(choice) {
      this.$router.push(choice)
    }
  }
}
</script>

<style scoped>

.list-header:hover {
  cursor: pointer;
  opacity: 0.7;
}

.sidebar .list-group {
  width: 100%;
  margin-top: 100px;
}

.sidebar .list-group li {
  width: 80%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0 5px 10px 5px;
}

.card-header {
  border-top: 20px solid var(--main-color);
}

</style>