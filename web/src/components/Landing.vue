<template>
  <div>
    <main>
      <div id="top-container">
        <div class="text-div">
          <div>
            <p class="emphasized"> {{ $t("landing.heading.part1") }}</p>
          </div>
          <div>
            <p class="second"> {{ $t("landing.heading.part2") }}</p>
          </div>
        </div>
        <div class="image-holder">
          <img src="@/assets/tennis.svg" alt="Tennis player">
        </div>
      </div>
      <div id="middle">
        <div class="card-container">
          <div class="card">
            <img src="@/assets/organize_resume.svg" alt="Planning">
            <div class="card-body">
              <h3 class="card-title">{{ $t("landing.middle.organize.title") }}</h3>
              <p class="card-text">{{ $t("landing.middle.organize.text") }}</p>
            </div>
          </div>
        </div>
        <div class="card-container">
          <div class="card">
            <img src="@/assets/schedule.svg" alt="Scheduling">
            <div class="card-body">
              <h3 class="card-title">{{ $t("landing.middle.host.title") }}</h3>
              <p class="card-text">{{ $t("landing.middle.host.text") }}</p>
            </div>
          </div>
        </div>
        <div class="card-container">
          <div class="card">
            <img src="@/assets/data_points.svg" alt="Results">
            <div class="card-body">
              <h3 class="card-title">{{ $t("landing.middle.report.title") }}</h3>
              <p class="card-text">{{ $t("landing.middle.report.text") }}</p>
            </div>
          </div>
        </div>
      </div>
      <div id="bottom">
        <div class="image-holder">
          <img src="@/assets/good_team.svg" alt="Tennis player">
        </div>
        <div id="bottom-text">
          <p>{{ $t("landing.bottom.section1") }}</p>
          <ul>
            <li>{{ $t("landing.bottom.bullit1") }}</li>
            <li>{{ $t("landing.bottom.bullit2") }}</li>
            <li>{{ $t("landing.bottom.bullit3") }}</li>
          </ul>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
export default {
  name: "Landing"
}
</script>

<style scoped>

#top-container {
  height: 400px;
  display: grid;
  grid-template-columns: 1fr 1fr;
}

.text-div {
  align-self: center;
  margin-left: 20px;
}

.emphasized {
  font-size: 40px;
}

.second {
  font-size: 24px;
}

.image-holder img {
  max-width: 90%;
  max-height: 90%;
}

/* Middle */
#middle {
  margin-top: 80px;
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
}

.card-container {
  background-color: var(--background2);
  font-size: 24px;
  padding: 50px;
}


.img-container {
  height: 50%;
}

.card {
  min-height: 350px;
  background: white !important;
}

.card-body {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.card h3 {
  font-weight: bolder;
}

#middle div img {
  margin: 30px auto 30px auto;
  max-width: 40%;
}


#bottom {
  display: grid;
  grid-template-columns: 1fr 1fr;
}

#bottom img {
  margin: 40px;
  max-width: 70%;
  max-height: 80%;
}

#bottom-text {
  display: flex;
  flex-direction: column;
  justify-content: center;
  text-align: left;
}


</style>