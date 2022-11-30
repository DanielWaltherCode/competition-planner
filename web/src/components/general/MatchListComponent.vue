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
        <td>{{ getTime(match) }}
          <span v-if="match.startTime === null">
          <i class="ms-2 fas fa-pen-square clickable" @click="showSetTimeModal = true; selectedMatch = match"></i>
        </span>
        </td>
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
    <vue-final-modal v-model="showSetTimeModal" classes="modal-container" content-class="modal-content">
      <div class="modal__content" style="min-height: 400px">
        <i class="modal__close fas fa-times clickable" @click="resetStartTime"></i>
        <div class="w-50 m-auto">
          <h2>{{ $t("draw.main.setStartTime") }}</h2>
          <datetime v-if="showSetTimeModal" format="YYYY-MM-DD H:i" first-day-of-week="1"
                    width="300px" v-model="selectedStartTime"></datetime>
        </div>
      </div>
      <div class="w-50 m-auto mt-3">
        <div class="modal-footer p-2">
          <button type="button" class="btn btn-primary" @click="setStartTime">
            {{ $t("general.save") }}
          </button>
          <button type="button" class="btn btn-secondary" @click="resetStartTime">
            {{ $t("general.close") }}
          </button>
        </div>
      </div>
    </vue-final-modal>
  </div>
</template>

<script>
import datetime from 'vuejs-datetimepicker';
import {
  didPlayerOneGiveWO, didPlayerTwoGiveWO,
  getPlayerOne,
  getPlayerTwo,
  isPlayerOneWinner,
  isPlayerTwoWinner
} from "@/common/util";
import ScheduleGeneralService from "@/common/api-services/schedule/schedule-general.service";

export default {
  name: "MatchListComponent",
  components: {datetime},
  props: ["matches"],
  data() {
    return {
      showSetTimeModal: false,
      selectedMatch: null,
      selectedStartTime: null
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  methods: {
    getPlayerOne: getPlayerOne,
    getPlayerTwo: getPlayerTwo,
    getTime(match) {
      if (match.startTime === null) {
        return this.$t("draw.pool.noTime")
      }
      return match.startTime
    },
    setStartTime() {
      ScheduleGeneralService.updateMatchTime(this.competition.id,
          this.selectedMatch.id, {matchTime: this.selectedStartTime}).then(() => {
            this.resetStartTime()
      })
    },
    resetStartTime() {
      this.showSetTimeModal = false;
      this.selectedStartTime = null;
      this.selectedMatch = null
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