/*!
 * Advanced interractions for Project Settings dialog elements
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

    var undef, projectSettings;

    XNAT.app = getObject(XNAT.app || {});

    XNAT.app.projectSettings = projectSettings =
        getObject(XNAT.app.projectSettings || XNAT.projectSettings || {});

    if (projectSettings.loaded) {
        console.log('projectSettings.js already loaded');
        return;
    }

    if (jsdebug) console.log('projectSettings.js');

    XNAT.data = getObject(XNAT.data);
    XNAT.data.context = getObject(XNAT.data.context);
    XNAT.data.project = getObject(XNAT.data.project);

    var projectId = XNAT.data.context.projectID;
    var projectLabel = XNAT.data.context.label;
    var projectName = XNAT.data.context.projectName;

    function getProjectId(id){
        return (projectId = id || projectId || getQueryStringValue('id') || getUrlHashValue('id'));
    }
    projectSettings.getProjectId = getProjectId;
    getProjectId();


    function getInputByName(form$, name){
        return $$(form$).find('[name="' + name + '"]');
    }

    function setInputValue(form$, name, value){
        getInputByName(form$, name).val(value+'').change();
    }

    // check for { ResultSet: { Result: [] } }
    function getResultSetResult(data){
        return (data && data.ResultSet && data.ResultSet.Result) ?
            data.ResultSet.Result :
            [];
    }

    /**
     *
     * @param url {String} - url string
     * @param [successOrElements] {Function|Array} - Success callback or array with form and input name
     * @param [error] {String|Function} - Error message string or failure callback
     */
    function getSettings(url, successOrElements, error){
        return XNAT.xhr.get({
            url: XNAT.url.restUrl(url),
            success: function(data){
                if (jsdebug) console.log('success: ' + url);
                if (!successOrElements) {
                    return;
                }
                if (Array.isArray(successOrElements)) {
                    successOrElements[2] = data || successOrElements[2] || ''; // add returned value to argument array
                    setInputValue.apply(this, successOrElements);
                }
                else {
                    try {
                        successOrElements.apply(this, arguments);
                    }
                    catch (e) {
                        if (jsdebug) console.error(e);
                    }
                }
            },
            failure: function(e){
                if (jsdebug) {
                    console.error('error:');
                    console.error(arguments);
                }
                if (!error) {
                    return;
                }
                if (isString(error)) {
                    console.log(error || 'An error occurred: ');
                    console.error(e);
                    // XNAT.ui.dialog.message(false, (error||'An error occurred: ') +  '<br><br>' + e);
                }
                else {
                    try {
                        error.apply(this, arguments);
                    }
                    catch (e) {
                        if (jsdebug) console.error(e);
                    }
                }
            }
        });
    }

    function submitSettings(obj){
        var URL = XNAT.url.csrfUrl(obj.url);
        var success = obj.success || '';
        var error = obj.failure || obj.error || '';
        return XNAT.xhr.request(extend(true, {}, obj, {
            method: obj.method || 'put',
            url: URL,
            success: function(data){
                if (jsdebug) console.log('success: ' + URL);
                if (isString(success)) {
                    XNAT.ui.banner.top(3000, success || 'Settings saved.', 'success');
                }
                else if (isFunction(success)) {
                    try {
                        success.apply(this, arguments);
                    }
                    catch (e) {
                        if (jsdebug) console.error(e);
                    }
                }
            },
            failure: function(e){
                if (jsdebug) {
                    console.error('error:');
                    console.error(arguments);
                }
                if (isString(error)) {
                    // console.log((error || 'An error occurred: ') + e);
                    XNAT.ui.dialog.message(false, (error || 'An error occurred: ') +  '<br><br>' + (isObject(e) ? JSON.stringify(e) : e+''));
                }
                else if (isFunction(error)) {
                    try {
                        error.apply(this, arguments);
                    }
                    catch (e) {
                        if (jsdebug) console.error(e);
                    }
                }
            }
        }))
    }





    ////////////////////////////////////////////////////////////////////////////////
    // Project Quarantine Settings
    ////////////////////////////////////////////////////////////////////////////////

    function quarantineUrl(value){
        return '/data/projects/' + projectId + '/quarantine_code' + (value !== undef ? ('/' + value) : '');
    }

    projectSettings.getQuarantineCode = function getQuarantineCode(form){
        var form$ = (form !== undef) ? $$(form) : $$('#quarantine-settings');
        getSettings(
            quarantineUrl(),
            // [form$, 'quarantine'],
            function(code) {
                getInputByName(form$, 'quarantine').filter('[value="' + code + '"]').checked(true);
            },
            'An error occurred getting quarantine preferences.'
        );
    };

    projectSettings.setQuarantineCode = function setQuarantineCode(form, e){
        if (e && e.preventDefault) e.preventDefault();
        var form$ = (form !== undef) ? $$(form) : $$('#quarantine-settings');
        var code = getInputByName(form$, 'quarantine').filter(':checked').val();
        submitSettings({
            url: quarantineUrl(code),
            // success: function() {
            //     XNAT.ui.banner.top(2000, 'Quarantine set to ' + (/1/.test(code) ? 'Yes' : 'No'), 'success')
            // },
            error: 'An error occurred setting quarantine preferences.'
        });
    };
    ////////////////////////////////////////////////////////////////////////////////





    ////////////////////////////////////////////////////////////////////////////////
    // Project Prearchive Settings
    ////////////////////////////////////////////////////////////////////////////////

    function prearchiveUrl(value){
        return '/data/projects/' + projectId + '/prearchive_code' + (value !== undef ? ('/' + value) : '');
    }

    projectSettings.getPrearhchiveCode = function getPrearhchiveCode(form){
        var form$ = (form !== undef) ? $$(form) : $$('#prearchive-settings');
        getSettings(
            prearchiveUrl(),
            // [form$, 'prearchive'],
            function(code) {
                getInputByName(form$, 'prearchive').filter('[value="' + code + '"]').checked(true);
            },
            'An error occurred getting prearchive preferences.'
        );
    };

    projectSettings.setPrearchiveCode = function setPrearchiveCode(form, e){
        if (e && e.preventDefault) e.preventDefault();
        var form$ = (form !== undef) ? $$(form) : $$('#prearchive-settings');
        var code = getInputByName(form$, 'prearchive').filter(':checked').val();
        submitSettings({
            url: prearchiveUrl(code),
            // succes: function() {
            //     XNAT.ui.banner.top(2000, 'Quarantine set to ' + (/1/.test(code) ? 'Yes' : 'No'), 'success')
            // },
            error: 'An error occurred setting prearchive preferences.'
        });
    };
    ////////////////////////////////////////////////////////////////////////////////





    ////////////////////////////////////////////////////////////////////////////////
    // Project Anon Script
    ////////////////////////////////////////////////////////////////////////////////

    function projectAnonUrl(item, param){
        return '/data/config/edit/projects/' + projectId + '/image/dicom/' + item + (param !== undef ? ('?' + param) : '' );
    }

    projectSettings.getProjectAnonScript = function getProjectAnonScript(form){
        waitForElement(1, (form || '#project-anon-form'), function(form$){

            // make sure the 'enabled' checkbox is present
            waitForElement(1, form$.find('#project-anon-enable'), function(enabled$){
                // 'enabled' checkbox
                getSettings(
                    projectAnonUrl('status', 'format=json'),
                    function(data) {

                        if (jsdebug) {
                            console.log('===================');
                            console.log('PROJECT ANON SCRIPT');
                            console.log('===================');
                        }

                        var result = getResultSetResult(data);
                        var status = result[0] && 'edit' in result[0] ? result[0].edit : false;
                        status = realValue(status);
                        enabled$.checked(status);
                        // do we want to disable/enable script editing
                        // based on 'enabled' status?
                        // enabled$.on('click', function(){
                        //     script$.enabled(enabled$[0].checked);
                        // });
                        // script$.enabled(status);
                    },
                    'An error occurred getting anon script status.'
                );
            });

            // make sure the script textarea is present
            waitForElement(1, form$.find('#project-anon-script'), function(script$){
                // script content
                getSettings(
                    projectAnonUrl('script', 'format=json'),
                    function(data) {
                        var result = getResultSetResult(data);
                        var script = result[0] && 'script' in result[0] ? result[0].script : '';
                        script$.val(script);
                    },
                    'An error occurred getting anon script content.'
                );
            });

        });
    };

    projectSettings.setProjectAnonScript = function setProjectAnonScript(form, e){

        if (e && e.preventDefault) e.preventDefault();

        waitForElement(1, (form || '#project-anon-form'), function(form$){

            var activate = form$.find('#project-anon-enable').prop('checked');
            // set 'activate=true' so we can save the script content
            submitSettings({
                url: projectAnonUrl('status', 'activate=true'),
                success: function(){
                    submitSettings({
                        url:  projectAnonUrl('script', 'inbody=true'),
                        data: form$.find('#project-anon-script').val(),
                        contentType: 'text/plain',
                        processData: false,
                        success: function(){
                            if (activate) {
                                XNAT.ui.banner.top(2000, 'Project anon script saved and enabled.', 'success');
                            }
                            else {
                                // set 'activate=false' if script is to be disabled
                                submitSettings({
                                    url: (projectAnonUrl('status', 'activate=false')),
                                    success: 'Project anon script disabled. Script content saved.',
                                    error: 'An error occurred setting project anon script status.'
                                });
                            }
                        },
                        failure: function(e){
                            if (jsdebug) {
                                console.error('error:');
                                console.error(arguments);
                            }
                            XNAT.ui.dialog.message(false, ('An error occurred saving project anon script: ') + '<br><br>' + e);
                        }
                    });
                },
                error: 'An error occurred setting project anon script status.'
            });
        });
    };
    ////////////////////////////////////////////////////////////////////////////////





    ////////////////////////////////////////////////////////////////////////////////
    // Series Import Filters
    ////////////////////////////////////////////////////////////////////////////////

    function seriesImportUrl(params){
        params = params ? '?' + [].concat(params || []).join('&') : '';
        return '/data/projects/' + projectId + '/config/seriesImportFilter/config' + params;
    }

    projectSettings.getSeriesImportFilter = function getSeriesImportFilter(form){
        waitForElement(1, (form || '#series-import-form'), function(form$){
            var enabled$ = form$.find('#series-import-enable');
            var mode$ = form$.find('#filter-mode');
            var filter$ = form$.find('#series-import-filter');
            mode$.on('change', function(e){
                console.log('series import filter changed')
            });
            getSettings(
                seriesImportUrl(),
                function(data) {
                    var result = getResultSetResult(data);
                    var contents = result[0] && 'contents' in result[0] ? result[0].contents : '[]';
                    var settings = JSON.parse(contents);
                    enabled$.checked(settings.enabled);
                    mode$.changeVal(settings.mode||'!');
                    filter$.val(settings.list);
                },
                'An error occurred getting series import filter.'
            );
        });
    };

    // probably will not use this since the form submits JSON
    projectSettings.setSeriesImportFilter = function setSeriesImportFilter(form, e){

        if (e && e.preventDefault) e.preventDefault();

        waitForElement(1, (form || '#series-import-form'), function(form$){
            var enabled$ = form$.find('#series-import-enable');
            var enabled = enabled$[0].checked;
            var params = ['status=enabled', 'inbody=true'];
            // var mode$ = form$.find('#filter-mode');
            // var filter$ = form$.find('#series-import-filter');
            var seriesImportSubmitUrl = XNAT.url.csrfUrl(seriesImportUrl(params));
            var seriesImportData = form2js(form$[0]);
            XNAT.xhr.put({
                url: seriesImportSubmitUrl,
                data: JSON.stringify(seriesImportData),
                contentType: 'text/plain',
                processData: false,
                success: function(){
                    if (jsdebug) console.log('success: ' + seriesImportSubmitUrl);
                    if (enabled) {
                        XNAT.ui.banner.top(2000, 'Series import filter saved.', 'success');
                    }
                    else {
                        // do another request to disable filter, if 'enabled' is unchecked
                        submitSettings({
                            url: seriesImportUrl(['status=disabled']),
                            success: 'Series import filter disabled, content saved.',
                            error: 'An error occurred saving series import filter.'
                        })
                    }
                },
                failure: function(e){
                    if (jsdebug) {
                        console.error('error:');
                        console.error(arguments);
                    }
                    XNAT.ui.dialog.message(false, ('An error occurred saving series import filter: ') + '<br><br>' + e);
                }
            });
        });
    };
    ////////////////////////////////////////////////////////////////////////////////





    ////////////////////////////////////////////////////////////////////////////////
    // DICOM config - Separate PET/MR
    ////////////////////////////////////////////////////////////////////////////////

    function dicomConfigUrl(param){
        return '/data/projects/' + projectId + '/config/separatePETMR/config' + (param !== undef ? ('?' + param) : '' );
    }

    projectSettings.getDicomConfig = function getDicomConfig(form){
        waitForElement(1, (form || '#dicom-separate-petmr'), function(form$){
            var menu$ = form$.find('#dicom-petmr-menu');
            getSettings(
                dicomConfigUrl('contents=true'),
                // [<form>, 'inputName', 'fallbackValue']
                // [form$, 'separatePETMR', 'system'],
                function(data) {
                    var result = data+'' || 'system';
                    menu$.val(result).change();
                },
                function(){
                    menu$.val('system').change();
                }
            );
        });
    };

    projectSettings.setDicomConfig = function setDicomConfig(form, e){

        if (e && e.preventDefault) e.preventDefault();

        waitForElement(1, (form || '#dicom-separate-petmr'), function(form$){
            var menu$ = form$.find('#dicom-petmr-menu');
            var val = menu$.val();
            // do DELETE if set to 'system' (dumb)
            if (/system/i.test(val)) {
                XNAT.xhr['delete'](XNAT.url.csrfUrl(dicomConfigUrl()))
            }
            else {
                submitSettings({
                    url: (dicomConfigUrl('inbody=true')),
                    data: menu$.val(),
                    processData: false,
                    contentType: 'text/plain',
                    success: 'Project DICOM PET/MR handling preferences saved.',
                    error: 'An error occurred setting project DICOM PET/MR handling.'
                });
            }
        });
    };
    ////////////////////////////////////////////////////////////////////////////////





    ////////////////////////////////////////////////////////////////////////////////
    // PET Tracers
    ////////////////////////////////////////////////////////////////////////////////

    function petTracersUrl(param){
        return '/data/projects/' + projectId + '/config/tracers/tracers/' + (param !== undef ? ('?' + param) : '' );
    }

    projectSettings.getPetTracers = function getPetTracers(form){
        waitForElement(1, (form || '#pet-tracers-form'), function(form$){
            var enabled$ = form$.find('#enable-pet-tracers');
            var contents$ = form$.find('#pet-tracer-contents');
            getSettings(
                petTracersUrl('format=json'),
                function(data) {
                    var result = getResultSetResult(data);
                    var status = result[0] && 'status' in result[0] ? result[0].status : 'disabled';
                    var contents = result[0] && 'contents' in result[0] ? result[0].contents : '';
                    enabled$.checked(/enabled/i.test(status)).val(status);
                    contents$.val(contents);
                },
                'An error occurred getting PET tracers.'
            );
        });
    };

    projectSettings.setPetTracers = function setPetTracers(form, e){

        if (e && e.preventDefault) e.preventDefault();

        waitForElement(1, (form || '#pet-tracers-form'), function(form$){
            var enabled$ = form$.find('#enable-pet-tracers');
            var contents$ = form$.find('#pet-tracer-contents');
            // set 'enabled' status
            var isEnabled = enabled$[0].checked;
            submitSettings({
                url: petTracersUrl('status=enabled&inbody=true'),
                data: contents$.val(),
                contentType: 'text/plain',
                processData: false,
                success: function(){
                    if (isEnabled) {
                        XNAT.ui.banner.top(2000, 'PET tracers saved and enabled.', 'success');
                    }
                    else {
                        submitSettings({
                            url: petTracersUrl('status=disabled'),
                            success: 'PET tracers disabled, list saved.',
                            error: 'An error occurred setting PET tracers.'
                        })
                    }
                },
                error: 'An error occurred setting PET tracers.'
            });
        });
    };
    ////////////////////////////////////////////////////////////////////////////////





    ////////////////////////////////////////////////////////////////////////////////
    // Notification list
    ////////////////////////////////////////////////////////////////////////////////

    projectSettings.setNotificationsList = function setNotificationList(form, e){

        if (e && e.preventDefault) e.preventDefault();

        var URL = '/data/projects/' + projectId + '/resources/notifications/files/archival.lst';
        var form$ = (form !== undef) ? $$(form) : $$('#notifications-config');
        var list$ = form$.find('#notification-email-list');
        var val = (list$.val()+'').trim();
        // do DELETE if empty (dumb)
        if (val === '') {
            submitSettings({
                method: 'DELETE',
                url: XNAT.url.csrfUrl(URL),
                success: 'Notifications list removed.',
                error: 'An error occurred removing the notifications list.'
            });
        }
        else {
            submitSettings({
                url: XNAT.url.csrfUrl(URL, [
                    'inbody=true',
                    'overwrite=true',
                    'content=NOTIFY_ARCHIVAL'
                ]),
                data: val,
                processData: false,
                contentType: 'text/plain',
                success: 'Notifications list saved.',
                error: 'An error occurred saving the notifications list.'
            });
        }
    };
    ////////////////////////////////////////////////////////////////////////////////





    projectSettings.init = function(){

        if (jsdebug) {
            console.log('======================');
            console.log('projectSettings.init()');
            console.log('======================');
        }

        getProjectId();


        // XNAT.spawner.resolve('xnat:projectSettings/tabs').ok(function(){
        //     this.render('#project-settings-tabs-container')
        // });

        // var spawnerResolve = XNAT.spawner.resolve('xnat:projectSettings/tabs');
        //
        // spawnerResolve.render('#project-settings-tabs-container', function(){
        //     console.log('hi there')
        //     //XNAT.tab.activate(XNAT.tab.active, XNAT.tabs.container);
        // });

        // projectSettings.getQuarantineCode();
        // projectSettings.getPrearhchiveCode();
        // projectSettings.getProjectAnonScript();
        // projectSettings.getSeriesImportFilter();
        // projectSettings.getDicomConfig();
        // projectSettings.getPetTracers();
        // // etc...
        //
        // var scanTypeMappingRadios = $$('?scan_type_mapping');
        // // set initial value of the scan type mapping radio buttons
        // scanTypeMappingRadios.filter('[value="' + XNAT.data.project.scanTypeMapping + '"]').checked(true);
        // // set initial value of the scan type mapping radio buttons
        // $(document).off('click.scan-type-mapping').on('click.scan-type-mapping', '[name="scan_type_mapping"]', function(){
        //     XNAT.data.project.scanTypeMapping = this.value;
        // });

    };


    // INITIALIZE!!!!!
    // projectSettings.init();

    // this script has loaded
    projectSettings.loaded = true;

    return XNAT.app.projectSettings = XNAT.projectSettings = projectSettings;

}));