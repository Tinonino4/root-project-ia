export default [
  {
    path: '/q/:token',
    name: 'Questionnaire',
    component: () => import('@/views/questionnaire/QuestionnaireView.vue'),
    meta: { layout: 'PublicLayout' }
  }
];
