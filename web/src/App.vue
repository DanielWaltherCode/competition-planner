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
@import url('https://fonts.googleapis.com/css2?family=Bebas+Neue&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;500;600;700&display=swap');

#app {
  font-weight: 400;
  text-align: center;
  font-size: 1.1em;
}

h1, h2, h3 {
  color: var(--main-color);
  text-align: center;
}

.form-label {
  display: flex !important;
}

.nav-link {
  color: #383838 !important;
}

.active {
  font-weight: bold;
}

/* Global variables */
:root {
  --main-color: #012970;
  --yellow: #FFB657;
  --background: #F6EBFF;
  --sidebar-color: #F1F5FD;
  --grey-color: #F8F8F8;

  --clr-primary-50: hsl(221, 94%, 92%);;
  --clr-primary-100: hsl(221, 94%, 87%);
  --clr-primary-200: hsl(221, 94%, 77%);
  --clr-primary-300: hsl(221, 94%, 67%);
  --clr-primary-400: hsl(221, 94%, 57%);
  --clr-primary-500: hsl(221, 94%, 47%); /* This is Primary in Figma*/
  --clr-primary-600: hsl(221, 94%, 37%);
  --clr-primary-700: hsl(221, 94%, 27%);
  --clr-primary-800: hsl(221, 94%, 17%);
}


.emphasized {
  color: var(--main-color)
}


.btn-primary {
  background-color: var(--main-color) !important;
  border: 1px solid var(--main-color) !important;
}

.btn-check:active+.btn-outline-primary, .btn-check:checked+.btn-outline-primary, .btn-outline-primary.active, .btn-outline-primary.dropdown-toggle.show, .btn-outline-primary:active{
  background-color: var(--main-color) !important;
  color: white !important;
  font-weight: bold;
}


.btn-outline-primary:hover {
  color: white !important;
  background-color: var(--main-color) !important;
}

.btn-outline-primary {
  color: var(--main-color) !important;
  border: 1px solid var(--main-color) !important;
}


/* Sidebar styling */
.sidebar-header {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 10px 0 30px 0;
  color: var(--main-color);
  font-weight: bold;
}

.sidebar {
  min-height: 100vh;
  z-index: 0;
  background: white;
  margin-top: -18px;
  box-shadow: 3px 3px 2px 1px #efefef;
}

.sidebar .selected {
  background-color: var(--main-color) !important;
  border-color: 1px var(--main-color) !important;
}

@media only screen and (max-width: 768px) {
  .sidebar {
    min-height: fit-content;
    padding-bottom: 20px;
  }
}

.sidebar li {
  background-color: inherit;
  color: black;
}

.sidebar li:hover {
  opacity: 0.7;
}

.sidebar li:hover {
  cursor: pointer;
}

.blue-section {
  background-color: #e1f1ff;
}

.blue-section .h1, h2, h3, h4, h5, h6 {
  color: #8f4570;
}

.fa-arrow-right:hover {
  cursor: pointer;
  text-decoration: underline;
}

.fa-arrow-left:hover {
  cursor: pointer;
  text-decoration: underline;
}

.clickable:hover {
  cursor: pointer;
  opacity: 0.8;
}

.custom-select{
  background: none !important;
}

.black{
  color: black;
}

.bg-grey {
  background-color: rgba(225, 225, 225, 0.20);
}

.custom-card {
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);
}

.modal__close {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
}

.modal__content {
  flex-grow: 1;
  overflow-y: auto;
}

.modal-footer button {
  width: 200px;
}
</style>
