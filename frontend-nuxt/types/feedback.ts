export interface FeedbackCategory {
  id: number | string
  code: string
  name: string
  description?: string
}

export interface FeedbackRelationship {
  id: number | string
  code: string
  name: string
  description?: string
}

export interface FeedbackRequest {
  id: number | string
  evaluatorEmail: string
  evaluatorName?: string
  relationshipCode?: string
  experienceId?: number | string
  status?: 'PENDING' | 'COMPLETED' | 'CANCELLED'
  visible?: boolean
  urlToken?: string
  createdAt?: string
  updatedAt?: string
  targetName?: string
  targetSurname?: string
  targetEmail?: string
  finished?: boolean
  trustScore?: number
}

export interface CreateFeedbackRequestPayload {
  targetName: string
  targetSurname: string
  targetEmail: string
  targetPhone?: string
  relationshipId: number
  experienceId: string
  stillWorksThere: boolean
  extraAnswers?: boolean
  emailLanguage?: string
}

export interface ToggleVisibilityPayload {
  visible: boolean
}

export interface BehavioralOptionDTO {
  id: string
  code: string
  text: string
  position: number
}

export interface BehavioralQuestionDTO {
  id: string
  code: string
  type: 'BARS' | 'FORCED_CHOICE' | 'CULTURAL_FIT'
  text: string
  position: number
  options: BehavioralOptionDTO[]
}

export interface QuestionnaireData {
  cacheRequestId: string
  userId: string
  candidateName?: string
  experienceId: string
  companyName?: string
  relationshipTypeId: number
  relationshipCode: string
  questions: BehavioralQuestionDTO[]
}

export interface BehavioralAnswerPayload {
  questionId: string
  selectedOptionIds: string[]
}

export interface SubmitQuestionnairePayload {
  answers: BehavioralAnswerPayload[]
  comments?: string
  extraAnswers?: Record<string, any>
}
