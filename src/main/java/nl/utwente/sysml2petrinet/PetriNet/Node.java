package nl.utwente.sysml2petrinet.PetriNet;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
    private String name;
    private final List<Arc> outgoingArcs;
    private final List<Arc> incomingArcs;

    public Node() {
        this.outgoingArcs = new ArrayList<>();
        this.incomingArcs = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Arc> getOutgoingArcs() {
        return outgoingArcs;
    }

    public List<Arc> getIncomingArcs() {
        return incomingArcs;
    }
}
