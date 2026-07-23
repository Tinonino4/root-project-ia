<script setup>
import { computed } from 'vue';
import { Radar } from 'vue-chartjs';
import {
  Chart as ChartJS,
  RadialLinearScale,
  PointElement,
  LineElement,
  Filler,
  Tooltip,
  Legend
} from 'chart.js';

ChartJS.register(
  RadialLinearScale,
  PointElement,
  LineElement,
  Filler,
  Tooltip,
  Legend
);

import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps({
  metrics: {
    type: Object,
    required: true
  }
});

const chartData = computed(() => {
  return {
    labels: [
      t('questionnaire.categories.TEAMWORK.name'),
      t('questionnaire.categories.PROACTIVITY.name'),
      t('questionnaire.categories.INTEGRITY.name'),
      t('questionnaire.categories.SELF_CONFIDENCE.name'),
      t('questionnaire.categories.FLEXIBILITY.name')
    ],
    datasets: [
      {
        label: t('dashboard.metrics.verifiedSkills'),
        backgroundColor: 'rgba(242, 151, 39, 0.2)',
        borderColor: 'rgba(242, 151, 39, 1)',
        pointBackgroundColor: 'rgba(242, 151, 39, 1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(242, 151, 39, 1)',
        borderWidth: 2,
        data: [
          props.metrics.teamwork || 0,
          props.metrics.proactivity || 0,
          props.metrics.integrity || 0,
          props.metrics.selfConfidence || 0,
          props.metrics.flexibility || 0
        ]
      }
    ]
  };
});

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    r: {
      angleLines: {
        color: 'rgba(150, 150, 150, 0.2)'
      },
      grid: {
        color: 'rgba(150, 150, 150, 0.2)'
      },
      pointLabels: {
        color: '#888', // This will be adjusted via CSS if possible, or left generic. Chart.js doesn't natively support dynamic CSS vars easily without a watcher
        font: {
          family: 'Inter',
          size: 12,
          weight: '600'
        }
      },
      min: 0,
      max: 5,
      ticks: {
        display: false,
        stepSize: 1
      }
    }
  },
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      backgroundColor: 'rgba(22, 24, 32, 0.9)',
      titleFont: { family: 'Inter', size: 12 },
      bodyFont: { family: 'Inter', size: 13, weight: 'bold' },
      padding: 10,
      cornerRadius: 8,
      displayColors: false,
      callbacks: {
        label: function(context) {
          return context.raw.toFixed(1) + ' / 5.0';
        }
      }
    }
  }
};
</script>

<template>
  <div class="relative w-full max-w-sm mx-auto aspect-square flex items-center justify-center">
    <Radar :data="chartData" :options="chartOptions" />
  </div>
</template>
