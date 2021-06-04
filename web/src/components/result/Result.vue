<template>
  <div>
    <div class="container-fluid">
      <div class="row gx-5">
        <!-- Main content -->
        <div id="main">
          <h1> {{ $t("results.heading") }}</h1>
          <div id="table-container" class="table-responsive" v-if="matches.length > 0">
            <table class="table table-borderless">
              <thead>
              <tr>
                <th scope="col" class="col-1">{{ $t("results.startTime") }}</th>
                <th scope="col" class="col-1">{{ $t("results.category") }}</th>
                <th scope="col" class="col-1">
                  {{ $t("results.round") }}
                </th>
                <th scope="col" class="col-2"></th>
                <th scope="col" class="col-2"></th>
                <th scope="col" class="col-2">{{ $t("results.result") }}</th>
                <th scope="col" class="col-1"></th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="match in matches" :key="match.id">
                <td>{{ getTime(match) }}</td>
                <td>{{ match.competitionCategory.categoryName }}</td>
                <td>
                  <span v-if="match.matchType === 'GROUP'"> {{$t("results.group")}}</span>
                  {{ match.groupOrRound}}
                </td>
                <td :class="isPlayerOneWinner(match) ? 'fw-bold': ''">{{ getPlayerOne(match) }}</td>
                <td :class="isPlayerTwoWinner(match) ? 'fw-bold': ''">{{ getPlayerTwo(match) }}</td>
                <td v-if="match !== null" class="d-flex justify-content-center">
                  <p class="pe-2" v-for="game in match.result.gameList" :key="game.id">
                    {{ game.firstRegistrationResult }} - {{ game.secondRegistrationResult}}
                  </p></td>
                <td><p class="report-result" @click="registerResult(match)"
                       data-bs-toggle="modal" data-bs-target="#resultModal">
                  <span v-if="match.result.gameList.length === 0">{{ $t("results.register") }}</span>
                  <span v-if="match.result.gameList.length > 0">{{ $t("results.update") }}</span>
                </p></td>
              </tr>
              </tbody>
            </table>
            <!-- Modal -->
            <div class="modal fade" id="resultModal" tabindex="-1" aria-labelledby="resultModalLabel"
                 aria-hidden="true">
              <div class="modal-dialog">
                <div class="modal-content">
                  <div class="modal-header">
                    <h5 class="modal-title" id="resultModalLabel"></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                  </div>
                  <div class="modal-body">
                    <div id="modal-table" class="col-12 m-auto">
                      <table class="table table-borderless">
                        <thead>
                        <tr>
                          <th class="col-4"></th>
                          <th></th>
                          <th></th>
                          <th></th>
                          <th></th>
                          <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <!-- Player 1 -->
                        <tr v-if="this.selectedMatch !== null">
                          <td>{{ getPlayerOne(this.selectedMatch) }}</td>
                          <td v-for="game in selectedMatch.result.gameList" :key="game.id">
                            <select class="form-control" v-model="game.firstRegistrationResult">
                              <option v-for="i in 30" :key="i" :value="i">
                                {{ i }}
                              </option>
                            </select>
                          </td>
                          <td v-for="(game, index) in gamesToAdd" :key="index">
                            <select class="form-control" v-model="game.firstRegistrationResult">
                              <option v-for="i in 30" :key="i" :value="i">
                                {{ i }}
                              </option>
                            </select>
                          </td>
                        </tr>
                        <!-- Player 2 -->
                        <tr v-if="selectedMatch !== null">
                          <td>{{ getPlayerTwo(selectedMatch) }}</td>
                          <td v-for="game in selectedMatch.result.gameList" :key="game.id">
                            <select class="form-control" v-model="game.secondRegistrationResult">
                              <option v-for="i in 30" :key="i" :value="i">
                                {{ i }}
                              </option>
                            </select>
                          </td>
                          <td v-for="(game, index) in gamesToAdd" :key="index">
                            <select class="form-control" v-model="game.secondRegistrationResult">
                              <option v-for="i in 30" :key="i" :value="i">
                                {{ i }}
                              </option>
                            </select>
                          </td>
                        </tr>
                        </tbody>
                      </table>
                    </div>
                  </div>
                  <div class="modal-footer">
                    <button type="button" class="btn btn-primary" @click="addGame">{{ $t("results.modal.add") }}
                    </button>
                    <button type="button" class="btn btn-primary" @click="removeGame">{{ $t("results.modal.remove") }}
                    </button>
                    <button type="button" class="btn btn-secondary" @click="closeWithoutSaving" data-bs-dismiss="modal">
                      {{ $t("general.close") }}
                    </button>
                    <button type="button" class="btn btn-primary" @click="saveResults" data-bs-dismiss="modal">{{ $t("general.save") }}</button>
                    <button type="button" class="btn btn-danger" @click="deleteResults" data-bs-dismiss="modal">{{ $t("results.modal.delete") }}</button>
                  </div>
                </div>
              </div>
            </div>

          </div>
          <div v-if="matches.length === 0">
            <p>
              No draw
              {{ matches.length }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MatchService from "@/common/api-services/match.service";
import ResultService from "@/common/api-services/result.service";

export default {
  name: "ResultComponent",
  data() {
    return {
      activeResultsReporting: false,
      matches: [],
      selectedMatch: null,
      gameResults: [],
      removedGames: [],
      gamesToAdd: []
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  mounted() {
    this.getMatches()
  },
  methods: {
    // TODO -- Don't call getMatches() all the time, process changes locally and only reload all on hard refresh
    getMatches() {
      MatchService.getMatchesInCompetition(this.competition.id).then(res => {
        this.matches = res.data
      })
    },
    // TODO - move to util
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
    },
    registerResult(match) {
      // Ensure helper arrays are empty before setting results
      this.gamesToAdd = []
      this.removedGames = []
      this.selectedMatch = match
      if (this.selectedMatch.result.gameList.length === 0) {
        this.addGame()
      }
    },
    closeWithoutSaving() {
      // If games were removed or added, ensure these actions are undone
      this.gamesToAdd = []
      this.removedGames.forEach(game => {
        this.selectedMatch.result.gameList.push(game)
      })
      this.removedGames = []
      this.selectedMatch = null
    },
    removeGame() {
      if (this.gamesToAdd.length > 0) {
        this.removedGames.push(this.gamesToAdd.pop())
      } else if (this.selectedMatch.result.gameList.length > 0) {
        this.removedGames.push(this.selectedMatch.result.gameList.pop())
      }
    },
    addGame() {
      this.gamesToAdd.push(
          {
            firstRegistrationResult: 0,
            secondRegistrationResult: 0,
            gameNumber: (this.gamesToAdd.length + this.selectedMatch.result.gameList.length) + 1
          })
    },
    saveResults() {
      // If there are existing games, update result
      if (this.selectedMatch.result.gameList.length > 0) {
        const gameList = this.selectedMatch.result.gameList.concat(this.gamesToAdd)
        ResultService.updateResult(this.selectedMatch.id, {gameList: gameList}).then(() => {
          this.selectedMatch = null
          this.getMatches()
        })
      }
      else {
        ResultService.addResult(this.selectedMatch.id, {gameList: this.gamesToAdd}).then(() => {
          this.selectedMatch = null
          this.getMatches()
        }).catch(err => {
          console.log(err.data)
        })
      }

    },
    deleteResults() {
      ResultService.deleteResult(this.selectedMatch.id)
      this.selectedMatch = null
      this.getMatches()
    }
  }
}
</script>

<style scoped>
th {
  text-decoration: underline;
}

.report-result:hover {
  cursor: pointer;
  opacity: 0.7;
}
</style>