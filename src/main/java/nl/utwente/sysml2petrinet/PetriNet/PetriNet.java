package nl.utwente.sysml2petrinet.PetriNet;

import java.util.ArrayList;
import java.util.List;

public class PetriNet {
    private String name;
    private final List<Node> nodes;
    private final List<Arc> arcs;

    public PetriNet() {
        this.nodes = new ArrayList<>();
        this.arcs = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Arc> getArcs() {
        return arcs;
    }

    public void addNode(Node node) {
        this.nodes.add(node);
    }

    public void addArc(Arc arc) {
        this.arcs.add(arc);
    }
}
