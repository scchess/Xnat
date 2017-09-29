/*
 * web: org.nrg.xapi.rest.data.InvestigatorsApi
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xapi.rest.data;

import com.google.common.collect.Maps;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ActionException;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.nrg.framework.utilities.Reflection;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NoContentException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.model.investigators.Investigator;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.ProjectId;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.bean.XnatFielddefinitiongroupBean;
import org.nrg.xdat.model.CatCatalogI;
import org.nrg.xdat.model.XnatFielddefinitiongroupI;
import org.nrg.xdat.om.XnatAbstractprotocol;
import org.nrg.xdat.om.XnatDatatypeprotocol;
import org.nrg.xdat.om.XnatInvestigatordata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.Authorizer;
import org.nrg.xdat.security.ElementSecurity;
import org.nrg.xdat.security.XdatStoredSearch;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.XFTItem;
import org.nrg.xft.XFTTable;
import org.nrg.xft.collections.ItemCollection;
import org.nrg.xft.db.MaterializedView;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.exception.DBPoolException;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.FieldNotFoundException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.presentation.FlattenedItemA;
import org.nrg.xft.presentation.ItemJSONBuilder;
import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperElement;
import org.nrg.xft.schema.Wrappers.XMLWrapper.SAXReader;
import org.nrg.xft.schema.Wrappers.XMLWrapper.SAXWriter;
import org.nrg.xft.search.ItemSearch;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.restlet.representations.ItemHTMLRepresentation;
import org.nrg.xnat.restlet.representations.ItemXMLRepresentation;
import org.nrg.xnat.restlet.representations.JSONObjectRepresentation;
import org.nrg.xnat.restlet.resources.SecureResource;
import org.nrg.xnat.services.investigators.InvestigatorService;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
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
import org.xml.sax.SAXParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

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
    public ResponseEntity<String> getFieldSet(@ApiParam("Whether multiples are allowed.") final @RequestParam(required = false, defaultValue = "true") boolean allowMultiples,
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
                _log.error("An error occurred trying to write the field set", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiOperation(value = "Retrieves the custom variable field definition groups.",
            notes = "This retrieves an XML file with the field definition groups for all custom variables.",
            response = String.class)
    @ApiResponses({@ApiResponse(code = 200, message = "The custom variable field definition groups xml successfully built."),
            @ApiResponse(code = 400, message = "Something is wrong with the request format."),
            @ApiResponse(code = 404, message = "The request was valid but nothing was found."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "fieldDefinitionGroups", produces = MediaType.TEXT_PLAIN_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getFieldDefinitionGroups() throws InsufficientPrivilegesException, NoContentException, NotFoundException {

        UserI user = XDAT.getUserDetails();
        if (user != null) {
            try (final StringWriter stringWriter = new StringWriter()) {
                try{
                    String query = "SELECT id,data_type,description, xnat_fielddefinitiongroup_id FROM xnat_fielddefinitiongroup;";
                    XFTTable table = XFTTable.Execute(query, user.getDBName(), user.getLogin());
                    stringWriter.write("<fieldDefinitionGroups>");
                    table.resetRowCursor();
                    while (table.hasMoreRows()) {
                        Object[] row = table.nextRow();
                        stringWriter.write("<fieldDefinitionGroup data-type=\"" + row[1] + "\" id=\"" + row[3] + "\" name=\"" + row[0] + "\" description=\"" + row[2] + "\"/>");
                    }
                    stringWriter.write("</fieldDefinitionGroups>");
                } catch (SQLException e) {
                    _log.error("", e);
                } catch (DBPoolException e) {
                    _log.error("", e);
                }
                return new ResponseEntity<>(stringWriter.getBuffer().toString(), HttpStatus.OK);
            } catch (IOException e) {
                _log.error("An error occurred trying to write the preferences field definition groups", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
    @XapiRequestMapping(value = "fieldset", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.TEXT_PLAIN_VALUE, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> addCustomVariable(@ApiParam("Whether multiples are allowed.") final @RequestParam(required = false, defaultValue = "true") boolean allowMultiples,
                                                    @ApiParam("Datatype associated with the custom variable to add.") @RequestParam final String dataType,
                                                    @ApiParam("Custom variable project.") @RequestParam @ProjectId final String projectId,
                                                    @ApiParam("Custom variable protocol id.") @RequestParam final String protID,
                                              @ApiParam("The XML of the new custom variable.") @RequestBody String xmlString) throws InsufficientPrivilegesException, NoContentException, NotFoundException {
        final UserI user = XDAT.getUserDetails();
        XnatProjectdata proj = null;
        XnatDatatypeprotocol existing = null;
        if (projectId != null) {
            proj = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
        }
        XFTItem item = null;
        if (user!=null){
            StringReader sr = new StringReader(xmlString);
            InputSource is = new InputSource(sr);

            SAXReader reader = new SAXReader(user);
            try {

                item = reader.parse(is);

                SaveItemHelper.unauthorizedSave(item, user, false, true,EventUtils.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, EventUtils.TYPE.SOAP, "Stored XML", EventUtils.EVENT_ACTION, EventUtils.EVENT_COMMENT));

            } catch (Exception e) {
                _log.error("",e);
            }

        }

        if (proj != null) {
            existing = (XnatDatatypeprotocol) XnatAbstractprotocol
                    .getXnatAbstractprotocolsById(protID, user, true);
        }
        if(dataType!=null){
            XnatDatatypeprotocol temp = (XnatDatatypeprotocol)proj.getProtocolByDataType(dataType);

            try {
                ElementSecurity ess = ElementSecurity.GetElementSecurity(dataType);

                if(temp==null && ess!=null){
                    GenericWrapperElement e= GenericWrapperElement.GetElement(dataType);
                    temp=new XnatDatatypeprotocol(user);
                    temp.setProperty("xnat_projectdata_id", proj.getId());
                    temp.setDataType(e.getXSIType());

                    temp.setId(proj.getId() + "_" + e.getSQLName());
                    if (temp.getProperty("name")==null){
                        temp.setProperty("name",ess.getPluralDescription());
                    }

                    if(temp.getXSIType().equals("xnat:datatypeProtocol")){
                        temp.setProperty("xnat:datatypeProtocol/definitions/definition[ID=default]/data-type", temp.getProperty("data-type"));
                        temp.setProperty("xnat:datatypeProtocol/definitions/definition[ID=default]/project-specific", "false");
                    }
                    PersistentWorkflowI wrk=PersistentWorkflowUtils.getOrCreateWorkflowData(null, user, proj.getItem(), EventUtils.newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, EventUtils.TYPE.STORE_XML, "Modified event data-type protocol."));
                    try {
                        SaveItemHelper.authorizedSave(temp,user, false, false,wrk.buildEvent());
                        PersistentWorkflowUtils.complete(wrk,wrk.buildEvent());
                    } catch (Exception e1) {
                        PersistentWorkflowUtils.fail(wrk,wrk.buildEvent());
                        throw e1;
                    }

                    StringReader sr = new StringReader(xmlString);
                    InputSource is = new InputSource(sr);
                    SAXReader reader = new SAXReader(user);
                    XFTItem itemToAdd = reader.parse(xmlString);
                    temp.setDefinitions_definition(itemToAdd);
                }
            } catch (Exception e) {
                _log.error("", e);
            }

            if(temp==null){
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }else if(item!=null){
                try{
                    temp.addDefinitions_definition((XnatFielddefinitiongroupI) org.nrg.xdat.base.BaseElement.WrapItems(existing.getChildItems("definitions/definition")));
                }
                catch(Exception e){
                    _log.error("",e);
                }

               // existing.addDefinitions_definition(new XnatFielddefinitiongroupBean(temp.getItem()));
                try (final StringWriter stringWriter = new StringWriter()) {
                    stringWriter.write(temp.getItem().toXML_String());
                    return new ResponseEntity<>(stringWriter.getBuffer().toString(), HttpStatus.OK);
                }
                catch (Exception e){
                    _log.error("",e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }else{
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static Map<String, SecureResource.ItemHandlerI> itemHandlers = null;

    private static Representation representItem(XFTItem item, org.restlet.data.MediaType mt) {
        /* Load any classes we have placed in org.nrg.xnat.restlet.representations.item.extensions, and register them as "handlers".
         * If the REST request that returns an object specifies a handler as a query param ("handler=foo") then pass the
         * object and request to whatever item handler class has registered the name "foo".
        */
        if (itemHandlers == null) {
            itemHandlers = Maps.newConcurrentMap();

            try {
                List<Class<?>> handlerClasses = Reflection.getClassesForPackage("org.nrg.xnat.restlet.representations.item.extensions");

                for (Class handler : handlerClasses) {
                    if (SecureResource.ItemHandlerI.class.isAssignableFrom(handler)) {
                        SecureResource.ItemHandlerI instance = (SecureResource.ItemHandlerI) handler.newInstance();
                        itemHandlers.put(instance.getHandlerString(), instance);
                    }
                }
            } catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
                _log.error("", e);
            }
        }
        return new ItemXMLRepresentation(item, org.restlet.data.MediaType.TEXT_XML, true, true);
    }

    private static final Logger _log = LoggerFactory.getLogger(CustomVariablesApi.class);

    private final InvestigatorService _service;
}
