import { defineStore } from 'pinia'
import api from '../services/api'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

export const useAuthStore = defineStore('auth', () => {
  const storedToken = localStorage.getItem('token')
  const token = ref(storedToken === 'null' || storedToken === 'undefined' ? null : storedToken)
  const user = ref(null)
  const router = useRouter()

  const setToken = (newToken) => {
    token.value = newToken
    if (newToken) {
      localStorage.setItem('token', newToken)
    } else {
      localStorage.removeItem('token')
    }
  }

  const login = async (email, password) => {
    try {
      const response = await api.post('/auth/login', { email, password })
      setToken(response.data.token)
      router.push('/')
      return true
    } catch (error) {
      console.error('Error logging in:', error)
      throw error
    }
  }

  const register = async (email, password) => {
    try {
      await api.post('/auth/register', { email, password })
      // Auto login after register
      return await login(email, password)
    } catch (error) {
      console.error('Error registering:', error)
      throw error
    }
  }

  const logout = () => {
    setToken(null)
    user.value = null
    router.push('/login')
  }

  return { token, user, setToken, login, register, logout }
})
