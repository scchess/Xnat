/*
 * web: org.nrg.xnat.restlet.actions.importer.ImporterHandlerPackages
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.restlet.actions.importer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.nrg.framework.services.ContextService;
import org.nrg.framework.utilities.Reflection;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.turbine.utils.PropertiesHelper;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.nrg.xnat.restlet.actions.importer.ImporterHandlerA.DEFAULT_HANDLER;

@Component
@Slf4j
public class ImporterHandlerPackages extends HashMap<String, Class<? extends ImporterHandlerA>> {
    public ImporterHandlerPackages(final Set<String> packages, final List<ImporterHandlerA> importers) throws ConfigurationException, IOException, ClassNotFoundException {
        putAll((new PropertiesHelper<ImporterHandlerA>()).buildClassesFromProps(IMPORTER_PROPERTIES, PROP_OBJECT_IDENTIFIER, PROP_OBJECT_FIELDS, CLASS_NAME));

        for (final String pkg : packages) {
            final List<Class<?>> classesForPackage = Reflection.getClassesForPackage(pkg);
            for (final Class<?> clazz : classesForPackage) {
                if (ImporterHandlerA.class.isAssignableFrom(clazz)) {
                    if (!clazz.isAnnotationPresent(ImporterHandler.class)) {
                        continue;
                    }
                    final Class<? extends ImporterHandlerA> importHandlerClass = clazz.asSubclass(ImporterHandlerA.class);
                    final ImporterHandler                   annotation         = importHandlerClass.getAnnotation(ImporterHandler.class);
                    if (annotation != null && !containsKey(annotation.handler())) {
                        log.debug("Found ImporterHandler: " + importHandlerClass.getName());
                        put(annotation.handler(), importHandlerClass);
                    }
                }
            }
        }

        for (final ImporterHandlerA importer : importers) {
            final Class<? extends ImporterHandlerA> importHandlerClass = importer.getClass();
            final ImporterHandler                   annotation         = importHandlerClass.getAnnotation(ImporterHandler.class);
            if (annotation != null && !containsKey(annotation.handler())) {
                log.debug("Found ImporterHandler: " + importHandlerClass.getName());
                _importers.put(annotation.handler(), importer);
            }
        }
    }

    public ImporterHandlerA getImporter(final String handler) {
        return _importers.get(handler);
    }

    public ImporterHandlerA getImporter(final Object listener, final UserI user, final FileWriterWrapperI writer, final Map<String, Object> parameters) {
        return getImporter(DEFAULT_HANDLER, listener, user, writer, parameters);
    }

    public ImporterHandlerA getImporter(final String format, final Object listener, final UserI user, final FileWriterWrapperI writer, final Map<String, Object> parameters) {
        final Class<? extends ImporterHandlerA> clazz = get(format);
        //return _importers;
        return null;
    }

    private static final String   PROP_OBJECT_IDENTIFIER = "org.nrg.import.handler.impl";
    private static final String   IMPORTER_PROPERTIES    = "importer.properties";
    private static final String   CLASS_NAME             = "className";
    private static final String[] PROP_OBJECT_FIELDS     = new String[]{CLASS_NAME};

    private final Map<String, ImporterHandlerA> _importers = new HashMap<>();
}