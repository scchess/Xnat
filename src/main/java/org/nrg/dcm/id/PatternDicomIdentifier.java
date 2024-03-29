/*
 * web: org.nrg.dcm.id.PatternDicomIdentifier
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.dcm.id;

import com.google.common.base.Strings;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.util.TagUtils;
import org.nrg.dcm.MatchedPatternExtractor;
import org.nrg.framework.utilities.SortedSets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PatternDicomIdentifier implements DicomDerivedString {
    private final Logger logger = LoggerFactory.getLogger(MatchedPatternExtractor.class);
    private final int tag, group;
    private final Pattern pattern;

    public PatternDicomIdentifier(final int tag, final Pattern pattern, final int group) {
        this.tag = tag;
        this.pattern = pattern;
        this.group = group;
        if (logger.isTraceEnabled()) {
            logger.trace("initialized {}", getDescription());
        }
    }

    @SuppressWarnings("unused")
    public PatternDicomIdentifier(final int tag, final Pattern pattern) {
        this(tag, pattern, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String apply(final DicomObject dicomObject) {
        final String v = dicomObject.getString(tag);
        if (Strings.isNullOrEmpty(v)) {
            logger.trace("no match to {}: null or empty tag", this);
            return null;
        } else {
            final Matcher m = pattern.matcher(v);
            if (m.matches()) {
                logger.trace("input {} matched rule {}", v, this);
                return m.group(group);
            } else {
                logger.trace("input {} did not match rule {}", v, this);
                return null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedSet<Integer> getTags() {
        return SortedSets.singleton(tag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + ":" + getDescription();
    }

    private String getDescription() {
        return TagUtils.toString(tag) + "~" + pattern + "["+ group + "]";
    }
}
