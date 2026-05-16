import client from './client';

export const feedbackApi = {
  getCategories: () => client.get('/v1/feedback/categories'),
  getRelationships: () => client.get('/v1/feedback/relationships'),
  createRequest: (data) => client.post('/v1/feedback/requests', data),
  getRequests: () => client.get('/v1/feedback/requests'),
  getRequestsByExperience: (experienceId) => client.get(`/v1/feedback/requests/experience/${experienceId}`),
  getCompletedCountByExperience: (experienceId) => client.get(`/v1/feedback/requests/experience/${experienceId}/count`),
};
