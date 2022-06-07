<template>
  <main>
    <h1 class="p-4 fs-3 fs-md-1">
      <i @click="$router.push('/draw')" class="fas fa-arrow-left" style="float: left"></i>
      {{ $t("schedule.main.heading") }}
      <i @click="$router.push('/results')" class="fas fa-arrow-right" style="float: right"></i>
    </h1>
    <div>
      <div class="row">

        <!-- Sidebar -->
        <div class="sidebar col-md-3 col-lg-2">
          <div class="sidebar-header">
            <h4> {{ $t("schedule.sidebar.alternatives") }}</h4>
          </div>
          <ul class="list-group list-group-flush">
            <li class="list-group-item active"> {{ $t("schedule.sidebar.overview") }}
            </li>
            <li class="list-group-item"
                @click="reRoute"> {{ $t("schedule.sidebar.advanced") }}
            </li>
          </ul>
        </div>

        <!-- Main content -->
        <div id="main" class="col-md-9 col-lg-10 ps-0">
          <!-- General information about competition -->
          <div class="row p-3 m-md-2 custom-card">
            <div>
              <div>
                <h3 class="p-3">{{ $t("schedule.generalInfo.heading") }}</h3>
                <div class="d-flex justify-content-end">
                  <button type="button" class="btn btn-primary m-1" @click="saveChanges">{{ $t("general.saveChanges") }}
                  </button>
                </div>
              </div>
              <!-- Daily start end -->
              <div class="col-sm-8 m-auto">
                <div>
                  <h4>{{ $t("schedule.generalInfo.startEnd") }}</h4>
                  <p>{{ $t("schedule.generalInfo.startEndHelper") }}
                    <span> <router-link to="/overview">{{
                        $t("schedule.generalInfo.startEndHelperHere")
                      }}</router-link> </span>
                  </p>
                </div>
                <div class="table-container">
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
                        <vue-timepicker v-model="day.startTime"></vue-timepicker>
                      </td>
                      <td>
                        <vue-timepicker v-model="day.endTime"></vue-timepicker>
                      </td>
                    </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <!-- Available tables -->
              <div class="other-settings col-sm-8 m-auto">
                <div>
                  <h5> {{ $t("schedule.generalInfo.availableTablesHeading") }}</h5>
                  <p>{{ $t("schedule.generalInfo.availableTablesHelper") }}
                    <span> <router-link to="/schedule-advanced">{{
                        $t("schedule.generalInfo.availableTablesHelperHere")
                      }}</router-link> </span>
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
                        <p v-if="tableDay.nrTables === -1">{{ $t("schedule.generalInfo.cannotChangeTables") }}</p>
                        <select v-if="tableDay.nrTables !== -1" id="table-selection" class="form-control"
                                v-model="tableDay.nrTables">
                          <option value="0"> {{ $t("schedule.generalInfo.availableTablesNotSet") }}</option>
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
                  <h5>{{ $t("schedule.generalInfo.averageMatchTimeHeading") }}</h5>
                  <p>{{ $t("schedule.generalInfo.averageMatchTimeHelper") }}</p>
                  <select id="match-length-selection" class="form-control" v-model="scheduleMetadata.minutesPerMatch">
                    <option v-for="i in minutesPerMatchOptions" :key="i" :value="i">
                      {{ i + " " + $t("schedule.generalInfo.minutes") }}
                    </option>
                  </select>
                  <br>
                  <br>
                </div>
              </div>
            </div>
          </div>

          <!-- Choose date and time for individual categories -->
          <div id="categories" class="row custom-card m-md-2">
            <div>
              <h3 class="p-4">{{ $t("schedule.main.categoryStartTimes") }}</h3>
              <p class="w-75 mx-auto p-2"> {{ $t("schedule.main.helperText") }}</p>
            </div>
            <div id="table-container" class="col-sm-12 mx-auto my-4">
              <table class="table table-bordered">
                <thead>
                <tr>
                  <th scope="col">{{ $t("schedule.main.category") }}</th>
                  <th scope="col">{{ $t("schedule.main.stage") }}</th>
                  <th scope="col">{{ $t("schedule.main.day") }}</th>
                  <th scope="col">{{ $t("schedule.main.timeSpan") }}</th>
                </tr>
                </thead>
                <tbody>
                <template v-for="category in categoryStartTimeDTO.categoryStartTimeList">
                  <tr :key="category.id" v-if="category.categoryDTO.settings.drawType === 'POOL_AND_CUP' ||
                         category.categoryDTO.settings.drawType === 'POOL_ONLY'">
                    <td>{{ category.categoryDTO.category.name }}</td>
                    <!-- Select stage (group or playoff) -->
                    <td>
                      <input class="form-control" disabled :value="$t('schedule.main.GROUP')">
                    </td>
                    <!-- Select date -->
                    <td>
                      <select id="date-selection" class="form-control"
                              v-model="categoryMatchSchedulerSpecifications[category.id]['GROUP']['day']">
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
                              v-model="categoryMatchSchedulerSpecifications[category.id]['GROUP']['interval']">
                        <option v-for="(interval, counter) in categoryStartTimeDTO.startTimeFormOptions.startIntervals"
                                v-bind:key="counter" :value="interval"
                        >
                          {{ getInterval(interval) }}
                        </option>
                      </select>
                    </td>
                  </tr>
                  <tr :key="category.id + 1000" v-if="category.categoryDTO.settings.drawType === 'POOL_AND_CUP' ||
                   category.categoryDTO.settings.drawType === 'CUP_ONLY'">
                    <td v-if="category.categoryDTO.settings.drawType !== 'CUP_ONLY'"></td>
                    <td v-if="category.categoryDTO.settings.drawType === 'CUP_ONLY'">
                      {{category.categoryDTO.category.name }}
                    </td>
                      <!-- Stage (group or playoff) -->
                    <td>
                      <input class="form-control" :value="$t('schedule.main.PLAYOFF')" disabled>
                    </td>
                    <!-- Select date -->
                    <td>
                      <select id="date-selection2" class="form-control"
                              v-model="categoryMatchSchedulerSpecifications[category.id]['PLAYOFF']['day']">
                        <option value="null"> {{ $t("schedule.main.notSelected") }}</option>
                        <option v-for="(day, counter) in categoryStartTimeDTO.startTimeFormOptions.availableDays"
                                v-bind:key="counter" :value="getPlayingDate(day)">
                          {{ getPlayingDate(day) }}
                        </option>
                      </select>
                    </td>
                    <!-- Select interval during day -->
                    <td>
                      <select id="interval-selection2" class="form-control"
                              v-model="categoryMatchSchedulerSpecifications[category.id]['PLAYOFF']['interval']">
                        <option v-for="(interval, counter) in categoryStartTimeDTO.startTimeFormOptions.startIntervals"
                                v-bind:key="counter" :value="interval"
                        >
                          {{ getInterval(interval) }}
                        </option>
                      </select>
                    </td>
                    <td>
                      <label for="tableNumbers" class="form-label">Example range</label>
                      <input type="range" class="form-range" min="0" max="5" id="tableNumbers">
                    </td>
                  </tr>
                </template>
                </tbody>
              </table>
            </div>
          </div>

          <!-- See generated schedule -->
          <div class="table-container col-sm-12 mx-auto my-4" v-if="generatedSchedule != null">
            <table class="table table-bordered">
              <thead>
              <tr>
                <th></th>
                <th :colspan="generatedSchedule.scheduleItemList.length">{{ $t("schedule.main.table") }}</th>
              </tr>
              <tr>
                <th>Tid</th>
                <th v-for="item in generatedSchedule.scheduleItemList" :key="item.tableNumber"> {{ item.tableNumber }}
                </th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="time in generatedSchedule.validStartTimes" :key="time">
                <td>{{ getHoursMinutes(new Date(time)) }}</td>
                <td v-for="item in generatedSchedule.scheduleItemList"
                    :style="{backgroundColor: colorCategoryMap[getCategoryAtTableTime(item.tableNumber, time)]}"
                    :key="item.tableNumber" style="font-size: 80%"> {{ getCategoryAtTableTime(item.tableNumber, time) }}
                </td>
              </tr>
              </tbody>
            </table>
          </div>
          <div class="row">
            <div class="col-1">

            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import {getFormattedDate, getHoursMinutes} from "@/common/util";
import VueTimepicker from 'vue2-timepicker/src/vue-timepicker.vue'
import CategoryStartTimeService from "@/common/api-services/schedule/category-start-time.service";
import DailyStartEndService from "@/common/api-services/schedule/daily-start-end.service";
import AvailableTablesService from "@/common/api-services/schedule/available-tables.service";
import ScheduleMetadataService from "@/common/api-services/schedule/schedule-metadata.service";
import CategoryService from "@/common/api-services/category.service";
import ScheduleGeneralService from "@/common/api-services/schedule/schedule-general.service";

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
      minutesPerMatchOptions: [15, 20, 25, 30, 35, 40, 45, 50, 55, 60],
      generatedSchedule: null,
      distinctPlannedCategories: [],
      categoryMatchSchedulerSpecifications: {},
      colors: ["#46b1c9", "#84c0c6", "#9fb7b9", "#bcc1ba", "#f2e2d2", "#dcd6f7", "#d8d2e1", "#c5d5e4",
        "#a6b1e1", "#cacfd6", "#d6e5e3", "#ce7da5", "#bee5bf", "#dff3e3", "#ffd1ba",
        "#b5ffe1", "#93e5ab", "#65b891", "#47682c", "#8c7051", "#ef3054",
        "#d3d57c", "#c7aa74", "#957964", "#0081a7", "#00afb9", "#fdfcdc", "#fed9b7", "#f07167"],
      colorCategoryMap: {},
      stages: ["GROUP", "PLAYOFF"]
    }
  },
  computed: {
    competition: function () {
      return this.$store.getters.competition
    }
  },
  mounted() {
    CategoryService.getCompetitionCategories(this.competition.id).then(res => {
      this.competitionCategories = res.data.categories
    })
    this.getCategoryStartTimes()
    this.getDailyStartEnd()
    this.getAvailableTablesForCompetition()
    this.getScheduleMetadata()
    this.getGeneratedSchedule()
  },
  methods: {
    reRoute() {
      this.$router.push("schedule-advanced")
    },
    getHoursMinutes: getHoursMinutes,
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
    getGeneratedSchedule() {
      ScheduleGeneralService.getExcelSchedule(this.competition.id).then(res => {
        this.generatedSchedule = res.data
        console.log("Fetch schedule")
        for (let i = 0; i < this.generatedSchedule.distinctCategories.length; i++) {
          const category = this.generatedSchedule.distinctCategories[i]
          this.colorCategoryMap[category.name] = this.colors[i]
        }
      }).catch(err => {
        console.log("Couldn't fetch generated schedule", err)
      })
    },
    getInterval(interval) {
      return this.$t("schedule.main.intervals." + interval)
    },
    getTime(time) {
      if (time === null || time === undefined || time.HH === "" || time.mm === "") {
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
        this.setUpMatchScheduleSpecs()
      })
    },
    formattedDate: getFormattedDate,
    formatTime: getHoursMinutes,
    saveChanges() {
      this.saveStartTimes()
      this.saveDailyStartEndTimes()
      this.saveAvailableTables()
      this.updateMinutesPerMatch()
    },
    saveStartTime(category) {
      const exactStartTime = this.getTime(category.exactStartTime)
      const objectToSave = {
        playingDay: category.playingDay === "null" ? null : category.playingDay,
        startInterval: category.startInterval,
        exactStartTime: exactStartTime,
      }
      CategoryStartTimeService.updateCategoryStartTime(category.id,
          this.competition.id, category.categoryDTO.categoryId, objectToSave).then(() => {
      })
    },
    saveStartTimes() {
      this.categoryStartTimeDTO.categoryStartTimeList.forEach(category => {
        this.saveStartTime(category)
      })
    },
    saveDailyStartEndTimes() {
      this.dailyStartEndDTO.dailyStartEndList.forEach(object => this.setDailyStartEnd(object))
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
    saveAvailableTables() {
      this.availableTables.forEach(tableDay => {
        AvailableTablesService.updateTablesForDay(this.competition.id, tableDay)
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
    },
    // Returns the category playing at a given table at a given time or empty string if no match is planned at that time
    getCategoryAtTableTime(table, time) {
      if (this.generatedSchedule == null) {
        return ""
      }

      let stringToReturn = ""
      this.generatedSchedule.scheduleItemList.forEach(item => {
        if (item.tableNumber === table) {
          item.matchesAtTable.forEach(match => {
            const matchTime = new Date(match.startTime)
            const timeWeAreLookingFor = new Date(time)
            if (matchTime.getTime() === timeWeAreLookingFor.getTime()) {
              stringToReturn = this.createCategoryMatchString(match)
            }
          })
        }
      })
      return stringToReturn
    },
    // Returns e.g. "Herrar 1 (Group A)"
    createCategoryMatchString(categoryMatch) {
      return categoryMatch.category.name // " (" + categoryMatch.groupOrRound + ")"
    },
    setUpMatchScheduleSpecs() {
      // Create specification for the these categories are used to generate schedule
      this.categoryStartTimeDTO.categoryStartTimeList.forEach(category => {
        const id = category.id
        if (category.categoryDTO.settings.drawType === "POOL_AND_CUP") {
          this.categoryMatchSchedulerSpecifications[id] =
              {
                "GROUP": {
                  "matchType": "GROUP",
                  "tableNumbers": [],
                  "day": "",
                  "interval": ""
                },
                "PLAYOFF": {
                  "matchType": "PLAYOFF",
                  "tableNumbers": [],
                  "day": "",
                  "interval": ""
                }
              }
        } else if (category.categoryDTO.settings.drawType === "POOL_ONLY") {
          this.categoryMatchSchedulerSpecifications[id] = {
            "GROUP": {
              "matchType": "GROUP",
              "tableNumbers": [],
              "day": "",
              "interval": ""
            },
          }
        } else if (category.categoryDTO.settings.drawType === "CUP_ONLY") {
          this.categoryMatchSchedulerSpecifications[id] = {
            "PLAYOFF": {
              "matchType": "PLAYOFF",
              "tableNumbers": [],
              "day": "",
              "interval": ""
            },
          }
        }
      })
    }
  }
}
</script>

<style scoped>

h1 {
  background-color: var(--clr-primary-100);
  margin-bottom: 0;
}

#categories h4, p {
  text-align: left;
}

h5 {
  text-align: left;
}

/* General info section */
#general-info a {
  display: inline;
}

</style>