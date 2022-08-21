<template>
  <div>
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
              <p @click="makeSortingChoice('name')" class="d-inline" :class="sortingChoice === 'name' ? 'active' : ''">
                {{ $t("player.name") }}
              </p>
              <p class="mx-2 d-inline" @click="makeSortingChoice('club')" :class="sortingChoice === 'club' ? 'active' : ''">
                {{ $t("player.club") }}
              </p>
              <p class="d-inline" @click="makeSortingChoice('category')"
                 :class="sortingChoice === 'category' ? 'active' : ''">
                {{ $t("player.categories") }}
              </p>
            </div>
          </div>
        </div>
      </div>
      <div v-for="(players, grouping) in filterPlayers" :key="grouping">
        <div v-if="sortingChoice === 'category'" class="heading">
          <p class="mb-0"> {{ tryTranslateCategoryName(grouping) }} </p>
        </div>
        <div v-if="sortingChoice === 'club' || sortingChoice === 'name'" class="heading">
          <p class="mb-0"> {{ grouping }} </p>
        </div>
        <div v-for="player in players" :key="player.id" class="mt-2 d-flex">
          <p class="player-name clickable" @click="$router.push('/players/detail/' + player.id)">{{ player.lastName + ", " + player.firstName }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import PlayerService from "@/common/api-services/player.service";
import { tryTranslateCategoryName } from "@/common/util"

export default {
  name: "RegisteredPlayers",
  data() {
    return {
      sortingChoice: "name",
      registeredPlayersAndGroups: {},
      searchString: "",
      playerSearchResults: [],
      player: null,
    }
  },
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

  },
  methods: {
    makeSortingChoice(choice) {
      this.sortingChoice = choice
      this.fetchPlayers()
    },
    fetchPlayers() {
      PlayerService.getRegisteredPlayersInCompetition(this.sortingChoice, this.competition.id).then(res => {
        this.registeredPlayersAndGroups = res.data
      })
    },
    tryTranslateCategoryName: tryTranslateCategoryName
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