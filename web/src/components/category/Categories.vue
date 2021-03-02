<template>
<div id="category-main">
  <div class="container">
    <div class="row">
      <div class="col-4">
        <category-list
          v-bind:categories="categories"
        />
      </div>
      <div class="col-8">
        <div class="row">
          <div class="col-4">
            <input type="radio" class="btn-check" name="classAndGameRulesToggle" id="btnClasses" autocomplete="off" checked>
            <label class="btn btn-outline-primary" for="btnClasses">Klasser</label>

            <input type="radio" class="btn-check" name="classAndGameRulesToggle" id="btnGameRules" autocomplete="off">
            <label class="btn btn-outline-primary" for="btnGameRules">Matchregler</label>
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
        <button v-on:click="doSomething" type="button" class="btn btn-warning">Ställ in klass</button>
        <button type="button" class="btn btn-danger">Ta bort klass</button>
      </div>
    </div>
  </div>

</div>
</template>

<script>
import CategoryList from "@/components/category/CategoryList"

export default {
  name: "Categories",
  components: {CategoryList},
  data: function(){
    return {
      categories: []
    }
  },
  created() {
    this.categories = [
      {
        id: 1,
        name: "Herrar-1"
      },
      {
        id: 2,
        name: "Damer-1"
      },
      {
        id: 3,
        name: "Flickor-14"
      },
      {
        id: 4,
        name: "Killar-14"
      }
    ]
  },
  methods: {
    doSomething : function() {
      console.log(this.name)
    }
  }
}
</script>

<style scoped>
input {
  padding: 10px;
}
label {
  float: left;
  margin-top: 30px;
  margin-bottom: 5px;
}
form {
  padding: 10px;
}

button {
  float: right;
  margin: 10px;
}
.btn-outline-primary {
  margin-right: 10px;
  border-color: white;
}
</style>