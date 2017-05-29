package stream_metab.doxygen.patch.airdo;

import neo.motif.*;
import neo.state.*;
import neo.table.*;
import neo.util.*;

/**
 * Controls the instantaneous photosynthetically active radiation (PAR)
 * (interpolated from table data)
 * 
 * @author Administrator
 */
public class InstPAR extends AbstractUpdaterDbl {

    /**
     * Table interpolator for PAR
     */
    private FileInterpolater interpPAR;
    
    /**
     * Name of the PAR table
     */
    private HStateStr tableName;

    /**
     * Calculates the PAR (interpolated from input table)
     * 
     * @return PAR [Energy Length<sup><small>-2</small></sup>
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        return interpPAR.getValue(Scheduler.getCurrentTime());
    }

    /**
     * Creates the table interpolator from the provided file name and calculates
     * the initial PAR (interpolated from input table)
     * 
     * @return PAR [Energy Length<sup><small>-2</small></sup>
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double initValue()
    {
        try
        {
            interpPAR = InterpolaterFactory.create(tableName.v);
            return computeValue();
        }
        catch (Throwable t)
        {
            Logger.logError(t.getMessage());
            return 0;
        }
    }

    /**
     * Defines the state dependencies for calculating DO concentration
     */
    @Override
    protected void setDependencies()
    {
        tableName = (HStateStr) getInitHState(Doxygen.Names.PAR_TABLE);
    }

}
