<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated class="bg-primary text-black text-bold" height-hint="98">
      <q-toolbar>

        <q-toolbar-title class="bg-primary text-black text-bold">
          <q-avatar>
            <a class="logo" href="">Logo</a>
            IMEIReservation
          </q-avatar>
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
q-avatar{
   vertical-align: middle;
  width: 500px;
  height: 500px;
  border-radius: 50%;
}
.logo { background: url("https://brodos.net/omnichannel/wp-content/uploads/2018/07/bnet_logo.png") no-repeat; width: 100px; height: 100px; display: block; text-indent: -9999px;}
</style>
