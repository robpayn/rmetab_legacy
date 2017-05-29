package stream_metab.doxygen.edge.advectdo;

import java.io.IOException;

import stream_metab.doxygen.edge.advectdo.Doxygen.Names;
import stream_metab.doxygen.utils.WaterGetter;
import stream_metab.doxygen.utils.WaterGetterHState;
import stream_metab.doxygen.utils.WaterGetterReader;
import neo.holon.*;
import neo.motif.*;
import neo.state.*;
import neo.util.Logger;

/**
 * Controls dissolved oxygen concentration
 * 
 * @author robpayn
 */
public class DoConc extends AbstractUpdaterDbl {

    /**
     * Volumetric flow rate of water through the edge
     * [Length<sup><small>3</small></sup> Time<sup><small>-1</small></sup>]
     */
    private WaterGetter discharge;
    
    /**
     * DO concentration in cell on "from" side of edge [Mass
     * Length<sup><small>-3</small></sup>]
     */
    private HStateDbl dOConcFrom;
    
    /**
     * DO concentration in cell on "to" side of edge [Mass
     * Length<sup><small>-3</small></sup>]
     */
    private HStateDbl dOConcTo;

    /**
     * Calculates the concentration based on the direction of water flow.
     */
    @Override
    protected double computeValue()
    {
        if (discharge.getValue() > 0)
        {
            // Discharge is flowing from the "from" cell (forward), so use the
            // concentration
            // in the "from" cell
            return dOConcFrom.v;
        }
        else
        {
            // Discharge is flowing from the "to" cell (backward), so use the
            // concentration
            // in the "to" cell
            return dOConcTo.v;
        }
    }

    /**
     * Calculate the initial concentration
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the dependencies for calculating the concentration
     */
    @Override
    protected void setDependencies()
    {
        dOConcFrom = (HStateDbl) getInitHState(((Edge) myHolon).getFrom(), Doxygen.Names.DO_CONC_CELL);
        dOConcTo = (HStateDbl) getInitHState(((Edge) myHolon).getTo(), Doxygen.Names.DO_CONC_CELL);
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
                Logger.logError("Can't get data.");
                e1.printStackTrace();
            }
        }
    }

}
