<template>
  <div class="container-fluid">
    <div class="row">

      <!-- Sidebar -->
      <div class="sidebar col-md-3">
        <div class="sidebar-header">
          <h4> {{ $t("player.sidebar.title") }}</h4>
        </div>
        <ul class="list-group list-group-flush">
          <li class="list-group-item" @click="makeChoice('name')" :class="choice === 'name' ? 'active' : ''">
            {{ $t("player.sidebar.name") }}
          </li>
          <li class="list-group-item" @click="makeChoice('club')" :class="choice === 'club' ? 'active' : ''">
            {{ $t("player.sidebar.club") }}
          </li>
          <li class="list-group-item" @click="makeChoice('categories')"
              :class="choice === 'categories' ? 'active' : ''">
            {{ $t("player.sidebar.categories") }}
          </li>
        </ul>
      </div>

      <!-- Main content -->
      <div id="main" class="col-md-8">
        <div class="row">
          <div class="col-md-4">
            <label for="playerSearch" class="form-label"> {{ $t("player.search") }}</label>
            <input type="text" class="form-control" v-model="searchString" id="playerSearch">
            <br>
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
</template>

<script>
import PlayerService from "@/common/api-services/player.service";

export default {
  name: "Player",
  data() {
    return {
      choice: "name",
      searchString: "",
      registeredPlayersAndGroups: {}
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
    fetchPlayers() {
      PlayerService.getRegisteredPlayersInCompetition(this.choice).then(res => {
        this.registeredPlayersAndGroups = res.data
      })
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


.active {
}
</style>