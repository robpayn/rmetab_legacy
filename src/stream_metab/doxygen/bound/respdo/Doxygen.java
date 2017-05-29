package stream_metab.doxygen.bound.respdo;

import java.io.IOException;

import stream_metab.doxygen.utils.WaterGetter;
import stream_metab.doxygen.utils.WaterGetterHState;
import stream_metab.doxygen.utils.WaterGetterReader;
import neo.holon.*;
import neo.motif.*;
import neo.state.*;
import neo.util.Logger;

/**
 * Controls the rate of oxygen removal due to respiration
 * <p>
 * <b>Input parameters or externally controlled states required by this resource
 * behavior:</b>
 * </p>
 * </b><ul style="list-style-type:none"> 
 * <li style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 * <i>DAILYRESP (double)</i> - Daily average mass flux of oxygen respiration
 * [Mass Length<sup><small>-2</small></sup> Time<sup><small>-1</small></sup>]
 * </li>
 * <li style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 * <i>WETTEDAREA (double, from attached surface water cell)</i> - 
 * Wetted surface area of the associated water body
 * [Length<sup><small>2</small></sup>]
 * </li>
 * </ul>
 * 
 * @author robert.payn
 */
public class Doxygen extends AbstractHalfRoute {
    
    /**
     * Static inner class for specifying names needed by this behavior 
     * 
     * @author robert.payn
     */
    public static class Names {
        
        /**
         * Name of state for the daily respiration rate
         */
        public static final String DAILY_RESP_RATE = "DAILYRESP";
        
        /**
         * Name of state for wetted area in the attached cell
         */
        public static final String WETTED_AREA = "WETTEDAREA";
        
    }

    /**
     * Instantaneous mass flux of oxygen due to respiration [Mass
     * Length<sup><small>-2</small></sup> Time<sup><small>-1</small></sup>]
     */
    private HStateDbl instResp;
    
    /**
     * The surface area over which production occurs
     * [Length<sup><small>2</small></sup>]
     */
    private WaterGetter surfArea;

    /**
     * Calculate the rate of oxygen mass reduction due to respiration
     * 
     * @return Rate of oxygen reduction [Mass Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        // rate of mass reduction is the mass flux rate times
        // the area across which the flux occurs
        return instResp.v * surfArea.getValue();
    }

    /**
     * Calculate the initial rate of oxygen mass reduction due to respiration
     * 
     * @return Rate of oxygen reduction [Mass Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the state dependencies for calculation of oxygen reduction rates
     * 
     * @see InstResp
     * @see stream_metab.water.patch.channel.WettedArea
     */
    @Override
    protected void setDependencies()
    {
        instResp = (HStateDbl) getInitHState(InstResp.class.getSimpleName());
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
