<template>
<div>
  <form>
    <div class="row">
      <div class="col">
        <label for="inputCost">Kostnad</label>
        <input v-model="category.cost" type="text" class="form-control" placeholder="Summa" id="inputCost">
      </div>
      <div class="col">
        <label for="inputStartDatetime">Starttid</label>
        <input v-model="category.startTime" type="text" class="form-control" placeholder="Starttid och datum" id="inputStartDatetime">
      </div>
    </div>
    <div class="row">
      <div class="col">
        <label for="inputDrawtype">Lottningstyp</label>
        <input v-model="category.drawType" type="text" class="form-control" placeholder="Pool med cupspel" id="inputDrawtype">
      </div>
      <div class="col">
        <label for="inputNumberOfPlayersPerPool">Antal spelare per pool</label>
        <input v-model="category.playersPerPool" type="text" class="form-control" placeholder="4" id="inputNumberOfPlayersPerPool">
      </div>
    </div>
    <div class="row">
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