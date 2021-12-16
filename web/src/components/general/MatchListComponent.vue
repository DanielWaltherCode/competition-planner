<template>
  <div v-if="matches.length !== 0">
    <table class="table table-bordered table-striped table-sm">
      <thead class="thead-dark">
      <tr>
        <th>{{ $t("draw.pool.time") }}</th>
        <th></th>
        <th></th>
        <th>{{ $t("draw.pool.result") }}</th>
      </tr>
      </thead>
      <tbody>
      <tr class="group-matches" v-for="match in matches" :key="match.id">
        <td>{{ getTime(match) }}</td>
        <td>{{ getPlayerOne(match) }}</td>
        <td>{{ getPlayerTwo(match) }}</td>
        <td>
          <p class="pe-2 d-inline" v-for="game in match.result.gameList" :key="game.id">
            {{ game.firstRegistrationResult }} - {{ game.secondRegistrationResult }}
          </p>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
  <div v-else>
    <p>{{$t("draw.main.notDrawnTitle")}}</p>
  </div>
</template>

<script>
export default {
  name: "MatchListComponent",
  props: ["matches"],
  methods: {
    getPlayerOne(match) {
      let playerOne = ""
      if (match.firstPlayer.length === 1) {
        playerOne = match.firstPlayer[0].firstName + " " + match.firstPlayer[0].lastName
      } else if (match.firstPlayer.length === 2) {
        playerOne = match.firstPlayer[0].firstName + " " + match.firstPlayer[0].lastName + "/" +
            match.firstPlayer[1].firstName + " " + match.firstPlayer[1].lastName
      }
      return playerOne
    },
    getPlayerTwo(match) {
      let playerTwo = ""
      if (match.secondPlayer.length === 1) {
        playerTwo = match.secondPlayer[0].firstName + " " + match.secondPlayer[0].lastName
      } else if (match.firstPlayer.length === 2) {
        playerTwo = match.secondPlayer[0].firstName + " " + match.secondPlayer[0].lastName + "/" +
            match.secondPlayer[1].firstName + " " + match.secondPlayer[1].lastName
      }
      return playerTwo
    },
    getTime(match) {
      if (match.startTime === null) {
        return this.$t("draw.pool.noTime")
      }
    }
  }
}
</script>

<style scoped>

</style>