import { createRouter, createWebHistory } from 'vue-router';
import { setupGuards } from './guards';

import publicRoutes from './routes/public.routes';
import authRoutes from './routes/auth.routes';
import dashboardRoutes from './routes/dashboard.routes';
import profileRoutes from './routes/profile.routes';
import experienceRoutes from './routes/experience.routes';
import feedbackRoutes from './routes/feedback.routes';
import questionnaireRoutes from './routes/questionnaire.routes';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    ...publicRoutes,
    ...authRoutes,
    ...dashboardRoutes,
    ...profileRoutes,
    ...experienceRoutes,
    ...feedbackRoutes,
    ...questionnaireRoutes,
  ],
  scrollBehavior(to, from, savedPosition) {
    if (to.hash) {
      return {
        el: to.hash,
        behavior: 'smooth',
      };
    }
    if (savedPosition) {
      return savedPosition;
    }
    return { top: 0 };
  }
});

setupGuards(router);

export default router;
