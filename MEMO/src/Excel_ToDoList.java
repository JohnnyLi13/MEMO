

import java.io.File;
import java.io.FileOutputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class Excel_ToDoList {
	
	final int HEADER_ROW = 0;
	final int TASK_NUMBER_CELL = 0;
	final int TASK_LOCATION_CELL = 1;
	final int TASK_CELL = 2;
	final int TASK_DEADLINE = 3;
	final int TASK_PRIORITY = 4;
	final int TASK_CREATION_DATE = 5;

	void init(String list_name) {
		if (new File(list_name + ".xls").exists()==true) {
			System.out.println(list_name + "'s list already exists");
			return;
		}
		try {
			Workbook wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet(list_name);
			Row header_row = sheet.createRow(HEADER_ROW);
			///
			CellStyle center_align_style = wb.createCellStyle();
			center_align_style.setAlignment(HorizontalAlignment.CENTER);
			///
			Cell task_num = header_row.createCell(TASK_NUMBER_CELL);
			task_num.setCellValue("task #");
			task_num.setCellStyle(center_align_style);
			///
			Cell location = header_row.createCell(TASK_LOCATION_CELL);
			location.setCellValue("location");
			location.setCellStyle(center_align_style);
			///
			Cell task = header_row.createCell(TASK_CELL);
			task.setCellValue("task");
			task.setCellStyle(center_align_style);
			///
			Cell task_deadline = header_row.createCell(TASK_DEADLINE);
			task_deadline.setCellValue("deadline");
			task_deadline.setCellStyle(center_align_style);
			///
			Cell task_priority = header_row.createCell(TASK_PRIORITY);
			task_priority.setCellValue("priority");
			task_priority.setCellStyle(center_align_style);
			///
			Cell task_creation_date = header_row.createCell(TASK_CREATION_DATE);
			task_creation_date.setCellValue("created on");
			task_creation_date.setCellStyle(center_align_style);
			///
			FileOutputStream output = new FileOutputStream(list_name + ".xls");
			wb.write(output);
			wb.close();
			output.close();
			System.out.println(list_name + "'s list is created");
		}catch(Exception e) {
			System.out.println("fail to init ToDoList for " + list_name);
			e.printStackTrace();
		}
	}
	
	
	
	
	
}
