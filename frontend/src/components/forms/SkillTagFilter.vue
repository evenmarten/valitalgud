<template>
  <div class="skill-tag-filter">
    <button type="button" class="form-select text-start stf-control" @click="toggleOpen">
      {{ selectedName }}
    </button>

    <div v-if="isOpen" class="stf-panel">
      <input
        ref="search"
        v-model="query"
        type="text"
        class="form-control stf-search"
        placeholder="Otsi tagi..."
        @keydown.esc="close"
      />
      <ul class="stf-list">
        <li
          class="stf-option"
          :class="{ 'stf-option--active': selectedId == null }"
          @click="selectTag(null)"
        >
          {{ allLabel }}
        </li>
        <li
          v-for="tag in filteredTags"
          :key="tag.id"
          class="stf-option"
          :class="{ 'stf-option--active': selectedId === tag.id }"
          @click="selectTag(tag.id)"
        >
          {{ tag.name }}
        </li>
        <li v-if="filteredTags.length === 0" class="stf-empty">Vasteid ei leitud</li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SkillTagFilter',
  props: {
    tags: {
      type: Array,
      default: () => [],
    },
    selectedId: {
      type: Number,
      default: null,
    },
    allLabel: {
      type: String,
      default: '-- Kõik tagid --',
    },
  },
  emits: ['event-tag-selected'],
  data() {
    return {
      isOpen: false,
      query: '',
    }
  },
  computed: {
    selectedName() {
      if (this.selectedId == null) {
        return this.allLabel
      }
      const selectedTag = this.tags.find((tag) => tag.id === this.selectedId)
      return selectedTag ? selectedTag.name : this.allLabel
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

    selectTag(tagId) {
      this.$emit('event-tag-selected', tagId)
      this.query = ''
      this.close()
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
.skill-tag-filter {
  position: relative;
}

.stf-control {
  cursor: pointer;
}

.stf-panel {
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

.stf-search {
  margin-bottom: 0.5rem;
}

.stf-list {
  list-style: none;
  margin: 0;
  padding: 0;
  max-height: 220px;
  overflow-y: auto;
}

.stf-option {
  padding: 0.4rem 0.6rem;
  cursor: pointer;
  font-weight: 600;
}

.stf-option:hover {
  background: var(--nb-yellow);
}

.stf-option--active {
  background: var(--nb-blue);
  color: var(--nb-white);
}

.stf-empty {
  padding: 0.4rem 0.6rem;
  color: #6c757d;
}
</style>
