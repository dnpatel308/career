import axios from 'axios'

export default async ({ Vue }) => {
  Window.log.debug('booting axios...')

  axios.defaults.baseURL = process.config.STOCKAPI
  axios.defaults.withCredentials = true
  axios.defaults.headers['Content-Type'] = 'application/json'
  axios.defaults.headers.common = {}

  Vue.prototype.$axios = axios
  Window.log.debug(Vue.prototype.$axios.defaults.headers)
}

export { axios }
