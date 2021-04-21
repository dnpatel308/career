import axios from 'axios'
import Cookie from 'cookies'

export default async ({ Vue }) => {
  axios.defaults.baseURL = 'http://stockdevvm.brodos.net:8080'
  const headers = {
    'Content-Type': 'application/json',
    'Cookie': 'ISAAC-authtoken=' + Cookie.get('ISAAC-authtoken')
  }
  axios.defaults.headers = headers
  Vue.prototype.$axios = axios
}
