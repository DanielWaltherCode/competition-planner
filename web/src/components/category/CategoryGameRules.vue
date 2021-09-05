<template>
<div v-if="gameRules != null">
  <div class="row">
    <div class="d-flex col-12 p-2 justify-content-end">
      <button class="btn btn-primary" type="button" @click="saveChanges">{{$t("general.saveChanges")}}</button>
    </div>
    <div class="col-md-6 mb-3">
      <label class="h5 form-label" for="inputNumberOfSets">{{$t("categoryGameRules.nrSets")}}</label>
      <input v-model="gameRules.nrSets" type="text" class="form-control" id="inputNumberOfSets">
    </div>
    <div class="col-md-6 mb-3">
      <label class="h5 form-label" for="inputPlayingUntil">{{$t("categoryGameRules.winScore")}}</label>
      <input v-model="gameRules.winScore" type="text" class="form-control" id="inputPlayingUntil">
    </div>
  </div>
  <div class="row">
    <div class="col mb-5">
      <label class="h5 form-label" for="inputWinMargin">{{$t("categoryGameRules.winMargin")}}</label>
      <input v-model="gameRules.winMargin" type="text" class="form-control" placeholder="2" id="inputWinMargin">
    </div>
  </div>
  <div class="form-check">
    <input v-model="differentRulesInEndgame" class="form-check-input" type="checkbox" value="" id="inputEndGameSettingsEnabled">
    <label class="h4 form-check-label" for="inputEndGameSettingsEnabled">{{$t("categoryGameRules.differentEndgame")}}</label>
  </div>
  <div v-if="differentRulesInEndgame" class="row">
    <div class="col-sm-12 col-md-6 mb-3">
      <label class="h5 form-label" for="newNrSetsFromRound"> {{$t("categoryGameRules.differentEndgameFrom")}}</label>
      <select id="newNrSetsFromRound" v-model="gameRules.differentNumberOfGamesFromRound" class="form-select">
        <option v-for="option in possibleRounds" :key="option" :value="option">
          {{$t("round." + option)}}
        </option>
      </select>
    </div>
    <div class="col-sm-12 col-md-6 mb-3 d-flex flex-column justify-content-end">
      <label class="h5 form-label" for="inputEndGameNumberOfSets">{{$t("categoryGameRules.nrSetsEndgame")}}</label>
      <input v-model="gameRules.nrSetsFinal" type="text" class="form-control" id="inputEndGameNumberOfSets">
    </div>
    <div class="col-sm-12 mb-5">
      <label class="h5 form-label" for="inputEndGameWinMargin">{{$t("categoryGameRules.winMarginFinals")}}</label>
      <input v-model="gameRules.winMarginFinal" type="text" class="form-control" id="inputEndGameWinMargin">
    </div>
  </div>
  <div class="form-check">
    <input v-model="gameRules.tiebreakInFinal" class="form-check-input" type="checkbox" id="inputTiebreakSettingsEnabled">
    <label class="h4 form-check-label" for="inputTiebreakSettingsEnabled">{{$t("categoryGameRules.useTiebreak")}}</label>
  </div>
  <div v-if="gameRules.tiebreakInFinal" class="row mb-5">
    <div class="col-sm-12 col-md-6">
      <label class="h5 form-label" for="inputTiebreakPlayingUntil">{{$t("categoryGameRules.winScoreTiebreak")}}</label>
      <input v-model="gameRules.winScoreTiebreak" type="text" class="form-control" placeholder="4" id="inputTiebreakPlayingUntil">
    </div>
    <div class="col-sm-12 col-md-6">
      <label class="h5 form-label" for="inputTiebreakWinMargin">{{$t("categoryGameRules.winMarginTiebreak")}}</label>
      <input v-model="gameRules.winMarginTiebreak" type="text" class="form-control" id="inputTiebreakWinMargin">
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
    category: Object
  },
  data() {
    return {
      gameRules: null,
      differentRulesInEndgame: false,
      possibleRounds: []
    }
  },
  mounted() {
    CategoryService.getCategoryGameRules(this.category.id).then( res => {
      this.gameRules = res.data
      if (this.gameRules.differentNumberOfGamesFromRound !== "UKNOWN") {
        this.differentRulesInEndgame = true
      }
    })
    CompetitionService.getPossibleRounds().then( res => {
      this.possibleRounds = res.data
    })
  },
  methods: {
    saveChanges() {
      CategoryService.updateGameRules(this.category.id, this.gameRules.id, this.gameRules).then(() => {
        console.log("Success!")
      })
    }
  }
}
</script>

<style scoped>
</style>