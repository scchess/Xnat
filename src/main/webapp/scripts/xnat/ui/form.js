/*!
 * Methods for creating and working with HTML forms in XNAT.
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

    var undef, form;

    XNAT.ui =
        getObject(XNAT.ui || {});

    XNAT.parse =
        getObject(XNAT.parse || {});

    // XNAT.ui.form is assigned at the bottom



    // helper function to fire $.fn.changeVal() if available
    function changeValue(el, val){
        var $el = $$(el);
        if ($.isFunction($.fn.changeVal)) {
            $el.changeVal(val);
        }
        else {
            if ($el.val() !== val){
                $el.val(val).trigger('change');
            }
        }
        return $el;
    }



    function isButton(el){
        return '' ||
            /button/i.test(el.nodeName) ||
            /button|submit|reset/i.test(el.type);
    }


    var ARRAYLIST_TEST_REGEX = /^\[.*]$/;
    // ARRAYLIST_TEST_REGEX.test('[foo]')
    // --> true

    var ARRAYLIST_SPLIT_REGEX = /[\[|\]]/;
    // '[foo|;]'.split(ARRAYLIST_SPLIT_REGEX)
    // --> ["", "foo", ";", ""]

    var ARRAYLIST_TRIM_REGEX = /^\[|]$/g;
    // '[foo]'.replace(ARRAYLIST_TRIM_REGEX, '')
    // --> 'foo'


    /**
     * Set the value for a single form element
     * @param input {HTMLElement} - a single form input element
     * @param value {*} - data to apply to [input]
     */
    function setValue(input, value){

        // don't bother if there's no value
        // if (value === undef) {
        //     return;
        // }

        var input$ = $$(input);
        var input0 = input$[0];

        // return if nothing to work with
        if (!input0 || !input0.tagName) return;

        var inputTag = input0.tagName;
        var inputType = input0.type;

        // handle names for array lists - like '[foo|;]'
        var nameParts = (input0.name || input0.title || input0.id).split(ARRAYLIST_SPLIT_REGEX);
        var inputName = nameParts[0] || nameParts[1];
        var listDelim = input0.getAttribute('data-delim') || (nameParts[2] || ',');

        var inputValue = input$.val();
        var val = firstDefined(value, '');

        // skip buttons (?)
        if (isButton(input0)) {
            ////////// RETURN //////////
            return val;
        }

        if (inputName && isPlainObject(value)) {
            if (value.hasOwnProperty(inputName)) {
                // setValue(input$, value[inputName]); //
                val = value[inputName];
            }
            else {
                ////////// RETURN //////////
                // RETURN IF THIS IS NOT THE ELEMENT WE'RE LOOKING FOR
                return [inputName, val];
            }
        }

        if (Array.isArray(val)) {
            val = val.join(listDelim + ' ');
            input$.addClass('array-list');
            input$.dataAttr('delim', listDelim);
        }
        else {
            val = stringable(val) ? val+'' : JSON.stringify(val);
        }

        // recursively parse values?
        if (XNAT.parse.parseable(val)) {
            // --- RECURSIVE PARSE RETURN --- //
            XNAT.parse(val).done(function(result){
                if (result === val) return;
                setValue(input$, result);
            });
            ////////// RETURN //////////
            return [inputName, val];
        }

        var valString = val !== undef ? (val+'') : '';
        var inputValueString = (inputValue+'');

        // add value to [data-value] attribute
        // (except for password fields, radio buttons, and textarea elements)
        if (!/TEXTAREA/i.test(inputTag) && !/password|radio/i.test(inputType)){
            input$.dataAttr('value', valString);
        }

        // set checkboxes and radio buttons to 'checked' if value === inputValue
        if (/checkbox/i.test(inputType)) {
            if (/true|false|1|0/i.test(val)) {
                input0.checked = !!realValue(val);
                input0.value = valString;
            }
            else {
                input0.checked = (inputValue && val && inputValueString === valString) || !!val;
                input0.value = (input0.value || valString) + '';
            }
        }
        else if (/radio/i.test(inputType)){
            input0.checked = inputValueString === valString;
            // input0.value = valString;
            //if (input0.checked) input$.trigger('change');
        }
        else if (/SELECT/i.test(inputTag)) {
            if (stringable(val) && input0.multiple) {
                val = valString.split(/\s*,\s*/);
            }
            // if (Array.isArray(val)) {
                forEach(input0.options, function(menuOption, index){
                    if ([].concat(val).indexOf(menuOption.value) > -1){
                        menuOption.selected = true;
                    }
                });
            // }
        }
        else if (/INPUT|TEXTAREA/i.test(inputTag)) {
            input$.val(val);
            // changeValue(input$, val);
        }
        else {
            // lastly, if not an input element, try to set the innerHTML
            if (input0.hasOwnProperty('innerHTML')) input0.innerHTML = val;
        }

        return [inputName, val];

    }


    function parseInputValue(input, val, count){
        count = count || 0;
        if (count > 100) {
            console.warn('parseInputValue() called too many times (> 100)');
            debugger;
        }
        if (input.jquery) {
            input = input[0];
        }
        if (XNAT.parse.parseable(val)) {
            XNAT.parse(val).done(function(result){
                var obj = this;
                // if val === result, return since nothing was parsed
                if (val === result) return;
                if (++count > 100) {
                    console.warn('XNAT.parse().done() called too many times (> 100)');
                    debugger;
                }
                if (jsdebug) console.log('setValues > parsed');
                if (obj.inputName) {
                    if (jsdebug) console.log(obj.inputName);
                    input = input.form[obj.inputName];
                    // input$ = input$.filter('[name="' + obj.inputName + '"]');
                }
                parseInputValue(input, result, count++);
            });
        }
        else {
            // js2form(input.form, val);
            forEach([].concat(input), function(inputi, i){
                setValue(inputi, val);
            });
        }
    }


    /**
     *
     * @param inputs
     * @param data
     * @returns {{}}
     */
    function setFieldValues(inputs, data){
        // create data object to use for js2form()
        var obj = {};
        forEach(inputs, function(input, i){
            var val = '';
            var name =
                    input.name ||
                    input.title.split(':')[0].trim() ||
                    input.getAttribute['data-name'] ||
                    input.id;
            var nameParts = name.split(ARRAYLIST_SPLIT_REGEX);
            if (name) {
                name = nameParts[0] || nameParts[1];
                val = data[name] !== undef ? data[name] : '';
                obj[name] = isPlainObject(data) ? val : data;
                parseInputValue(input, obj);
                // setValue(input, data[name]);
            }
        });
        if (jsdebug) console.log(obj);
        return obj;
    }


    /**
     * Set values for inputs/form(s) with [values]
     * @param inputs {HTMLElement|HTMLCollection|Array|$}
     * @param values {Object|String} - data object or 'parseable' string
     * @param [count] {Number} - hack to prevent infinite recursion
     */
    function setValues(inputs, values, count){

        count = count || 1;

        if (jsdebug) {
            console.log('========== setValues ==========');
            console.log(count);
        }

        if (count > 300) {
            console.warn('The setValues() function has been called more than 300 times. There is probably something wrong.');
            return;
        }

        var inputs$ = $$(inputs);
        var inputs_ = inputs;
        // var valObj = null;  // values obj
        // var obj = {};  // output obj
        // var tmp = {};  // temp obj for non-parseable values
        var form$, form0;

        // if [inputs] is a form, gather all inputs from the form
        if (/FORM/i.test(inputs$[0].nodeName)) {
            form$ = inputs$;
            form0 = form$[0];
            inputs_ = form0.elements;
        }
        else {
            form0 = inputs_[0].form;
            // form$ = $(form0);
        }

        // no form? don't bother.
        if (!form0) return;


        if (isPlainObject(values)) {
            try {
                // use js2form() for easier setting of array values to multiple inputs
                // js2form(form0, values, '.', null, 'data-name'); //
                setFieldValues(inputs_, values);
            }
            catch (e) {
                if (jsdebug) console.error(e);
            }
        }
        else {
            if (XNAT.parse.parseable(values)) {
                XNAT.parse(values).done(function(result){
                    if (result === values) return;
                    setValues(form0, result, ++count)
                });
            }
            else {
                setFieldValues(inputs_, values);
            }
        }
    }



    /**
     * Setup constructor for XNAT.form()
     * @param formElement {HTMLElement|Object|String}
     * @constructor
     */
    function Form(formElement){

        // pass a selector string to select an existing form
        if (typeof formElement === 'string') {
            this.form$ = $$(formElement);
            this.form = this.form$[0];
        }
        // pass a spawn() config object to create a NEW form
        else if (isPlainObject(formElement)) {
            this.form = spawn('form', formElement)
        }

        this.getForm = function(formEl){
            formEl = formEl || this.form || formElement || spawn('form');
            if (formEl instanceof HTMLFormElement) {
                this.form = formEl;
                this.form$ = $(formEl);
            }
            else if (formEl.nodeName && /INPUT|SELECT|TEXTAREA/i.test(formEl.nodeName)) {
                this.form = formEl.form || formEl.parentElement;
                this.form$ = $(this.form);
            }
            else {
                this.form$ = $$(formEl);
                this.form = this.form$[0];
            }
        };

        this.getForm();

        // every instance gets a UID
        this.uid = randomID('formx', false);

        if (this.form) {
            this.uid = getDataAttrValue(this.form, 'uid') || this.uid;
            addDataAttrs(this.form, { uid: this.uid });
            this.form.id = this.form.id || this.uid;
        }

        this.element = this.form;
        this.formElement = {};
        this.buttonElement = {};
        this.inputs = this.formElements = [];
        this.buttons = this.buttonElements = [];

    }

    Form.fn = Form.prototype;



    /**
     * Spawn child elements into form
     * @param [children] {Array|String} -  array of spawn() arrays, elements, or strings; or HTML string
     * @returns {Form}
     */
    Form.fn.spawn = function(children){
        this.form$ = this.form$.spawn(children);
        this.form = this.form$[0];
        return this;
    };



    /**
     * Return the selected or spawned <form> element
     * @returns {Element}
     */
    Form.fn.get = function(){
        return this.form;
    };



    /**
     * Return the form wrapped in a jQuery instance
     * @returns {*|jQuery|HTMLElement}
     */
    Form.fn.get$ = function(){
        return (this.form$ = this.form$ || $$(this.form))
    };



    /**
     * Render the form into [container] with optional [callback]
     * @param container
     * @param [callback]
     * @returns {Form}
     */
    Form.fn.render = function(container, callback){
        if (container) {
            this.container$ = $$(container);
            this.container = this.container$[0];
            this.container$.append(this.form);
            if (typeof callback === 'function') {
                callback.call(this, this.form);
            }
        }
        return this;
    };



    /**
     * Return array of ALL matching child elements in <form>
     * @param [selector] {String|Element} - CSS selector string for element(s) to find
     * @returns {Form}
     */
    Form.fn.getElements = function(selector){
        var that = this;
        // set (this.selectedElements = null) to get elements again
        if (this.form && !this.selectedElements) {
            this.selectedElements$ = this.get$().find(selector||':input');
            // add to elements to object map keyed by [name] or [id] attribute
            this.selectedElements$.each(function(i, element){
                var name = this.name || this.getAttribute('data-name') || element.id || element.title;
                if (name) {
                    if (name in that.formElement) {
                        that.formElement[name] = [].concat(that.formElement[name], element);
                    }
                    else {
                        if (isButton(element)) {
                            that.buttonElement[name] = element;
                            that.buttonElements = (that.buttonElements || []).concat(element);
                        }
                        else {
                            that.formElement[name] = element;
                            that.formElements = (that.formElements || []).concat(element);
                        }
                    }
                }
                that.selectedElements = (that.selectedElements || []).concat(element);
            });
        }
        // this.inputs = this.formElements;
        return this;
    };
    // alias to find
    // Form.fn.find = Form.fn.getElements;



    /**
     * Return first matching child element in <form>
     * @param selector {String|Element} - CSS selector string for element to find
     * @param [i] {Number} - get element at index
     */
    Form.fn.getElement = function(selector, i){
        this.getElements(selector);
        return (this.selectedElement = this.selectedElements[i||0])
    };



    /**
     * Get a siingle form input element by name
     * @param name
     * @returns {*}
     */
    Form.fn.getInput = function(name){
        this.getElements();
        return (this.selectedElement = this.formElement[name]);
    };



    /**
     * Select specific inputs that are part of the parent <form>
     * @param [inputs] {String|Elements} - input name(s) or element to find in the parent <form>
     * @returns {Array}
     */
    Form.fn.getInputs = function(inputs){
        var that = this;
        var inputElements = [];
        this.getElements(inputs||':input');
        if (typeof inputs === 'string') {
            inputs.split(',').forEach(function(name){
                if (name in that.formElement) {
                    inputElements.push(that.formElement[name])
                }
            });
        }
        else {
            inputElements = this.formElements;
        }
        return (this.inputs = this.selectedElements = inputElements);
    };



    /**
     * Return a single value for the specified input
     * @param name {String} - [name] attribute for input to get value from
     * @returns {*|null}
     */
    Form.fn.getValue = function(name){
        this.getElements();
        return this.formElement[name].value || null;
    };



    /**
     * Return object map of values from form elements (must have a name, title, or id attribute present)
     * @param [inputs] {String|Array|Elements} - selector string, array of strings, or element(s) to find in the parent <form>
     */
    Form.fn.getValues = function(inputs){
        if (!inputs) {
            return form2js(this.form);
        }
        var obj = {};
        this.getInputs(inputs);
        forOwn(this.formElement, function(name, el){
            obj[name] = el.value || '';
        });
        return obj;
    };



    /**
     * Set the value for a SINGLE input element
     * @param el {Element|String} - Element or selector string
     * @param val
     * @returns {Form}
     */
    Form.fn.setValue = function(el, val){
        val = val || el;
        el = this.selectedElement || (this.selectedElement = this.getElement(el));
        // this.getElement(el);
        setValue(el, val);
        return this;
    };



    /**
     * Set values for all selected inputs
     * @param data {String|Object} - 'parseable' string or data object/string
     * @returns {Form}
     */
    Form.fn.setValues = function(data){
        setValues(this.get$(), data);
        return this;
    };



    /**
     * Spawn [contents] into the form
     * @param contents {String|Array|Element} - spawn() argument(s)
     * @param [data] {String|Object} - 'parseable' string or data object/string
     * @returns {Form}
     */
    Form.fn.append = function(contents, data) {
        var spawned;
        if (contents) {
            spawned = spawn.apply(null, [].concat(contents));
            this.form.appendChild(spawned);
            if (data) {
                // should we only set the value(s) for the NEW contents?
                setValues(spawned, data);
                // this.setValues(data);
            }
        }
        return this;
    };



    /**
     * Submit form data as JSON
     * @param opts {Object} - config object
     * @returns {Object} - AJAX instance
     */
    Form.fn.submitJSON = function(opts){
        return XNAT.xhr.submitJSON(this.form, opts);
    };



    /**
     * Main XNAT.form() function - select a <form> to work with
     * @param formElement
     * @returns {Form}
     */
    form = function XNATform(formElement){
        var newForm = new Form(formElement);
        if (formElement) {
            return newForm.getElements();
        }
        return newForm;
    };



    // expose as static methods on XNAT.form
    form.setValue = setValue;
    form.setValues = setValues;

    // make globally accessible through $
    $.setValues = setValues;

    // add as a jQuery method
    $.fn.setValues = function $fnSetValues(dataObj){
        setValues(this, dataObj);
        return this;
    };



    form.getValues = function formGetValues(inputs){
        return inputs !== undef ? form(inputs).getValues() : {};
    };

    // make globally accessible through $
    $.getValues = form.getValues;

    // get values of selected form elements
    $.fn.getValues = function $fnGetValues(callback){
        var values = form.getValues(this);
        if (typeof callback === 'function') {
            callback.call(this, values);
        }
        return values;
    };



    /**
     * Create a NEW form element, populating with [inputs] spawn() array
     * and setting values from [obj] object map
     * @param [opts] {Object} - options for spawning <form> element
     * @param [inputs] {Array} - spawn() content array for child elements
     * @param [obj] {Object} - object map with values for form elements
     */
    form.spawn = function formSpawn(opts, inputs, obj){
        var spawnedForm = spawn('form', opts, [].concat(inputs));
        var newForm = new Form(spawnedForm);
        if (obj !== undef) {
            newForm.setValues(obj);
        }
        return newForm;
    };



    /**
     * XNAT.form.init() will get called when processing 'form' Spawner elements
     * @param opts {Object}
     * @returns {{form, element, spawned, get: get, render: render}}
     */
    form.init = function formInit(opts){
        // ??
        console.log('XNAT.form.init()');
        var spawnedForm = form.spawn(opts);
        return {
            form: spawnedForm,
            element: spawnedForm.form,
            spawned: spawnedForm.form,
            get: function(){
                return spawnedForm.form
            },
            render: function(container, callback){
                var $container;
                if (container !== undef) {
                    $container = $$(container);
                    $container.append(spawnedForm.form);
                }
                if (isFunction(callback)) {
                    try {
                        return callback.call(spawnedForm, spawnedForm.form, $container)
                    }
                    catch (e) {
                        console.warn(e)
                    }
                }
                return spawnedForm.form;
            }
        }
    };



    // this script has loaded
    form.loaded = true;

    // make sure these have the proper assignment
    return XNAT.ui.form = XNAT.form = form;

}));

