import { toast } from 'vue-sonner'
import type { UseFetchOptions } from 'nuxt/app'
import type { FetchOptions, FetchError } from 'ofetch'

export function useApiFetch<T = unknown>(
  url: string | Ref<string> | (() => string),
  opts: UseFetchOptions<T> & { _retry?: boolean } = {}
) {
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
  const { headers: customHeaders, ...restOpts } = opts

  return useFetch<T>(url as any, {
    baseURL,
    headers: {
      ...defaultHeaders,
      ...(customHeaders as Record<string, string> | undefined),
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
              return
            }
          } catch {
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
        const serverMessage = (response._data as any)?.message || (response._data as any)?.error || null
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
    ...(restOpts as any),
  })
}

export async function $api<T = unknown>(
  url: string,
  opts: FetchOptions & { _retry?: boolean } = {}
): Promise<T> {
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
  const { headers: customHeaders, _retry, ...restOpts } = opts

  try {
    return (await $fetch(url as any, {
      baseURL,
      headers: {
        ...defaultHeaders,
        ...(customHeaders as Record<string, string> | undefined),
      },
      ...(restOpts as any),
    } as any)) as unknown as T
  } catch (error: unknown) {
    const fetchError = error as FetchError
    const response = fetchError.response
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
              return (await $fetch(url as any, {
                baseURL,
                headers: {
                  ...defaultHeaders,
                  Authorization: `Bearer ${refreshRes.accessToken}`,
                  ...(customHeaders as Record<string, string> | undefined),
                },
                ...(restOpts as any),
                _retry: true,
              } as any)) as unknown as T
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
      } else if (import.meta.client) {
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
