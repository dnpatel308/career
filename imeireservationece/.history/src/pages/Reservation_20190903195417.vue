<template>
  <q-page padding>
    <div class="q-pa-md" style="max-width: 400px">
      <q-form @submit="onSubmit" @reset="onReset" class="q-gutter-md">
        <q-input
          v-model="articleNo"
          filled
          lazy-rules
          label="Artikel-Nr: *"
          :rules="[ val => val && val.length > 0 || 'Please enter Artikel-Nr']"
        ></q-input>
        <q-input
          filled
          type="number"
          v-model="quantity"
          label="Anzahl: *"
          lazy-rules
          disable="disable"
          :rules="[]"
        ></q-input>
        <q-input
          filled
          type="email"
          v-model="customerEmail"
          label="Mail-Adresse: *"
          lazy-rules
          :rules="[
            val => val !== null && val !== '' || 'Please enter mail-adresse'
          ]"
        ></q-input>
        <q-input
          filled
          type="textarea"
          v-model="reservationComment"
          label="Kommentar:"
          lazy-rules
          :rules="[
            val => val !== null && val !== '' || 'Please enter kommentar'
          ]"
        ></q-input>
        <div>
          <q-btn label="Submit" type="submit" color="secondary no-shadow"></q-btn>
          <q-btn
            label="Reset"
            type="reset"
            color="primary"
            text-color="dark-gray"
            class="q-ml-sm no-shadow"
          ></q-btn>
        </div>
      </q-form>
    </div>
    <div q-pa-md q-gutter-sm>
      <q-dialog v-model="alert">
        <q-card>
          <q-card-section>
            <div class="text-h6">Alert</div>
          </q-card-section>

          <q-card-section>Lorem ipsum dolor sit amet consectetur adipisicing elit. Rerum repellendus sit voluptate voluptas eveniet porro. Rerum blanditiis perferendis totam, ea at omnis vel numquam exercitationem aut, natus minima, porro labore.</q-card-section>

          <q-card-actions align="right">
            <q-btn flat label="OK" color="primary" v-close-popup />
          </q-card-actions>
        </q-card>
      </q-dialog>
    </div>
  </q-page>
</template>

<script>
export default {
  data () {
    return {
      articleNo: null,
      age: null,
      quantity: 1,
      customerEmail: null,
      reservationComment: null,
      alert: false
    }
  },

  methods: {
    onSubmit () {
      let obj = {
        articleNumber: this.articleNo,
        customerNumber: 1234,
        email: this.customerEmail,
        quantity: this.quantity,
        reservationComment: this.reservationComment,
        tenantId: 1
      }

      this.$axios.post('/wms/serialnumberreservation', obj).then(response => {
        this.showAlert()
      })
    },

    onReset () {
      this.name = null
      this.age = null
      this.customerEmail = null
      this.reservationComment = null
      this.alert = false
    },

    showAlert () {
      this.$q.dialog({
        title: 'Alert',
        message: 'IMEI reservation is successfull'
      }).onOk(() => {
        this.onReset()
      }).onCancel(() => {
        console.log('Cancel')
      }).onDismiss(() => {
        console.log('I am triggered on both OK and Cancel')
      })
    }
  }
}
</script>

<style>
</style>
