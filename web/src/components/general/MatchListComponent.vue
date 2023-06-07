<template>
  <div v-if="matches.length !== 0" class="table-responsive">
    <table class="table table-bordered table-striped table-sm">
      <thead class="thead-dark">
      <tr>
        <th>{{ $t("draw.pool.time") }}</th>
        <th />
        <th />
        <th>{{ $t("draw.pool.result") }}</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="match in matches" :key="match.id" class="group-matches">
        <td>
          {{ getTime(match) }}
        </td>
        <td :class="isPlayerOneWinner(match) ? 'fw-bolder' : ''">
          {{ getPlayerOne(match) }} <span v-if="didPlayerOneGiveWO(match)"> (W.O)</span>
        </td>
        <td :class="isPlayerTwoWinner(match) ? 'fw-bolder' : ''">
          {{ getPlayerTwo(match) }}
          <span v-if="didPlayerTwoGiveWO(match)"> (W.O)</span>
        </td>
        <td>
          <p v-for="game in match.result.gameList" :key="game.id" class="pe-2 d-inline">
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
  didPlayerOneGiveWO, didPlayerTwoGiveWO, getMatchTime,
  getPlayerOne,
  getPlayerTwo,
  isPlayerOneWinner,
  isPlayerTwoWinner
} from "@/common/util";

export default {
  name: "MatchListComponent",
  props: ["matches"],
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  methods: {
    getPlayerOne: getPlayerOne,
    getPlayerTwo: getPlayerTwo,
    getTime: getMatchTime,
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

::v-deep .modal-container {
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: scroll;
}

::v-deep .modal-content {
  max-height: 90%;
  max-width: 75%;
  margin: 0 1rem;
  padding: 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.25rem;
  background: #fff;
  overflow: scroll;
}
</style>