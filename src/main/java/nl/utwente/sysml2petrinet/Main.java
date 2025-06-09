package nl.utwente.sysml2petrinet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.sysml.lang.sysml.Namespace;

import java.util.List;
import java.util.Map;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            String filePath = "src/main/resources/test.sysml";
            String modelDir = "src/main/resources/sysml.library";

            SysMLProcessor processor = new SysMLProcessor(modelDir);
            Namespace model = processor.processSysMLFile(filePath);

            if (model != null) {
                logger.info("Successfully processed SysML file");
                Map<String, List<String>> result = processor.collectNodesAndTransitions(model);
                logger.info("Nodes: {}", result.get("nodes"));
                logger.info("Transitions: {}", result.get("transitions"));
//                 System.out.println(model.getClass());
//                 System.out.println("=====");
//                 System.out.println("Declared name: " + model.getMember().getFirst().getName());
// //            System.out.println(model.getMember().getFirst().getOwnedElement().get(1).getName());
//                 var test = model.getMember().getFirst().getOwnedElement();
//                 for (int i = 0; i < test.size(); i++) {
//                     System.out.println(test.get(i).getClass());
//                 }
//                 System.out.println("========");
//                 var test2 = model.getMember().getFirst().getOwnedRelationship();
//                 for (int i = 0; i < test2.size(); i++) {
//                     System.out.println(test2.get(i).getClass());
//                 }
//                 System.out.println("====");
// //                System.out.println(((SuccessionAsUsage) test.getFirst()).getOwnedRelationship().getFirst().getName());
//                 System.out.println(((SuccessionAsUsage) test.get(0)).getSource().getFirst().getName());
//                 System.out.println(((SuccessionAsUsage) test.get(0)).getTarget().getFirst().getName());
//                 System.out.println(test2.getFirst().getSource().getFirst().getName());
//                 System.out.println(test2.getFirst().getTarget().getFirst());
//                 System.out.println("====");
//                 System.out.println(((SuccessionAsUsage) test.getLast()).getSource().getFirst().getName());
//                 System.out.println(((SuccessionAsUsage) test.getLast()).getTargetFeature());
//                 var result = ((SuccessionAsUsage) test.getLast()).getTargetFeature();
//                 System.out.println("====");
            } else {
                logger.error("Failed to process SysML file");
            }
        } catch (Exception e) {
            logger.error("Error in main", e);
        }
    }
}
