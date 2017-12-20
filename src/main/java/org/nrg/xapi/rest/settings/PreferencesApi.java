/*
 * web: org.nrg.xapi.rest.settings.PreferencesApi
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xapi.rest.settings;

import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.prefs.beans.AbstractPreferenceBean;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.nrg.xdat.security.helpers.AccessLevel.Admin;

@Api(description = "Preferences Service API")
@XapiRestController
@RequestMapping(value = "/prefs")
public class PreferencesApi extends AbstractXapiRestController {
    @Autowired
    public PreferencesApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final List<AbstractPreferenceBean> preferences) {
        super(userManagementService, roleHolder);
        for (final AbstractPreferenceBean preferenceBean : preferences) {
            _preferences.put(preferenceBean.getToolId(), preferenceBean);
        }
    }

    @ApiOperation(value = "Returns the full map of preferences and values for this XNAT application.", response = Properties.class, responseContainer = "Map")
    @ApiResponses({@ApiResponse(code = 200, message = "Preference settings successfully retrieved."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Insufficient privileges to retrieve the requested setting."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Admin)
    public ResponseEntity<Map<String, Properties>> getAllPreferenceSettings() {
        _log.info("User {} requested the system preference settings.", getSessionUser().getUsername());
        final Map<String, Properties> beanProperties = Maps.transformEntries(_preferences, new Maps.EntryTransformer<String, AbstractPreferenceBean, Properties>() {
            @Override
            public Properties transformEntry(@Nullable final String key, @Nullable final AbstractPreferenceBean value) {
                return value != null ? value.asProperties() : EMPTY_PROPERTIES;
            }
        });
        return new ResponseEntity<>(beanProperties, HttpStatus.OK);
    }

    @ApiOperation(value = "Returns the full map of preferences and values for this XNAT application.", response = String.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Preference settings successfully retrieved."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Insufficient privileges to retrieve the requested setting."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "ini", produces = MediaType.TEXT_PLAIN_VALUE, method = RequestMethod.GET, restrictTo = Admin)
    public ResponseEntity<String> getPreferenceSettingsInis() {
        _log.info("User {} requested all system preference settings in ini format.", getSessionUser().getUsername());
        final Map<String, Properties> beanProperties = Maps.transformEntries(_preferences, new Maps.EntryTransformer<String, AbstractPreferenceBean, Properties>() {
            @Override
            public Properties transformEntry(@Nullable final String key, @Nullable final AbstractPreferenceBean value) {
                return value != null ? value.asProperties() : EMPTY_PROPERTIES;
            }
        });

        try (final StringWriter stringWriter = new StringWriter(); final PrintWriter writer = new PrintWriter(stringWriter)) {
            for (final String toolId : beanProperties.keySet()) {
                writer.println("[" + toolId + "]");
                beanProperties.get(toolId).store(writer, "Settings for tool " + toolId);
                writer.println();
            }
            return new ResponseEntity<>(stringWriter.getBuffer().toString(), HttpStatus.OK);
        } catch (IOException e) {
            _log.error("An error occurred trying to write the preferences in ini format", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Returns the full map of preferences and values for this XNAT application.", response = String.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Preference settings successfully retrieved."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Insufficient privileges to retrieve the requested setting."),
                   @ApiResponse(code = 404, message = "Tool ID not found in the system."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "ini/{toolId}", produces = MediaType.TEXT_PLAIN_VALUE, method = RequestMethod.GET, restrictTo = Admin)
    public ResponseEntity<String> getPreferenceSettingsIni(@PathVariable final String toolId) throws NotFoundException {
        if (!_preferences.containsKey(toolId)) {
            throw new NotFoundException("There is no tool with ID " + toolId + " in this system.");
        }

        _log.info("User {} requested the system preference settings for tool ID {} in ini format.", getSessionUser().getUsername(), toolId);
        final Properties properties = _preferences.get(toolId).asProperties();

        try (final StringWriter stringWriter = new StringWriter(); final PrintWriter writer = new PrintWriter(stringWriter)) {
            writer.println("[" + toolId + "]");
            properties.store(writer, "Settings for tool " + toolId);
            writer.println();
            return new ResponseEntity<>(stringWriter.getBuffer().toString(), HttpStatus.OK);
        } catch (IOException e) {
            _log.error("An error occurred trying to write the preferences in ini format", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static final Logger     _log             = LoggerFactory.getLogger(PreferencesApi.class);
    private static final Properties EMPTY_PROPERTIES = new Properties();

    private final Map<String, AbstractPreferenceBean> _preferences = Maps.newHashMap();
}
