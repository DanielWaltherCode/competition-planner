<template>
  <div class="row">
    <!-- Players in group (simple list) -->
    <div class="col-sm-3 p-3 p-sm-2">
      <h5 class="black text-start fw-bolder">{{ $t("draw.pool.players") }}</h5>
      <p v-for="(playerInGroup, index) in group.players" class="text-start" :key="index">
        {{getName(playerInGroup.playerDTOs) }} {{ getClub(playerInGroup.playerDTOs) }}
      </p>
    </div>
    <!-- Current standing in group -->
    <div class="col-sm-9">
      <h5 class="black text-center fw-bolder">{{ $t("draw.pool.standing") }}</h5>
      <table class="table table-bordered">
        <thead>
        <tr>
          <th scope="col"></th>
          <th scope="col">{{ $t("draw.pool.nrMatches") }}</th>
          <th scope="col">{{ $t("draw.pool.matches") }}</th>
          <th scope="col">{{ $t("draw.pool.games") }}</th>
          <th scope="col">{{ $t("draw.pool.points") }}</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(standing, index) in group.groupStandingList" :key="index">
          <td class="text-start ps-3">{{index + 1 + ". " + getName(standing.player) }} {{ getClub(standing.player) }}</td>
          <td>{{standing.matchesPlayed}}</td>
          <td>{{standing.matchesWonLost.won}} - {{standing.matchesWonLost.lost}}</td>
          <td>{{standing.gamesWonLost.won}} - {{standing.gamesWonLost.lost}}</td>
          <td>{{standing.pointsWonLost.won}} - {{standing.pointsWonLost.lost}}</td>
        </tr>
        </tbody>
      </table>
      <div class="d-flex justify-content-end clickable" @click="showModal = true">
        <i v-if="hasSubGroupRanking(group.groupStandingList)" class="fas fa-info-circle">
          {{ $t("draw.pool.subgroupStanding.explanation") }}
        </i>
      </div>
      <!-- Modal -->
      <vue-final-modal v-model="showModal" classes="modal-container" content-class="modal-content">
        <pool-subgroup
            :group-standing-list="group.groupStandingList"
            v-on:close="showModal = false">

        </pool-subgroup>
      </vue-final-modal>
    </div>

  </div>
</template>

<script>
import PoolSubgroup from "@/components/draw/PoolSubgroup";
export default {
  name: "PoolDraw",
  components: {PoolSubgroup},
  props: {
    group: Object
  },
  data() {
    return {
      showModal: false
    }
  },
  methods: {
    getClub(playerDTOs) {
      if (playerDTOs.length === 1) {
        return playerDTOs[0].club.name
      } else if (playerDTOs.length === 2) {
        return playerDTOs[0].club.name + "/" + playerDTOs[1].club.name
      }
    },
    getName(playerDTOs) {
      if (playerDTOs.length === 1) {
        return playerDTOs[0].firstName + " " + playerDTOs[0].lastName
      } else if (playerDTOs.length === 2) {
        return playerDTOs[0].firstName + " " + playerDTOs[0].lastName + "/" +
            playerDTOs[1].firstName + " " + playerDTOs[1].lastName
      }
    },
    hasSubGroupRanking(groupStandingList) {
      let hasSubGroupStanding = false
      groupStandingList.forEach(standing => {
        if (standing.subgroupStanding !== null) {
          hasSubGroupStanding = true
        }
      })
      return hasSubGroupStanding
    }
  }
}
</script>

<style scoped>
::v-deep .modal-container {
  display: flex;
  justify-content: center;
  align-items: center;
}
::v-deep .modal-content {
  max-height: 90%;
  max-width: 75%;
  margin: 0 1rem;
  padding: 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.25rem;
  background: #fff;
}
</style>