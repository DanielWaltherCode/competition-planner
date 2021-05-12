<template>
  <div class="container-fluid">
    <div class="row">

      <!-- Sidebar -->
      <div class="sidebar col-md-3">
        <div class="sidebar-header">
          <h4> {{ $t("player.sidebar.title") }}</h4>
        </div>
        <ul class="list-group list-group-flush">
          <li class="list-group-item" @click="makeDisplayChoice('overview')" :class="displayChoice === 'overview' ? 'active' : ''">
            {{ $t("player.sidebar.overview") }}
          </li>
          <li class="list-group-item" @click="makeDisplayChoice('addNew')" :class="displayChoice === 'addNew' ? 'active' : ''">
            {{ $t("player.sidebar.addNew") }}
          </li>
        </ul>
      </div>

      <!-- Main content -->
      <div id="main" class="col-md-8">
        <div class="row">
          <h1>{{ $t("player.heading") }}</h1>
          <p class="text-start">{{ $t("player.headingHelper") }}</p>
        </div>
        <!-- Show currently registered players -->
        <div v-if="displayChoice === 'overview'" id="registered-players" class="mt-4 pt-3 border-top">
          <h3> {{ $t("player.registeredPlayers") }}</h3>
          <div class="row mb-4">
          <div class="col-sm-7">
            <label for="playerSearch" class="text-start form-label"> {{ $t("player.search") }}</label>
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

        <!-- Add new player -->
        <div v-if="displayChoice === 'addNew'">
          <p>Add new</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import PlayerService from "@/common/api-services/player.service";

export default {
  name: "Player",
  data() {
    return {
      choice: "name",
      searchString: "",
      registeredPlayersAndGroups: {},
      displayChoice: "overview"
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
    this.fetchPlayers()
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
      PlayerService.getRegisteredPlayersInCompetition(this.choice).then(res => {
        this.registeredPlayersAndGroups = res.data
      })
    },
    reRoute() {
      this.$router.push("schedule")
    }
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