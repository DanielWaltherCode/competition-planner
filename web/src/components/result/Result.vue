<template>
  <div>
    <h1 class="p-4">
      <i class="fas fa-arrow-left" style="float: left" @click="$router.push('/schedule')" />
      {{ $t("results.title") }}
      <i class="fas fa-arrow-right" style="float: right" @click="$router.push('/billing')" />
    </h1>
    <div class="container-fluid">
      <div class="row">
        <!-- Main content -->
        <div id="main" class="p-4">
          <!-- Filters -->
          <div class="row mb-2 d-flex align-items-center p-3 bg-grey">
            <div class="col-12 col-md-6">
              <div class="d-flex align-items-center mb-2">
                <i class="fas fa-search me-2" />
                <label for="playerSearch" class="form-label d-flex mb-0"> {{ $t("results.search") }}</label>
              </div>
              <div class="d-flex">
                <input id="playerSearch" v-model="searchString" type="text" class="form-control me-3">
                <button type="button" class="btn btn-primary" @click="searchString = ''">
                  {{ $t("general.clear") }}
                </button>
              </div>
            </div>
            <div class="col-5 col-md-2 ms-4 form-check d-flex justify-content-start">
              <input id="hide-finished-matches" v-model="hideFinishedMatches" type="checkbox" class="form-check-input">
              <label class="form-check-label ms-2" for="hide-finished-matches">{{ $t("results.hideFinished") }}</label>
            </div>
            <div class="col-5 col-md-2">
              <label for="category-choice">{{ $t("schedule.main.dropdownPlaceholder") }}</label>
              <select id="category-choice" v-model="selectedCategory" class="form-select my-3 mx-auto">
                <option value="">
                  {{ $t("results.categoryDefaultChoice") }}
                </option>
                <option v-for="category in competitionCategories" :key="category" :value="category">
                  {{ tryTranslateCategoryName(category) }}
                </option>
              </select>
            </div>
          </div>
          <div v-if="matches.length > 0" id="table-container" class="table-responsive">
            <table class="table table-borderless table-striped">
              <thead>
              <tr>
                <th scope="col" class="col-1">
                  {{ $t("results.startTime") }}
                </th>
                <th scope="col" class="col-1 d-none d-md-table-cell">
                  {{ $t("results.category") }}
                </th>
                <th scope="col" class="col-1 d-none d-md-table-cell">
                  {{ $t("results.round") }}
                </th>
                <th scope="col" class="col-2" />
                <th scope="col" class="col-2" />
                <th scope="col" class="col-2 text-start">
                  {{ $t("results.result") }}
                </th>
                <th scope="col" class="col-1">
                  {{ $t("results.report") }}
                </th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="match in filterMatches" :key="match.id">
                <td v-if="match.startTime !== null && match.startTime !== ''">
                  {{ getTime(match) }}
                </td>
                <td v-else>
                  <datetime v-model="match.startTime" type="datetime" @close="setStartTime(match)" />
                </td>
                <td class="d-none d-md-table-cell">
                  {{ tryTranslateCategoryName(match.competitionCategory.name) }}
                </td>
                <td class="d-none d-md-table-cell">
                  <span v-if="match.matchType === 'GROUP'"> {{ $t("results.group") + ' ' + match.groupOrRound }}</span>
                  <span v-else>{{ $t("round." + match.groupOrRound) }}</span>
                </td>
                <td class="text-start" :class="isPlayerOneWinner(match) ? 'fw-bold': ''">
                  {{ getPlayerOne(match) }}
                </td>
                <td class="text-start" :class="isPlayerTwoWinner(match) ? 'fw-bold': ''">
                  {{ getPlayerTwo(match) }}
                </td>
                <td v-if="match !== null && match.result.gameList.length > 0" class="text-start">
                  <p v-for="game in match.result.gameList" :key="game.id" class="pe-2 pb-0 d-inline">
                    {{ game.firstRegistrationResult }} - {{ game.secondRegistrationResult }}
                  </p>
                </td>
                <td v-else />
                <td>
                  <button type="button" class="btn btn-outline-primary" @click="selectMatch(match)">
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
                  @close="hideModal"
                  @closeAndUpdate="hideModalAndUpdate"
                  @walkover="onWalkover" />
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
import {
  getFormattedDate, getHoursMinutes,
  getPlayerOneWithClub,
  getPlayerTwoWithClub,
  isPlayerOneWinner,
  isPlayerTwoWinner, shouldShowPlayoff
} from "@/common/util";
import RegisterResult from "@/components/result/RegisterResult";
import {tryTranslateCategoryName} from "@/common/util"
import ScheduleGeneralService from "@/common/api-services/schedule/schedule-general.service";
import {Datetime} from 'vue-datetime';

export default {
  name: "ResultComponent",
  components: {RegisterResult, Datetime},
  data() {
    return {
      activeResultsReporting: false,
      matches: [],
      selectedMatch: null,
      gameResults: [],
      nrGames: null,
      showModal: false,
      searchString: "",
      hideFinishedMatches: false,
      competitionCategories: [],
      selectedCategory: "",
      matchForTimeSetting: null,
      selectedStartTime: null
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    },
    filterMatches() {
      let filteredMatches = []
      if (this.hideFinishedMatches) {
        this.matches.forEach(match => {
          if (match.winner.length === 0) {
            filteredMatches.push(match)
          }
        })
      } else {
        filteredMatches = this.matches
      }

      if (this.selectedCategory !== "") {
        filteredMatches = filteredMatches.filter(match => match.competitionCategory.name === this.selectedCategory)
      }

      let matchesWithSearchString = []
      if (this.searchString !== "") {
        filteredMatches.forEach(match => {
          if (this.getPlayerOne(match).toLowerCase().includes(this.searchString.toLowerCase())) {
            matchesWithSearchString.push(match)
          } else if (this.getPlayerTwo(match).toLowerCase().includes(this.searchString.toLowerCase())) {
            matchesWithSearchString.push(match)
          }
        })
      } else {

        return filteredMatches
      }
      return matchesWithSearchString
    },
  },
  mounted() {
    this.getMatches()
    this.reset()
  },
  methods: {
    // TODO -- Don't call getMatches() all the time, process changes locally and only reload all on hard refresh
    getMatches() {
      MatchService.getMatchesInCompetition(this.competition.id).then(res => {
        const matchesWithPlaceholders = res.data
        this.matches = matchesWithPlaceholders.filter(match => match.firstPlayer[0].id !== -1 && match.secondPlayer[0].id !== -1)
        const allCategories = this.matches.map(match => match.competitionCategory)
        this.competitionCategories = new Set()
        allCategories.forEach(category => {
          this.competitionCategories.add(category.name)
        })
      })
    },
    reset() {
      this.selectedMatch = null
      this.matchForTimeSetting = null
      this.showModal = false
    },
    getPlayerOne: getPlayerOneWithClub,
    getPlayerTwo: getPlayerTwoWithClub,
    getTime(match) {
      if (match != null && match.startTime === null) {
        return this.$t("draw.pool.noTime")
      }
      return match.startTime
    },
    isPlayerOneWinner: isPlayerOneWinner,
    isPlayerTwoWinner: isPlayerTwoWinner,
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
            this.updateMatchesIfAllMatchesPlayedInRound(this.matches[i])
          }
        }
      })
      this.hideModal()
    },
    updateMatchesIfAllMatchesPlayedInRound(match) {
      if (match.matchType === "PLAYOFF") {
        let round = match.groupOrRound;
        let competitionCategoryId = match.competitionCategory.id
        let matchesInSameRoundAndCategory = this.matches.filter(m => m.groupOrRound === round && m.competitionCategory.id === competitionCategoryId)
        let allMatchesPlayedInRound = matchesInSameRoundAndCategory.every(m => m.winner.length > 0)
        if (allMatchesPlayedInRound) {
          this.getMatches()
        }
      }
    },
    onWalkover() {
      this.getMatches()
      this.hideModal()
    },
    hideModal() {
      this.showModal = false
    },
    setStartTime(match) {
      if (match.startTime !== null && match.startTime !== "") {
        const date = new Date(match.startTime)
        const formattedTime = `${getFormattedDate(date)} ${getHoursMinutes(date)}`
        if (formattedTime.length > 6) {
          ScheduleGeneralService.updateMatchTime(
              this.competition.id, match.id, {matchTime: formattedTime})
              .then(() => {
                match.startTime = formattedTime
                this.resetStartTime()
              })
        }
      }
    },
    resetStartTime() {
      this.showSetTimeModal = false;
      this.selectedStartTime = null;
      this.matchForTimeSetting = null
    },
    tryTranslateCategoryName: tryTranslateCategoryName,
    getFormattedDate: getFormattedDate,
    getHoursMinutes: getHoursMinutes
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