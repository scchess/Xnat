<%@ page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="pg" tagdir="/WEB-INF/tags/page" %>

<pg:init/>
<pg:jsvars/>

<c:url var="SITE_ROOT" value=""/>

<%-- get certain siteConfig settings to determine display of project settings tabs --%>
<script>

    XNAT.data.siteConfig = getObject(XNAT.data.siteConfig || {});

    <c:import url="/xapi/siteConfig/uiAllowQuarantine" var="uiAllowQuarantine"/>
    XNAT.data.siteConfig.uiAllowQuarantine = realValue(${uiAllowQuarantine});

    <c:import url="/xapi/siteConfig/projectAllowAutoArchive" var="projectAllowAutoArchive"/>
    XNAT.data.siteConfig.projectAllowAutoArchive = realValue(${projectAllowAutoArchive});

</script>

<style type="text/css">
    #project-settings-header { margin-bottom: 20px; }
</style>

<div id="page-wrapper">
    <div class="pad">

        <div id="project-not-specified" class="error hidden">Project not specified.</div>

        <%-- show an error if the project data is not returned from the rest call --%>
        <div id="project-data-error" class="error hidden">Data for "<span class="project-id"></span>" project not found.</div>

        <%-- if an 'id' param is passed, use its value to edit specified project data --%>
        <h3 id="project-settings-header" class="hidden">Settings for <span class="project-id"></span></h3>

        <div id="project-settings-tabs">
            <div class="content-tabs xnat-tab-container">

                <%--<div class="xnat-nav-tabs side pull-left">--%>
                <%--<!-- ================== -->--%>
                <%--<!-- Admin tab flippers -->--%>
                <%--<!-- ================== -->--%>
                <%--</div>--%>
                <%--<div class="xnat-tab-content side pull-right">--%>
                <%--<!-- ================== -->--%>
                <%--<!-- Admin tab panes    -->--%>
                <%--<!-- ================== -->--%>
                <%--</div>--%>

            </div>
        </div>

        <script>
            (function(){

                var PROJECT_ID =
                    window.projectId =
                        '${param.id}' || getQueryStringValue('id') || getUrlHashValue('#id=');

                var jsdebug = window.jsdebug || '${param.jsdebug}' || '${param.debug}' || false;

                if (!PROJECT_ID) {
                    $('#project-not-specified').hidden(false);
                    return;
                }

                $('span.project-id').text(PROJECT_ID);

                // cache DOM objects
                var projectSettingsHeader$ = $('#project-settings-header');

                // render siteSettings tab into specified container
                var projectSettingsTabs = $('#project-settings-tabs').find('div.content-tabs');

                // these properties _should_ be set before spawning 'tabs' widgets
                XNAT.tabs.container = projectSettingsTabs;
                XNAT.tabs.layout    = 'left';

                function getProjectSettings(){
                    return XNAT.spawner.resolve('xnat:projectSettings/root');
                }

                // get project data first so we have data to work with
                XNAT.xhr.getJSON({
                    url: XNAT.url.restUrl('/data/projects/' + PROJECT_ID),
                    success: function(data){

                        // make project id available
                        XNAT.data.projectId = XNAT.data.projectID = PROJECT_ID;

                        // make returned project data available for Spawner elements
                        XNAT.data.projectData = data;

                        if (jsdebug) { console.log(data) }

                        // show the header since we should have the data
                        projectSettingsHeader$.hidden(false);

                        // render standard XNAT project settings tabs
                        getProjectSettings().ok(function(obj){
                            this.render(
                                XNAT.tabs.container//,
                                //XNAT.app.projectSettings.init
                            );
                            this.done(function(){
                                // render default XNAT project settings tabs
                                XNAT.tab.activate(XNAT.tab.active, projectSettingsTabs);
                                // then render plugins' project settings tabs... FOR PLUGINS
                                XNAT.app.pluginSettings.showTabs = true;
                                XNAT.app.pluginSettings.hasProjectSettingsTabs = false; // reset every time
                                XNAT.app.pluginSettings.projectSettingsTabs = projectSettingsTabs;
                                XNAT.app.pluginSettings.projectSettings(projectSettingsTabs, function(data){
                                    if (jsdebug) {
                                        console.log(data);
                                        console.log(arguments);
                                    }
                                    //XNAT.tab.activate(XNAT.tab.active, projectSettingsTabs);
                                });

                                // populate values of form panels
//                                XNAT.app.projectSettings.init();

                            });
                        });
                    },
                    failure: function(){
                        // if REST call for project data fails,
                        projectSettingsHeader$.hidden();
                        $('#project-data-error').hidden(false);
                    }
                })

            }())

        </script>

    </div>
</div>
