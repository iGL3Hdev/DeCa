const BASE_URL = '/api'

export class ApiError extends Error {
  status: number
  detalle: unknown

  constructor(status: number, message: string, detalle: unknown) {
    super(message)
    this.status = status
    this.detalle = detalle
  }
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${BASE_URL}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })

  if (!response.ok) {
    const cuerpo = await response.json().catch(() => null)
    throw new ApiError(
      response.status,
      cuerpo?.detail ?? `Error ${response.status}`,
      cuerpo,
    )
  }

  if (response.status === 204) {
    return undefined as T
  }

  return response.json() as Promise<T>
}

export const api = {
  get: <T>(path: string) => request<T>(path),
  post: <T>(path: string, body: unknown) =>
    request<T>(path, { method: 'POST', body: JSON.stringify(body) }),
  put: <T>(path: string, body: unknown) =>
    request<T>(path, { method: 'PUT', body: JSON.stringify(body) }),
  patch: <T>(path: string) => request<T>(path, { method: 'PATCH' }),
  delete: <T>(path: string) => request<T>(path, { method: 'DELETE' }),
}
