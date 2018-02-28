<%@ page contentType="application/javascript" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
    <%@ taglib prefix="pg" tagdir="/WEB-INF/tags/page" %>

    <%-- What trickery is this? A JSP file served as a JavaScript file??? --%>

    <pg:init/>

    var XNAT = getObject(XNAT);

    (function(){

        var undef, dataTypeAccess;
        var displayItems = [];
        var displayName = 'browseable';
        var SITE_ROOT = '${sessionScope.siteRoot}' || '';
        var USERNAME = window.username || '${sessionScope.username}' || 'user';

        XNAT.app =
            getObject(XNAT.app || {});

        XNAT.app.dataTypeAccess = dataTypeAccess =
            getObject(XNAT.app.dataTypeAccess || {});

        // safety function for JSP data
        function returnValue(VALUE){
            return VALUE;
        }

        dataTypeAccess.isReady = false;
        dataTypeAccess.url = XNAT.url.rootUrl('/xapi/access/displays');

        function dataTypeAccessUrl(part){
            return dataTypeAccess.url + (part ? ('/' + part) : '')
        }

        dataTypeAccess.setUrl = dataTypeAccessUrl;

        // get the modified timestamp synchronously (fast)
        <c:import var="accessDisplaysModified" url="/xapi/access/displays/modified"/>
        dataTypeAccess.modified = returnValue(${accessDisplaysModified});

        var ACCESS_DISPLAYS_MODIFIED = 'ACCESS_DISPLAYS_MODIFIED.' + USERNAME;
        dataTypeAccess.modifiedCookie = ACCESS_DISPLAYS_MODIFIED;

        dataTypeAccess.needsUpdate = false;
        // if there's a cookie, get the value, compare, and set a flag to update later
        if (XNAT.cookie.exists(ACCESS_DISPLAYS_MODIFIED)) {
            // set a simple boolean to indicate an update needs to be made when setting up data type access
            dataTypeAccess.needsUpdate = (XNAT.cookie.get(ACCESS_DISPLAYS_MODIFIED) != dataTypeAccess.modified);
        }
        dataTypeAccess.needsUpdate = /true|all/i.test(getQueryStringValue('updateAccess')) || dataTypeAccess.needsUpdate;
        // always set the cookie (after checking above)
        XNAT.cookie.set(ACCESS_DISPLAYS_MODIFIED, dataTypeAccess.modified);

        // get the list of display types synchronously (fast)
        <%--<c:import var="dataTypeAccessDisplays" url="/xapi/access/displays"/>--%>
        <%--dataTypeAccess.displays = returnValue(${dataTypeAccessDisplays}) || [];--%>
        dataTypeAccess.displays = [
            'browseable',
            'browseableCreateable',
            'createable',
            'searchable',
            'searchableByDesc',
            'searchableByPluralDesc'
        ];

        // save the display type list as a cookie as well
        XNAT.cookie.set('ACCESS_DISPLAYS.' + USERNAME, dataTypeAccess.displays.join('|'));

        dataTypeAccess.display = getObject(dataTypeAccess.display || {});
        <%--dataTypeAccess.display['@modified'] = dataTypeAccess.modified;--%>

        // to ensure we're storing items for the correct user
        var dataStoreName = USERNAME + '@' + SITE_ROOT.replace(/^\//, '');

        var DATA_STORE_KEY = 'accessDisplays';
        var MODIFIED_KEY = 'accessDisplaysModified';
        var dataStore;

        // initialize localForage for storing data type objects
        dataTypeAccess.dataStore = dataStore = localforage.createInstance({
            name: dataStoreName,
            storeName: dataStoreName
        });

        dataTypeAccess.getValue = function(key, callback){
            return dataStore.getItem(key, function(e, val){
                (callback || diddly).call(this, val, e);
            });
        };

        dataTypeAccess.setValue = function(key, val, callback){
            dataStore.setItem(key, val, function(e, val){
                (callback || diddly).call(this, val, e);
            });
            return dataStore;
        };

        // get all the things!
        dataTypeAccess.getValues = function(callback){
            return dataStore.getItem(DATA_STORE_KEY, function(e, obj){
                dataTypeAccess.display = extend({}, dataTypeAccess.display, obj);
                (callback || diddly).call(this, obj, e);
            });
        }

        // SET all the things!
        dataTypeAccess.setValues = function(data, callback){
            var DATA = data || dataTypeAccess.display || {};
            return dataStore.setItem(DATA_STORE_KEY, DATA, function(e, obj){
                (callback || diddly).call(this, obj, e);
            });
        }

        // update element names and objects for an access type
        function updateDisplayItems(display, items){
            // Do we need to sort the entire array of objects or just the names?
            var displayItems = sortObjects([].concat(items), 'elementName');
            var displayName = display || 'elements';
            var elementsObj = {
                element: {},
                elements: [],
                dataTypes: []
            };
            forEach(displayItems, function(item, i){
                elementsObj.element[item.elementName] = item;
                elementsObj.elements.push(item);
                elementsObj.dataTypes.push(item.elementName);
            });
            dataTypeAccess.display[displayName] = extend({}, dataTypeAccess.display[displayName], elementsObj);
            dataTypeAccess.display[displayName].accessType = displayName;
            return dataTypeAccess.display[displayName];
        }

        //
        dataTypeAccess.accessDisplays = (XNAT.cookie.get('ACCESS_DISPLAYS.'+USERNAME)||'').split('|');

        <%--// track previously called requests to get elements--%>
        <%-- (DO NOT) --%>
        <%--dataTypeAccess.called = {};--%>

        <%--// object for individual access type methods--%>
        <%--dataTypeAccess.getElements = {};--%>

        <%--// store update status for each access type--%>
        <%-- actually, don't --%>
        <%--dataTypeAccess.updateAccess = {};--%>

        // call the methods for each access type like this:
        //
        // dataTypeAccess.getElements('browseable').ready( callbackFn )
        // dataTypeAccess.getElements('browseableCreateable').ready( callbackFn )
        // dataTypeAccess.getElements('createable').ready( callbackFn )
        // dataTypeAccess.getElements('searchable').ready( callbackFn )
        // dataTypeAccess.getElements('searchableByDesc').ready( callbackFn )
        // dataTypeAccess.getElements('searchableByPluralDesc').ready( callbackFn )
        //
        dataTypeAccess.getElements = function getElements(accessType, force) {

            var __isReady = false;

            // brute-force timer for data readiness
            function whenReady(callback){
                waitForIt(10, function(){
                    return __isReady;
                }, function(){
                    try {
                        callback()
                    }
                    catch (e) {
                        debugError(e);
                    }
                })
            }

            dataTypeAccess.display[accessType] = getObject(dataTypeAccess.display[accessType] || {});

            // only call ONCE for each access type
            if (dataTypeAccess.getElements[accessType]) {
                console.log('aready got ' + accessType);
                return dataTypeAccess.getElements[accessType];
            }

            dataTypeAccess.getValues(function(obj){
                if (force || dataTypeAccess.needsUpdate || !(obj || {}).hasOwnProperty(accessType)) {
                    XNAT.xhr.get({
                        url: SITE_ROOT + '/xapi/access/displays/' + accessType,
                        dataType: 'json',
                        success: function(accessTypeItems){
                            console.log(SITE_ROOT + '/xapi/access/displays/' + accessType)
                            console.log(arguments);
                            updateDisplayItems(accessType, accessTypeItems)
                            // save datatype objects in browser storage
                            dataTypeAccess.setValues(dataTypeAccess.display, function(data){
                                debugLog(data);
                                __isReady = true;
                            });
                        }
                    })
                }
                else {
                    dataTypeAccess.setValues(dataTypeAccess.display, function(data){
                        debugLog(data);
                        __isReady = true;
                    });
                }
            });

            <%--whenReady(function(){--%>
                <%--dataTypeAccess.display[accessType].ready = true;--%>
            <%--});--%>

            // set the returned 'ready' function - return existing function if present, otherwise create it
            var readyFn = dataTypeAccess.getElements[accessType] && isFunction(dataTypeAccess.getElements[accessType].ready) ?
                dataTypeAccess.getElements[accessType].ready :
                function(callback){
                    // make sure access type is available before executing callback
                    if (dataTypeAccess.accessDisplays.indexOf(accessType) > -1) {
                        whenReady(function(){
                            (callback || diddly).call(this, dataTypeAccess.display[accessType]);
                        });
                    }
                }

            return dataTypeAccess.getElements[accessType] = {
                ready: readyFn
            }
        }

        var readyItems = 0;
        var updateAccessType = getQueryStringValue('updateAccess');

        if (updateAccessType) {
            if (/true|all/i.test(updateAccessType)) {
                dataTypeAccess.dataStore.removeItem(DATA_STORE_KEY, function(){
                    var startTime = Date.now();
                    var maxTime = startTime + (1000 * 60 * 3);
                    waitForIt(10,
                        function(){
                            if (readyItems >= dataTypeAccess.accessDisplays.length) {
                                return true;
                            }
                            // just add 10ms each time rather than
                            // calling Date.now() over and over
                            startTime += 10;
                            // don't wait more than the max time.
                            if (startTime > maxTime) {
                                return true;
                            }
                        },
                        function(){
                            // what to do???
                            readyItems = 0;
                            dataTypeAccess.isReady = true;
                            console.log('dataTypeAccess.isReady');
                        });

                    // update each access type
                    forEach(dataTypeAccess.accessDisplays, function(accessDisplay){
                        dataTypeAccess.getElements[accessDisplay] = dataTypeAccess.getElements(accessDisplay, true).ready(function(){
                            readyItems += 1;
                        });
                    });
                });
            }
            else if (dataTypeAccess.accessDisplays.indexOf(updateAccessType) > -1) {
                // reset the access type we're updating
                dataTypeAccess.display[updateAccessType] = {};
                dataTypeAccess.getElements[updateAccessType] = dataTypeAccess.getElements(updateAccessType, true).ready(function(){
                    dataTypeAccess.isReady = true;
                    console.log('dataTypeAccess.isReady');
                })
            }
        }
        else {
            forEach(dataTypeAccess.accessDisplays, function(display){
                // call .getElements() method for each access type
                // then later call the .ready() method
                dataTypeAccess.getElements(display);
            });
        }

        // always store the 'modified' value in browser storage
        dataTypeAccess.setValue(MODIFIED_KEY, dataTypeAccess.modified, function(modified){
            console.log('modified:');
            console.log(modified);
            dataTypeAccess.setValues(dataTypeAccess.display, function(){
                // dataTypeAccess.getElements[display]
                // get the currently stored values first
                dataTypeAccess.getValues(function(data){
                    console.log('stored dataTypeAccess values:');
                    console.log(data);
                    <%--console.log(DATA_STORE_KEY);--%>
                    <%--console.log(data);--%>
                    // then update them
                    <%--dataTypeAccess.display = extend({}, data, dataTypeAccess.display);--%>
                    <%--// then save them back via localForage--%>
                    <%--dataTypeAccess.setValue(DATA_STORE_KEY, dataTypeAccess.display, function(val){--%>
                    <%--console.log(val);--%>
                    <%--dataTypeAccess.getValue(DATA_STORE_KEY, function(dataTypeAccessDisplay){--%>
                    <%--console.log(DATA_STORE_KEY);--%>
                    <%--console.log(dataTypeAccessDisplay);--%>
                    <%--console.log('createable');--%>
                    <%--console.log(dataTypeAccessDisplay.createable);--%>
                    <%--dataTypeAccess.isReady = true;--%>
                    <%--})--%>
                    <%--});--%>
                    dataTypeAccess.isReady = true;
                });
            });
        });

        // MAIN CALLBACK FUNCTION
        dataTypeAccess.ready = function(callback){
            console.log('dataTypeAccess.ready');
        };

    })();
