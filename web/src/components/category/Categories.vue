<template>
  <main class="container-fluid">
    <div class="row">
      <h1 class="p-4">
        <i @click="$router.push('/overview')" class="fas fa-arrow-left" style="float: left"></i>
        {{ $t("categories.title") }}
        <i @click="$router.push('/players')" class="fas fa-arrow-right" style="float: right"></i>
      </h1>

      <!-- Sidebar -->
      <div class="sidebar col-md-3">
        <div>
          <h5> {{ $t("categories.createNew") }}</h5>
          <select class="form-select w-75 my-3 mx-auto" v-model="newCategory">
            <option :value="null" disabled hidden>{{ $t("categories.chooseClass")}}</option>
            <option v-for="category in possibleCategories" :value="category" :key="category.id">
              {{ category.name }}
            </option>
          </select>
          <button class="btn btn-primary" @click="addCategory">{{ $t("categories.addClass") }}</button>

        </div>
        <div class="py-5">
          <h5> {{ $t("categories.alreadyAddedCategories") }}</h5>
          <ul class="list-group list-group-flush">
            <li v-for="category in competitionCategories" :key="category.id" class="list-group-item"
                @click="chooseCategory(category)">
              {{ category.category.name }}
            </li>
          </ul>
        </div>
      </div>

      <!-- Main -->
        <div v-if="activeCategory !== null" class="col-md-9 pt-5 ps-md-5">
          <h2> {{activeCategory.name}}</h2>
          <ul class="nav nav-tabs mb-4" id="myTab" role="tablist">
            <li class="nav-item" role="presentation">
              <button class="nav-link" :class="displayChoice === 'SETTINGS' ? 'active' : ''"
                      @click="displayChoice = 'SETTINGS'">{{ $t("categories.overview") }}
              </button>
            </li>
            <li class="nav-item" role="presentation">
              <button class="nav-link" :class="displayChoice === 'GAME_RULES' ? 'active' : ''"
                      @click="displayChoice = 'GAME_RULES'">{{ $t("categories.gameRules") }}
              </button>
            </li>
          </ul>
          <div class="text-start row">
            <div class="tab-content" id="myTabContent">

              <h2 class="p-3">{{activeCategory.category.name}}</h2>
              <div class="d-flex col-12 p-2 justify-content-end">
                <div class="p-2 border border-1 rounded">
                  <button class="btn btn-primary m-1" type="button" @click="save">{{ $t("general.saveChanges") }}</button>
                  <button class="btn btn-danger" type="button" @click="deleteCategory">{{ $t("categories.delete") }}</button>
                </div>
              </div>
              <CategoryGeneralSettings v-if="displayChoice === 'SETTINGS'"
                                       :category="activeCategory"></CategoryGeneralSettings>
              <CategoryGameSettings v-if="displayChoice === 'GAME_RULES'"
                                    :category="activeCategory"></CategoryGameSettings>
            </div>
          </div>
        </div>
        <div v-else class="col justify-content-center">
          <h3 class="text-dark p-4">{{ $t("categories.noCategories") }}</h3>
        </div>
      </div>
  </main>
</template>

<script>
import CategoryGeneralSettings from "@/components/category/CategoryGeneralSettings"
import CategoryGameSettings from "@/components/category/CategoryGameRules";
import CategoryService from "@/common/api-services/category.service";

export default {
  name: "Categories",
  components: {
    CategoryGameSettings,
    CategoryGeneralSettings
  },
  data() {
    return {
      displayChoice: 'SETTINGS',
      activeCategory: null,
      competitionCategories: [],
      possibleCategories: [],
      possibleMetaDataValues: Object,
      newCategory: null
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  mounted() {
    // Fetch categories already set up in the competition
    CategoryService.getCompetitionCategories(this.competition.id).then(res => {
      this.competitionCategories = res.data
      if (this.competitionCategories.length > 0) {
        this.activeCategory = this.competitionCategories[0]
      }
    })

    // Fetch possible categories
    CategoryService.getCategories().then(res => {
      this.possibleCategories = res.data.filter(category => category.name !== "BYE")
    })
    CategoryService.getPossibleMetaDataValues(this.competition.id).then(res => {
      this.possibleMetaDataValues = res.data
    })
  },
  methods: {
    chooseCategory(category) {
      this.activeCategory = category
    },
    addCategory() {
      CategoryService.addCompetitionCategory(this.competition.id, this.newCategory).then(res => {
        const addedCategory = res.data
        this.competitionCategories.push(addedCategory)
        this.activeCategory = addedCategory
        this.newCategory = null
      })
    },
    save() {
      CategoryService.updateCompetitionCategory(this.competition.id, this.activeCategory.id, this.activeCategory).then(() => {
        this.$toasted.show(this.$tc("toasts.categoryUpdated")).goAway(3000)
      })
    },
    deleteCategory() {
      CategoryService.deleteCompetitionCategory(this.competition.id, this.activeCategory.id).then(() => {
        this.$toasted.show(this.$tc("toasts.categoryDeleted")).goAway(3000)
        CategoryService.getCompetitionCategories(this.competition.id).then(res => {
          this.competitionCategories = res.data
          if (this.competitionCategories.length > 0) {
            this.activeCategory = this.competitionCategories[0]
          }
        })
      })
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