<template>
  <main class="container-fluid">
    <div class="row">
      <h1 class="p-4">
        <i @click="$router.push('/overview')" class="fas fa-arrow-left" style="float: left"></i>
        {{ $t("categories.title") }}
        <i @click="$router.push('/players')" class="fas fa-arrow-right" style="float: right"></i>
      </h1>
      <div class="row justify-content-start flex-grow-1">
        <div class="col-3">
          <category-list
              v-on:selectedClass="fillFormWithClass"
              v-on:createdClass="createClass"
              v-bind:competitionCategories="competitionCategories"
              v-bind:activeCategory="activeCategory"
              v-bind:categories="categories"
          />
        </div>
        <div v-if="activeCategory !== null" class="col-6 pt-5 ps-md-5">
          <ul class="nav nav-tabs mb-4" id="myTab" role="tablist">
            <li class="nav-item" role="presentation">
              <button class="nav-link active" id="overview-tab" data-bs-toggle="tab"
                      data-bs-target="#overview" type="button" role="tab" aria-controls="overview"
                      aria-selected="true">{{$t("categories.overview")}}
              </button>
            </li>
            <li class="nav-item" role="presentation">
              <button class="nav-link" id="matchrules-tab" data-bs-toggle="tab"
                      data-bs-target="#matchrules" type="button" role="tab" aria-controls="matchrules"
                      aria-selected="false">{{$t("categories.gameRules")}}
              </button>
            </li>
          </ul>
          <form class="text-start row">
            <div class="tab-content" id="myTabContent">
              <CategoryGeneralSettings class="tab-pane show active" id="overview" role="tabpanel"
                                       aria-labelledby="overview-tab"
                                       v-bind:category="activeCategory"></CategoryGeneralSettings>
              <CategoryGameSettings class="tab-pane" id="matchrules" role="tabpanel" aria-labelledby="matchrules-tab"
                                    v-bind:category="activeCategory"></CategoryGameSettings>
            </div>
          </form>
        </div>
        <div v-else class="col justify-content-center">
          <h3 class="text-dark p-4">{{$t("categories.noCategories")}}</h3>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import CategoryList from "@/components/category/CategoryList"
import CategoryGeneralSettings from "@/components/category/CategoryGeneralSettings"
import CategoryGameSettings from "@/components/category/CategoryGameSettings";
import CategoryService from "@/common/api-services/categoryservice";

export default {
  name: "Categories",
  components: {
    CategoryGameSettings,
    CategoryList,
    CategoryGeneralSettings
  },
  data: function () {
    return {
      shownTab: "",
      activeCategory: Object,
      competitionCategories: [],
      categories: [],
      possibleMetaDataValues: Object
    }
  },
  created() {
    this.shownTab = "GENERAL_RULES"
    this.activeCategory = null
    this.competitionCategories = []
    CategoryService.getCategories().then(res => {
      this.categories = res.data.filter(category => category.name !== "BYE")
    })
    CategoryService.getPossibleMetaDataValues(this.competition.id).then(res => {
      this.possibleMetaDataValues = res.data
    })
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  methods: {
    fillFormWithClass: function (classId) {
      this.activeCategory = this.competitionCategories.find((item) => {
        return (item.id === classId)
      })
    },
    createClass: function (category) {
      CategoryService.createCompetitionCategory(this.competition.id, category.id).then(res => {
        let newlyCreatedCompetitionCategory = res.data.categories.find(competitionCategory => competitionCategory.categoryName === category.name)
        this.activeCategory = this.addDefaultValuesTo(newlyCreatedCompetitionCategory)
        this.competitionCategories.push(this.activeCategory)
        this.saveCompetitionCategorySettings(this.activeCategory)
      })
    },
    saveCompetitionCategorySettings: function(competitionCategory) {
      CategoryService.addMetaData(this.competition.id, competitionCategory.id, this.asMetaSpec(competitionCategory))
      CategoryService.addGameRules(competitionCategory.id, this.asGameSpec(competitionCategory))
    },
    asMetaSpec: function(competitionCategory) {
      return {
        cost: competitionCategory.cost,
        drawTypeId: this.possibleMetaDataValues.drawTypes.find(drawType => drawType.name === "POOL_ONLY").id,
        nrPlayersPerGroup: competitionCategory.playersPerPool,
        nrPlayersToPlayoff: competitionCategory.playersThatAdvancePerGroup,
        poolDrawStrategyId: this.possibleMetaDataValues.drawStrategies.find(strategy => strategy.name === "normal").id,
      }
    },
    asGameSpec: function(competitionCategory) {
      return {
        nrSets: competitionCategory.defaultGameSettings.numberOfSets,
        winScore: competitionCategory.defaultGameSettings.playingUntil,
        winMargin: competitionCategory.defaultGameSettings.winMargin,
        nrSetsFinal: competitionCategory.endGameSettings.numberOfSets,
        winScoreFinal: competitionCategory.endGameSettings.playingUntil,
        winMarginFinal: competitionCategory.endGameSettings.winMargin,
        winScoreTiebreak: competitionCategory.tiebreakSettings.playingUntil,
        winMarginTieBreak: competitionCategory.tiebreakSettings.winMargin
      }
    },
    addDefaultValuesTo: function (competitionCategory) {
      return {
        id: competitionCategory.competitionCategoryId,
        name: competitionCategory.categoryName,
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
      }
    }
  }
}
</script>

<style scoped>
main {
  height: 100vh;
}

h1 {
  background-color: var(--clr-primary-100);
}

</style>