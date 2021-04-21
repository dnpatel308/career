<template>
  <q-page padding>
    <!-- content -->
    <div class="q-pa-md">
    <q-table
      class="my-sticky-header-table"
      title="Manage Reservation"
      :data="data"
      :columns="columns"
      row-key="name"
      flat
      bordered
    ></q-table>
  </q-page>
</template>

<script>
export default {
  // name: 'PageName',
}
</script>

<style lang="stylus">
.my-sticky-header-table
  /* max height is important */
  .q-table__middle
    max-height auto

  .q-table__top,
  .q-table__bottom,
  thead tr:first-child th /* bg color is important for th; just specify one */
    background-color #f6f7f7

  thead tr:first-child th
    position sticky
    top 0
    opacity 1
    z-index 1
</style>
