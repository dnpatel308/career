<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated class="bg-primary text-black text-bold" height-hint="98">
      <q-toolbar>
        <q-toolbar-title class="bg-primary text-black text-bold">
          <q-avatar>
            <img src="https://brodos.net/omnichannel/wp-content/uploads/2018/07/bnet_logo.png" />
          </q-avatar>
          IMEIReservation
        </q-toolbar-title>
        <div><q-btn label="Back to brodosnet" type="submit" color="light-blue no-shadow"></q-btn></div>
        </q-toolbar>
      <q-tabs v-model="selectedTab" inline-label class="text-secondary no-shadow" align="left">
        <q-route-tab to="/reservation" label="Reservieren" name="reservation"/>
        <q-route-tab to="/managereservation" label="Verwalten" name="manageReservation"/>
        <q-route-tab to="/imeihistory" label="IMEI History" name="imeiHistory"/>
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

</style>
