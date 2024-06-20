import { backendUrl } from '@/store'

export function backendRequest(url: string, options: RequestInit = {}): Promise<Response> {
  options.credentials = 'include'
  return fetch(backendUrl + url, options)
}
