/*!
 * Collection of handy jQuery methods
 */

(function(factory){
    if (typeof define === 'function' && define.amd) {
        define(factory);
    }
    else if (typeof exports === 'object') {
        module.exports = factory();
    }
    else {
        return factory();
    }
}(function(){

    var undef;
    var xx = getObject(window.xx     || function(){});
    var $  = getObject(window.jQuery || function(){});

    function noop(){}

    var consoleLog   = window.console && window.console.log   ? window.console.log   : noop;
    var consoleWarn  = window.console && window.console.warn  ? window.console.warn  : noop;
    var consoleError = window.console && window.console.error ? window.console.error : noop;

    //////////////////////////////
    // STATIC UTILITY METHODS
    // jQuery **OPTIONAL**
    //////////////////////////////

    function isString(it){
        return typeof it === 'string';
    }
    xx.isString = isString;

    // can the value be reasonably used as a string?
    function stringable(val){
        return /^(string|number|boolean)$/.test(typeof val);
    }
    xx.stringable = stringable;

    function isNumber(it){
        return (typeof it == 'number' && !isNaN(it));
    }
    xx.isNumber = isNumber;

    // copy of jQuery's $.isNumeric() method
    function isNumeric(it) {
        // parseFloat NaNs numeric-cast false positives (null|true|false|"")
        // ...but misinterprets leading-number strings, particularly hex literals ("0x...")
        // subtraction forces infinities to NaN
        // adding 1 corrects loss of precision from parseFloat (jQuery issue #15100)
        return !Array.isArray(it) && (it - parseFloat(it) + 1) >= 0;
    }
    xx.isNumeric = isNumeric;

    function isFunction(it){
        return typeof it === 'function';
    }
    xx.isFunction = isFunction;

    function isPlainObject(it){
        return Object.prototype.toString.call(it) === '[object Object]';
    }
    xx.isPlainObject = isPlainObject;

    function getObject(it){
        return (isPlainObject(it) || isFunction(it)) ? it : {};
    }
    xx.getObject = getObject;

    function isElement(it){
        return it.nodeType && it.nodeType === 1;
    }
    xx.isElement = isElement;

    function isFragment(it){
        return it.nodeType && it.nodeType === 11;
    }
    xx.isFragment = isFragment;


    /**
     * Returns real boolean, number, undefined, or null from string
     * @param val {*} - value to parse
     * @param [bool] {*} - optional - convert 1/0 to boolean true/false
     * @returns {*} - returns parsed value of certain type
     */
    function realValue(val, bool){
        // only evaluate strings
        if (!isString(val)) return val;
        if (bool){
            if (val === '0'){
                return false;
            }
            if (val === '1'){
                return true;
            }
        }
        if (isNumeric(val)){
            return +val;
        }
        switch(val) {
            case 'true': return true;
            case 'false': return false;
            case 'undefined': return undef;
            case 'null': return null;
            default: return val;
        }
    }
    xx.realValue = realValue;


    /**
     * Returns first defined argument - useful for retrieving falsey values
     * @param {...*} - any number of arguments to test
     * @returns {*} - returns first defined argument
     */
    function firstDefined(/* foo, bar, baz, false */) {
        var i = -1;
        var len = arguments.length;
        while (++i < len) {
            if (arguments[i] !== undef) {
                return arguments[i];
            }
        }
        return undef;
    }
    xx.firstDefined = firstDefined;


    /**
     * Convert array-like object to a real array - (twice as fast as Array.prototype.slice.call())
     * @param it
     * @returns {any[]}
     */
    function toArray(it) {
        var i = -1,
            len = it.length,
            newArray = new Array(len);
        while (++i < len) {
            newArray[i] = it[i];
        }
        return newArray;
    }
    xx.toArray = toArray;


    /**
     * Break an array into a 2-D array of smaller 'chunks'
     * @param arr {Array} - input array
     * @param len {Number} - length of each new 'chunk'
     * @returns {Array} - output array
     */
    function chunkArray(arr, len) {
        var chunks = [],
            i = 0,
            n = arr.length;
        while (i < n) {
            chunks.push(arr.slice(i, i += len));
        }
        return chunks;
    }
    xx.chunkArray = chunkArray;


    /**
     * Chunks arrays AND array-like objects
     * @param it {Array|*} - input array or array-like object
     * @param len {Number} - length of each new 'chunk'
     * @returns {Array} - output array
     */
    function chunk(it, len){
        var arr = it;
        // convert non-array to real array
        if (!Array.isArray(it)) {
            arr = toArray(it);
        }
        return chunkArray(arr, len);
    }
    xx.chunk = chunk;


    /**
     * Execute [fn] function on each item in an array or array-like object (with length property)
     * Works just like Array.forEach but also for array-like objects that aren't actual arrays
     * @param {Array} arr - item to iterate over
     * @param {Function} [fn] - function to execute on each iteration
     * @param {this} [context] - optional context for 'this' in iterator
     * @returns {Array|*} - returns new array of iterated items
     */
    function forEach(arr, fn, context){
        var i = -1, len, out;
        if (!arr || !arr.length) { return [] }
        out = new Array(arr.length);
        len = arr.length;
        fn = (fn && isFunction(fn)) ? fn : null;
        while (++i < len) {
            out[i] = arr[i];
            if (fn) {
                fn.call(context || arr[i], arr[i], i);
            }
        }
        return out;
    }
    xx.forEach = forEach;


    /**
     * Execute a function on an object's own enumerable properties - similar to $.each({}) but with less overhead
     * @param {Object} obj - object to iterate
     * @param {Function} [fn] - function to call for each object property
     * @param {this} [context] - optional optional context for 'this' in iterator
     * @returns {Array|*} - returns array of property names - same as Object.keys({})
     */
    function forOwn(obj, fn, context){
        var keys = [],
            key;
        if (!xx.isPlainObject(obj)) { return [] }
        for (key in obj) {
            if (obj.hasOwnProperty(key)) {
                keys.push(key);
                if (!fn || !isFunction(fn)) continue;
                fn.call(context || obj[key], key, obj[key]);
            }
        }
        return keys;
    }
    xx.forOwn = forOwn;


    /**
     * Convert [str] to hyphenated version - 'Foo_BarBaz' --> 'foo-bar-baz'
     * @param {String} str - string to convert
     * @returns {String}
     */
    function toDashed(str){
        str = str !== undef ? str+'' : '';
        return str.replace(/[A-Z]/g, function(u) {
            return '-' + u;
        }).replace(/[A-Z]-/g, function(c){
            return c.replace(/-$/, '');
        }).toLowerCase().replace(/[\W_-]+/g, '-').replace(/^-*|-*$/g, '');
    }
    xx.toDashed = toDashed;


    /**
     * Convert [str] to underscored version - 'foo-barBaz' --> 'foo_bar_baz'
     * @param {String} str - string to convert
     * @param {Boolean} [sanitize] - pass 'false' to SKIP sanitizing string before converting (converts 'foo-BarBaz' to 'foo-bar-baz' first)
     * @returns {String}
     */
    function toUnderscore(str, sanitize){
        // optionally skip 'sanitize' (by running [str] through toDashed())
        if (sanitize !== false) {
            str = toDashed(str);
        }
        return str.replace(/[\W_-]+/g, '_').replace(/^_*|_*$/g, '');
    }
    xx.toUnderscore = toUnderscore;


    /**
     * Convert [str] to camelCase version - 'foo-bar-baz' --> 'fooBarBaz'
     * @param {String} str - string to convert
     * @param {Boolean} [sanitize] - pass 'false' to SKIP sanitizing string before converting (converts 'Foo_BarBaz' to 'foo-bar-baz' first)
     * @returns {String}
     */
    function toCamelCase(str, sanitize) {
        // optionally skip 'sanitize' (by running [str] through toDashed())
        if (sanitize !== false) {
            str = toDashed(str);
        }
        return str.replace(/[\W_-]./g, function(u){
            return (u.substr(1) || '').toUpperCase();
        });
    }
    xx.toCamelCase = toCamelCase;


    // if the first character is one of the default delimeters...
    // ...the character contained within will be the key/value separator
    var parseOptsStr1 = 'foo=1|bar=0|baz=false'; // <-- preferred
    var parseOptsStr2 = 'foo=1;bar=0;baz=false';
    var parseOptsStr3 = ',:,foo:1,bar:0,baz:false';
    var parseOptsStr4 = '?foo=1&bar=0&baz=false'; // query string format
    /**
     * Parse an 'opts' string into an options object
     * accepted syntaxes:
     * 'foo=1|bar=2|baz=3' <-- preferred
     * 'foo:1|bar:2|baz:3'
     * 'foo=1;bar=2;baz=3'
     * 'foo:1,bar:2,baz:3'
     * use (:) or (=) for key/value separator
     * and (|) or (;) or (,) for prop delimeter
     * @param {String} str options string to parse
     * @param {String|RegExp} [delim] optional property delimeter
     * @param {String|RegExp} [sep] optional key/value separator string
     */
    function parseOpts(str, delim, sep){
        if (!str) return {};
        delim = delim || /[&|;,]/;
        sep   = sep   || /[:=]/;
        // if a delimiting or separating character is first...
        // ...use that as the respective split character
        if (str.search(delim) === 0) {
            delim = str.charAt(0);
        }
        var parts = str.split(delim);
        // if the first part is less than 3 characters...
        // ...it will be the key/value separator
        if (parts[0].trim().length < 3) {
            sep = parts.shift();
        }
        var obj = {};
        forEach(parts, function(part){
            part = part.trim();
            var prop = (part.split(sep)[0]+'').trim();
            var val  = (part.split(sep)[1]+'').trim();
            obj[prop] = realValue(val);
        });
        return obj;
    }
    xx.parseOpts = parseOpts;


    // TODO: stripped-down spawn() function...
    function spawnElement(tag, opts, content){
        consoleLog('createElement()')
    }
    xx.spawnElement = spawnElement;
    xx.createElement = spawnElement;


    //////////////////////////////
    // jQuery check
    //////////////////////////////


    if (!window.jQuery) {
        console.warn('jQuery is required for instance methods.');
        // still return 'xx' methods without jQuery
        return window.xx = xx;
    }

    // add ALL xx.* methods to $.xx.*
    $.xx = xx;

    // add non-conflicting xx.* methods directly to $.*
    // $.forOwn === xx.forOwn
    // ...but...
    // $.isFunction === $.isFunction  (not the xx.* method)
    $ = $.extend({}, xx, $);


    //////////////////////////////
    // INSTANCE METHODS
    // jQuery **REQUIRED**
    //////////////////////////////


    /**
     * Case-insensitive filter method for selecting elements with specified [str] text
     * @param {String} str - text to search for
     * @param {String} [selector] - optional filter selector to narrow down elements to search
     * @returns {{jQuery}} - returns jQuery object containing filtered elements
     */
    $.fn.containsNC = function(str, selector){
        if (!str) return this;
        var el$ = this;
        if (selector) {
            el$ = this.filter(selector);
        }
        return el$.filter(function(){
            return (this.textContent || this.innerText || '').toLowerCase().indexOf(str.toLowerCase()) > -1;
        })
    };


    /**
     * Override #attr() instance method with added functionality
     */
    (function($fnAttr){

        function notEmpty(item){
            return item > ''
        }

        function mapAttrs(attrs){
            var i = -1,
                attr,
                obj = {};
            while (++i < attrs.length){
                attr = attrs[i];
                if (attr.specified) {
                    obj[attr.name] =
                        obj[toCamelCase(attr.name)] =
                            attr.value;
                }
            }
            return obj;
        }

        // alias 'original' #attr() method before redefining
        $.fn.jQueryAttr = $.fn.attr$ = $fnAttr;

        /**
         * Return map of a selected element's defined attributes
         * @param [attrs] {string|array} - optional list of attributes to return
         * @returns {*} - returns object map of all defined attributes
         */
        $.fn.getAttributes = function(attrs){
            var attributes = this[0].attributes,
                attrMap = mapAttrs(attributes),
                names = [],
                obj = {};
            if (!this.length) {
                return null;
            }
            // normalize to an array of attribute names
            if (attrs) {
                names = [].concat(attrs||[]).join(' ').split(/[,\s+]/).filter(notEmpty);
            }
            else {
                names = toArray(attributes).map(function(item){ return item.name });
            }
            if (!names.length) {
                return attrMap;
            }
            names.forEach(function(name){
                obj[name] =
                    obj[toCamelCase(name)] =
                        attrMap[name].value;
            });
            return obj;
        };
        $.fn.getAttr = $.fn.getAttrs = $.fn.getAttributes;


        // given: <div id="foo" title="Foo" class="bar">Foo</div>
        // $('#foo').attr();
        // --> { id: 'foo', title: 'Foo', "class": 'bar' }
        $.fn.attrs = function() {
            if (!arguments.length) {
                if (!this.length) {
                    return null;
                }
                return mapAttrs(this[0].attributes);
            }
            return $fnAttr.apply(this, arguments);
        };
        $.fn.attr = $.fn.attrs;

    })(jQuery.fn.attr);


    // make sure methods are available in $.* and jQuery.* namespaces
    window.$ = window.jQuery = $;


    // only return the 'xx' static methods
    // the instance methods will be available to jQuery objects
    // and don't need to be explicitly returned
    return window.xx = xx;

}));
