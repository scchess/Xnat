package org.nrg.xapi.rest.dicomweb;

import org.dcm4che2.data.Tag;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.io.DicomInputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

public class DicomDataSummary {

    private Entries entries = new Entries();

    public static void main( String[] args) {
        File dir = new File( args[0]);
        if( dir.exists() && dir.isDirectory()) {
            try {
                DicomDataSummary summary = new DicomDataSummary();
                summary.doit(dir);
                summary.dump( System.out);

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void doit( File file) throws IOException {
        process( file);
        entries.updateStudyTotals();
    }

    public void process( File file) throws IOException {
        if( file.isDirectory()) {
            for( File f: file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return !name.equals(".DS_Store");
                }
            })) {
                process(f);
            }
        }
        else {
            processFile(file);
        }
    }

    public void processFile( File f) throws IOException {
        Entry newEntry = new Entry( f);
        entries.record( newEntry);

    }

    public class Entry {
        String studyDate;
        String studyTime;
        String patientName;
        String patientID;
        String studyInstanceUID;
        String studyID;
        String modalitiesInStudy;
        String accessionNumber;
        int numStudyRelatedSeries;
        int numStudyRelatedInstances;
        String referringPhysicianName;
        String modality;
        String seriesDescription;
        String seriesInstanceUID;
        String seriesNumber;
        int numSeriesRelatedInstances;
        String rows;
        String columns;
        String bitsAllocated;
        String numFrame;

        public Entry( File file) throws IOException {
            Attributes attributes = new DicomInputStream( file).readDataset(-1, -1);
            studyDate = attributes.getString(Tag.StudyDate);
            studyTime = attributes.getString(Tag.StudyTime);
            patientName = attributes.getString(Tag.PatientName);
            patientID = attributes.getString(Tag.PatientID);
            studyInstanceUID = attributes.getString(Tag.StudyInstanceUID);
            studyID = attributes.getString(Tag.StudyID);
            modalitiesInStudy = attributes.getString(Tag.ModalitiesInStudy);
            accessionNumber = attributes.getString(Tag.AccessionNumber);
            numStudyRelatedInstances = 0;
            numStudyRelatedSeries = 0;
            referringPhysicianName = attributes.getString(Tag.ReferringPhysicianName);
            modality = attributes.getString(Tag.Modality);
            seriesDescription = attributes.getString(Tag.SeriesDescription);
            seriesInstanceUID = attributes.getString(Tag.SeriesInstanceUID);
            seriesNumber = attributes.getString(Tag.SeriesNumber);
            numSeriesRelatedInstances = 1;
            rows = attributes.getString(Tag.Rows);
            columns = attributes.getString(Tag.Columns);
            bitsAllocated = attributes.getString(Tag.BitsAllocated);
            numFrame = attributes.getString(Tag.NumberOfFrames);
        }

        public boolean matches(Entry e) {
            return studyInstanceUID != null && seriesInstanceUID != null && studyInstanceUID.equals( e.studyInstanceUID) && seriesInstanceUID.equals(e.seriesInstanceUID);
        }

        public void update( Entry e) {
            numSeriesRelatedInstances++;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append( studyDate);
            sb.append("\t").append( studyTime);
            sb.append("\t").append( patientName);
            sb.append("\t").append( patientID);
            sb.append("\t").append( studyInstanceUID);
            sb.append("\t").append( studyID);
            sb.append("\t").append( modalitiesInStudy);
            sb.append("\t").append( accessionNumber);
            sb.append("\t").append( numStudyRelatedSeries);
            sb.append("\t").append( numStudyRelatedInstances);
            sb.append("\t").append( referringPhysicianName);
            sb.append("\t").append( modality);
            sb.append("\t").append( seriesDescription);
            sb.append("\t").append( seriesInstanceUID);
            sb.append("\t").append( seriesNumber);
            sb.append("\t").append( numSeriesRelatedInstances);
            sb.append("\t").append( rows);
            sb.append("\t").append( columns);
            sb.append("\t").append( bitsAllocated);
            sb.append("\t").append( numFrame);

            return sb.toString();
        }

    }

    public class Entries extends ArrayList<Entry> {

        public void record( Entry newEntry) {
            if( this.isEmpty()) {
                this.add( newEntry);
            }
            else {
                Entry match = getMatch( newEntry);
                if( match == null) {
                    this.add( newEntry);
                }
                else {
                    match.update( newEntry);
                }
            }
        }

        public Entry getMatch( Entry entry) {
            Entry match = null;
            for( Entry e: this) {
                if( e.matches( entry)) {
                    match = e;
                    break;
                }
            }
            return match;
        }

        public void updateStudyTotals() {
            for( Entry e1: this) {
                for( Entry e2: this) {
                    if( e1.studyInstanceUID.equals( e2.studyInstanceUID)) {
                        e1.numStudyRelatedSeries++;
                        e1.numStudyRelatedInstances += e2.numSeriesRelatedInstances;
                    }
                }

            }
        }

    }

    public void dump(PrintStream os) {
        for( Entry e: entries) {
            os.println( e);
        }
    }
}
