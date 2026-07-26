export interface SkillsMetrics {
  teamwork?: number
  proactivity?: number
  integrity?: number
  selfConfidence?: number
  flexibility?: number
  averageScore?: number
  [key: string]: number | undefined
}

export interface CategoryAverage {
  TEAMWORK?: number
  SELF_CONFIDENCE?: number
  PROACTIVITY?: number
  INTEGRITY?: number
  FLEXIBILITY?: number
  [key: string]: number | undefined
}

export interface Testimonial {
  createdAt?: string
  relationshipCode: string
  trustScore: number
  comment: string
}

export interface ExperienceMetric {
  experienceId: number | string
  averageScore: number
  referencesCount: number
  averageTrustScore: number
  categoryAverages?: CategoryAverage
  relationshipCounts?: Record<string, number>
  testimonials?: Testimonial[]
}

export interface Experience {
  id: number | string
  position: string
  companyName: string
  department?: string
  startDate: string
  finishDate?: string | null
  functions?: string
  description?: string
  isCurrent?: boolean
  createdAt?: string
  updatedAt?: string
}

export interface UserProfile {
  id?: number | string
  userId?: number | string
  username?: string
  name: string
  surname?: string
  jobTitle?: string
  aboutMe?: string
  bio?: string
  headline?: string
  photoUrl?: string
  avatarUrl?: string
  totalReferencesCount?: number
  skills?: SkillsMetrics
  experiences?: Experience[]
  experienceMetrics?: ExperienceMetric[]
}

export interface UpdateProfilePayload {
  name?: string
  surname?: string
  jobTitle?: string
  aboutMe?: string
  bio?: string
  photoUrl?: string
}

export interface AddExperiencePayload {
  position: string
  companyName: string
  department?: string
  startDate: string
  finishDate?: string | null
  functions?: string
}

export interface TopSkill {
  key: string
  categoryKey: string
}
