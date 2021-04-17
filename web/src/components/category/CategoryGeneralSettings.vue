<template>
<div>
  <form>
    <div class="row">
      <div class="col">
        <label for="inputCost">Kostnad</label>
        <input v-model="category.cost" type="text" class="form-control" placeholder="Summa" id="inputCost">
      </div>
      <div class="col">
        <label for="inputNumberOfPlayersPerPool">Antal spelare per pool</label>
        <input v-model="category.playersPerPool" type="text" class="form-control" placeholder="4" id="inputNumberOfPlayersPerPool">
      </div>
    </div>
    <div class="row">
      <div class="col">
      <h5>Lottningstyp</h5>
      <div class="form-check form-check-inline">
        <input v-model="category.drawType" value="POOL" class="form-check-input" type="radio" name="inlineRadioOptions" id="inputDrawTypePool">
        <label class="form-check-label" for="inputDrawTypePool">Pool</label>
      </div>
      <div class="form-check form-check-inline">
        <input v-model="category.drawType" value="POOLANDCUP" class="form-check-input" type="radio" name="inlineRadioOptions" id="inputDrawTypePoolAndCup">
        <label class="form-check-label" for="inputDrawTypePoolAndCup">Pool + Cupspel</label>
      </div>
      <div class="form-check form-check-inline">
        <input v-model="category.drawType" value="CUP" class="form-check-input" type="radio" name="inlineRadioOptions" id="inputDrawTypeCup">
        <label class="form-check-label" for="inputDrawTypeCup">Cupspel</label>
      </div>
      </div>
    </div>
    <div v-if="category.drawType === 'CUP' || category.drawType === 'POOLANDCUP'" class="row">
      <div class="col">
        <label for="inputPlayersThatAdvance">Antal spelare some går vidare</label>
        <input v-model="category.playersThatAdvancePerGroup" type="text" class="form-control" placeholder="2" id="inputPlayersThatAdvance">
      </div>
      <div class="col">
        <label for="inputDrawStrategy">Lottningstrategi</label>
        <input v-model="category.drawStrategy" type="text" class="form-control" placeholder="Snakelottning" id="inputDrawStrategy">
      </div>
    </div>
  </form>
</div>
</template>

<script>
export default {
  name: "CategoryGeneralSettings",
  props: {
    category: Object
  }
}
</script>

<style scoped>

</style>