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

    if (window.jsdebug) console.log('projectSettings.js');

    XNAT.app = getObject(XNAT.app || {});

    XNAT.app.projectSettings = projectSettings =
        getObject(XNAT.app.projectSettings || XNAT.projectSettings || {});

    XNAT.data = getObject(XNAT.data);
    XNAT.data.context = getObject(XNAT.data.context);

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

    function getSettings(url, successOrElements, errorMsg){
        return XNAT.xhr.get({
            url: XNAT.url.restUrl(url),
            success: function(data){
                if (jsdebug) console.log('success: ' + url);
                if (Array.isArray(successOrElements)) {
                    successOrElements.push(data); // add returned value to argument array
                    setInputValue.apply(this, successOrElements);
                }
                else {
                    successOrElements.apply(this, arguments);
                }
            },
            failure: function(e){
                if (jsdebug) {
                    console.error('error:');
                    console.error(arguments);
                }
                XNAT.ui.dialog.message(false, (errorMsg||'An error occurred: ') +  '<br><br>' + e);
            }
        });
    }

    function submitSettings(url, success, errorMsg, method){
        return XNAT.xhr[method || 'put']({
            url: XNAT.url.csrfUrl(url),
            success: function(data){
                if (jsdebug) console.log('success: ' + url);
                if (success) {
                    success.apply(this, arguments);
                }
                else {
                    XNAT.ui.banner.top(2000, 'Settings saved.', 'success');
                }
            },
            failure: function(e){
                if (jsdebug) {
                    console.error('error:');
                    console.error(arguments);
                }
                XNAT.ui.dialog.message(false, (errorMsg||'An error occurred: ') +  '<br><br>' + e);
            }
        })
    }





    ////////////////////////////////////////////////////////////////////////////////
    // Project Quarantine Settings
    ////////////////////////////////////////////////////////////////////////////////

    function quarantineUrl(value){
        return '/data/projects/' + projectId + '/quarantine_code' + (value !== undef ? ('/' + value) : '');
    }

    projectSettings.getQuarantineCode = function(form){
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

    projectSettings.setQuarantineCode = function(form, e){
        if (e && e.preventDefault) e.preventDefault();
        var form$ = (form !== undef) ? $$(form) : $$('#quarantine-settings');
        var code = getInputByName(form$, 'quarantine').filter(':checked').val();
        submitSettings(
            quarantineUrl(code),
            false,
            // function() {
            //     XNAT.ui.banner.top(2000, 'Quarantine set to ' + (/1/.test(code) ? 'Yes' : 'No'), 'success')
            // },
            'An error occurred setting quarantine preferences.'
        );
    };
    ////////////////////////////////////////////////////////////////////////////////





    ////////////////////////////////////////////////////////////////////////////////
    // Project Prearchive Settings
    ////////////////////////////////////////////////////////////////////////////////

    function prearchiveUrl(value){
        return '/data/projects/' + projectId + '/prearchive_code' + (value !== undef ? ('/' + value) : '');
    }

    projectSettings.getPrearhchiveCode = function(form){
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

    projectSettings.setPrearchiveCode = function(form, e){
        if (e && e.preventDefault) e.preventDefault();
        var form$ = (form !== undef) ? $$(form) : $$('#prearchive-settings');
        var code = getInputByName(form$, 'prearchive').filter(':checked').val();
        submitSettings(
            prearchiveUrl(code),
            false,
            // function() {
            //     XNAT.ui.banner.top(2000, 'Quarantine set to ' + (/1/.test(code) ? 'Yes' : 'No'), 'success')
            // },
            'An error occurred setting prearchive preferences.'
        );
    };
    ////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////
    // Project Anon Script
    ////////////////////////////////////////////////////////////////////////////////

    function projectAnonUrl(item, param){
        return '/data/config/edit/projects/' + projectId + '/image/dicom/' + item + (param !== undef ? ('?' + param) : '' );
    }

    projectSettings.getProjectAnonScript = function(form){
        var form$ = (form !== undef) ? $$(form) : $$('#project-anon-form');
        var enabled$ = form$.find('#project-anon-enable');
        var script$ = form$.find('#project-anon-script');
        // 'enabled' checkbox
        getSettings(
            projectAnonUrl('status', 'format=json'),
            function(data) {
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

    };

    projectSettings.setProjectAnonScript = function(form, e){
        if (e && e.preventDefault) e.preventDefault();
        var form$ = (form !== undef) ? $$(form) : $$('#project-anon-form');
        var activate = form$.find('#project-anon-enable').prop('checked');
        // set 'enabled' status
        submitSettings(
            projectAnonUrl('status', 'activate=' + activate),
            function(){
                // submit script content if 'active/enabled'
                if (activate) {
                    var scriptContentUrl = XNAT.url.csrfUrl(projectAnonUrl('script', 'inbody=true&activate=' + activate));
                    XNAT.xhr.put({
                        url: scriptContentUrl,
                        data: form$.find('#project-anon-script').val(),
                        contentType: 'text/plain',
                        processData: false,
                        success: function(){
                            if (jsdebug) console.log('success: ' + scriptContentUrl);
                            // if (activate) {
                                XNAT.ui.banner.top(2000, 'Project anon script enabled and saved.', 'success');
                            // }
                            // else {
                            //     XNAT.ui.banner.top(2000, 'Project anon script was saved and set as "disabled".', 'success');
                            // }
                        },
                        failure: function(e){
                            if (jsdebug) {
                                console.error('error:');
                                console.error(arguments);
                            }
                            XNAT.ui.dialog.message(false, ('An error occurred saving project anon script: ') +  '<br><br>' + e);
                        }
                    })
                }
                else {
                    XNAT.ui.banner.top(2000, 'Project anon script disabled (and not saved).', 'success');
                }
            },
            'An error occurred setting project anon script.'
        );
    };
    ////////////////////////////////////////////////////////////////////////////////



    // Series Import Filters
    ////////////////////////////////////////////////////////////////////////////////

    function seriesImportUrl(params){
        params = params ? '?' + [].concat(params || []).join('&') : '';
        return '/data/projects/' + projectId + '/config/seriesImportFilter/config' + params;
    }

    projectSettings.getSeriesImportFilter = function(form){
        var form$ = (form !== undef) ? $$(form) : $$('#series-import-form');
        var enabled$ = form$.find('#series-import-enable');
        var mode$ = form$.find('#filter-mode');
        var filter$ = form$.find('#series-import-filter');
        getSettings(
            seriesImportUrl(),
            function(data) {
                var result = getResultSetResult(data);
                var contents = result[0] && 'contents' in result[0] ? result[0].contents : '[]';
                var settings = JSON.parse(contents);
                enabled$.checked(settings.enabled);
                mode$.val(settings.mode).change();
                filter$.val(settings.list);
            },
            'An error occurred getting series import filter.'
        );
    };

    // probably will not use this since the form submits JSON
    projectSettings.setSeriesImportFilter = function(form, e){
        if (e && e.preventDefault) e.preventDefault();
        var form$ = (form !== undef) ? $$(form) : $$('#series-import-form');
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
                    XNAT.ui.banner.top(2000, 'Series import filter disabled.', 'success');
                }
            },
            failure: function(e){
                if (jsdebug) {
                    console.error('error:');
                    console.error(arguments);
                }
                XNAT.ui.dialog.message(false, ('An error occurred saving series import filter: ') + '<br><br>' + e);
            }
        })
    };
    ////////////////////////////////////////////////////////////////////////////////



    projectSettings.init = function(){
        console.log('projectSettings.init');
        getProjectId();
        projectSettings.getQuarantineCode();
        projectSettings.getPrearhchiveCode();
        projectSettings.getProjectAnonScript();
        projectSettings.getSeriesImportFilter();
        // etc...
    };


    // this script has loaded
    projectSettings.loaded = true;

    return XNAT.app.projectSettings = XNAT.projectSettings = projectSettings;

}));
