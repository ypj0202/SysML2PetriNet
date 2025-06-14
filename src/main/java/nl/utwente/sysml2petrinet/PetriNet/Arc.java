package nl.utwente.sysml2petrinet.PetriNet;

public class Arc {
    private String name;
    private int weight;
    private Node source;
    private Node target;

    public Arc() {
        this.weight = 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
        if (source != null) {
            source.getOutgoingArcs().add(this);
        }
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target) {
        this.target = target;
        if (target != null) {
            target.getIncomingArcs().add(this);
        }
    }
}
