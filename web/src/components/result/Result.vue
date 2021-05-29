<template>
  <div>
    <div class="container-fluid">
      <div class="row gx-5">
        <!-- Main content -->
        <div id="main">
          <h1> {{ $t("results.heading") }}</h1>
          <div id="table-container" class="col-sm-12 m-auto" v-if="matches.length > 0">
            <table class="table table-borderless">
              <thead>
              <tr>
                <th scope="col" class="col-2">{{ $t("results.startTime") }}</th>
                <th scope="col" class="col-2">{{ $t("results.category") }}</th>
                <th scope="col" class="col-3">{{ $t("") }}</th>
                <th scope="col" class="col-3">{{ $t("") }}</th>
                <th scope="col" class="col-2">{{ $t("results.result") }}</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="match in matches" :key="match.id">
                <td>{{ getTime(match.startTime) }}</td>
                <td>{{ match.competitionCategory.categoryName }}</td>
                <td>{{ match.firstPlayer }}</td>
                <td>{{ match.secondPlayer }}</td>
                <td></td>
              </tr>
              </tbody>
            </table>
          </div>
          <div v-if="matches.length === 0">
            <p>
              Inga matcher
              {{matches.length}}
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MatchService from "@/common/api-services/match.service";

export default {
  name: "ResultComponent",
  data() {
    return {
      matches: []
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  mounted() {
    MatchService.getMatchesInCompetition(this.competition.id).then(res => {
      this.matches = res.data
    })
  },
  methods: {
    // TODO - move to util
    getPlayerOne(match) {
      let playerOne = ""
      if(match.firstPlayer.length === 1) {
        playerOne = match.firstPlayer[0].firstName + " " + match.firstPlayer[0].lastName
      }
      else if (match.firstPlayer.length === 1) {
        playerOne = match.firstPlayer[0].firstName + " " + match.firstPlayer[0].lastName + "/" +
            match.firstPlayer[1].firstName + " " + match.firstPlayer[1].lastName
      }
      return playerOne
    },
    getPlayerTwo(match) {
      let playerTwo = ""
      if(match.secondPlayer.length === 1) {
        playerTwo = match.secondPlayer[0].firstName + " " + match.secondPlayer[0].lastName
      }
      else if (match.firstPlayer.length === 1) {
        playerTwo = match.secondPlayer[0].firstName + " " + match.secondPlayer[0].lastName + "/" +
            match.secondPlayer[1].firstName + " " + match.secondPlayer[1].lastName
      }
      return playerTwo
    },
    getTime(match) {
      if (match!=null && match.startTime === null) {
        return this.$t("draw.pool.noTime")
      }
    }
  }
}
</script>

<style scoped>
th {
  text-decoration: underline;
}
</style>