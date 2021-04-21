<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated class="bg-primary text-black text-bold" height-hint="98">
      <q-toolbar>

        <q-toolbar-title class="bg-primary text-black text-bold">
          <div class="custom-logo">
            <img src="https://brodos.net/omnichannel/wp-content/uploads/2018/07/bnet_logo.png">
            IMEIReservation
          </div>
        </q-toolbar-title>
        <div>Back to brodosnet</div>
        </q-toolbar>
      <q-tabs v-model="selectedTab" color="transparent" text-color="green" align="left">
        <q-route-tab to="/page1" label="Reservation" name="reservation"/>
        <q-route-tab to="/page2" label="Manage Reservation" name="manageReservation"/>
        <q-route-tab to="/page3" label="IMEIHistory" name="imeiHistory"/>
      </q-tabs>
    </q-header>
    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { openURL } from 'quasar'

export default {
  name: 'MyLayout',
  data () {
    return {
      selectedTab: 'reservation'
    }
  },
  methods: {
    openURL
  }
}
</script>

<style>
.custom-logo{
    height : 250px;
    width: 250px;
}
</style>
