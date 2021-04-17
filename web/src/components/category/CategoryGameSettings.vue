<template>
<div>
  <form>
    <div class="row">
      <div class="col">
        <label for="inputNumberOfSets">Antal set</label>
        <input v-model="category.defaultGameSettings.numberOfSets" type="text" class="form-control" placeholder="5" id="inputNumberOfSets">
      </div>
      <div class="col">
        <label for="inputPlayingUntil">Man spelar till</label>
        <input v-model="category.defaultGameSettings.playingUntil" type="text" class="form-control" placeholder="4" id="inputPlayingUntil">
      </div>
    </div>
    <div class="row">
      <div class="col">
        <label for="inputWinMargin">Vinstmarginal</label>
        <input v-model="category.defaultGameSettings.winMargin" type="text" class="form-control" placeholder="2" id="inputWinMargin">
      </div>
    </div>
    <div class="row">
      <div class="form-check">
        <input v-model="category.endGameSettings.enabled" class="form-check-input" type="checkbox" value="" id="inputEndGameSettingsEnabled">
        <label class="form-check-label" for="inputEndGameSettingsEnabled">Annan matchlängd i slutspel</label>
      </div>
    </div>
    <div v-if="category.endGameSettings.enabled === true" class="row">
      <div class="col">
        <label for="inputEndGameNumberOfSets">Antal set</label>
        <input v-model="category.endGameSettings.numberOfSets" type="text" class="form-control" placeholder="4" id="inputEndGameNumberOfSets">
      </div>
      <div class="col">
        <label for="inputEndGamePlayingUntil">Man spelar till</label>
        <input v-model="category.endGameSettings.playingUntil" type="text" class="form-control" placeholder="4" id="inputEndGamePlayingUntil">
      </div>
      <div class="col">
        <label for="inputEndGameWinMargin">Vinstmarginal</label>
        <input v-model="category.endGameSettings.winMargin" type="text" class="form-control" placeholder="4" id="inputEndGameWinMargin">
      </div>
    </div>
    <div class="row">
      <div class="form-check">
        <input v-model="category.tiebreakSettings.enabled" class="form-check-input" type="checkbox" value="" id="inputTiebreakSettingsEnabled">
        <label class="form-check-label" for="inputTiebreakSettingsEnabled">Tiebreak</label>
      </div>
    </div>
    <div v-if="category.tiebreakSettings.enabled === true" class="row">
      <div class="col">
        <label for="inputTiebreakPlayingUntil">Man spelar till</label>
        <input v-model="category.tiebreakSettings.playingUntil" type="text" class="form-control" placeholder="4" id="inputTiebreakPlayingUntil">
      </div>
      <div class="col">
        <label for="inputTiebreakWinMargin">Vinstmarginal</label>
        <input v-model="category.tiebreakSettings.winMargin" type="text" class="form-control" placeholder="4" id="inputTiebreakWinMargin">
      </div>
    </div>
  </form>
</div>
</template>

<script>
export default {
  name: "CategoryGameSettings",
  props: {
    category: Object
  },
  data: function() {
    return {

    }
  }
}
</script>

<style scoped>

</style>