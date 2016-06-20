/*!
 * Manage Authentication Methods
 */

var XNAT = getObject(XNAT || {});

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

    var authManager, undefined,
        rootUrl = XNAT.url.rootUrl;

    XNAT.admin = 
        getObject(XNAT.admin || {});

    XNAT.admin.authManager = authManager =
        getObject(XNAT.admin.authManager || {});

    function spacer(width){
        return spawn('i.spacer', {
            style: {
                display: 'inline-block',
                width: width + 'px'
            }
        })
    }
    
    // keep track of used ports to help prevent port conflicts
    authManager.usedOrders = [];

    // keep track of authNames to prevent name conflicts
    authManager.authNames = [];

    // get the list of Auth Methods
    authManager.getAuthMethods = authManager.getAll = function(callback){
        callback = isFunction(callback) ? callback : function(){};
        authManager.usedOrders = [];
        authManager.authNames = [];
        return XNAT.xhr.get({
            url: rootUrl('/xapi/auth'),
            dataType: 'json',
            success: function(data){
                authManager.authMethods = data;
                // refresh the 'usedOrders' array every time this function is called
                data.forEach(function(item){
                    authManager.usedOrders.push(item.authOrder);
                    authManager.authNames.push(item.authName);
                });
                callback.apply(this, arguments);
            }
        });
    };

    authManager.getAuthMethod = authManager.getOne = function(authName, callback){
        if (!authName) return null;
        callback = isFunction(callback) ? callback : function(){};
        return XNAT.xhr.get({
            url: rootUrl('/xapi/auth/' + authName),
            dataType: 'json',
            success: callback
        });
    };

    authManager.get = function(authName){
        if (!authName) {
            return authManager.getAll();
        }
        return authManager.getOne(authName);
    };

    // dialog to create/edit auth methods
    authManager.dialog = function(item, opts){
        var tmpl = $('#auth-editor-template');
        var doWhat = !item ? 'New' : 'Edit';
        var isNew = doWhat === 'New';
        item = item || {};
        xmodal.open({
            title: doWhat + ' Auth Method',
            template: tmpl.clone(),
            height: 400,
            padding: '0',
            beforeShow: function(obj){
                var $form = obj.$modal.find('#auth-editor-panel');
                if (item && item.authName) {
                    // check the 'enabled' checkbox for new items
                    $form.setValues(item);
                    // forOwn(item, function(prop, val){
                    //     $form.find('[name="'+prop+'"]').val(val);
                    // });
                }
                //if (isNew) {
                //    $form.find('#auth-enabled').prop('checked', true);
                //    // $$('?enabled')[0].checked = true;
                //}
            },
            okClose: false,
            okLabel: 'Save',
            okAction: function(obj){
                // the form panel is 'authEditorTemplate' in site-admin-element.yaml
                var $form = obj.$modal.find('#auth-editor-panel');
                var authName = $form.find('#auth-name').val();
                if (!authName) {
                    xmodal.message('Auth Name is required');
                    return false;
                }
                $form.submitJSON({
                    method: 'PUT',
                    url: '/xapi/auth/' + authName,
                    validate: function(){
                        $form.find(':input').removeClass('invalid');
                        var $authName = $form.find('#auth-name');
                        var $authAddress = $form.find('#auth-address');
                        var $authType = $form.find('#auth-type');
                        var $authOrder = $form.find('#auth-order');
                        var errors = 0;
                        var errorMsg = 'Errors were found with the following fields: <ul>';

                        [$authName, $authAddress, $authType, $authOrder].forEach(function($el){
                            var el = $el[0];
                            if (!el.value) {
                                errors++;
                                errorMsg += '<li><b>' + el.title + '</b> is required.</li>';
                                $el.addClass('invalid');
                            }
                        });

                        if (isNew) {
                            if (authManager.authNames.indexOf($authName.val()) > -1) {
                                errors++;
                                errorMsg += '<li><b>Auth Name</b> already exists. Please use a different Name value.</li>';
                                $authName.addClass('invalid');
                            }
                            if (authManager.usedPorts.indexOf($port.val()) > -1) {
                                errors++;
                                errorMsg += '<li><b>Port</b> is already in use. Please use another port number.</li>';
                                $port.addClass('invalid');
                            }
                        }

                        errorMsg += '</ul>';

                        if (errors > 0) {
                            xmodal.message('Errors Found', errorMsg);
                        }

                        return errors === 0;

                    },
                    success: function(){
                        refreshTable();
                        xmodal.close(obj.$modal);
                        XNAT.ui.banner.top(2000, 'Saved.', 'success')
                    }
                });
            }
        });
    };

    // create table for Auth Methods
    authManager.table = function(container, callback){

        // initialize the table - we'll add to it below
        var authTable = XNAT.table({
            className: 'auth-methods xnat-table',
            style: {
                width: '100%',
                marginTop: '15px',
                marginBottom: '15px'
            }
        });

        // add table header row
        authTable.tr()
                .th({ addClass: 'left', html: '<b>Auth Name</b>' })
                .th({ addClass: 'left', html: '<b>Address</b>' })
                .th({ addClass: 'left', html: '<b>Type</b>' })
                .th({ addClass: 'left', html: '<b>Order</b>' })
                .th('<b>Enabled</b>')
                .th('<b>Actions</b>');

        // TODO: move event listeners to parent elements - events will bubble up
        // ^-- this will reduce the number of event listeners
        function enabledCheckbox(item){
            return spawn('div.center', [
                ['input.enabled', {
                    type: 'checkbox',
                    checked: !!item.enabled,
                    onclick: function(){
                        // save the status when clicked
                        var enabled = this.checked;
                        XNAT.xhr.put({
                            url: rootUrl('/xapi/auth/' + item.authName + '/enabled/' + enabled),
                            success: function(){
                                console.log(item.authName + (enabled ? ' enabled' : ' disabled'))
                            }
                        });
                    }
                }]
            ]);
        }

        function editLink(item, text){
            return spawn('a.link|href=#!', {
                onclick: function(e){
                    e.preventDefault();
                    authManager.dialog(item);
                }
            }, [['b', text]]);
        }

        function deleteButton(item){
            return spawn('button.btn.sm.delete', {
                onclick: function(){
                    xmodal.confirm({
                        content: "" +
                        "<p>Are you sure you'd like to delete the '" + item.authName + "' authentication method?</p>" +
                        "<p><b>This action cannot be undone.</b></p>",
                        okAction: function(){
                            XNAT.xhr.delete({
                                url: rootUrl('/xapi/auth/' + item.authName),
                                success: function(){
                                    console.log('"'+ item.authName + '" deleted');
                                    XNAT.ui.banner.top(2000, '<b>"'+ item.authName + '"</b> deleted.', 'success');
                                    refreshTable();
                                }
                            });
                        }
                    })
                }
            }, 'Delete');
        }
        
        authManager.getAll().done(function(data){
            data.forEach(function(item){
                authTable.tr({title:item.authName})
                        .td([editLink(item, item.authName)])
                        .td(item.authAddress)
                        .td(item.authType)
                        .td([['div.mono.center', item.authOrder]])
                        .td([enabledCheckbox(item)])
                        .td([['div.center', [deleteButton(item)]]]);
                // authTable.row([
                //     item.aeTitle,
                //     [['div.mono.center', item.port]],
                //     [enabledCheckbox(item)],
                //     //[['div.center', [['input|type=radio;name=defaultAuthMethod']] ]], // how do we know which one is 'default'
                //     [['div.center', [editButton(item), spacer(10), deleteButton(item)]]]
                // ]);
            });

            if (container){
                $$(container).append(authTable.table);
            }

            if (isFunction(callback)) {
                callback(authTable.table);
            }

        });

        authManager.$table = $(authTable.table);
        
        return authTable.table;

    };

    authManager.init = function(container){
        
        var $manager = $$(container||'div#auth-manager');

        authManager.$container = $manager;

        $manager.append(authManager.table());
        // authManager.table($manager);
        
        var newAuth = spawn('button.new-auth-method.btn.btn-sm.submit', {
            html: 'New Auth Method',
            onclick: function(){
                authManager.dialog();
            }
        });


        // add the start, stop, and 'add new' buttons at the bottom
        $manager.append(spawn('div', [
            newAuth,
            ['div.clear.clearfix']
        ]));
        
        return {
            element: $manager[0],
            spawned: $manager[0],
            get: function(){
                return $manager[0]
            }
        };
    };

    function refreshTable(){
        authManager.$table.remove();
        authManager.table(null, function(table){
            authManager.$container.prepend(table);
        });
    }
    authManager.refresh = refreshTable;


    authManager.init();

    return XNAT.admin.authManager = authManager;

}));
