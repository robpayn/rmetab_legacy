package stream_metab.water;

import neo.util.General;

/**
 * Central location for utility methods that are likely to be used in several
 * places.
 * <ul>
 * Current contents:
 * <li>groundHeadToVol(...)
 * <li>surfaceHeadToVol(...)
 * <li>getLinkLengthHoriz(...)
 * <li>getLinkLengthVert(...)
 * </ul>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company: Eco-metrics, Inc.
 * </p>
 * 
 * @author Chris Bennett
 */
public class Utility {

    /**
     * Convert head to volume for ground compartment based on the given
     * parameters.
     * 
     * @param head
     *            head value (m) to be converted
     * @param ztop
     *            elevation (m) of top surface of compartment
     * @param zbot
     *            elevation (m) of bottom surface of compartment
     * @param porosity
     *            porosity of the aquifer
     * @param secstore
     *            compressivity of the aquifer
     * @param surfarea
     *            surface area (m2) of the top surface
     * @param fieldcap
     *            field capacity or gravity drainage limit
     * @return cubic meters of water in the porespace of the compartment
     */
    static public double groundHeadToVol(double head, double ztop, double zbot, double porosity, double secstore,
            double surfarea, double fieldcap)
    {
        double h2o;
        double grossvolume = surfarea * (ztop - zbot);

        if (head > ztop)
            h2o = (porosity + ((head - ztop) * secstore)) * grossvolume;
        else if (head > zbot && head <= ztop)
            h2o = ((head - zbot) * porosity + (ztop - head) * fieldcap) * surfarea;
        else
            h2o = grossvolume * fieldcap;

        return h2o;
    }

    /**
     * Convert head value for surface channel to volume for the compartment.
     * 
     * @param head  head value to be converted [Length]
     * @param zbank elevation of the bank top [Length]
     * @param zbed elevation of the stream bed [Length]
     * @param as surface area of the stream bed [Length<sup><small>2</small></sup>]
     * @param delas stream surface area increase for each meter change in depth [Length<sup><small>2</small></sup>]
     * @param asmax  maximum stream surface area [Length<sup><small>2</small></sup>]
     * @return volume of water in the stream for the given head [Length<sup><small>3</small></sup>]
     */
    static public double surfaceHeadToVol(double head, double zbank, double zbed, double as, double delas,
            double asmax)
    {
        double depth;
        // get depth of water in channel with a maximum depth of bankfull flow
        if (head > zbank)
        {
            // flooded, depth is maximum channel depth
            depth = zbank - zbed;
        }
        else
        {
            // not flooded, depth is water in channel
            depth = head - zbed;
        }
        // calculate volume of h2o in channel
        double h2o = (depth * as) + (depth * depth * delas / 2);
        // if flooded, add h20 from flood water
        if (head > zbank)
        {
            h2o += (head - zbank) * asmax;
        }
        return h2o;
    }

    /**
     * compute the euclidean distance between two horizontally linked
     * compartments. All units are meters. Coordinates can be anywhere, but are
     * usually taken to be the center of the compartments, except where one or
     * both compartments are surface nodes.
     * 
     * @param f_xcoord
     *            xcoord of the "in" node of the link
     * @param f_ycoord
     *            ycoord of the "in" node of the link
     * @param f_zcoord
     *            zcoord of the "in" node of the link
     * @param t_xcoord
     *            xcoord of the "out" node of the link
     * @param t_ycoord
     *            ycoord of the "out" node of the link
     * @param t_zcoord
     *            ycoord of the "out" node of the link
     * @return distance between node centers
     */
    static public double getLinkLengthHoriz(double f_xcoord, double f_ycoord, double f_zcoord, double t_xcoord,
            double t_ycoord, double t_zcoord)
    {
        double ret = 0;

        double x = f_xcoord - t_xcoord;
        double y = f_ycoord - t_ycoord;
        ret = General.getXYLength(x, y);
        return ret;

    }

    /**
     * compute euclidean distance between two vertically linked compartments.
     * All units are meters. Coordinates can be anywhere, but are usually taken
     * to be the center of the compartments, except where one or both
     * compartments are surface nodes.
     * 
     * @param f_zcoord
     *            double
     * @param t_zcoord
     *            double
     * @return distance between node centers
     */
    static public double getLinkLengthVert(double f_zcoord, double t_zcoord)
    {

        return (t_zcoord - f_zcoord);

    }
}
