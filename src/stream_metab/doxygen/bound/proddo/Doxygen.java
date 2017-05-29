package stream_metab.doxygen.bound.proddo;

import java.io.IOException;

import stream_metab.doxygen.utils.WaterGetter;
import stream_metab.doxygen.utils.WaterGetterHState;
import stream_metab.doxygen.utils.WaterGetterReader;
import neo.holon.*;
import neo.motif.*;
import neo.state.*;
import neo.util.Logger;

/**
 * Controls the rate of oxygen production due to photsynthesis
 * <p>
 * <b>Input parameters or externally controlled states required by this resource
 * behavior:</b>
 * </p>
 * </b><ul style="list-style-type:none"> 
 * <li style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 * <i>PTOPARRATIO (double)</i> - Daily average ratio of oxygen production to PAR
 * [(Mass Length<sup><small>-2</small></sup> Time<sup><small>-1</small></sup>)
 * (Energy Length<sup><small>-2</small></sup>
 * Time<sup><small>-1</small></sup>)<sup><small>-1</small></sup>]
 * </li>
 * <li style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 * <i>WETTEDAREA (double, from attached surface water cell)</i> - 
 * Wetted surface area of the associated water body
 * [Length<sup><small>2</small></sup>]
 * </li>
 * </ul>
 * 
 * @author Rob Payn
 */
public class Doxygen extends AbstractHalfRoute {

    /**
     * Static inner class for specifying names needed by this behavior 
     * 
     * @author robert.payn
     */
    public static class Names {
        
        /**
         * Motif name for the reaeration behavior attached to the cell
         */
        public static final String MOTIF_REAERATION = "reaerationdo";
        
        /**
         * Name of state for the daily P to PAR ratio for the current day
         */
        public static final String P_TO_PAR_RATIO = "PTOPARRATIO";
        
        /**
         * State name for the PAR in the air cell associated with the attached cell
         */
        public static final String PAR = stream_metab.doxygen.patch.airdo.InstPAR.class.getSimpleName();
        
        /**
         * Name of state for wetted area in the attached cell
         */
        public static final String WETTED_AREA = "WETTEDAREA";
        
    }

    /**
     * The instantaneous flux rate of production (for current time step) [Mass
     * Length<sup><small>-2</small></sup> Time<sup><small>-1</small></sup>]
     */
    private HStateDbl instProd;
    
    /**
     * The surface area over which production occurs
     * [Length<sup><small>2</small></sup>]
     */
    private WaterGetter surfArea;

    /**
     * Calculate the rate of oxygen mass production
     * 
     * @return Oxygen mass production rate [Mass
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        // rate of mass production is the mass flux rate times
        // the area across which the flux occurs
        return instProd.v * surfArea.getValue();
    }

    /**
     * Calculate the initial rate of oxygen mass production
     * 
     * @return Oxygen mass production rate [Mass
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the state dependencies for calculation of oxygen production rates
     * 
     * @see InstProd
     * @see stream_metab.water.patch.channel.WettedArea
     */
    @Override
    protected void setDependencies()
    {
        instProd = (HStateDbl) getInitHState(InstProd.class.getSimpleName());
        try
        {
            ((Boundary)myHolon).getPatch().getHState(Names.WETTED_AREA);
            surfArea = new WaterGetterHState((HStateDbl)getInitHState(
                    ((Boundary)myHolon).getPatch(), Names.WETTED_AREA));
        }
        catch (Exception e)
        {
            try
            {
                surfArea = new WaterGetterReader(
                        ((HStateStr)getInitHState(((Boundary)myHolon).getPatch(), "ReaderPath")).v,
                        ((Boundary)myHolon).getPatch().getUID().toString(),
                        "wettedarea");
            }
            catch (IOException e1)
            {
                Logger.logError("Can't get data.");
                e1.printStackTrace();
            }
        }

    }

}
