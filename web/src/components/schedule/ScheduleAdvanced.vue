<template>
  <main>
    <h1 class="p-4">{{ $t("schedule.main.heading") }}</h1>
    <div class="container-fluid">
      <div class="row">

        <!-- Sidebar -->
        <div class="sidebar col-md-3">
          <br>
          <ul class="list-group list-group-flush">
            <li class="list-group-item"
                @click="reRoute"> {{ $t("schedule.sidebar.overview") }}
            </li>
            <li class="list-group-item active"> {{ $t("schedule.sidebar.advanced") }}
            </li>
          </ul>
        </div>
        <div id="main" class="col-md-9 mx-auto px-0">
          <!-- Handle pauses -->
          <div id="pauses" class="blue-section p-4">
            <h3 class="p-4"> {{ $t("schedule.advanced.heading") }}</h3>
            <h4>{{ $t("schedule.advanced.pauseHeading") }}</h4>
            <p>{{ $t("schedule.advanced.pauseHelper") }}</p>

            <div class="row d-flex align-items-end">
              <div class="col-md-4">

                <label for="group-pause" class="form-label">{{ $t("schedule.advanced.pauseGroup") }}</label>
                <select id="group-pause" class="form-control"
                        v-on:change="setPause" v-model="scheduleMetadata.pauseBetweenGroupMatches">
                  <option v-for="i in groupPauseOptions" :key="i" :value="i">
                    {{ i }} {{ $t("schedule.generalInfo.minutes") }}
                  </option>
                </select>
              </div>

              <div class="col-md-4">
                <label for="playoff-pause" class="form-label">{{ $t("schedule.advanced.pausePlayoff") }}</label>
                <select id="playoff-pause" class="form-control"
                        v-on:change="setPause" v-model="scheduleMetadata.pauseBetweenPlayoffMatches">
                  <option v-for="i in playoffPauseOptions" :key="i" :value="i">
                    {{ i }}
                  </option>
                </select>
              </div>
              <div class="col-md-4">

                <label for="group-playoff-pause" class="form-label">
                  {{ $t("schedule.advanced.pauseBetweenGroupAndPlayoff") }}
                </label>
                <select id="group-playoff-pause" class="form-control"
                        v-on:change="setPause" v-model="scheduleMetadata.pauseAfterGroupStage">
                  <option v-for="i in groupPlayoffPauseOptions" :key="i" :value="i">
                    {{ i }}
                  </option>
                </select>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import ScheduleMetadataService from "@/common/api-services/schedule/schedule-metadata.service";

export default {
  name: "ScheduleAdvanced",
  data() {
    return {
      scheduleMetadata: {},
      groupPauseOptions: [0, 25, 30, 35, 40, 45, 50, 55, 60, 90, 120, 180],
      playoffPauseOptions: [0, 25, 50, 75, 100, 125, 150],
      groupPlayoffPauseOptions: [0, 25, 50, 75, 100, 125, 150, 175],
    }
  },
  mounted() {
    this.getScheduleMetadata()
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  methods: {
    getScheduleMetadata() {
      ScheduleMetadataService.getScheduleMetadata(this.competition.id).then(res => {
        this.scheduleMetadata = res.data
      })
    },
    setPause() {
      const objectToSave = {
        minutesPerMatch: this.scheduleMetadata.minutesPerMatch,
        pauseAfterGroupStage: this.scheduleMetadata.pauseAfterGroupStage,
        pauseBetweenGroupMatches: this.scheduleMetadata.pauseBetweenGroupMatches,
        pauseBetweenPlayoffMatches: this.scheduleMetadata.pauseBetweenPlayoffMatches,
      }

      ScheduleMetadataService.updateScheduleMetadata(
          this.competition.id,
          this.scheduleMetadata.id,
          objectToSave)
    },
    reRoute() {
      this.$router.push("schedule")
    },
  }
}
</script>

<style scoped>

h1 {
  background-color: var(--clr-primary-100);
  margin-bottom: 0;
}

#pauses {
  text-align: left;
}

#pauses p {
  margin-top: 10px;
  margin-bottom: 15px;
}

#pauses .form-label {
  margin-top: 20px;
}

/*
* Available tables
 */

.days:hover {
  cursor: pointer;
  opacity: 0.7;
}

p.active {
  color: black;
  text-decoration: underline;
}
</style>