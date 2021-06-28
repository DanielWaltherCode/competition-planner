<template>
  <main>

    <h1 class="p-4">{{ $t("player.heading") }}</h1>
    <div class="container-fluid">
      <div class="row">

        <!-- Sidebar -->
        <div class="sidebar col-md-3">
          <div class="sidebar-header">
            <h4> {{ $t("player.sidebar.title") }}</h4>
          </div>
          <ul class="list-group list-group-flush">
            <li class="list-group-item" @click="makeDisplayChoice('overview')"
                :class="displayChoice === 'overview' ? 'active' : ''">
              {{ $t("player.sidebar.overview") }}
            </li>
            <li class="list-group-item" @click="makeDisplayChoice('addNew')"
                :class="displayChoice === 'addNew' ? 'active' : ''">
              {{ $t("player.sidebar.addNew") }}
            </li>
          </ul>
        </div>

        <!-- Main content -->
        <div id="main" class="col-md-9 mx-auto">
          <div v-if="displayChoice === 'overview'">
            <div class="row blue-section">
              <p class="text-start col-md-9 mx-auto p-4">{{ $t("player.headingHelper") }}</p>
            </div>
            <!-- Show currently registered players -->
            <div id="registered-players" class="pt-4 m-auto">
              <h3> {{ $t("player.registeredPlayers") }}</h3>
              <div class="row col-md-10 mx-auto">
                <div class="col-sm-7 mb-2">
                  <div class="d-flex align-items-center mb-2">
                    <i class="fas fa-search me-2"></i>
                    <label for="playerSearch" class="form-label d-flex mb-0"> {{ $t("player.search") }}</label>
                  </div>
                  <input type="text" class="form-control" v-model="searchString" id="playerSearch">
                </div>
                <div class="col-sm-5 justify-content-center">
                  <p class="fw-bolder"> {{ $t("player.sortBy") }}</p>
                  <div>
                    <div id="sort-choice">
                      <p @click="makeChoice('name')" class="d-inline" :class="choice === 'name' ? 'active' : ''">
                        {{ $t("player.name") }}
                      </p>
                      <p class="mx-2 d-inline" @click="makeChoice('club')" :class="choice === 'club' ? 'active' : ''">
                        {{ $t("player.club") }}
                      </p>
                      <p class="d-inline" @click="makeChoice('categories')"
                         :class="choice === 'categories' ? 'active' : ''">
                        {{ $t("player.categories") }}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
              <div v-for="(players, grouping) in filterPlayers" :key="grouping">
                <div class="heading">
                  <p> {{ grouping }} </p>
                </div>
                <div v-for="player in players" :key="player.id" class="players">
                  <p class="player-name">{{ player.lastName + ", " + player.firstName }}</p>
                </div>
              </div>
            </div>
          </div>
          <!-- Add new player -->
          <div v-if="displayChoice === 'addNew'">
            <div class="row p-4 blue-section">
              <h4 class="text-start">{{ $t("player.add.heading") }}</h4>
              <p class="text-start">{{ $t("player.add.helperText") }}</p>
            </div>
            <div class="row">
              <div class="d-flex p-4">
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
              <div id="input-form" class="rounded row p-3">
                <div class="col-md-11">
                  <div class="row mx-auto">
                    <div class="col-sm-6 mb-3">
                      <label for="firstName" class="text-start form-label"> {{ $t("player.add.firstName") }}</label>
                      <input type="text" :disabled="player !== null" class="form-control" v-model="firstName"
                             id="firstName">
                    </div>

                    <div class="col-sm-6 mb-3">
                      <label for="lastName" class="text-start form-label"> {{ $t("player.add.lastName") }}</label>
                      <input type="text" :disabled="player !== null" class="form-control" v-model="lastName"
                             id="lastName">
                    </div>

                    <div class="col-sm-6 mb-3">
                      <label for="club-select" class="form-label"> {{ $t("player.add.club") }}</label>
                      <select :disabled="player !== null" class="form-control" name="club-select" id="club-select"
                              v-model="club">
                        <option v-for="club in clubs" :key="club.id" :value="club">
                          {{ club.name }}
                        </option>
                      </select>
                    </div>
                    <div class="col-sm-6 mb-3">
                      <label for="dateOfBirth" class="text-start form-label"> {{ $t("player.add.dateOfBirth") }}</label>
                      <input type="date" :disabled="player !== null" class="form-control" v-model="dateOfBirth"
                             id="dateOfBirth">
                    </div>
                    <div class="form-check col-sm-6 mb-3">
                      <div v-for="category in competitionCategories" :key="category.competitionCategoryId">

                        <input class="form-check-input ms-1" type="checkbox" :value="category.categoryName"
                               :id="category.competitionCategoryId" v-model="selectedCategories">
                        <label class="form-check-label d-flex ps-2" :for="category.competitionCategoryId">
                          {{ category.categoryName }}
                        </label>
                      </div>
                    </div>
                    <div class="d-flex justify-content-end">
                      <button type="button" class="btn btn-primary" @click="addPlayerToCompetition">
                        {{ $t("player.add.buttonText") }}
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import PlayerService from "@/common/api-services/player.service";
import Autocomplete from '@trevoreyre/autocomplete-vue'
import '@trevoreyre/autocomplete-vue/dist/style.css';
import DrawService from "@/common/api-services/draw.service";
import ClubService from "@/common/api-services/club.service";
import RegistrationService from "@/common/api-services/registration.service";

export default {
  name: "Player",
  data() {
    return {
      choice: "name",
      searchString: "",
      registeredPlayersAndGroups: {},
      displayChoice: "overview",
      playerNotFound: false,
      playerSearchResults: [],
      player: null,
      clubs: [],
      firstName: "",
      lastName: "",
      club: "",
      dateOfBirth: "",
      competitionCategories: [],
      selectedCategories: [],
      failedToAddPlayer: false
    }
  },
  components: {Autocomplete},
  computed: {
    competition: function () {
      return this.$store.getters.competition
    },
    filterPlayers() {
      let filteredPlayersAndGroups = {}
      if (this.searchString !== "") {
        for (const grouping in this.registeredPlayersAndGroups.groupingsAndPlayers) {
          const filteredPlayers = []
          this.registeredPlayersAndGroups.groupingsAndPlayers[grouping].forEach(player => {
            if (player.firstName.toLowerCase().includes(this.searchString.toLowerCase())) {
              filteredPlayers.push(player)
            } else if (player.lastName.toLowerCase().includes(this.searchString.toLowerCase())) {
              filteredPlayers.push(player)
            }
          })
          if (filteredPlayers.length > 0) {
            filteredPlayersAndGroups[grouping] = filteredPlayers
          }
        }
      } else {
        filteredPlayersAndGroups = this.registeredPlayersAndGroups.groupingsAndPlayers
      }
      return filteredPlayersAndGroups
    }
  },
  mounted() {
    this.fetchPlayers()
    DrawService.getCompetitionCategories(this.competition.id).then(res => {
      this.competitionCategories = res.data.categories
    })
    ClubService.getClubs().then(res => {
      this.clubs = res.data
    })
  },
  methods: {
    makeChoice(choice) {
      this.choice = choice
      this.fetchPlayers()
    },
    makeDisplayChoice(choice) {
      this.displayChoice = choice
    },
    fetchPlayers() {
      PlayerService.getRegisteredPlayersInCompetition(this.choice, this.competition.id).then(res => {
        this.registeredPlayersAndGroups = res.data
      })
    },
    searchPlayers(input) {
      if (input.length < 1) {
        return [];
      }
      return new Promise(resolve => {
        PlayerService.searchAllPlayers(input).then(res => {
          this.playerNotFound = false
          resolve(res.data)
        }).catch(() => {
          this.nameNotFound = true;
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
      this.club = result.club.name
      this.dateOfBirth = result.dateOfBirth
    },
    addPlayerToCompetition() {
      // First check if player is completely new or from database, check if this.player.id exists
      if (this.player === null) {
        // TODO -- Add input validation
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
      const categoriesToRegisterIn = this.competitionCategories.filter(val => this.selectedCategories.includes(val.categoryName))
      categoriesToRegisterIn.forEach(category => {
        const registrationSpec = {
          playerId: this.player.id,
          competitionCategoryId: category.competitionCategoryId
        }
        console.log("Registrationspec", registrationSpec)
        RegistrationService.registerPlayerSingles(this.competition.id, registrationSpec)
      })
    },
    reRoute() {
      this.$router.push("schedule")
    }
  }
}
</script>

<style scoped>

h1 {
  background-color: var(--clr-primary-100);
  margin-bottom: 0;
}

.heading {
  color: var(--clr-primary-400);
  border-bottom: 1px solid lightgrey;
  text-align: left;
}

.heading p {
  margin-bottom: 0;
}

.players {
  margin-top: 10px;
  display: flex;
}

.player-name {
  font-size: 80%;
}

#sort-choice .active {
  text-decoration: underline;
}

#sort-choice p:hover {
  cursor: pointer;
}

#input-form {
  border: 1px solid gainsboro;
}
</style>