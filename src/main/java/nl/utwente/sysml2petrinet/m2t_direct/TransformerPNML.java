package nl.utwente.sysml2petrinet.m2t_direct;

import nl.utwente.sysml2petrinet.PetriNet.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * Transform PetriNet intermediate object directly to PNML file
 */
public class TransformerPNML {
    private static final Logger logger = LogManager.getLogger(TransformerPNML.class);
    private final PetriNet petriNet;
    private final String baseFilename;

    public TransformerPNML(PetriNet petriNet, String inputFilename) {
        this.petriNet = petriNet;
        // Extract base filename from the input path
        this.baseFilename = new File(inputFilename).getName();
    }

    /**
     * Save intermediate object to PNML file directly
     *
     * @param outputDirectory Output directory
     * @throws IOException IO errors
     */
    public void saveToPNML(String outputDirectory) throws IOException {
        // Create output directory if it doesn't exist
        File outputDir = new File(outputDirectory);
        if (!outputDir.exists() && outputDir.mkdirs()) {
            logger.info("{} was created.", outputDirectory);
        }

        // Use input base file name as output file name
        String baseName = baseFilename.substring(0, baseFilename.lastIndexOf('.'));
        String outputPath = Paths.get(outputDirectory, baseName + ".pnml").toString();

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(outputPath), StandardCharsets.UTF_8)) {
            // Write XML declaration, and PNML header
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.write(System.lineSeparator());
            writer.write("<pnml>");
            writer.write(System.lineSeparator());
            writer.write("  ");
            // PTNet type
            writer.write("<net id=\"" + petriNet.getName() + "\" type=\"http://www.pnml.org/version-2009/grammar/ptnet\">");
            writer.write(System.lineSeparator());
            // Add a page element
            writer.write("    <page id=\"page1\">\n");
            // Write places and transitions
            for (Node node : petriNet.getNodes()) {
                if (node instanceof Place place) {
                    writer.write("      <place id=\"" + place.getName() + "\">\n");
                    // Write initial marking
                    if (place.getInitialMarking() > 0) {
                        writer.write("        <initialMarking>\n");
                        writer.write("          <value>" + place.getInitialMarking() + "</value>\n");
                        writer.write("        </initialMarking>\n");
                    }
                    writer.write("      </place>\n");
                } else if (node instanceof Transition transition) {
                    writer.write("      <transition id=\"" + transition.getName() + "\">\n");
                    writer.write("      </transition>\n");
                }
            }
            // Write arcs
            for (Arc arc : petriNet.getArcs()) {
                writer.write("      <arc id=\"" + arc.getName() + "\" source=\"" + arc.getSource().getName() + "\" target=\"" + arc.getTarget().getName() + "\">\n");
                writer.write("        <inscription>\n");
                writer.write("          <value>" + arc.getWeight() + "</value>\n");
                writer.write("        </inscription>\n");
                writer.write("      </arc>\n");
            }
            // Close page and net
            writer.write("    </page>\n");
            writer.write("  </net>\n");
            writer.write("</pnml>\n");
        }
        logger.info("Saved PNML file to: {}", outputPath);
    }
} 