package stream_metab.doxygen.edge.advectdocalib;

import java.io.IOException;

import stream_metab.doxygen.utils.WaterGetter;
import stream_metab.doxygen.utils.WaterGetterHState;
import stream_metab.doxygen.utils.WaterGetterReader;
import neo.motif.*;
import neo.state.HStateDbl;
import neo.state.HStateNotFoundException;
import neo.state.HStateStr;
import neo.util.Scheduler;

/**
 * Dummy hub for the observed behavior.  The observed behavior 
 * loads observed oxygen and temperature data that are necessary for
 * other states in the dissolved oxygen currency.  However, this
 * behavior does not directly influence oxygen movement at the
 * edge in which it is installed.
 * 
 * @author robert.payn
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
         * State name for the air pressure in the air cell associated with the attached cell
         */
        public static final String AIR_PRESSURE = stream_metab.doxygen.patch.airdo.BaroPressure.class.getSimpleName();
        
        /**
         * State name for the DO concentration in an attached cell
         */
        public static final String DO_CONC_CELL = stream_metab.doxygen.patch.channeldo.DOConc.class.getSimpleName();

        /**
         * Motif name for the reaeration behavior attached to the cell
         */
        public static final String MOTIF_REAERATION = "reaerationdo";
        
        /**
         * State name for the name of the boundary with the input saturated DO concentration
         */
        public static final String SAT_DO_CONC_IN = "SATDOCONCBOUNDIN";

        /**
         * State name for the table of DO Conc values for this holon
         */
        public static final String TABLE_DO_CONC = "DOCONCTABLE";
        
        /**
         * State name for the table of temperature values for this holon
         */
        public static final String TABLE_TEMP = "TEMPTABLE";
        
        /**
         * State name for the name of the boundary with the input temperature
         */
        public static final String TEMP_IN = "TEMPBOUNDIN";

        /**
         * State name for flow of water in this holon
         */
        public static final String WATER = "WATER";
        
    }

    /**
     * Volumetric flow rate of water through the edge
     * [Length<sup><small>3</small></sup> Time<sup><small>-1</small></sup>]
     */
    private WaterGetter discharge;
    
    /**
     * DO concentration
     * [Mass Length<sup><small>-3</small></sup>]
     */
    private HStateDbl dOConc;
    
    /**
     * Returns zero such that oxygen at the edge is not directly affected
     */
    @Override 
    public double computeValue()
    {
        // solute load is volumetric rate of water movement
        // times the concentration of solute in the water
        return discharge.getValue() * dOConc.v;
    }
    
    /**
     * Returns zero such that oxygen at the edge is not directly affected
     */
    @Override
    protected double initValue()
    {
        return computeValue();
    }

    /**
     * No dependencies
     */
    @Override
    protected void setDependencies()
    {
        dOConc = (HStateDbl) getInitHState(DoConc.class.getSimpleName());
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
