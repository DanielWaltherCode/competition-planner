<template>
  <main class="container-fluid">
    <div class="row">
      <h1 class="p-4">
        <i @click="$router.push('/classes')" class="fas fa-arrow-left" style="float: left"></i>
        {{ $t("player.title") }}
        <i @click="$router.push('/draw')" class="fas fa-arrow-right" style="float: right"></i>
      </h1>

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
        <RegisteredPlayers v-if="displayChoice === 'overview'"></RegisteredPlayers>
        <AddPlayerToCompetition v-if="displayChoice === 'addNew'"></AddPlayerToCompetition>
      </div>
    </div>
  </main>
</template>

<script>
import RegisteredPlayers from "@/components/player/RegisteredPlayers";
import AddPlayerToCompetition from "@/components/player/AddPlayerToCompetition";

export default {
  name: "Player",
  data() {
    return {
      displayChoice: "overview",
    }
  },
  components: {AddPlayerToCompetition, RegisteredPlayers},

  methods: {
    makeDisplayChoice(choice) {
      this.displayChoice = choice
    },
  }
}
</script>

<style scoped>

h1 {
  background-color: var(--clr-primary-100);
  margin-bottom: 0;
}

</style>