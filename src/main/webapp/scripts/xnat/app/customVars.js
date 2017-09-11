/*
 * web: customVars.js
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
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
    XNAT.customVars = getObject(XNAT.customVars || {});

    XNAT.customVars.sitewideVars = getObject(XNAT.customVars.sitewideVars || []);
    XNAT.customVars.projectVars = getObject(XNAT.customVars.projectVars || []);

    function getCvUrl(projectId,xsiType){
        if (projectId && xsiType) {

        } else {
            return XNAT.url.csrfUrl('/servlet/AjaxServlet?remote-class=org.nrg.xnat.ajax.RequestProtocolDefinitionGroups&remote-method=execute')
        }
    }

    XNAT.customVars.init = function(){
        // Initialize the custom variable page for a project:
        // Query for all known site-wide custom variable sets in XNAT,
        // then compare that with defined custom variable sets in this project.

    };

    XNAT.customVars.addTableRow = function(table,definition){
        // accepts an XNAT table object and a custom variable set definition and returns a table row
        var fields = (definition.fields) ? definition.fields.field : [];
        var fieldNames = [];
        if (fields.length) {
            fields.forEach(function(f){
                fieldNames.push(f['_name']);
            })
        } else {
            fieldNames.push('n/a');
        }
        table.tr({ className: 'cvar-'+definition['_ID'] })
            .td({ className: 'left' }, definition['_ID'] )
            .td(fieldNames.toString() )
            .td()
            .td();

        return table;
    }

}));