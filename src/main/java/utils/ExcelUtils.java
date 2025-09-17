package utils;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;

public class ExcelUtils {
    public static String getRowColValue(String filePath, String sheetName, int rowNum, int colNum) {
        String fileName = System.getProperty("user.dir") + "\\src\\main\\java\\utils\\functional\\" + filePath + ".xlsx";
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(new File(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Sheet sheet = workbook.getSheet(sheetName);
        return sheet.getRow(rowNum).getCell(colNum).getStringCellValue();
    }
}
