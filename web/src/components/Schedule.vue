<template>
  <div>
    <div class="container-fluid">
      <div class="row gx-5">

        <!-- Sidebar -->
        <div class="sidebar col-md-3">
          <br>
          <ul class="list-group list-group-flush">
            <li class="list-group-item"
                :class="sidebarChoice === 'overview' ? 'active' : ''"
                @click="sidebarChoice = 'overview'"> {{ $t("schedule.sidebar.overview") }}
            </li>
            <li class="list-group-item"
                :class="sidebarChoice === 'advanced' ? 'active' : ''"
                @click="sidebarChoice = 'advanced'"> {{ $t("schedule.sidebar.advanced") }}
            </li>
          </ul>
        </div>

        <!-- Main content -->
        <div id="main" class="col-md-9">
          <h1> {{ $t("schedule.main.heading") }}</h1>
          <div v-if="sidebarChoice === 'overview'">
            <div id="categories" class="row">
              <h2>{{ $t("schedule.main.categoryStartTimes") }}</h2>
              <p> {{$t("schedule.main.helperText")}}</p>
              <div id="table-container" class="col-sm-12 m-auto">
                <table class="table table-bordered">
                  <thead>
                  <tr>
                    <th scope="col">{{ $t("schedule.main.category") }}</th>
                    <th scope="col">{{ $t("schedule.main.day") }}</th>
                    <th scope="col">{{ $t("schedule.main.timeSpan") }}</th>
                    <th scope="col">{{ $t("schedule.main.startTime") }}</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="category in categoryStartTimeDTO.categoryStartTimeList" :key="category.id">
                    <td>{{ category.categoryDTO.categoryName }}</td>
                    <!-- Select date -->
                    <td>
                      <select id="date-selection" class="form-control"
                              v-on:change="setStartTime(category)" v-model="category.playingDay">
                        <option value="null"> {{ $t("schedule.main.notSelected") }}</option>
                        <option v-for="(day, counter) in categoryStartTimeDTO.startTimeFormOptions.availableDays"
                                v-bind:key="counter" :value="getPlayingDate(day)">
                          {{ getPlayingDate(day) }}
                        </option>
                      </select>
                    </td>
                    <!-- Select interval during day -->
                    <td>
                      <select id="interval-selection" class="form-control"
                              v-on:change="setStartTime(category)" v-model="category.startInterval">
                        <option v-for="(interval, counter) in categoryStartTimeDTO.startTimeFormOptions.startIntervals"
                                v-bind:key="counter" :value="interval" :disabled="category.playingDay === null || category.playingDay === 'null'">
                          {{ getInterval(interval) }}
                        </option>
                      </select>
                    </td>
                    <!-- Select exact start time -->
                    <td>
                      <vue-timepicker v-model="category.exactStartTime"
                                      v-on:change="setStartTime(category)" :disabled="category.startInterval === 'NOT_SELECTED'"></vue-timepicker>
                    </td>
                  </tr>
                  </tbody>
                </table>
              </div>
            </div>
            <!-- General information about competition -->
            <div id="general-info" class="row">
              <br>
              <!-- Daily start end -->
              <h2>{{ $t("schedule.generalInfo.heading") }}</h2>
              <div>
                <div>
                <h4>{{ $t("schedule.generalInfo.startEnd") }}</h4>
                <p>{{ $t("schedule.generalInfo.startEndHelper") }}
                  <span> <router-link to="/overview">{{ $t("schedule.generalInfo.startEndHelperHere") }}</router-link> </span>
                </p>
                </div>
                <div class="table-container col-sm-7 m-auto">
                  <table class="table table-bordered">
                    <thead>
                    <tr>
                      <th scope="col"> {{ $t("schedule.main.day") }}</th>
                      <th scope="col"> {{ $t("schedule.generalInfo.startTime") }}</th>
                      <th scope="col"> {{ $t("schedule.generalInfo.endTime") }}</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="(day, counter) in dailyStartEndDTO.dailyStartEndList" :key="counter">
                      <td>
                        {{ day.day }}
                      </td>
                      <td>
                        <vue-timepicker v-model="day.startTime"
                                        v-on:change="setDailyStartEnd(day)"></vue-timepicker>
                      </td>
                      <td>
                        <vue-timepicker v-model="day.endTime"
                                        v-on:change="setDailyStartEnd(day)"></vue-timepicker>
                      </td>
                      <td>

                      </td>
                    </tr>
                    </tbody>
                  </table>
                </div>
                <!-- Available tables -->
                <div class="other-settings col-sm-7 m-auto">
                  <div>
                    <h5> {{$t("schedule.generalInfo.availableTablesHeading")}}</h5>
                    <p>{{ $t("schedule.generalInfo.availableTablesHelper") }}
                      <span> <router-link to="/schedule-advanced">{{ $t("schedule.generalInfo.availableTablesHelperHere") }}</router-link> </span>
                    </p>
                    <table class="table table-bordered">
                      <thead>
                      <tr>
                        <th scope="col"> {{ $t("schedule.main.day") }}</th>
                        <th scope="col"> {{ $t("schedule.generalInfo.nrTables") }}</th>
                      </tr>
                      </thead>
                      <tbody>
                      <tr v-for="(tableDay, counter) in availableTables" :key="counter">
                        <td>
                          {{ tableDay.day }}
                        </td>
                        <td>
                          <select id="table-selection" class="form-control" v-model="tableDay.nrTables"
                                  v-on:change="setAvailableTables(tableDay.day)">
                            <option v-for="i in 100" :key="i" :value="i">
                              {{ i }}
                            </option>
                          </select>
                        </td>
                      </tr>
                      </tbody>
                    </table>
                  </div>
                  <!-- Average time per match -->
                  <div>
                    <h5>{{$t("schedule.generalInfo.averageMatchTimeHeading")}}</h5>
                    <p>{{$t("schedule.generalInfo.averageMatchTimeHelper")}}</p>
                    <select id="match-length-selection" class="form-control" v-model="scheduleMetadata.minutesPerMatch"
                            v-on:change="updateMinutesPerMatch">
                      <option v-for="i in minutesPerMatchOptions" :key="i" :value="i">
                        {{ i + " " + $t("schedule.generalInfo.minutes")}}
                      </option>
                    </select>
                    <br>
                    <br>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {getFormattedDate, getHoursMinutes} from "@/common/util";
import VueTimepicker from 'vue2-timepicker/src/vue-timepicker.vue'
import DrawService from "@/common/api-services/draw.service";
import CategoryStartTimeService from "@/common/api-services/schedule/category-start-time.service";
import DailyStartEndService from "@/common/api-services/schedule/daily-start-end.service";
import AvailableTablesService from "@/common/api-services/schedule/available-tables.service";
import ScheduleMetadataService from "@/common/api-services/schedule/schedule-metadata.service";

export default {
  name: "Schedule",
  components: {VueTimepicker},
  data() {
    return {
      sidebarChoice: "overview",
      selectedCategory: "",
      competitionCategories: [],
      categoryStartTimeDTO: {},
      dailyStartEndDTO: {},
      availableTables: null,
      scheduleMetadata: {},
      minutesPerMatchOptions:  [15, 20, 25, 30, 35, 40, 45, 50, 55, 60]
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  mounted() {
    DrawService.getCompetitionCategories(this.competition.id).then(res => {
      this.competitionCategories = res.data.categories
    })
    this.getCategoryStartTimes()
    this.getDailyStartEnd()
    this.getAvailableTablesForCompetition()
    this.getScheduleMetadata()
  },
  methods: {
    getPlayingDate(date) {
      if (date === null) {
        return this.$t("schedule.main.notSelected")
      } else {
        return this.formattedDate(date)
      }
    },
    getDailyStartEnd() {
      DailyStartEndService.getDailyStartEndForCompetition(this.competition.id).then(
          res => {
            this.dailyStartEndDTO = res.data
          }
      ).catch(err => {
        console.log("Couldn't fetch daily start end, ", err)
      })
    },
    getInterval(interval) {
      return this.$t("schedule.main.intervals." + interval)
    },
    getTime(time) {
      if (time === null || time.HH === "" || time.mm === "") {
        return null
      } else if (typeof time === "object") {
        return `${time.HH}:${time.mm}`
      } else {
        return time
      }
    },
    getCategoryStartTimes() {
      // Category id is not used in backend for this call so can be set to anything
      CategoryStartTimeService.getCategoryStartTimesInCompetition(this.competition.id,
          5).then(res => {
        this.categoryStartTimeDTO = res.data
      })
    },
    formattedDate: getFormattedDate,
    formatTime: getHoursMinutes,
    setStartTime(updatedStartTime) {
      const exactStartTime = this.getTime(updatedStartTime.exactStartTime)
      const objectToSave = {
        playingDay: updatedStartTime.playingDay === "null" ? null : updatedStartTime.playingDay,
        startInterval: updatedStartTime.startInterval,
        exactStartTime: exactStartTime,
      }
      CategoryStartTimeService.updateCategoryStartTime(updatedStartTime.id, this.competition.id, updatedStartTime.categoryDTO.categoryId, objectToSave)
    },
    setDailyStartEnd(dailyStartEndObject) {
      const startTime = this.getTime(dailyStartEndObject.startTime)
      const endTime = this.getTime(dailyStartEndObject.endTime)
        const objectToSave = {
          day: dailyStartEndObject.day,
          startTime: startTime,
          endTime: endTime
      }
      DailyStartEndService.updateDailyStartEnd(dailyStartEndObject.id, this.competition.id, objectToSave)
    },
    /*
    * Available tables
     */
    setAvailableTables(day) {
      this.availableTables.forEach(tableDay => {
        if (tableDay.day === day) {
          AvailableTablesService.updateTablesForDay(this.competition.id, tableDay)
        }
      })
    },
    getAvailableTablesForCompetition() {
      AvailableTablesService.getAvailableTablesForCompetition(this.competition.id).then(
          res => {
            this.availableTables = res.data
          }
      )
    },
    /**
     * Minutes per match
     */
    getScheduleMetadata() {
      ScheduleMetadataService.getScheduleMetadata(this.competition.id).then(res => {
        this.scheduleMetadata = res.data
      })
    },
    updateMinutesPerMatch() {
      ScheduleMetadataService.updateMinutesPerMatch(this.competition.id, {minutesPerMatch: this.scheduleMetadata.minutesPerMatch})
    }
  }
}
</script>

<style scoped>

#main {
  margin-top: 20px;
}

#main h1 {
  text-align: left;
  margin-bottom: 30px;
}

#main h4 p {
  margin-bottom: 30px;
}

/* General info section */
#general-info {
  margin-top: 40px;
  background-color: var(--sidebar-color);
}

#general-info a {
  display: inline;
}

#general-info h5 {
  margin-top: 30px;
}
</style>