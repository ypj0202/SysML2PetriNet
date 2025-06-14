package nl.utwente.sysml2petrinet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * Only used if one want to run a case
     *
     * @param args Any
     * @throws IOException IO Error
     */
    public static void main(String[] args) throws IOException {
        try {
            String filePath = "src/main/resources/model/test-correct.sysml";
            String outputDir = "src/main/resources/model/output";
            String outputXMI = outputDir + "/output.xmi";
            SysML2PetriNet sysML2PetriNet = new SysML2PetriNet();
            sysML2PetriNet.transform(filePath, outputXMI, outputDir, true);
        } catch (Exception e) {
            logger.error("Error in main", e);
        }
    }
}
