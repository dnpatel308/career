import axios from 'axios'
import { Cookies } from 'quasar'

export default async ({ Vue }) => {
  axios.defaults.baseURL = 'http://stockdevvm.brodos.net:8181'
  const headers = {
    'Content-Type': 'application/json',
    'Cookie': 'ISAAC-authtoken=' + Cookies.get('ISAAC-authtoken')
  }
  axios.defaults.headers = headers
  Vue.prototype.$axios = axios
}
