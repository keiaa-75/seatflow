/**
 * Reusable search functionality for filtering lists of items
 */
class SearchFilter {
    constructor(options) {
        this.searchInput = document.querySelector(options.searchInput);
        this.itemsContainer = document.querySelector(options.itemsContainer);
        this.itemSelector = options.itemSelector;
        this.searchAttribute = options.searchAttribute || 'data-search-text';
        this.noResultsMessage = options.noResultsMessage || 'No items found matching your search.';
        this.debounceDelay = options.debounceDelay || 300;
        
        this.init();
    }
    
    init() {
        if (!this.searchInput || !this.itemsContainer) {
            console.warn('SearchFilter: Required elements not found');
            return;
        }
        
        this.searchInput.addEventListener('input', this.debounce(this.handleSearch.bind(this), this.debounceDelay));
    }
    
    handleSearch() {
        const query = this.searchInput.value.toLowerCase().trim();
        const items = this.itemsContainer.querySelectorAll(this.itemSelector);
        let visibleCount = 0;
        
        items.forEach(item => {
            const searchText = item.getAttribute(this.searchAttribute);
            if (!searchText) return;
            
            if (query === '' || searchText.toLowerCase().includes(query)) {
                item.style.display = '';
                visibleCount++;
            } else {
                item.style.display = 'none';
            }
        });
        
        this.handleNoResults(visibleCount === 0 && query !== '');
    }
    
    handleNoResults(show) {
        let noResultsEl = this.itemsContainer.querySelector('.no-search-results');
        
        if (show) {
            if (!noResultsEl) {
                noResultsEl = document.createElement('div');
                noResultsEl.className = 'no-search-results has-text-centered has-text-grey py-5';
                noResultsEl.textContent = this.noResultsMessage;
                this.itemsContainer.appendChild(noResultsEl);
            }
            noResultsEl.style.display = '';
        } else if (noResultsEl) {
            noResultsEl.style.display = 'none';
        }
    }
    
    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func.apply(this, args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }
}

// Export for use in other scripts
window.SearchFilter = SearchFilter;
