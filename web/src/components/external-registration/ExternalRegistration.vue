<template>
  <div class="row bg-grey">
    <!-- Main content -->
    <div id="main" class="col-lg-11 mx-auto ps-0 bg-grey">
      <!-- General information about competition -->
      <div class="row p-3 m-md-2 custom-card bg-white">
        <h1>{{ $t("externalRegistration.title") }}</h1>

        <!-- Selections -->
        <div id="selections" class="p-4">
          <label class="form-label" for="select-competition"> {{ $t("externalRegistration.pickCompetition") }}</label>
          <select id="select-competition" v-model="selectedCompetition" class="form-select mb-3"
                  @change="getCategories">
            <option v-for="competition in competitions" :key="competition.id" class="form-control" :value="competition">
              {{ competition.name }}
            </option>
          </select>
          <label class="form-label" for="select-competition"> {{ $t("externalRegistration.pickCategory") }}</label>
          <select id="select-category" v-model="selectedCategory" class="form-select" @change="getRegisteredPlayers">
            <option v-for="category in categories" :key="category.id" class="form-control" :value="category">
              {{ tryTranslateCategoryName(category.category.name) }}
            </option>
          </select>
        </div>

        <!-- Registrations -->
        <div id="registration" v-if="selectedCompetition != null && selectedCategory != null">
          <h2> {{
              $t("externalRegistration.secondTitle",
                  {
                    competition: selectedCompetition.name,
                    category: tryTranslateCategoryName(selectedCategory.category.name)
                  })
            }}</h2>
          <div class="row">
            <div class="col-12 col-md-4 custom-card p-2">
              <p class="fw-bold"> {{ $t("externalRegistration.playersInClub", {club: loggedInClub.name}) }}</p>
              <div class="text-start">
                <p v-for="player in playersThatCanBeRegistered" :key="player.id">
                  {{ player.lastName + " " + player.firstName }} <i class="fas fa-caret-right clickable"
                                                          @click="addToRegister(player)"></i>
                </p>
              </div>
            </div>
            <!-- Register for singles -->
            <div class="col-12 col-md-4 custom-card mx-2 p-2" v-if="selectedCategory.category.type === 'SINGLES'">
              <p class="fw-bold"> {{ $t("externalRegistration.toRegister") }}</p>
              <div class="text-start">
                <p v-for="player in playersToRegister" :key="player.id">
                  <i class="fas fa-caret-left clickable"
                     @click="removeFromRegister(player)"/>
                  {{ getFormattedPlayerName(player) }}
                </p>
              </div>
              <div class="d-flex justify-content-center">
                <button v-if="playersToRegister.length > 0"
                        class="btn btn-primary" @click="registerPlayerSingles">{{ $t("externalRegistration.register") }}
                </button>
              </div>
            </div>
            <!-- Register for doubles -->
            <div class="col-12 col-md-4 custom-card mx-2 p-2" v-if="selectedCategory.category.type === 'DOUBLES'">
              <p class="fw-bold"> {{ $t("externalRegistration.toRegister") }}</p>
              <div v-if="playersToRegister.length > 0"  class="text-start">
                <i class="fas fa-caret-left clickable pe-1"
                   @click="removeFromRegister(playersToRegister[0])"/>
                <input type="text" disabled :value="getFormattedPlayerName(playersToRegister[0])">

                <div v-if="playersToRegister.length === 1" class="pt-4">
                  <button @click="searchOtherClub = !searchOtherClub" class="btn btn-secondary">
                    {{ $t("externalRegistration.searchOtherClub") }}
                  </button>
                  <search-player-component v-if="searchOtherClub" :with-competition="false" ref="double2" class="justify-content-center" @clear-player="playersToRegister.pop()"
                                           @player-found="playersToRegister.push($event)"></search-player-component>

                </div>
                <div v-else>
                  <i class="fas fa-caret-left clickable"
                     @click="removeFromRegister(playersToRegister[1])"/>
                  <input type="text" disabled :value="getFormattedPlayerName(playersToRegister[1])">
                </div>
              </div>
              <div class="d-flex justify-content-center">
                <button v-if="playersToRegister.length === 2"
                        class="btn btn-primary" @click="registerDoublesPlayers">{{ $t("externalRegistration.register") }}
                </button>
              </div>
            </div>
            <!-- Already registered -->
            <div class="col-12 col-md-3 custom-card p-2">
              <p class="fw-bold"> {{ $t("externalRegistration.registered") }}</p>
              <div v-if="registeredPlayers.length > 0">
                <div v-for="(innerPlayerList, index) in registeredPlayers" :key="index">
                  <div v-if="innerPlayerList.length === 1" class="text-start mb-0 pb-0">
                    <p> {{ getFormattedPlayerName(innerPlayerList[0]) }}</p>
                  </div>
                  <div v-if="innerPlayerList.length === 2">
                    <p> {{ getFormattedPlayerNameWithClub(innerPlayerList[0]) }} /
                      {{ getFormattedPlayerNameWithClub(innerPlayerList[1]) }}
                    </p>
                  </div>
                </div>
              </div>

            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  generalErrorHandler,
  getFormattedDate,
  getFormattedPlayerName,
  getFormattedPlayerNameWithClub,
  tryTranslateCategoryName
} from "@/common/util";
import CompetitionService from "@/common/api-services/competition.service";
import PlayerService from "@/common/api-services/player.service";
import ClubService from "@/common/api-services/club.service";
import ExternalRegistrationService from "@/common/api-services/external-registration";
import SearchPlayerComponent from "@/components/player/SearchPlayerComponent";

export default {
  name: "ExternalRegistration",
  components: {SearchPlayerComponent},
  data() {
    return {
      competitions: [],
      categories: [],
      selectedCompetition: null,
      selectedCategory: null,
      loggedInClub: "",
      allPlayers: [],
      playersToRegister: [],
      registeredPlayers: [],
      doublesPlayer2: null,
      searchOtherClub: false

    }
  },
  computed: {
    playersThatCanBeRegistered: function () {
      const idsThatCannotBeRegistered = []
      this.playersToRegister.forEach(it => idsThatCannotBeRegistered.push(it.id))
      this.registeredPlayers.forEach(playerList => {
        playerList.forEach(player => idsThatCannotBeRegistered.push(player.id))
      })
      return this.allPlayers.filter(it => !idsThatCannotBeRegistered.includes(it.id))
    }
  },
  mounted() {
    this.getCompetitions();
    ClubService.getLoggedInClub().then(res => {
      this.loggedInClub = res.data
      this.getPlayersInClub()
    })
  },
  methods: {
    removeFromRegister(player) {
      this.playersToRegister = this.playersToRegister.filter(it => it.id !== player.id)
    },
    addToRegister(player) {
      if (this.selectedCategory.category.type === 'SINGLES') {
        this.playersToRegister.push(player)
      }
      else if (this.selectedCategory.category.type === 'DOUBLES') {
        if (this.playersToRegister.length < 2) {
          this.playersToRegister.push(player)
        }
      }
    },
    getCompetitions() {
      const date = new Date()
      CompetitionService.getAll(this.getFormattedDate(date)).then(res => {
        this.competitions = res.data
      })
          .catch(err => {
            this.generalErrorHandler(err)
          })
    },
    getCategories() {
      if (this.selectedCompetition == null) {
        return
      }
      this.selectedCategory = null
      ExternalRegistrationService.getCompetitionCategories(this.selectedCompetition.id).then(res => {
        this.categories = res.data
      }).catch(err => {
        this.generalErrorHandler(err)
      })
    },
    getPlayersInClub() {
      PlayerService.getPlayersByClubId(this.loggedInClub.id).then(res => {
        this.allPlayers = res.data
      })
    },
    registerPlayerSingles() {
      this.playersToRegister.forEach(player => {
        const registrationSpec = {
          playerId: player.id,
          competitionCategoryId: this.selectedCategory.id
        }

        ExternalRegistrationService.registerPlayerSingles(registrationSpec).then(() => {
          this.playersToRegister = []
          this.getRegisteredPlayers()
        }).catch(err => {
          this.errorHandler(err.data)
        })
      })
    },
    registerDoublesPlayers() {
      if (this.playersToRegister.length !== 2) {
        this.$toasted.error(this.$tc("toasts.player.doubleRegistrationError")).goAway(7000)
        return;
      }

      const registrationSpec = {
        playerOneId: this.playersToRegister[0].id,
        playerTwoId: this.playersToRegister[1].id,
        competitionCategoryId: this.selectedCategory.id
      }
      ExternalRegistrationService.registerPlayerDoubles(registrationSpec).then(() => {
        this.$toasted.success(this.$tc("toasts.player.added")).goAway(3000)
        this.playersToRegister = []
        this.getRegisteredPlayers()
      }).catch(err => {
        this.errorHandler(err.data)
      })
    },
    getRegisteredPlayers() {
      ExternalRegistrationService.getRegisteredPlayersInCategoryForClub(this.selectedCategory.id, this.loggedInClub.id)
          .then(res => {
            this.registeredPlayers = res.data
          }).catch(err => {
        this.errorHandler(err.data)
      })
    },
    getFormattedDate: getFormattedDate,
    generalErrorHandler: generalErrorHandler,
    tryTranslateCategoryName: tryTranslateCategoryName,
    getFormattedPlayerName: getFormattedPlayerName,
    getFormattedPlayerNameWithClub: getFormattedPlayerNameWithClub,
    errorHandler: generalErrorHandler
  }
}
</script>

<style scoped>

</style>