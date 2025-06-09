package nl.utwente.sysml2petrinet;

import com.google.inject.Injector;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.xtext.SysMLStandaloneSetup;
import org.omg.sysml.lang.sysml.SysMLPackage;
import org.omg.kerml.xtext.KerMLStandaloneSetup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.Element;

import java.io.File;
import java.util.*;

public class SysMLProcessor {
    private static final Logger logger = LogManager.getLogger(SysMLProcessor.class);
    private final ResourceSet resourceSet;
    private final String modelDir;

    public SysMLProcessor(String modelDir) {
        this.modelDir = modelDir;
        KerMLStandaloneSetup.doSetup();
        SysMLStandaloneSetup.doSetup();
        SysMLPackage.eINSTANCE.eClass();
        Injector injector = new SysMLStandaloneSetup().createInjectorAndDoEMFRegistration();
        this.resourceSet = injector.getInstance(XtextResourceSet.class);
        loadSysMLFiles();
    }

    /**
     * Recursively find all .sysml files in a given folder
     * @param dir Directory that contains .sysml file
     * @return list of File object
     */
    private List<File> listSysmlFiles(File dir) {
        List<File> result = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    result.addAll(listSysmlFiles(file));
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
            List<File> allSysmlFiles = listSysmlFiles(new File(modelDir));
            logger.info("Found {} sysml library files", allSysmlFiles.size());

            for (File f : allSysmlFiles) {
                resourceSet.getResource(URI.createFileURI(f.getAbsolutePath()), true);
            }
            logger.info("Loaded {} sysml library files", allSysmlFiles.size());
            EcoreUtil.resolveAll(resourceSet);
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
            if (!resource.getErrors().isEmpty()) {
                logger.error("Resource has errors:");
                resource.getErrors().forEach(err -> logger.error("  {}", err));
            }
            if (!resource.getWarnings().isEmpty()) {
                logger.warn("Resource has warnings:");
                resource.getWarnings().forEach(warn -> logger.warn("  {}", warn));
            }

            return (Namespace) resource.getContents().getFirst();
        } catch (Exception e) {
            logger.error("Error processing SysML file", e);
            return null;
        }
    }

    /**
     * Collect nodes and transitions from the SysML model
     * @param model The root element of the SysML model
     * @return A map containing two lists: "nodes" and "transitions"
     */
    public Map<String, List<String>> collectNodesAndTransitions(Namespace model) {
        Map<String, List<String>> result = new HashMap<>();
        Set<String> nodes = new HashSet<>();
        List<String> transitions = new ArrayList<>();

        if (model != null && !model.getMember().isEmpty()) {
            var elements = model.getMember().getFirst().getOwnedElement();
            
            // Process each element
            for (Element element : elements) {
                if (element instanceof SuccessionAsUsage succession) {
                    // Get source and target nodes
                    var sourceElements = succession.getSource();
                    var targetElements = succession.getTarget();
                    
                    if (!sourceElements.isEmpty() && !targetElements.isEmpty()) {
                        Element source = sourceElements.getFirst();
                        Element target = targetElements.getFirst();
                        
                        // Add source node
                        if (source instanceof ActionUsage) {
                            nodes.add(source.getName());
                        }
                        
                        // Add target node
                        if (target instanceof ActionUsage) {
                            nodes.add(target.getName());
                        }
                        
                        // Add transition
                        String transition = String.format("%s -> %s", 
                            source instanceof ActionUsage ? source.getName() : "unknown",
                            target instanceof ActionUsage ? target.getName() : "unknown");
                        transitions.add(transition);
                    }
                }
            }
        }

        result.put("nodes", new ArrayList<>(nodes));
        result.put("transitions", transitions);
        return result;
    }
} 