<template>
  <div class="auth-container">
    <div class="auth-card">
      <h1 class="auth-title">Create Account</h1>
      <p class="auth-subtitle">Join us today to get started</p>

      <form @submit.prevent="handleRegister" class="auth-form">
        <div class="input-group">
          <label for="email">Email</label>
          <input id="email" v-model="email" type="email" placeholder="name@example.com" required />
        </div>
        <div class="input-group">
          <label for="password">Password</label>
          <input id="password" v-model="password" type="password" placeholder="••••••••" required />
        </div>
        <!-- In a real app we would add confirm password -->

        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? 'Creating...' : 'Sign Up' }}
        </button>
        <p v-if="error" class="error-msg">{{ error }}</p>
      </form>

      <div class="divider">
        <span>OR</span>
      </div>

      <div class="social-login">
        <a href="http://localhost:8080/oauth2/authorization/google" class="btn-social google">
          <svg viewBox="0 0 24 24" width="24" height="24" xmlns="http://www.w3.org/2000/svg"><path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/><path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84A10.99 10.99 0 0012 23z" fill="#34A853"/><path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/><path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/></svg>
          Sign Up with Google
        </a>
      </div>

      <p class="auth-redirect">
        Already have an account? <router-link to="/login">Log in</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../stores/auth'

const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)
const authStore = useAuthStore()

const handleRegister = async () => {
  error.value = ''
  loading.value = true
  try {
    await authStore.register(email.value, password.value)
  } catch (err) {
    error.value = err.response?.data?.body || 'Registration failed. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1e1e2f 0%, #2a2a40 100%);
  font-family: 'Inter', sans-serif;
}

.auth-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 3rem;
  border-radius: 20px;
  width: 100%;
  max-width: 440px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
  text-align: center;
  color: #fff;
}

.auth-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  background: linear-gradient(90deg, #00c6ff, #0072ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.auth-subtitle {
  color: #a0a0b0;
  margin-bottom: 2rem;
  font-size: 1rem;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.input-group {
  display: flex;
  flex-direction: column;
  text-align: left;
}

.input-group label {
  font-size: 0.875rem;
  font-weight: 500;
  margin-bottom: 0.5rem;
  color: #d1d1e0;
}

.input-group input {
  padding: 1rem;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #fff;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.input-group input:focus {
  outline: none;
  border-color: #00c6ff;
  box-shadow: 0 0 0 3px rgba(0, 198, 255, 0.2);
}

.btn-primary {
  background: linear-gradient(90deg, #0072ff, #00c6ff);
  color: white;
  border: none;
  padding: 1rem;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  margin-top: 0.5rem;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0, 114, 255, 0.4);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.divider {
  display: flex;
  align-items: center;
  margin: 2rem 0;
  color: #a0a0b0;
}

.divider::before, .divider::after {
  content: "";
  flex: 1;
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
}

.divider span {
  padding: 0 1rem;
  font-size: 0.875rem;
  font-weight: 500;
}

.social-login {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.btn-social {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 0.875rem;
  border-radius: 10px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.3s;
  text-decoration: none;
}

.btn-social.google {
  background: white;
  color: #333;
}

.btn-social.google:hover {
  background: #f1f1f1;
}

.auth-redirect {
  margin-top: 2rem;
  color: #a0a0b0;
  font-size: 0.875rem;
}

.auth-redirect a {
  color: #00c6ff;
  text-decoration: none;
  font-weight: 600;
}

.auth-redirect a:hover {
  text-decoration: underline;
}

.error-msg {
  color: #ff5252;
  font-size: 0.875rem;
  margin-top: 0.5rem;
}
</style>
