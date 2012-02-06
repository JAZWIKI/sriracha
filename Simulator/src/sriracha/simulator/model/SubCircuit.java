package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

import java.util.ArrayList;

/**
 * Subcircuits use a different internal node numbering system and maintains a mapping from internal nodes to
 * matrix indices
 */
public class SubCircuit extends CircuitElement{

    /**
     * Template this subcircuit is based on
     */
     private SubCircuitTemplate template;

    /**
     * Node numbers for mapping from internal system to
     * the mapping from the internal node system is done implicitly
     * through the index.
     * 
     */
    private ArrayList<Integer> nodes;

    /**
     * elements forming up the subcircuit these will have node numbers corresponding
     * to the corresponding matrix index.
     */
    private ArrayList<CircuitElement> elements;

    /**
     * adds a new element to the subcircuit
     * @param element - element with external node mapping
     */
    private void addElement(CircuitElement element){
        elements.add(element);
    }


    public SubCircuit(String name, SubCircuitTemplate template) {
        super(name);
        this.template = template;

    }

    /**
     * Called once all the nodes have received a mapping
     * uses that information combined with the template
     * to finish setting up before applying stamps
     */
    public void expand(){
       for(CircuitElement e : template.getElements()){
           CircuitElement copy = e.buildCopy(e.name);
           int[] internal = e.getNodeIndices();
           int[] external = new int[internal.length];
           for(int i = 0; i< internal.length; i++){
               external[i] = nodes.get(internal[i]);
           }
           copy.setNodeIndices(external);
           elements.add(copy);
           
       }
    }


    @Override
    public void setNodeIndices(int... indices) {
        for(int i : indices){
            nodes.add(i);
        }
    }

    @Override
    public int[] getNodeIndices() {
        int val[] = new int[nodes.size()], k = 0;
        for(Integer i: nodes) val[k++] = i;
        return val;
    }


    @Override
    public void applyStamp(IEquation equation) {
        for(CircuitElement e : elements){
            e.applyStamp(equation);
        }
    }

    /**
     * Number of External nodes only
     * @return
     */
    @Override
    public int getNodeCount() {
        return template.getExternalNodeCount();
    }

    /**
     * For subcircuits internal nodes count as extra variables
     * @return
     */
    @Override
    public int getExtraVariableCount() {
        int evCount = 0;
        for(CircuitElement e : elements){
            evCount += e.getExtraVariableCount();
        }
        return evCount + template.getInternalNodeCount();
    }

    /**
     * Makes a copy of the subcircuit
     * @param name - the name for the copy.
     */
    @Override
    public SubCircuit buildCopy(String name){
       return new SubCircuit(name, template);
    }
}
