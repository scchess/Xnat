/*!
 * Initialize XNAT settings
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

    var undef, init;

    XNAT.init = init = getObject(XNAT.init || {});

    XNAT.init.debug =
        getQueryStringValue('debug') ||
        getQueryStringValue('jsdebug');

    if (XNAT.init.debug === 'true') {
        console.log('/scripts/xnat/init.js');
    }

    // reset or remove cookies based on query string
    (function(){
        var cookiesParam = getQueryStringValue('cookies');
        var cookiePath = XNAT.url.rootUrl();
        if (/[*/~]/.test(cookiesParam.split('|')[1] || '')) {
            cookiePath = '/';
        }
        if (/^reset/i.test(cookiesParam)) {
            XNAT.cookie.resetAll({ path: cookiePath })
        }
        if (/^remove/i.test(cookiesParam)) {
            XNAT.cookie.removeAll({ path: cookiePath });
        }

    })();


    // add build info to page elements *AFTER* DOM load
    $(function(){

        // prevent default click triggers on '#' links
        $(document).on('click', '[href^="#"], [href^="@!"]', function(e){
            e.preventDefault();
        });

        // add 'dirty' class to changed input on focus
        $(document).on('focus.check-input', 'input, textarea', function(){
            // console.log('focus');
            var input$ = $(this);
            var startVal = input$.data('value') || input$.val();
            input$.off('blur.check-input');
            input$.on('blur.check-input', function(){
                // console.log('blur');
                var this$ = $(this);
                if (startVal !== this$.val()) {
                    this$.addClass('dirty');
                }
                else {
                    this$.removeClass('dirty');
                }
            });
        });

        $(document).on('click.check-ckbx', 'input[type="radio"], input[type="checkbox"]', function(){
            console.log('changed');
            $(this).addClass('dirty');
        });

        // manhandle 'checked' attribute and property on radio buttons
        // applies to radio buttons with the same name in the same form
        $(document).on('click', 'input[type="radio"]', function(){
            var radio$ = $(this);
            // make sure we've got the custom 'checked' method
            if (radio$.checked && isFunction(radio$.checked)) {
                radio$.closest('form').find('input[name="' + this.name + '"]').checked(false);
                radio$.checked(true);
            }
        });

        // <input type="checkbox" name="bogus" data-values="yes|no" data-proxy="id-of-proxy-input">
        // ...or...
        // <input type="checkbox" name="bogus" title="bogus=yes|no">
        // ...or...
        // <input type="checkbox" name="bogus" title="bogus: yes|no">
        $(document).on('change', 'input.controller[type="checkbox"]', function(){

            var ckbx$ = $(this);
            var ckbx0 = this;
            var NAME = (ckbx0.name || ckbx0.title.split(/[:=]/)[0] || ckbx$.data('name')).trim();
            var values = ['true', 'false'];
            var dataValues = ckbx$.data('values') || ckbx$.data('options');

            ckbx0.name = '';

            // if the [title] attribute contains '=' (or ':') and '|'
            // ...it's probably the value options
            if (!dataValues && /.+[:=].+[|]/.test(ckbx0.title)) {
                dataValues = (ckbx0.title.split(/[:=]/)[1] || '').trim();
            }

            if (dataValues) {
                values = (dataValues+'').split('|');
            }

            var VALUE = ckbx0.checked ? (values[0]+'').trim() : (values[1]+'').trim();
            ckbx0.value = VALUE;

            var container$ = ckbx$.closest('form');

            if (!container$.length) {
                container$ = $(document.body);
            }

            var proxyId = ckbx$.data('proxy');
            var proxy$ = [];

            // is a proxy defined in the input element?
            if (proxyId) {
                proxy$ = container$.find('#' + proxyId);
                proxy$[0].name = NAME;
            }
            else {
                // is there already a proxy input that tracks the value?
                proxy$ = container$.find('input.proxy[name="' + NAME + '"]');
                proxyId = randomID('prx', false);
                ckbx$.data('proxy', proxyId);
            }

            if (!proxy$.length) {
                proxy$ = $.spawn('input.proxy|type=hidden');
                ckbx$.after(proxy$);
            }

            proxy$[0].id = proxyId;
            proxy$[0].value = VALUE;

            // ckbx$.addClass('ready');

        });

        // make sure switchboxes track values properly
        // encode the input name and values into the [title] attribute of the outer <label> element:
        // <label class="switchbox" title="myInput=checkedValue|uncheckedValue">
        // ...or...
        // <label class="switchbox" title="myInput: checkedValue|uncheckedValue">
        // $(document).on('change', 'input.switchbox', function(){
        //     var chkbox$ = $(this);
        //     var chkbox0 = chkbox$[0];
        //     var switch$ = chkbox$.closest('label.switchbox');
        //     var switch0 = switch$[0];
        //     var NAME    = switch0.title.split(/[:=]/)[0];
        //     var VALUES  = (switch0.title.split(/[:=]/)[1] || '').split('|') || ['true', 'false'];
        //     var proxy$  = switch$.find('input.proxy');
        //     var proxy0  = proxy$[0];
        //     if (!proxy0) {
        //         proxy0 = spawn('input.proxy|type=hidden');
        //         switch0.appendChild(proxy0);
        //     }
        //     proxy0.name = NAME;
        //     proxy0.value = chkbox0.checked ? (VALUES[0]+'').trim() : (VALUES[1]+'').trim();
        //     // set [name] attribute for the checkbox to an empty string
        //     if (chkbox0.name) {
        //         chkbox0.name = '';
        //     }
        //     chkbox0.value = proxy0.value
        // });

        // add version to title attribute of XNAT logos
        if (window.top.loggedIn !== undef && window.top.loggedIn === true) {

            XNAT.cookie.set('SESSION_ACTIVE', 'true');

            var buildInfoSample = {
                "Application-Name": "XNAT",
                "Manifest-Version": "1.0",
                buildDate: "Sun Jun 05 12:41:24 CDT 2016",
                buildNumber: "Manual",
                commit: "v275-gd2220fd",
                version: "1.7.0"
            };

            function displayBuildInfo(data){
                var version = XNAT.version = XNAT.data.version = data.version;
                var version_string = version + ', build: ' + data.buildNumber;
                var version_title = 'XNAT version ' + version_string;
                var isNonRelease = /SNAPSHOT|BETA|RC/ig.test(version);
                var build_string = ' ';

                if (isNonRelease) {
                    version_string += ' (' + data.commit + ')';
                    build_string += '<br>' + data.buildDate;
                }

                $('#xnat_power')
                    .empty()
                    .spawn('a.xnat-version', {
                        href: 'http://www.xnat.org',
                        target: '_blank',
                        title: version_title
                    }, [['img', { src: serverRoot + '/images/xnat_power_small.png' }]])
                    .spawn('small', 'version ' + version_string + build_string);

                // $('#xnat_power').find('a')
                //                 .attr('title', version_title)
                //                 .after('<small>version ' + version_string + build_string + '</small>');

                $('#header_logo').attr('title', version_title);

                // save the complete string
                XNAT.app.version = version_string;

                console.log('version ' + XNAT.version);

            }

            // use existing data if available
            if (XNAT.data && XNAT.data.siteConfig && XNAT.data.siteConfig.buildInfo) {
                displayBuildInfo(XNAT.data.siteConfig.buildInfo);
            }
            else {
                XNAT.xhr.get(serverRoot + '/xapi/siteConfig/buildInfo', function(data){
                    extend(true, XNAT, {
                        data: {
                            siteConfig: {
                                buildInfo: data
                            }
                        }
                    });
                    displayBuildInfo(data);
                });
            }
        }
        else {
            XNAT.cookie.set('SESSION_ACTIVE', 'false');
        }

        var clicker = XNAT.event.click('#header_logo, #xnat_power > a');

        // shift-click the header or footer XNAT logo to TOGGLE debug mode on/off
        clicker.shiftKey(function(e){
            e.preventDefault();
            if (XNAT.cookie.get('debug') === 'on') {
                XNAT.cookie.set('debug', 'off');
                window.location.hash = 'debug=off';
            }
            else {
                XNAT.cookie.set('debug', 'on');
                window.location.hash = 'debug=on';
            }
            window.location.reload();
        });

        // alt-shift-click to open the Swagger page in a new window
        clicker.altShift(function(e){
            e.preventDefault();
            XNAT.ui.popup(XNAT.url.rootUrl('/xapi/swagger-ui.html'));
        });

    });


    // this script has loaded
    init.loaded = true;

    return XNAT.init = init;

}));
