package stream_metab.doxygen.bound.loaddo;

import java.io.IOException;

import stream_metab.doxygen.edge.advectdo.Doxygen.Names;
import stream_metab.doxygen.utils.WaterGetter;
import stream_metab.doxygen.utils.WaterGetterHState;
import stream_metab.doxygen.utils.WaterGetterReader;
import neo.holon.*;
import neo.motif.*;
import neo.state.*;

/**
 * Controls the dissolved oxygen concentration in water moving across the
 * boundary
 * 
 * @author Rob Payn
 */
public class DOConc extends AbstractUpdaterDbl {

    /**
     * Volumetric rate of water movement across the boundary
     * [Length<sup><small>3</small></sup> Time<sup><small>-1</small></sup>]
     */
    private WaterGetter discharge;
    
    /**
     * DO concentration in the water of the attached cell [Mass
     * Length<sup><small>-3</small></sup>]
     */
    private HStateDbl dOConcCell;
    
    /**
     * Observed DO concentration at the boundary location [Mass
     * Length<sup><small>-3</small></sup>]
     */
    private HStateDbl dOConcObs;

    /**
     * Selects the DO concentration from the cell or observed value based on
     * direction of discharge
     * 
     * @return DO concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        if (discharge.getValue() > 0)
        {
            // Discharge is flowing into the cell, so use the concentration
            // in the boundary
            return dOConcObs.v;
        }
        else
        {
            // Discharge is flowing out of the cell, so use the concentration
            // in the cell
            return dOConcCell.v;
        }
    }

    /**
     * Selects the initial DO concentration from the cell or observed value
     * based on direction of discharge
     * 
     * @return DO concentration [Mass Length<sup><small>-3</small></sup>]
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Defines the state dependencies for calculating DO concentration
     */
    @Override
    protected void setDependencies()
    {
        dOConcCell = (HStateDbl) getInitHState(((Boundary) myHolon).getPatch(), Doxygen.Names.DO_CONC_CELL);
        dOConcObs = (HStateDbl) getInitHState(DoConcObs.class.getSimpleName());
        try
        {
            myHolon.getHState(Names.WATER);
            discharge = new WaterGetterHState((HStateDbl)getInitHState(Names.WATER));
        }
        catch (HStateNotFoundException e)
        {
            try
            {
                discharge = new WaterGetterReader(
                        ((HStateStr)getInitHState("ReaderPath")).v,
                        myHolon.getUID().toString(),
                        "water");
            }
            catch (IOException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

}
