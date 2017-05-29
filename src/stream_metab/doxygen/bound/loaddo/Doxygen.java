package stream_metab.doxygen.bound.loaddo;

import java.io.IOException;

import stream_metab.doxygen.utils.WaterGetter;
import stream_metab.doxygen.utils.WaterGetterHState;
import stream_metab.doxygen.utils.WaterGetterReader;
import neo.motif.*;
import neo.state.*;

/**
 * Controls the rate of oxygen transfer due to advection in an inflow
 * <p>
 * <b>Input parameters or externally controlled states required by this resource
 * behavior:</b>
 * </p>
 * </b><ul style="list-style-type:none"> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px"> <i>WATER
 * (double)</i> - Volumetric rate of water transfer across the boundary
 * [Length<sup><small>3</small></sup> Time<sup><small>-1</small></sup>] </li>
 * <li style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 * <i>DOCONCTABLE (string)</i> - Name of table containing the time series of DO
 * concentrations in the boundary </li> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 * <i>TEMPTABLE (string)</i> - Name of table containing the time series of water
 * temperatures in the boundary </li> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 * <i>BAROPRESSURE (double, from attached "airdo" cell)</i> - Air pressure. (mm
 * of Hg) or provide a conversion factor BAROPRESSURECONV to convert to other
 * units. </li> <li
 * style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 * <i>{optional} SATDOCONCCONV (double, default value = 1.0)</i> - A conversion
 * factor for saturated DO concentration necessary if model units are not grams
 * for mass and meters for length. Conversion factor should convert from (g
 * m<sup><small>-3</small></sup>) to the desired units through multiplication.
 * </li> <li style="margin-bottom: 5px; margin-left: 10px; text-indent:-10px">
 * <i>{optional} BAROPRESSURECONV (double, default value = 1.0)</i> - A conversion
 * factor for atmospheric pressure necessary if input data units are not mm of
 * Hg. Conversion factor should convert from desired units to mm of Hg through
 * multiplication. </li> </ul>
 * 
 * @author Rob Payn
 */
public class Doxygen extends AbstractHalfRoute {
    
    /**
     * An inner class specifying the names of states and motifs
     * used by this behavior 
     * 
     * @author robert.payn
     */
    public static class Names {
        
        /**
         * State name for the air pressure in the air cell associated with the attached cell
         */
        public static final String AIR_PRESSURE = stream_metab.doxygen.patch.airdo.BaroPressure.class.getSimpleName();
        
        /**
         * State name for the DO concentration in the attached cell
         */
        public static final String DO_CONC_CELL = stream_metab.doxygen.patch.channeldo.DOConc.class.getSimpleName();
        
        /**
         * Motif name for the reaeration behavior attached to the cell
         */
        public static final String MOTIF_REAERATION = "reaerationdo";
        
        /**
         * State name for the table of DO Conc values for this holon
         */
        public static final String TABLE_DO_CONC = "DOCONCTABLE";
        
        /**
         * State name for the table of temperature values for this holon
         */
        public static final String TABLE_TEMP = "TEMPTABLE";
        
        /**
         * State name for flow of water in this holon
         */
        public static final String WATER = "WATER";
        
    }

    /**
     * Volumetric rate of water movement across the boundary
     * [Length<sup><small>3</small></sup> Time<sup><small>-1</small></sup>]
     */
    private WaterGetter discharge;
    
    /**
     * DO concentration in the water [Mass Length<sup><small>-3</small></sup>]
     */
    private HStateDbl dOConc;
    
    /**
     * Calculates the rate of oxygen mass movement (DO load) across the boundary
     * 
     * @return DO load [Mass Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double computeValue()
    {
        // solute load is volumetric rate of water movement
        // times the concentration of solute in the water
        return discharge.getValue() * dOConc.v;
    }

    /**
     * Calculates the initial rate of oxygen mass movement (DO load) across the
     * boundary
     * 
     * @return DO load [Mass Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * Defines the state dependencies for calculation of rate of DO movement
     * 
     * @see DOConc
     * @see stream_metab.water.bound.flow.Water
     * @see stream_metab.water.bound.looprate.Water
     */
    @Override
    protected void setDependencies()
    {
        dOConc = (HStateDbl) getInitHState(DOConc.class.getSimpleName());
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
