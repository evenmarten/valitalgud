import axios from 'axios'

export default {
  sendAddCartItemRequest(addCartItemDto) {
    return axios.post('/api/cart/items', addCartItemDto)
  },

  sendGetCartRequest(userId) {
    return axios.get('/api/cart', { params: { userId } })
  },

  sendUpdateCartItemRequest(cartItemId, updateCartItemDto) {
    return axios.put(`/api/cart/items/${cartItemId}`, updateCartItemDto)
  },

  sendDeleteCartItemRequest(cartItemId, userId) {
    return axios.delete(`/api/cart/items/${cartItemId}`, { params: { userId } })
  },
}