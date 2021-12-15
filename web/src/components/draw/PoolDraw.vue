<template>
  <div class="row">
    <!-- Players in group (simple list) -->
    <div class="col-sm-3 p-3 p-sm-2">
      <h5 class="black text-start fw-bolder">{{ $t("draw.pool.players") }}</h5>
      <p v-for="(playerInGroup, index) in group.players" class="text-start" :key="index">
        {{ index + 1 + ". " + getName(playerInGroup.playerDTOs) }} {{ getClub(playerInGroup.playerDTOs) }}
      </p>
    </div>
    <!-- Current standing in group -->
    <div class="col-sm-9">
      <h5 class="black text-center fw-bolder">{{ $t("draw.pool.standing") }}</h5>
      <table class="table table-bordered">
        <thead>
        <tr>
          <th scope="col"></th>
          <th scope="col">{{ $t("draw.pool.points") }}</th>
          <th scope="col">{{ $t("draw.pool.won") }}</th>
          <th scope="col">{{ $t("draw.pool.matches") }}</th>
          <th scope="col">{{ $t("draw.pool.games") }}</th>
          <th scope="col">{{ $t("draw.pool.points") }}</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(playerInGroup, index) in group.players" :key="index">
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  name: "PoolDraw",
  props: {
    group: Object
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
    }
  }
}
</script>

<style scoped>

</style>