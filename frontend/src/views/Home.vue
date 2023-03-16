<template>
  <div class="home">
    <div class="columns">
      <div class="column is-one-fifth hero is-success is-light is-fullheight">
        <div class="p-5 sidebar has-text-left">
          <h1 class="title">Dashboard</h1>
          <form>
            <!-- COUNTRY -->
            <b class="is-size-5">Country</b>
            <b-field class="my-2">
              <b-checkbox v-model="country"
                native-value="New Zealand">
                New Zealand
              </b-checkbox>
            </b-field>
            <b-field class="my-2">
              <b-checkbox v-model="country"
                native-value="Japan">
                Japan
              </b-checkbox>
            </b-field>
            
            <!-- DATE -->
            <b class="is-size-5">Date</b>
            <p>Start Date</p>
            <b-field class="my-2">
              <b-datepicker
                editable
                :locale="locale"
                type="month"
                placeholder="Start Date"
                icon="calendar-today"
                v-model="startDate"
                trap-focus>
              </b-datepicker>
            </b-field>
            <p>End Date</p>
            <b-field class="my-2">
              <b-datepicker
                editable
                :locale="locale"
                type="month"
                placeholder="End Date"
                icon="calendar-today"
                v-model="endDate"
                trap-focus>
              </b-datepicker>
            </b-field>

            <!-- FIELDS -->
            <b class="is-size-5">Fields</b>
            <b-field class="my-2">
              <b-checkbox v-model="field"
                native-value="imports">
                Imports
              </b-checkbox>
            </b-field>
            <b-field class="my-2">
              <b-checkbox v-model="field"
                native-value="exports">
                Exports
              </b-checkbox>
            </b-field>
            <b-field class="my-2">
              <b-checkbox v-model="field"
                native-value="demand">
                Demand
              </b-checkbox>
            </b-field>
            <b-field class="my-2">
              <b-checkbox v-model="field"
                native-value="supply">
                Supply
              </b-checkbox>
            </b-field>
            <b-field class="my-2">
              <b-checkbox v-model="field"
                native-value="netBalance">
                Net Balance
              </b-checkbox>
            </b-field>
            <b-field class="my-2">
              <b-checkbox v-model="field"
                native-value="grossBalance">
                Gross Balance
              </b-checkbox>
            </b-field>
            <b-field class="my-2">
              <b-checkbox v-model="field"
                native-value="productYield">
                Product Yield
              </b-checkbox>
            </b-field>
            <b-field class="my-2">
              <b-checkbox v-model="field"
                native-value="netImports">
                Net Import
              </b-checkbox>
            </b-field>
            <b-field class="my-2">
              <b-checkbox v-model="field"
                native-value="supplyMeter">
                Supply Meter
              </b-checkbox>
            </b-field>
            <b-field class="my-2">
              <b-checkbox v-model="field"
                native-value="shipmentTotal">
                Shipment Total
              </b-checkbox>
            </b-field>
          </form>
        </div>
      </div>
      
      <!-- DISPLAY -->
      <div class="column is-four-fifths">
        <div class="box">
          <b class="is-size-5">Start Date</b> 
          &emsp;
          <span class="is-size-5">{{ parsedStartDate }}</span>
          &emsp;&emsp;
          <b class="is-size-5">End Date</b>
          &emsp;
          <span class="is-size-5">{{ parsedEndDate }}</span>
        </div>
        <Viz
          v-for="(f, index) in field"
          :key="index"
          :country="country"
          :field="f"
          :startDate="parsedStartDate"
          :endDate="parsedEndDate"
          >
        </Viz>
      </div>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import Viz from '@/components/Viz.vue'

export default {
  name: 'Home',
  components: {
    Viz,
  },
  data() {
    return {
      country: ["New Zealand", "Japan"],
      field: ["imports", "exports", "demand", "supply"],
      startDate: new Date(2020, 7),
      endDate: new Date(2021, 7),
      locale: 'en-CA'
    }
  },
  computed: {
    parsedStartDate() {
      var d = new Date(this.startDate),
        month = '' + (d.getMonth() + 1),
        year = d.getFullYear();

      if (month.length < 2) 
        month = '0' + month;

      return [year, month].join('-');
    },
    parsedEndDate() {
      var d = new Date(this.endDate),
        month = '' + (d.getMonth() + 1),
        year = d.getFullYear();

      if (month.length < 2) 
        month = '0' + month;

      return [year, month].join('-');
    },
  }
}
</script>

<style scoped>
.sidebar {
  position: fixed;
}
</style>