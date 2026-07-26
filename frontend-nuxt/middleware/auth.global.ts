export default defineNuxtRouteMiddleware((to) => {
  const token = useCookie<string | null>('token')
  const lastActivity = useCookie<string | null>('lastActivity')

  const INACTIVITY_TIMEOUT = 12 * 60 * 60 * 1000

  // Public paths
  const isPublicRoute = 
    to.path === '/' ||
    to.path === '/login' ||
    to.path === '/register' ||
    to.path.startsWith('/auth/') ||
    to.path.startsWith('/oauth2/') ||
    to.path.startsWith('/q/') ||
    to.path.startsWith('/u/') ||
    to.path.startsWith('/profile/public/')

  // Check 12h inactivity timeout
  if (token.value && lastActivity.value) {
    const last = parseInt(lastActivity.value, 10)
    if (!isNaN(last) && Date.now() - last > INACTIVITY_TIMEOUT) {
      token.value = null
      useCookie('refreshToken').value = null
      useCookie('user').value = null
      lastActivity.value = null
      if (!isPublicRoute) {
        return navigateTo('/login')
      }
    } else if (import.meta.client) {
      lastActivity.value = Date.now().toString()
    }
  }

  // Redirect unauthenticated users
  if (!token.value && !isPublicRoute) {
    return navigateTo('/login')
  }

  // Redirect authenticated users away from guest pages
  if (token.value && (to.path === '/login' || to.path === '/register')) {
    return navigateTo('/dashboard')
  }
})
