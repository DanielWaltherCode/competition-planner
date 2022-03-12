<template>
  <div v-if="matches.length !== 0" class="table-responsive">
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
        <td :class="isPlayerOneWinner(match) ? 'fw-bolder' : ''">
          {{ getPlayerOne(match) }} <span v-if="didPlayerOneGiveWO(match)"> (W.O)</span>
        </td>
        <td :class="isPlayerTwoWinner(match) ? 'fw-bolder' : ''">
          {{ getPlayerTwo(match) }}
          <span v-if="didPlayerTwoGiveWO(match)"> (W.O)</span>
        </td>
        <td>
          <p class="pe-2 d-inline" v-for="game in match.result.gameList" :key="game.id">
            {{ game.firstRegistrationResult }}-{{ game.secondRegistrationResult }}
          </p>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import {
  didPlayerOneGiveWO, didPlayerTwoGiveWO,
  getPlayerOne,
  getPlayerTwo,
  isPlayerOneWinner,
  isPlayerTwoWinner
} from "@/common/util";

export default {
  name: "MatchListComponent",
  props: ["matches"],
  methods: {
    getPlayerOne: getPlayerOne,
    getPlayerTwo: getPlayerTwo,
    getTime(match) {
      if (match.startTime === null) {
        return this.$t("draw.pool.noTime")
      }
    },
    isPlayerOneWinner: isPlayerOneWinner,
    isPlayerTwoWinner: isPlayerTwoWinner,
    didPlayerOneGiveWO: didPlayerOneGiveWO,
    didPlayerTwoGiveWO: didPlayerTwoGiveWO
  }
}
</script>

<style scoped>
tr td {
  min-width: 150px;
}
</style>