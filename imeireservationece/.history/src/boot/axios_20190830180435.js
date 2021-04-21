import axios from 'axios'

export default async ({ Vue }) => {
  axios.defaults.baseURL = 'http://stockdevvm.brodos.net'
  Vue.prototype.$axios = axios
}
