<template>
  <main class="container-fluid">
    <div class="row">
      <h1 class="display-1 text-start ps-3 ps-md-5 ">KLASSER</h1>
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
                      aria-selected="true">Översikt
              </button>
            </li>
            <li class="nav-item" role="presentation">
              <button class="nav-link" id="matchrules-tab" data-bs-toggle="tab"
                      data-bs-target="#matchrules" type="button" role="tab" aria-controls="matchrules"
                      aria-selected="false">Matchregler
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
          <h2 class="display-3">Inga klasser skapade</h2>
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
      categories: []
    }
  },
  created() {
    this.shownTab = "GENERAL_RULES"
    this.activeCategory = null
    this.competitionCategories = []
    CategoryService.getCategories().then(res => {
      this.categories = res.data.filter(category => category.name !== "BYE")
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
      console.log(this.competition.id)
      CategoryService.createCompetitionCategory(this.competition.id, category.id).then(res => {
        console.log(res.data)
      })
      this.activeCategory = this.defaultClass(category)
      this.competitionCategories.push(this.activeCategory)
    },
    defaultClass: function (category) {
      return {
        id: this.competitionCategories.length + 1,
        name: category.name,
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
  border-bottom: 0.5rem solid var(--clr-primary-500);
}

.display-1 {
  font-family: 'Bebas Neue', cursive;
  letter-spacing: 0.5rem;
  color: var(--clr-primary-500);
  background-color: #E0EAFF; /*var(--clr-primary-100);*/
}
</style>