<template>
  <div id="app">
    <Header />
    <router-view id="router"></router-view>
  </div>
</template>

<script>
import Header from "@/components/Header";

export default {
  name: 'App',
  components: {
    Header,
  }
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;700&display=swap');

#app {
  font-family: 'Open Sans', sans-serif;;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  font-weight: 400;
  text-align: center;
  color: black;
  font-size: 1.2em;
}

/* Global variables */
:root {
  --emphasis-color: #1190cb;
  --background2: #edf4fc;
}

.emphasized {
  color: var(--emphasis-color)
}

</style>
