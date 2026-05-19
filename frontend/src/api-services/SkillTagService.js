import axios from 'axios'

export default {
  sendGetSkillTagsRequest() {
    return axios.get('/api/skill-tags')
  },
}
