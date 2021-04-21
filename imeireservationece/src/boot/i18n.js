import VueI18n from 'vue-i18n'

let i18n

export default ({ app, Vue }) => {
  Vue.use(VueI18n)

  app.i18n = new VueI18n({
    locale: 'de',
    messages: {
      de: {
        CANCELLED: 'storniert',
        REQUESTFORSENTOUT: 'Versand wird vorbereitet',
        SENTOUT: 'versendet',
        RESERVED: 'reserviert',
        OPEN: 'warten auf IMEI',
        CLOSED: 'storniert'
      }
    }
  })

  i18n = app.i18n
}

export { i18n }
