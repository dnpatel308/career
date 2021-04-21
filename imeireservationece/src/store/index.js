import Vue from 'vue'
import Vuex from 'vuex'

import brocloakStore from './brocloak-store/index'
import imeireservationeceStore from './imeireservationece-store/index'

import * as globalmethods from '../assets/globalmethods'

Vue.use(Vuex)

Vue.mixin({
  methods: globalmethods
})

/*
 * If not building with SSR mode, you can
 * directly export the Store instantiation
 */

export default function (/* { ssrContext } */) {
  const Store = new Vuex.Store({
    modules: {
      brocloakStore,
      imeireservationeceStore
    }
  })

  return Store
}
