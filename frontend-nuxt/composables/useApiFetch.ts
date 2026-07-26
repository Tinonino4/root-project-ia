import { toast } from 'vue-sonner'

export function useApiFetch<T = any>(url: string | Ref<string> | (() => string), opts: any = {}) {
  const config = useRuntimeConfig()
  const token = useCookie<string | null>('token', TOKEN_COOKIE_OPTIONS)
  const refreshToken = useCookie<string | null>('refreshToken', REFRESH_TOKEN_COOKIE_OPTIONS)
  const lastActivity = useCookie<string | null>('lastActivity', LAST_ACTIVITY_COOKIE_OPTIONS)

  const INACTIVITY_TIMEOUT = 12 * 60 * 60 * 1000 // 12 hours

  // Check inactivity
  if (import.meta.client && token.value && lastActivity.value) {
    const last = parseInt(lastActivity.value, 10)
    if (isNaN(last) || Date.now() - last > INACTIVITY_TIMEOUT) {
      token.value = null
      refreshToken.value = null
      lastActivity.value = null
      useCookie('user', USER_COOKIE_OPTIONS).value = null
      navigateTo('/login')
    } else {
      lastActivity.value = Date.now().toString()
    }
  }

  const defaultHeaders: Record<string, string> = {}
  if (token.value) {
    defaultHeaders['Authorization'] = `Bearer ${token.value}`
  }

  const baseURL = config.public.apiBaseUrl || '/api'

  return useFetch<T>(url, {
    baseURL,
    headers: {
      ...defaultHeaders,
      ...opts.headers,
    },
    async onResponseError({ response }) {
      const status = response.status
      if (status === 401) {
        // Attempt refresh
        if (refreshToken.value) {
          try {
            const refreshRes = await $fetch<{ accessToken: string; refreshToken?: string }>(`${baseURL}/auth/refresh-token`, {
              method: 'POST',
              body: { refreshToken: refreshToken.value }
            })
            if (refreshRes.accessToken) {
              token.value = refreshRes.accessToken
              if (refreshRes.refreshToken) {
                refreshToken.value = refreshRes.refreshToken
              }
              // Retry can be triggered
              return
            }
          } catch (e) {
            token.value = null
            refreshToken.value = null
            lastActivity.value = null
            useCookie('user', USER_COOKIE_OPTIONS).value = null
            navigateTo('/login')
          }
        } else {
          token.value = null
          lastActivity.value = null
          useCookie('user', USER_COOKIE_OPTIONS).value = null
          navigateTo('/login')
        }
      } else {
        const serverMessage = response._data?.message || response._data?.error || null
        if (status === 400) {
          toast.error('Solicitud incorrecta', { description: serverMessage || 'Revisa los datos enviados.' })
        } else if (status === 403) {
          toast.error('Acceso denegado', { description: 'No tienes permisos para realizar esta acción.' })
        } else if (status === 404) {
          toast.error('Recurso no encontrado', { description: serverMessage || 'El recurso no existe.' })
        } else if (status === 409) {
          toast.error('Conflicto', { description: serverMessage || 'Conflicto de datos.' })
        } else if (status === 422) {
          toast.error('Datos inválidos', { description: serverMessage || 'Revisa los campos.' })
        } else if (status >= 500) {
          toast.error('Error del servidor', { description: 'Ha ocurrido un error inesperado.' })
        }
      }
    },
    ...opts,
  })
}

export async function $api<T = any>(url: string, opts: any = {}): Promise<T> {
  const config = useRuntimeConfig()
  const token = useCookie<string | null>('token', TOKEN_COOKIE_OPTIONS)
  const refreshToken = useCookie<string | null>('refreshToken', REFRESH_TOKEN_COOKIE_OPTIONS)
  const lastActivity = useCookie<string | null>('lastActivity', LAST_ACTIVITY_COOKIE_OPTIONS)

  const INACTIVITY_TIMEOUT = 12 * 60 * 60 * 1000

  if (import.meta.client && token.value && lastActivity.value) {
    const last = parseInt(lastActivity.value, 10)
    if (isNaN(last) || Date.now() - last > INACTIVITY_TIMEOUT) {
      token.value = null
      refreshToken.value = null
      lastActivity.value = null
      useCookie('user', USER_COOKIE_OPTIONS).value = null
      navigateTo('/login')
      throw new Error('Sesión expirada')
    } else {
      lastActivity.value = Date.now().toString()
    }
  }

  const defaultHeaders: Record<string, string> = {}
  if (token.value) {
    defaultHeaders['Authorization'] = `Bearer ${token.value}`
  }

  const baseURL = config.public.apiBaseUrl || '/api'

  try {
    return await $fetch<T>(url, {
      baseURL,
      headers: {
        ...defaultHeaders,
        ...opts.headers,
      },
      ...opts,
    })
  } catch (error: any) {
    const response = error.response
    if (response) {
      const status = response.status
      if (status === 401 && !opts._retry) {
        if (refreshToken.value) {
          try {
            const refreshRes = await $fetch<{ accessToken: string; refreshToken?: string }>(`${baseURL}/auth/refresh-token`, {
              method: 'POST',
              body: { refreshToken: refreshToken.value }
            })
            if (refreshRes.accessToken) {
              token.value = refreshRes.accessToken
              if (refreshRes.refreshToken) {
                refreshToken.value = refreshRes.refreshToken
              }
              return await $fetch<T>(url, {
                baseURL,
                headers: {
                  ...defaultHeaders,
                  Authorization: `Bearer ${refreshRes.accessToken}`,
                  ...opts.headers,
                },
                ...opts,
                _retry: true,
              })
            }
          } catch (e) {
            token.value = null
            refreshToken.value = null
            lastActivity.value = null
            useCookie('user', USER_COOKIE_OPTIONS).value = null
            navigateTo('/login')
            throw e
          }
        } else {
          token.value = null
          lastActivity.value = null
          useCookie('user', USER_COOKIE_OPTIONS).value = null
          navigateTo('/login')
        }
      } else {
        const serverMessage = response._data?.message || response._data?.error || null
        if (status === 400) {
          toast.error('Solicitud incorrecta', { description: serverMessage || 'Revisa los datos enviados.' })
        } else if (status === 403) {
          toast.error('Acceso denegado', { description: 'No tienes permisos para realizar esta acción.' })
        } else if (status === 404) {
          toast.error('Recurso no encontrado', { description: serverMessage || 'El recurso no existe.' })
        } else if (status === 409) {
          toast.error('Conflicto', { description: serverMessage || 'Conflicto de datos.' })
        } else if (status === 422) {
          toast.error('Datos inválidos', { description: serverMessage || 'Revisa los campos.' })
        } else if (status >= 500) {
          toast.error('Error del servidor', { description: 'Ha ocurrido un error inesperado.' })
        }
      }
    }
    throw error
  }
}
