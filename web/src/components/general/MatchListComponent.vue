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
        <td :class="isPlayerOneWinner(match) ? 'fw-bolder' : ''">{{ getPlayerOne(match) }}</td>
        <td :class="isPlayerTwoWinner(match) ? 'fw-bolder' : ''">{{ getPlayerTwo(match) }}</td>
        <td>
          <p class="pe-2 d-inline" v-for="game in match.result.gameList" :key="game.id">
            {{ game.firstRegistrationResult }}-{{ game.secondRegistrationResult }}
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
import {getPlayerOne, getPlayerTwo, isPlayerOneWinner, isPlayerTwoWinner} from "@/common/util";

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
  }
}
</script>

<style scoped>

</style>