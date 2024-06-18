import { ref, watch } from 'vue'
import { defineStore } from 'pinia'
import { LoginRequest, LoginResponse } from 'modellflug-logbuch-common-data'

export interface UserSession {
  readonly loginResponse: LoginResponse
}

export const useLoginSessionStore = defineStore('loginSession', () => {
  const readFromLocalStorage = localStorage.getItem('loginSession')
  const savedSession: UserSession | null =
    readFromLocalStorage != null ? JSON.parse(readFromLocalStorage) : null

  const loginSession = ref<UserSession | null>(savedSession)
  watch(loginSession, (newVal) => {
    localStorage.setItem('loginSession', JSON.stringify(newVal))
  })

  async function login(
    username: string,
    password: string,
    onSuccess: () => void,
    onError: (response: Response) => void,
  ) {
    const response = await fetch('/api/v1/account/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(new LoginRequest(username, password)),
    })
    if (response.ok) {
      const loginResponse = (await response.json()) as LoginResponse
      loginSession.value = {
        loginResponse: loginResponse,
      }
      console.debug(`Login successful for user ${loginResponse.username}`)
      onSuccess()
    } else {
      console.error(response)
      onError(response)
    }
  }

  async function logout() {
    const response = await fetch('/api/v1/account/logout', { method: 'POST' })
    if (!response.ok) {
      console.error(`Failed to logout, status: ${response.status} ${response.statusText}`)
    }
    loginSession.value = null
  }

  return { loginSession, login, logout }
})
