<template>
<div>
  <div class="row">
    <div class="col-sm-12 col-md-6 mb-3">
      <label class="h5 form-label" for="inputCost">Kostnad</label>
      <input v-model="category.cost" type="text" class="form-control" placeholder="Summa" id="inputCost">
    </div>
    <div class="col-sm-12 col-md-6 mb-3">
      <label class="h5 form-label" for="inputNumberOfPlayersPerPool">Spelare per pool</label>
      <input v-model="category.playersPerPool" type="text" class="form-control" placeholder="4" id="inputNumberOfPlayersPerPool">
    </div>
  </div>
  <div class="row">
      <fieldset class="col mb-3">
        <label class="h5 form-label">Lottningstyp</label>
        <div class="col"></div>
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
      </fieldset>
  </div>
  <div v-if="category.drawType === 'CUP' || category.drawType === 'POOLANDCUP'" class="row">
    <div class="col-sm-12 col-md-6 mb-3">
      <label class="h5 form-label" for="inputPlayersThatAdvance">Spelare som går vidare</label>
      <input v-model="category.playersThatAdvancePerGroup" type="text" class="form-control" placeholder="2" id="inputPlayersThatAdvance">
    </div>
    <div class="col-sm-12 col-md-6">
      <label class="h5 form-label" for="inputDrawStrategy">Lottningstrategi</label>
      <input v-model="category.drawStrategy" type="text" class="form-control" placeholder="Snakelottning" id="inputDrawStrategy">
    </div>
  </div>
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
@media (max-width: 767px){
  .form-check-inline{
    display: block;
  }
}
</style>