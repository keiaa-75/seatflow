const SpaRouter = (function() {
    const ROUTE_CHANGE_EVENT = 'routechange';
    let currentRoute = null;
    let isInitialLoad = true;
    let ignoreNextPopState = false;

    function init() {
        document.addEventListener('click', handleLinkClick);
        window.addEventListener('popstate', handlePopState);
        document.addEventListener('DOMContentLoaded', function() {
            updateActiveNav(window.location.pathname);
        });
    }

    function handleLinkClick(e) {
        const link = e.target.closest('a[data-spa-link]');
        if (!link) return;

        const href = link.getAttribute('href');
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
            const response = await fetch(path + '?fragment=true', {
                headers: { 'X-Requested-With': 'XMLHttpRequest' }
            });

            if (!response.ok) throw new Error('Route not found');

            const html = await response.text();
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            const target = document.querySelector('[data-spa-content]');

            let content;
            const wrapper = doc.querySelector('[data-spa-content]');
            if (wrapper) {
                content = wrapper.innerHTML;
            } else {
                content = doc.body.innerHTML;
            }

            if (target) {
                target.innerHTML = content;
                executeScripts(target);
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
        const scripts = container.querySelectorAll('script');
        scripts.forEach(script => {
            const newScript = document.createElement('script');
            if (script.src) {
                newScript.src = script.src;
            } else {
                newScript.textContent = script.textContent;
            }
            script.parentNode.replaceChild(newScript, script);
        });
    }

    function updateActiveNav(path) {
        document.querySelectorAll('[data-spa-link]').forEach(link => {
            const href = link.getAttribute('href');
            if (href === path || (path.startsWith(href) && href !== '/')) {
                link.classList.add('is-active');
            } else {
                link.classList.remove('is-active');
            }
        });
    }

    function showLoading() {
        let overlay = document.getElementById('spa-loading-overlay');
        if (!overlay) {
            overlay = document.createElement('div');
            overlay.id = 'spa-loading-overlay';
            overlay.innerHTML = '<div class="spinner"></div>';
            overlay.style.cssText = 'position:fixed;top:0;left:0;right:0;bottom:0;background:rgba(255,255,255,0.9);display:flex;justify-content:center;align-items:center;z-index:9999;opacity:0;transition:opacity 0.2s ease;';
            document.body.appendChild(overlay);
        }
        requestAnimationFrame(() => overlay.style.opacity = '1');
    }

    function hideLoading() {
        const overlay = document.getElementById('spa-loading-overlay');
        if (overlay) {
            overlay.style.opacity = '0';
            setTimeout(() => {
                overlay.style.display = 'none';
            }, 200);
        }
    }

    return { init, navigate, loadRoute };
})();

document.addEventListener('DOMContentLoaded', function() {
    SpaRouter.init();
});
