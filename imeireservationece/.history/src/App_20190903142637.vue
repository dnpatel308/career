<template>
  <div id="q-app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
  beforeMount : function () {
    console.log(this.$route.query)
  }
}
</script>

<style>
</style>
