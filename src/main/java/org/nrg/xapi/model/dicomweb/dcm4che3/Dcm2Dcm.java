/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/gunterze/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.nrg.xapi.model.dicomweb.dcm4che3;

import org.dcm4che3.data.*;
import org.dcm4che3.imageio.codec.Compressor;
import org.dcm4che3.imageio.codec.Decompressor;
import org.dcm4che3.imageio.codec.TransferSyntaxType;
import org.dcm4che3.io.DicomEncodingOptions;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomInputStream.IncludeBulkData;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.Property;
import org.dcm4che3.util.SafeClose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapted from the dcm4che5 tool to be free of the command line.
 *
 * @author Gunter Zeilinger <gunterze@gmail.com>
 */
public class Dcm2Dcm {

    private String tsuid;
    private TransferSyntaxType tstype;
    private boolean retainfmi;
    private boolean nofmi;
    private DicomEncodingOptions encOpts = DicomEncodingOptions.DEFAULT;
    private final List<Property> params = new ArrayList<Property>();

    public final void setTransferSyntax(String uid) {
        this.tsuid = uid;
        this.tstype = TransferSyntaxType.forUID(uid);
        if (tstype == null) {
            throw new IllegalArgumentException( "Unsupported Transfer Syntax: " + tsuid);
        }
    }

    public final void setRetainFileMetaInformation(boolean retainfmi) {
        this.retainfmi = retainfmi;
    }

    public final void setWithoutFileMetaInformation(boolean nofmi) {
        this.nofmi = nofmi;
    }

    public final void setEncodingOptions(DicomEncodingOptions encOpts) {
        this.encOpts = encOpts;
    }

    public void addCompressionParam(String name, Object value) {
        params.add(new Property(name, value));
    }

    public void transcode(DicomInputStream dis, DicomOutputStream dos) throws IOException {
        Attributes fmi;
        Attributes dataset;
        try {
            dis.setIncludeBulkData(IncludeBulkData.URI);
            fmi = dis.readFileMetaInformation();
            dataset = dis.readDataset(-1, -1);
        } finally {
            dis.close();
        }
        Object pixeldata = dataset.getValue(Tag.PixelData);
        Compressor compressor = null;
        try {
            String tsuid = this.tsuid;
            if (pixeldata != null) {
                if (tstype.isPixeldataEncapsulated()) {
                    tsuid = adjustTransferSyntax(tsuid,
                            dataset.getInt(Tag.BitsStored, 8));
                    compressor = new Compressor(dataset, dis.getTransferSyntax(),
                            tsuid, params.toArray(new Property[params.size()]));
                    compressor.compress();
                } else if (pixeldata instanceof Fragments)
                    Decompressor.decompress(dataset, dis.getTransferSyntax());
            }
            if (nofmi)
                fmi = null;
            else if (retainfmi && fmi != null)
                fmi.setString(Tag.TransferSyntaxUID, VR.UI, tsuid);
            else
                fmi = dataset.createFileMetaInformation(tsuid);
            dos.setEncodingOptions(encOpts);
            dos.writeDataset(fmi, dataset);
        } finally {
            SafeClose.close(compressor);
            SafeClose.close(dos);
        }
    }

    private String adjustTransferSyntax(String tsuid, int bitsStored) {
        switch (tstype) {
        case JPEG_BASELINE:
            if (bitsStored > 8)
                return UID.JPEGExtended24;
            break;
        case JPEG_EXTENDED:
            if (bitsStored <= 8)
                return UID.JPEGBaseline1;
            break;
        default:
        }
        return tsuid;
    }

    public boolean isSupportedTransferSyntax(String tsuid) {
//        return ! (TransferSyntaxType.forUID(tsuid) == null);
        if( tsuid == null) return false;
        switch( tsuid) {
            case "1.2.840.10008.1.2.1":
            case "1.2.840.10008.1.2":
//            case "1.2.840.10008.1.2.4.50":
                return true;
            default:
                return false;
        }
    }
}
