<template>
  <table class="table table-bordered">
    <thead>
    <tr>
      <th scope="col">{{$t("draw.pool.club")}}</th>
      <th scope="col"></th>
      <th scope="col">1</th>
      <th scope="col">2</th>
      <th scope="col">3</th>
      <th scope="col" v-if="group.nrPlayers >= 4">4</th>
      <th scope="col" v-if="group.nrPlayers >= 5">5</th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="(playerInGroup, index) in group.poolDraw" :key="index">
      <td> {{getClub(playerInGroup.playerDTOs)}}</td>
      <td>{{getName(playerInGroup.playerDTOs)}}</td>
      <td></td>
      <td></td>
      <td></td>
      <td v-if="group.nrPlayers >= 4"></td>
      <td v-if="group.nrPlayers >= 5"></td>
    </tr>
    </tbody>
  </table>
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
      }
      else if (playerDTOs.length === 2) {
        return playerDTOs[0].club.name + "/" + playerDTOs[1].club.name
      }
    },
    getName(playerDTOs) {
      if (playerDTOs.length === 1) {
        return playerDTOs[0].firstName + " " + playerDTOs[0].lastName
      }
      else if (playerDTOs.length === 2) {
        return playerDTOs[0].firstName + " " + playerDTOs[0].lastName + "/" +
            playerDTOs[1].firstName + " " + playerDTOs[1].lastName
      }
    }
  }
}
</script>

<style scoped>

</style>