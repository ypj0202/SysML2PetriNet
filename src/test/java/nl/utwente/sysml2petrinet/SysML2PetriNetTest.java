package nl.utwente.sysml2petrinet;

import nl.utwente.sysml2petrinet.PetriNet.Node;
import nl.utwente.sysml2petrinet.PetriNet.Arc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.thaiopensource.relaxng.jaxp.XMLSyntaxSchemaFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import static org.junit.Assert.*;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * JUnit tests for SysML2PetriNet transformation
 * Tests both valid models and error models, and validates generated PNML files against the schema.
 */
@RunWith(JUnit4.class)
public class SysML2PetriNetTest {

    private SysML2PetriNet transformer;
    private static final String MODEL_DIR = "src/main/resources/model";
    private static final String OUTPUT_DIR = "src/main/resources/model/output";
    private static final String SCHEMA_FILE = "src/test/resources/ptnet.pntd.xml";
    private static final List<String> validModels = Arrays.asList(
            "controlNodeAll.sysml",
            "controlNodeDecisionMerge.sysml",
            "controlNodeForkJoin.sysml",
            "example.sysml",
            "shop.sysml"
    );

    @Before
    public void setUp() {
        transformer = new SysML2PetriNet();
    }

    /**
     * Test valid SysML models transformation
     * Tests all models that don't start with "error"
     */
    @Test
    public void testValidModelsTransformation() throws Exception {
        for (String modelFile : validModels) {
            String modelPath = MODEL_DIR + "/" + modelFile;
            String outputPath = OUTPUT_DIR + "/";
            
            // Test direct transformation
            transformer.transform(modelPath, null, outputPath, true, true);
            
            // Verify PNML file was generated
            File pnmlFile = new File(outputPath + "/" + modelFile.replace(".sysml","") + ".pnml");
            assertTrue("PNML file should be generated for " + modelFile, pnmlFile.exists());

            // Test direct transformation
            transformer.transform(modelPath, null, outputPath, true, false);
            // Validate PNML against schema
            validatePNMLFile(pnmlFile.getAbsolutePath());
            // Verify PNML file was generated
            assertTrue("PNML file should be generated for " + modelFile, pnmlFile.exists());

            // Validate PNML against schema
            validatePNMLFile(pnmlFile.getAbsolutePath());
        }
    }

    /**
     * Test error model: errorModel.sysml
     */
    @Test
    public void testErrorModel() {
        String modelPath = MODEL_DIR + "/errorModel.sysml";
        String outputPath = OUTPUT_DIR + "/errorModel";
        var exception = assertThrows(Exception.class, () -> transformer.transform(modelPath, null, outputPath, true, true));
        assertEquals("Failed to process or transform SysML file", exception.getMessage());
    }

    /**
     * Test error model: errorWithoutStart.sysml
     */
    @Test
    public void testErrorWithoutStart() {
        String modelPath = MODEL_DIR + "/errorWithoutStart.sysml";
        var exception = assertThrows(Exception.class, () -> transformer.transform(modelPath, null, OUTPUT_DIR, true, true));
        assertEquals("No start element found!", exception.getMessage());
    }

    /**
     * Test error model: errorWithoutEnd.sysml
     */
    @Test
    public void testErrorWithoutEnd() {
        String modelPath = MODEL_DIR + "/errorWithoutEnd.sysml";
        var exception = assertThrows(Exception.class, () -> transformer.transform(modelPath, null, OUTPUT_DIR, true, true));
        assertEquals("No done element found!", exception.getMessage());
    }

    /**
     * Test that node names and arc names are unique in valid models
     */
    @Test
    public void testArcAndNodeNamesAreUnique() throws Exception {
        String modelPath = MODEL_DIR + "/controlNodeAll.sysml";

        transformer.transform(modelPath, null, OUTPUT_DIR, true, true);
        var pt = transformer.getPetriNet();
        
        // Check that all node names are unique
        List<String> nodeNames = new ArrayList<>();
        for(Node node: pt.getNodes()) {
            String nodeName = node.getName();
            assertFalse("Node name '" + nodeName + "' should be unique", nodeNames.contains(nodeName));
            nodeNames.add(nodeName);
        }
        
        // Check that all arc names are unique
        List<String> arcNames = new ArrayList<>();
        for(Arc arc: pt.getArcs()) {
            String arcName = arc.getName();
            if (arcName != null && !arcName.isEmpty()) {
                assertFalse("Arc name '" + arcName + "' should be unique", arcNames.contains(arcName));
                arcNames.add(arcName);
            }
        }
    }

    /**
     * Test counterexample: model with duplicate node names should fail
     */
    @Test
    public void testDuplicateNodeNames() {
        String modelPath = MODEL_DIR + "/duplicateNodeNames.sysml";
        String outputPath = OUTPUT_DIR + "/duplicateNodeNames";
        var exception = assertThrows(Exception.class, () -> transformer.transform(modelPath, null, outputPath, true, true));
        assertEquals("Duplicate node name found!", exception.getMessage());
    }


    /**
     * Test non-direct transformation (using XMI intermediate)
     */
    @Test
    public void testNonDirectTransformation() throws Exception {
        for (String modelFile : validModels) {
            String modelPath = MODEL_DIR + "/" + modelFile;
            String outputPath = OUTPUT_DIR + "/";
            String outputXMI = OUTPUT_DIR + "/" + modelFile.replace(".sysml","") + ".xmi";
            // Test non-direct transformation
            transformer.transform(modelPath, outputXMI, outputPath, false, true);

            // Verify PNML file was generated
            File pnmlFile = new File(outputPath + modelFile.replace(".sysml","") + ".pnml");
            assertTrue("PNML file should be generated for " + modelFile, pnmlFile.exists());

            // Verify XMI file was generated
            File xmiFile = new File(outputXMI);
            assertTrue("XMI file should be generated", xmiFile.exists());

            // Verify PNML file was generated
            File pnmlFileXMI = new File(OUTPUT_DIR+ "/" + modelFile.replace(".sysml","") + ".pnml");
            assertTrue("PNML file should be generated", pnmlFile.exists());

            // Validate PNML against schema
            validatePNMLFile(pnmlFileXMI.getAbsolutePath());
        }
        

    }

    /**
     * Custom EntityResolver to redirect remote anyElement.rng to local file
     */
    private static class LocalEntityResolver implements EntityResolver {
        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws IOException{
            if (systemId != null && systemId.endsWith("anyElement.rng")) {
                // Redirect to local file
                return new InputSource(new FileInputStream("src/test/resources/anyElement.rng.xml"));
            }
            return null; // Use default behavior
        }
    }

    /**
     * Validate a PNML file against the PTNet RNG schema using Jing
     */
    private void validatePNMLFile(String pnmlFilePath) {
        try {
            SchemaFactory factory = new XMLSyntaxSchemaFactory();
            File schemaFile = new File(SCHEMA_FILE);
            if (!schemaFile.exists()) {
                fail("Schema file not found: " + SCHEMA_FILE);
            }
            Source schemaSource = new StreamSource(schemaFile);
            schemaSource.setSystemId(schemaFile.toURI().toString());
            System.out.println("Loading schema from: " + schemaFile.getAbsolutePath());

            // Set up custom EntityResolver for Jing
            org.xml.sax.XMLReader reader = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
            reader.setEntityResolver(new LocalEntityResolver());
            System.setProperty("org.xml.sax.driver", reader.getClass().getName());

            Schema schema = factory.newSchema(schemaSource);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            File pnmlFile = new File(pnmlFilePath);
            if (!pnmlFile.exists()) {
                fail("PNML file not found: " + pnmlFilePath);
            }
            System.out.println("Validating PNML file: " + pnmlFile.getAbsolutePath());
            javax.xml.validation.Validator validator = schema.newValidator();
            validator.validate(new StreamSource(pnmlFile));
            System.out.println("PNML file " + pnmlFilePath + " is valid according to PTNet RNG schema");
        } catch (Exception e) {
            System.err.println("Primary validation failed: " + e.getMessage());
            e.printStackTrace();
            try {
                validatePNMLFileAlternative(pnmlFilePath);
            } catch (Exception e2) {
                System.err.println("Alternative validation failed: " + e2.getMessage());
                e2.printStackTrace();
                fail("PNML file " + pnmlFilePath + " failed PTNet RNG schema validation: " + e.getMessage() + " and alternative: " + e2.getMessage());
            }
        }
    }
    
    /**
     * Alternative validation approach using direct file loading
     */
    private void validatePNMLFileAlternative(String pnmlFilePath) throws Exception {
        SchemaFactory factory = new XMLSyntaxSchemaFactory();
        File schemaFile = new File(SCHEMA_FILE);
        Source schemaSource = new StreamSource(schemaFile);
        // Set up custom EntityResolver for Jing
        org.xml.sax.XMLReader reader = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
        reader.setEntityResolver(new LocalEntityResolver());
        System.setProperty("org.xml.sax.driver", reader.getClass().getName());
        Schema schema = factory.newSchema(schemaSource);
        File pnmlFile = new File(pnmlFilePath);
        javax.xml.validation.Validator validator = schema.newValidator();
        validator.validate(new StreamSource(pnmlFile));
        System.out.println("PNML file " + pnmlFilePath + " is valid according to PTNet RNG schema (alternative method)");
    }

    /**
     * Test that PTNet RNG schema can be loaded properly
     */
    @Test
    public void testRNGSchemaLoading() {
        try {
            // Create schema factory for RELAX NG using Jing
            SchemaFactory factory = new XMLSyntaxSchemaFactory();
            
            // Load the PTNet RNG schema
            File schemaFile = new File(SCHEMA_FILE);
            if (!schemaFile.exists()) {
                fail("Schema file not found: " + SCHEMA_FILE);
            }
            
            Source schemaSource = new StreamSource(schemaFile);
            schemaSource.setSystemId(schemaFile.toURI().toString());
            
            System.out.println("Loading schema from: " + schemaFile.getAbsolutePath());
            Schema schema = factory.newSchema(schemaSource);
            
            // If we get here, schema loading was successful
            assertNotNull("PTNet schema should be loaded successfully", schema);
            System.out.println("PTNet RNG schema loaded successfully");
            
        } catch (Exception e) {
            System.err.println("Schema loading failed: " + e.getMessage());
            e.printStackTrace();
            fail("Failed to load PTNet RNG schema: " + e.getMessage());
        }
    }
} 