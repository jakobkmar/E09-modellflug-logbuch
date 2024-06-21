import { ref, watch } from 'vue'
import { defineStore } from 'pinia'
import { LoginRequest, SharedSessionData } from 'modellflug-logbuch-common-data'
import { backendRequest } from '@/networking'

export interface UserSession {
  readonly sessionData: SharedSessionData
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
    const response = await backendRequest('/api/v1/account/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(new LoginRequest(username, password)),
    })
    if (response.ok) {
      const sessionData: SharedSessionData = await response.json()
      loginSession.value = {
        sessionData: sessionData,
      }
      console.debug(`Login successful for user ${sessionData.username}`)
      onSuccess()
    } else {
      console.error(`Failed to login, status: ${response.status} ${response.statusText}`)
      onError(response)
    }
  }

  async function logout(
    onFinish: () => void,
    onError: (response: Response) => void,
  ) {
    const response = await backendRequest('/api/v1/account/logout', {
      method: 'POST',
    })
    loginSession.value = null
    onFinish()
    if (!response.ok) {
      console.error(`Failed to logout, status: ${response.status} ${response.statusText}`)
      onError(response)
    }
  }

  function canSeeAllLogs(): boolean {
    const sessionData = loginSession.value?.sessionData
    if (sessionData == null) {
      return false
    }
    return sessionData.isAdminUnsafe || sessionData.canSeeAllLogsUnsafe
  }

  return { loginSession, login, logout, canSeeAllLogs }
})
