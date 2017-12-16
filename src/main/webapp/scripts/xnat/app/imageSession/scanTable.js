/**
 * Functionality for scan tables on image session pages
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

    XNAT.app =
        getObject(XNAT.app || {});

    XNAT.app.imageSession =
        getObject(XNAT.app.imageSession || {});

    XNAT.app.imageSession.scanTable =
        getObject(XNAT.app.imageSession.scanTable || {});

    var scanTable = XNAT.app.imageSession.scanTable;

    var projectId = XNAT.data.context.project;
    var subjectId = XNAT.data.context.subjectID;
    var exptId    = XNAT.data.context.ID;

    var htmlUrlExample = '/data/experiments/XNAT_E01124/scans/0,1,2/files?format=html';
    var zipUrlExample  = '/data/experiments/XNAT_E01124/scans/0,1,2/files?format=zip';

    scanTable.container$ = $('#selectable-table-scans');
    scanTable.dataTable$ = $('#scan-data-table');

    function scanUrl(part){
        return XNAT.url.rootUrl(
            '/REST' +
            '/projects/' + projectId +
            '/subjects/' + subjectId +
            '/experiments/' + exptId +
            (part || '')
        )
    }

    // extract scan id from a.href
    function getScanId(a){
        return getHashValue(a.href, 'id=')
    }

    // create then destroy iframe to initiate download
    function downloadIframe(url){
        var iframedId = randomID('dli', false);

        var downloadFrame$ = $.spawn('iframe.download.hidden', {
            id: iframedId,
            attr: { src: url }
        });

        $('body').append(downloadFrame$);

        // remove the iframe after 5 seconds
        window.setTimeout(function(){
            downloadFrame$.remove();
        }, 5000);
    }

    function loadSnapshotImageNoBlocking(scanID) {
        var element = $("#" + "scan" + scanID + "snapshot");
        element[0].src = element.data("xnat-src");
        return true;
    }

    // inline scan table functions
    function displayScanDetails(scanId){

        if (!scanId) return false;

        if (loadSnapshotImageNoBlocking(scanId)) {

            var tmpl = $('#scan-' + scanId + '-details-template').html();

            XNAT.ui.dialog.message({
                title: 'Scan ' + scanId,
                width: 720,
                content: tmpl,
                isDraggable: true,
                mask: false,
                esc: true,
                okLabel: 'Close',
                footer: {
                    content: 'Click in the header to move this dialog around the page'
                }
            });

        }
    }

    function renderViewer(scanId){
        var url = scanUrl('/scans/' + scanId);
        launchXImgView(XNAT.url.rootUrl(url));
    }

    // download all selected scans
    function downloadSelectedScans(){
        var selectedScans = [];
        scanTable.dataTable$.find('input.selectable-select-one:checked').each(function(){
            selectedScans.push(this.value);
        });
        var url = scanUrl('/scans/' + selectedScans + '/files?format=zip');
        downloadIframe(url);
    }


    // download a *single* scan
    function downloadScan(scanId){
        scanId = scanId || getScanId(this);
        var url = scanUrl('/scans/' + scanId + '/files?format=zip');
        downloadIframe(url);
    }

    function deleteScan(scanId){
        scanId = scanId || getScanId(this);
        XNAT.ui.dialog.open({
            title: 'Delete Scan ' + scanId,
            width: 480,
            nuke: true, // destroys the modal on close, rather than preserving its state
            content: XNAT.spawner.spawn({
                p: {
                    tag: 'p',
                    content: 'This will delete scan <b>' + scanId + '</b> from this image session. You can choose whether to delete all scan files from the filesystem as well.'
                },
                delete_files: {
                    kind: 'switchbox',
                    name: 'delete_files',
                    id: 'delete_files',
                    value: 'true',
                    values: 'true|false',
                    label: 'Delete Scan Files',
                    onText: 'Yes (Default)',
                    offText: 'No'
                }
            }).get(),
            buttons: [
                {
                    label: 'Delete Scan',
                    isDefault: true,
                    close: false,
                    action: function(obj){

                        var deleteUrl   = '/REST/experiments/' + XNAT.data.context.ID + '/scans/' + scanId;
                        var deleteFiles = obj.dialogBody$.find('#delete_files').is(':checked');

                        var params = [
                            'format=json',
                            'event_action=Removed scan',
                            'event_type=WEB_FORM'
                        ];

                        if (deleteFiles) {
                            params.push('removeFiles=true');
                        }

                        XNAT.xhr.delete({
                            url: XNAT.url.csrfUrl(deleteUrl, params),
                            success: function(data){
                                XNAT.ui.dialog.open({
                                    title: 'Success',
                                    width: 360,
                                    content: '<p>Scan Deleted. Page will reload.</p>',
                                    buttons: [
                                        {
                                            label: 'OK',
                                            isDefault: true,
                                            close: true,
                                            action: function(){
                                                XNAT.ui.dialog.closeAll();
                                                window.location.reload();
                                            }
                                        }
                                    ]
                                })
                            },
                            fail: function(e){
                                XNAT.ui.dialog.open({
                                    title: 'Error',
                                    width: 360,
                                    content: '<p><strong>Error ' + e.status + '</strong></p><p>' + e.statusText + '</p>',
                                    buttons: [
                                        {
                                            label: 'OK',
                                            isDefault: true,
                                            close: true
                                        }
                                    ]
                                })
                            }
                        })
                    }
                },
                {
                    label: 'Cancel',
                    isDefault: false,
                    close: true
                }
            ]
        });
    }

    function editScanNote(e){

        var this$    = $(this);
        var scanId   = this$.data('scanId');
        var xsiType  = XNAT.data.context.xsiType;
        var noteText = $('#scan-' + scanId + '-note').find('.scan-note-content > span').html().trim();
        var noteEditor = spawn('textarea.note-editor', {
            name: xsiType + '/scans/scan[ID=' + scanId + ']/note',
            value: noteText,
            attr: { rows: 10 },
            style: { width: '100%' }
        });

        var editorForm$ = $.spawn('form.scan-note-editor', [noteEditor]);

        XNAT.ui.dialog.open({
            title: 'Edit Note for Scan ' + scanId,
            width: 480,
            destroyOnClose: true,
            content: editorForm$[0],
            afterShow: function(){
                $(noteEditor).focus();
            },
            buttons: [
                {
                    label: 'Update Note',
                    isDefault: true,
                    close: false,
                    action: function(obj){
                        var updateNoteUrl    = scanUrl('?req_format=form', projectId, subjectId);
                        if (!noteEditor.value) {
                            noteEditor.value = ' '
                        }
                        var updateNoteString = editorForm$.serialize() || '';
                        XNAT.xhr.put({
                            url: XNAT.url.csrfUrl(updateNoteUrl),
                            data: updateNoteString,
                            success: function(data){
                                XNAT.ui.dialog.message({
                                    title: 'Success',
                                    width: 360,
                                    content: '<p>Scan note updated. Page will reload.</p>',
                                    enter: true,
                                    buttons: [
                                        {
                                            label: 'OK',
                                            isDefault: true,
                                            close: true,
                                            action: function(){
                                                XNAT.ui.dialog.closeAll();
                                                window.location.reload();
                                            }
                                        }
                                    ]
                                })
                            },
                            fail: function(e){
                                XNAT.ui.dialog.message({
                                    title: 'Error',
                                    width: 360,
                                    content: '<p><strong>Error ' + e.status + '</strong></p><p>' + e.statusText + '</p>',
                                    enter: true,
                                    esc: true,
                                    buttons: [
                                        {
                                            label: 'OK',
                                            isDefault: true,
                                            close: true
                                        }
                                    ]
                                })
                            }
                        })
                    }
                },
                {
                    label: 'Cancel',
                    close: true
                }
            ]
        });


        // XNAT.ui.dialog.open({
        //     title: 'Edit Note for Scan ' + scanId,
        //     width: 480,
        //     nuke: true,
        //     content: '<div class="panel"><form id="scan-note-editor"></form></div>',
        //     beforeShow: function(obj){
        //         obj.dialogBody$.find('#scan-note-editor')
        //            .append(XNAT.ui.panel.textarea({
        //                name: xsiType + '/scans/scan[ID=' + scanId + ']/note',
        //                label: 'Note',
        //                value: noteText,
        //                code: 'html'
        //            }).spawned);
        //     },
        //     buttons: [
        //         {
        //             label: 'Update Note',
        //             isDefault: true,
        //             close: false,
        //             action: function(obj){
        //                 var updateNoteUrl    = '/REST/projects/' + XNAT.data.context.projectID + '/subjects/' + XNAT.data.context.subjectID + '/experiments/' + XNAT.data.context.ID + '?req_format=form';
        //                 var updateNoteString = obj.dialogBody$.find('#scan-note-editor').serialize();
        //                 updateNoteString     = updateNoteString || null;
        //                 XNAT.xhr.put({
        //                     url: XNAT.url.csrfUrl(updateNoteUrl),
        //                     data: updateNoteString,
        //                     success: function(data){
        //                         XNAT.ui.dialog.open({
        //                             title: 'Success',
        //                             width: 360,
        //                             content: '<p>Scan note updated. Page will reload.</p>',
        //                             buttons: [
        //                                 {
        //                                     label: 'OK',
        //                                     isDefault: true,
        //                                     close: true,
        //                                     action: function(){
        //                                         XNAT.ui.dialog.closeAll();
        //                                         window.location.reload();
        //                                     }
        //                                 }
        //                             ],
        //                         })
        //                     },
        //                     fail: function(e){
        //                         XNAT.ui.dialog.open({
        //                             title: 'Error',
        //                             width: 360,
        //                             content: '<p><strong>Error ' + e.status + '</strong></p><p>' + e.statusText + '</p>',
        //                             buttons: [
        //                                 {
        //                                     label: 'OK',
        //                                     isDefault: true,
        //                                     close: true
        //                                 }
        //                             ]
        //                         })
        //                     }
        //                 })
        //             }
        //         },
        //         {
        //             label: 'Cancel',
        //             isDefault: false,
        //             close: true
        //         }
        //     ]
        // });

    }

    // init function for XNAT.app.scanTable
    scanTable.init = function(){

        projectId = XNAT.data.context.project;
        subjectId = XNAT.data.context.subjectID;
        exptId    = XNAT.data.context.ID;

        var scanTableContainer$ = scanTable.container$ = $('#selectable-table-scans');

        // get this once and cache for later use
        var scanDataTable$ = scanTable.dataTable$ = $('#scan-data-table');

        //
        scanTableContainer$.on('click.view-details', 'a[href^="#!details"]', function viewDetailsFn(e){
            e.preventDefault();
            displayScanDetails.call(this, getScanId(this));
        });

        scanTableContainer$.on('click.ximg-viewer', 'a[href^="#!ximg-viewer"]', function ximgViewerFn(e){
            e.preventDefault();
            renderViewer.call(this, getScanId(this));
        });

        // Download a *single* scan (?)
        scanTableContainer$.on('click.download-scan', 'a[href^="#!download"]', function downloadScanFn(e){
            e.preventDefault();
            downloadScan.call(this, getScanId(this));
        });

        //
        scanTableContainer$.on('click.delete-scan', 'a[href^="#!delete"]', function deleteScanFn(e){
            e.preventDefault();
            deleteScan.call(this, getScanId(this));
        });

        // Download *all* selected scans
        scanTableContainer$.on('click.download-scans', 'button.do-scan-download:not(.disabled)', function downloadSelectedFn(e){
            e.preventDefault();
            downloadSelectedScans.call(this);
        });

        //
        scanTableContainer$.on('click', '.edit-scan-note', editScanNote);

    };

    $(document).ready(scanTable.init);

    $(document).on('click', 'table.scan-details a.view-dicom-headers', function(e){
        e.preventDefault();
        XNAT.dialog.load($(this).attr('href') + ' #layout_content table.dump', { minWidth: 800, width: '80%', esc: true, enter: true });
    });

    // this script has loaded
    scanTable.loaded = true;

    return XNAT.app.scanTable = scanTable;

}));
