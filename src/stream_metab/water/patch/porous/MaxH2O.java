package stream_metab.water.patch.porous;

import neo.motif.AbstractParameterDbl;
import neo.state.HStateDbl;

/** Maximum water volume this compartment can contain. */
public class MaxH2O extends AbstractParameterDbl {

    HStateDbl grossvolume = null;
    HStateDbl porosity = null;

    public void setDependencies()
    {
        grossvolume = (HStateDbl) getInitHState("GROSSVOLUME");
        porosity = (HStateDbl) getInitHState("POROSITY");
    }

    public double initValue()
    {
        return (grossvolume.v * porosity.v);
    }

}
