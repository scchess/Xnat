package org.nrg.xapi.rest.auth;

import io.swagger.annotations.*;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.services.SerializerService;
import org.nrg.xapi.rest.NotFoundException;
import org.nrg.xdat.rest.AbstractXnatRestApi;
import org.nrg.xnat.security.XnatProviderManager;
import org.nrg.xnat.security.provider.XnatAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Api(description = "XNAT Auth Methods management API")
@XapiRestController
@RequestMapping(value = "/auth")
public class AuthMethodsApi extends AbstractXnatRestApi {
    private static final Logger _log = LoggerFactory.getLogger(AuthMethodsApi.class);

    @ApiOperation(value = "Get list of all configured authentication methods.", notes = "The primary authentication methods retrieval function returns a list of all authentication methods defined for the current system.", response = XnatAuthenticationProvider.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "A list of authentication method definitions."), @ApiResponse(code = 500, message = "Unexpected error")})
    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<XnatAuthenticationProvider>> authMethodsGet() {
        HttpStatus status = isPermitted();
        if (status != null) {
            return new ResponseEntity<>(status);
        }
        List<AuthenticationProvider> allProviders = _manager.getProviders();
        List<XnatAuthenticationProvider> xnatProviders = new ArrayList<>();
        for(AuthenticationProvider tempProv : allProviders){
            if (XnatAuthenticationProvider.class.isAssignableFrom(tempProv.getClass())){
                xnatProviders.add((XnatAuthenticationProvider)tempProv);
            }
        }
        return new ResponseEntity<>(xnatProviders, HttpStatus.OK);
    }

    @ApiOperation(value = "Gets the authentication method with the specified name.", notes = "Returns the authentication method with the specified name.", response = XnatAuthenticationProvider.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Authentication method successfully retrieved."), @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."), @ApiResponse(code = 403, message = "Not authorized to view this authentication method."), @ApiResponse(code = 404, message = "Authentication method not found."), @ApiResponse(code = 500, message = "Unexpected error")})
    @RequestMapping(value = {"/{name}"}, produces = {MediaType.APPLICATION_JSON_VALUE}, method = {RequestMethod.GET})
    public ResponseEntity<XnatAuthenticationProvider> authMethodGet(@ApiParam(value = "Name of the authentication method to fetch", required = true) @PathVariable("name") final String name) {
        HttpStatus status = isPermitted();
        if (status != null) {
            return new ResponseEntity<>(status);
        }
        return _manager.findAuthenticationProviderByProviderName(name)!=null ? new ResponseEntity<>(_manager.findAuthenticationProviderByProviderName(name), HttpStatus.OK)
                                        : new ResponseEntity<XnatAuthenticationProvider>(HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Creates or updates the authentication method with the specified name.", notes = "Returns the updated authentication method.", response = XnatAuthenticationProvider.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Authentication method successfully created or updated."), @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."), @ApiResponse(code = 403, message = "Not authorized to create or update this authentication method."), @ApiResponse(code = 404, message = "Authentication method not found."), @ApiResponse(code = 500, message = "Unexpected error")})
    @RequestMapping(value = {"/{name}"}, produces = {MediaType.APPLICATION_JSON_VALUE}, method = {RequestMethod.PUT})
    public ResponseEntity<XnatAuthenticationProvider> authMethodCreateOrUpdate(@ApiParam(value = "The name of the authentication method to create or update.", required = true) @PathVariable("name") final String name, @RequestBody final XnatAuthenticationProvider provider) throws NotFoundException {
        HttpStatus status = isPermitted();
        if (status != null) {
            return new ResponseEntity<>(status);
        }
        if (_manager.findAuthenticationProviderByProviderName(name)==null) {
            _manager.addProvider(provider);
        } else {
            _manager.updateProvider(provider);
        }
        return new ResponseEntity<>(provider, HttpStatus.OK);
    }

    @ApiOperation(value = "Deletes the authentication method with the specified name.", notes = "", response = Void.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Authentication method successfully created or updated."), @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."), @ApiResponse(code = 403, message = "Not authorized to delete this authentication method."), @ApiResponse(code = 404, message = "Authentication method not found."), @ApiResponse(code = 500, message = "Unexpected error")})
    @RequestMapping(value = {"/{name}"}, produces = {MediaType.APPLICATION_JSON_VALUE}, method = {RequestMethod.DELETE})
    public ResponseEntity<Void> authMethodDelete(@ApiParam(value = "The name of the auth method to delete.", required = true) @PathVariable("name") final String name) throws NotFoundException {
        HttpStatus status = isPermitted();
        if (status != null) {
            return new ResponseEntity<>(status);
        }
        XnatAuthenticationProvider provider = _manager.findAuthenticationProviderByProviderName(name);
        if (provider==null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        _manager.removeProvider(provider);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Inject
    private XnatProviderManager _manager;

    @Autowired
    @Lazy
    private SerializerService _serializer;
}
