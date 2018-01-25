/*!
 * Interractions with main nav menu
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

    var undef;
    var mainNav;

    XNAT.app = getObject(XNAT.app || {});

    XNAT.app.mainNav = mainNav =
        getObject(XNAT.app.mainNav || {});



    // initialize main nav
    mainNav.init = function(){

        var docBody          = $(document.body);
        var mainNav$         = $('#main_nav').find('ul.nav');
        var mainNavBackdrop$ = $('#main-nav-backdrop');

        function mainNavClose(e){
            mainNavBackdrop$.hide();
            mainNav$.find('.active').removeClass('active');
            mainNav$.find('.open').hide().removeClass('open');
        }

        $('#nav-home').css({
            width: '30px',
            backgroundImage: "url('" + serverRoot + "/images/xnat-nav-logo-white-lg.png')",
            backgroundRepeat: 'no-repeat',
            backgroundSize: '32px',
            backgroundPosition: 'center'
        });

        // give menus with submenus a class of 'more'
        mainNav$.find('li ul, li li ul').closest('li').addClass('more');
        mainNav$.find('li li ul').addClass('subnav');

        // mainNav$.on('click.nolink', 'a[href^="#"]', function(e){
        //     e.preventDefault();
        // });

        mainNav$.on('click.topnav', '> li', function(e){
            // e.stopPropagation();

            var topMenuItem  = $(this);
            var subMenu      = topMenuItem.find('> ul');
            var subMenuItems = null;

            mainNavBackdrop$.show();

            // deactive all top-level items
            mainNav$.find('> li').removeClass('active');
            // activate only this one
            topMenuItem.addClass('active');

            subMenuItems = subMenu.find('> li.more').removeClass('active');
            subMenuItems.off('click.subnav').on('click.subnav', function(e){
                e.stopPropagation();
                subMenuItems.removeClass('active');
                var thisMenuItem = $(this);
                thisMenuItem.addClass('active')
                            .find('> ul')
                            .each(function(){
                                var sub     = $(this);
                                var offsetL = sub.closest('li').width();
                                sub.css({ 'left': offsetL });
                            });
            });

        });

        mainNav$.on('click', 'a', function(e){
            var href = $(this).attr('href');
            if (href.indexOf('#') !== 0) {
                mainNavClose.call(this, e)
            }
        });
        mainNavBackdrop$.on('click', mainNavClose);

        // clicking the "Logout" link sets the warning bar cookie to 'OPEN' so it's available if needed on next login
        $('#logout_user').click(function(e){
            e.preventDefault();
            XNAT.cookie.set('WARNING_BAR', 'OPEN');
            XNAT.cookie.set('NOTIFICATION_MESSAGE', 'OPEN');
            XNAT.app.userSession.logout('logout');
        });

    };



    // initialize on dom ready
    $(document).ready(mainNav.init);




    // this script has loaded
    mainNav.loaded = true;

    return XNAT.app.mainNav = mainNav;

}));
