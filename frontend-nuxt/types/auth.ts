export interface User {
  id?: number | string
  name?: string
  surname?: string
  email?: string
  role?: string
  jobTitle?: string
  photoUrl?: string
  avatarUrl?: string
  bio?: string
  headline?: string
  username?: string
  verified?: boolean
  createdAt?: string
  updatedAt?: string
}

export interface LoginCredentials {
  email: string
  password: string
}

export interface RegisterData {
  name: string
  email: string
  password: string
  role?: string
}

export interface AuthResponse {
  id?: number | string
  token: string
  accessToken?: string
  refreshToken?: string
  name?: string
  email?: string
  role?: string
  user?: User
}

export interface ConfirmAccountPayload {
  email: string
  code: string
}

export interface ForgotPasswordPayload {
  email: string
}

export interface ResetPasswordPayload {
  email: string
  code: string
  newPassword: string
}

export interface RefreshTokenResponse {
  accessToken: string
  refreshToken?: string
}
