package nl.utwente.sysml2petrinet.PetriNet;

public class Transition extends Node {
    private Boolean isMerge;
    private Boolean isDecision;
    public Transition() {
        super();
        this.isMerge = false;
        this.isDecision = false;
    }

    public boolean isMerge() {
        return isMerge;
    }
    public void setIsMerge(Boolean isMerge) {
        this.isMerge = isMerge;
    }
    public boolean isDecision() {return isDecision;}
    public void setIsDecision(Boolean isDecision) {this.isDecision = isDecision;}
} 