package nl.utwente.sysml2petrinet.PetriNet;

public class Transition extends Node {
    private Boolean isMerge;
    public Transition() {
        super();
        this.isMerge = false;
    }

    public boolean isMerge() {
        return isMerge;
    }
    public void setIsMerge(Boolean isMerge) {
        this.isMerge = isMerge;
    }
} 