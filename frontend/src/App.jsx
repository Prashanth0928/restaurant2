import { useState } from 'react'
import OrdersPage from './pages/OrdersPage'
import MealSearchPage from './pages/MealSearchPage'

export default function App() {
  const [tab, setTab] = useState('orders')

  return (
    <>
      <nav className="navbar">
        <span className="navbar-brand">Restaurant Order Service</span>
        <button className={`nav-tab ${tab === 'orders' ? 'active' : ''}`} onClick={() => setTab('orders')}>
          Orders
        </button>
        <button className={`nav-tab ${tab === 'meals' ? 'active' : ''}`} onClick={() => setTab('meals')}>
          Meal Search
        </button>
      </nav>

      {tab === 'orders' ? <OrdersPage /> : <MealSearchPage />}
    </>
  )
}
