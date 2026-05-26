import { useState } from 'react'
import { mealsApi } from '../api/mealsApi'

function MealModal({ meal, onClose }) {
  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" style={{ width: '680px' }} onClick={(e) => e.stopPropagation()}>
        {meal.strMealThumb && (
          <img src={meal.strMealThumb} alt={meal.strMeal}
            style={{ width: '100%', height: '220px', objectFit: 'cover', borderRadius: '6px', marginBottom: '1rem' }} />
        )}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <h2 style={{ fontSize: '1.3rem', fontWeight: 700 }}>{meal.strMeal}</h2>
          <button onClick={onClose} style={{ background: 'none', border: 'none', fontSize: '1.4rem', cursor: 'pointer', color: '#888' }}>✕</button>
        </div>

        <div style={{ display: 'flex', gap: '0.6rem', margin: '0.6rem 0 1rem', flexWrap: 'wrap' }}>
          {meal.strCategory && <span className="badge badge-CONFIRMED">{meal.strCategory}</span>}
          {meal.strArea && <span className="badge badge-PENDING">{meal.strArea} Cuisine</span>}
          {meal.strTags && meal.strTags.split(',').map((t) => (
            <span key={t} className="badge badge-PREPARING">{t.trim()}</span>
          ))}
        </div>

        {meal.strInstructions && (
          <>
            <h3 style={{ fontSize: '0.95rem', fontWeight: 600, marginBottom: '0.5rem' }}>Instructions</h3>
            <div style={{
              fontSize: '0.88rem', lineHeight: '1.7', color: '#444',
              maxHeight: '280px', overflowY: 'auto', paddingRight: '0.5rem',
              whiteSpace: 'pre-line'
            }}>
              {meal.strInstructions}
            </div>
          </>
        )}

        {meal.strYoutube && (
          <a href={meal.strYoutube} target="_blank" rel="noreferrer"
            className="btn btn-primary"
            style={{ display: 'inline-block', marginTop: '1.2rem', textDecoration: 'none' }}>
            Watch on YouTube
          </a>
        )}
      </div>
    </div>
  )
}

export default function MealSearchPage() {
  const [query, setQuery] = useState('')
  const [meals, setMeals] = useState([])
  const [loading, setLoading] = useState(false)
  const [searched, setSearched] = useState(false)
  const [error, setError] = useState('')
  const [selected, setSelected] = useState(null)

  const handleSearch = async (e) => {
    e.preventDefault()
    if (!query.trim()) return
    setError('')
    setLoading(true)
    setSearched(true)
    try {
      setMeals(await mealsApi.search(query.trim()))
    } catch (err) {
      setError(err.message)
      setMeals([])
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1 className="page-title">Meal Search</h1>
        <span style={{ fontSize: '0.85rem', color: '#888' }}>Powered by TheMealDB</span>
      </div>

      <form className="search-bar" onSubmit={handleSearch}>
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search for a dish… e.g. biryani, pizza, pasta"
        />
        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Searching…' : 'Search'}
        </button>
      </form>

      {error && <div className="error-msg">{error}</div>}
      {loading && <div className="loading">Fetching from TheMealDB…</div>}
      {!loading && searched && meals.length === 0 && !error && (
        <div className="empty-state">No meals found for "{query}"</div>
      )}

      <div className="meal-grid">
        {meals.map((meal) => (
          <div className="meal-card" key={meal.idMeal} onClick={() => setSelected(meal)}
            style={{ cursor: 'pointer' }}>
            {meal.strMealThumb && <img src={meal.strMealThumb} alt={meal.strMeal} />}
            <div className="meal-card-body">
              <div className="meal-card-title">{meal.strMeal}</div>
              <div className="meal-card-meta">
                {meal.strCategory && <span>Category: {meal.strCategory}</span>}
                {meal.strArea && <span>Cuisine: {meal.strArea}</span>}
              </div>
              {meal.strTags && (
                <div style={{ marginTop: '0.5rem', fontSize: '0.78rem', color: '#aaa' }}>
                  Tags: {meal.strTags}
                </div>
              )}
              <div style={{ marginTop: '0.6rem', fontSize: '0.82rem', color: '#e94560', fontWeight: 500 }}>
                Click to view instructions →
              </div>
            </div>
          </div>
        ))}
      </div>

      {selected && <MealModal meal={selected} onClose={() => setSelected(null)} />}
    </div>
  )
}
