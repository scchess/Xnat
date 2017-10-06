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
        // 'enabled' checkbox
        getSettings(
            projectAnonUrl('status', 'format=json'),
            function(data) {
                var status = data && data.ResultSet.Result.length ? data.ResultSet.Result[0].edit : false;
                form$.find('#project-anon-enable').checked(realValue(status));
            },
            'An error occurred getting anon script status.'
        );
        // script content
        getSettings(
            projectAnonUrl('script', 'format=json'),
            function(data) {
                var script = data && data.ResultSet.Result.length ? data.ResultSet.Result[0].script : '';
                form$.find('#project-anon-script').val(script);
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
                    var scriptContentUrl = XNAT.url.csrfUrl(projectAnonUrl('script', 'inbody=true'));
                    XNAT.xhr.request({
                        method: 'PUT',
                        url: scriptContentUrl,
                        data: form$.find('#project-anon-script').val(),
                        contentType: 'text/plain',
                        processData: false,
                        success: function(){
                            if (jsdebug) console.log('success: ' + scriptContentUrl);
                            XNAT.ui.banner.top(2000, 'Project anon script enabled and saved.', 'success');
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
                    XNAT.ui.banner.top(2000, 'Project anon script disabled.', 'success');
                }
            },
            'An error occurred setting project anon script.'
        );
    };
    ////////////////////////////////////////////////////////////////////////////////


    projectSettings.init = function(){
        console.log('projectSettings.init');
        getProjectId();
        projectSettings.getQuarantineCode();
        projectSettings.getPrearhchiveCode();
        projectSettings.getProjectAnonScript();
        // etc...
    };


    // this script has loaded
    projectSettings.loaded = true;

    return XNAT.app.projectSettings = XNAT.projectSettings = projectSettings;

}));
