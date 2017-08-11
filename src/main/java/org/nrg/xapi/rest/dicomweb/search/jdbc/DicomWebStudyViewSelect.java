package org.nrg.xapi.rest.dicomweb.search.jdbc;

import java.util.Arrays;

/**
 * Created by davidmaffitt on 8/9/17.
 */
public class DicomWebStudyViewSelect extends SqlSelectStatement {

    private final static String TABLE_NAME  = "dicomweb_study_view";

    private final static String SUBJECTID_COL_LABEL        = "sbj_id";
    private final static String SUBJECTLABEL_COL_LABEL     = "sbj_label";
    private final static String PROJECTID_COL_LABEL        = "prj_id";
    private final static String PROJECTNAME_COL_LABEL      = "prj_name";
    private final static String SESSIONID_COL_LABEL        = "session_id";
    private final static String STUDYDATE_COL_LABEL        = "studyDate";
    private final static String STUDYTIME_COL_LABEL        = "studyTime";
    private final static String ACCESSIONNUMBER_COL_LABEL  = "accessionNumber";
    private final static String MODALITIES_COL_LABEL       = "modalities";
    private final static String PATIENTNAME_COL_LABEL      = "patientName";
    private final static String PATIENTID_COL_LABEL        = "patientId";
    private final static String STUDYINSTANCEUID_COL_LABEL = "studyInstanceUID";

    public DicomWebStudyViewSelect() {
        super();
        columnNames = Arrays.asList(
                SUBJECTID_COL_LABEL,
                SUBJECTLABEL_COL_LABEL,
                PROJECTID_COL_LABEL,
                PROJECTNAME_COL_LABEL,
                SESSIONID_COL_LABEL,
                STUDYDATE_COL_LABEL,
                STUDYTIME_COL_LABEL,
                ACCESSIONNUMBER_COL_LABEL,
                MODALITIES_COL_LABEL,
                PATIENTNAME_COL_LABEL,
                PATIENTID_COL_LABEL,
                STUDYINSTANCEUID_COL_LABEL
        );
        tables = Arrays.asList(TABLE_NAME);
    }

    public static void main(String[] args)  {
        SqlSelectStatement statement = new DicomWebStudyViewSelect();
        statement.addOrClause( "foo", "bar");
        statement.addOrClause( "foo", "baz");

        System.out.println( statement.getStatement());
    }

    public String getPatientIDLabel() { return "patientName"; }

    public String getStudyInstanceUIDLabel() {
        return STUDYINSTANCEUID_COL_LABEL;
    }

    public String getAccessionNumberLabel() {
        return ACCESSIONNUMBER_COL_LABEL;
    }

    public String getModalitiesLabel() {
        return MODALITIES_COL_LABEL;
    }

    public String getPatientSexLabel() {
        return null;
    }

    public String getPatientNameLabel() {
        return "patientName";
    }
}
