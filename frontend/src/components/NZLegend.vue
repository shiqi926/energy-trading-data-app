<template>
    <div>
        <span v-for="(value, key, index) in colorList" :key=index class="leg">
            <span class="square" :style="{ 'background-color': value }"></span>
            <span class="label">{{key}}</span>
        </span>
    </div>
</template>

<script>
export default {
  name: 'NZLegend',
  data() {
    return {
      colorList: {'Crude Oil, Condensates, Naphtha': '#1f77b4',
                'LPG': '#ff7f0e',
                'Gasoline': '#2ca02c',
                'Aviation Fuel': '#d62728',
                'Diesel': '#9467bd',
                'Fuel Oil': '#8c564b'
                }
    }
  }
}
</script>

<style scoped>
.leg {
    display: inline-flex;
}

.square {
    height: 10px;
    width: 10px;
    margin: 10px;
    object-position: 50% 50%;
}

.label {
    font-size: 0.6em;
    height: 30px;
    line-height: 30px;
}
</style>