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
      <q-tabs class="full-height">
  <!-- Tabs - notice slot="title" -->
  <q-tab default count="5" slot="title" name="tab-1" icon="message"></q-tab>
  <q-tab disable slot="title" name="tab-2" icon="fingerprint" ></q-tab>
  <q-tab alert slot="title" name="tab-3" icon="account_box" ></q-tab>
  <q-tab slot="title" name="tab-4" icon="accessibility" ></q-tab>
  <q-tab slot="title" name="tab-5" icon="build" ></q-tab>

  <!-- Targets -->
  <q-tab-pane name="tab-1" class="row">
    <div class="bg-grey-5 square q-ma-sm" v-for="(item, $index) in ar" key="index">
    </div>
  </q-tab-pane>
  <q-tab-pane name="tab-2">Tab Two</q-tab-pane>
  <q-tab-pane name="tab-3">Tab Three</q-tab-pane>
  <q-tab-pane name="tab-4">Tab Four</q-tab-pane>
  <q-tab-pane name="tab-5">Tab Five</q-tab-pane>
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
