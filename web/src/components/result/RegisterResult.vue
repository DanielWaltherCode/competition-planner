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
        <td>{{ getPlayerOne(this.selectedMatch) }}</td>
        <td v-for="(game, index) in setList" :key="index">
          <select class="form-select custom-select p-1 text-center" v-model="game.firstRegistrationResult">
            <option v-for="i in 30" :key="i" :value="i" class="p-2">
              {{ i }}
            </option>
          </select>
        </td>
      </tr>

      <!-- Player 2 -->
      <tr v-if="selectedMatch !== null">
        <td>{{getPlayerTwo(this.selectedMatch)}}</td>
        <td v-for="(game, index) in setList" :key="index">
          <select class="form-select custom-select p-1 text-center" v-model="game.secondRegistrationResult">
            <option v-for="i in 30" :key="i" :value="i" class="p-2">
              {{ i }}
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
        <button type="button" class="btn btn-primary" @click="saveResults">{{ $t("results.modal.registerFinishedMatch") }}</button>
        <button type="button" class="btn btn-outline-warning" @click="savePartialResults">{{ $t("results.modal.savePartial") }}</button>
        <button type="button" class="btn btn-outline-danger" @click="deleteResults">{{ $t("results.modal.delete") }}</button>
    </div>
    <div v-if="error != null" class="p-2">
      <p class="mb-1 text-danger"> {{error}}</p>
      <span class="clickable text-decoration-underline text-primary"  @click="$router.push('/classes')" > {{$t("results.modal.linkToCategorySettings")}} </span>
    </div>
    <!-- Helper texts -->
    <div class="d-flex justify-content-center align-items-center clickable" @click="showInfo = !showInfo" >
      <p class="mb-0">{{$t("results.modal.helperTexts")}}</p>
      <i class="fas fa-info-circle mx-3"></i>
    </div>
    <div class="modal-footer" v-if="showInfo">
      <p class="text-start py-3">{{$t("results.modal.info")}}
      <span class="clickable text-decoration-underline text-primary"  @click="$router.push('/classes')" > {{$t("results.modal.linkToCategorySettings")}} </span>
      </p>
      <div>
        <definition-component :word="$t('results.modal.registerFinishedMatch')" :explanation="$t('results.modal.registerFinishedMatchExplanation')"></definition-component>
        <definition-component :word="$t('results.modal.savePartial')" :explanation="$t('results.modal.savePartialExplanation')"></definition-component>
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
  watch: {
    selectedMatch: function() {
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
      CategoryService.getCategoryGameRules(this.selectedMatch.competitionCategory.id).then(res => {
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
        if (!(result.firstRegistrationResult ===0 && result.secondRegistrationResult === 0)) {
          resultsToSubmit.push(result)
        }
      })
      ResultService.addPartialResult(this.selectedMatch.id, {gameList: resultsToSubmit}).then(() => {
        this.$emit("closeAndUpdate", this.selectedMatch.id)
        this.$toasted.show(this.$tc("toasts.temporaryResultRegistered")).goAway(3000)
      }).catch(err => {
        console.log(err.data)
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
        if (!(result.firstRegistrationResult ===0 && result.secondRegistrationResult === 0)) {
          resultsToSubmit.push(result)
        }
      })
      ResultService.updateFullMatchResult(this.selectedMatch.id, {gameList: resultsToSubmit}).then(() => {
        this.$emit("closeAndUpdate", this.selectedMatch.id)
        this.$toasted.show(this.$tc("toasts.resultRegistered")).goAway(3000)
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
      if(confirm(this.$tc("confirm.deleteResult"))) {
        ResultService.deleteResult(this.selectedMatch.id).then(() => {
          this.$emit("close", this.selectedMatch.id)
        })
      }
    },
    getPlayerOne: getPlayerOneWithClub,
    getPlayerTwo: getPlayerTwoWithClub
  }
}
</script>

<style scoped>

.modal__close {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
}

.modal__content {
  flex-grow: 1;
  overflow-y: auto;
}
.modal-footer button {
  width: 200px;
}
</style>