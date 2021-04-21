<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated class="bg-primary text-black text-bold" height-hint="98">
      <q-toolbar>

        <q-toolbar-title class="bg-primary text-black text-bold content-center">
          <q-avatar class="custom-logo">
            <img src="https://brodos.net/omnichannel/wp-content/uploads/2018/07/bnet_logo.png">
          </q-avatar>
          IMEIReservation
        </q-toolbar-title>
        <div>Back to brodosnet</div>
        </q-toolbar>
      <q-layout-header>
    <q-tabs>
      <q-route-tab
        icon="mail"
        to="/mails"
        exact
        slot="title"
      />

      <q-route-tab
        icon="alarm"
        to="/alarms"
        exact
        slot="title"
      />
    </q-tabs>
  </q-layout-header>
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
      leftDrawerOpen: false
    }
  },
  methods: {
    openURL
  }
}
</script>

<style>

</style>
