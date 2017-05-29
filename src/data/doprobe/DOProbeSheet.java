package data.doprobe;

import java.util.*;

import org.apache.poi.ss.usermodel.*;

public class DOProbeSheet implements Iterable<Row> {

    public static final int ROW_HEADINGS = 1;

    private static final int ROW_START = 4;

    private static final int COL_DATE = 0;

    private static final int COL_TIME = 1;
    
    private HashMap<String, Integer> headingMap;
    
    private Sheet sheet;
    
    private int rowStart;
    
    private int rowStop;
    
    private int cursor;
    

    public DOProbeSheet(Sheet sheet)
    {
        this.sheet = sheet;
        headingMap = createHeadingMap();
        rowStart = ROW_START;
        rowStop = findLastValidRow();
        cursor = ROW_START - 1;
    }

    public HashMap<String, Integer> createHeadingMap()
    {
        HashMap<String, Integer> headingMap = new HashMap<String, Integer>();
        Row headingRow = sheet.getRow(ROW_HEADINGS);
        int colCount = 0;
        while (colCount >= 0)
        {
            Cell cell = headingRow.getCell(colCount);
            if (cell == null)
            {
                break;
            }
            String heading = cell.getStringCellValue();
            if (heading == null || heading.trim().matches(""))
            {
                break;
            }
            headingMap.put(heading.trim(), colCount);
            colCount++;
        }
        return headingMap;
    }

    public int findLastValidRow()
    {
        int rowCount = ROW_START;
        while (rowCount >= ROW_START)
        {
            Row row = sheet.getRow(rowCount);
            if (row == null)
            {
                break;
            }
            int dateCellType = row.getCell(COL_DATE).getCellType();
            if (dateCellType == Cell.CELL_TYPE_BLANK)
            {
                break;
            }
            rowCount++;
        }
        return rowCount - 1;
    }
    
    public int getRowStart()
    {
        return rowStart;
    }
    
    public int getRowStop()
    {
        return rowStop;
    }
    
    public double getStartTime()
    {
        return getTime(rowStart);
    }
    
    public double getStopTime()
    {
        return getTime(rowStop);
    }
    
    public double getTime(int rowNumber)
    {
        return sheet.getRow(rowNumber).getCell(COL_DATE).getNumericCellValue() +
            sheet.getRow(rowNumber).getCell(COL_TIME).getNumericCellValue();
    }

    @Override
    public Iterator<Row> iterator()
    {
        return new Iterator<Row>(){
            
            int cursor = rowStart;

            @Override
            public boolean hasNext()
            {
                return (cursor + 1) <= rowStop;
            }

            @Override
            public Row next()
            {
                if ((cursor + 1) > rowStop)
                {
                    return null;
                }
                else
                {
                    cursor++;
                    return sheet.getRow(cursor);
                }
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException("Cannot remove from this iterator.");
            }
        };
    }

    public Integer getColNumber(String heading)
    {
        return headingMap.get(heading);
    }

    
    public String getSheetName()
    {
        return sheet.getSheetName();
    }
 
}
