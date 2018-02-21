package org.nrg.xnat.archive.operations;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.dcm4che2.io.DicomInputStream;
import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.dcm.DicomFileNamer;
import org.nrg.dicom.mizer.service.MizerService;
import org.nrg.dicomtools.filters.DicomFilterService;
import org.nrg.framework.status.StatusProducer;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.exception.InvalidPermissionException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.DicomObjectIdentifier;
import org.nrg.xnat.archive.FinishImageUpload;
import org.nrg.xnat.archive.processors.ArchiveProcessor;
import org.nrg.xnat.helpers.PrearcImporterHelper;
import org.nrg.xnat.helpers.ZipEntryFileWriterWrapper;
import org.nrg.xnat.helpers.merge.SiteWideAnonymizer;
import org.nrg.xnat.helpers.prearchive.PrearcDatabase;
import org.nrg.xnat.helpers.prearchive.PrearcUtils;
import org.nrg.xnat.helpers.prearchive.SessionData;
import org.nrg.xnat.helpers.prearchive.SessionException;
import org.nrg.xnat.helpers.uri.URIManager;
import org.nrg.xnat.helpers.uri.UriParserUtils;
import org.nrg.xnat.restlet.actions.PrearcImporterA;
import org.nrg.xnat.restlet.actions.SessionImporter;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.nrg.xnat.restlet.util.RequestUtil;
import org.nrg.xnat.services.messaging.prearchive.PrearchiveOperationRequest;
import org.nrg.xnat.status.ListenerUtils;
import org.nrg.xnat.turbine.utils.XNATSessionPopulater;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SessionImportOperation extends AbstractDicomImportOperation {
    private static final Logger logger = LoggerFactory.getLogger(SessionImportOperation.class);

    private boolean overrideExceptions;
    private boolean allowSessionMerge;
    private Object control;

    public SessionImportOperation(final Object control, final UserI user, final FileWriterWrapperI fileWriter, final Map<String, Object> parameters, final List<ArchiveProcessor> processors, final DicomFilterService filterService, final DicomObjectIdentifier<XnatProjectdata> identifier, final MizerService mizer, final DicomFileNamer namer) {
        super(control, user, parameters, fileWriter, identifier, namer, mizer, filterService, processors);
        this.control = control;
        String overwriteV=(String)parameters.remove("overwrite");

        overrideExceptions = false;
        allowSessionMerge = false;
        if(overwriteV!=null){
            if(overwriteV.equalsIgnoreCase(PrearcUtils.APPEND)){
                overrideExceptions=false;
                allowSessionMerge=true;
            }else if(overwriteV.equalsIgnoreCase(PrearcUtils.DELETE)){//leaving this for backwards compatibility... deprecated by 'override' setting
                overrideExceptions=true;
                allowSessionMerge=true;
            }else if(overwriteV.equalsIgnoreCase("override")){
                overrideExceptions=true;
                allowSessionMerge=true;
            } else{
                overrideExceptions=false;
                allowSessionMerge=true;
            }
        }
    }

    public static List<PrearcImporterA.PrearcSession> importToPrearc(StatusProducer parent, String format, Object listener, UserI user, FileWriterWrapperI fw, Map<String,Object> params, boolean allowSessionMerge, boolean overwriteFiles) throws ActionException{
        //write file
        try {
            final PrearcImporterA destination = PrearcImporterA.buildImporter(format, listener, user, fw, params, allowSessionMerge, overwriteFiles);
            final PrearcImporterA listeners = ListenerUtils.addListeners(parent, destination);
            return listeners.call();
        } catch (SecurityException e) {
            throw new ServerException(e.getMessage(),e);
        } catch (IllegalArgumentException e) {
            throw new ClientException(Status.CLIENT_ERROR_BAD_REQUEST,e.getMessage(),e);
        } catch (NoSuchMethodException e) {
            throw new ClientException(Status.CLIENT_ERROR_BAD_REQUEST,e.getMessage(),e);
        } catch (InstantiationException e) {
            throw new ServerException(e.getMessage(),e);
        } catch (IllegalAccessException e) {
            throw new ServerException(e.getMessage(),e);
        } catch (InvocationTargetException e) {
            throw new ServerException(e.getMessage(),e);
        } catch (PrearcImporterA.UnknownPrearcImporterException e) {
            throw new ClientException(Status.CLIENT_ERROR_NOT_FOUND,e.getMessage(),e);
        }
    }

    public static XnatImagesessiondata getExperimentByIdorLabel(final String project, final String expt_id, final UserI user){
        XnatImagesessiondata expt=null;
        if(!StringUtils.isEmpty(project)){
            expt=(XnatImagesessiondata) XnatExperimentdata.GetExptByProjectIdentifier(project, expt_id, user, false);
        }

        if(expt==null){
            expt=(XnatImagesessiondata)XnatExperimentdata.getXnatExperimentdatasById(expt_id, user, false);
        }
        return expt;
    }

    @Override
    public List<String> call() throws Exception {
        try {
            String dest =(String)getParameters().get(RequestUtil.DEST);

            XnatImagesessiondata expt=null;

            final URIManager.DataURIA destination=(!StringUtils.isEmpty(dest))? UriParserUtils.parseURI(dest):null;

            String project=null;

            Map<String,Object> prearc_parameters= new HashMap<>(getParameters());

            //check for existing session by URI
            if(destination!=null){
                if(destination instanceof URIManager.PrearchiveURI){
                    prearc_parameters.putAll(destination.getProps());
                    String timezone=(String)getParameters().get("TIMEZONE");
                    if(!StringUtils.isEmpty(timezone)){
                        prearc_parameters.put("TIMEZONE", timezone);
                    }
                    String source=(String)getParameters().get("SOURCE");
                    if(!StringUtils.isEmpty(source)){
                        prearc_parameters.put("SOURCE", source);
                    }
                }else{
                    project= PrearcImporterHelper.identifyProject(destination.getProps());
                    if(!StringUtils.isEmpty(project)){
                        prearc_parameters.put("project", project);
                    }

                    if(destination.getProps().containsKey(URIManager.SUBJECT_ID)){
                        prearc_parameters.put("subject_ID", destination.getProps().get(URIManager.SUBJECT_ID));
                    }

                    String timezone=(String)getParameters().get("TIMEZONE");
                    if(!StringUtils.isEmpty(timezone)){
                        prearc_parameters.put("TIMEZONE", timezone);
                    }

                    String source=(String)getParameters().get("SOURCE");
                    if(!StringUtils.isEmpty(source)){
                        prearc_parameters.put("SOURCE", source);
                    }

                    String expt_id=(String)destination.getProps().get(URIManager.EXPT_ID);
                    if(!StringUtils.isEmpty(expt_id)){
                        expt=getExperimentByIdorLabel(project, expt_id,getUser());
                    }

                    if(expt==null){
                        if(!StringUtils.isEmpty(expt_id)){
                            prearc_parameters.put("label", expt_id);
                        }
                    }
                }
            }

            if(expt==null){
                if(StringUtils.isEmpty(project)){
                    project=PrearcImporterHelper.identifyProject(prearc_parameters);
                }

                //check for existing experiment by params
                if(prearc_parameters.containsKey(URIManager.SUBJECT_ID)){
                    prearc_parameters.put("xnat:subjectAssessorData/subject_ID", prearc_parameters.get(URIManager.SUBJECT_ID));
                }

                String expt_id=(String)prearc_parameters.get(URIManager.EXPT_ID);
                String expt_label=(String)prearc_parameters.get(URIManager.EXPT_LABEL);
                if(!StringUtils.isEmpty(expt_id)){
                    expt=getExperimentByIdorLabel(project, expt_id,getUser());
                }

                if(expt==null && !StringUtils.isEmpty(expt_label)){
                    expt=getExperimentByIdorLabel(project, expt_label,getUser());
                }

                if(expt==null){
                    if(!StringUtils.isEmpty(expt_label)){
                        prearc_parameters.put("xnat:experimentData/label", expt_label);
                    }else if(!StringUtils.isEmpty(expt_id)){
                        prearc_parameters.put("xnat:experimentData/label", expt_id);
                    }
                }
            }

            //set properties to match existing session
            if(expt!=null){
                prearc_parameters.put("xnat:experimentData/project", expt.getProject());
                if(!prearc_parameters.containsKey("xnat:subjectAssessorData/subject_ID")){
                    prearc_parameters.put("xnat:subjectAssessorData/subject_ID", expt.getSubjectId());
                }
                prearc_parameters.put("xnat:experimentData/label", expt.getLabel());
            }

            //import to prearchive, code allows for merging new files into a pre-existing session directory
            final List<PrearcImporterA.PrearcSession> sessions=importToPrearc(this,(String)getParameters().remove(PrearcImporterA.PREARC_IMPORTER_ATTR),control,getUser(),getFileWriter(),prearc_parameters,allowSessionMerge,overrideExceptions);

            if(sessions.size()==0){
                failed("Upload did not include parseable files for session generation.");
                throw new ClientException("Upload did not include parseable files for session generation.");
            }

            //if prearc=destination, then return
            if(destination!=null && destination instanceof URIManager.PrearchiveURI){
                this.completed("Successfully uploaded " + sessions.size() +" sessions to the prearchive.");
                resetStatus(sessions);
                return returnURLs(sessions);
            }


            //if unknown destination, only one session supported
            if(sessions.size()>1){
                resetStatus(sessions);
                failed("Upload included files for multiple imaging sessions.");
                throw new ClientException("Upload included files for multiple imaging sessions.");
            }

            final PrearcImporterA.PrearcSession session = sessions.get(0);
            session.getAdditionalValues().putAll(getParameters());

            try {
                final FinishImageUpload finisher= ListenerUtils.addListeners(this, new FinishImageUpload(control, getUser(), session,destination, overrideExceptions,allowSessionMerge,true));
                XnatImagesessiondata s = new XNATSessionPopulater(getUser(), session.getSessionDir(), session.getProject(), false).populate();
                SiteWideAnonymizer site_wide = new SiteWideAnonymizer(s, true);
                site_wide.call();
                if(finisher.isAutoArchive()){
                    final ArrayList<String> urls = new ArrayList<String>() {{
                        add(finisher.call());
                    }};
                    if (PrearcDatabase.setStatus(session.getFolderName(), session.getTimestamp(), session.getProject(), PrearcUtils.PrearcStatus.QUEUED_DELETING)) {
                        final SessionData sessionData = PrearcDatabase.getSession(session.getFolderName(), session.getTimestamp(), session.getProject());
                        final File sessionDir = session.getSessionDir();
                        XDAT.sendJmsRequest(new PrearchiveOperationRequest(getUser(), sessionData, sessionDir, "Delete"));
                    }
                    return urls;
                }else{
                    this.completed("Successfully uploaded " + sessions.size() +" sessions to the prearchive.");
                    resetStatus(sessions);
                    return returnURLs(sessions);
                }
            } catch (Exception e) {
                resetStatus(sessions);
                if(e instanceof ClientException && Status.CLIENT_ERROR_CONFLICT.equals(((ClientException)e).getStatus())){
                    //if this failed due to a conflict
                    PrearcDatabase.setStatus(session.getSessionDir().getName(), session.getTimestamp(), session.getProject(), PrearcUtils.PrearcStatus.CONFLICT);
                }else{
                    PrearcDatabase.setStatus(session.getSessionDir().getName(), session.getTimestamp(), session.getProject() , PrearcUtils.PrearcStatus.ERROR);
                }
                throw e;
            }

        } catch (ClientException | ServerException e) {
            this.failed(e.getMessage());
            throw e;
        } catch (IOException e) {
            logger.error("",e);
            this.failed(e.getMessage());
            throw new ServerException(e.getMessage(),e);
        } catch (SAXException e) {
            logger.error("",e);
            this.failed(e.getMessage());
            throw new ClientException(e.getMessage(),e);
        } catch (Throwable e) {
            logger.error("",e);
            throw new ServerException(e.getMessage(),new Exception());
        }
    }

    public List<String> returnURLs(final List<PrearcImporterA.PrearcSession> sessions)throws ActionException {
        final List<String> urls= new ArrayList<>();
        for(final PrearcImporterA.PrearcSession ps: sessions){
            urls.add(ps.getUrl());
        }
        return urls;
    }

    public void resetStatus(final List<PrearcImporterA.PrearcSession> sessions)throws ActionException{
        for(final PrearcImporterA.PrearcSession ps:sessions){

            try {
                Map<String,Object> session = PrearcUtils.parseURI(ps.getUrl());
                try {
                    PrearcUtils.addSession(getUser(), (String) session.get(URIManager.PROJECT_ID), (String) session.get(PrearcUtils.PREARC_TIMESTAMP), (String) session.get(PrearcUtils.PREARC_SESSION_FOLDER),true);
                } catch (SessionException e) {
                    PrearcUtils.resetStatus(getUser(), (String) session.get(URIManager.PROJECT_ID), (String) session.get(PrearcUtils.PREARC_TIMESTAMP), (String) session.get(PrearcUtils.PREARC_SESSION_FOLDER),true);
                }
            } catch (InvalidPermissionException e) {
                logger.error("",e);
                throw new ClientException(Status.CLIENT_ERROR_FORBIDDEN,e);
            } catch (Exception e) {
                logger.error("",e);
                throw new ServerException(e);
            }
        }
    }
}
