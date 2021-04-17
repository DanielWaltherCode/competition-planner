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
            <input v-model="shownTab" value="GENERAL_RULES" type="radio" class="btn-check" name="classAndGameRulesToggle" id="btnClasses" autocomplete="off" checked>
            <label class="btn btn-outline-primary" for="btnClasses">Översikt</label>

            <input v-model="shownTab" value="GAME_RULES" type="radio" class="btn-check" name="classAndGameRulesToggle" id="btnGameRules" autocomplete="off">
            <label class="btn btn-outline-primary" for="btnGameRules">Matchregler</label>
          </div>
        </div>
        <CategoryGeneralSettings v-if="shownTab === 'GENERAL_RULES'" v-bind:category="activeCategory"></CategoryGeneralSettings>
        <CategoryGameSettings v-if="shownTab === 'GAME_RULES'" v-bind:category="activeCategory"></CategoryGameSettings>
      </div>
    </div>
  </div>

</div>
</template>

<script>
import CategoryList from "@/components/category/CategoryList"
import CategoryGeneralSettings from "@/components/category/CategoryGeneralSettings"
import CategoryGameSettings from "@/components/category/CategoryGameSettings";

export default {
  name: "Categories",
  components: {
    CategoryGameSettings,
    CategoryList,
    CategoryGeneralSettings
  },
  data: function(){
    return {
      shownTab: "",
      activeCategory: Object,
      categories: []
    }
  },
  created() {
    this.shownTab = "GENERAL_RULES"
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
        defaultGameSettings: {
          numberOfSets: 5,
          playingUntil: 7,
          winMargin: 1,
        },
        endGameSettings: {
          enabled: false,
          numberOfSets: 3,
          playingUntil: 11,
          winMargin: 1
        },
        tiebreakSettings: {
          enabled: false,
          playingUntil: 3,
          winMargin: 2
        }
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
        defaultGameSettings: {
          numberOfSets: 5,
          playingUntil: 7,
          winMargin: 1,
        },
        endGameSettings: {
          enabled: true,
          numberOfSets: 3,
          playingUntil: 11,
          winMargin: 1
        },
        tiebreakSettings: {
          enabled: false,
          playingUntil: 3,
          winMargin: 2
        }
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
        defaultGameSettings: {
          numberOfSets: 5,
          playingUntil: 7,
          winMargin: 1,
        },
        endGameSettings: {
          enabled: true,
          numberOfSets: 3,
          playingUntil: 11,
          winMargin: 1
        },
        tiebreakSettings: {
          enabled: false,
          playingUntil: 3,
          winMargin: 2
        }
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
        defaultGameSettings: {
          numberOfSets: 5,
          playingUntil: 7,
          winMargin: 1,
        },
        endGameSettings: {
          enabled: true,
          numberOfSets: 3,
          playingUntil: 11,
          winMargin: 1
        },
        tiebreakSettings: {
          enabled: false,
          playingUntil: 3,
          winMargin: 2
        }
      }
    ]
    this.activeCategory = this.categories[0]
  },
  methods: {
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