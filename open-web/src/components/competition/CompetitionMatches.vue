<template>
  <div>
    <div class="container-fluid">
        <!-- Main content -->
        <div id="main" class="d-grid">
          <match-result v-for="match in matches" :match="match" :key="match.id"></match-result>
        </div>
          <div v-if="matches.length === 0">
            <p>
             {{$t("results.notDrawn")}}
            </p>
          </div>
        </div>
    </div>
</template>

<script>
import ApiService from "../../api-services/api.service";
import MatchResult from "../MatchResult";

export default {
  name: "CompetitionMatches",
  components: {MatchResult},
  data() {
    return {
      matches: [],
      selectedMatch: null,
      competitionId: 0
    }
  },
  mounted() {
    this.competitionId = this.$route.params.competitionId
    this.getMatches()
  },
  methods: {
    getMatches() {
      ApiService.getMatchesInCompetition(this.competitionId).then(res => {
        this.matches = res.data
      })
    },
    getPlayerOne(match) {
      let playerOne = ""
      if (match.firstPlayer.length === 1) {
        playerOne = match.firstPlayer[0].firstName + " " + match.firstPlayer[0].lastName + " " + match.firstPlayer[0].club.name
      } else if (match.firstPlayer.length === 2) {
        playerOne = match.firstPlayer[0].firstName + " " + match.firstPlayer[0].lastName + " " + match.firstPlayer[0].club.name + "/" +
            match.firstPlayer[1].firstName + " " + match.firstPlayer[1].lastName + " " + match.firstPlayer[1].club.name
      }
      return playerOne
    },
    getPlayerTwo(match) {
      let playerTwo = ""
      if (match.secondPlayer.length === 1) {
        playerTwo = match.secondPlayer[0].firstName + " " + match.secondPlayer[0].lastName + " " + match.secondPlayer[0].club.name
      } else if (match.firstPlayer.length === 2) {
        playerTwo = match.secondPlayer[0].firstName + " " + match.secondPlayer[0].lastName + " " + match.secondPlayer[0].club.name + "/" +
            match.secondPlayer[1].firstName + " " + match.secondPlayer[1].lastName + " " + match.secondPlayer[1].club.name
      }
      return playerTwo
    },
    getTime(match) {
      if (match != null && match.startTime === null) {
        return this.$t("draw.pool.noTime")
      }
    },
    isPlayerOneWinner(match) {
      if(match.winner.length > 0) {
        const winnerIds = match.winner.map(winner => winner.id)
        if (winnerIds.includes(match.firstPlayer[0].id)) {
          return true
        }
      }
      return false
    },
    isPlayerTwoWinner(match) {
      if(match.winner.length > 0) {
        const winnerIds = match.winner.map(winner => winner.id)
        if (winnerIds.includes(match.secondPlayer[0].id)) {
          return true
        }
      }
      return false
    }
  }

}
</script>

<style scoped>
th {
  text-decoration: underline;
}

#main {
  margin-top: 30px;
  grid-gap: 2rem;
}

@media (min-width: 600px) {
  #main {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1000px) {
  #main {
    grid-template-columns: repeat(3, 1fr);
  }
}

</style>