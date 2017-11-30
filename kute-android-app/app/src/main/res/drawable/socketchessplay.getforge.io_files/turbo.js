(function() {
  this.TurboJS = (function() {
    function TurboJS() {}

    TurboJS.isSupported = function() {
      return window.history && window.history.pushState && window.history.replaceState && !navigator.userAgent.match(/CriOS\//);
    };

    TurboJS.pages = {};

    TurboJS.cache = {};

    TurboJS.version = '1.0.6-forge';

    TurboJS.load = function(options) {
      var script, sibling;
      if (options == null) {
        options = {};
      }
      if (!TurboJS.isSupported()) {
        return false;
      }
      script = document.createElement('script');
      script.src = options.src;
      if (sibling = options.sibling) {
        return sibling.parentNode.insertBefore(script, sibling);
      } else {
        return document.head.appendChild(script);
      }
    };

    TurboJS.run = function() {
      if (!TurboJS.isSupported()) {
        return false;
      }
      TurboJS.current = TurboJS.pages[TurboJS.forge.htmlify(location.pathname)] = {
        path: TurboJS.forge.htmlify(location.pathname),
        document: document.documentElement,
        head: document.head,
        body: document.body,
        styles: document.head.querySelectorAll('link[type="text/css"]'),
        headerScripts: document.head.querySelectorAll('script[type="text/javascript"]'),
        scripts: true
      };
      document.addEventListener('click', TurboJS.lastClick, true);
      window.addEventListener('popstate', TurboJS.popstate, false);
      TurboJS.helpers.trigger('turbojs:run');
      TurboJS.helpers.log('running', "v" + TurboJS.version);
      return document.write = function() {
        throw new Error('document.write is not supported with TurboJS');
      };
    };

    TurboJS.lastClick = function(event) {
      if (event.defaultPrevented) {
        return;
      }
      document.removeEventListener('click', TurboJS.click, false);
      return document.addEventListener('click', TurboJS.click, false);
    };

    TurboJS.popstate = function(event) {
      if (!TurboJS.pushedState) {
        return;
      }
      if (!TurboJS.visit(TurboJS.forge.htmlify(location.pathname), false)) {
        return window.location = location.href;
      }
    };

    TurboJS.click = function(event) {
      var link;
      if (event.defaultPrevented) {
        return;
      }
      link = TurboJS.helpers.extractLink(event);
      if (link.nodeName === 'A' && !TurboJS.helpers.ignoreClick(event, link)) {
        if (TurboJS.visit(link.pathname)) {
          return event.preventDefault();
        }
      }
    };

    TurboJS.getPage = function(path) {
      var doc, headerScripts, page, styles, title;
      if (path in TurboJS.pages) {
        return TurboJS.pages[path];
      }
      if (!(path in TurboJS.cache)) {
        return;
      }
      page = TurboJS.pages[path] = TurboJS.cache[path];
      page.path = path;
      if (page.html) {
        doc = TurboJS.helpers.createDocument(page.html);
        title = doc.querySelector('title');
        styles = doc.head.querySelectorAll('link[type="text/css"]');
        headerScripts = doc.head.querySelectorAll('script[type="text/javascript"]');
        page.document = doc.documentElement;
        page.head = doc.head;
        page.body = doc.body;
        page.title = title != null ? title.textContent : void 0;
        page.styles = styles;
        page.headerScripts = headerScripts;
      }
      return page;
    };

    TurboJS.setLocation = function(path) {
      TurboJS.pushedState = true;
      return window.history.pushState({
        turbojs: true
      }, '', TurboJS.forge.dehtmlify(path));
    };

    TurboJS.visit = function(path, pushState) {
      var location, page;
      if (pushState == null) {
        pushState = true;
      }
      page = TurboJS.getPage(path);
      if (!page) {
        return false;
      }
      if (location = page.redirect) {
        return TurboJS.visit(location);
      }
      TurboJS.saveScrollOffset(TurboJS.current);
      if (pushState) {
        TurboJS.setLocation(path);
      }
      TurboJS.replace(page);
      TurboJS.syncStylesheets(TurboJS.current, page);
      TurboJS.syncScripts(TurboJS.current, page);
      if (!page.scripts) {
        page.scripts = true;
        setTimeout(function() {
          return TurboJS.executeScripts();
        }, 10);
        TurboJS.helpers.trigger('turbojs:scripts');
      }
      TurboJS.scrollTo(page);
      TurboJS.helpers.log("Switching to " + path);
      TurboJS.helpers.trigger('turbojs:change');
      return TurboJS.current = page;
    };

    TurboJS.replace = function(page) {
      document.documentElement.replaceChild(page.body, document.body);
      if (page.title) {
        return document.title = page.title;
      }
    };

    TurboJS.executeScripts = function() {
      var attr, copy, nextSibling, parentNode, script, scripts, _i, _j, _len, _len1, _ref, _ref1, _results;
      scripts = document.querySelectorAll('script:not([data-turbojs="false"])');
      scripts = Array.prototype.slice.call(scripts);
      _results = [];
      for (_i = 0, _len = scripts.length; _i < _len; _i++) {
        script = scripts[_i];
        if (!((_ref = script.type) === '' || _ref === 'text/javascript')) {
          continue;
        }
        copy = document.createElement('script');
        _ref1 = script.attributes;
        for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
          attr = _ref1[_j];
          copy.setAttribute(attr.name, attr.value);
        }
        copy.appendChild(document.createTextNode(script.innerHTML));
        parentNode = script.parentNode, nextSibling = script.nextSibling;
        parentNode || (parentNode = document.body);
        parentNode.removeChild(script);
        _results.push(parentNode.insertBefore(copy, nextSibling));
      }
      return _results;
    };

    TurboJS.syncStylesheets = function(previous, current) {
      var node, toAdd, toRemove, _i, _j, _len, _len1, _results;
      toRemove = TurboJS.helpers.diffNodes(previous.styles, current.styles);
      toAdd = TurboJS.helpers.diffNodes(current.styles, previous.styles);
      for (_i = 0, _len = toRemove.length; _i < _len; _i++) {
        node = toRemove[_i];
        TurboJS.helpers.remove(node);
      }
      _results = [];
      for (_j = 0, _len1 = toAdd.length; _j < _len1; _j++) {
        node = toAdd[_j];
        _results.push(TurboJS.helpers.append(document.head, node));
      }
      return _results;
    };

    TurboJS.syncScripts = function(previous, current) {
      var node, toAdd, toRemove, _i, _j, _len, _len1, _results;
      toRemove = TurboJS.helpers.diffNodes(previous.headerScripts, current.headerScripts);
      toAdd = TurboJS.helpers.diffNodes(current.headerScripts, previous.headerScripts);
      for (_i = 0, _len = toRemove.length; _i < _len; _i++) {
        node = toRemove[_i];
        TurboJS.helpers.remove(node);
      }
      _results = [];
      for (_j = 0, _len1 = toAdd.length; _j < _len1; _j++) {
        node = toAdd[_j];
        _results.push(TurboJS.helpers.append(document.body, node));
      }
      return _results;
    };

    TurboJS.saveScrollOffset = function(page) {
      page.positionY = window.pageYOffset;
      return page.positionX = window.pageXOffset;
    };

    TurboJS.scrollTo = function(page) {
      return window.scrollTo(page.positionX || 0, page.positionY || 0);
    };

    return TurboJS;

  }).call(this);

}).call(this);
(function() {
  var anchoredLink, append, containsNode, createDocument, createDocumentBody, crossOriginLink, diffNodes, extractLink, host, ignoreClick, log, noTurbolink, nonHtmlLink, nonStandardClick, remove, removeHash, targetLink, trigger,
    __slice = [].slice;

  removeHash = function(url) {
    var link;
    link = url;
    if (url.href == null) {
      link = document.createElement('A');
      link.href = url;
    }
    return link.href.replace(link.hash, '');
  };

  extractLink = function(event) {
    var link;
    link = event.target;
    while (!(!link.parentNode || link.nodeName === 'A')) {
      link = link.parentNode;
    }
    return link;
  };

  crossOriginLink = function(link) {
    return location.protocol !== link.protocol || location.host !== link.host;
  };

  anchoredLink = function(link) {
    return ((link.hash && removeHash(link)) === removeHash(location)) || (link.href === location.href + '#');
  };

  nonHtmlLink = function(link) {
    var url;
    url = removeHash(link);
    return url.match(/\.[a-z]+(\?.*)?$/g) && !url.match(/\.html?(\?.*)?$/g);
  };

  noTurbolink = function(link) {
    var ignore;
    while (!(ignore || link === document)) {
      ignore = link.getAttribute('data-no-turbolink') != null;
      link = link.parentNode;
    }
    return ignore;
  };

  targetLink = function(link) {
    return link.target.length !== 0;
  };

  nonStandardClick = function(event) {
    return event.which > 1 || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey;
  };

  ignoreClick = function(event, link) {
    return crossOriginLink(link) || anchoredLink(link) || nonHtmlLink(link) || noTurbolink(link) || targetLink(link) || nonStandardClick(event);
  };

  createDocument = (function() {
    var createDocumentUsingDOM, createDocumentUsingParser, createDocumentUsingWrite, e, testDoc, _ref;
    createDocumentUsingParser = function(html) {
      return (new DOMParser).parseFromString(html, 'text/html');
    };
    createDocumentUsingDOM = function(html) {
      var doc;
      doc = document.implementation.createHTMLDocument('');
      doc.documentElement.innerHTML = html;
      return doc;
    };
    createDocumentUsingWrite = function(html) {
      var doc;
      doc = document.implementation.createHTMLDocument('');
      doc.open('replace');
      doc.write(html);
      doc.close();
      return doc;
    };
    try {
      if (window.DOMParser) {
        testDoc = createDocumentUsingParser('<html><body><p>test');
        return createDocumentUsingParser;
      }
    } catch (_error) {
      e = _error;
      testDoc = createDocumentUsingDOM('<html><body><p>test');
      return createDocumentUsingDOM;
    } finally {
      if ((testDoc != null ? (_ref = testDoc.body) != null ? _ref.childNodes.length : void 0 : void 0) !== 1) {
        return createDocumentUsingWrite;
      }
    }
  })();

  createDocumentBody = function(body) {
    return createDocument("<html><body>" + body + "</body></html>");
  };

  trigger = function(name) {
    var event;
    event = document.createEvent('Events');
    event.initEvent(name, true, true);
    return document.dispatchEvent(event);
  };

  host = function(url) {
    var parent, parser;
    parent = document.createElement('div');
    parent.innerHTML = "<a href=\"" + url + "\">x</a>";
    parser = parent.firstChild;
    return parser.host;
  };

  containsNode = function(array, node) {
    var item, _i, _len;
    for (_i = 0, _len = array.length; _i < _len; _i++) {
      item = array[_i];
      if (node.isEqualNode(item)) {
        return true;
      }
    }
    return false;
  };

  diffNodes = function(array, rest) {
    var item, _i, _len, _results;
    _results = [];
    for (_i = 0, _len = array.length; _i < _len; _i++) {
      item = array[_i];
      if (!containsNode(rest, item)) {
        _results.push(item);
      }
    }
    return _results;
  };

  remove = function(element) {
    var _ref;
    return (_ref = element.parentNode) != null ? _ref.removeChild(element) : void 0;
  };

  append = function(parent, element) {
    return parent.appendChild(element);
  };

  log = function() {
    var msgs;
    msgs = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
    return typeof console !== "undefined" && console !== null ? typeof console.log === "function" ? console.log.apply(console, ['TurboJS'].concat(__slice.call(msgs))) : void 0 : void 0;
  };

  this.TurboJS.helpers = {
    extractLink: extractLink,
    ignoreClick: ignoreClick,
    createDocument: createDocument,
    createDocumentBody: createDocumentBody,
    trigger: trigger,
    diffNodes: diffNodes,
    remove: remove,
    append: append,
    host: host,
    log: log
  };

}).call(this);
(function() {
  if (!this.TurboJS.isSupported()) {
    return;
  }

  document.addEventListener('turbojs:change', function(event) {
    if (typeof _gaq !== "undefined" && _gaq !== null) {
      _gaq.push(['_trackPageview']);
    }
    return typeof pageTracker !== "undefined" && pageTracker !== null ? pageTracker._trackPageview() : void 0;
  });

}).call(this);
(function() {
  var options, script, src;

  if (!this.TurboJS.isSupported()) {
    return;
  }

  script = document.querySelector('script[data-turbojs]');

  if (!script) {
    return;
  }

  src = script.getAttribute('data-turbojs');

  options = {
    sibling: script,
    src: src
  };

  TurboJS.load(options);

}).call(this);
(function() {
  var dehtmlify, htmlify;

  htmlify = function(path) {
    if (path.slice(-1) === "/") {
      path += "index";
    }
    return "" + path + ".html";
  };

  dehtmlify = function(path) {
    path = path.replace('.html', '');
    if (path.slice(-6) === '/index') {
      path = path.slice(0, -5);
    }
    return path;
  };

  this.TurboJS.forge = {
    htmlify: htmlify,
    dehtmlify: dehtmlify
  };

}).call(this);
