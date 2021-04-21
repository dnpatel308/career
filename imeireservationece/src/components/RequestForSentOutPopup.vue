<template>
  <q-dialog ref="dialog" @hide="onDialogHide">
    <q-card class="q-dialog-plugin">
      <q-form id="RequestForSentOutForm">
      <q-card-section>
        <h6 class="text-bold" style="color: #13496d; margin: 0; font-size: 1.15em;">Wie sollen wir den Artikel versenden?</h6>
          <q-separator/>
          <div class="row">
              <div><q-radio v-model="kauf" val="COMMISSION" label="Kommissionsware"></q-radio></div>
              <div><q-radio v-model="kauf" val="INVOICE" label="Kaufware"></q-radio></div>
            </div>
          <q-separator/>
          <h6 class="text-bold" style="color: #13496d; margin: 0; font-size: 1.15em;">Lieferadresse</h6>
            <div class="row">
              <div><q-radio v-model="lieferung" val="In den laden liefern" label="In den Laden liefern"></q-radio></div>
              <q-space style="max-width: 16px;"/>
              <div><q-radio v-model="lieferung" val="Strecke zum kunden" label="Strecke zum Kunden"></q-radio></div>
            </div>

          <q-separator/>
          <div class="row">
            <h6 class="text-bold" style="color: #13496d; margin: 0; margin-left: 5px; font-size: 1.15em;">{{lieferung === 'In den laden liefern' ? 'Hinweis: Wenn du keine Adresse einträgst, senden wir an deine Standardlieferadresse' : ''}}</h6>
          </div>
          <q-space style="min-height: 15px;"/>
          <label>Anrede:</label>
           <q-btn-dropdown no-caps :label="salutation" style="margin-left: 10px; min-width: 100px;">
            <q-list>
              <q-item clickable v-close-popup @click="updateSalutation('Herr')">
                <q-item-section>
                  <q-item-label>Herr</q-item-label>
                </q-item-section>
              </q-item>

              <q-item clickable v-close-popup @click="updateSalutation('Frau')">
                <q-item-section>
                  <q-item-label>Frau</q-item-label>
                </q-item-section>
              </q-item>

              <q-item clickable v-close-popup @click="updateSalutation('Firma')">
                <q-item-section>
                  <q-item-label>Firma</q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
          </q-btn-dropdown>
          <q-space style="min-height: 15px;"/>
          <div v-if="salutation === 'Firma'">
            <label>Firma:</label>
            <q-input
              v-model="unternehmen"
              filled
              lazy-rules
            ></q-input>
            <q-space style="min-height: 15px;"/>
          </div>
          <div v-if="salutation !== 'Firma'">
            <label>Vorname:</label>
            <q-input
              v-model="firstName"
              filled
              lazy-rules
            ></q-input>
            <q-space style="min-height: 15px;"/>
            <label>Nachname:</label>
            <q-input
              v-model="lastName"
              filled
              lazy-rules
            ></q-input>
            <q-space style="min-height: 15px;"/>
          </div>
          <q-space style="min-height: 0px;"/>
          <label>Namenszusatz:</label>
            <q-input
              v-model="nameAffix"
              filled
              lazy-rules
            ></q-input>
            <q-space style="min-height: 15px;"/>
          <label>Straße:</label>
           <q-input
            ref="straße"
            v-model="straße"
            filled
            lazy-rules
            :rules="[ val => (!lieferung || lieferung === 'Strecke zum kunden' ? val && val.length > 0 : true) || 'Pflichtfeld']"
            @focus="handleFocus('straße')"
          ></q-input>
          <q-space style="min-height: 0px;"/>
          <label>Hausnummer:</label>
           <q-input
            ref="hausnummer"
            v-model="hausnummer"
            filled
            lazy-rules
            :rules="[ val => (!lieferung || lieferung === 'Strecke zum kunden' ? val && val.length > 0 : true) || 'Pflichtfeld']"
            @focus="handleFocus('hausnummer')"
          ></q-input>
          <q-space style="min-height: 0px;"/>
          <label>Postleitzahl:</label>
           <q-input
            ref="postleitzahl"
            v-model="postleitzahl"
            filled
            lazy-rules
            :rules="[ val => (!lieferung || lieferung === 'Strecke zum kunden' ? val && val.length > 0 : true) || 'Pflichtfeld']"
            @focus="handleFocus('postleitzahl')"
          ></q-input>
          <label>Ort:</label>
           <q-input
            ref="ort"
            v-model="ort"
            filled
            lazy-rules
            :rules="[ val => (!lieferung || lieferung === 'Strecke zum kunden' ? val && val.length > 0 : true) || 'Pflichtfeld']"
            @focus="handleFocus('ort')"
          ></q-input>
      </q-card-section>

      <q-separator style="margin-top: -20px;"/>

      <!-- buttons example -->
      <q-card-actions align="right">
        <q-btn no-caps style="background: gray; color: white;" label="Abbrechen" @click="onCancelClick" />
        <q-btn no-caps type="submit" color="positive" label="Versenden" @click="onOKClick" />
      </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script>

import { Notify } from 'quasar'
import { mapGetters, mapActions } from 'vuex'

export default {
  data () {
    return {
      kauf: '',
      lieferung: '',
      unternehmen: '',
      firstName: '',
      lastName: '',
      straße: '',
      hausnummer: '',
      postleitzahl: '',
      ort: '',
      nameAffix: '',
      salutation: 'Firma'
    }
  },

  props: {
    // ...your custom props
  },

  methods: {
    ...mapGetters('imeireservationeceStore', [
      'getPosition',
      'getImei',
      'getEan'
    ]),
    ...mapActions('imeireservationeceStore', [
      'updateStatus'
    ]),
    handleFocus (index) {
      this.$refs[index].resetValidation()
    },

    // following method is REQUIRED
    // (don't change its name --> "show")
    show () {
      this.$refs.dialog.show()
    },

    // following method is REQUIRED
    // (don't change its name --> "hide")
    hide () {
      this.$refs.dialog.hide()
    },

    onDialogHide () {
      this.$emit('hide')
    },

    onOKClick () {
      Window.log.debug(this.kauf)
      Window.log.debug(this.lieferung)
      Window.log.debug(this.unternehmen)
      Window.log.debug(this.firstName)
      Window.log.debug(this.lastName)
      Window.log.debug(this.straße)
      Window.log.debug(this.hausnummer)
      Window.log.debug(this.postleitzahl)
      Window.log.debug(this.salutation)
      Window.log.debug(this.nameAffix)
      Window.log.debug(this.ort)

      if (!this.kauf || !this.lieferung ||
      (!this.unternehmen && (!this.firstName || !this.lastName) && this.lieferung !== 'In den laden liefern') ||
      (this.lieferung === 'Strecke zum kunden' && (!this.ort || !this.straße || !this.hausnummer || !this.postleitzahl))) {
        Window.log.debug('invalid')
        this.$q.dialog({
          title: 'Fehler',
          message: 'Bitte fülle alle Pflichtfelder aus.',
          ok: {
            push: true,
            size: 'md',
            color: 'positive'
          }
        }).onOk(() => {
        })
      } else {
        Window.log.debug('valid')

        let reqBody = {}
        reqBody.tenantId = '1'
        reqBody.imei = this.getImei()
        reqBody.ean = this.getEan()
        reqBody.imeiReservationStatus = 'REQUESTFORSENTOUT'
        reqBody.purchaseType = this.kauf
        reqBody.reservationAddressDTO = {}
        reqBody.reservationAddressDTO.addressType = (this.lieferung === 'In den laden liefern' ? 'SELF' : 'CUSTOMER')
        if (this.unternehmen || (this.firstName && this.lastName)) {
          if (this.unternehmen) {
            reqBody.reservationAddressDTO.companyName = this.unternehmen
          }
          if (this.lastName && this.lastName) {
            reqBody.reservationAddressDTO.firstName = this.firstName
            reqBody.reservationAddressDTO.lastName = this.lastName
          }
          reqBody.reservationAddressDTO.street = this.straße
          reqBody.reservationAddressDTO.houseNo = this.hausnummer
          reqBody.reservationAddressDTO.zipCode = this.postleitzahl
          reqBody.reservationAddressDTO.salutation = this.salutation
          reqBody.reservationAddressDTO.nameAffix = this.nameAffix
          reqBody.reservationAddressDTO.city = this.ort
        }

        Window.log.debug(reqBody)

        Window.log.debug(this.$axios.defaults.headers)
        this.$q.loading.show()
        let callback = () => {
          this.$axios.put('/serialnumberreservation', JSON.stringify(reqBody)).then(responseJSON => {
            this.$q.loading.hide()
            Notify.create({
              message: 'Die Anfrage wurde erfolgreich übermittelt.',
              position: 'center',
              timeout: 5000
            })

            this.updateStatus(this.getPosition())
            this.hide()
          }).catch((err) => {
            Window.log.debug(err.response.data.data)
            try {
              if ((err.response.data.code === 404 && err.response.data.data.code === 7019) ||
              (err.response.data.code === 400 && err.response.data.data.code === 7053)) {
                this.removePosition(this.getPosition())
                this.hide()
              }
            } catch (err) {
              // igonre
            }

            this.showGenericRequestFailureMessage(this, err)
          })
        }

        setTimeout(callback, 500)
      }
    },

    onCancelClick () {
      this.hide()
    },

    updateSalutation (val) {
      this.salutation = val

      if (this.salutation === 'Firma') {
        this.firstName = ''
        this.lastName = ''
      } else {
        this.unternehmen = ''
      }
    }
  }
}
</script>
