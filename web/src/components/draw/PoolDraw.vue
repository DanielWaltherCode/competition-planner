<template>
  <table class="table table-bordered">
    <thead>
    <tr>
      <th scope="col">{{$t("draw.pool.club")}}</th>
      <th scope="col"></th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="(playerInGroup, index) in group.poolDraw" :key="index">
      <td> {{getClub(playerInGroup.playerDTOs)}}</td>
      <td>{{getName(playerInGroup.playerDTOs)}}</td>

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