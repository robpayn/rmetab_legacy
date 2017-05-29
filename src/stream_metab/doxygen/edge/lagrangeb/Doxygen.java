package stream_metab.doxygen.edge.lagrangeb;

import neo.motif.*;

/**
 * This behavior directly calculates the concentration and
 * is not designed to interface with water.  Therefore,
 * oxygen mass cannot be calculated (no discharge) and this
 * is a necessary place holder to allow use of the NEO framework.
 * 
 * @author robert.payn
 *
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
         * State name for water depth
         */
        public static final String DEPTH_WATER = "DEPTH";
        
        /**
         * State name for the time interval of the PAR data
         */
        public static final String INTERVAL_PAR_DATA = "PARDATATIMEINT";
        
        /**
         * State name for the gas exchange velocity at a Schmidt number of 600
         */
        public static final String K_600 = "K600";
        
        /**
         * Motif name for the reaeration behavior attached to the cell
         */
        public static final String MOTIF_REAERATION = "reaerationdo";
        
        /**
         * State name for the ration of daily P to daily PAR
         */
        public static final String RATIO_PROD_PAR = "PRODPARRATIO";
        
        /**
         * State name for the daily average respiration
         */
        public static final String RESP_DAILY_AVG = "DAILYRESP";
        
        /**
         * State name for the table of downstream DO Conc values for this holon
         */
        public static final String TABLE_DO_CONC_DOWN = "DODOWNTABLE";
        
        /**
         * State name for the table of upstream DO Conc values for this holon
         */
        public static final String TABLE_DO_CONC_UP = "DOUPTABLE";
        
        /**
         * State name for the table of PAR data
         */
        public static final String TABLE_PAR = "PARTABLE";
        
        /**
         * State name for the table of temperatures at the downstream end of the reach
         */
        public static final String TABLE_TEMP_DOWN = "TEMPDOWNTABLE";
        
        /**
         * State name for the table of temperatures at the upstream end of the reach
         */
        public static final String TABLE_TEMP_UP = "TEMPUPTABLE";
        
        /**
         * State name for the transport time of the reach
         */
        public static final String TIME_TRANSPORT = "TRANSPORTTIME";
        
    }

    /**
     * Place holder maintains a 0 value
     */
    @Override
    protected double computeValue()
    {
        return 0;
    }

    /**
     * Place holder maintains a 0 value
     */
    @Override
    protected double initValue()
    {
        return 0;
    }

    /**
     * No dependencies
     */
    @Override
    protected void setDependencies()
    {
    }

}
