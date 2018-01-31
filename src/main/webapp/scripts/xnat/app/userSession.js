/*
 * web: userSession.js
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

/*!
 * Session timeout warning dialog
 */

var XNAT = getObject(XNAT);
var PAGE = getObject(PAGE);

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

    var userSession, siteRoot, undef;
    var $timeLeftDisplay = $('#timeLeft');



    var UN = window.username || PAGE.username;
    var SITE = XNAT.url.siteUrl;
    var LOC = window.location.href;

    // special check for ?login=true to prevent redirect to same page
    if (/true/i.test(XNAT.url.splitUrl(LOC).params.login)) {
        LOC = XNAT.url.splitUrl(LOC).base;
    }

    function jmbl(str){
        return str.split('').reverse().join('');
    }

    function encVal(val){
        return XNAT.sub64.dlxEnc(val).enc;
    }

    function decVal(val){
        return XNAT.sub64.dlxDec(val).dec;
    }

    var VALS = {
        UN: UN,
        SITE: SITE,
        LOC: LOC
    };

    var VALSenc = {};
    forOwn(VALS, function(key, val){
        VALSenc[key] = encVal(val);
    });

    var UNe = VALSenc.UN;
    var SITEe = VALSenc.SITE;
    var LOCe = VALSenc.LOC;

    var USERKEY = (toUnderscore(UNe.toLowerCase()) + '__' + toUnderscore(SITEe.toLowerCase())).toUpperCase();
    window.USERKEY = USERKEY;
    window.userKey = UNe + '__' + SITEe;

    var KEYS = {
        currentPage: 'currentPage',
        lastPage: 'lastPage',
        userLastPage: jmbl(UN) + '.lastPage',
        status: 'status',
        endTime: 'endTime',
        continue: 'continue'
    };

    var KEYSenc = {};
    forOwn(KEYS, function(key, name){
        KEYSenc[key] = encVal(name);
    });

    var resetCookies = getQueryStringValue('cookies') === 'reset';

    XNAT.app = getObject(XNAT.app || {});

    XNAT.app.userSession = userSession =
        getObject(XNAT.app.userSession || {});

    XNAT.app.siteRoot = siteRoot =
        XNAT.app.siteRoot || XNAT.url.rootUrl();

    function dateString(ms, strip){
        var str = (new Date(ms)).toString();
        if (strip !== false) {
            str = str.replace(/\s+/g, '-');
        }
        return str;
    }

    // local variable for working with the dataStore
    var dataStore;

    // initialize localForage instance for current user
    userSession.dataStore = dataStore = localforage.createInstance({
        name: USERKEY,
        storeName: USERKEY
    });

    userSession.getValue = function(key, callback){
        return dataStore.getItem(key, function(e, val){
            (callback || diddly).call(this, val, e);
        });
    };

    userSession.setValue = function(key, val, callback){
        dataStore.setItem(key, val, function(e, val){
            (callback || diddly).call(this, val, e);
        });
        return dataStore;
    };

    userSession.setValues = function(data, callback){

    };

    userSession.setValueToo = function(key, val, callback){
        var jmbly = jmbl(JSON.stringify(val));
        return dataStore.setItem(key, jmbly, function(e, val){
            (callback || diddly).call(this, val, e);
        });
    };

    userSession.getValueToo = function(key, callback){
        return dataStore.getItem(key, function(e, val){
            var njmbly = JSON.parse(jmbl(val || '""'));
            (callback || diddly).call(this, e, njmbly);
        })
    };


    // only try to redirect GET requests for logged in users
    if (!window.isGuest && window.loggedIn && (/^(\/app\/action\/)/i.test(XNAT.url.getPath())) && /GET/i.test(window.requestMethod)) {
        userSession.setValue(KEYS.currentPage, LOCe, function(currentPage){
            userSession.getValue(KEYS.status, function(status){
                if (/logout|timeout/.test(status)) {
                    userSession.setValue(KEYS.status, 'active');
                    userSession.getValueToo(KEYSenc.userLastPage, function(e, lastPage){
                        var lastPageDec = decVal(lastPage);
                        var lastPageParts = lastPageDec.split('/');
                        if (lastPage !== currentPage) {

                            userSession.getValue(KEYS.continue, function(val){

                                // save the value from the 'continue_session' cookie then remove it
                                var CONTINUE = XNAT.cookie.get('continue_session') || val || false;

                                var data = {};

                                data[KEYS.continue] = 'foo';

                                userSession.setValue(KEYS.continue, CONTINUE, function(){

                                    XNAT.cookie.remove('continue_session');

                                    if (/true/i.test(CONTINUE)) {
                                        XNAT.dialog.loadingBar.open();
                                        setTimeout(function(){
                                            window.location.replace(lastPageDec);
                                        }, 0);
                                    }

                                    if (undef) {
                                        XNAT.dialog.open({
                                            width: 500,
                                            title: 'Resume browser session?',
                                            content: spawn('div.resume-session', [
                                                '<p>Continue to last visited page?</p>',
                                                ['a.last-page-url', { href: lastPageDec }, '<b></b>']
                                            ]),
                                            afterShow: function(){
                                                var innerWidth = this.body$.find('> .inner').width();
                                                var lastPageUrl$ = this.body$.find('a.last-page-url > b');
                                                var urlString = lastPageParts.shift();
                                                lastPageParts.forEach(function(part){
                                                    var urlTemp = urlString + '/' + part;
                                                    // add the new part
                                                    lastPageUrl$.html(urlTemp);
                                                    // if it's wider than 400px AFTER appending...
                                                    // ...rewrite the HTML with a <br> before the part
                                                    var elWidth = lastPageUrl$.width();
                                                    if (elWidth > innerWidth) {
                                                        urlTemp = urlString + '<br> /' + part;
                                                        lastPageUrl$.html(urlTemp);
                                                    }
                                                    urlString = urlTemp;
                                                });
                                            },
                                            okLabel: 'Continue',
                                            okAction: function(){
                                                window.location.replace(lastPageDec)
                                            },
                                            cancelAction: function(){
                                                userSession.setValueToo(KEYSenc.userLastPage, LOCe);
                                            }
                                        })
                                    }

                                });

                            });
                        }
                        else {
                            // store this page as last visited
                            userSession.setValueToo(KEYSenc.userLastPage, LOCe);
                        }
                    });
                }
                userSession.setValue(KEYS.status, 'active');
            });
        });
    }

    // timeout polling interval
    userSession.timerInterval = userSession.timerInterval || 1000;

    // when do we show the dialog?
    // (how many seconds until the session ends)
    userSession.showTime = userSession.showTime || 60;

    // how long will the screen be grayed out
    // before redirecting?
    userSession.delayTime = userSession.delayTime || 20;

    userSession.cancelled = false;

    function timeoutCookie(name){

        var undefined;

        function CookieFn(){
            this.cookieExists = false;
            this.name = name !== undefined ? name : this.name;
            this.value = '';
            this.opts = {
                path: siteRoot,
                domain: XNAT.url.getDomain()
            };
        }

        var fn = CookieFn.prototype;

        fn.exists = function(name){
            this.name = name !== undefined ? name : this.name;
            this.cookieExists = XNAT.cookie.get(this.name) !== undefined;
            return this.cookieExists;
        };
        // go ahead and check if this cookie exists
        fn.exists();

        // reset values after each call
        fn.reset = function(){
            //this.cookieExists = false;
            this.name = name !== undefined ? name : this.name;
            this.value = '';
            this.opts = {
                path: siteRoot,
                domain: XNAT.url.getDomain()
            };
            return this;
        };

        fn.set = function(name, value, opts){
            if (value === undefined) {
                value = name;
                name = undefined;
            }
            this.name = name !== undefined ? name : this.name;
            this.value = value !== undefined ? value : this.value;
            this.opts = opts !== undefined ? opts : this.opts;
            XNAT.cookie.set(this.name, this.value, this.opts);
            //this.reset(); // reset to prevent passing of values to chained methods
            return this;
        };

        // 'value' is a default value to set if the cookie doesn't exist
        fn.get = function(value){
            value = value !== undefined ? value : '';
            this.value = this.exists() ? XNAT.cookie.get(this.name) : value;
            return this;
        };

        fn.getJSON = function(name){
            this.name = name !== undefined ? name : this.name;
            return getObject(Cookies.getJSON(this.name));
        };

        fn.getValue = function(prop){
            return XNAT.cookie.getValue(this.name, prop);
        };

        fn.is = function(value){
            return (this.get().value || '').toString() === value.toString();
        };

        fn.remove = function(){
            XNAT.cookie.remove(this.name);
            return !this.exists();
        };

        fn.cookie = function(name){
            this.name = name;
            this.exists();
            return this;
        };

        fn.update = function(prop, val){
            prop = prop || '*';
            val = firstDefined(val, '/');
            XNAT.cookie.setValue(this.name, prop, val, this.opts);
            return this;
        };

        return new CookieFn();

    }

    var cookie = {};

    // store cookie value from server as a js var
    cookie.SESSION_EXPIRATION_TIME = timeoutCookie('SESSION_EXPIRATION_TIME').get();

    // is the dialog displayed?
    cookie.SESSION_DIALOG_OPEN = timeoutCookie('SESSION_DIALOG_OPEN').set('false');

    // has it been cancelled?
    cookie.SESSION_DIALOG_CANCELLED = timeoutCookie('SESSION_DIALOG_CANCELLED').set('false');

    // is the session still active? (could have been logged out in another window)
    cookie.SESSION_ACTIVE = timeoutCookie('SESSION_ACTIVE').set(window.loggedIn);

    // has the session timed out?
    cookie.SESSION_TIMED_OUT = timeoutCookie('SESSION_TIMED_OUT').get();

    // the time, in ms, that the session will end or has ended
    cookie.SESSION_TIMEOUT_TIME = timeoutCookie('SESSION_TIMEOUT_TIME');

    // the date and time, as a string, that the session will end or has ended
    // cookie.SESSION_TIMEOUT_STRING = timeoutCookie('SESSION_TIMEOUT_STRING');

    // has the user been redirected after timout?
    cookie.SESSION_REDIRECT = timeoutCookie('SESSION_REDIRECT').update();

    // the time, in ms, that the session started
    cookie.SESSION_LAST_LOGIN = timeoutCookie('SESSION_LAST_LOGIN');

    // the last logged-in user - used for redirect check
    // cookie.SESSION_LAST_USER = timeoutCookie('SESSION_LAST_USER').get();

    // what was the last page visited?
    // cookie.SESSION_LAST_PAGE = timeoutCookie('SESSION_LAST_PAGE').get();

    // remove potentially problematic cookies
    XNAT.cookie.remove('SESSION_LAST_USER', { path: '/' });
    XNAT.cookie.remove('SESSION_LAST_USER');
    XNAT.cookie.remove('SESSION_LAST_PAGE', { path: '/' });
    XNAT.cookie.remove('SESSION_LAST_PAGE');

    userSession.expCookie = '';

    userSession.startTime = Date.now();

    // parse the timeout values
    userSession.getValues = (function(){
        // define the function
        function getTimeoutValues(){
            var expCookie = cookie.SESSION_EXPIRATION_TIME.get().value;
            if (userSession.expCookie && userSession.expCookie === expCookie) return;
            userSession.expCookie = expCookie; // save it for next time
            expCookie = (expCookie || '').replace(/"/g, '').split(',');
            return {
                startTime: (userSession.startTime = +((expCookie[0] || '').trim())),
                duration: (userSession.duration = +((expCookie[1] || '').trim())),
                endTime: (userSession.endTime = userSession.startTime + userSession.duration)
            }
        }
        // call the function to initialize the needed values
        getTimeoutValues();
        // return for later use with userSession.getValues()
        return getTimeoutValues;
    })();


    // set SESSION_TIMEOUT_* cookies on load
    cookie.SESSION_TIMEOUT_TIME.set(userSession.endTime);
    // cookie.SESSION_TIMEOUT_STRING.set(dateString(userSession.endTime));


    function parseTimestamp(time){
        time = time || userSession.endTime;
        var timeLeft = time - Date.now();
        var secondsLeft = Math.floor(timeLeft / 1000);
        var minutesLeft = Math.floor(secondsLeft / 60);
        var secondsPart = secondsLeft % 60;
        var hoursPart = Math.floor(minutesLeft / 60);
        var minutesPart = minutesLeft % 60;
        return {
            time: time,
            timeLeft: timeLeft,
            secondsLeft: secondsLeft,
            seconds: secondsPart,
            minutes: minutesPart,
            hours: hoursPart
        };
    }

    var shade = XNAT.ui.dialog.shade({
        protected: true,
        id: 'page-obfuscator',
        afterShow: function(){
            shade.isOpen = true;
        }
    }).hide();

    // custom shade
    shade.mask$.css({
        background: '#aaa',
        opacity: '0.95'
    });

    window.shade = shade;

    // these things need to wait for the DOM to load
    // ...or do they?
    (function(){

        function eRedir(x, p){
            var e = {
                X: XNAT.sub64.dlxEnc(x || UN).encoded,
                P: XNAT.sub64.encode(p || LOC).encoded
            };
            e.ENC = e.X + '@' + e.P;
            return e;
        }

        function dRedir(x, p){
            var d = {
                X: XNAT.sub64.dlxDec(x).decoded,
                P: XNAT.sub64.decode(p).decoded
            };
            d.DEC = d.X + '@' + d.P;
            return d;
        }

        var curr = eRedir();

        var resumeCounter = 0;

        // if the last visited page is different than the current page,
        // try to want to go to there, if same user
        // function resumeSession(x, p){
        //
        //     console.log('resumeSession()');
        //
        //     var redirObj, decParts;
        //
        //     // prevent infinite callback loop
        //     if ((resumeCounter += 1) > 10) {
        //         xx.consoleLog('too many cooks');
        //         return;
        //     }
        //
        //     curr = eRedir(x, p);
        //
        //     redirObj = cookie.SESSION_REDIRECT.getJSON();
        //
        //     xx.consoleLog(redirObj);
        //
        //     // !!!!!!!!!!!!!!!!!!!!!!!!!!
        //     //
        //     var redirObjSample = {
        //         'bob': '/page/foo',
        //         'fred': '/page/bar'
        //     };
        //     //
        //     // !!!!!!!!!!!!!!!!!!!!!!!!!!
        //
        //
        //     // TODO: (MAYBE?) prepend url with '!' if redirect is done
        //
        //
        //     // only attempt a redirect if user@page is different
        //     if (curr.X in redirObj && redirObj[curr.X] && redirObj[curr.X] !== curr.P) {
        //
        //         decParts = dRedir(curr.X, redirObj[curr.X]);
        //
        //         // alert('going to: ' + decParts.P);
        //
        //         cookie.SESSION_REDIRECT.update(curr.X, '');
        //
        //         // DO NOT REDIRECT... YET...
        //         // window.location.href = decParts.P;
        //
        //         // calling this again should let
        //         // the session resume properly
        //
        //     }
        //     else {
        //
        //         cookie.SESSION_REDIRECT.update(curr.X, curr.P);
        //         // resumeSession(curr.X, curr.P);
        //
        //         // if (LOC !== p) {
        //         //     window.location.href = p;
        //         // }
        //         // else {
        //         //     resumeSession()
        //         // }
        //         // console.log('???');
        //         //window.location.href = XNAT.url.rootUrl('/');
        //     }
        // }


        // resumeSession();


        // create the dialog but don't render until DOM load
        // and don't show it until needed
        function warningDialog(){

            var z = 99999;

            var warning = XNAT.ui.dialog.init({
                id: 'session-timeout-warning',
                classes: 'keep static',
                width: 360,
                // height: 200,
                speed: -1,   // do not open
                nuke: false, // do not destroy on close
                protected: true, // do not destroy EVER
                title: false,
                content: '' +
                '<div style="font-size:14px;">' +
                'Your <b>' + XNAT.app.siteId + '</b> session will expire in: <br><br>' +
                '<b class="mono timeout-hours"></b> hours ' +
                '<b class="mono timeout-minutes"></b> minutes ' +
                '<b class="mono timeout-seconds"></b> seconds.' +
                '</br></br>Click <b>"Renew"</b> to reset session timer.' +
                '</div>',
                beforeShow: function(){
                    // console.log('beforeShow');
                    // ONLY SHOW THE DIALOG IF NOT OPEN OR CANCELLED
                    return !userSession.dialogIsOpen && cookie.SESSION_DIALOG_CANCELLED.is('false');
                },
                afterShow: function(){
                    // console.log('afterShow');
                    // set this as a js var so it's window-independent
                    cookie.SESSION_DIALOG_OPEN.set('true');
                    userSession.dialogIsOpen = true;
                    // if (cookie.SESSION_DIALOG_CANCELLED.is('false')) {
                    //     cookie.SESSION_DIALOG_OPEN.set('true');
                    //     userSession.dialogIsOpen = true;
                    // }
                },
                onHide: function(){
                    // console.log('onHide');
                    return userSession.dialogIsOpen;
                },
                afterHide: function(){
                    // console.log('afterHide');
                    shade.hide();
                    cookie.SESSION_DIALOG_OPEN.set('false');
                    userSession.dialogIsOpen = false;
                },
                buttons: [
                    {
                        label: 'Renew',
                        isDefault: true,
                        close: false,
                        action: function(dialog){
                            userSession.renew();
                            dialog.hide(0);
                        }
                    },
                    {
                        label: 'Close',
                        close: false,
                        action: function(dialog){
                            userSession.handleCancel();
                            dialog.hide(0);
                        }
                    }
                ],
                // okLabel: 'Renew',
                // okClose: false,
                // okAction: function(){
                //     userSession.renew();
                //     warning.hide();
                // },
                // cancelClose: false, // don't destroy the dialog
                // cancelAction: function(){
                //     userSession.handleCancel();
                //     warning.hide();
                // },
                bogus: null

            }).hide();

            warning.hours = warning.$modal.find('b.timeout-hours');
            warning.minutes = warning.$modal.find('b.timeout-minutes');
            warning.seconds = warning.$modal.find('b.timeout-seconds');

            // warning.show = function(){
            //     // DON'T SHOW IF ALREADY SHOWING
            //     // set this as a js var so it's window-independend
            //     if (userSession.dialogIsOpen) return;
            //     // ONLY SHOW THE DIALOG IF NOT CANCELLED
            //     if (cookie.SESSION_DIALOG_CANCELLED.is('false')) {
            //         warning.$mask.show();
            //         warning.$modal.show();
            //         cookie.SESSION_DIALOG_OPEN.set('true');
            //         userSession.dialogIsOpen = true;
            //     }
            // };
            //
            // warning.hide = function(){
            //     if (userSession.dialogIsOpen) {
            //         warning.$modal.hide();
            //         warning.$mask.hide();
            //         cookie.SESSION_DIALOG_OPEN.set('false');
            //     }
            //     userSession.dialogIsOpen = false;
            // };

            return warning;

        }


        userSession.warningDialog = warningDialog();


        function redirectToLogin(){
            var END = userSession.endTime;
            var lastPageEnc = eRedir();
            userSession.redirecting = true;
            userSession.warningDialog.hide(0);
            if (!shade.isOpen) {
                shade.open();
            }
            XNAT.ui.dialog.message({
                title: false,
                content: '' +
                '<div class="warning" style="font-size:14px;">' +
                'User session ended: ' + (new Date(END)).toLocaleString() +
                '</div>',
                // padding: 30,
                okAction: function(){
                    // window.location.href = XNAT.url.rootUrl('/');
                    cookie.SESSION_REDIRECT.update(lastPageEnc.X, lastPageEnc.P);
                    userSession.logout('timeout');
                    // userSession.setValue(KEYS.status, 'timeout');
                    //// window.location.reload();
                }
            });
            cookie.SESSION_DIALOG_OPEN.set('false');
            cookie.SESSION_DIALOG_CANCELLED.set('false');
            cookie.SESSION_TIMED_OUT.set('true');
            cookie.SESSION_TIMEOUT_TIME.set(END);
            // cookie.SESSION_TIMEOUT_STRING.set(dateString(NOW));
            // cookie.SESSION_REDIRECT.update(lastPageEnc.X, lastPageEnc.P);
            timeoutCookie('WARNING_BAR').set('OPEN');
            timeoutCookie('guest').set('true');
            // need to wait a little longer before reloading
            window.setTimeout(function(){
                if (!window.top.debug) {
                    XNAT.dialog.loadingBar.show();
                }
                window.setTimeout(function(){
                    // by going to the site root after timing out,
                    // this script will handle redirection
                    cookie.SESSION_REDIRECT.update(lastPageEnc.X, lastPageEnc.P);
                    // window.location.href = XNAT.url.rootUrl('/#timeout=' + NOW);
                    // window.location.reload();
                    userSession.logout('timeout');
                    // userSession.setValue(KEYS.status, 'timeout');
                }, 2000);
            }, 20000)
        }


        function renewSession(){
            // redirect if trying to renew an expired session
            if (cookie.SESSION_ACTIVE.is('false')) {
                // resumeSession();
                redirectToLogin();
            }
            userSession.warningDialog.hide(100);
            shade.hide();
            cookie.SESSION_EXPIRATION_TIME.get();
            userSession.getValues();
            cookie.SESSION_DIALOG_OPEN.set('false');
            cookie.SESSION_DIALOG_CANCELLED.set('false');
            cookie.SESSION_TIMED_OUT.set('false');
            cookie.SESSION_TIMEOUT_TIME.set(userSession.endTime);
            // cookie.SESSION_TIMEOUT_STRING.set(dateString(userSession.endTime));
            if (console && console.log) {
                console.log('Session ends: ' + dateString(userSession.endTime, false));
            }
            // cookie.SESSION_REDIRECT.update(lastPageEnc.X, lastPageEnc.P);
            userSession.cancelled = false;
        }


        userSession.touch = function(opts){
            return XNAT.xhr.get(extend(true, {
                url: XNAT.url.rootUrl('/xapi/siteConfig/buildInfo')
            }, opts || {}));
        };


        userSession.renew = function(){
            return userSession.touch().done(function(data){
                cookie.SESSION_DIALOG_CANCELLED.set('true');
                // an object is returned if session is still valid
                userSession.sessionExpired = !isPlainObject(data);
                if (userSession.sessionExpired) {
                    redirectToLogin();
                }
                else {
                    renewSession();
                }
            });
        };
        // alias for legacy compatibility
        userSession.handleOk = userSession.renew;


        // fire this once when the script loads
        userSession.renew();


        userSession.handleCancel = function(){
            userSession.warningDialog.hide(100);
            userSession.cancelled = true;
            cookie.SESSION_DIALOG_CANCELLED.set('true');
            cookie.SESSION_DIALOG_OPEN.set('false');
        };


        // manually log out
        userSession.logout = function(status){
            // var curr = eRedir(UN, LOC);
            cookie.SESSION_REDIRECT.update(curr.X, curr.P);
            XNAT.xhr.get(window.logoutUrl, function(){
                var STATUS = status !== undef ? status : '';
                var END = (STATUS === 'logout') ? Date.now() : userSession.endTime;
                var loginRedirectUrl = XNAT.url.rootUrl('/app/template/Login.vm' + (STATUS ? ('#' + STATUS + '=' + END) : ''));
                userSession.setValue(KEYS.status, STATUS || 'logout');
                userSession.setValue(KEYS.endTime, END);
                userSession.setValue(KEYS.currentPage, '');
                userSession.setValueToo(KEYSenc.userLastPage, LOCe);
                userSession.getValue(KEYS.continue, function(val){
                    if (/true/i.test(val)) {
                        loginRedirectUrl += '#continue'
                    }
                    $('#layout_content').fadeOut(20, function(){
                        $('#layout_content2').empty().spawn('div.message', 'Logging out...').fadeIn(100, function(){
                            window.setTimeout(function(){
                                window.location.replace(loginRedirectUrl);
                            }, 1000);
                        });
                    });
                });
                return false;
            });
        };


        // check every second to see if our timeout time has been reached
        userSession.check = function(){

            userSession.getValues();

            var NOW = Date.now();

            // redirect if TIMED_OUT cookie is true
            if (!userSession.redirecting && cookie.SESSION_TIMED_OUT.is('true')) {
                redirectToLogin();
                return false;
            }

            // set cookie if time has run out - will redirect on next tick
            if (userSession.endTime <= (NOW + 500)) {
                cookie.SESSION_TIMED_OUT.set('true');
                return false;
            }

            // redirect if logged out from another window
            if (cookie.SESSION_ACTIVE.is('false')) {
                redirectToLogin();
                return false;
            }

            // close dialog if closed from another window
            if (cookie.SESSION_DIALOG_OPEN.is('false')) {
                userSession.warningDialog.hide(0);
            }

            // if endTime minus showTime is less than now
            if (userSession.endTime - (userSession.showTime * 1000) <= NOW) {
                //don't do anything if the dialog has already been cancelled
                if (cookie.SESSION_DIALOG_CANCELLED.is('true')) {
                    return false;
                }
                cookie.SESSION_DIALOG_CANCELLED.set('false');
                userSession.warningDialog.toTop(false).show(100);
                return false;
            }

            return true;

        };


        userSession.countdown = function(){

            var timeLeft = parseTimestamp();

            var hours = timeLeft.hours;
            var mins = zeroPad(timeLeft.minutes);
            var secs = zeroPad(timeLeft.seconds);

            $timeLeftDisplay.text(hours + ":" + mins + ":" + secs);

            if (cookie.SESSION_TIMED_OUT.is('true')) {
                $timeLeftDisplay.text("Session Expired");
                hours = mins = secs = '--';
            }

            // Update the text in the dialog too so it's always in synch
            userSession.warningDialog.hours.text(hours);
            userSession.warningDialog.minutes.text(mins);
            userSession.warningDialog.seconds.text(secs);

        };


        userSession.running = false;


        userSession.init = function(){
            if (!userSession.running) {
                userSession.running = true;
                window.setInterval(
                    function(){
                        userSession.check();
                        userSession.countdown();
                    },
                    userSession.timerInterval
                );
            }
        };

        // only run the timer if *not* a guest user (if an authenticated user)
        if ((!!XNAT.cookie.get('guest')) && (XNAT.cookie.get('guest') === 'false')) {
            userSession.init();
        }

        // attach event handler to elements with 'renew-session' class
        $(document).on('click', '#timeLeftRenew, .renew-session', function(){
            userSession.renew();
        });

        $(window).on('beforeunload', function(){
            userSession.setValue(KEYS.currentPage, '');
            userSession.setValueToo(KEYSenc.userLastPage, LOCe);
        });

    })();


    // this script has loaded
    userSession.loaded = true;

    // alias to old namespace
    XNAT.app.timeout = userSession;

    return XNAT.app.userSession = userSession;


}));

