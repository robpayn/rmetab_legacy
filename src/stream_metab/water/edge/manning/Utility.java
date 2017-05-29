package stream_metab.water.edge.manning;

/**
 * <p>
 * Title: Restate ecosystem agent simulation system
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company: Eco-metrics, Inc.
 * </p>
 * 
 * @author Chris Bennett
 * @version 1.0
 */
public class Utility {
    public static final double radExp = 1.33333;
    public static final double velExp = 2.0;
    public static final int nSegments = 10000;
    public static double[] loVelPows, loHRadPows, loDfusePows;
    public static double[] velPows, hRadPows, dfusePows;
    public static final double maxHydroRad = 50.0;
    public static final double maxVelocity = 50.0;
    public static final double maxDiffuseVal = 10.0;
    public static final double maxLoVelocity = maxVelocity * 0.05;
    public static final double maxLoHydroRad = maxHydroRad * 0.05;
    public static final double maxLoDiffuseVal = maxDiffuseVal * 0.05;

    static
    {
        velPows = new double[nSegments + 1];
        hRadPows = new double[nSegments + 1];
        dfusePows = new double[nSegments + 1];
        loVelPows = new double[nSegments + 1];
        loHRadPows = new double[nSegments + 1];
        loDfusePows = new double[nSegments + 1];

        for (int i = 0; i < nSegments + 1; i++)
        {
            loVelPows[i] = Math.pow(maxVelocity * 0.05 * ((double) i / nSegments), velExp - 1);
            velPows[i] = Math.pow(maxVelocity * ((double) i / nSegments), velExp - 1);
            loHRadPows[i] = Math.pow(maxHydroRad * 0.05 * ((double) i / nSegments), radExp);
            hRadPows[i] = Math.pow(maxHydroRad * ((double) i / nSegments), radExp);
            loDfusePows[i] = Math.pow(maxDiffuseVal * 0.05 * ((double) i / nSegments), 1 / velExp);
            dfusePows[i] = Math.pow(maxDiffuseVal * ((double) i / nSegments), 1 / velExp);
        }

    }

    public Utility()
    {
    }

    public static double[] getVelPows()
    {
        return velPows;
    }

    public static double[] getHRadPows()
    {
        return hRadPows;
    }

    public static double[] getDiffusePows()
    {
        return dfusePows;
    }

    public static double[] getLoVelPows()
    {
        return loVelPows;
    }

    public static double[] getLoHRadPows()
    {
        return loHRadPows;
    }

    public static double[] getLoDiffusePows()
    {
        return loDfusePows;
    }

    public static class EquationTypes {
        static final public int DYNAMIC_WAVE = 1, DIFFUSE_WAVE = 2;

        public static String typeToString(int eqType)
        {
            switch (eqType)
            {
            case DYNAMIC_WAVE:
                return "DynamicWave";
            case DIFFUSE_WAVE:
                return "DiffuseWave";
            }
            return "***ERROR: Unknown Channel Type";
        }
    }

}
