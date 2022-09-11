<template>
  <div>
    <!-- Page heading -->
    <div class="row p-4 blue-section">
      <h4 class="text-start">{{ $t("player.add.heading") }}</h4>
      <p class="text-start col-sm-8">{{ $t("player.add.helperText") }}
        <span class="clickable text-decoration-underline"
              @click="$router.push('/players/create')">{{ $t("player.sidebar.create") }}
        </span>
      </p>
    </div>
    <!-- Add player to singles categories -->
    <div class="row custom-card pb-4">
      <h2 class="p-3">{{ $t("player.addPlayerSingles") }}</h2>
      <search-player-component ref="singles-search" class="justify-content-center" v-on:clear-player="singlesPlayer = null"
                               v-on:player-found="singlesPlayer = $event"></search-player-component>
      <div v-if="singlesPlayer !== null" class="row px-4">
        <div class="col-sm-4"></div>
        <div class="form-check col-sm-6 mb-3">
          <h5 class="text-start">{{$t("player.categories")}}</h5>
          <div v-for="competitionCategory in singlesCategories" :key="competitionCategory.category.id">
            <input class="form-check-input ms-1" type="checkbox" :value="competitionCategory.category.name"
                   :id="competitionCategory.category.id" @change="noCategories = false" v-model="selectedSinglesCategories">
            <label class="form-check-label d-flex ps-2" :for="competitionCategory.category.id">
              {{ tryTranslateCategoryName(competitionCategory.category.name) }}
            </label>
          </div>
          <p class="fs-6 text-danger text-start" v-if="noCategories">{{ $t("validations.noCategories") }}</p>
        </div>
        <div class="d-flex justify-content-end">
          <button type="button" class="btn btn-primary" @click="addSinglesPlayerToCompetition">
            {{ $t("player.add.buttonText") }}
          </button>
        </div>
      </div>
    </div>
    <!-- Add players to doubles categories -->
    <div class="row custom-card mt-5">
      <h2 class="p-3">{{ $t("player.addPlayerDoubles") }}</h2>
      <div class="d-flex justify-content-start px-4">
        <p class="mb-0">{{ $t("player.doublesPlayer1") }}</p>
      </div>
      <search-player-component class="justify-content-center" ref="double1" v-on:clear-player="doublesPlayer1 = null"
                               v-on:player-found="doublesPlayer1 = $event"></search-player-component>
      <div class="d-flex justify-content-start px-4">
        <p class="mb-0">{{ $t("player.doublesPlayer2") }}</p>
      </div>
      <search-player-component class="justify-content-center" ref="double2" v-on:clear-player="doublesPlayer2 = null"
                               v-on:player-found="doublesPlayer2 = $event"></search-player-component>
      <div v-if="doublesPlayer1 !== null && doublesPlayer2 !== null" class="row px-4 pb-4">
        <div class="col-sm-4"></div>
        <div class="form-check col-sm-6 mb-3">
          <h5 class="text-start">{{$t("player.categories")}}</h5>
          <div v-for="competitionCategory in doublesCategories" :key="competitionCategory.category.id">
            <input class="form-check-input ms-1" type="checkbox" :value="competitionCategory.category.name"
                   :id="competitionCategory.category.id" @change="noCategories = false" v-model="selectedDoublesCategories">
            <label class="form-check-label d-flex ps-2" :for="competitionCategory.category.id">
              {{ tryTranslateCategoryName(competitionCategory.category.name) }}
            </label>
          </div>
          <p class="fs-6 text-danger text-start" v-if="noCategories">{{ $t("validations.noCategories") }}</p>
        </div>
        <div class="d-flex justify-content-end">
          <button type="button" class="btn btn-primary" @click="registerDoublesPlayers">
            {{ $t("player.add.buttonText") }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CategoryService from "@/common/api-services/category.service";

import RegistrationService from "@/common/api-services/registration.service";
import SearchPlayerComponent from "@/components/player/SearchPlayerComponent";
import {generalErrorHandler, tryTranslateCategoryName} from "@/common/util"

export default {
  name: "AddPlayerToCompetition",
  components: {SearchPlayerComponent},
  data() {
    return {
      noCategories: false,
      selectedSinglesCategories: [],
      selectedDoublesCategories: [],
      competitionCategories: [],
      singlesPlayer: null,
      doublesPlayer1: null,
      doublesPlayer2: null
    }
  },
  computed: {
    competition() {
      return this.$store.getters.competition
    },
    singlesCategories() {
      return this.competitionCategories.filter(competitionCategory => competitionCategory.category.type === "SINGLES")
    },
    doublesCategories() {
      return this.competitionCategories.filter(competitionCategory => competitionCategory.category.type === "DOUBLES")
    }
  },
  mounted() {
    CategoryService.getCompetitionCategories(this.competition.id).then(res => {
      this.competitionCategories = res.data
    })

  },
  methods: {
    addSinglesPlayerToCompetition() {
      if (this.singlesPlayer === null) {
        this.$toasted.error(this.$tc("toasts.player.couldNotBeAddedToCompetition")).goAway(5000)
        return;
      }
      this.registerSinglesPlayer()
    },
    registerSinglesPlayer() {
      const categoriesToRegisterIn = this.competitionCategories.filter(val => this.selectedSinglesCategories.includes(val.category.name))

      if (categoriesToRegisterIn.length === 0) {
        this.noCategories = true;
        return;
      }

      categoriesToRegisterIn.forEach(category => {
        const registrationSpec = {
          playerId: this.singlesPlayer.id,
          competitionCategoryId: category.id
        }
        RegistrationService.registerPlayerSingles(this.competition.id, registrationSpec).then(() => {
          this.$toasted.success(this.$tc("toasts.player.added")).goAway(3000)
        }).catch(err => {
          this.errorHandler(err.data)
        })
      })
      this.singlesPlayer = null
      this.$refs["singles-search"].clearPlayer()
      this.selectedSinglesCategories = []
    },
    registerDoublesPlayers() {
      if (this.doublesPlayer1 === null || this.doublesPlayer2 === null) {
        this.$toasted.error(this.$tc("toasts.player.doubleRegistrationError")).goAway(5000)
        return;
      }
      const categoriesToRegisterIn = this.competitionCategories.filter(val => this.selectedDoublesCategories.includes(val.category.name))

      if (categoriesToRegisterIn.length === 0) {
        this.noCategories = true;
        return;
      }

      categoriesToRegisterIn.forEach(category => {
          const registrationSpec = {
            playerOneId: this.doublesPlayer1.id,
            playerTwoId: this.doublesPlayer2.id,
            competitionCategoryId: category.id
        }
        RegistrationService.registerPlayerDoubles(this.competition.id, registrationSpec).then(() => {
          this.$toasted.success(this.$tc("toasts.player.added")).goAway(3000)
          this.$refs.double1.clearPlayer()
          this.$refs.double2.clearPlayer()
          this.selectedSinglesCategories = []
        }).catch(err => {
          this.errorHandler(err.data)
        })
      })
    },
    tryTranslateCategoryName: tryTranslateCategoryName,
    errorHandler: generalErrorHandler
  }
}
</script>

<style scoped>

</style>