package stream_metab.doxygen.edge.reaerationdo;

import java.io.IOException;

import stream_metab.doxygen.utils.WaterGetter;
import stream_metab.doxygen.utils.WaterGetterHState;
import stream_metab.doxygen.utils.WaterGetterReader;
import neo.holon.*;
import neo.motif.*;
import neo.state.*;
import neo.util.Logger;

/**
 * Controls the rate of diffusive oxygen exchange between surface water and air
 * (a.k.a. reaeration)
 * <p>
 * Air to water is positive<br>
 * Water to air is negative<br>
 * (or air is on "from" side and water is on "to" side)
 * </p>
 * <p>
 * <b>Input parameters or externally controlled states required by this resource
 * behavior:</b>
 * </p>
 * </b><ul style="list-style-type:none"> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px"> <i>K600
 * (double)</i> - First-order reaeration rate at a Schmidt number of 600 [Length
 * Time<sup><small>-1</small></sup>] </li> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 * <i>{optional} TempBound (string)</i> - Name of a boundary holon that contains
 * the temperature to be used in temperature correction calculation. If
 * TempBound is not present or is empty, the temperature state from the adjacent
 * water cell will be used. </li> </ul>
 * 
 * @author Administrator
 */
public class Doxygen extends AbstractRoute {
    
    /**
     * An inner class specifying the names of states and motifs
     * used by this behavior 
     * 
     * @author robert.payn
     */
    public static class Names {
        
        /**
         * Name of state for reaeration rate at a Schmidt number of 600
         */
        public static final String K_SCHMIDT_600 = "K600";
        
        /**
         * Name of the boundary which has temperature data to use for correction of
         * reaeration rate
         */
        public static final String BOUND_NAME_TEMP = "TEMPBOUND";
        
        /**
         * Name of state for DO saturated deficit in the attached water cell
         */
        public static final String DO_SAT_DEF = 
                stream_metab.doxygen.patch.channeldo.SatDODef.class.getSimpleName();
        
        /**
         * Name of state for temperature in a specified boundary
         */
        public static final String TEMP_BOUND =
                stream_metab.doxygen.bound.loaddo.TempObs.class.getSimpleName();
        
        /**
         * Name of state for temperature in the attached water cell
         */
        public static final String TEMP_CELL =
                stream_metab.doxygen.patch.channeldo.Temperature.class.getSimpleName();
        
        /**
         * Name of state for wetted area in the attached water cell
         */
        public static final String WETTED_AREA = "WETTEDAREA";
        
    }

    /**
     * First-order reaeration rate times water depth [Length
     * Time<sup><small>-1</small></sup>]
     */
    private HStateDbl k;
    
    /**
     * Saturation deficit (concentration) of DO in water [Mass
     * Length<sup><small>-3</small></sup>]
     */
    private HStateDbl satDef;
    
    /**
     * The surface area over which reaeration occurs
     * [Length<sup><small>2</small></sup>]
     */
    private WaterGetter surfArea;

    /**
     * Calculate the mass transfer rate of oxygen due to reaeration
     * 
     * @return Mass transfer rate of oxygen [Mass
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        return k.v * satDef.v * surfArea.getValue();
    }

    /**
     * Calculate the initial mass transfer rate of oxygen due to reaeration
     * 
     * @return Mass transfer rate of oxygen [Mass
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Define the state dependencies for calculating reaeration
     * 
     * @see VelocityK
     * @see stream_metab.doxygen.patch.channeldo.SatDODef
     * @see stream_metab.water.patch.channel.WettedArea
     */
    @Override
    protected void setDependencies()
    {
        // Get a reference to the "to" cell
        Holon toPatch = ((Edge) myHolon).getTo();

        // Get references and set dependencies
        k = (HStateDbl) getInitHState(VelocityK.class.getSimpleName());
        satDef = (HStateDbl) getInitHState(toPatch, Names.DO_SAT_DEF);
        try
        {
            toPatch.getHState(Names.WETTED_AREA);
            surfArea = new WaterGetterHState((HStateDbl)getInitHState(toPatch, Names.WETTED_AREA));
        }
        catch (Exception e)
        {
            try
            {
                surfArea = new WaterGetterReader(
                        ((HStateStr)getInitHState(toPatch, "ReaderPath")).v,
                        toPatch.getUID().toString(),
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
