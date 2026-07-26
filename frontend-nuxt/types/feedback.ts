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

export interface SkillAnswer {
  questionId: string | number
  rating: number
}

export interface QuestionnaireQuestion {
  id: string | number
  text: string
}

export interface QuestionnaireCategory {
  id: string | number
  code: string
  name: string
  description?: string
  questions: QuestionnaireQuestion[]
}

export interface QuestionnaireData {
  urlToken: string
  targetName?: string
  categories: QuestionnaireCategory[]
}

export interface SubmitQuestionnairePayload {
  skillAnswers: SkillAnswer[]
  extraAnswers?: {
    comments?: string
  }
}
