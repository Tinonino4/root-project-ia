<template>
  <div class="auth-container">
    <div class="auth-card">
      <h1 class="auth-title">Welcome Back</h1>
      <p class="auth-subtitle">Log in to your account</p>

      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="input-group">
          <label for="email">Email</label>
          <input id="email" v-model="email" type="email" placeholder="name@example.com" required />
        </div>
        <div class="input-group">
          <label for="password">Password</label>
          <input id="password" v-model="password" type="password" placeholder="••••••••" required />
        </div>

        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? 'Logging in...' : 'Log In' }}
        </button>
        <p v-if="error" class="error-msg">{{ error }}</p>
      </form>

      <div class="divider">
        <span>OR</span>
      </div>

      <div class="social-login">
        <a href="http://localhost:8080/oauth2/authorization/google" class="btn-social google">
          <svg viewBox="0 0 24 24" width="24" height="24" xmlns="http://www.w3.org/2000/svg"><path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/><path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84A10.99 10.99 0 0012 23z" fill="#34A853"/><path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/><path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/></svg>
          Continue with Google
        </a>
        <a href="http://localhost:8080/oauth2/authorization/github" class="btn-social github">
          <svg viewBox="0 0 16 16" width="24" height="24" fill="currentColor" xmlns="http://www.w3.org/2000/svg"><path d="M8 0a8 8 0 00-2.53 15.59c.4.07.55-.17.55-.38l-.01-1.49c-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82A7.68 7.68 0 018 3.86c.69 0 1.39.09 2.06.26 1.53-1.03 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48l-.01 2.2c0 .21.15.46.55.38A8.01 8.01 0 0016 8a8 8 0 00-8-8z"/></svg>
          Continue with GitHub
        </a>
      </div>

      <p class="auth-redirect">
        Don't have an account? <router-link to="/register">Sign up</router-link>
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

const handleLogin = async () => {
  error.value = ''
  loading.value = true
  try {
    await authStore.login(email.value, password.value)
  } catch (err) {
    error.value = err.response?.data?.message || 'Login failed. Please check your credentials.'
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
  background: linear-gradient(90deg, #bb86fc, #3700b3);
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
  border-color: #bb86fc;
  box-shadow: 0 0 0 3px rgba(187, 134, 252, 0.2);
}

.btn-primary {
  background: linear-gradient(90deg, #6200ea, #bb86fc);
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
  box-shadow: 0 10px 20px rgba(98, 0, 234, 0.4);
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

.btn-social.github {
  background: #24292e;
  color: white;
}

.btn-social.github:hover {
  background: #1b1f23;
}

.auth-redirect {
  margin-top: 2rem;
  color: #a0a0b0;
  font-size: 0.875rem;
}

.auth-redirect a {
  color: #bb86fc;
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
