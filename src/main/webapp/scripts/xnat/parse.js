/*!
 * Methods for parsing 'special' strings
 * and working with the associated data
 */

var XNAT = getObject(XNAT);

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

    // use object to store REGEX values
    var REGEX = {};

    // does the string start with one of these...?
    // ??  !?  #?  $?  ~/  {{  ((
    REGEX.parseable = /^(\?\?|!\?|#\?|\$\?|~\/|{{|\(\()/;

    // search for a value at a specific object path location
    // value: '$? /data/stuff/thing | :ResultSet:Result:0:contents'   // use lookupObjectValue()
    // value: '$? /data/stuff/thing | /ResultSet/Result/[1]/contents' // use XPath syntax (DefiantJS)
    // value: '$? /data/stuff/thing | $.ResultSet.Result[0]contents'  // use JSONpath syntax
    REGEX.useLookup = /^(\|*\s*:)/;       //   | :
    REGEX.useXPath = /^(\|*\s*\/)/;       //   | /
    REGEX.useJSONpath = /^(\|*\s*\$)/;    //   | $

    // $? = do REST call and use returned value
    // value: '$? /data/stuff/thing'
    REGEX.ajaxPrefix = /^(\$\?|~\/|\/)[:=\s]*/;

    // $: = specify expected data type for ajax request
    // value: '$? /data/stuff/thing $:json'
    REGEX.ajaxDataType = /\$:/;

    // ?? = lookup value from variable in global scope
    // (optionally use colons instead of dots for object path)
    // value: '?? :NAMESPACE:var:name'
    REGEX.lookupPrefix = /^\?\?[:=\s]*/;

    // {{ NAMESPACE:var:name }} - get value from global variable
    REGEX.lookupTest = /^{{.+}}$/;
    REGEX.lookupTrim = /^{{\s*|\s*}}$/g;
    REGEX.lookupReplace = /{{\s*(.+)\s*}}/;

    // #? = execute function by name
    // value: "#? NS.func.name()"
    REGEX.fnPrefix = /^#\?[:=\s]*/;

    REGEX.fnTest = /^#\?.+(\(\))*$/;
    REGEX.fnTrim = /^(#\?[:=\s]*)|(\(\))$/g;
    REGEX.fnReplace = /^#\?[:=\s]*(.+)\s*\(\)$/;

    // !? = use result of JS eval() from the supplied string
    // value: "!? (function(){ return $('#thing').val() })()"
    REGEX.evalPrefix = /^!\?[:=\s]*/;

    // (( evalString )) - return result of eval() using enclosed string
    REGEX.evalTest = /^\(\(.+\)\)$/;
    REGEX.evalTrim = /^(\(\()|(\)\))$/g;
    REGEX.evalReplace = /\(\(\s*(.+)\s*\)\)/;

    // special syntax to get/set form input values
    // value: '$? /data/thing | :ResultSet:Result:0 > [[yerInput]]'
    REGEX.setInputValue = />\s*\[\[(.+)]]/;
    REGEX.getInputValue = /<\s*\[\[(.+)]]/;
    REGEX.inputValueTest = /[><]+\s*\[\[.+]]/;
    REGEX.inputValueSplit = /\s*[><]\s*/;
    REGEX.inputValueTrim = /^(\s*\[\[\s*)|(\s*]]\s*)$/g;

    // @? = copy value from another element (on submit)
    REGEX.copyValue = /^(@\?)/;



    /**
     * Is [value] a 'special' string that can be further parsed?
     * @param value {String} - string to test
     * @returns {Boolean}
     */
    function parseable(value){
        var VAL = (value + '').trim();
        // a 'parseable' string MUST start with
        // one of these: ??  !?  #?  $?  ~/  {{  ((
        return REGEX.parseable.test(VAL);
    }



    /**
     * Configure multiple callback functions
     * @param fns {Function|Array} - callback function or array of functions
     * @param methods {Array} - array of strings for method names
     */
    function callbacks(fns, methods){
        var obj = this;
        // call the [success] callback from the main function
        [].concat(fns).forEach(function(fn){
            if (typeof fn === 'function') {
                // obj.result = obj.value;
                fn.call(obj, obj.result);
            }
        });
        // define methods to return for chaining
        [].concat(methods).forEach(function(method){
            obj[method] = function(callback){
                if (typeof callback === 'function') {
                    // obj.result = obj.value;
                    callback.call(obj, obj.result);
                }
                return obj;
            };
        });
        return obj;
    }



    //////////////////////////////////////////
    // MAIN CONSTRUCTOR           ////////////
    //////////////////////////////////////////
    function Parser(input){
        // NEVER transform this.input
        this.input = input;
        // ALWAYS transform this.value
        this.value = input;
        // result defaults to undefined
        this.result = undef;
        // collect callbacks
        this.doneCallbacks = [];
        this.failCallbacks = [];
    }

    //////////////////////////////////////////



    // alias 'prototype' to 'fn' for brevity
    Parser.fn = Parser.prototype;



    /**
     * Is [it] parseable?
     * @param [it] {String|*} - value to test
     * @returns {Boolean}
     */
    Parser.fn.parseable = function(it){
        this.input = it || this.input;
        return parseable(it);
    };



    /**
     * Helper for executing callback functions
     * @param fn {Function}
     * @param [status] {String} - check against this.status
     */
    function doCallback(fn, status){
        // always call this function with .call(this, ...)
        if (status === 'always' || this.status === status) {
            this.result = firstDefined(this.result, this.value);
            if (typeof fn === 'function') {
                fn.call(this, this.result);
            }
        }
    }



    /**
     * Always execute this callback despite 'status' value
     * @param fn {Function}
     * @returns {Parser}
     */
    Parser.fn.always = function(fn){
        doCallback.call(this, fn, 'always');
        return this;
    };



    /**
     * Execute [fn] callback on successful parse/ajax
     * @param fn {Function}
     * @returns {Parser}
     */
    Parser.fn.done = function(fn){
        this.doneCallbacks.push(fn);
        // obj.result = obj.value;
        doCallback.call(this, fn, 'success');
        return this;
    };
    Parser.fn.success = Parser.fn.done;



    /**
     * Execute [fn] callback for failure of parse/ajax
     * @param fn {Function}
     * @param [e] {String} - Error message
     * @returns {Parser}
     */
    Parser.fn.fail = function(fn, e){
        this.failCallbacks.push(fn);
        // obj.result = obj.value;
        doCallback.call(this, fn, 'failure');
        if (e) console.error(e);
        return this;
    };
    Parser.fn.failure = Parser.fn.fail;



    /**
     * Should we lookup value for [val] in a global (namespaced) variable?
     * @param value {String|*}
     * @param success {Function}
     * @param failure {Function}
     */
    function doLookup(value, success, failure){
        var obj = this;
        // lookup value from a global variable?
        // ?? varname
        // ?? OBJ.item | //*[contents]
        // ?? OBJ.item | $..contents.*
        // {{ varName }}
        // {{ varName | //*[contents] }}

        if (REGEX.lookupPrefix.test(value) || REGEX.lookupTest.test(value)) {

            if (jsdebug) console.log('===== doLookup =====');

            obj.value = value.replace(REGEX.lookupPrefix, '')
                             .replace(REGEX.lookupTrim, '')
                             .split('|')[0].trim();

            obj.value = obj.value.replace(/^[:]*/, ':');

            obj.path = (value.split('|')[1] || '').trim();

            try {
                obj.result = lookupObjectValue(window, obj.value);

                if (obj.result && obj.path) {
                    // optionally use XPath syntax
                    if (REGEX.useXPath.test(obj.path)) {
                        // obj.result = jsel(data).selectAll(path);
                        obj.result = JSON.search.call(JSON, obj.result, obj.path);
                    }
                    // or JSONPath syntax
                    else if (REGEX.useJSONpath.test(obj.path)) {
                        obj.result = jsonPath.call(window, obj.result, obj.path);
                    }
                    else {
                        obj.result = lookupObjectValue(obj.result, obj.path);
                    }
                }
                obj.status = 'success';
                // call the [success] callback and return 'success/done' method for chaining
                obj.done(success);
            }
            catch(e) {
                if (window.jsdebug) console.error(e);
                obj.result = obj.value;
                obj.status = 'failure';
                // call the [failure] callback and return 'failure/fail' method for chaining
                obj.fail(failure);
            }
            // --- LOOKUP RETURN --- //
            return obj;
        }
        return undef;
    }

    Parser.fn.doLookup = doLookup;



    /**
     * Should we retrieve the value via ajax and fire callbacks?
     * @param val {String} - url string
     * @param success {Function}
     * @param failure {Function}
     * @returns {*}
     */
    function doAjax(val, success, failure){

        var obj = this;
        // lookup value using XHR?
        // $? /path/to/data
        // $? /path/to/stuff | $.:ResultSet:Result:0
        // $? /path/to/other $:xml
        if (REGEX.ajaxPrefix.test(val)) {

            if (jsdebug) console.log('===== doAjax =====');

            obj.url = val.replace(REGEX.ajaxPrefix, '');
            obj.url = obj.url.split('|')[0];
            obj.url = obj.url.split(REGEX.ajaxDataType)[0];
            obj.url = obj.url.trim();
            obj.url = strReplace(obj.url);

            // if using an object path, json dataType will be assumed
            obj.path = (val.split('|')[1] || '').trim();

            if (obj.path) {
                // obj.path = '$.' + obj.path;
                // defaults to built-in object lookup
                obj.lookupMethod = lookupObjectValue;
                // optionally use XPath syntax
                if (REGEX.useXPath.test(obj.path)) {
                    obj.lookupMethod = function(data, path){
                        // return jsel(data).selectAll(path);
                        return JSON.search.call(JSON, data, path);
                    };
                }
                // or JSONPath syntax
                else if (REGEX.useJSONpath.test(obj.path)) {
                    obj.lookupMethod = function(data, path){
                        return jsonPath.call(window, data, path);
                    };
                }
                obj.dataType = 'json';
            }
            else {
                obj.dataType = (val.split(REGEX.ajaxDataType)[1] || 'text').trim();
            }

            // do XHR
            obj.request = XNAT.xhr.get({
                url: XNAT.url.rootUrl(obj.url),
                dataType: obj.dataType//,
                // success: function(data){
                //     obj.result = obj.path ? obj.lookupMethod(data, obj.path) : data;
                //     obj.status = 'success';
                //     obj.done(success);
                // },
                // error: function(statusText){
                //     obj.result = statusText;
                //     obj.status = 'failure';
                //     obj.fail(failure, arguments);
                // }
            });

            var doneCallback = obj.done || obj.success;
            obj.done = obj.success = function(callback){
                // wait until the request is done to
                // fire the success method(s)
                obj.request.done(function(VAL){
                    obj.result = obj.path ? obj.lookupMethod(VAL, obj.path) : VAL;
                    obj.status = 'success';
                    doneCallback.call(obj, callback);
                });
                return obj;
            };
            if (isFunction(success)) {
                obj.success(success);
            }

            var failCallback = obj.fail || obj.failure;
            obj.fail = obj.failure = function(callback){
                obj.request.fail(function(statusText){
                    obj.result = statusText;
                    obj.status = 'failure';
                    failCallback.call(obj, callback, arguments);
                });
                return obj;
            };
            if (isFunction(failure)) {
                obj.failure(failure);
            }

            // --- AJAX RETURN --- //
            return obj;
        }

        return undef;
    }

    Parser.fn.doAjax = doAjax;



    /**
     * Should we get the value by executing a function by name?
     * @param value {*}
     * @param success {Function}
     * @param failure {Function}
     * @returns {*}
     */
    function doFn(value, success, failure){
        var obj = this;
        var val;
        // execute a function to get the value
        // #?:NS.func.name()
        if (REGEX.fnTest.test(value)) {

            if (jsdebug) console.log('===== doFn =====');

            val = value.replace(REGEX.fnTrim, '')
                       .trim();

            try {
                // --- execute the function --- //
                obj.result = lookupObjectValue(val);
                if (obj.result === undef) {
                    obj.result = val;
                }
                if (isFunction(obj.result)) {
                    obj.result = obj.result.call(obj);
                }
                obj.status = 'success';
                // call the [success] callback and return 'success/done' method for chaining
                obj.done(success);
                // callbacks.call(obj, success, ['success', 'done']);
            }
            catch(e) {
                if (jsdebug) console.error(e);
                obj.result = val;
                obj.status = 'failure';
                // call the [failure] callback and return 'failure/fail' method for chaining
                obj.fail(failure);
                // callbacks.call(obj, failure, ['failure', 'fail']);
            }
            // --- EVAL RETURN --- //
            return obj;
        }
        return undef;
    }

    Parser.fn.doFn = doFn;



    /**
     * Should we get the value using a js eval()?
     * @param value {*}
     * @param success {Function}
     * @param failure {Function}
     * @returns {*}
     */
    function doEval(value, success, failure){
        var obj = this;
        var val;
        // use eval() to get the value
        // !? XNAT.data.context.projectId.toLowerCase();
        if (REGEX.evalPrefix.test(value) || REGEX.evalTest.test(value)) {

            if (jsdebug) console.log('===== doEval =====');

            val = value.replace(REGEX.evalPrefix, '')
                       .replace(REGEX.evalTrim, '')
                       .trim();
            try {
                // --- DO THE eval() --- //
                obj.result = eval(val);
                if (obj.result === undef) {
                    obj.result = val;
                }
                obj.status = 'success';
                // call the [success] callback and return 'success/done' method for chaining
                obj.done(success);
                // callbacks.call(obj, success, ['success', 'done']);
            }
            catch(e) {
                if (window.jsdebug) console.error(e);
                obj.result = val;
                obj.status = 'failure';
                // call the [failure] callback and return 'failure/fail' method for chaining
                obj.fail(failure);
                // callbacks.call(obj, failure, ['failure', 'fail']);
            }
            // --- EVAL RETURN --- //
            return obj;
        }
        return undef;
    }

    Parser.fn.doEval = doEval;



    /**
     * Parse [value] string and execute [success] using result
     * @param value - string to parse
     * @param [success] - success callback
     * @param [failure] - failure callback
     * @returns {Object}
     */
    Parser.fn.parseValue = function parseValue(value, success, failure){

        // object to return
        var obj = this;

        // coerce [value] to a string and trim whitespace
        obj.value = ((obj.input || obj.value || value) + '').trim();

        // return [value] if it can't be used as a string or starts with '@?'
        if (!parseable(obj.value)) {
            // --- NOT PARSEABLE RETURN --- //
            return obj;
        }

        // is there a name of an input element we're targeting?
        if (REGEX.inputValueTest.test(obj.value)) {
            obj.inputName = obj.value.split(REGEX.inputValueSplit)[1].replace(REGEX.inputValueTrim, '');
            obj.value = obj.value.split(REGEX.inputValueSplit)[0];
        }

        // all retrieval methods use the same arguments
        var args = [obj.value, success, failure];

        // return the first non-falsey result, or [obj]
        return firstDefined(
            doLookup.apply(obj, args),
            doAjax.apply(obj, args),
            doFn.apply(obj, args),
            doEval.apply(obj, args),
            obj // if not lookup, ajax, or eval...
        );
    };



    /**
     * Main method - parses [value] string and defines lookup method based on syntax:
     * '??' - lookup value in global/namespaced variable
     * '$?' - lookup value via ajax/REST
     * '#?' - Execute function by name
     * '!?' - Run js eval() on [value] string
     * @param [value] {String|*}
     * @returns {*}
     */
    XNAT.parse = function(value){
        var newParser = new Parser(value);
        return value ? newParser.parseValue(value) : newParser;
    };

    // expose parseable() function as a static method
    XNAT.parse.parseable = parseable;

    // expose RegExp items via XNAT.parse namespace
    XNAT.parse.REGEX = REGEX;

    // this script has loaded
    XNAT.parse.loaded = true;

    return XNAT.parse;

}));
