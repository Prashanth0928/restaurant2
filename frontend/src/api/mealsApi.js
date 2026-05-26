export const mealsApi = {
  search: async (dishName) => {
    const res = await fetch(`/meals/search/${encodeURIComponent(dishName)}`)
    const json = await res.json()
    if (!res.ok) throw new Error(json.message || 'Search failed')
    return json.data ?? []
  }
}
