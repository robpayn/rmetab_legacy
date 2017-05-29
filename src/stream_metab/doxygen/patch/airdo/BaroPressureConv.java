package stream_metab.doxygen.patch.airdo;

import neo.motif.*;

/**
 * Controls a unit conversion factor (default 1.0) for the barometric pressure
 * 
 * @author Administrator
 */
public class BaroPressureConv extends AbstractParameterDbl {

    /**
     * Calculate the initial barometric conversion factor
     * 
     * @return Unit conversion factor ((mm of Hg) ({user provided
     *         units})<sup><small>-1</small></sup>)
     */
    @Override
    protected double initValue()
    {
        if (isNil())
        {
            return 1.0;
        }
        else
        {
            return getValue();
        }
    }

    /**
     * No dependencies
     */
    @Override
    protected void setDependencies()
    {
    }

}
