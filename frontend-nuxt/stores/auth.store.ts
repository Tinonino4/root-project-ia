import { defineStore, skipHydrate } from 'pinia'
import { ref, computed } from 'vue'
import type { User, AuthResponse, RefreshTokenResponse } from '~/types'

export type { User }

export const useAuthStore = defineStore('auth', () => {
  const token = useCookie<string | null>('token', { default: () => null, ...TOKEN_COOKIE_OPTIONS })
  const refreshToken = useCookie<string | null>('refreshToken', { default: () => null, ...REFRESH_TOKEN_COOKIE_OPTIONS })
  const user = useCookie<User | null>('user', { default: () => null, ...USER_COOKIE_OPTIONS })
  const lastActivity = useCookie<string | null>('lastActivity', { default: () => null, ...LAST_ACTIVITY_COOKIE_OPTIONS })

  const loading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => !!token.value)
  const userName = computed(() => user.value?.name || '')
  const userRole = computed(() => user.value?.role || '')

  function setAuth(userData: User | null, authToken: string | null, refreshAuthToken?: string | null) {
    token.value = authToken
    user.value = userData
    if (authToken) {
      lastActivity.value = Date.now().toString()
    }
    if (refreshAuthToken !== undefined) {
      refreshToken.value = refreshAuthToken
    }
  }

  function logout() {
    token.value = null
    refreshToken.value = null
    user.value = null
    lastActivity.value = null
  }

  async function login(email: string, password: string) {
    loading.value = true
    error.value = null
    try {
      const response = await $api<AuthResponse>('/auth/login', {
        method: 'POST',
        body: { email, password }
      })
      setAuth(
        { id: response.id, name: response.name, role: response.role },
        response.token,
        response.refreshToken
      )
      return response
    } catch (e: unknown) {
      const err = e as { response?: { _data?: { message?: string } }; message?: string }
      error.value = err.response?._data?.message || err.message || 'Error de inicio de sesión'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function register(name: string, email: string, password: string, role?: string) {
    loading.value = true
    error.value = null
    try {
      const response = await $api<AuthResponse>('/auth/register', {
        method: 'POST',
        body: { name, email, password, role }
      })
      return response
    } catch (e: unknown) {
      const err = e as { response?: { _data?: { message?: string } }; message?: string }
      error.value = err.response?._data?.message || err.message || 'Error en el registro'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function confirmAccount(email: string, code: string) {
    loading.value = true
    error.value = null
    try {
      const response = await $api<{ message?: string }>('/auth/confirm', {
        method: 'POST',
        body: { email, code }
      })
      return response
    } catch (e: unknown) {
      const err = e as { response?: { _data?: { message?: string } }; message?: string }
      error.value = err.response?._data?.message || err.message || 'Error al confirmar la cuenta'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function forgotPassword(email: string) {
    loading.value = true
    error.value = null
    try {
      const response = await $api<{ message?: string }>('/auth/forgot-password', {
        method: 'POST',
        body: { email }
      })
      return response
    } catch (e: unknown) {
      const err = e as { response?: { _data?: { message?: string } }; message?: string }
      error.value = err.response?._data?.message || err.message || 'Error al solicitar recuperación'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function resetPassword(email: string, code: string, newPassword: string) {
    loading.value = true
    error.value = null
    try {
      const response = await $api<{ message?: string }>('/auth/reset-password', {
        method: 'POST',
        body: { email, code, newPassword }
      })
      return response
    } catch (e: unknown) {
      const err = e as { response?: { _data?: { message?: string } }; message?: string }
      error.value = err.response?._data?.message || err.message || 'Error al resetear la contraseña'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function refreshSession() {
    if (!refreshToken.value) {
      logout()
      throw new Error('No refresh token available')
    }
    try {
      const response = await $api<RefreshTokenResponse>('/auth/refresh-token', {
        method: 'POST',
        body: { refreshToken: refreshToken.value }
      })
      token.value = response.accessToken
      if (response.refreshToken) {
        refreshToken.value = response.refreshToken
      }
      return response
    } catch (e) {
      logout()
      throw e
    }
  }

  async function fetchCurrentUser(authToken?: string) {
    loading.value = true
    error.value = null
    try {
      const currentToken = authToken || token.value
      const headers: Record<string, string> = {}
      if (currentToken) {
        headers['Authorization'] = `Bearer ${currentToken}`
      }
      const response = await $api<User>('/professional/profile', { headers })
      user.value = response
      return response
    } catch (e: unknown) {
      const err = e as { response?: { _data?: { message?: string; error?: string } } }
      error.value = err.response?._data?.message || err.response?._data?.error || 'Error al obtener los datos del perfil de usuario.'
      logout()
      throw e
    } finally {
      loading.value = false
    }
  }

  return {
    token: skipHydrate(token),
    refreshToken: skipHydrate(refreshToken),
    user: skipHydrate(user),
    lastActivity: skipHydrate(lastActivity),
    loading,
    error,
    isAuthenticated,
    userName,
    userRole,
    login,
    register,
    confirmAccount,
    forgotPassword,
    resetPassword,
    refreshSession,
    fetchCurrentUser,
    setAuth,
    logout,
  }
})
