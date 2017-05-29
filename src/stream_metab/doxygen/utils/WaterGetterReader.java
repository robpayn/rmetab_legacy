package stream_metab.doxygen.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import neo.util.InputFile;
import neo.util.InputLoader;

public class WaterGetterReader implements WaterGetter {

    private InputFile inputFile;
    
    private String holon;
    
    private String state;
    
    public WaterGetterReader(String path, String holon, String state) throws IOException
    {
        int[] colDefs = {1, 3, 4};
        inputFile = InputLoader.registerHolon(path, colDefs, -1, holon);
        this.holon = holon;
        this.state = state;
    }

    @Override
    public double getValue()
    {
        return inputFile.getData(holon, state);
    }

}
