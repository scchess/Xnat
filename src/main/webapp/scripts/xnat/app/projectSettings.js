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

    var UNDEF, projectSettings;

    if (window.jsdebug) console.log('projectSettings.js');

    XNAT.app = getObject(XNAT.app || {});

    XNAT.app.projectSettings = projectSettings =
        getObject(XNAT.app.projectSettings || XNAT.projectSettings || {});


    // handle loading and submission of project anon script
    projectSettings.anonScript = function(){

        var anonForm = '#project-anon-form';

        $(document).on('submit', anonForm, function(e){
            e.preventDefault();
            console.log($(this).getValues())
        })

    };


    projectSettings.init = function(){

    };


    // this script has loaded
    projectSettings.loaded = true;

    return XNAT.app.projectSettings = XNAT.projectSettings = projectSettings;

}));
