<template>
  <div>
    <div class="modal__content">
      <i class="modal__close fas fa-times clickable" @click="$emit('close')"></i>
      <!--
      <div class="col-sm-9">
        <h5 class="black text-center fw-bolder">{{ $t("draw.pool.standing") }}</h5>
        <table class="table table-bordered table-responsive">
          <thead>
          <tr>
            <th scope="col"></th>
            <th scope="col">{{ $t("draw.pool.nrMatches") }}</th>
            <th scope="col">{{ $t("draw.pool.matches") }}</th>
            <th scope="col">{{ $t("draw.pool.games") }}</th>
            <th scope="col">{{ $t("draw.pool.points") }}</th>
            <th scope="col">{{ $t("draw.pool.groupPoints") }}</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(standing, index) in groupStandingList" :key="index">
            <td class="text-start ps-3">{{ index + 1 + ". " + getName(standing.player) }} {{
                getClub(standing.player)
              }}
            </td>
            <td>{{ standing.matchesPlayed }}</td>
            <td>{{ standing.matchesWonLost.won }} - {{ standing.matchesWonLost.lost }}</td>
            <td>{{ standing.gamesWonLost.won }} - {{ standing.gamesWonLost.lost }}</td>
            <td>{{ standing.pointsWonLost.won }} - {{ standing.pointsWonLost.lost }}</td>
            <td>{{ standing.groupScore }}</td>
          </tr>
          </tbody>
        </table>
      </div>-->
      <div class="col-sm-11">
        <h3> {{ $t("draw.pool.subgroupStanding.heading") }}</h3>
        <p class="fs-6 text-black-50">{{ $t("draw.pool.subgroupStanding.introText") }}</p>
        <div v-for="(standingList, key) in subGroupList" :key="key" class="table-responsive">
          <table class="table table-bordered table-responsive">
            <thead>
            <tr>
              <th scope="col">{{ $t("draw.pool.place")}}</th>
              <th scope="col">{{ $t("draw.pool.subgroupStanding.matchesInSubGroup") }}</th>
              <th scope="col">{{ $t("draw.pool.matches") }}</th>
              <th scope="col">{{ $t("draw.pool.subgroupStanding.GAME_QUOTIENT") }}</th>
              <th scope="col">{{ $t("draw.pool.subgroupStanding.POINT_QUOTIENT") }}</th>
              <th scope="col">{{ $t("draw.pool.groupPoints") }}</th>
              <th scope="col">{{ $t("draw.pool.subgroupStanding.reasonForPosition") }}</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(standing, index) in standingList.groupStandingList" :key="index">
              <td class="text-start ps-3">{{ standing.groupPosition + ". " + getName(standing.player) }} {{
                  getClub(standing.player)
                }}
              </td>
              <td>{{ standing.subgroupStanding.matchesPlayed }}</td>
              <td>{{ standing.subgroupStanding.matchesWonLost.won }} - {{ standing.subgroupStanding.matchesWonLost.lost }}</td>
              <td>{{ standing.subgroupStanding.gamesWonLost.won }}/{{ standing.subgroupStanding.gamesWonLost.lost }}</td>
              <td>{{ standing.subgroupStanding.pointsWonLost.won }}/{{ standing.subgroupStanding.pointsWonLost.lost }}</td>
              <td>{{ standing.subgroupStanding.groupScore }}</td>
              <td>{{ $t("draw.pool.subgroupStanding." + standing.sortedBy) }}</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <div class="modal-footer p-2">
      <button type="button" class="btn btn-secondary" @click="$emit('close')">
        {{ $t("general.close") }}
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: "PoolSubgroup",
  props: ["subGroupList"],
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
  }
}
</script>

<style scoped>
tr td {
  min-width: 150px;
}
</style>