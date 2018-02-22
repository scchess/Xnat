console.log('datatypes.js');

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
}(function() {

    XNAT.admin =
        getObject(XNAT.admin || {});
    XNAT.admin.datatype =
        getObject(XNAT.admin.datatype || {});
    XNAT.admin.datatypes =
        getObject(XNAT.admin.datatypes || {});
    
    // table renderer
    console.log('datatypes.js - render table');

    XNAT.admin.displayDataTypes = function(projectTerm){
        function checkMarkFormCell(data,key,i){
            var els = [];
            var val = (data[key]==='1') ? 'true' : '';
            var shortkey = key.split('.')[1];
            if (data[key]==='1') {
                els.push(spawn('i.fa.fa-check'));
                els.push(spawn('i.hidden.filtering.'+shortkey,'1'));
            }
            else {
                els.push(spawn('i.hidden.filtering.'+shortkey,'2'))
            }
            els.push(spawn('input',{
                type: 'hidden',
                name: 'xdat:security/element_security_set/element_security['+i+']/'+shortkey,
                value: val
            }))
            return spawn('!',els);
        }

        function getI(xsiType){
            return Object.keys(XNAT.admin.datatypes).indexOf(xsiType.toString());
        }

        // set up custom filter menus
        function filterMenuElement(prop){
            if (!prop) return false;
            // call this function in context of the table
            var datatypeTable = $(this);
            var FILTERCLASS = 'filter-' + prop;
            // var FILTERCLASS = 'hidden';
            return {
                id: 'datatype-filter-select-' + prop,
                on: {
                    change: function(){
                        var selectedValue = $(this).val();
                        // console.log(selectedValue);
                        datatypeTable.find('i.filtering.'+prop).each(function(){
                            var row = $(this).closest('tr');
                            if (selectedValue === 'all') {
                                row.removeClass(FILTERCLASS);
                                return;
                            }
                            row.addClass(FILTERCLASS);
                            if (selectedValue == this.textContent) {
                                row.removeClass(FILTERCLASS);
                            }
                        })
                    }
                }
            };
        }

        console.log('dtTable.js');

        function dtTable(){
            var data = [], datarows = [];

            Object.keys(XNAT.admin.datatypes).forEach(function(datatype){
                data.push(datatype);
            });

            var styles = {
                element: (260-24)+'px',
                singular: (220-24)+'px',
                plural: (220-24) + 'px',
                code: (90-24) + 'px',
                accessible: (70-24) + 'px',
                secure: (70-24) +'px',
                searchable: (70-24) + 'px',
                browse: (70-24) + 'px',
                actions: (60-24) + 'px'
            };

            return {
                kind: 'table.dataTable',
                name: 'datatypeListing',
                id: 'datatype-listing',
                data: data,
                before: {
                    filterCss: {
                        tag: 'style|type=text/css',
                        content: '\n' +
                        '#datatype-listing td.element { width: ' + styles.element + '; } \n' +
                        '#datatype-listing td.singular { width: ' + styles.singular + '; } \n' +
                        '#datatype-listing td.plural { width: ' + styles.plural + '; } \n' +
                        '#datatype-listing td.code { width: ' + styles.code + '; } \n' +
                        '#datatype-listing td.accessible { width: ' + styles.accessible + '; } \n' +
                        '#datatype-listing td.secure { width: ' + styles.secure + '; } \n' +
                        '#datatype-listing td.searchable { width: ' + styles.searchable + '; } \n' +
                        '#datatype-listing td.browse { width: ' + styles.browse + '; } \n' +
                        '#datatype-listing td.actions { width: ' + styles.actions + '; } \n'
                    }
                },
                table: {
                    classes: 'highlight hidden'
                },
                trs: function(tr,xsitype){
                    var link = XNAT.admin.datatypes[xsitype].legacyEditLink;
                    addDataAttrs(tr, { 'legacylink': link, 'xsitype': xsitype });
                },
                filter: 'element, singular, plural, code',
                items: {
                    element: {
                        label: 'Element',
                        sort: true,
                        td: {className: 'element'},
                        apply: function () {
                            return spawn('a.data-type-link', {
                                href: '#!',
                                title: 'Edit Attributes For ' + this,
                                style: { 'font-weight': 'bold' },
                                html: this
                            })
                        }
                    },
                    singular: {
                        label: 'Singular',
                        sort: true,
                        td: {
                            className: 'singular name-singular',
                            title: 'Term used to describe one instance of this data'
                        },
                        apply: function () {
                            var that = XNAT.admin.datatypes[this],
                                i = getI(this);
                            return spawn('!', [
                                spawn('i.hidden', that['xdat:element_security.singular']),
                                spawn('input', {
                                    type: 'text',
                                    name: 'xdat:security/element_security_set/element_security[' + i + ']/singular',
                                    value: that['xdat:element_security.singular'],
                                    size: 20
                                })
                            ])
                        }
                    },
                    plural: {
                        label: 'Plural',
                        sort: true,
                        td: {
                            className: 'plural name-plural',
                            title: 'Term used to describe more than one instance of this data'
                        },
                        apply: function () {
                            var that = XNAT.admin.datatypes[this],
                                i = getI(this);
                            return spawn('!', [
                                spawn('i.hidden', that['xdat:element_security.plural']),
                                spawn('input', {
                                    type: 'text',
                                    name: 'xdat:security/element_security_set/element_security[' + i + ']/plural',
                                    value: that['xdat:element_security.plural'],
                                    size: 20
                                })
                            ])
                        }
                    },
                    code: {
                        label: 'Code',
                        sort: true,
                        td: {
                            className: 'code center',
                            title: 'Short code used in the creation of IDs'
                        },
                        apply: function () {
                            var that = XNAT.admin.datatypes[this],
                                i = getI(this);
                            return spawn('!', [
                                spawn('i.hidden', that['xdat:element_security.code']),
                                spawn('input', {
                                    type: 'text',
                                    name: 'xdat:security/element_security_set/element_security[' + i + ']/code',
                                    value: that['xdat:element_security.code'],
                                    size: '8',
                                    maxlength: '5'
                                })
                            ])
                        }
                    },
                    accessible: {
                        label: 'ACC',
                        sort: true,
                        td: {
                            className: 'center',
                            title: 'Accessible: Whether or not unspecified users can use this data-type in their '+projectTerm
                        },
                        filter: function(table){
                            return spawn('div.center', [XNAT.ui.select.menu({
                                value: 'all',
                                options: [
                                    { label: 'All', value: 'all' },
                                    { label: 'Yes', value: '1' },
                                    { label: 'No', value: '2' }
                                ],
                                element: filterMenuElement.call(table, 'accessible')
                            }).element])
                        },
                        apply: function(){
                            var that = XNAT.admin.datatypes[this],
                                i = getI(this);
                            return checkMarkFormCell(that,'xdat:element_security.accessible',i)
                        }
                    },
                    secure: {
                        label: 'SEC',
                        sort: true,
                        td: {
                            className: 'center',
                            title: 'Secured: Whether or not access to data of this type should be restricted'
                        },
                        filter: function(table){
                            return spawn('div.center', [XNAT.ui.select.menu({
                                value: 'all',
                                options: [
                                    { label: 'All', value: 'all' },
                                    { label: 'Yes', value: '1' },
                                    { label: 'No', value: '2' }
                                ],
                                element: filterMenuElement.call(table, 'secure')
                            }).element])
                        },
                        apply: function(){
                            var that = XNAT.admin.datatypes[this],
                                i = getI(this);
                            return checkMarkFormCell(that,'xdat:element_security.secure',i)
                        }
                    },
                    searchable: {
                        label: 'SCH',
                        sort: true,
                        td: {
                            className: 'center',
                            title: 'Searchable: Whether or not data of this type should be searchable'
                        },
                        filter: function(table){
                            return spawn('div.center', [XNAT.ui.select.menu({
                                value: 'all',
                                options: [
                                    { label: 'All', value: 'all' },
                                    { label: 'Yes', value: '1' },
                                    { label: 'No', value: '2' }
                                ],
                                element: filterMenuElement.call(table, 'searchable')
                            }).element])
                        },
                        apply: function(){
                            var that = XNAT.admin.datatypes[this],
                                i = getI(this);
                            return checkMarkFormCell(that,'xdat:element_security.searchable',i)
                        }
                    },
                    browse: {
                        label: 'BRW',
                        sort: true,
                        td: {
                            className: 'center',
                            title: 'Browseable: Whether or not data of this type should be browseable'
                        },
                        filter: function(table){
                            return spawn('div.center', [XNAT.ui.select.menu({
                                value: 'all',
                                options: [
                                    { label: 'All', value: 'all' },
                                    { label: 'Yes', value: '1' },
                                    { label: 'No', value: '2' }
                                ],
                                element: filterMenuElement.call(table, 'browse')
                            }).element])
                        },
                        apply: function(){
                            var that = XNAT.admin.datatypes[this],
                                i = getI(this);
                            return checkMarkFormCell(that,'xdat:element_security.browse',i)
                        }
                    },
                    actions: {
                        label: 'Actions',
                        sort: false,
                        td: {
                            className: 'center actions inline-actions-menu-container'
                        },
                        apply: function(){
                            var that = XNAT.admin.datatypes[this],
                                i = getI(this);
                            return spawn('!',[
                                spawn('div.inline-actions-menu-toggle', {
                                    data: {
                                        actionset: 'datatypeActions',
                                        id: this
                                    },
                                    html: '<i class="fa fa-ellipsis-h"></i>'
                                }),
                                spawn('ul.inline-actions-menu', {
                                    style: { display: 'none' }
                                }),
                                spawn('input',{
                                    type: 'hidden',
                                    name: 'xdat:security/element_security_set/element_security['+i+']/element_name',
                                    value: this
                                })
                            ])
                        }
                    }
                }
            }
        }

        var container = jq('#datatype-table-container');
        container.empty().append(
            XNAT.spawner.spawn({ table: dtTable() }).render(container)
        )
    };

    // attribute editors
    console.log('datatype.js - attribute editor')
    var editDatatypeFormObj = function(datatype) {
        return {
            kind: 'panel.form',
            id: 'edit-xsitype-form',
            header: false,
            footer: false,
            element: {
                style: { border: 'none', margin: '0' },
                addClass: 'optOutOfXnatDefaultFormValidation',
                action: '/app/action/ModifyItem',
                method: 'post'
            },
            contents: {
                singular: {
                    kind: 'panel.input.text',
                    name: 'xdat:element_security.singular',
                    label: 'Singular Name'
                },
                plural: {
                    kind: 'panel.input.text',
                    name: 'xdat:element_security.plural',
                    label: 'Plural Name'
                },
                code: {
                    kind: 'panel.input.text',
                    name: 'xdat:element_security.code',
                    label: 'Code'
                },
                category: {
                    kind: 'panel.input.hidden',
                    name: 'xdat:element_security.category',
                    value: ''
                },
                quarantine: {
                    kind: 'panel.input.hidden',
                    name: 'xdat:element_security.quarantine',
                    value: '0'
                },
                sequence: {
                    kind: 'panel.input.hidden',
                    name: 'xdat:element_security.sequence',
                    value: '0'
                },
                accessible: {
                    kind: 'panel.input.switchbox',
                    name: 'xdat:element_security.accessible',
                    label: 'Accessible',
                    onText: 'Yes',
                    offText: 'No',
                    values: '1|0'
                },
                secure: {
                    kind: 'panel.input.switchbox',
                    name: 'xdat:element_security.secure',
                    label: 'Secure',
                    onText: 'Yes',
                    offText: 'No',
                    values: '1|0'
                },
                searchable: {
                    kind: 'panel.input.switchbox',
                    name: 'xdat:element_security.searchable',
                    label: 'Searchable',
                    onText: 'Yes',
                    offText: 'No',
                    values: '1|0'
                },
                browse: {
                    kind: 'panel.input.switchbox',
                    name: 'xdat:element_security.browse',
                    label: 'Browseable',
                    onText: 'Yes',
                    offText: 'No',
                    values: '1|0'
                },
                advanced: {
                    tag: 'div#advanced-settings',
                    contents: {
                        advancedSeparator: {
                            tag: 'hr',
                            element: {
                                style: {
                                    margin: '2em 0'
                                }
                            }
                        },
                        advancedTitle: {
                            tag: 'p',
                            content: '<strong>Advanced Settings</strong>'
                        },
                        secureRead: {
                            kind: 'panel.input.switchbox',
                            name: 'xdat:element_security.secure_read',
                            label: 'Secure Read',
                            onText: 'Yes',
                            offText: 'No',
                            values: '1|0'
                        },
                        secureCreate: {
                            kind: 'panel.input.switchbox',
                            name: 'xdat:element_security.secure_create',
                            label: 'Secure Create',
                            onText: 'Yes',
                            offText: 'No',
                            values: '1|0'
                        },
                        secureEdit: {
                            kind: 'panel.input.switchbox',
                            name: 'xdat:element_security.secure_edit',
                            label: 'Secure Edit',
                            onText: 'Yes',
                            offText: 'No',
                            values: '1|0'
                        },
                        secureDelete: {
                            kind: 'panel.input.switchbox',
                            name: 'xdat:element_security.secure_delete',
                            label: 'Secure Delete',
                            onText: 'Yes',
                            offText: 'No',
                            values: '1|0'
                        },
                        secondaryPassword: {
                            kind: 'panel.input.switchbox',
                            name: 'xdat:element_security.secondary_password',
                            label: 'Requires Secondary Password',
                            onText: 'Yes',
                            offText: 'No',
                            values: '1|0'
                        },
                        usage: {
                            kind: 'panel.input.textarea',
                            name: 'xdat:element_security.usage',
                            label: 'Restricted Usage',
                            description: 'Which projects can use this data type? (Specify a comma-separated list of project IDs)',
                            element: {
                                rows: '2'
                            }
                        },
                        restrictIp: {
                            kind: 'panel.input.switchbox',
                            name: 'xdat:element_security.secure_ip',
                            label: 'Restrict By IP Address',
                            onText: 'Yes',
                            offText: 'No',
                            values: '1|0'
                        }
                    }
                }
            }
        }
    };

    var formPresubmit = function(datatype){
        var formInit = [
            XNAT.ui.panel.input.hidden({
                name: 'xdat:element_security.element_name',
                value: datatype['xdat:element_security.element_name']
            }),
            XNAT.ui.panel.input.hidden({
                name: 'edit_screen',
                value: 'XDATScreen_dataTypes.vm'
            }),
            XNAT.ui.panel.input.hidden({
                name: 'ELEMENT_0',
                value: 'xdat:element_security'
            }),
            XNAT.ui.panel.input.hidden({
                name: 'eventSubmit_doSetup',
                value: 'Submit'
            })
        ];

        var psf = datatype.securityFields;
        if (psf){
            psf.forEach(function(securityField,i){
                formInit.push(
                    XNAT.ui.panel.input.hidden({
                        name: 'xdat:element_security.primary_security_fields.primary_security_field__'+i+'.primary_security_field',
                        value: securityField.field
                    })
                );
                formInit.push(
                    XNAT.ui.panel.input.hidden({
                        name: 'xdat:element_security.primary_security_fields.primary_security_field__'+i+'.xdat_primary_security_field_id',
                        value: securityField.id
                    })
                );
            });
        }

        return formInit;
    };

    XNAT.admin.datatype.edit = function(xsiType){
        var datatype = XNAT.admin.datatypes[xsiType];
        if (!datatype || typeof datatype !== 'object') {
            XNAT.ui.banner.top(2000,'Data type '+xsiType+ ' not recognized','error');
            return false;
        }

        var title = (datatype['xdat:element_security.plural'].length) ?
        'Edit Actions for '+datatype['xdat:element_security.plural'] :
        'Edit Actions for '+datatype['xdat:element_security.element_name'];

        XNAT.ui.dialog.open({
            title: title,
            width: 600,
            content: '<div id="edit-xsitype-form-container"></div>',
            beforeShow: function(obj){
                var container = obj.$modal.find('#edit-xsitype-form-container');
                XNAT.spawner.spawn({ form: editDatatypeFormObj(datatype) }).render(container);

                var form = obj.$modal.find('form');
                form.setValues(datatype);

                form.append(spawn('!', formPresubmit(datatype)));
            },
            buttons: [
                {
                    label: 'Save Changes',
                    isDefault: true,
                    close: true,
                    action: function(obj){
                        var form = obj.$modal.find('form');
                        submitEditForm(form);
                    }
                },
                {
                    label: 'Edit Data Type Actions',
                    close: true,
                    action: function(){
                        XNAT.admin.datatype.editActions(xsiType);
                    }
                },
                {
                    label: 'Cancel',
                    close: true
                }
            ]
        })
    };

    XNAT.admin.datatype.addActionRow = function(table){
        var i = jq(table).data('action-count')-1;

        var tr = jq(table).find('tbody').find('tr')[i];
        var newTr = jq(tr).clone();

        jq(newTr[0]).find('input').each(function(){
            var name = jq(this).prop('name');
            name = name.replace(i,i+1);
            jq(this).prop('name',name);
            jq(this).val('');
        });

        jq(table)
            .data('action-count',i+2)
            .find('tbody').append(newTr[0]);
    };
    XNAT.admin.datatype.deleteActionRow = function(event){
        event.preventDefault;
        var row = jq(event.target).parents('tr');

        jq(row).slideUp()
            .find('input.delete').prop('disabled',false);
    };

    XNAT.admin.datatype.editActions = function(xsiType){
        var datatype = XNAT.admin.datatypes[xsiType];
        if (!datatype || typeof datatype !== 'object') {
            XNAT.ui.banner.top(2000,'Data type '+xsiType+ ' not recognized','error');
            return false;
        }

        var reportActionObj = {
            id: 'xdat:element_security.element_actions.element_action__0.xdat_element_action_type_id',
            displayName: 'xdat:element_security.element_actions.element_action__0.display_name',
            actionName: 'xdat:element_security.element_actions.element_action__0.element_action_name',
            grouping: 'xdat:element_security.element_actions.element_action__0.grouping',
            popup: 'xdat:element_security.element_actions.element_action__0.popup',
            secureAccess: 'xdat:element_security.element_actions.element_action__0.secureAccess',
            parameterString: 'xdat:element_security.element_actions.element_action__0.parameterString',
            remove: 'REMOVE__0=xdat:element_action_type.xdat_element_action_type_id'
        };
        var listingActionObj = {
            id: 'xdat:element_security.listing_actions.listing_action__0.xdat_element_security_listing_action_id',
            displayName: 'xdat:element_security.listing_actions.listing_action__0.display_name',
            actionName: 'xdat:element_security.listing_actions.listing_action__0.element_action_name',
            grouping: 'xdat:element_security.listing_actions.listing_action__0.grouping',
            popup: 'xdat:element_security.listing_actions.listing_action__0.popup',
            secureAccess: 'xdat:element_security.listing_actions.listing_action__0.secureAccess',
            parameterString: 'xdat:element_security.listing_actions.listing_action__0.parameterString',
            remove: 'REMOVE__0=xdat:element_security_listing_action.xdat_element_security_listing_action_id'
        };

        var actionTable = function(actions,inputobj){
            var atTable = XNAT.table({
                className: 'xnat-table clean narrow',
                style: {
                    width: '100%',
                    'margin-top': '5px',
                    'margin-bottom': '15px'
                },
                data: {
                    'action-count': actions.length
                }
            });
            atTable.thead().tr()
                .th({ addClass: 'left' })
                .th({ addClass: 'left' }, '<b>Label</b>')
                .th({ addClass: 'left' }, '<b>Action Screen Name</b>')
                .th({ addClass: 'left' }, '<b>Grouping</b>')
                .th({ addClass: 'left' }, '<b>Popup</b>')
                .th({ addClass: 'left' }, '<b>Secure Access</b>')
                .th({ addClass: 'left' }, '<b>Additional Params</b>')
            atTable.tbody();

            if (actions.length) {
                actions.forEach(function (action, i) {
                    function resolveName(name, i) {
                        var arr = name.split('0');
                        return arr[0] + i + arr[1];
                    }

                    function textInput(key, i, val) {
                        return spawn('input', {
                            type: 'text',
                            name: resolveName(inputobj[key], i),
                            value: action[key]
                        })
                    }

                    function hiddenInput(key, i, val) {
                        return spawn('input', {
                            type: 'hidden',
                            name: resolveName(inputobj[key], i),
                            value: action[key]
                        })
                    }

                    function deleteIcon(key, i) {
                        return spawn('button.btn.sm.delete.delete-action-row', {
                            title: 'Delete Action'
                        }, [
                            spawn('i.fa.fa-trash'),
                            spawn('input.delete', {
                                type: 'hidden',
                                disabled: 'disabled',
                                name: resolveName(inputobj['remove'],i),
                                value: action[key]
                            })
                        ]);
                    }

                    function selectPopup(key, i) {
                        function selectOpt(val) {
                            return (val === action[key]) ? 'selected' : false
                        }

                        return spawn('select', {
                            name: resolveName(inputobj[key], i)
                        }, [
                            spawn('option', {value: '', selected: selectOpt('')}),
                            spawn('option', {value: 'always', selected: selectOpt('always')}, 'always'),
                            spawn('option', {value: 'sometimes', selected: selectOpt('sometimes')}, 'sometimes'),
                            spawn('option', {value: 'never', selected: selectOpt('never')}, 'never')
                        ])
                    }

                    function selectAccess(key, i) {
                        function selectOpt(val) {
                            return (val === action[key]) ? 'selected' : false
                        }

                        return spawn('select', {
                            name: resolveName(inputobj[key], i)
                        }, [
                            spawn('option', {value: '', selected: selectOpt('')}),
                            spawn('option', {value: 'edit', selected: selectOpt('edit')}, 'edit'),
                            spawn('option', {value: 'delete', selected: selectOpt('delete')}, 'delete')
                        ])
                    }

                    // use the toBody() selector rather than the tbody() selector
                    // to specify that we want to add this row to the existing table body, not create a new one
                    atTable.toBody().tr()
                        .td([deleteIcon('id', i)])
                        .td([textInput('displayName', i), hiddenInput('id', i)])
                        .td([textInput('actionName', i)])
                        .td([textInput('grouping', i)])
                        .td([selectPopup('popup', i)])
                        .td([selectAccess('secureAccess', i)])
                        .td([textInput('parameterString', i)])
                })
            }

            function addRowButton(){
                return spawn('button.btn.sm',{
                    onclick: function(e){
                        e.preventDefault();
                        var table = jq(this).parents('table');
                        XNAT.admin.datatype.addActionRow(table)
                    },
                    html: '<i class="fa fa-plus"></i> Add Action'
                })
            }
            atTable.tfoot().tr({class: 'add-new-action-row'})
                .td({colSpan: '7'}, [ addRowButton()]);

            return atTable.table;
        };

        // open & populate editor dialog
        var url = XNAT.url.csrfUrl('/app/action/ModifyItem/popup/true?popup=true');

        var title = (datatype['xdat:element_security.plural'].length) ?
            'Edit Actions for '+datatype['xdat:element_security.plural'] :
            'Edit Actions for '+datatype['xdat:element_security.element_name'];

        XNAT.ui.dialog.open({
            width: 960,
            title: title,
            content: '<form id="action-table-form"></form>',
            beforeShow: function(obj){
                var form = obj.$modal.find('#action-table-form');
                form.append( spawn('h3','Report Actions') );
                form.append( spawn('div',{style: { 'margin-bottom': '2em' }}, [ actionTable(datatype.reportActions, reportActionObj) ]) );

                form.append( spawn('h3','Listing Actions') );
                form.append( spawn('div',{style: { 'margin-bottom': '2em' }}, [ actionTable(datatype.listingActions, listingActionObj) ]) );

                form.append( spawn('!', formPresubmit(datatype)));
            },
            buttons: [
                {
                    label: 'Save Changes',
                    isDefault: true,
                    close: true,
                    action: function(obj){
                        var form = obj.$modal.find('form');
                        submitEditForm(form);
                    }
                },
                {
                    label: 'Edit Data Type Attributes',
                    close: true,
                    action: function(){
                        XNAT.admin.datatype.edit(xsiType);
                    }
                },
                {
                    label: 'Cancel',
                    close: true
                }
            ]
        })
    };

    XNAT.admin.datatype.delete = function(xsiType){
        var deleteUrl = XNAT.url.csrfUrl('/app/action/DeleteXdatElementSecurity');
        var data = 'search_element=xdat:element_security';
        data += '&search_field=xdat:element_security.element_name';
        data += '&search_value='+xsiType;

        XNAT.dialog.open({
            title: 'Confirm Data Type Deletion',
            width: 450,
            content: 'Are you sure you want to delete the <strong>'+xsiType+'</strong> data type? This operation cannot be undone.',
            buttons: [
                {
                    label: 'Confirm and Delete Data Type',
                    isDefault: true,
                    close: true,
                    action: function(){
                        XNAT.xhr.ajax({
                            url: deleteUrl,
                            method: 'POST',
                            data: data,
                            cache: false,
                            success: function(data){
                                console.log(data);
                                XNAT.ui.banner.top(2000,'Data type '+ xsiType +' deleted','success');
                                window.location.reload();
                            },
                            fail: function(e){
                                console.log(e);
                                XNAT.ui.banner.top(2000,'ERROR. Could not delete '+ xsiType +' data type.','error');
                            }
                        })
                    }
                },
                {
                    label: 'Cancel',
                    close: true
                }
            ]
        })
    };

    function submitEditForm(form){
        var url = XNAT.url.csrfUrl('/app/action/ModifyItem/popup/true?popup=true');

        // insert a hack for checkbox-based switchboxes
        form.find('input.controller.switchbox').each(function(){
            if ($(this).prop('type') === 'checkbox') $(this).prop('checked','checked');
        });

        // insert a hack to NULLify empty text inputs
        form.find('input[type=text]').each(function(){
            if ($(this).val().length === 0) $(this).val('NULL')
        });

        var formData = form.serialize();

        XNAT.xhr.ajax({
            url: url,
            method: 'POST',
            data: formData,
            cache: false,
            success: function(data){
                console.log(data);
                XNAT.ui.banner.top(2000,'Data type updated','success');
                window.location.reload();
            },
            fail: function(e){
                XNAT.ui.banner.top(2000,'ERROR. Could not update data type.','error');
                console.log(e);
            }
        })
    }

    jq(document).on('click','.delete-action-row',function(e){
        e.preventDefault();
        XNAT.admin.datatype.deleteActionRow(e);
    });

}));