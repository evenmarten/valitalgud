<template>
  <div class="skill-tag-multiselect">
    <div class="stm-row">
      <div class="stm-control-wrap">
        <button type="button" class="form-select text-start stm-control" @click="toggleOpen">
          {{ placeholder }}
        </button>

        <div v-if="isOpen" class="stm-panel">
          <input
            ref="search"
            v-model="query"
            type="text"
            class="form-control stm-search"
            placeholder="Otsi tagi..."
            @keydown.esc="close"
          />
          <ul class="stm-list">
            <li
              v-for="tag in filteredTags"
              :key="tag.id"
              class="stm-option"
              :class="{ 'stm-option--active': isSelected(tag.id) }"
              @click="toggleTag(tag.id)"
            >
              <span>{{ tag.name }}</span>
              <span v-if="isSelected(tag.id)" class="stm-tick">✓</span>
            </li>
            <li v-if="filteredTags.length === 0" class="stm-empty">Vasteid ei leitud</li>
          </ul>
        </div>
      </div>

      <div class="stm-chips">
        <span v-for="tag in selectedTags" :key="tag.id" class="stm-chip">
          {{ tag.name }}
          <button type="button" class="stm-chip-remove" @click="removeTag(tag.id)">×</button>
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SkillTagMultiSelect',
  props: {
    tags: {
      type: Array,
      default: () => [],
    },
    selectedIds: {
      type: Array,
      default: () => [],
    },
    placeholder: {
      type: String,
      default: '-- Vali tagid --',
    },
  },
  emits: ['event-tags-changed'],
  data() {
    return {
      isOpen: false,
      query: '',
    }
  },
  computed: {
    selectedTags() {
      return this.selectedIds
        .map((id) => this.tags.find((tag) => tag.id === id))
        .filter((tag) => tag != null)
    },

    filteredTags() {
      const query = this.query.trim().toLowerCase()
      if (!query) {
        return this.tags
      }
      return this.tags.filter((tag) => this.matchesQuery(tag.name, query))
    },
  },
  methods: {
    toggleOpen() {
      this.isOpen = !this.isOpen
      if (this.isOpen) {
        this.$nextTick(() => this.$refs.search?.focus())
      }
    },

    close() {
      this.isOpen = false
    },

    isSelected(tagId) {
      return this.selectedIds.includes(tagId)
    },

    toggleTag(tagId) {
      const newIds = this.isSelected(tagId)
        ? this.selectedIds.filter((id) => id !== tagId)
        : [...this.selectedIds, tagId]
      this.$emit('event-tags-changed', newIds)
    },

    removeTag(tagId) {
      this.$emit('event-tags-changed', this.selectedIds.filter((id) => id !== tagId))
    },

    matchesQuery(name, query) {
      const lowerName = name.toLowerCase()
      return lowerName.startsWith(query) || lowerName.split(/\s+/).some((word) => word.startsWith(query))
    },

    handleOutsideClick(event) {
      if (!this.$el.contains(event.target)) {
        this.close()
      }
    },
  },
  mounted() {
    document.addEventListener('click', this.handleOutsideClick)
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleOutsideClick)
  },
}
</script>

<style scoped>
.stm-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.5rem;
}

.stm-control-wrap {
  position: relative;
  min-width: 200px;
  flex: 0 0 auto;
}

.stm-control {
  cursor: pointer;
}

.stm-panel {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  right: 0;
  z-index: 1050;
  background: var(--nb-white);
  border: var(--nb-border);
  box-shadow: var(--nb-shadow);
  padding: 0.5rem;
}

.stm-search {
  margin-bottom: 0.5rem;
}

.stm-list {
  list-style: none;
  margin: 0;
  padding: 0;
  max-height: 220px;
  overflow-y: auto;
}

.stm-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.4rem 0.6rem;
  cursor: pointer;
  font-weight: 600;
}

.stm-option:hover {
  background: var(--nb-yellow);
}

.stm-option--active {
  background: var(--nb-blue);
  color: var(--nb-white);
}

.stm-empty {
  padding: 0.4rem 0.6rem;
  color: #6c757d;
}

.stm-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
}

.stm-chip {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.25rem 0.6rem;
  background: var(--nb-yellow);
  border: 2px solid var(--nb-black);
  font-weight: 700;
  font-size: 0.9rem;
}

.stm-chip-remove {
  border: none;
  background: transparent;
  font-size: 1.1rem;
  line-height: 1;
  cursor: pointer;
  padding: 0;
  color: var(--nb-black);
}
</style>
