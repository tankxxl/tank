package com.thinkgem.jeesite.modules.sys.utils;

import com.thinkgem.jeesite.common.utils.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExportUtils3 {

	/**
	 * 一:根据类似el的elStr----->${xxx.xxx.xxx}得到对应值
	 * @param bean
	 * @param cellElValue
	 * @return
	 */
	private static Object getValueByCellElValue(Object bean, String cellElValue) {
		String elValue = "";
		Object value = null;
		if(cellElValue.startsWith("${")) {
			//读取类型一 ${value1.value2}
			elValue = cellElValue.substring(cellElValue.indexOf("{")+1, cellElValue.length() -1);
			try {
				value = PropertyUtils.getProperty(bean, elValue);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		} else if(cellElValue.startsWith("$dict{")) {
			//读取类型二 字典 $dict{value1.value2,category}
			elValue = cellElValue.substring(cellElValue.indexOf("{")+1, cellElValue.indexOf(","));
			String dictCategory = cellElValue.substring(cellElValue.indexOf(",")+1, cellElValue.length() -1);
			String dictValue = "";
			try {
				dictValue = BeanUtils.getProperty(bean, elValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
			value = DictUtils.getDictLabel(dictValue, dictCategory, "");
		}
		return value;
	}
	
	// 设置cell的值
	private static void setCellValue(HSSFCell cell, Object objValue, String pattern) {
		if(objValue == null) {
			cell.setCellValue(""); 
			return;
		}
		if(objValue instanceof String) {
			cell.setCellValue((String)objValue);
		} else if(objValue instanceof Double) {
			cell.setCellValue((Double)objValue);
		} else if(objValue instanceof Integer) {
			cell.setCellValue((Integer)objValue);
		} else if (objValue instanceof Date) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			cell.setCellValue(dateFormat.format((Date)objValue));
		}
	}
	// 设置cell的值	
	private static void insertBeanToCell(Object bean, HSSFCell targetCell, String pattern ) {
		if (targetCell == null)
			return;
		String tempCellValue = targetCell.getStringCellValue();
		if (StringUtils.isBlank(tempCellValue))
			return;
		// 普通cell不填充
		if (!tempCellValue.startsWith("$"))
			return;
		setCellValue(targetCell, getValueByCellElValue(bean, tempCellValue), pattern);
	}
	
	/**
	 * 填充一个sheet
	 * @param sheet  excel中的sheet页
	 * @param rowStart sheet页开始行号
	 * @param rowEnd  sheet页结束行号
	 * @param bean  业务对象
	 * @param pattern  时间格式
	 */
	private  static void  insertBeanToSheet(HSSFSheet sheet, int rowStart, int rowEnd, Object bean, String pattern){
		HSSFRow row = null;
        HSSFCell cell = null;
        for(int i = rowStart -1; i<rowEnd; i++) {
        	row = sheet.getRow(i);
        	if(row == null) { continue; }
        	for(int j = 0; j < row.getLastCellNum(); j++) {
        		cell = row.getCell(j);
        		if(cell == null) { continue; }
        		insertBeanToCell(bean, cell, pattern);
        	}
        }
	}
		
	/**
	 * 导出到excel中
	 * @param response 用于把excel文件流输出到客户端
	 * @param bean 业务对象
	 * @param excelTemplatePathName excel模板文件路径和名称 
	 * @param fileReturnName excel输出文件名称
     *
	 */
	public static void export( HttpServletResponse response,
			Object bean,
			String excelTemplatePathName,
			String fileReturnName) {

        Context context = new Context();
        context.putVar("bean", bean);
        exportByContext(response, context, excelTemplatePathName, fileReturnName);

	}


    public static void exportByContext( HttpServletResponse response,
                               Context context,
                               String excelTemplatePathName,
                               String fileReturnName) {

        OutputStream os = null;
        FileInputStream is = null;
        try {
            String codedFileName = java.net.URLEncoder.encode(fileReturnName, "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + codedFileName + ".xls");
            response.setContentType("application/vnd.ms-excel");


            os =response.getOutputStream();
            //得到模板workbook
            is = new FileInputStream(excelTemplatePathName);

            JxlsHelper.getInstance().processTemplate(is, os, context);


			// begin
			// Map<String, List<Report>> beanParams = new HashMap<String, List<Report>>();
			// beanParams.put("reports", reports);
			// XLSTransformer former = new XLSTransformer();
			// former.transformXLS(tplPath, beanParams, destPath);
			// end

//            Transformer transformer = TransformerFactory.createTransformer(is, os);
//            AreaBuilder areaBuilder = new XlsCommentAreaBuilder(transformer);
//            List<Area> xlsAreaList = areaBuilder.build();
//            Area xlsArea = xlsAreaList.get(0);
//
//            Context context = new Context();
//            context.putVar("x", 5);
//            context.putVar("y", 10);
//            context.putVar("apply", bean);
//
//            JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator) transformer.getTransformationConfig().getExpressionEvaluator();
//            Map<String, Object> functionMap = new HashMap();
//            functionMap.put("dictUtils", new DictUtils());
//            evaluator.getJexlEngine().setFunctions(functionMap);
//
//            xlsArea.applyAt(new CellRef("Result!A1"), context);
//
//            transformer.write();

        } catch (Exception e) {
            System.out.println(e);
//			addMessage(redirectAttributes, "导出用户失败！失败信息："+e.getMessage());
        }finally{

            try{
                if(os!=null){
                    os.flush();
                    os.close();
                }
            }catch(Exception e){
                System.out.println(e);
            }
        }
        return;
    }
	
	public static void main(String[] args) {
		
	}
}
