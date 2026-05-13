import client from './client';

export const questionnaireApi = {
  getQuestionnaire: (urlToken) => client.get(`/questionnaire/${urlToken}`),
  submitQuestionnaire: (urlToken, data) => client.post(`/questionnaire/${urlToken}`, data),
};
