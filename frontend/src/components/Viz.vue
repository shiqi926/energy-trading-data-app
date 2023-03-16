<template>
  <div class="tile">
    <div class="tile is-parent is-vertical" v-if="!jpFields.includes(field) && country.includes('New Zealand')">
      <article class="tile is-child notification is-primary is-light">
        <p class="subtitle"><b>New Zealand - {{ allFields[field] }}</b></p>
        <D3LineChart :config="chart_config" :datum="nz"></D3LineChart>
        <NZLegend></NZLegend>
      </article>
    </div>
    <div class="tile is-parent is-vertical" v-if="!nzFields.includes(field) && country.includes('Japan')">
      <article class="tile is-child notification is-primary is-light">
        <p class="subtitle"><b>Japan - {{ allFields[field] }}</b></p>
        <D3LineChart :config="chart_config2" :datum="jp"></D3LineChart>
        <JPLegend></JPLegend>
      </article>
    </div>
  </div>
</template>

<script>
import { D3LineChart } from 'vue-d3-charts';
import JPLegend from './JPLegend.vue';
import NZLegend from './NZLegend.vue';

export default {
  props: ['country','field','startDate','endDate'],
  components: {
    D3LineChart,
    JPLegend,
    NZLegend
  },
  data() {
    return {
      allFields: {
        imports: 'Imports (thousand metric tonnes)',
        exports: 'Exports (thousand metric tonnes)',
        demand: 'Demand (thousand metric tonnes)',
        supply: 'Supply (thousand metric tonnes)',
        netBalance: 'Net Balance (thousand metric tonnes)',
        grossBalance: 'Gross Balance (thousand metric tonnes)',
        productYield: 'Product Yield (%)',
        netImports: 'Net Import (thousand metric tonnes)',
        supplyMeter: 'Supply Meter (thousand metric tonnes)',
        shipmentTotal: 'Shipment Total (thousand metric tonnes)',
      },
      nzFields: ["netBalance", "productYield", "netImports"],
      jpFields: ["supplyMeter", "shipmentTotal"],
      columns: [
        {
          field: 'date',
          label: 'Date'
        },
        {
          field: 'butane',
          label: 'Butane'
        },
        {
          field: 'propane',
          label: 'Propane'
        }
      ],
      nz: [],
      chart_config: {
        values: ['crudeOilCondensatesNaphtha','lpg','gasoline','aviation','diesel','fuelOil'],
        date: {
          key: 'date',
          inputFormat: '%Y-%m',
          outputFormat: '%Y-%m',
        },
        color: { scheme: "schemeCategory10", current: "red" },
        points: {
          visibleSize: 3,
          hoverSize: 6,
        },
        axis: {
          yTicks: 8,
          yFormat: ".1f"
        },
        tooltip: {
          labels: ['Crude Oil, Condensates, Naphtha','LPG','Gasoline','Aviation Fuel','Diesel','Fuel Oil'],
        }
      },
      jp: [],
      chart_config2: {
        values: ['butane','propane'],
        date: {
          key: 'date',
          inputFormat: '%Y-%m',
          outputFormat: '%Y-%m',
        },
        color: { scheme: "schemeCategory10", current: "red" },
        points: {
          visibleSize: 3,
          hoverSize: 6,
        },
        axis: {
          yTicks: 8,
          yFormat: ".1f"
        },
        tooltip: {
          labels: ['Butane', 'Propane'],
        }
      },
    }
  },
  methods: {
    loadData() {
      var body = {
        "startDate": this.startDate,
        "endDate": this.endDate,
      }

      // fetch(`http://localhost:8080/nz/data/between/`, {
      fetch(`https://oliveproject.herokuapp.com/nz/data/between/`, {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
          'Content-Type': 'application/json'
        }
      }).then((res) => {
        return res.json().then((data) => {
          if (res.status == 200) {
            var dates = data.data.map(obj => obj.date)
            var extracted = data.data.map(obj => obj[this.field])
            for (let index = 0; index < dates.length; index++) {
              let date = new Date(dates[index]),
                month = '' + (date.getMonth() + 1),
                year = date.getFullYear();

              if (month.length < 2) 
                month = '0' + month;
              
              let d = [year, month].join('-')
              extracted[index]["date"] = d

              if (this.field != "productYield") {
                let tempExtracted = extracted[index]
                for (const key in tempExtracted){
                  if (key != "date") {
                    extracted[index][key] = extracted[index][key] / 1000
                  }
                }
              } else {
                console.log(extracted[index])
              }
            }
            this.nz = extracted
          }
        })
      }).catch(console.error)

      // fetch(`http://localhost:8080/jp/data/between/`, {
      fetch(`https://oliveproject.herokuapp.com/jp/data/between/`, {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
          'Content-Type': 'application/json'
        }
      }).then((res) => {
        return res.json().then((data) => {
          if (res.status == 200) {
            this.jp = []
            var dates = data.data.map(obj => obj.date)
            var butane = data.data.map(obj => obj.butane[this.field])
            var propane = data.data.map(obj => obj.propane[this.field])
            for (let index = 0; index < dates.length; index++) {
              let date = new Date(dates[index]),
                month = '' + (date.getMonth()),
                year = date.getFullYear();

              if (month.length < 2) 
                month = '0' + month;
              
              let d = [year, month].join('-')

              let japanMonth = {}
              japanMonth["date"] = d
              japanMonth["butane"] = butane[index] / 1000
              japanMonth["propane"] = propane[index] / 1000

              this.jp.push(japanMonth)
            }
          }
        })
      }).catch(console.error)
    }
  },
  watch: {
    startDate: function() {
      this.loadData()
    },
    endDate: function() {
      this.loadData()
    },
  },
  created() {
    this.loadData()
  }
}
</script>
