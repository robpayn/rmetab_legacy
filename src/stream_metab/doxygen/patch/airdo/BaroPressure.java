package stream_metab.doxygen.patch.airdo;

import neo.motif.*;
import neo.state.*;
import neo.util.*;

/**
 * Controls the barometric pressure of the air
 * 
 * @author Administrator
 */
public class BaroPressure extends AbstractParameterDbl {

    /**
     * A unit conversion factor in input pressures are not in mm of Hg ((mm of
     * Hg) ({user provided units})<sup><small>-1</small></sup>)
     */
    private HStateDbl unitConv;

    /**
     * Calculate the initial barometric pressure
     * 
     * @return Pressure (mm of Hg)
     */
    @Override
    protected double initValue()
    {
        // Check if value is provided in input table
        if (isNil())
        {
            // Log an error if value is not provided (required parameter)
            Logger.logError("Must provide barometric pressure for holon " + myHolon.getUID().toString());
            return 0;
        }
        else
        {
            // set the barometric pressure value
            return unitConv.v * getValue();
        }
    }

    /**
     * Define the state dependencies for calculating barometric pressure
     * 
     * @see BaroPressureConv
     */
    @Override
    protected void setDependencies()
    {
        unitConv = (HStateDbl) getInitHState(BaroPressureConv.class.getSimpleName());
    }

}
