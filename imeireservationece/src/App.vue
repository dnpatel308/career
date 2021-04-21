<template>
  <div id="q-app">
    <router-view />
  </div>
</template>

<script>
import { brocloak } from './assets/brocloak.js'

export default {
  name: 'App',
  preFetch ({ store, redirect }) {
    Window.log.debug('preFetching...')

    var conf = {
      BNETURL: process.config.BNETURL,
      OTPAPI: process.config.OTPAPI,
      STOREID: 'brocloakStore',
      REFRESHAPI: process.config.REFRESHAPI
    }

    return brocloak(store, redirect, conf)
  }
}
</script>

<style>
</style>
