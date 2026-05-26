import { useEffect, useState } from 'react'
import { ordersApi } from '../api/ordersApi'
import OrderForm from '../components/OrderForm'

export default function OrdersPage() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [modal, setModal] = useState(null) // null | 'create' | { edit: order }
  const [saving, setSaving] = useState(false)

  const load = async () => {
    try {
      setLoading(true)
      setOrders(await ordersApi.getAll())
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const handleCreate = async (data) => {
    setSaving(true)
    try {
      await ordersApi.create(data)
      setModal(null)
      load()
    } finally {
      setSaving(false)
    }
  }

  const handleUpdate = async (data) => {
    setSaving(true)
    try {
      await ordersApi.update(modal.edit.orderId, data)
      setModal(null)
      load()
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this order?')) return
    try {
      await ordersApi.delete(id)
      load()
    } catch (e) {
      alert(e.message)
    }
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1 className="page-title">Orders</h1>
        <button className="btn btn-primary" onClick={() => setModal('create')}>+ New Order</button>
      </div>

      {error && <div className="error-msg">{error}</div>}

      {loading ? (
        <div className="loading">Loading orders…</div>
      ) : orders.length === 0 ? (
        <div className="empty-state">No orders yet. Create one!</div>
      ) : (
        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Order ID</th>
                <th>Customer</th>
                <th>Dish</th>
                <th>Qty</th>
                <th>Price</th>
                <th>Status</th>
                <th>Payment</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((o) => (
                <tr key={o.orderId}>
                  <td style={{ fontFamily: 'monospace', fontSize: '0.8rem' }}>{o.orderId}</td>
                  <td>{o.customerName}</td>
                  <td>{o.orderedDish}</td>
                  <td>{o.quantity}</td>
                  <td>${Number(o.orderPrice).toFixed(2)}</td>
                  <td><span className={`badge badge-${o.orderStatus}`}>{o.orderStatus}</span></td>
                  <td><span className={`badge badge-${o.paymentStatus}`}>{o.paymentStatus}</span></td>
                  <td style={{ display: 'flex', gap: '0.4rem' }}>
                    <button className="btn btn-secondary" style={{ padding: '0.3rem 0.7rem', fontSize: '0.8rem' }}
                      onClick={() => setModal({ edit: o })}>Edit</button>
                    <button className="btn btn-danger" style={{ padding: '0.3rem 0.7rem', fontSize: '0.8rem' }}
                      onClick={() => handleDelete(o.orderId)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {modal && (
        <div className="modal-overlay" onClick={() => setModal(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-title">{modal === 'create' ? 'New Order' : 'Edit Order'}</div>
            <OrderForm
              initial={modal.edit ?? undefined}
              onSubmit={modal === 'create' ? handleCreate : handleUpdate}
              onCancel={() => setModal(null)}
              loading={saving}
            />
          </div>
        </div>
      )}
    </div>
  )
}
