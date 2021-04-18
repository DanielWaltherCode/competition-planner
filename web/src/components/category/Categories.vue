<template>
<div id="category-main">
  <div class="container">
    <div class="row justify-content-start">
      <div class="col-4">
        <category-list
          v-on:selectedClass="fillFormWithClass"
          v-bind:categories="categories"
        />
      </div>
      <div class="col">
        <ul class="nav nav-tabs mb-4" id="myTab" role="tablist">
          <li class="nav-item" role="presentation">
            <button class="nav-link active" id="overview-tab" data-bs-toggle="tab"
                    data-bs-target="#overview" type="button" role="tab" aria-controls="overview"
                    aria-selected="true">Översikt</button>
          </li>
          <li class="nav-item" role="presentation">
            <button class="nav-link" id="matchrules-tab" data-bs-toggle="tab"
                    data-bs-target="#matchrules" type="button" role="tab" aria-controls="matchrules"
                    aria-selected="false">Matchregler</button>
          </li>
        </ul>
        <form class="text-start row">
          <div class="tab-content" id="myTabContent">
            <CategoryGeneralSettings class="tab-pane show active" id="overview" role="tabpanel" aria-labelledby="overview-tab"
                v-bind:category="activeCategory"></CategoryGeneralSettings>
            <CategoryGameSettings class="tab-pane" id="matchrules" role="tabpanel" aria-labelledby="matchrules-tab"
                v-bind:category="activeCategory"></CategoryGameSettings>
          </div>
        </form>
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
</style>