package nl.utwente.sysml2petrinet;

import com.google.inject.Injector;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;
import org.omg.sysml.lang.sysml.*;
import org.omg.sysml.xtext.SysMLStandaloneSetup;
import org.omg.kerml.xtext.KerMLStandaloneSetup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

public class SysMLProcessor {
    private static final Logger logger = LogManager.getLogger(SysMLProcessor.class);
    private final ResourceSet resourceSet;
    private final String modelDir;

    /**
     * A class to initialise sysml resources and resolve all proxies for derived attribute in the model
     *
     * @param modelDir Directory of the model
     */
    public SysMLProcessor(String modelDir) {
        this.modelDir = modelDir;
        KerMLStandaloneSetup.doSetup();
        SysMLPackage.eINSTANCE.eClass();
        Injector injector = new SysMLStandaloneSetup().createInjectorAndDoEMFRegistration();
        this.resourceSet = injector.getInstance(XtextResourceSet.class);
        loadSysMLFiles();
    }

    /**
     * Recursively find all .sysml files in a given folder
     *
     * @param dir Directory that contains .sysml file
     * @return list of File object
     */
    public List<File> listSysmlFiles(File dir, boolean recursive) {
        List<File> result = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && recursive) {
                    result.addAll(listSysmlFiles(file, true));
                } else if (file.getName().endsWith(".sysml")) {
                    result.add(file);
                }
            }
        }
        return result;
    }

    /**
     * Load sysml library files and resolve all proxies
     */
    private void loadSysMLFiles() {
        try {
            List<File> allSysmlFiles = listSysmlFiles(new File(modelDir), true);
            logger.info("Found {} sysml library files", allSysmlFiles.size());

            for (File f : allSysmlFiles) {
                resourceSet.getResource(URI.createFileURI(f.getAbsolutePath()), true);
            }
            logger.info("Loaded {} sysml library files", allSysmlFiles.size());
//            EcoreUtil.resolveAll(resourceSet);
            logger.info("All proxies processed.");
        } catch (Exception e) {
            logger.error("Error loading SysML files", e);
        }
    }

    /**
     * Given a .sysml model and return root element
     *
     * @param filePath .sysml model
     * @return root element of the model
     */
    public Namespace processSysMLFile(String filePath) {
        try {
            // Check if file exists
            File file = new File(filePath);
            if (file.exists()) {
                logger.info("File exists: {}", file.getAbsolutePath());
            } else {
                logger.info("File does NOT exist: {}", file.getAbsolutePath());
                return null;
            }

            Resource resource = resourceSet.getResource(URI.createFileURI(filePath), true);
            // Check for errors and contents
            if (resource == null || resource.getContents().isEmpty()) {
                logger.error("Failed to load resource or resource is empty.");
                return null;
            }
//            EcoreUtil.resolveAll(resourceSet);
//            IResourceServiceProvider serviceProvider = IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(resource.getURI());
//            IResourceValidator validator = serviceProvider.get(IResourceValidator.class);
//            List<Issue> issues = validator.validate(resource, CheckMode.NORMAL_AND_FAST, CancelIndicator.NullImpl);
//
//            for (Issue issue : issues) {
//                if (issue.getSeverity() == Severity.WARNING) {
//                    logger.error("Warning: {}", issue.getMessage());
//                    throw new Exception("Input model has warning(s)");
//                }
//            }
            if (!resource.getErrors().isEmpty() || !resource.getWarnings().isEmpty()) {
                logger.error("Input model errors:");
                resource.getErrors().forEach(err -> logger.error("  {}", err));
                throw new Exception("Input model has errors");
            }

            return (Namespace) resource.getContents().getFirst();
        } catch (Exception e) {
            logger.error("Error processing SysML file", e);
            return null;
        }
    }


} 