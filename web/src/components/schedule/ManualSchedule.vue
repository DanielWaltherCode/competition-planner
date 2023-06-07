<template>
  <div>
    <h1 class="p-4">
      Manuell tidssättning
    </h1>
    <div class="container-fluid">
      <div class="row">
        <!-- Main content -->
        <div id="main" class="p-4">
          <!-- Filters -->
          <div class="row mb-2 d-flex align-items-center p-3 bg-grey">
            <div class="col-12 col-md-6">
              <div class="d-flex align-items-center mb-2">
                <i class="fas fa-search me-2"/>
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
          <div>
            <button class="btn btn-warning" v-if="matchesChanged" @click="updateStartTimes">
              {{ $t("general.saveChanges") }}
            </button>
          </div>
          <table>
            <thead>
            <tr>
              <th scope="col" class="col-1 d-none d-md-table-cell">
                {{ $t("results.category") }}
              </th>
              <th scope="col" class="col-1 d-none d-md-table-cell">
                {{ $t("results.round") }}
              </th>
              <th scope="col" class="col-2"/>
              <th scope="col" class="col-2"/>
              <th scope="col" class="col-1">
                {{ $t("results.startTime") }}
              </th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(match, index) in filterMatches" :key="index">
              <td>
                {{ tryTranslateCategoryName(match.competitionCategory.name) }}
              </td>
              <td>
                <span v-if="match.matchType === 'GROUP'"> {{ $t("results.group") + ' ' + match.groupOrRound }}</span>
                <span v-else>{{ $t("round." + match.groupOrRound) }}</span>
              </td>
              <td class="text-start" :class="isPlayerOneWinner(match) ? 'fw-bold': ''">
                {{ getPlayerOne(match) }}
              </td>
              <td class="text-start" :class="isPlayerTwoWinner(match) ? 'fw-bold': ''">
                {{ getPlayerTwo(match) }}
              </td>
              <td><input type="datetime-local" v-model="match.startTime" class="form-control"
                         @change="matchesChanged = true"></td>
            </tr>
            </tbody>
          </table>

        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MatchService from "@/common/api-services/match.service";
import {
  getFormattedDate,
  getHoursMinutes,
  getPlayerOneWithClub,
  getPlayerTwoWithClub, isPlayerOneWinner, isPlayerTwoWinner,
  tryTranslateCategoryName
} from "@/common/util";
import ScheduleGeneralService from "@/common/api-services/schedule/schedule-general.service";

export default {
  name: "ManualSchedule",
  data() {
    return {
      matches: [],
      searchString: "",
      hideFinishedMatches: true,
      selectedCategory: "",
      competitionCategories: [],
      matchesChanged: false
    };
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
    this.getMatches();
  },
  methods: {
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
    getPlayerOne: getPlayerOneWithClub,
    getPlayerTwo: getPlayerTwoWithClub,
    isPlayerOneWinner: isPlayerOneWinner,
    isPlayerTwoWinner: isPlayerTwoWinner,
    updateStartTimes() {
      this.matches.forEach(match => {
        if (match.startTime !== null && match.startTime !== "") {
          const date = new Date(match.startTime)
          const formattedTime = `${getFormattedDate(date)} ${getHoursMinutes(date)}`
          if (formattedTime.length > 6) {
            ScheduleGeneralService.updateMatchTime(
                this.competition.id, match.id, {matchTime: formattedTime})
                .then(() => {
                  match.startTime = formattedTime
                })
          }
        }
      })
      this.matchesChanged = false
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
</style>