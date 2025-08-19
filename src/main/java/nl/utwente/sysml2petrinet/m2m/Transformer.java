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
    
    /**
     * Generate an XML-safe ID using a simple counter
     *
     */
    private static int idCounter = 1;
    private static String generateXmlSafeId() {
        return "arc" + (idCounter++);
    }
    private final Namespace rootElement;
    private final Element rootAction;

    public Transformer(Namespace rootElement) {
        this.rootElement = rootElement;
        this.rootAction = rootElement.getOwnedElement().getFirst();
    }


    /**
     * Postprocess merge node so it fits definition for merge node. Not the best solution but it works, as it does not fit in the main transformation
     *
     * @param petriNet Original PetriNet
     * @return Updated PetriNet object
     */
    private PetriNet postProcessNode(PetriNet petriNet, boolean processMerge) {
        List<Arc> originalArcs = new ArrayList<>(petriNet.getArcs());
        Map<Node, List<Arc>> nodeToRelevantArcs = new HashMap<>();

        // Find relevant nodes based on mode
        for (Arc arc : originalArcs) {
            Node source = arc.getSource();
            Node target = arc.getTarget();

            if (processMerge) {
                // Processing merge nodes: track incoming arcs to merge node
                if (target instanceof Transition && ((Transition) target).isMerge()) {
                    nodeToRelevantArcs.putIfAbsent(target, new ArrayList<>());
                    nodeToRelevantArcs.get(target).add(arc);
                }
            } else {
                // Processing decision nodes: track outgoing arcs from decision node
                if (source instanceof Transition && ((Transition) source).isDecision()) {
                    nodeToRelevantArcs.putIfAbsent(source, new ArrayList<>());
                    nodeToRelevantArcs.get(source).add(arc);
                }
            }
        }

        List<Arc> arcsToAdd = new ArrayList<>();
        List<Arc> arcsToRemove = new ArrayList<>();
        List<Node> nodesToAdd = new ArrayList<>();
        List<Node> nodesToRemove = new ArrayList<>();
        int cloneCounter = 1;

        for (Map.Entry<Node, List<Arc>> entry : nodeToRelevantArcs.entrySet()) {
            Node targetNode = entry.getKey();
            List<Arc> relevantArcs = entry.getValue();

            if (processMerge) {
                // Skip merge nodes with <=1 incoming arc
                if (relevantArcs.size() <= 1) {
                    continue;
                }

                Map<Node, Node> sourceToClone = new HashMap<>();

                for (Arc incomingArc : relevantArcs) {
                    Node source = incomingArc.getSource();
                    Transition clonedMergeNode = new Transition();
                    clonedMergeNode.setName(targetNode.getName() + "_" + cloneCounter++);
                    clonedMergeNode.setIsMerge(true);

                    nodesToAdd.add(clonedMergeNode);

                    Arc newArc = new Arc();
                    newArc.setSource(source);
                    newArc.setTarget(clonedMergeNode);
                    newArc.setName(generateXmlSafeId());
                    arcsToAdd.add(newArc);

                    sourceToClone.put(source, clonedMergeNode);
                    arcsToRemove.add(incomingArc);
                }

                for (Arc downstreamArc : targetNode.getOutgoingArcs()) {
                    Node downstreamTarget = downstreamArc.getTarget();

                    for (Node clonedMergeNode : sourceToClone.values()) {
                        Arc newDownstreamArc = new Arc();
                        newDownstreamArc.setSource(clonedMergeNode);
                        newDownstreamArc.setTarget(downstreamTarget);
                        newDownstreamArc.setName(generateXmlSafeId());
                        arcsToAdd.add(newDownstreamArc);
                    }
                    arcsToRemove.add(downstreamArc);
                }

                nodesToRemove.add(targetNode);

            } else {
                // Skip decision nodes with <=1 outgoing arc
                if (relevantArcs.size() <= 1) {
                    continue;
                }

                Map<Node, Node> targetToClone = new HashMap<>();

                for (Arc outgoingArc : relevantArcs) {
                    Node target = outgoingArc.getTarget();
                    Transition clonedDecisionNode = new Transition();
                    clonedDecisionNode.setName(targetNode.getName() + "_" + cloneCounter++);
                    clonedDecisionNode.setIsDecision(true);

                    nodesToAdd.add(clonedDecisionNode);
                    var guardName = outgoingArc.getGuard(); if (outgoingArc.getGuard() == null) {
                        guardName = "else";
                    }
                    var guardPlace = new Place();
                    guardPlace.setName(guardName);
                    var guardArc = new Arc();
                    guardArc.setName("arc_" + guardName);
                    guardArc.setSource(guardPlace);
                    guardArc.setTarget(clonedDecisionNode);
                    arcsToAdd.add(guardArc);
                    nodesToAdd.add(guardPlace);

                    Arc newArc = new Arc();
                    newArc.setSource(clonedDecisionNode);
                    newArc.setTarget(target);
                    newArc.setName(generateXmlSafeId());
                    arcsToAdd.add(newArc);

                    targetToClone.put(target, clonedDecisionNode);
                    arcsToRemove.add(outgoingArc);
                }

                for (Arc incomingArc : targetNode.getIncomingArcs()) {
                    Node upstreamSource = incomingArc.getSource();

                    for (Node clonedDecisionNode : targetToClone.values()) {
                        Arc newIncomingArc = new Arc();
                        newIncomingArc.setSource(upstreamSource);
                        newIncomingArc.setTarget(clonedDecisionNode);
                        newIncomingArc.setName(generateXmlSafeId());
                        arcsToAdd.add(newIncomingArc);
                    }
                    arcsToRemove.add(incomingArc);
                }

                nodesToRemove.add(targetNode);
            }
        }

        petriNet.getArcs().removeAll(arcsToRemove);
        petriNet.getArcs().addAll(arcsToAdd);

        petriNet.getNodes().removeAll(nodesToRemove);
        petriNet.getNodes().addAll(nodesToAdd);

        return petriNet;
    }


    public PetriNet transform() throws Exception{
        logger.info("Starting PetriNet transformation");
        PetriNet petriNet = new PetriNet();
        Map<String, Node> nodeMap = new HashMap<>();
        logger.info("Transforming model: {}", rootAction.getName());

        if (rootElement != null && !rootElement.getMember().isEmpty()) {
            petriNet.setName(rootAction.getName());
            var elements = rootAction.getOwnedElement();
            logger.info("Processing {} elements", elements.size());
            try {
                var start = ((ActionUsage) rootAction).getOwnedMembership().stream().filter(a -> a != null && a.getMemberElement() != null && a.getMemberElement().getName() != null && a.getMemberElement().getName().equals("start")).toList().getFirst();
                elements.add(start.getMemberElement());
            } catch (Exception e) {
                throw new Exception("No start element found!");
            }
//            var start = ((ActionUsage) rootAction).getOwnedMembership().stream()
//                    .filter(a -> a != null && a.getMemberElement() != null && "start".equals(a.getMemberElement().getName()))
//                    .findFirst()
//                    .orElseThrow(() -> new Exception("No start element found!"));
//
//            elements.add(start.getMemberElement());
            // Create all places and transitions
            for (Element element : elements) {
                if(element instanceof ForkNode || element instanceof MergeNode || element instanceof JoinNode || element instanceof  DecisionNode) {
                    Transition transition = new Transition();
                    if(element instanceof MergeNode){
                        transition.setIsMerge(true);
                    }
                    else if(element instanceof DecisionNode){
                        transition.setIsDecision(true);
                    }
                    transition.setName(element.getName());
                    if(nodeMap.containsKey(element.getName())) {
                        throw new Exception("Duplicate node name found!");
                    }
                    nodeMap.put(element.getName(), transition);
                    petriNet.addNode(transition);
                } else if(element instanceof ActionUsage && !(element instanceof TransitionUsage)) {
                    Place place = new Place();
                    place.setName(element.getName());
                    if(element.getName().equals("start")){
                        place.setInitialMarking(1);
                    } else{
                        place.setInitialMarking(0);
                }
                    if(nodeMap.containsKey(element.getName())) {
                        throw new Exception("Duplicate node name found!");
                    }
                    nodeMap.put(element.getName(), place);
                    petriNet.addNode(place);
                }
            }

            // Create arcs
            for (Element element : elements) {
                if (element instanceof SuccessionAsUsage || element instanceof TransitionUsage) {
                    Element source;
                    Element target;
                    if(element instanceof SuccessionAsUsage succession){
                        source = succession.getSource().getFirst();
                        target = succession.getTarget().getFirst();
                    }else{
                        source = ((TransitionUsage)element).getSource();
                        target = ((TransitionUsage)element).getTarget();
                    }

                    if (source != null && target != null) {
                        // For done
                        if (target.getName().equals("done") && !nodeMap.containsKey(target.getName())) {
                            Place place = new Place();
                            place.setName(target.getName());
                            place.setInitialMarking(0);
                            nodeMap.put(target.getName(), place);
                            petriNet.addNode(place);
                        }
                        Node sourceNode = nodeMap.get(source.getName());
                        Node targetNode = nodeMap.get(target.getName());

                        if (sourceNode == null || targetNode == null) {
                            logger.error("Source or target node not found for succession: {}", element.toString());
                            throw new Exception("Source or target node not found");
                        }
                        Arc arc = new Arc();
                        arc.setName(generateXmlSafeId());
                        if (source instanceof DecisionNode) {
                            var guard = ((TransitionUsage)element).getOwnedFeatureMembership().stream().filter(a->a instanceof TransitionFeatureMembership).toList();
                            if(!guard.isEmpty()){
                                arc.setGuard(((FeatureReferenceExpression)guard.getFirst().getMemberElement()).getReferent().getName());
                            }
                        }
                        if (sourceNode instanceof Place && targetNode instanceof Place) {
                            Arc arc1 = new Arc();
                            arc1.setName(generateXmlSafeId());
                            Transition transitionBetweenPlaces = new Transition();
                            transitionBetweenPlaces.setName(source.getName() + "_to_" + target.getName());
                            nodeMap.put(transitionBetweenPlaces.getName(), transitionBetweenPlaces);
                            petriNet.addNode(transitionBetweenPlaces);
                            arc.setWeight(1);
                            arc1.setWeight(1);
                            arc.setSource(sourceNode);
                            arc.setTarget(transitionBetweenPlaces);
                            arc1.setSource(transitionBetweenPlaces);
                            arc1.setTarget(targetNode);
                            petriNet.addArc(arc1);
                        } else if (sourceNode instanceof Transition && targetNode instanceof Transition) {
                            Arc arc2 = new Arc();
                            arc2.setName(generateXmlSafeId());
                            Place placeBetweenControlNode = new Place();
                            placeBetweenControlNode.setName(source.getName() + "_to_" + target.getName());
                            nodeMap.put(placeBetweenControlNode.getName(), placeBetweenControlNode);
                            petriNet.addNode(placeBetweenControlNode);
                            arc.setWeight(1);
                            arc2.setWeight(1);
                            arc.setSource(sourceNode);
                            arc.setTarget(placeBetweenControlNode);
                            arc2.setSource(placeBetweenControlNode);
                            arc2.setTarget(targetNode);
                            petriNet.addArc(arc2);
                        } else{
                            arc.setWeight(1);
                            arc.setSource(sourceNode);
                            arc.setTarget(targetNode);
                        }
                        petriNet.addArc(arc);
                    }
                }
            }
        }
        if(!nodeMap.containsKey("done")) {
            throw new Exception("No done element found!");
        }
        var processedDecidePetriNet = postProcessNode(petriNet, false);
        return postProcessNode(processedDecidePetriNet, true);
    }

    /**
     * Save transformed object to XMI
     *
     * @param petriNet   PetriNet object
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
