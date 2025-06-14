package nl.utwente.sysml2petrinet.m2m;

import nl.utwente.sysml2petrinet.PetriNet.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.omg.sysml.lang.sysml.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.util.Diagnostician;
import petrinet.PetrinetPackage;
import petrinet.PetrinetFactory;

import java.util.*;


public class Transformer {
    private static final Logger logger = LogManager.getLogger(Transformer.class);
    private final Namespace rootElement;
    private final Element rootAction;

    public Transformer(Namespace rootElement) {
        this.rootElement = rootElement;
        this.rootAction = rootElement.getOwnedElement().getFirst();
    }

    private boolean isTransition(Element element) {
        return element instanceof ForkNode || element instanceof MergeNode || element instanceof JoinNode || (element instanceof ActionUsage && !isPlace(element));
    }

    private boolean isPlace(Element element) {
        var member = element.getOwnedElement();
        return element instanceof ActionUsage && member.stream().anyMatch(e -> e.getName() != null && e.getName().equals("initialMarking"));
    }

    private Integer getInitialMarking(Element element) {
        var member = element.getOwnedElement();
        for (Element e : member) {
            if (e.getName() != null && e.getName().equals("initialMarking")) {
                if (e instanceof AttributeUsage attr) {
                    return getValue(attr);
                }
            }
        }
        logger.debug("No initial marking found for {}", element.getName());
        return 0;
    }

    /**
     * Get weight from arc in the model, if there is not presented, then by default, the weight for in and out will be 1.
     *
     * @param element Element from sysml
     * @return weight in map
     */
    private Map<String, Integer> getWeights(Element element) {
        Map<String, Integer> weights = new HashMap<>();
        // Default weight is set to 1
        weights.put("in", 1);
        weights.put("out", 1);
        var members = element.getOwnedElement();
        for (Element weight : members) {
            if (weight instanceof AttributeUsage) {
                if (weight.getName().equals("pIn")) {
                    weights.put("in", getValue(weight));
                }
                if (weight.getName().equals("pOut")) {
                    weights.put("out", getValue(weight));
                }
            }
        }
        return weights;
    }

    /**
     * Get value from element object for weight
     * @param element Parent element to get the value from
     * @return Obtained value, if no value found, return 1
     */
    private Integer getValue(Element element) {
        var member = element.getOwnedElement();
        for (Element element1 : member) {
            if (element1 instanceof LiteralInteger) {
                return ((LiteralInteger) element1).getValue();
            }
        }
        logger.debug("No value found for {}", element.getName());
        return 1; // Default to 1 if no value found
    }

    /**
     * Transform input model to intermediate PetriNet object
     * @return Transformed PetriNet object
     * @throws Exception Violation on constraint
     */
    public PetriNet transform() throws Exception {
        logger.info("Starting PetriNet transformation");
        PetriNet petriNet = new PetriNet();
        Map<String, Node> nodeMap = new HashMap<>();
        Map<String, Map<String, Integer>> placeWeights = new HashMap<>();
        logger.info("Transforming model: {}", rootAction.getName());

        if (rootElement != null && !rootElement.getMember().isEmpty()) {
            petriNet.setName(rootAction.getName());
            var elements = rootAction.getOwnedElement();
            logger.info("Processing {} elements", elements.size());

            // Create all places and transitions
            for (Element element : elements) {
                if (isPlace(element)) {
                    Place place = new Place();
                    place.setName(element.getName());
                    place.setInitialMarking(getInitialMarking(element));
                    nodeMap.put(element.getName(), place);
                    petriNet.addNode(place);
                    logger.info("Created place: {} with initial marking: {}", place.getName(), place.getInitialMarking());

                    // Store weights for this place
                    placeWeights.put(element.getName(), getWeights(element));
                } else if (isTransition(element)) {
                    Transition transition = new Transition();
                    transition.setName(element.getName());
                    nodeMap.put(element.getName(), transition);
                    petriNet.addNode(transition);
                    logger.info("Created transition: {}", transition.getName());
                }
            }

            // Create arcs
            for (Element element : elements) {
                if (element instanceof SuccessionAsUsage succession) {
                    var sourceElements = succession.getSource();
                    var targetElements = succession.getTarget();

                    if (!sourceElements.isEmpty() && !targetElements.isEmpty()) {
                        Element source = sourceElements.getFirst();
                        Element target = targetElements.getFirst();
                        if (source.getName().equals("start") || target.getName().equals("done") || target.getName().equals("start") || source.getName().equals("done")) {
                            logger.error("ActionUsage start or done not allowed");
                            throw new Exception("ActionUsage start or done not allowed");
                        }
                        Node sourceNode = nodeMap.get(source.getName());
                        Node targetNode = nodeMap.get(target.getName());

                        if (sourceNode == null || targetNode == null) {
                            logger.error("Source or target node not found for succession: {}", element.toString());
                            throw new Exception("Source or target node not found");
                        }

                        // Create arc, using uuid as its id
                        Arc arc = new Arc();
                        arc.setName(String.valueOf(UUID.randomUUID()));

                        // Determine if this is an input arc (Place -> Transition) or output arc (Transition -> Place)
                        if (sourceNode instanceof Place) {
                            // In: Place -> Transition
                            arc.setWeight(placeWeights.get(source.getName()).get("out"));
                            logger.debug("Created input arc from {} to {} with weight {}", source.getName(), target.getName(), arc.getWeight());
                        } else if (targetNode instanceof Place) {
                            // Out: Transition -> Place
                            arc.setWeight(placeWeights.get(target.getName()).get("in"));
                            logger.debug("Created output arc from {} to {} with weight {}", source.getName(), target.getName(), arc.getWeight());
                        } else {
                            logger.error("Transition to transition or place to place not allowed");
                            throw new Exception("Transition to transition not allowed");
                        }
                        arc.setSource(sourceNode);
                        arc.setTarget(targetNode);
                        petriNet.addArc(arc);
                        logger.info("Created arc: {}", arc.getName());
                    }
                }
            }
        }

        logger.info("PetriNet transformation completed with {} nodes and {} arcs", petriNet.getNodes().size(), petriNet.getArcs().size());
        return petriNet;
    }

    /**
     * Save transformed object to XMI
     * @param petriNet PetriNet object
     * @param outputPath Output path to save the model
     * @throws Exception Violation on constraints
     */
    public void saveToXMI(PetriNet petriNet, String outputPath) throws Exception {
        logger.info("Exporting XMI");
        //Setup XMI and register petriNet package (From PetriNet metamodel)
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.createResource(URI.createFileURI(outputPath));
        resourceSet.getPackageRegistry().put(PetrinetPackage.eNS_URI, PetrinetPackage.eINSTANCE);

        // Create the root PetriNet object
        petrinet.PetriNet petriNetObject = PetrinetFactory.eINSTANCE.createPetriNet();
        resource.getContents().add(petriNetObject);
        petriNetObject.setName(petriNet.getName());
        Map<Node, petrinet.Node> nodeMap = new HashMap<>();

        // Create places and transitions
        for (Node node : petriNet.getNodes()) {
            petrinet.Node nodeObject;
            if (node instanceof Place) {
                petrinet.Place place = PetrinetFactory.eINSTANCE.createPlace();
                place.setInitialMarking(((Place) node).getInitialMarking());
                nodeObject = place;
                logger.debug("Created EMF Place: {} with initial marking: {}", node.getName(), ((Place) node).getInitialMarking());
            } else {
                nodeObject = PetrinetFactory.eINSTANCE.createTransition();
                logger.debug("Created EMF Transition: {}", node.getName());
            }

            nodeObject.setName(node.getName());
            petriNetObject.getNodes().add(nodeObject);
            nodeMap.put(node, nodeObject);
        }

        // Create arcs
        for (Arc arc : petriNet.getArcs()) {
            petrinet.Arc arcObject = PetrinetFactory.eINSTANCE.createArc();
            arcObject.setName(arc.getName());
            arcObject.setWeight(arc.getWeight());
            arcObject.setSource(nodeMap.get(arc.getSource()));
            arcObject.setTarget(nodeMap.get(arc.getTarget()));
            petriNetObject.getArcs().add(arcObject);
            logger.debug("Created EMF Arc: {} with weight: {}", arc.getName(), arc.getWeight());
        }

        Map<String, Object> options = new HashMap<>();
        options.put("ENCODING", "UTF-8");

        // Validate the entire model using defined constraint before saving
        Diagnostic diagnostic = Diagnostician.INSTANCE.validate(petriNetObject);
        if (diagnostic.getSeverity() != Diagnostic.OK) {
            StringBuilder errorMessage = new StringBuilder("Model validation failed:\n");
            for (Diagnostic child : diagnostic.getChildren()) {
                errorMessage.append(child.getMessage()).append("\n");
            }
            throw new Exception(errorMessage.toString());
        }
        logger.info("Model validation passed");

        resource.save(options);
        logger.info("Saved XMI file to: {}", outputPath);
    }
}
