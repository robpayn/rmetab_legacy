package stream_metab.doxygen.patch.channeldo;

import java.io.IOException;

import stream_metab.doxygen.utils.WaterGetter;
import stream_metab.doxygen.utils.WaterGetterHState;
import stream_metab.doxygen.utils.WaterGetterReader;
import neo.motif.*;
import neo.state.*;
import neo.util.Logger;

/**
 * Controls the mass of oxygen in surface water channel cell
 * <p>
 * <b>Input parameters or externally controlled states required by this resource
 * behavior:</b>
 * </p>
 * </b><ul style="list-style-type:none"> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 * <i>INITDOCONC (double)</i> - Initial concentration of DO in the cell [Mass
 * Length<sup><small>-3</small></sup>] </li> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px"> <i>VELOCITY
 * (double)</i> - Average velocity of water through the cell [Length
 * Time<sup><small>-1</small></sup>] </li> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px"> <i>WIDTH
 * (double)</i> - Average width of water in the cell [Length] </li> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px"> <i>LENGTH
 * (double)</i> - Length of cell [Length] </li> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 * <i>SATDOCONCBOUNDIN (string)</i> - Name of the boundary holon with the
 * overall inlet DO saturation concentration </li> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 * <i>SATDOCONCBOUNDOUT (string)</i> - Name of the boundary holon with the
 * overall outlet DO saturation concentration </li> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 * <i>TEMPBOUNDIN (string)</i> - Name of the boundary holon with the overall
 * inlet temperature </li> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 * <i>TEMPBOUNDOUT (string)</i> - Name of the boundary holon with the overall
 * outlet temperature </li> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 * <i>{optional} DOCONCBOUND (string)</i> - Name of a boundary holon that
 * contains the DO concentration to be used in saturation deficit calculation.
 * If DOConcBound is not present or is empty, the DOConc state from the same
 * water cell with the SatDODef state will be used. </li> </ul>
 * 
 * @author Administrator
 */
public class Doxygen extends AbstractHub {
    
    /**
     * An inner class specifying the names of states and motifs
     * used by this behavior 
     * 
     * @author robert.payn
     */
    public static class Names {
        
        /**
         * State name for initial concentration
         */
        public static final String DO_CONC_INIT = "INITDOCONC";
        
        /**
         * State name for average saturated DO concentration
         */
        public static final String SAT_DO_CONC_AVG = 
            stream_metab.doxygen.edge.advectdocalib.SatDOConcReachAvg.class.getSimpleName(); 
        
        /**
         * State name for the name of the boundary with the input saturated DO concentration
         */
        public static final String SAT_DO_CONC_AVG_EDGE = "SATDOCONCAVGEDGE";
        
        /**
         * State name for average temperature
         */
        public static final String TEMP_AVG = 
            stream_metab.doxygen.edge.advectdocalib.TempReachAvg.class.getSimpleName();
        
        /**
         * State name for the name of the edge with the observed average reach temperature
         */
        public static final String TEMP_AVG_EDGE = "TEMPAVGEDGE";

        /**
         * State name for flow of water in this holon
         */
        public static final String WATER = "WATER";
        
    }

    /**
     * Initial DO concentration [Mass Length<sup><small>-3</small></sup>]
     */
    private HStateDbl initDOConc;
    
    /**
     * Volume of water in the cell [Length<sup><small>3</small></sup>]
     */
    private WaterGetter volume;

    /**
     * Calculate the initial mass of oxygen in the cell
     * 
     * @return Mass of DO [Mass]
     */
    @Override
    protected double initValue()
    {
        return initDOConc.v * volume.getValue();
    }

    /**
     * Define the dependencies for calculating initial mass of oxygen in the
     * cell
     * 
     * @see stream_metab.water.patch.channel.Water
     */
    @Override
    protected void setDependencies()
    {
        initDOConc = (HStateDbl) getInitHState(Names.DO_CONC_INIT);
        try
        {
            myHolon.getHState(Names.WATER);
            volume = new WaterGetterHState((HStateDbl)getInitHState(Names.WATER));
        }
        catch (HStateNotFoundException e)
        {
            try
            {
                volume = new WaterGetterReader(
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
