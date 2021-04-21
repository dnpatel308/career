import axios from 'axios'

export default async ({ Vue }) => {
  axios.defaults.baseURL = 'http://stockdevvm.brodos.net:8080'
  const headers = {
    'Content-Type': 'application/json',
    'Cookie': 'ISAAC-authtoken=' + $cookies.get('ISAAC-authtoken')
  }
  axios.defaults.headers = headers
  Vue.prototype.$axios = axios
}
