<template>
  <div>
    <main>
      <div id="top-bar">

      </div>
      <div id="competition-select">

        <div class="card" >
          <div class="card-header">
            <h2>{{$t("landing.choice")}}</h2>
          </div>
          <ul class="list-group list-group-flush">
            <li class="list-group-item" v-for="competition in competitions" :key="competition.id">
              <h5 class="mb-1 list-header" v-on:click="chooseCompetition(competition.id)">{{competition.name}}</h5>
              <!-- Smaller font size -->
              <p class="fs-6 mb-0">{{competition.organizingClub.name}}</p>
              <p class="fs-6 mb-0"> {{competition.startDate}} - {{competition.endDate}}</p>
            </li>
          </ul>
        </div>
      </div>
    </main>
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
    }
  }
}
</script>

<style scoped>
#top-bar {
  min-height: 100px;
  background-color: var(--main-color);
}

.list-header:hover {
  cursor: pointer;
  opacity: 0.7;
}

</style>