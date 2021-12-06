<template>
  <div v-if="match" class="custom-card">
    <div class="bg-grey p-1 d-flex justify-content-center">
      <h2 class="fs-6 text-uppercase"> {{ getTitle() }}</h2>
    </div>
    <div class="p-2">
      <div class="row">
        <p class="col-5 text-start" :class="isPlayerOneWinner(match) ? 'fw-bold': ''">{{ getPlayerOne(match) }}</p>
        <p class="col-1 p-0" v-for="game in match.result.gameList" :key="game.id">
          {{ game.firstRegistrationResult }}
        </p>
      </div>
      <div class="row">
        <p class="col-5 mb-1 text-start" :class="isPlayerTwoWinner(match) ? 'fw-bold': ''">{{ getPlayerTwo(match) }}</p>
        <p class="col-1 p-0" v-for="game in match.result.gameList" :key="game.id">
          {{ game.secondRegistrationResult }}
        </p>
      </div>

    </div>
  </div>
</template>

<script>
export default {
  name: "MatchResult",
  props: {
    match: Object
  },
  methods: {
    getTitle() {
      if (this.match.matchType === 'GROUP') {
        return this.match.competitionCategory.name + " | " + this.$t("results.group") + " " + this.match.groupOrRound
      } else {
        return this.match.competitionCategory.name
      }
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
      if (match.winner.length > 0) {
        const winnerIds = match.winner.map(winner => winner.id)
        if (winnerIds.includes(match.firstPlayer[0].id)) {
          return true
        }
      }
      return false
    },
    isPlayerTwoWinner(match) {
      if (match.winner.length > 0) {
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

</style>