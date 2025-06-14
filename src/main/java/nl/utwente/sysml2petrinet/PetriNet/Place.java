package nl.utwente.sysml2petrinet.PetriNet;

public class Place extends Node {
    private Integer initialMarking;

    public Place() {
        super();
        this.initialMarking = 0;
    }

    public Integer getInitialMarking() {
        return initialMarking;
    }

    public void setInitialMarking(Integer initialMarking) {
        this.initialMarking = initialMarking;
    }
}
