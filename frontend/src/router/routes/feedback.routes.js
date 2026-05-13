export default [
  {
    path: '/feedback',
    name: 'FeedbackList',
    component: () => import('@/views/feedback/FeedbackListView.vue'),
    meta: { layout: 'DefaultLayout', auth: true }
  },
  {
    path: '/feedback/new',
    name: 'FeedbackCreate',
    component: () => import('@/views/feedback/FeedbackCreateView.vue'),
    meta: { layout: 'DefaultLayout', auth: true }
  }
];
