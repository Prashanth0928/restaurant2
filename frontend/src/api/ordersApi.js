const BASE = '/orders'

async function request(url, options = {}) {
  const res = await fetch(url, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  const json = await res.json()
  if (!res.ok) throw new Error(json.message || 'Request failed')
  return json.data
}

export const ordersApi = {
  getAll: () => request(BASE),
  getById: (id) => request(`${BASE}/${id}`),
  create: (body) => request(BASE, { method: 'POST', body: JSON.stringify(body) }),
  update: (id, body) => request(`${BASE}/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  delete: (id) => request(`${BASE}/${id}`, { method: 'DELETE' }),
}
