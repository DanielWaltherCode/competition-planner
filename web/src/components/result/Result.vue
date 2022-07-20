<template>
  <div>
    <h1 class="p-4">
      <i @click="$router.push('/schedule')" class="fas fa-arrow-left" style="float: left"></i>
      {{ $t("results.title") }}
      <i @click="$router.push('/billing')" class="fas fa-arrow-right" style="float: right"></i>
    </h1>
    <div class="container-fluid">
      <div class="row">
        <!-- Main content -->
        <div id="main" class="p-4">
          <div class="col-sm-7 mb-2">
            <div class="d-flex align-items-center mb-2">
              <i class="fas fa-search me-2"></i>
              <label for="playerSearch" class="form-label d-flex mb-0"> {{ $t("results.search") }}</label>
            </div>
            <div class="d-flex">
              <input type="text" class="form-control me-3" v-model="searchString" id="playerSearch">
              <button type="button" class="btn btn-primary" @click="searchString = ''">{{$t("general.clear")}}</button>
            </div>
          </div>
          <div id="table-container" class="table-responsive" v-if="matches.length > 0">
            <table class="table table-borderless">
              <thead>
              <tr>
                <th scope="col" class="col-1">{{ $t("results.startTime") }}</th>
                <th scope="col" class="col-1" v-if="!isMobile">{{ $t("results.category") }}</th>
                <th scope="col" class="col-1" v-if="!isMobile">
                  {{ $t("results.round") }}
                </th>
                <th scope="col" class="col-2"></th>
                <th scope="col" class="col-2"></th>
                <th scope="col" class="col-2">{{ $t("results.result") }}</th>
                <th scope="col" class="col-1"></th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="match in filterMatches" :key="match.id">
                <td>{{ getTime(match) }}</td>
                <td v-if="!isMobile">{{ match.competitionCategory.name }}</td>
                <td v-if="!isMobile">
                  <span v-if="match.matchType === 'GROUP'"> {{ $t("results.group") + ' ' + match.groupOrRound }}</span>
                  <span v-else>{{ $t("round." + match.groupOrRound) }}</span>
                </td>
                <td :class="isPlayerOneWinner(match) ? 'fw-bold': ''">{{ getPlayerOne(match) }}</td>
                <td :class="isPlayerTwoWinner(match) ? 'fw-bold': ''">{{ getPlayerTwo(match) }}</td>
                <td v-if="match !== null" class="d-flex justify-content-center">
                   <p class="pe-2" v-for="game in match.result.gameList" :key="game.id">
                    {{ game.firstRegistrationResult }} - {{ game.secondRegistrationResult }}
                  </p></td>
                <td>
                  <button type="button" class="btn btn-light" @click="selectMatch(match)">
                    <span v-if="match.result.gameList.length === 0">{{ $t("results.register") }}</span>
                    <span v-if="match.result.gameList.length > 0">{{ $t("results.update") }}</span>
                  </button>
                </td>
              </tr>
              </tbody>
            </table>
            <!-- Modal -->
            <vue-final-modal v-model="showModal" classes="modal-container" content-class="modal-content">
              <register-result
                  :selected-match="getMatchCopy()"
                  v-on:close="hideModal"
                  v-on:closeAndUpdate="hideModalAndUpdate"
                  v-on:walkover="onWalkover"></register-result>
            </vue-final-modal>
          </div>

        </div>
        <div v-if="matches.length === 0">
          <p>
            {{ $t("results.noMatches") }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MatchService from "@/common/api-services/match.service";
import {getPlayerOneWithClub, getPlayerTwoWithClub, isPlayerOneWinner, isPlayerTwoWinner} from "@/common/util";
import RegisterResult from "@/components/result/RegisterResult";

export default {
  name: "ResultComponent",
  components: {RegisterResult},
  data() {
    return {
      activeResultsReporting: false,
      matches: [],
      selectedMatch: null,
      gameResults: [],
      nrGames: null,
      showModal: false,
      searchString: ""
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    },
    filterMatches() {
      let filteredMatches = []
      if (this.searchString !== "") {
        this.matches.forEach(match => {
          if (this.getPlayerOne(match).toLowerCase().includes(this.searchString.toLowerCase())) {
            filteredMatches.push(match)
          } else if (this.getPlayerTwo(match).toLowerCase().includes(this.searchString.toLowerCase())) {
            filteredMatches.push(match)
          }
        })
      } else {
        return this.matches
      }
      return filteredMatches
    },
    isMobile: function () {
      const width = window.innerWidth
          || document.documentElement.clientWidth
          || document.body.clientWidth;
      console.log("width: " + width)
      console.log(width < 700)
      return width < 700;
    }
  },
  mounted() {
    this.getMatches()
  },
  methods: {
    // TODO -- Don't call getMatches() all the time, process changes locally and only reload all on hard refresh
    getMatches() {
      MatchService.getMatchesInCompetition(this.competition.id).then(res => {
        const matchesWithPlaceholders = res.data
        this.matches = matchesWithPlaceholders.filter(match => match.firstPlayer[0].id !== -1 && match.secondPlayer[0].id !== -1)
      })
    },
    getPlayerOne: getPlayerOneWithClub,
    getPlayerTwo: getPlayerTwoWithClub,
    getTime(match) {
      if (match != null && match.startTime === null) {
        return this.$t("draw.pool.noTime")
      }
    },
    isPlayerOneWinner: isPlayerOneWinner,
    isPlayerTwoWinner: isPlayerTwoWinner,
    removeGame() {
      if (this.gamesToAdd.length > 0) {
        this.removedGames.push(this.gamesToAdd.pop())
      } else if (this.selectedMatch.result.gameList.length > 0) {
        this.removedGames.push(this.selectedMatch.result.gameList.pop())
      }
    },
    selectMatch(selectedMatch) {
      this.selectedMatch = selectedMatch
      this.showModal = true
    },
    getMatchCopy() {
      return JSON.parse(JSON.stringify(this.selectedMatch));
    },
    hideModalAndUpdate(matchId) {
      MatchService.getMatch(this.competition.id, matchId).then(res => {
        for (let i = 0; i < this.matches.length; i++) {
          if (this.matches[i].id === matchId) {
            this.$set(this.matches, i, res.data)
          }
        }
      })
      this.hideModal()
    },
    onWalkover() {
      this.getMatches()
      this.hideModal()
    },
    hideModal() {
      this.showModal = false
    }
  }
}
</script>

<style scoped>

h1 {
  background-color: var(--clr-primary-100);
  margin-bottom: 0;
}

th {
  text-decoration: underline;
}

tr td {
  font-size: 90%;
}

.report-result:hover {
  cursor: pointer;
  opacity: 0.7;
}

::v-deep .modal-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

::v-deep .modal-content {
  max-height: 90%;
  max-width: 75%;
  margin: 0 1rem;
  padding: 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.25rem;
  background: #fff;
}

@media only screen and (max-width: 768px) {
  ::v-deep .modal-content {
    max-height: 90%;
    max-width: 95%;
    margin: 0 1rem;
    padding: 1rem;
    border: 1px solid #e2e8f0;
    border-radius: 0.25rem;
    background: #fff;
  }
  ::v-deep .modal-footer {
    justify-content: center;
  }
}

</style>