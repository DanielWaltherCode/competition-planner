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
              </div>
              <!-- Daily start end -->
              <div class="col-sm-10 m-auto custom-card p-5">
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
                  <div>
                    <button type="button" class="btn btn-primary" @click="saveDailyStartEndTimes">
                      {{ $t("general.saveChanges") }}
                    </button>
                  </div>
                </div>
              </div>

              <!-- Available tables -->
              <div class="other-settings p-5 col-md-9 col-lg-10  mx-auto custom-card">
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
                  <div>
                    <button type="button" class="btn btn-primary" @click="saveAvailableTables">
                      {{ $t("general.saveChanges") }}
                    </button>
                  </div>
                </div>
              </div>
              <!-- Average time per match -->
              <div class="p-5 col-sm-10 m-auto custom-card">
                <h5>{{ $t("schedule.generalInfo.averageMatchTimeHeading") }}</h5>
                <p>{{ $t("schedule.generalInfo.averageMatchTimeHelper") }}</p>
                <select id="match-length-selection" class="form-control" v-model="scheduleMetadata.minutesPerMatch">
                  <option v-for="i in minutesPerMatchOptions" :key="i" :value="i">
                    {{ i + " " + $t("schedule.generalInfo.minutes") }}
                  </option>
                </select>
                <div class="p-3">
                  <button type="button" class="btn btn-primary" @click="saveMinutesPerMatch">
                    {{ $t("general.saveChanges") }}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Choose date and time for individual categories -->
          <div v-if="scheduleCategoryContainerDTO !== null"
               id="categories"
               class="row p-5 col-md-9 col-lg-10 mx-auto custom-card">
            <div>
              <h3 class="py-4">{{ $t("schedule.main.categoryStartTimes") }}</h3>
              <p class="w-75 mx-auto py-2"> {{ $t("schedule.main.helperText") }}</p>
            </div>
            <div id="table-container" class="col-sm-12 mx-auto my-4">
              <table class="table table-bordered">
                <thead>
                <tr>
                  <th scope="col">
                    {{ $t("schedule.main.category") }}
                  </th>
                  <th scope="col">
                    {{ $t("schedule.main.stage") }}
                  </th>
                  <th scope="col">
                    {{ $t("schedule.main.day") }}
                  </th>
                  <th scope="col">
                    {{ $t("schedule.main.startTime") }}
                  </th>
                  <th>
                    {{ $t("schedule.main.tables") }}
                  </th>
                </tr>
                </thead>
                <tbody>
                <template v-for="categorySchedule in scheduleCategoryContainerDTO.scheduleCategoryList">
                  <tr :key="categorySchedule.categoryDTO.id + categorySchedule.selectedMatchType">
                    <td>{{ categorySchedule.categoryDTO.category.name }}</td>
                    <!-- Stage (group or playoff) -->
                    <td>
                      <input
                          class="form-control"
                          disabled
                          :value="$t('schedule.main.' + categorySchedule.selectedMatchType)"
                      >
                    </td>
                    <!-- Select date -->
                    <td>
                      <select id="date-selection"
                              v-model="categorySchedule.selectedDay"
                              @change="checkAndSubmitForScheduling(categorySchedule)"
                              class="form-control">
                        <option value="null">
                          {{ $t("schedule.main.notSelected") }}
                        </option>
                        <option
                            v-for="(tableDay, counter) in availableTables"
                            :key="counter"
                            :value="tableDay.day">
                          {{ tableDay.day }}
                        </option>
                      </select>
                    </td>
                    <!-- Select desired start time -->
                    <td>
                      <vue-timepicker
                          v-model="categorySchedule.selectedStartTime"
                          @change="checkAndSubmitForScheduling(categorySchedule)"
                      />
                    </td>
                    <!-- Select tables -->
                    <td>
                      <div v-if="getTablesForDay(categorySchedule.selectedDay)" class="dropdown">
                        <button id="dropdownMenuButton1" class="btn btn-secondary dropdown-toggle" type="button"
                                data-bs-toggle="dropdown" aria-expanded="false">
                          {{ $t("schedule.main.chooseTables") }}
                        </button>
                        <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton1">
                          <div v-for="tableNr in getTablesForDay(categorySchedule.selectedDay)"
                               :key="tableNr"
                               class="form-check form-check-inline">
                            <input :id="'inlineCheckbox' + tableNr"
                                   v-model="categorySchedule.selectedTables"
                                   @change="checkAndSubmitForScheduling(categorySchedule)"
                                   :value="tableNr" class="form-check-input"
                                   type="checkbox">
                            <label class="form-check-label" :for="'inlineCheckbox' + tableNr">{{ tableNr }}</label>
                          </div>
                          <br/>
                        </ul>
                      </div>
                      <div v-else>
                        {{ $t("schedule.main.noTablesAdded") }}
                      </div>
                    </td>
                  </tr>
                </template>
                </tbody>
              </table>
            </div>
          </div>

          <!-- See generated schedule -->
          <div v-if="generatedScheduleContainer!== null && generatedScheduleContainer.excelScheduleList.length > 0"
                class="col-sm-11 mx-auto my-4 custom-card">
            <!-- Select date -->
            <div>
              <h4> {{ $t("schedule.main.generatedSchedule")}}</h4>
              <button v-for="date in dailyStartEndDTO.availableDays" :key="date"
              class="btn btn-primary m-2" @click="selectTimeTableDay(date)">
                {{ date }}
              </button>
            </div>
            <div v-if="selectedGeneratedSchedule != null" class="table-container ">
              <table class="table table-bordered">
                <thead>
                <tr>
                  <th></th>
                  <th :colspan="selectedGeneratedSchedule.scheduleItemList.length">{{ $t("schedule.main.table") }}</th>
                </tr>
                <tr>
                  <th>Tid</th>
                  <th v-for="item in selectedGeneratedSchedule.scheduleItemList" :key="item.tableNumber"> {{ item.tableNumber }}
                  </th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="time in selectedGeneratedSchedule.validStartTimes" :key="time">
                  <td>{{ getHoursMinutes(new Date(time)) }}</td>
                  <td v-for="item in selectedGeneratedSchedule.scheduleItemList"
                      :style="{backgroundColor: colorCategoryMap[getCategoryAtTableTime(item.tableNumber, time)]}"
                      :key="item.tableNumber" style="font-size: 80%"> {{ getCategoryAtTableTime(item.tableNumber, time) }}
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import {getFormattedDate, getHoursMinutes, undefinedOrNull} from "@/common/util";
import VueTimepicker from 'vue2-timepicker/src/vue-timepicker.vue'
import DailyStartEndService from "@/common/api-services/schedule/daily-start-end.service";
import AvailableTablesService from "@/common/api-services/schedule/available-tables.service";
import ScheduleMetadataService from "@/common/api-services/schedule/schedule-metadata.service";
import CategoryService from "@/common/api-services/category.service";
import ScheduleGeneralService from "@/common/api-services/schedule/schedule-general.service";
import scheduleGeneralService from "@/common/api-services/schedule/schedule-general.service";

export default {
  name: "Schedule",
  components: {VueTimepicker},
  data() {
    return {
      sidebarChoice: "overview",
      selectedCategory: "",
      competitionCategories: [],
      scheduleCategoryContainerDTO: null,
      dailyStartEndDTO: {},
      availableTables: null,
      scheduleMetadata: {},
      minutesPerMatchOptions: [15, 20, 25, 30, 35, 40, 45, 50, 55, 60],
      selectedGeneratedSchedule: null,
      generatedScheduleContainer: null,
      distinctPlannedCategories: [],
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
    this.getDailyStartEnd()
    this.getAvailableTablesForCompetition()
    this.getScheduleMetadata()
    this.getCategorySchedulerSettings()
    this.getTimeTableInfo()
  },
  methods: {
    reRoute() {
      this.$router.push("schedule-advanced")
    },
    getTablesForDay(day) {
      if (this.availableTables != null) {
        const tableDay = this.availableTables
            .find(tableDay => tableDay.day === day)

        if (tableDay === null || tableDay === undefined) {
          return null
        }
        return tableDay.nrTables
      }
      return null
    },
    checkAndSubmitForScheduling(categorySchedule) {
      if (undefinedOrNull(categorySchedule) ||
          undefinedOrNull(categorySchedule.selectedDay) ||
          undefinedOrNull(categorySchedule.selectedStartTime) ||
          categorySchedule.selectedTables.length === 0) {
        return
      }
      else {
        const startTime = new Date(categorySchedule.selectedDay)
        startTime.setHours(categorySchedule.selectedStartTime.HH)
        startTime.setMinutes(categorySchedule.selectedStartTime.mm)
        const categorySpec = {
          "mode": "ABSOLUTE",
          "matchType": categorySchedule.selectedMatchType,
          "tableNumbers": categorySchedule.selectedTables,
          "startTime": startTime,
          "location": this.competition.location.name
        }
        console.log(categorySpec)

        scheduleGeneralService
            .tryScheduleMatches(this.competition.id, categorySchedule.categoryDTO.id, categorySpec)
            .then(() => {
              this.getTimeTableInfo()
        })

      }
    },
    getTimeTableInfo() {
      scheduleGeneralService.getTimeTableInfo(this.competition.id).then(res => {
        this.generatedScheduleContainer = res.data
        if (this.generatedScheduleContainer.excelScheduleList.length > 0) {
          console.log("length greater than 0")
          this.selectedGeneratedSchedule = this.generatedScheduleContainer.excelScheduleList[0]
          console.log(this.selectedGeneratedSchedule)
          this.selectTimeTableDay(this.selectedGeneratedSchedule.dateOfPlay)
        }
      }).catch(err => {
        console.log("Couldn't fetch generated schedule", err)
      })
    },
    selectTimeTableDay(date) {
      this.selectedGeneratedSchedule = this.generatedScheduleContainer.excelScheduleList.find(element => element.dateOfPlay === date)
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
    getCategorySchedulerSettings() {
      // Category id is not used in backend for this call so can be set to anything
      ScheduleGeneralService.getCategorySchedulerSettings(this.competition.id).then(res => {
        this.scheduleCategoryContainerDTO = res.data
      })
    },
    formattedDate: getFormattedDate,
    formatTime: getHoursMinutes,
    undefinedOrNull: undefinedOrNull,
    saveChanges() {
      this.saveDailyStartEndTimes()
      this.saveAvailableTables()
      this.saveMinutesPerMatch()
    },
    trySchedule(competitionCategoryId) {

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
    saveMinutesPerMatch() {
      ScheduleMetadataService.updateMinutesPerMatch(this.competition.id, {minutesPerMatch: this.scheduleMetadata.minutesPerMatch})
    },
    // Returns the category playing at a given table at a given time or empty string if no match is planned at that time
    getCategoryAtTableTime(table, time) {
      if (this.selectedGeneratedSchedule == null) {
        return ""
      }

      let stringToReturn = ""
      this.selectedGeneratedSchedule.scheduleItemList.forEach(item => {
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