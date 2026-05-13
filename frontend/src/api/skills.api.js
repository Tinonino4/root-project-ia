import client from './client';

export const skillsApi = {
  getMetrics: () => client.get('/skills/metrics'),
};
