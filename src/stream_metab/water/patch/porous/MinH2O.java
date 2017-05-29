package stream_metab.water.patch.porous;

import neo.motif.AbstractParameterDbl;
import neo.state.HStateDbl;

/** Maximum water volume this compartment can contain. */
public class MinH2O extends AbstractParameterDbl {

    HStateDbl grossvolume = null;
    HStateDbl fieldcap = null;

    public void setDependencies()
    {
        grossvolume = (HStateDbl) getInitHState("GROSSVOLUME");
        fieldcap = (HStateDbl) getInitHState("FIELDCAP");

    }

    public double initValue()
    {
        return grossvolume.v * fieldcap.v;
    }

}
