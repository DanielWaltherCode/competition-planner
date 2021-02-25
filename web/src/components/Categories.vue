<template>
<div id="category-main">
  <div class="container">
    <div class="row">
      <div class="col-4">
        <ul class="list-group">
          <li class="list-group-item">Herrar 1</li>
          <li class="list-group-item">Damer 1</li>
          <li class="list-group-item">Flickor 12 GP</li>
          <li class="list-group-item">Flickor 11</li>
          <li class="list-group-item">Pojkar 13</li>
        </ul>
      </div>
      <div class="col-8">
        <div class="row">
          <div class="col-6">
            <button type="button" class="btn btn-primary">Klassöversikt</button>
          </div>
          <div class="col-6">
            <button type="button" class="btn btn-primary">Matchregler</button>
          </div>
        </div>
        <form>
          <div class="row">
            <div class="col">
              <label for="inputCost">Kostnad</label>
              <input type="text" class="form-control" placeholder="Summa" id="inputCost">
            </div>
            <div class="col">
              <label for="inputStartDatetime">Starttid</label>
              <input type="text" class="form-control" placeholder="Starttid och datum" id="inputStartDatetime">
            </div>
          </div>
          <div class="row">
            <div class="col">
              <label for="inputDrawtype">Lottningstyp</label>
              <input type="text" class="form-control" placeholder="Pool med cupspel" id="inputDrawtype">
            </div>
            <div class="col">
              <label for="inputNumberOfPlayersPerPool">Antal spelare per pool</label>
              <input type="text" class="form-control" placeholder="4" id="inputNumberOfPlayersPerPool">
            </div>
          </div>
          <div class="row">
            <div class="col">
              <label for="inputPlayersThatAdvance">Lottningstyp</label>
              <input type="text" class="form-control" placeholder="2" id="inputPlayersThatAdvance">
            </div>
            <div class="col">
              <label for="inputDrawStrategy">Lottningstrategi</label>
              <input type="text" class="form-control" placeholder="Snakelottning" id="inputDrawStrategy">
            </div>
          </div>
        </form>
        <div class="row">
          <div class="col-4">
            <button type="button" class="btn btn-primary">Ta bort klass</button>
          </div>
          <div class="col-4">
            <button type="button" class="btn btn-primary">Ställ in klass</button>
          </div>
        </div>
      </div>
    </div>
  </div>

</div>
</template>

<script>
export default {
  name: "Categories"
}
</script>

<style scoped>

</style>