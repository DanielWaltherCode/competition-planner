<template>
  <div>
    <div class="row p-4 blue-section">
      <h4 class="text-start">{{ $t("player.add.heading") }}</h4>
      <p class="text-start">{{ $t("player.add.helperText") }}</p>
    </div>
    <div class="row">
      <div class="d-flex p-5">
        <autocomplete class="col-sm-8 me-2" id="autocomplete-field"
                      ref="autocomplete"
                      :search="searchPlayers"
                      auto-select
                      :get-result-value="getSearchResult"
                      :placeholder="$t('player.add.search')"
                      @submit="handleSubmit">
        </autocomplete>
        <button class="btn btn-primary" type="button" @click="clearPlayer"> {{
            $t("player.add.clear")
          }}
        </button>
      </div>
      <form id="input-form" class="rounded row p-3">
        <div class="col-md-11">
          <div class="row mx-auto">
            <div class="col-sm-6 mb-3">
              <label for="firstName" class="text-start form-label"> {{ $t("player.add.firstName") }}</label>
                <input type="text" :disabled="player !== null" @keyup="noFirstName = false" class="form-control" v-model="firstName"
                       id="firstName">
              <p class="fs-6 text-danger" v-if="noFirstName">{{ $t("validations.required") }}</p>
            </div>

            <div class="col-sm-6 mb-3">
              <label for="lastName" class="text-start form-label"> {{ $t("player.add.lastName") }}</label>
                <input type="text" :disabled="player !== null" @keyup="noLastName = false" class="form-control" v-model="lastName"
                       id="lastName">
              <p class="fs-6 text-danger" v-if="noLastName">{{ $t("validations.required") }}</p>
            </div>

            <div class="col-sm-6 mb-3">
              <label for="club-select" class="form-label"> {{ $t("player.add.club") }}</label>
              <!-- Standard validation does not work on select menues -->
              <select :disabled="player !== null" class="form-select" name="club-select" id="club-select"
                      @change="noClub = false" v-model="club">
                <option v-for="club in clubs" :key="club.id" :value="club">
                  {{ club.name }}
                </option>
              </select>
              <p class="fs-6 text-danger" v-if="noClub">{{ $t("validations.required") }}</p>
            </div>

            <div class="col-sm-6 mb-3">
              <label for="dateOfBirth" class="text-start form-label"> {{ $t("player.add.dateOfBirth") }}</label>
                <input type="date" :disabled="player !== null" @change="noDateOfBirth = false" class="form-control" v-model="dateOfBirth"
                       id="dateOfBirth">
              <p class="fs-6 text-danger" v-if="noDateOfBirth">{{ $t("validations.required") }}</p>
            </div>

            <div class="form-check col-sm-6 mb-3">
              <div v-for="competitionCategory in competitionCategories" :key="competitionCategory.category.id">
                <input class="form-check-input ms-1" type="checkbox" :value="competitionCategory.category.name"
                       :id="competitionCategory.category.id" @change="noCategories = false" v-model="selectedCategories">
                <label class="form-check-label d-flex ps-2" :for="competitionCategory.category.id">
                  {{ competitionCategory.category.name }}
                </label>
              </div>
              <p class="fs-6 text-danger" v-if="noCategories">{{ $t("validations.required") }}</p>
            </div>
            <div class="d-flex justify-content-end">
              <button type="button" class="btn btn-primary" @click="addPlayerToCompetition">
                {{ $t("player.add.buttonText") }}
              </button>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import CategoryService from "@/common/api-services/category.service";
import ClubService from "@/common/api-services/club.service";
import Autocomplete from '@trevoreyre/autocomplete-vue'
import '@trevoreyre/autocomplete-vue/dist/style.css';
import PlayerService from "@/common/api-services/player.service";
import RegistrationService from "@/common/api-services/registration.service";

export default {
  name: "AddPlayerToCompetition",
  data() {
    return {
      noClub: false,
      noCategories: false,
      noFirstName: false,
      noLastName: false,
      noDateOfBirth: false,
      failedToAddPlayer: false,
      playerNotFound: false,
      player: null,
      firstName: null,
      lastName: null,
      club: null,
      clubs: [],
      dateOfBirth: null,
      selectedCategories: [],
      competitionCategories: []
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    },
  },
  components: {Autocomplete},
  mounted() {
    CategoryService.getCompetitionCategories(this.competition.id).then(res => {
      this.competitionCategories = res.data
    })
    ClubService.getClubs().then(res => {
      this.clubs = res.data
    })
  },
  methods: {
    searchPlayers(input) {
      if (input.length < 1) {
        return [];
      }
      return new Promise(resolve => {
        PlayerService.searchAllPlayers(input).then(res => {
          this.playerNotFound = false
          resolve(res.data)
        }).catch(() => {
          this.playerNotFound = true;
        })
      })
    },
    getSearchResult(searchResult) {
      return searchResult.firstName + " " + searchResult.lastName
    },
    clearPlayer() {
      this.player = null
      this.firstName = null
      this.lastName = null
      this.club = null
      this.dateOfBirth = null
      this.selectedCategories = []
      this.failedToAddPlayer = false
      this.$refs.autocomplete.value = ""
    },
    handleSubmit(result) {
      console.log(result)
      if (result === undefined || result === "") {
        return;
      }
      this.player = result
      this.firstName = result.firstName
      this.lastName = result.lastName
      this.club = result.club
      this.dateOfBirth = result.dateOfBirth
    },
    validateSubmission() {
      if (!this.firstName) {
        this.noFirstName = true
        return false
      }
      if (!this.lastName) {
        this.noLastName = true
        return false
      }
      if (!this.club) {
        this.noClub = true
        return false
      }
      if (!this.dateOfBirth) {
        this.noDateOfBirth = true
        return false
      }
      if (this.selectedCategories.length === 0) {
        this.noCategories = true
        return false;
      }
      return true
    },
    addPlayerToCompetition() {
      // First check if player is completely new or from database, check if this.player exists
      if (this.player === null) {
        if (!this.validateSubmission()) {
          return
        }
        const playerSpec = {
          firstName: this.firstName,
          lastName: this.lastName,
          clubId: this.club.id,
          dateOfBirth: this.dateOfBirth
        }
        PlayerService.addPlayer(playerSpec).then(res => {
          this.player = res.data
          // If successful, register in each category
          this.registerPlayer()
        })
            .catch(() => {
              this.failedToAddPlayer = true;
            })
      } else {
        this.registerPlayer()
      }
      this.clearPlayer()
    },
    registerPlayer() {
      const categoriesToRegisterIn = this.competitionCategories.filter(val => this.selectedCategories.includes(val.category.name))
      categoriesToRegisterIn.forEach(category => {
        const registrationSpec = {
          playerId: this.player.id,
          competitionCategoryId: category.id
        }
        RegistrationService.registerPlayerSingles(this.competition.id, registrationSpec).then(() => {
          this.$toasted.show(this.$tc("toasts.playerAdded")).goAway(3000)
        })
      })
    },
  }
}
</script>

<style scoped>

#input-form {
  border: 1px solid gainsboro;
}
</style>