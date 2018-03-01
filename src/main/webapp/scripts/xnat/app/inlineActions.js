console.log('inlineActions.js');

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

    XNAT.app =
        getObject(XNAT.app || {});
    XNAT.app.inlineActions =
        getObject(XNAT.admin.inlineActions || {});

    XNAT.app.inlineActions.init = function(target,actions,opts){
        function cannotInit(){
            console.log('Cannot initialize actions menu on '+target);
            return false;
        }

        opts = getObject(opts || {});
        
        if (!target || !actions) cannotInit();

        var $target = $$(target);
        var $menu;
        var undefined;

        // allow for initialization at multiple levels
        if ($target.hasClass('inline-actions-menu-container')) {
            $menu = $target.find('.inline-actions-menu');
        }
        else if ($target.hasClass('inline-actions-menu-toggle')) {
            $menu = $target.parents('.inline-actions-menu-container').find('.inline-actions.menu');
        }
        else if ($target.hasClass('inline-actions-menu')){
            $menu = $target;
        }
        else cannotInit();

        // add a title to the actions box, if specified
        if (opts.label) {
            $menu.append(spawn('li.inline-action-title',opts.label));
        }

        
        if (actions.length === 0) {
            $menu.parents('.inline-actions-menu-container').addClass('disabled')
        }
        else {
            var actionSet = [];
            actions.forEach(function(action){

                if (action.onclick !== undefined) {
                    actionSet.push(
                        spawn('li', { onclick: action.onclick }, [
                            spawn('a', {
                                html: action.label,
                                href: '#!',
                                className: 'actionLauncher'
                            })
                        ]));
                }
                else if (action.href !== undefined) {
                    actionSet.push(
                        spawn('li', [
                            spawn('a', {
                                html: action.label,
                                href: action.href,
                                className: 'actionLauncher'
                            })
                        ]));
                }
                
            });

            $menu.append(actionSet);
        }
        
    };


    /* selectable table checkbox behavior */
    $(document).on('click','.selectable-select-all',function(){
        var checkProp = false;
        var containingTable = $(this).parents('.data-table-container');

        // check all checkboxes in the table body to find any unchecked. Default behavior will be to check all.
        containingTable.find('tbody').find('.selectable-select-one').each(function(){
            if (!$(this).is(':checked')) {
                checkProp = 'checked';
                return false;
            }
        });

        // now iterate over all checkboxes again, performing the default operation
        containingTable.find('tbody').find('.selectable-select-one').each(function(){
            $(this).prop('checked',checkProp);
        });

        if (checkProp === 'checked') {
            containingTable.find('.data-table-action').removeClass('disabled');
        } else {
            containingTable.find('.data-table-action').addClass('disabled');
        }
    });


    // use the inline menu toggle as the trigger for opening the menu.
    // hide the menu after a short time if the user does not mouse into the menu.
    $(document).on('mouseenter','.inline-actions-menu-toggle',function(){
        var $menu = $(this).parents('td').find('.inline-actions-menu');
        $menu.show();

        setTimeout(function(){
            if (!$menu.hasClass('active')) $menu.fadeOut(150);
        },1500);
    });

    $(document).on('mouseenter','.inline-actions-menu',function(){ $(this).addClass('active'); });
    $(document).on('mouseleave','.inline-actions-menu',function(){ $(this).removeClass('active').hide(); });

// if user mouses away from the toggle button, hide the menu unless the user has moused over the menu
//     $(document).on('mouseleave','.inline-actions-menu-toggle',function(event){
//         var menu = $(this).parents('td').find('.inline-actions-menu');
//         if (!event.relatedTarget.className.match(/menu/) ) {
//             setTimeout(function(){
//                 if (!menu.hasClass('active')) menu.hide();
//             },100);
//         }
//     });

}));