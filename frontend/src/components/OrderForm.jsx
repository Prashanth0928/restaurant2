import { useState } from 'react'

const ORDER_STATUSES = ['PENDING', 'CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED']
const PAYMENT_STATUSES = ['PENDING', 'PAID', 'FAILED', 'REFUNDED']

const emptyForm = {
  customerName: '', orderedDish: '', quantity: 1,
  orderPrice: '', orderStatus: 'PENDING',
  restaurantName: '', deliveryAddress: '', paymentStatus: 'PENDING',
}

export default function OrderForm({ initial, onSubmit, onCancel, loading }) {
  const [form, setForm] = useState(initial ?? emptyForm)
  const [error, setError] = useState('')

  const set = (field) => (e) => setForm((f) => ({ ...f, [field]: e.target.value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await onSubmit({ ...form, quantity: Number(form.quantity), orderPrice: Number(form.orderPrice) })
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      {error && <div className="error-msg">{error}</div>}
      <div className="form-grid">
        <div className="form-group">
          <label>Customer Name *</label>
          <input value={form.customerName} onChange={set('customerName')} required />
        </div>
        <div className="form-group">
          <label>Restaurant Name *</label>
          <input value={form.restaurantName} onChange={set('restaurantName')} required />
        </div>
        <div className="form-group">
          <label>Ordered Dish *</label>
          <input value={form.orderedDish} onChange={set('orderedDish')} required />
        </div>
        <div className="form-group">
          <label>Quantity *</label>
          <input type="number" min="1" max="100" value={form.quantity} onChange={set('quantity')} required />
        </div>
        <div className="form-group">
          <label>Order Price ($) *</label>
          <input type="number" step="0.01" min="0.01" value={form.orderPrice} onChange={set('orderPrice')} required />
        </div>
        <div className="form-group">
          <label>Order Status</label>
          <select value={form.orderStatus} onChange={set('orderStatus')}>
            {ORDER_STATUSES.map((s) => <option key={s}>{s}</option>)}
          </select>
        </div>
        <div className="form-group">
          <label>Payment Status</label>
          <select value={form.paymentStatus} onChange={set('paymentStatus')}>
            {PAYMENT_STATUSES.map((s) => <option key={s}>{s}</option>)}
          </select>
        </div>
        <div className="form-group full-width">
          <label>Delivery Address *</label>
          <input value={form.deliveryAddress} onChange={set('deliveryAddress')} required />
        </div>
      </div>
      <div className="form-actions">
        <button type="button" className="btn btn-secondary" onClick={onCancel}>Cancel</button>
        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Saving…' : 'Save Order'}
        </button>
      </div>
    </form>
  )
}
