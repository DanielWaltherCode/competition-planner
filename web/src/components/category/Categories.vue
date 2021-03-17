<template>
<div id="category-main">
  <div class="container">
    <div class="row">
      <div class="col-4">
        <category-list
          v-on:selectedClass="fillFormWithClass"
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
        <CategoryGeneralSettings v-bind:category="activeCategory"></CategoryGeneralSettings>
        <button type="button" class="btn btn-primary">Primary ghe asd</button>
        <button v-on:click="doSomething" type="button" class="btn btn-warning">Ställ in klass</button>
        <button type="button" class="btn btn-danger">Ta bort klass</button>
      </div>
    </div>
  </div>

</div>
</template>

<script>
import CategoryList from "@/components/category/CategoryList"
import CategoryGeneralSettings from "@/components/category/CategoryGeneralSettings";

export default {
  name: "Categories",
  components: {
    CategoryList,
    CategoryGeneralSettings
  },
  data: function(){
    return {
      activeCategory: Object,
      categories: []
    }
  },
  created() {
    this.categories = [
      {
        id: 1,
        name: "Herrar-1",
        cost: 110,
        startTime: "10AM 2022-10-18",
        drawType: "POOL",
        drawStrategy: "Snakelottning",
        playersPerPool: 4,
        playersThatAdvancePerGroup: 2,
      },
      {
        id: 2,
        name: "Damer-1",
        cost: 100,
        startTime: "11AM 2022-10-18",
        drawType: "POOL",
        drawStrategy: "Snakelottning",
        playersPerPool: 4,
        playersThatAdvancePerGroup: 2,
      },
      {
        id: 3,
        name: "Flickor-14",
        cost: 85,
        startTime: "12AM 2022-10-18",
        drawType: "CUP",
        drawStrategy: "Random",
        playersPerPool: 8,
        playersThatAdvancePerGroup: 1,
      },
      {
        id: 4,
        name: "Killar-14",
        cost: 75,
        startTime: "13AM 2022-10-18",
        drawType: "CUP",
        drawStrategy: "Random",
        playersPerPool: 8,
        playersThatAdvancePerGroup: 1,
      }
    ]
    this.activeCategory = this.categories[0]
  },
  methods: {
    doSomething : function() {
      console.log(this.name)
    },
    fillFormWithClass : function(classId){
      this.activeCategory = this.categories.find((item) => {
        return (item.id === classId)
      })
      console.log("Selected class " + classId + ". Updating form")
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