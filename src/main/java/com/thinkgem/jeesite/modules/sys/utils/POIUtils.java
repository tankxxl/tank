package com.thinkgem.jeesite.modules.sys.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Row;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.Iterator;

/**
 * POI工具类 功能点： 
 * 1、实现excel的sheet复制，复制的内容包括单元的内容、样式、注释
 * 2、setMForeColor修改HSSFColor.YELLOW的色值，setMBorderColor修改PINK的色值
 * 3、rgz实现sheet、row、cell等的拷贝，用于合同导出
 *
 * @author Administrator
 */

public class POIUtils {
	
	
	@Test
	public void test() throws Exception {
		FileInputStream workBookFis = new FileInputStream("/Users/Lu/Documents/abc.xls");
		HSSFWorkbook wb =new HSSFWorkbook(workBookFis);
		HSSFSheet sheet2 = wb.createSheet("ab");
		HSSFSheet sheet = wb.getSheetAt(0);
		copySheet(wb, sheet, sheet2, true);
		String stringCellValue = sheet2.getRow(0).getCell(0).getStringCellValue();
		System.out.println(stringCellValue);
	}
//  /**  
//   * 把一个excel中的cellstyletable复制到另一个excel，这里会报错，不能用这种方法，不明白呀？？？？？  
//   * @param fromBook  
//   * @param toBook  
//   */  
//  public static void copyBookCellStyle(HSSFWorkbook fromBook,HSSFWorkbook toBook){  
//      for(short i=0;i<fromBook.getNumCellStyles();i++){  
//          HSSFCellStyle fromStyle=fromBook.getCellStyleAt(i);  
//          HSSFCellStyle toStyle=toBook.getCellStyleAt(i);  
//          if(toStyle==null){  
//              toStyle=toBook.createCellStyle();  
//          }  
//          copyCellStyle(fromStyle,toStyle);  
//      }  
//  }  
    /** 
     * 复制一个单元格样式到目的单元格样式 
     * @param fromStyle 
     * @param toStyle 
     */  
    public static void copyCellStyle(HSSFCellStyle fromStyle,  
            HSSFCellStyle toStyle) {
        // 版本升级后api变化
        // toStyle.setAlignment(fromStyle.getAlignment());
        toStyle.setAlignment(fromStyle.getAlignmentEnum());

        //边框和边框颜色
        // 版本升级后api变化
        // toStyle.setBorderBottom(fromStyle.getBorderBottom());
        // toStyle.setBorderLeft(fromStyle.getBorderLeft());
        // toStyle.setBorderRight(fromStyle.getBorderRight());
        // toStyle.setBorderTop(fromStyle.getBorderTop());

        toStyle.setBorderBottom(fromStyle.getBorderBottomEnum() );
        toStyle.setBorderLeft( fromStyle.getBorderLeftEnum());
        toStyle.setBorderRight( fromStyle.getBorderRightEnum());
        toStyle.setBorderTop(fromStyle.getBorderTopEnum());

        toStyle.setTopBorderColor(fromStyle.getTopBorderColor());  
        toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());  
        toStyle.setRightBorderColor(fromStyle.getRightBorderColor());  
        toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());  
          
        //背景和前景  
        toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());  
        toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());  
          
        toStyle.setDataFormat(fromStyle.getDataFormat());

        // 版本升级后api变化
        // toStyle.setFillPattern(fromStyle.getFillPattern());
        toStyle.setFillPattern(fromStyle.getFillPatternEnum());

//      toStyle.setFont(fromStyle.getFont(null));  
        toStyle.setHidden(fromStyle.getHidden());  
        toStyle.setIndention(fromStyle.getIndention());//首行缩进  
        toStyle.setLocked(fromStyle.getLocked());  
        toStyle.setRotation(fromStyle.getRotation());//旋转

        // 版本升级后api变化
        // toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());
        toStyle.setVerticalAlignment(fromStyle.getVerticalAlignmentEnum());
        toStyle.setWrapText(fromStyle.getWrapText());  
          
    }  
    /** 
     * Sheet复制 
     * @param fromSheet 
     * @param toSheet 
     * @param copyValueFlag 
     */  
    public static void copySheet(HSSFWorkbook wb,HSSFSheet fromSheet, HSSFSheet toSheet,  
            boolean copyValueFlag) {  
        //合并区域处理  
        mergerRegion(fromSheet, toSheet);  
        for (Iterator<Row> rowIt = fromSheet.rowIterator(); rowIt.hasNext();) {
            HSSFRow tmpRow = (HSSFRow) rowIt.next();  
            HSSFRow newRow = toSheet.createRow(tmpRow.getRowNum());  
            //行复制  
            copyRow(wb,tmpRow,newRow,copyValueFlag);  
        }
        
        //设置列宽度
        copeSheetCellWidth(fromSheet, toSheet);
    }
    /**
     * 原理：cope第一列的宽度：若2表的第一列（那么会为fromSheet页创建个第一个行）
     * 将fromSheet的列宽度  同步到 toSheet的列宽度
     * @param fromSheet
     * @param toSheet
     */
	public static void copeSheetCellWidth(HSSFSheet fromSheet, HSSFSheet toSheet) {
		HSSFRow formSheetRow = fromSheet.getRow(0);
		if(formSheetRow == null){
			formSheetRow =fromSheet.createRow(0);
		}
		int physicalNumberOfCells = fromSheet.getRow(0).getPhysicalNumberOfCells();//得到实际列数
        for(int j =0;j<physicalNumberOfCells;j++){
        	toSheet.setColumnWidth(j, fromSheet.getColumnWidth(j));
        }
	} 
    
    
    
    /** 
     * 行复制功能 
     * @param fromRow 
     * @param toRow 
     */  
    public static void copyRow(HSSFWorkbook wb,HSSFRow fromRow,HSSFRow toRow,boolean copyValueFlag){  
        for (Iterator cellIt = fromRow.cellIterator(); cellIt.hasNext();) {
            HSSFCell tmpCell = (HSSFCell) cellIt.next();
            // 版本升级后api变化
            // HSSFCell newCell = toRow.createCell(tmpCell.getCellNum());
            HSSFCell newCell = toRow.createCell(tmpCell.getColumnIndex());
            copyCell(wb,tmpCell, newCell, copyValueFlag);  
        }
        //设置行的高度
        toRow.setHeight(fromRow.getHeight());
        
    }  
    /** 
    * 复制原有sheet的合并单元格到新创建的sheet 
    *  
    * @param fromSheet 原有的sheet
    * @param toSheet   新创建sheet
    */  
    public static void mergerRegion(HSSFSheet fromSheet, HSSFSheet toSheet) {  
       int sheetMergerCount = fromSheet.getNumMergedRegions();  
       for (int i = 0; i < sheetMergerCount; i++) {

        // Region mergedRegionAt = fromSheet.getMergedRegionAt(i);
        // toSheet.addMergedRegion(mergedRegionAt);
        // 版本升级后api变化
        toSheet.addMergedRegion(fromSheet.getMergedRegion(i));
       }  
    }  
    /** 
     * 复制单元格 
     *  
     * @param srcCell 
     * @param distCell 
     * @param copyValueFlag 
     *            true则连同cell的内容一起复制 
     */  
    public static void copyCell(HSSFWorkbook wb,HSSFCell srcCell, HSSFCell distCell,  
            boolean copyValueFlag) {  
        HSSFCellStyle newstyle=wb.createCellStyle();  
        copyCellStyle(srcCell.getCellStyle(), newstyle);  
//        distCell.setEncoding(srcCell.getEncoding());  
        //样式  
        distCell.setCellStyle(newstyle);  
        //评论  
        if (srcCell.getCellComment() != null) {  
            distCell.setCellComment(srcCell.getCellComment());  
        }  
        // 不同数据类型处理  
        int srcCellType = srcCell.getCellType();  
        distCell.setCellType(srcCellType);  
        if (copyValueFlag) {  
            if (srcCellType == HSSFCell.CELL_TYPE_NUMERIC) {  
                if (HSSFDateUtil.isCellDateFormatted(srcCell)) {  
                    distCell.setCellValue(srcCell.getDateCellValue());  
                } else {  
                    distCell.setCellValue(srcCell.getNumericCellValue());  
                }  
            } else if (srcCellType == HSSFCell.CELL_TYPE_STRING) {  
                distCell.setCellValue(srcCell.getRichStringCellValue());  
            } else if (srcCellType == HSSFCell.CELL_TYPE_BLANK) {  
                // nothing21  
            } else if (srcCellType == HSSFCell.CELL_TYPE_BOOLEAN) {  
                distCell.setCellValue(srcCell.getBooleanCellValue());  
            } else if (srcCellType == HSSFCell.CELL_TYPE_ERROR) {  
                distCell.setCellErrorValue(srcCell.getErrorCellValue());  
            } else if (srcCellType == HSSFCell.CELL_TYPE_FORMULA) {  
                distCell.setCellFormula(srcCell.getCellFormula());  
            } else { // nothing29  
            }  
        }  
    }  
}  