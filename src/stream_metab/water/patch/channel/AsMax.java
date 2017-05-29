package stream_metab.water.patch.channel;

import neo.motif.*;
import neo.state.*;

public class AsMax extends AbstractParameterDbl {

    private HStateDbl areaBed;
    private HStateDbl lengthPatch;
    private HStateDbl widthAvg;

    @Override
    protected double initValue()
    {

        if (isNil())
        {
            return (widthAvg.v + (widthAvg.v - (areaBed.v / lengthPatch.v))) * lengthPatch.v;
        }
        else
        {
            return getValue();
        }

    }

    @Override
    protected void setDependencies()
    {

        areaBed = (HStateDbl) getInitHState("AS");
        lengthPatch = (HStateDbl) getInitHState("LENGTH");
        widthAvg = (HStateDbl) getInitHState("WIDTHAVG");

    }

}
