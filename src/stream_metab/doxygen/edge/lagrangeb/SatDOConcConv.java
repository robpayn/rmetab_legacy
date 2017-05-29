package stream_metab.doxygen.edge.lagrangeb;

import neo.motif.*;

/**
 * Controls a unit conversion factor for saturated DO Concentration
 * <p>
 * Default value is 1.0 if a conversion factor is not provided in the
 * initialization tables.
 * </p>
 * 
 * @author Rob Payn
 */
public class SatDOConcConv extends AbstractParameterDbl {

    /**
     * Sets initial value to the value in the input table. If no value is
     * present in the initialization table, set the value to 1.0.
     * 
     * @return Unit conversion constant ({user defined units} (g
     *         m<sup><small>-3</small></sup>)<sup><small>-1</small></sup>)
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
