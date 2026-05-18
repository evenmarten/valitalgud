import axios from 'axios'

export default {
  sendCreateOrderRequest(createOrderDto) {
    return axios.post('/api/orders', createOrderDto)
  },
}