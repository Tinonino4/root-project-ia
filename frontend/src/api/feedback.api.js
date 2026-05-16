import client from './client';

export const feedbackApi = {
  getCategories: () => client.get('/feedback/categories'),
  getRelationships: () => client.get('/feedback/relationships'),
  createRequest: (data) => client.post('/feedback/requests', data),
  getRequests: () => client.get('/feedback/requests'),
  getRequestsByExperience: (experienceId) => client.get(`/feedback/requests/experience/${experienceId}`),
  getCompletedCountByExperience: (experienceId) => client.get(`/feedback/requests/experience/${experienceId}/count`),
};
