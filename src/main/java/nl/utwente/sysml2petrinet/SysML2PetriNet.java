package nl.utwente.sysml2petrinet;

import nl.utwente.sysml2petrinet.PetriNet.PetriNet;
import nl.utwente.sysml2petrinet.m2m.Transformer;
import nl.utwente.sysml2petrinet.m2t.main.GeneratePetriNet;
import nl.utwente.sysml2petrinet.m2t_direct.TransformerPNML;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;
import org.omg.sysml.lang.sysml.Namespace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SysML2PetriNet {
    private static final Logger logger = LogManager.getLogger(SysML2PetriNet.class);

    private final SysMLProcessor processor;
    private PetriNet petriNet; // For JUnit testing

    public SysML2PetriNet(){
        String modelDir = "src/main/resources/sysml.library";
        this.processor = new SysMLProcessor(modelDir);
    }

    public PetriNet getPetriNet() {
        return petriNet;
    }

    public void transform(String filePath, String outputXMI, String outputDir, Boolean directTransformation) throws Exception {
        Namespace rootElement = processor.processSysMLFile(filePath);
        if (rootElement != null) {
            logger.info("Model inputted");
            Transformer transformer = new Transformer(rootElement);
            petriNet = transformer.transform();
            if(directTransformation){
                transformationDirect(petriNet, outputDir, filePath);
            }else {
                transformer.saveToXMI(petriNet, outputXMI);
                transformation(outputXMI, outputDir);
            }

        } else {
            throw new Exception("Failed to process or transform SysML file");
        }
    }

    public void transformMultiple(String fileDir, String outputDir,Boolean directTransformation) throws Exception {
        for(File f : processor.listSysmlFiles(new File(fileDir), false)){
            transform(f.getAbsolutePath(), outputDir + f.getName() + ".xmi", outputDir, directTransformation);
        }
    }

    private void transformation(String outputXMI, String outputDir) throws IOException {
        // Initialize the Acceleo generator and perform m2t generation
        URI modelURI = URI.createFileURI(outputXMI);
        File targetFolder = new File(outputDir);
        List<String> arguments = new ArrayList<>();
        GeneratePetriNet generatePetriNet = new GeneratePetriNet(modelURI, targetFolder, arguments);
        generatePetriNet.doGenerate(new BasicMonitor());
    }

    private void transformationDirect(PetriNet petriNet, String outputDir, String filePath) throws IOException {
        TransformerPNML transformerPnml = new TransformerPNML(petriNet, filePath);
        transformerPnml.saveToPNML(outputDir);
        logger.info("Saved PNML to: {}", outputDir);
    }
}
