<template>
  <main class="container-fluid">
    <div class="row">
      <h1 class="p-4">
        <i @click="$router.push('/overview')" class="fas fa-arrow-left" style="float: left"></i>
        {{ $t("categories.title") }}
        <i @click="$router.push('/players')" class="fas fa-arrow-right" style="float: right"></i>
      </h1>

      <!-- Sidebar -->
      <div class="sidebar col-md-4">
        <div>
          <div>
            <h4> {{ $t("categories.createNew") }}</h4>
            <hr>
            <ul class="nav nav-tabs" id="addCategoryTab" role="tablist">
              <li class="nav-item" role="presentation">
                <button class="nav-link"
                        :class="customCategoryToggleSelected === false ? 'active' : ''"
                        @click="customCategoryToggleSelected = false">
                  {{ $t("categories.standardCategory.buttonText") }}
                </button>
              </li>
              <li class="nav-item" role="presentation">
                <button class="nav-link"
                        :class="customCategoryToggleSelected === true ? 'active' : ''"
                        @click="customCategoryToggleSelected = true">
                  {{ $t("categories.customCategory.buttonText") }}
                </button>
              </li>
            </ul>
          </div>
          <div v-if="!customCategoryToggleSelected" class="text-start py-3">
            <select class="form-select my-3 mx-auto" v-model="newCategory">
              <option :value="null" disabled hidden>{{ $t("categories.chooseClass") }}</option>
              <option v-for="category in possibleCategories" :value="category" :key="category.id">
                {{ tryTranslateCategoryName(category.name) }}
              </option>
            </select>
            <div class="d-flex justify-content-end">
              <button class="btn btn-primary" type="button" :disabled="newCategory === null" @click="addCategory">{{
                  $t("categories.addClass")
                }}
              </button>
            </div>
          </div>

          <!-- Create custom class -->
          <div v-if="customCategoryToggleSelected" class="text-start py-3">
            <div class="p-3 my-2">
              <p>
                {{ $t("categories.customCategory.helper") }}
              </p>
              <input v-model="customCategory.name"
                     type="text"
                     class="form-control"
                     :placeholder="$t('categories.customCategory.name')">
              <label class="text-start pt-2" for="category-type-select">{{
                  $t('categories.customCategory.type')
                }}</label>
              <select id="category-type-select" v-model="customCategory.type" class="form-select my-2">
                <option value="SINGLES"> {{ $t("categories.SINGLES") }}</option>
                <option value="DOUBLES"> {{ $t("categories.DOUBLES") }}</option>
              </select>
              <div class="d-flex justify-content-end">
                <button class="btn btn-primary" type="button" :disabled="!customCategoryFilledOut()" @click="addCustomCategory">
                  {{ $t("categories.addClass") }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Added categories list-->
        <div class="py-5">
          <h5 class="text-start"> {{ $t("categories.alreadyAddedCategories") }}</h5>
          <hr>
          <ul class="list-group list-group-flush">
            <li v-for="category in competitionCategories" :key="category.id"
                class="list-group-item text-start"
                :class="activeCategory.id === category.id ? 'active' : 'none'"
                @click="chooseCategory(category)">
              {{ tryTranslateCategoryName(category.category.name) }}
            </li>
          </ul>
        </div>
      </div>

      <!-- Main -->
      <div v-if="activeCategory !== null" class="col-md-8 pt-5 px-md-4">
        <ul class="nav nav-tabs mb-4" id="myTab" role="tablist">
          <li class="nav-item text-black" role="presentation">
            <button class="nav-link text-black" :class="displayChoice === 'SETTINGS' ? 'active' : ''"
                    @click="displayChoice = 'SETTINGS'">{{ $t("categories.overview") }}
            </button>
          </li>
          <li class="nav-item text-black" role="presentation">
            <button class="nav-link text-black" :class="displayChoice === 'GAME_RULES' ? 'active' : ''"
                    @click="displayChoice = 'GAME_RULES'">{{ $t("categories.gameRules") }}
            </button>
          </li>
          <li class="nav-item" role="presentation">
            <button class="nav-link text-black" :class="displayChoice === 'REGISTER' ? 'active' : ''"
                    @click="displayChoice = 'REGISTER'">{{ $t("categories.register") }}
            </button>
          </li>
        </ul>
        <div class="text-start row">
          <div class="tab-content custom-card" id="myTabContent">

            <h2 class="p-3">{{ tryTranslateCategoryName(activeCategory.category.name) }}</h2>
            <div class="d-flex col-12 p-2 justify-content-end">
              <div class="p-2 custom-card">
                <button class="btn btn-primary me-3" type="button" @click="save"
                        :disabled="isDrawMade && displayChoice==='SETTINGS'">{{ $t("general.saveChanges") }}
                </button>
                <button class="btn btn-danger" type="button" @click="deleteCategory"
                        :disabled="isDrawMade && displayChoice==='SETTINGS'">{{ $t("categories.delete") }}
                </button>
              </div>
            </div>
            <CategoryGeneralSettings v-if="displayChoice === 'SETTINGS'" :is-draw-made="isDrawMade"
                                     :category="activeCategory"></CategoryGeneralSettings>
            <CategoryGameSettings v-if="displayChoice === 'GAME_RULES'"
                                  :category="activeCategory"></CategoryGameSettings>
            <AddPlayerToCategory v-if="displayChoice === 'REGISTER'" :category="activeCategory"></AddPlayerToCategory>
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
import DrawService from "@/common/api-services/draw.service";
import AddPlayerToCategory from "@/components/category/AddPlayerToCategory";
import {generalErrorHandler, tryTranslateCategoryName} from "@/common/util"

export default {
  /* eslint-disable */
  name: "CompetitionCategories",
  components: {
    AddPlayerToCategory,
    CategoryGameSettings,
    CategoryGeneralSettings
  },
  data() {
    return {
      displayChoice: 'SETTINGS',
      activeCategory: null,
      competitionCategories: [],
      possibleMetaDataValues: Object,
      allCompetitionCategories: [],
      newCategory: null,
      isDrawMade: false,
      customCategoryToggleSelected: false,
      customCategory: {}
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    },
    possibleCategories() {
      const alreadyAddedCategoryIds = this.competitionCategories.map(val => val.category.id)
      return this.allCompetitionCategories.filter(category => !alreadyAddedCategoryIds.includes(category.id))
    },
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
    CategoryService.getCategories(this.competition.id).then(res => {
      this.allCompetitionCategories = res.data.filter(category => category.name !== "BYE")
    })
    CategoryService.getPossibleMetaDataValues(this.competition.id).then(res => {
      this.possibleMetaDataValues = res.data
    })
  },
  methods: {
    chooseCategory(category) {
      this.activeCategory = category
      DrawService.isDrawMade(this.competition.id, category.id).then(res => {
        this.isDrawMade = res.data
      })
          .catch(() => {
            console.log("Couldn't determine if draw was already made")
          })

    },
    addCategory() {
      CategoryService.addCompetitionCategory(this.competition.id, this.newCategory).then(res => {
        this.$toasted.success(this.$tc("toasts.categoryAdded")).goAway(3000)
        const addedCategory = res.data
        this.competitionCategories.push(addedCategory)
        this.activeCategory = addedCategory
        this.isDrawMade = false
        this.newCategory = null
      }).catch(() => {
        this.$toasted.error(this.$tc("toasts.categoryNotAdded")).goAway(7000)
      })
    },
    addCustomCategory() {
      if (!this.customCategoryFilledOut()) {
        this.$toasted.error(this.$tc("toasts.customCategoryIncomplete")).goAway(3000)
        return
      }
      CategoryService.addCustomCategory(this.competition.id, this.customCategory).then(res => {
        this.$toasted.success(this.$tc("toasts.categoryAdded")).goAway(3000)
        const addedCategory = res.data
        this.competitionCategories.push(addedCategory)
        this.activeCategory = addedCategory
        this.isDrawMade = false
        this.customCategory = {}
        this.customCategoryToggleSelected = false
      }).catch(() => {
        this.$toasted.error(this.$tc("toasts.categoryNotAdded")).goAway(7000)
      })
    },
    customCategoryFilledOut() {
     return (this.customCategory.name !== null && this.customCategory.name !== undefined
         && this.customCategory.type !== null && this.customCategory.type !== undefined)
    },
    tryTranslateCategoryName: tryTranslateCategoryName,
    save() {
      this.activeCategory.gameSettings.winScoreFinal = this.activeCategory.gameSettings.winScore // Use same win score setting in endgame matches.
      CategoryService.updateCompetitionCategory(this.competition.id, this.activeCategory.id, this.activeCategory).then(() => {
        this.$toasted.success(this.$tc("toasts.categoryUpdated")).goAway(3000)
      }).catch(err => {
        this.errorHandler(err.data)
      })
    },
    deleteCategory() {
      if (confirm(this.$tc("confirm.deleteCategory"))) {
        CategoryService.deleteCompetitionCategory(this.competition.id, this.activeCategory.id).then(() => {
          this.$toasted.success(this.$tc("toasts.categoryDeleted")).goAway(3000)
          CategoryService.getCompetitionCategories(this.competition.id).then(res => {
            this.competitionCategories = res.data
            if (this.competitionCategories.length > 0) {
              this.activeCategory = this.competitionCategories[0]
            }
          }).catch(err => {
            this.errorHandler(err.data)
          })
        })
      }
    },
    errorHandler: generalErrorHandler
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