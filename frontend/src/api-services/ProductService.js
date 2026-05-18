import axios from 'axios'

export default {
  sendGetProductsRequest() {
    return axios.get('/api/products')
  },

  sendGetProductDetailsRequest(productId) {
    return axios.get(`/api/products/${productId}`)
  },
}