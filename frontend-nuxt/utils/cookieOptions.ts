export const DEFAULT_SESSION_MAX_AGE = 12 * 60 * 60 // 12 hours in seconds
export const REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60 // 7 days in seconds

export const getCookieOptions = <T = string>(maxAgeSeconds: number = DEFAULT_SESSION_MAX_AGE) => ({
  sameSite: 'lax' as const,
  secure: process.env.NODE_ENV === 'production',
  maxAge: maxAgeSeconds,
  path: '/',
})

export const TOKEN_COOKIE_OPTIONS = getCookieOptions(DEFAULT_SESSION_MAX_AGE)
export const REFRESH_TOKEN_COOKIE_OPTIONS = getCookieOptions(REFRESH_TOKEN_MAX_AGE)
export const USER_COOKIE_OPTIONS = getCookieOptions(DEFAULT_SESSION_MAX_AGE)
export const LAST_ACTIVITY_COOKIE_OPTIONS = getCookieOptions(DEFAULT_SESSION_MAX_AGE)
