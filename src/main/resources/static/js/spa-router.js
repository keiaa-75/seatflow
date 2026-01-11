const SpaRouter = (function() {
    const ROUTE_CHANGE_EVENT = 'routechange';
    let currentRoute = null;
    let isInitialLoad = true;
    let ignoreNextPopState = false;

    function init() {
        $(document).on('click', handleLinkClick);
        $(window).on('popstate', handlePopState);
        observeNav();
    }

    function observeNav() {
        const observer = new MutationObserver(function() {
            updateActiveNav(window.location.pathname);
        });

        $(document).ready(function() {
            const $nav = $('[data-spa-link]');
            if ($nav.length) {
                updateActiveNav(window.location.pathname);
            } else {
                observer.observe(document.body, { childList: true, subtree: true });
            }
        });
    }

    function handleLinkClick(e) {
        const link = $(e.target).closest('a[data-spa-link]');
        if (!link.length) return;

        const href = link.attr('href');
        if (!href || href.startsWith('#') || href.startsWith('http') || href.startsWith('mailto')) return;

        e.preventDefault();
        navigate(href);
    }

    function handlePopState(e) {
        if (ignoreNextPopState) {
            ignoreNextPopState = false;
            return;
        }
        if (currentRoute !== window.location.pathname) {
            loadRoute(window.location.pathname, false);
        }
    }

    function navigate(href) {
        if (href === window.location.pathname) return;
        history.pushState({}, '', href);
        loadRoute(href, true);
    }

    async function loadRoute(path, triggerEvent = true) {
        showLoading();
        currentRoute = path;

        try {
            const url = new URL(path, window.location.origin);
            url.searchParams.set('fragment', 'true');
            const response = await fetch(url.toString(), {
                headers: { 'X-Requested-With': 'XMLHttpRequest' }
            });

            if (!response.ok) throw new Error('Route not found');

            const html = await response.text();
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            const $target = $('[data-spa-content]');

            let content;
            const wrapper = doc.querySelector('[data-spa-content]');
            if (wrapper) {
                content = wrapper.innerHTML;
            } else {
                content = doc.body.innerHTML;
            }

            if ($target.length) {
                $target.html(content);
                executeScripts($target);
            }

            updateActiveNav(path);
            hideLoading();

            if (triggerEvent) {
                document.dispatchEvent(new CustomEvent(ROUTE_CHANGE_EVENT, { detail: { path } }));
            }

            isInitialLoad = false;
        } catch (error) {
            console.error('Route error:', error);
            hideLoading();
            window.location.href = path;
        }
    }

    function executeScripts(container) {
        container.find('script').each(function() {
            const $script = $(this);
            const newScript = document.createElement('script');
            if ($script.attr('src')) {
                newScript.src = $script.attr('src');
            } else {
                newScript.textContent = $script.text();
            }
            this.parentNode.replaceChild(newScript, this);
        });
    }

    function updateActiveNav(path) {
        $('[data-spa-link]').each(function() {
            const $link = $(this);
            const href = $link.attr('href');
            if (href === path || (path.startsWith(href) && href !== '/')) {
                $link.addClass('is-active');
            } else {
                $link.removeClass('is-active');
            }
        });
    }

    function showLoading() {
        let $overlay = $('#spa-loading-overlay');
        if (!$overlay.length) {
            $overlay = $('<div id="spa-loading-overlay"><div class="spinner"></div></div>');
            $overlay.css({
                'position': 'fixed',
                'top': '0',
                'left': '0',
                'right': '0',
                'bottom': '0',
                'background': 'rgba(255,255,255,0.9)',
                'display': 'flex',
                'justify-content': 'center',
                'align-items': 'center',
                'z-index': '9999',
                'opacity': '0',
                'transition': 'opacity 0.2s ease'
            });
            $('body').append($overlay);
        }
        requestAnimationFrame(() => $overlay.css('opacity', '1'));
    }

    function hideLoading() {
        const $overlay = $('#spa-loading-overlay');
        if ($overlay.length) {
            $overlay.css('opacity', '0');
            setTimeout(() => {
                $overlay.css('display', 'none');
            }, 200);
        }
    }

    return { init, navigate, loadRoute };
})();

$(document).ready(function() {
    SpaRouter.init();
});
