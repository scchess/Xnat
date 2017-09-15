/*
 * web: org.nrg.xapi.rest.data.InvestigatorsApi
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xapi.rest.data;

import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NoContentException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.model.investigators.Investigator;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.model.CatCatalogI;
import org.nrg.xdat.om.XnatInvestigatordata;
import org.nrg.xdat.security.Authorizer;
import org.nrg.xdat.security.XdatStoredSearch;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.XFTItem;
import org.nrg.xft.collections.ItemCollection;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.FieldNotFoundException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.schema.Wrappers.XMLWrapper.SAXReader;
import org.nrg.xft.schema.Wrappers.XMLWrapper.SAXWriter;
import org.nrg.xft.search.ItemSearch;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.services.investigators.InvestigatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

@Api(description = "XNAT Custom Variables API")
@XapiRestController
@RequestMapping(value = "/customvariables")
public class CustomVariablesApi extends AbstractXapiRestController {
    @Autowired
    public CustomVariablesApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final InvestigatorService service) {
        super(userManagementService, roleHolder);
        _service = service;
    }

    @ApiOperation(value = "Retrieves the custom variable field definition set.",
            notes = "This retrieves an XML file with the field definition set for the supplied custom variables.",
            response = String.class)
    @ApiResponses({@ApiResponse(code = 200, message = "The custom variable field definition set was successfully built."),
            @ApiResponse(code = 204, message = "No resources were specified."),
            @ApiResponse(code = 400, message = "Something is wrong with the request format."),
            @ApiResponse(code = 403, message = "The user is not authorized to access one or more of the specified resources."),
            @ApiResponse(code = 404, message = "The request was valid but one or more of the specified resources was not found."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "fieldset", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.TEXT_PLAIN_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> getFieldSet(@ApiParam("Whether multiples are allowed.") @PathVariable final @RequestParam(required = false, defaultValue = "true") boolean allowMultiples,
                                                   @ApiParam("The XML of the existing custom variables.") @RequestBody String xmlString) throws InsufficientPrivilegesException, NoContentException, NotFoundException {

        boolean allowChildren = allowMultiples;
        UserI user = XDAT.getUserDetails();
        if (user != null) {
            try (final StringWriter stringWriter = new StringWriter()) {
                StringReader sr = new StringReader(xmlString);
                InputSource is = new InputSource(sr);
                boolean successful = false;
                SAXReader reader = new SAXReader(user);
                try {

                    XFTItem item = reader.parse(is);

                    XdatStoredSearch xss = new XdatStoredSearch(item);
                    ItemSearch search = xss.getItemSearch(user);

                    ItemCollection items = search.exec(allowChildren);
                    if (items.size() > 1 || items.size() == 0) {
                        stringWriter.write("<matchingResults xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
                        Iterator iter = items.iterator();
                        while (iter.hasNext()) {
                            XFTItem next = (XFTItem) iter.next();
                            try {
                                Authorizer.getInstance().authorizeRead(next, user);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                stringWriter.write("<matchingResult>");

                                try {
                                    SAXWriter writer = new SAXWriter(baos, false);
                                    writer.setWriteHiddenFields(true);
                                    writer.write(next);
                                } catch (TransformerConfigurationException e) {
                                    _log.error("", e);
                                } catch (TransformerFactoryConfigurationError e) {
                                    _log.error("", e);
                                } catch (FieldNotFoundException e) {
                                    _log.error("", e);
                                }
                                stringWriter.write(baos.toString());
                                stringWriter.flush();

                                stringWriter.write("</matchingResult>");
                            } catch (Exception e) {

                            }
                        }
                        stringWriter.write("</matchingResults>");
                    } else {
                        XFTItem next = (XFTItem) items.first();
                        Authorizer.getInstance().authorizeRead(next, user);
                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            SAXWriter writer = new SAXWriter(baos, false);
                            writer.setWriteHiddenFields(true);

                            writer.write(next);
                            stringWriter.write(baos.toString());

                        } catch (TransformerConfigurationException e) {
                            _log.error("", e);
                        } catch (TransformerFactoryConfigurationError e) {
                            _log.error("", e);
                        } catch (FieldNotFoundException e) {
                            _log.error("", e);
                        }
                    }

                } catch (SAXException e) {
                    _log.error("", e);
                } catch (XFTInitException e) {
                    _log.error("", e);
                } catch (ElementNotFoundException e) {
                    _log.error("", e);
                } catch (FieldNotFoundException e) {
                    _log.error("", e);
                } catch (Exception e) {
                    _log.error("", e);
                }
                return new ResponseEntity<>(stringWriter.getBuffer().toString(), HttpStatus.OK);
            } catch (IOException e) {
                _log.error("An error occurred trying to write the preferences in ini format", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static final Logger _log = LoggerFactory.getLogger(CustomVariablesApi.class);

    private final InvestigatorService _service;
}
