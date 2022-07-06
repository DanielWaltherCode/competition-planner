<template>
  <main>
    <h1 class="p-4 fs-3 fs-md-1">
      <i @click="$router.push('/draw')" class="fas fa-arrow-left" style="float: left"></i>
      {{ $t("schedule.main.heading") }}
      <i @click="$router.push('/results')" class="fas fa-arrow-right" style="float: right"></i>
    </h1>
    <div>
      <div class="row bg-grey">

        <!-- Main content -->
        <div id="main" class="col-lg-11 mx-auto ps-0 bg-grey">
          <!-- General information about competition -->
          <div class="row p-3 m-md-2 custom-card bg-white">
            <div>
              <!-- Daily start end -->
              <div class="row col-lg-11 mx-auto custom-card p-5">
                <div>
                  <h4>{{ $t("schedule.generalInfo.startEnd") }}</h4>
                  <p>{{ $t("schedule.generalInfo.startEndHelper") }}
                    <span> <router-link to="/overview">{{
                        $t("schedule.generalInfo.startEndHelperHere")
                      }}</router-link> </span>
                  </p>
                </div>
                <div class="table-container col-md-8">
                  <table class="table table-bordered">
                    <thead>
                    <tr>
                      <th scope="col"> {{ $t("schedule.main.day") }}</th>
                      <th scope="col"> {{ $t("schedule.generalInfo.startTime") }}</th>
                      <th scope="col"> {{ $t("schedule.generalInfo.endTime") }}</th>
                      <th scope="col"> {{ $t("schedule.generalInfo.nrTables") }}</th>
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
                      <td>
                        <p v-if="getTablesForDay(day.day) === -1">{{
                            $t("schedule.generalInfo.cannotChangeTables")
                          }}</p>
                        <select v-if="getTablesForDay(day.day)  !== -1" id="table-selection" class="form-control"
                                @change="setTablesForDay(day.day, $event)">
                          <option value="0"> {{ $t("schedule.generalInfo.availableTablesNotSet") }}</option>
                          <option v-for="i in 100" :key="i" :value="i" :selected="getTablesForDay(day.day) === i">
                            {{ i }}
                          </option>
                        </select>
                      </td>
                    </tr>
                    </tbody>
                  </table>
                </div>
                <!-- Average time per match -->
                <div class="col-md-4 mx-auto bg-grey shadow">
                  <h5 class="p-3 text-center">{{ $t("schedule.generalInfo.averageMatchTimeHeading") }}</h5>
                  <p>{{ $t("schedule.generalInfo.averageMatchTimeHelper") }}</p>
                  <select id="match-length-selection" class="form-control" v-model="scheduleMetadata.minutesPerMatch">
                    <option v-for="i in minutesPerMatchOptions" :key="i" :value="i">
                      {{ i + " " + $t("schedule.generalInfo.minutes") }}
                    </option>
                  </select>
                </div>
                <div>
                  <button type="button" class="btn btn-primary" @click="saveChanges">
                    {{ $t("general.saveChanges") }}
                  </button>
                </div>
              </div>

            </div>


            <!-- Choose date and time for individual categories -->
            <div v-if="scheduleCategoryContainerDTO !== null"
                 id="categories"
                 class="row p-5 col-lg-11 mx-auto custom-card">
              <div>
                <h3 class="py-4">{{ $t("schedule.main.categoryStartTimes") }}</h3>
                <p class="mx-auto py-2"> {{ $t("schedule.main.helperText") }}</p>
              </div>
              <div id="table-container" class="my-4">
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
                            :minute-interval="5"
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
          </div>

          <!-- See generated schedule -->
          <div v-if="generatedScheduleContainer!== null && generatedScheduleContainer.excelScheduleList.length > 0"
               class="col-sm-11 mx-auto my-4 custom-card">
            <!-- Select date -->
            <div>
              <h4> {{ $t("schedule.main.generatedSchedule") }}</h4>
              <button v-for="date in dailyStartEndDTO.availableDays" :key="date"
                      class="btn btn-primary m-2" @click="selectTimeTableDay(date)">
                {{ date }}
              </button>
            </div>
            <div v-if="selectedGeneratedSchedule != null" class="table-container ">
              <div class="p-4">
                <p> {{ $t("schedule.main.abbreviated.GROUP") }} -> {{ $t("schedule.main.GROUP") }}</p>
                <p> {{ $t("schedule.main.abbreviated.PLAYOFF") }} -> {{ $t("schedule.main.PLAYOFF") }}</p>
              </div>
              <table class="table table-bordered">
                <thead>
                <tr>
                  <th></th>
                  <th :colspan="selectedGeneratedSchedule.scheduleItemList.length">{{ $t("schedule.main.table") }}</th>
                </tr>
                <tr>
                  <th>Tid</th>
                  <th v-for="item in selectedGeneratedSchedule.scheduleItemList" :key="item.tableNumber">
                    {{ item.tableNumber }}
                  </th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="time in selectedGeneratedSchedule.validStartTimes" :key="time">
                  <td>{{ getHoursMinutes(new Date(time)) }}</td>
                  <td v-for="item in selectedGeneratedSchedule.scheduleItemList"
                      :style="{backgroundColor: colorCategoryMap[getCategoryAtTableTime(item.tableNumber, time, false)]}"
                      :key="item.tableNumber" style="font-size: 80%">
                    {{ getCategoryAtTableTime(item.tableNumber, time, true) }}
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
    this.setUp()
  },
  methods: {
    setUp() {
      this.getDailyStartEnd()
      this.getAvailableTablesForCompetition()
      this.getScheduleMetadata()
      this.getCategorySchedulerSettings()
      this.getTimeTableInfo()
    },
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
    setTablesForDay(day, event) {
      for (let i = 0; i < this.availableTables.length; i++) {
        if (this.availableTables[i].day === day) {
          this.availableTables[i].nrTables = event.target.value
        }
      }
    },
    checkAndSubmitForScheduling(categorySchedule) {
      if (undefinedOrNull(categorySchedule) ||
          undefinedOrNull(categorySchedule.selectedDay) ||
          undefinedOrNull(categorySchedule.selectedStartTime) ||
          categorySchedule.selectedTables.length === 0) {
        return
      } else {
        console.log(Intl.DateTimeFormat().resolvedOptions().timeZone)
        const startTime = categorySchedule.selectedDay + 'T' + this.getTime(categorySchedule.selectedStartTime) + 'Z'
        console.log(startTime)
        const categorySpec = {
          "mode": "APPEND",
          "matchType": categorySchedule.selectedMatchType,
          "tableNumbers": categorySchedule.selectedTables,
          "startTime": startTime,
          "location": this.competition.location.name
        }
        scheduleGeneralService
            .scheduleCategory(this.competition.id, categorySchedule.categoryDTO.id, categorySpec)
            .then(() => {
              this.getTimeTableInfo()
            })

      }
    },
    getTimeTableInfo() {
      scheduleGeneralService.getTimeTableInfo(this.competition.id).then(res => {
        this.generatedScheduleContainer = res.data
        if (this.generatedScheduleContainer.excelScheduleList.length > 0) {
          this.selectedGeneratedSchedule = this.generatedScheduleContainer.excelScheduleList[0]
          for (let i = 0; i < this.selectedGeneratedSchedule.distinctCategories.length; i++) {
            const category = this.selectedGeneratedSchedule.distinctCategories[i]
            this.colorCategoryMap[category.name] = this.colors[i]
          }
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
      const saveGeneralChanges = {
        dailyStartEnd: {
          dailyStartEndList: this.dailyStartEndDTO.dailyStartEndList.map(object => this.convertToDailyStartEndSpec(object))
        },
        minutesPerMatchSpec: {minutesPerMatch: this.scheduleMetadata.minutesPerMatch },
        availableTables: {tableDays: this.availableTables }
      }

      ScheduleGeneralService.saveMainScheduleChanges(this.competition.id, saveGeneralChanges).then(() => {
        this.setUp()
      })
    },
    trySchedule(competitionCategoryId) {

    },
    convertToDailyStartEndSpec(dailyStartEndObject) {
      const startTime = this.getTime(dailyStartEndObject.startTime)
      const endTime = this.getTime(dailyStartEndObject.endTime)
      const objectToSave = {
        day: dailyStartEndObject.day,
        startTime: startTime,
        endTime: endTime
      }
      return objectToSave
    },
    /*
    * Available tables
     */
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
    // Returns the category playing at a given table at a given time or empty string if no match is planned at that time
    getCategoryAtTableTime(table, time, withGroup) {
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
              if (withGroup) {
                stringToReturn = this.createCategoryMatchStringWithGroup(match)
              } else {
                stringToReturn = this.createCategoryMatchStringNoGroup(match)
              }
            }
          })
        }
      })
      return stringToReturn
    },
    // Returns e.g. "Herrar 1 (G)"
    createCategoryMatchStringWithGroup(categoryMatch) {
      return categoryMatch.category.name + ' (' + this.$t("schedule.main.abbreviated." + categoryMatch.groupOrRound) + ")"
    },
    createCategoryMatchStringNoGroup(categoryMatch) {
      return categoryMatch.category.name;
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