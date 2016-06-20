/*
 * org.nrg.xnat.security.provider.XnatAuthenticationProvider
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 7/10/13 9:04 PM
 */
package org.nrg.xnat.security.provider;

public class XnatAuthenticationProviderRepresentation {
    public XnatAuthenticationProviderRepresentation() {

    }

    public XnatAuthenticationProviderRepresentation(String _authName, String _authAddress, String _authType, int _authOrder, boolean _enabled) {
        this._authName = _authName;
        this._authAddress = _authAddress;
        this._authType = _authType;
        this._authOrder = _authOrder;
        this._enabled = _enabled;
    }

    public String getAuthName() {
        return _authName;
    }

    public void setAuthName(String _authName) {
        this._authName = _authName;
    }

    public String getAuthAddress() {
        return _authAddress;
    }

    public void setAuthAddress(String _authAddress) {
        this._authAddress = _authAddress;
    }

    public String getAuthType() {
        return _authType;
    }

    public void setAuthType(String _authType) {
        this._authType = _authType;
    }

    public int getAuthOrder() {
        return _authOrder;
    }

    public void setAuthOrder(int _authOrder) {
        this._authOrder = _authOrder;
    }

    public boolean isEnabled() {
        return _enabled;
    }

    public void setEnabled(boolean _enabled) {
        this._enabled = _enabled;
    }

    private String _authName;
    private String _authAddress;
    private String _authType;
    private int _authOrder;
    private boolean _enabled = true;
}
