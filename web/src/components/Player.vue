<template>
  <div class="container-fluid">
    <div class="row">
      <div id="sidebar" class="col-md-3">
        <div id="sidebar-header">
          <h4> {{ $t("player.sidebar.title") }}</h4>
        </div>
        <ul class="list-group list-group-flush">
          <li class="list-group-item" @click="makeChoice('name')" :class="choice === 'name' ? 'active' : ''">
            {{ $t("player.sidebar.name") }}
          </li>
          <li class="list-group-item" @click="makeChoice('club')" :class="choice === 'club' ? 'active' : ''">
            {{ $t("player.sidebar.club") }}
          </li>
          <li class="list-group-item" @click="makeChoice('categories')" :class="choice === 'categories' ? 'active' : ''">
            {{ $t("player.sidebar.categories") }}
          </li>
        </ul>
        <div></div>
      </div>
      <div id="main" class="col-md-8">
        <div v-for="(players, grouping) in registeredPlayers.groupingsAndPlayers" :key="grouping">
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
      registeredPlayers: []
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
        this.registeredPlayers = res.data
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

#sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

#sidebar {
  min-height: 100vh;
  z-index: 0;
  box-shadow: 3px 3px 2px 1px #efefef;
}

@media only screen and (max-width: 768px) {
  #sidebar {
    min-height: fit-content;
  }
}

#sidebar-header:hover {
  cursor: pointer;
}

#sidebar li:hover {
  opacity: 0.8;
}

#sidebar li:hover {
  cursor: pointer;
}
.players {
  margin-top: 10px;
  display: flex;
}

.player-name {
  font-size: 80%;
  color: var(--emphasis-color);
}



.active {
  background-color: var(--emphasis-color) !important;
}
</style>