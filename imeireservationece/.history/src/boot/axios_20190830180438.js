import axios from 'axios'

export default async ({ Vue }) => {
  axios.defaults.baseURL = 'http://stockdevvm.brodos.net:8080'
  Vue.prototype.$axios = axios
}
