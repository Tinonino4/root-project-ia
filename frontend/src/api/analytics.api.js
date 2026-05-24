import client from './client';

export const analyticsApi = {
  getMetrics: () => client.get('/skills/metrics'),
};
