<template>
  <div class="container-fluid">
    <div class="row">
      <!-- Sidebar -->
      <div class="sidebar col-md-3">
        <div class="sidebar-header">
          <h2> {{ $t("player.sidebarTitle") }}</h2>
        </div>
        <ul class="list-group list-group-flush">
          <li class="list-group-item" @click="makeChoice('name')"
              :class="choice === 'name' ? 'active' : ''">
            {{ $t("player.name") }}
          </li>
          <li class="list-group-item" @click="makeChoice('club')"
              :class="choice === 'club' ? 'active' : ''">
            {{ $t("player.club") }}
          </li>
          <li class="list-group-item" @click="makeChoice('categories')"
              :class="choice === 'categories' ? 'active' : ''">
            {{ $t("player.categories") }}
          </li>
        </ul>
      </div>

      <!-- Main content -->
      <div id="main" class="col-md-8">
          <div class="row">
            <h1>{{ $t("player.registeredPlayers") }}</h1>
          </div>
          <!-- Show currently registered players -->
          <div id="registered-players" class="mt-4 pt-3 border-top">
            <div class="row mb-4">
              <div class="col-sm-7">
                <label for="playerSearch" class="text-start form-label"> {{ $t("player.search") }}</label>
                <input type="text" class="form-control" v-model="searchString" id="playerSearch">
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
      </div>
  </div>
</template>

<script>

import ApiService from "../../api-services/api.service";

export default {
  name: "CompetitionPlayers",
  data() {
    return {
      choice: "name",
      searchString: "",
      registeredPlayersAndGroups: {},
      playerNotFound: false,
      playerSearchResults: [],
      competitionId: 0
    }
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
  mounted() {
    this.competitionId = this.$route.params.competitionId
    this.getPlayers()
  },
  methods: {
    makeChoice(choice) {
      this.choice = choice
      this.getPlayers()
    },
    getPlayers() {
      ApiService.getRegisteredPlayersInCompetition(this.choice, this.competitionId).then(res => {
        this.registeredPlayersAndGroups = res.data
      })
    },
    getSearchResult(searchResult) {
      return searchResult.firstName + " " + searchResult.lastName
    },
  }
}
</script>

<style scoped>
#main {
  margin: 10px 30px;
}

.heading {
  color: gray;
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
  color: var(--main-color);
}

#sort-choice .active {
  text-decoration: underline;
}

#sort-choice p:hover {
  cursor: pointer;
}
</style>