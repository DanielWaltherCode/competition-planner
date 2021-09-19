<template>
<div v-if="category.gameSettings != null">
  <div class="row">
    <div class="col-md-6 mb-3">
      <label class="h5 form-label" for="inputNumberOfSets">{{$t("categoryGameRules.nrSets")}}</label>
      <input v-model="category.gameSettings.numberOfSets" type="text" class="form-control" id="inputNumberOfSets">
    </div>
    <div class="col-md-6 mb-3">
      <label class="h5 form-label" for="inputPlayingUntil">{{$t("categoryGameRules.winScore")}}</label>
      <input v-model="category.gameSettings.winScore" type="text" class="form-control" id="inputPlayingUntil">
    </div>
  </div>
  <div class="row">
    <div class="col mb-5">
      <label class="h5 form-label" for="inputWinMargin">{{$t("categoryGameRules.winMargin")}}</label>
      <input v-model="category.gameSettings.winMargin" type="text" class="form-control" placeholder="2" id="inputWinMargin">
    </div>
  </div>
  <div class="form-check">
    <input v-model="category.gameSettings.useDifferentRulesInEndGame" class="form-check-input" type="checkbox" value="" id="inputEndGameSettingsEnabled">
    <label class="h4 form-check-label" for="inputEndGameSettingsEnabled">{{$t("categoryGameRules.differentEndgame")}}</label>
  </div>
  <div v-if="category.gameSettings.useDifferentRulesInEndGame" class="row">
    <div class="col-sm-12 col-md-6 mb-3">
      <label class="h5 form-label" for="newNrSetsFromRound"> {{$t("categoryGameRules.differentEndgameFrom")}}</label>
      <select id="newNrSetsFromRound" v-model="category.gameSettings.differentNumberOfGamesFromRound" class="form-select">
        <option v-for="option in possibleRounds" :key="option" :value="option">
          {{$t("round." + option)}}
        </option>
      </select>
    </div>
    <div class="col-sm-12 col-md-6 mb-3 d-flex flex-column justify-content-end">
      <label class="h5 form-label" for="inputEndGameNumberOfSets">{{$t("categoryGameRules.nrSetsEndgame")}}</label>
      <input v-model="category.gameSettings.numberOfSetsFinal" type="text" class="form-control" id="inputEndGameNumberOfSets">
    </div>
    <div class="col-sm-12 mb-5">
      <label class="h5 form-label" for="inputEndGameWinMargin">{{$t("categoryGameRules.winMarginFinals")}}</label>
      <input v-model="category.gameSettings.winMarginFinal" type="text" class="form-control" id="inputEndGameWinMargin">
    </div>
  </div>
  <div class="form-check">
    <input v-model="category.gameSettings.tiebreakInFinalGame" class="form-check-input" type="checkbox" id="inputTiebreakSettingsEnabled">
    <label class="h4 form-check-label" for="inputTiebreakSettingsEnabled">{{$t("categoryGameRules.useTiebreak")}}</label>
  </div>
  <div v-if="category.gameSettings.tiebreakInFinalGame" class="row mb-5">
    <div class="col-sm-12 col-md-6">
      <label class="h5 form-label" for="inputTiebreakPlayingUntil">{{$t("categoryGameRules.winScoreTiebreak")}}</label>
      <input v-model="category.gameSettings.winScoreTiebreak" type="text" class="form-control" placeholder="4" id="inputTiebreakPlayingUntil">
    </div>
    <div class="col-sm-12 col-md-6">
      <label class="h5 form-label" for="inputTiebreakWinMargin">{{$t("categoryGameRules.winMarginTiebreak")}}</label>
      <input v-model="category.gameSettings.winMarginTieBreak" type="text" class="form-control" id="inputTiebreakWinMargin">
    </div>
  </div>
</div>
</template>

<script>
import CategoryService from "@/common/api-services/category.service";
import CompetitionService from "@/common/api-services/competition.service";

export default {
  name: "CategoryGameSettings",
  props: {
    category: Object,
  },
  data() {
    return {
      possibleRounds: []
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  mounted() {
    CompetitionService.getPossibleRounds().then( res => {
      this.possibleRounds = res.data
    })
  },
  methods: {
    saveChanges() {
      CategoryService.updateCompetitionCategory(this.competition.id, this.category).then(() => {
        this.$toasted.show(this.$tc("toasts.categoryUpdated")).goAway(3000)
        console.log("Update success")
      })
    }
  }
}
</script>

<style scoped>
</style>