<template>
  <main>
    <h1 class="p-4">{{ $t("schedule.main.heading") }}</h1>
    <div class="container-fluid">
      <div class="row gx-5">

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
          <div id="available-tables" class="p-4">
            <h4 class="text-start">{{ $t("schedule.advanced.availableTables.heading") }}</h4>
            <p class="text-start">{{ $t("schedule.advanced.availableTables.infoText") }}</p>
            <p></p>
            <div class="mt-4">
              <h5 class="text-start">{{ $t("schedule.advanced.availableTables.daySelection") }}</h5>
              <div class="d-flex mb-4">
                <p class="d-inline-flex mx-3 days" v-for="day in competitionDays"
                   :class="selectedDay === day ? 'active' : 'none' " :key="day" @click="setDay(day)">
                  {{ day }}</p>
              </div>
            </div>
            <div class="row">
              <div class="col-md-10 mx-auto">

              <table class="table table-bordered">
                <thead>
                <tr>
                  <th>
                    {{ $t("schedule.advanced.availableTables.hour") }}
                  </th>
                  <th>
                    {{ $t("schedule.advanced.availableTables.nrTables") }}
                  </th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="availableTable in availableTables" :key="availableTable.id">
                  <td>
                    {{ availableTable.hour }}
                  </td>
                  <td>
                    <select id="table-selection" class="form-control" v-model="availableTable.nrTables"
                            v-on:change="setAvailableTables(availableTable)">
                      <option v-for="i in 100" :key="i" :value="i">
                        {{ i }}
                      </option>
                    </select>
                  </td>
                </tr>
                </tbody>
              </table>
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
import CompetitionService from "@/common/api-services/competition.service";
import AvailableTablesService from "@/common/api-services/schedule/available-tables.service";

export default {
  name: "ScheduleAdvanced",
  data() {
    return {
      scheduleMetadata: {},
      groupPauseOptions: [0, 25, 30, 35, 40, 45, 50, 55, 60, 90, 120, 180],
      playoffPauseOptions: [0, 25, 50, 75, 100, 125, 150],
      groupPlayoffPauseOptions: [0, 25, 50, 75, 100, 125, 150, 175],
      competitionDays: [],
      selectedDay: {},
      availableTables: []
    }
  },
  mounted() {
    this.getScheduleMetadata()
    CompetitionService.getDaysInCompetition(this.competition.id).then(res => {
      this.competitionDays = res.data.competitionDays
      this.selectedDay = this.competitionDays[0]
      this.getAvailableTables()
    })
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
    getAvailableTables() {
      AvailableTablesService.getAvailableTablesForDay(this.competition.id, this.selectedDay).then(res => {
        this.availableTables = res.data
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
    setAvailableTables(availableTable) {
      const objectToSave = {
        nrTables: availableTable.nrTables,
        day: availableTable.day,
        hour: availableTable.hour
      }
      AvailableTablesService.updateAvailableTable(availableTable.id, this.competition.id, objectToSave)
    },
    setDay(day) {
      this.selectedDay = day
      this.getAvailableTables()
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