<template>
  <div class="modal__content">
    <i class="modal__close fas fa-times clickable" @click="$emit('close')"></i>
    <div class="container-fluid p-md-5 p-sm-3 p-1 light-grey" v-if="selectedMatch != null">
      <div id="player1" class="row">
        <div class="col-1 text-start">
          <p class="fw-bold">{{$t("results.games")}}</p>
        </div>
        <div class="col-4">
          <p class="fw-bold"> {{ getPlayerOne(selectedMatch) }}
            <br>
            <span class="fs-6 fw-light fst-italic clickable"
                  @click="giveWalkover(selectedMatch.competitionCategory.id, 'PLAYER_ONE')"> {{
                $t("results.modal.walkover")
              }}
         </span></p>
        </div>
        <div class="col-4">
          <p class="fw-bold">{{ getPlayerTwo(selectedMatch) }}
            <br>
            <span class="fs-6 fw-light fst-italic clickable"
                  @click="giveWalkover(selectedMatch.competitionCategory.id, 'PLAYER_TWO')">
              {{ $t("results.modal.walkover") }}
         </span></p>
        </div>
      </div>
      <div v-for="(game, index) in setList" class="row mb-3" :key="index">
        <div class="d-flex align-items-center col-1">
          <span class="pe-4">{{ index + 1 }}</span>
        </div>
        <div class="col-4">
          <input
              v-model="game.firstRegistrationResult"
              type="text"
              class="form-control p-1"
              max="30"
              min="0">
        </div>
        <div class="col-4">
          <input
              v-model="game.secondRegistrationResult"
              type="text"
              class="form-control p-1 col-4"
              max="30"
              min="0">
        </div>
      </div>
    </div>
    <div class="row mx-auto">
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
import {generalErrorHandler, getPlayerOneWithClub, getPlayerTwoWithClub} from "@/common/util";
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
    },
    resetVariables() {
      this.showInfo = false
      this.error = null
    },
    savePartialResults() {
      const resultsToSubmit = []
      this.setList.forEach(result => {
        if (!(result.firstRegistrationResult === 0 || result.firstRegistrationResult === "" && result.secondRegistrationResult === 0 || result.secondRegistrationResult === "")) {
          resultsToSubmit.push(result)
        }
      })
      ResultService.addPartialResult(this.competition.id, this.selectedMatch.id, {gameList: resultsToSubmit}).then(() => {
        this.$emit("closeAndUpdate", this.selectedMatch.id)
        this.$toasted.success(this.$tc("toasts.temporaryResultRegistered")).goAway(3000)
      }).catch(() => {
        this.$toasted.error(this.$tc("toasts.error.general.save")).goAway(7000)
      })
    },
    validateSubmission() {
      return true
    },
    saveResults() {
      if (!this.validateSubmission()) {
        return
      }
      const resultsToSubmit = []
      this.setList.forEach(result => {
        if (!(result.firstRegistrationResult === 0 || result.firstRegistrationResult === "" && result.secondRegistrationResult === 0 || result.secondRegistrationResult === "")) {
          resultsToSubmit.push(result)
        }
      })
      ResultService.updateFullMatchResult(this.competition.id, this.selectedMatch.id, {gameList: resultsToSubmit})
          .then(() => {
            this.$emit("closeAndUpdate", this.selectedMatch.id)
            this.$toasted.success(this.$tc("toasts.resultRegistered")).goAway(3000)
          }).catch(err => {
        this.errorHandler(err.data)
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
                this.$toasted.error(this.$tc("toasts.error.WO")).goAway(7000)
              })
        })
            .catch(() => {
              console.log("Couldn't fetch registrationId for player")
            })

      }
    },
    getPlayerOne: getPlayerOneWithClub,
    getPlayerTwo: getPlayerTwoWithClub,
    errorHandler: generalErrorHandler
  }
}
</script>

<style scoped>


</style>