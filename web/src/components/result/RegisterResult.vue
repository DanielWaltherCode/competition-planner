<template>
  <div class="modal__content">
    <i class="modal__close fas fa-times clickable" @click="$emit('close')"></i>
    <div class="row p-md-5 p-sm-3 p-1">
      <table class="table table-borderless table-responsive table-responsive-sm">
        <thead>
        <tr>
          <th class="col-3 col-sm-4"></th>
          <th v-for="set in matchRules.numberOfSets" :key="set" class="col-auto">{{ set }}</th>
        </tr>
        </thead>
        <tbody>
        <!-- Player 1 -->
        <tr v-if="this.selectedMatch !== null">
          <td>{{ getPlayerOne(this.selectedMatch) }}
            <br>
            <button class="btn btn-light btn-sm ps-1" type="button"
                    @click="giveWalkover(selectedMatch.competitionCategory.id, 'PLAYER_ONE')">
              {{ $t("results.modal.walkover") }}
            </button>
          </td>
          <td v-for="(game, index) in setList" :key="index">
            <select class="form-select custom-select p-1 text-center" v-model="game.firstRegistrationResult">
              <option v-for="i in 31" :key="i" :value="i-1" class="p-2">
                {{ i - 1 }}
              </option>
            </select>
          </td>
        </tr>

        <!-- Player 2 -->
        <tr v-if="selectedMatch !== null">
          <td>{{ getPlayerTwo(this.selectedMatch) }}
            <br>
            <button class="btn btn-light btn-sm ps-1" type="button"
                    @click="giveWalkover(selectedMatch.competitionCategory.id, 'PLAYER_TWO')">
              {{ $t("results.modal.walkover") }}
            </button>
          </td>
          <td v-for="(game, index) in setList" :key="index">
            <select class="form-select custom-select p-1 text-center" v-model="game.secondRegistrationResult">
              <option v-for="i in 31" :key="i" :value="i-1" class="p-2">
                {{ i - 1 }}
              </option>
            </select>
          </td>
        </tr>
        </tbody>
      </table>
      <div class="modal-footer p-2">
        <button type="button" class="btn btn-secondary" @click="$emit('close')">
          {{ $t("general.close") }}
        </button>
        <button type="button" class="btn btn-primary" @click="saveResults">{{
            $t("results.modal.registerFinishedMatch")
          }}
        </button>
        <button type="button" class="btn btn-outline-warning" @click="savePartialResults">
          {{ $t("results.modal.savePartial") }}
        </button>
        <button type="button" class="btn btn-outline-danger" @click="deleteResults">{{
            $t("results.modal.delete")
          }}
        </button>
      </div>
      <div v-if="error != null" class="p-2">
        <p class="mb-1 text-danger"> {{ error }}</p>
        <span class="clickable text-decoration-underline text-primary"
              @click="$router.push('/classes')"> {{ $t("results.modal.linkToCategorySettings") }} </span>
      </div>
      <!-- Helper texts -->
      <div class="d-flex justify-content-center align-items-center clickable" @click="showInfo = !showInfo">
        <p class="mb-0">{{ $t("results.modal.helperTexts") }}</p>
        <i class="fas fa-info-circle mx-3"></i>
      </div>
      <div class="modal-footer" v-if="showInfo">
        <p class="text-start py-3">{{ $t("results.modal.info") }}
          <span class="clickable text-decoration-underline text-primary"
                @click="$router.push('/classes')"> {{ $t("results.modal.linkToCategorySettings") }} </span>
        </p>
        <div>
          <definition-component :word="$t('results.modal.registerFinishedMatch')"
                                :explanation="$t('results.modal.registerFinishedMatchExplanation')"></definition-component>
          <definition-component :word="$t('results.modal.savePartial')"
                                :explanation="$t('results.modal.savePartialExplanation')"></definition-component>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ResultService from "@/common/api-services/result.service";
import {getPlayerOneWithClub, getPlayerTwoWithClub} from "@/common/util";
import CategoryService from "@/common/api-services/category.service";
import DefinitionComponent from "@/components/general/DefinitionComponent";
import RegistrationService from "@/common/api-services/registration.service";

export default {
  name: "RegisterResult",
  components: {DefinitionComponent},
  props: {
    selectedMatch: Object
  },
  data() {
    return {
      matchRules: {},
      setList: [],
      showInfo: false,
      error: null
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  watch: {
    selectedMatch: function () {
      this.setUp()
    }
  },
  created() {
    this.setUp()
  },
  mounted() {
    this.setUp()
  },
  methods: {
    setUp() {
      this.resetVariables()
      if (this.selectedMatch === null) {
        return
      }
      CategoryService.getCategoryGameRules(this.competition.id, this.selectedMatch.competitionCategory.id).then(res => {
        this.matchRules = res.data

        // Add already registered game result to our local setlist
        this.setList = this.selectedMatch.result.gameList.slice()
        let counter = this.setList.length
        if (counter === undefined) {
          counter = 0
        }
        while (counter < this.matchRules.numberOfSets) {
          this.addGame()
          counter++
        }
      })
          .catch(err => {
            console.log("Couldn't fetch match rules")
            console.log(err)
          })
    },
    resetVariables() {
      this.showInfo = false
      this.error = null
    },
    savePartialResults() {
      const resultsToSubmit = []
      this.setList.forEach(result => {
        if (!(result.firstRegistrationResult === 0 && result.secondRegistrationResult === 0)) {
          resultsToSubmit.push(result)
        }
      })
      ResultService.addPartialResult(this.competition.id, this.selectedMatch.id, {gameList: resultsToSubmit}).then(() => {
        this.$emit("closeAndUpdate", this.selectedMatch.id)
        this.$toasted.success(this.$tc("toasts.temporaryResultRegistered")).goAway(3000)
      }).catch(() => {
        this.$toasted.error(this.$tc("toasts.error.general")).goAway(5000)
      })
    },
    validateSubmission() {
      return true
    },
    saveResults() {
      if (!this.validateSubmission()) {
        return
      }
      this.error = null
      const resultsToSubmit = []
      this.setList.forEach(result => {
        if (!(result.firstRegistrationResult === 0 && result.secondRegistrationResult === 0)) {
          resultsToSubmit.push(result)
        }
      })
      ResultService.updateFullMatchResult(this.competition.id, this.selectedMatch.id, {gameList: resultsToSubmit}).then(() => {
        this.$emit("closeAndUpdate", this.selectedMatch.id)
        this.$toasted.success(this.$tc("toasts.resultRegistered")).goAway(3000)
      }).catch(err => {
        const response = err.response.data
        console.log(response)
        this.error = this.$t("results.modal.errors." + response.message)
        console.log(this.error)
      })
    },
    addGame() {
      this.setList.push(
          {
            firstRegistrationResult: 0,
            secondRegistrationResult: 0,
            gameNumber: (this.setList.length + this.selectedMatch.result.gameList.length) + 1
          })
    },
    deleteResults() {
      if (confirm(this.$tc("confirm.deleteResult"))) {
        ResultService.deleteResult(this.competition.id, this.selectedMatch.id).then(() => {
          this.$emit("closeAndUpdate", this.selectedMatch.id)
        })
      }
    },
    giveWalkover(categoryId, player) {
      let playerName = ""
      let playerId = -1
      if (player === "PLAYER_ONE") {
        playerName = getPlayerOneWithClub(this.selectedMatch)
        playerId = this.selectedMatch.firstPlayer[0].id
      } else if (player === "PLAYER_TWO") {
        playerName = getPlayerTwoWithClub(this.selectedMatch)
        playerId = this.selectedMatch.secondPlayer[0].id
      }
      if (confirm(this.$t("player.walkoverWarningText", {player: playerName}))) {
        RegistrationService.getRegistrationId(this.competition.id, categoryId, playerId).then(res => {
          const registrationId = res.data
          RegistrationService.giveWalkover(this.competition.id, categoryId, registrationId).then(() => {
            this.$emit("walkover")
            this.$toasted.success(this.$tc("toasts.resultRegistered")).goAway(3000)
          })
              .catch(() => {
                this.$toasted.error(this.$tc("toasts.error.WO")).goAway(5000)
              })
        })
            .catch(() => {
              console.log("Couldn't fetch registrationId for player")
            })

      }
    },
    getPlayerOne: getPlayerOneWithClub,
    getPlayerTwo: getPlayerTwoWithClub
  }
}
</script>

<style scoped>


</style>