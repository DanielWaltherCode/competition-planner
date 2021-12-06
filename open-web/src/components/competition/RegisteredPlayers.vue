<template>
  <div class="row mx-1">
    <div class="col-md-10 mx-auto custom-card p-2 p-md-5">
      <!-- Show currently registered players -->
      <div id="registered-players" class="row m-auto">
        <h3> {{ $t("player.registeredPlayers") }}</h3>
        <div class="col-sm-7 py-4 mb-2">
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
              <p @click="makeSortingChoice('name')" class="d-inline" :class="sortingChoice === 'name' ? 'active' : ''">
                {{ $t("player.name") }}
              </p>
              <p class="mx-2 d-inline" @click="makeSortingChoice('club')"
                 :class="sortingChoice === 'club' ? 'active' : ''">
                {{ $t("player.club") }}
              </p>
              <p class="d-inline" @click="makeSortingChoice('categories')"
                 :class="sortingChoice === 'categories' ? 'active' : ''">
                {{ $t("player.categories") }}
              </p>
            </div>
          </div>
        </div>
        <div v-for="(players, grouping) in filterPlayers" :key="grouping">
          <div class="heading">
            <p class="mb-0"> {{ grouping }} </p>
          </div>
          <div v-for="player in players" :key="player.id" class="mt-2 d-flex">
            <p class="player-name clickable fs-6" @click="$router.push(`/competition/${competitionId}/player/${player.id}/detail`)">
              {{ player.lastName + ", " + player.firstName }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import ApiService from "../../api-services/api.service";

export default {
  name: "RegisteredPlayers",
  data() {
    return {
      sortingChoice: "name",
      registeredPlayersAndGroups: {},
      searchString: "",
      playerSearchResults: [],
      player: null,
      competitionId: null
    }
  },
  mounted() {
    this.competitionId = this.$route.params.competitionId
    this.fetchPlayers()
  },
  computed: {
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
  methods: {
    makeSortingChoice(choice) {
      this.sortingChoice = choice
      this.fetchPlayers()
    },
    fetchPlayers() {
      ApiService.getRegisteredPlayersInCompetition(this.sortingChoice, this.competitionId).then(res => {
        this.registeredPlayersAndGroups = res.data
      })
    },
  }
}
</script>

<style scoped>
.heading {
  color: var(--clr-primary-400);
  border-bottom: 1px solid lightgrey;
  text-align: left;
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
</style>