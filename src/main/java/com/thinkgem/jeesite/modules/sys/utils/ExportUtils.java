package com.thinkgem.jeesite.modules.sys.utils;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.apply.entity.external.ProjectApplyExternal;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.User;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExportUtils {
	
	
	/**
	 * test
	 * @param args
	 */
	public static void main(String[] args) {
		Office office = new Office();
		office.setName("bangongshi");
		User user = new User();
		user.setName("zhang");
		user.setOffice(office);
		ProjectApplyExternal external = new ProjectApplyExternal();
		external.setSaler(user);
		external.setEstimatedGrossProfitMarginDescription("abc");
		try {
			Object likeElValue = getLikeElValue4Field("${estimatedGrossProfitMarginDescription}",external);
//			Object likeElValue = getLikeElValue("${saler:office:name}",external);
			System.out.println(likeElValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test1(){
	}
	/**
	 * 用于得到 属性对应的值（该值有可能是super类的属性）
	 * @param filedName
	 * @param obj
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object getGetMethodReturnValue(String filedName, Object obj) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String getMethodName = "get"+filedName.substring(0,1).toUpperCase()+filedName.substring(1);
		Method method = obj.getClass().getMethod(getMethodName, new Class[]{});
		return method.invoke(obj, new Object[]{});
	}
	/**
	 * 一:根据类似el的elStr----->${xxx:xxx:xxx}得到对应值
	 * @param elStr
	 * @param obj
	 * @return
	 */
	public static Object getLikeElValue4Field(String elStr, Object obj) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		String[] valueTree = null;
        Object tempObj =obj;
	      //读取类型一  
			String substring = elStr.substring(elStr.indexOf("{")+1, elStr.length() -1);
			
			valueTree =substring.split(":");
			for(int m =0;m<valueTree.length;m++){
				tempObj = getGetMethodReturnValue(valueTree[m],tempObj);
				if(tempObj == null) {tempObj ="";break;}
			}
		return tempObj;
	}
	/**
	 *  *二：读取字典表的---->$dict{xxx:xxx:xxx,dict_category}
	 * @param elStr
	 * @param obj
	 * @return
	 */
	public static Object getLikeElValue4Dict(String elStr, Object obj) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		String[] valueTree = null;
		Object tempObj =obj;
		//读取类型二 字典
			String substring = elStr.substring(elStr.indexOf("{")+1, elStr.indexOf(","));
			String dictCategory = elStr.substring(elStr.indexOf(",")+1, elStr.length() -1);
			valueTree =substring.split(":");
			for(int m =0;m<valueTree.length;m++){
				tempObj = getGetMethodReturnValue(valueTree[m],tempObj);
				if(tempObj == null) {tempObj ="";break;}
			}
			return DictUtils.getDictLabel((String)tempObj,dictCategory, "");
	}
	/**
	 * 三：读取流程审批list---->$acts{0,xxx:xxx:xxx} 插入 actList 的act （机制：若actList）
	 * @param elStr
	 * @param actList 流程act列表
	 * @return
	 * @throws Exception
	 */
	public static Object getLikeElValue(String elStr, List<Act> actList) throws Exception {
			
		 Object tempObj ="";
		 String[] valueTree = null;
		int index = Integer.parseInt(elStr.substring(elStr.indexOf("{")+1, elStr.indexOf(",")));
		if(index>=actList.size()){
			throw new Exception("并未走到该审批人");
		}
		tempObj =actList.get(index);
		String substring = elStr.substring(elStr.indexOf(",")+1, elStr.length() -1);
		
		valueTree =substring.split(":");
		for(int m =0;m<valueTree.length;m++){
			tempObj = getGetMethodReturnValue(valueTree[m],tempObj);
			if(tempObj == null) {tempObj ="";break;}
		}
		return tempObj;
	}
	
	

	/**
	 * 注意 时间要转化为字符保存。（）
	 * 将object类型转化为实际类型 然后赋值给cell
	 * @param cell
	 * @param obj
	 * @param dateFormate 时间格式 如"yyyy-MM-dd"
	 */
	public static void setCellValue4StrongTurn(HSSFCell cell, Object obj, String pattern){
		if(obj == null) {cell.setCellValue(""); return;};
		setCellValue4StrongTurn(cell,obj);
		if(obj instanceof Date){
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			cell.setCellValue(dateFormat.format((Date)obj));
		}
	}
	/**
	 * 注意 该方法不解析date类型
	 * 将object类型转化为实际类型 然后赋值给cell
	 * @param cell
	 * @param obj
	 */
	public static void setCellValue4StrongTurn(HSSFCell cell,Object obj){
		if(obj == null) {cell.setCellValue(""); return;};
		if(obj instanceof String){
			cell.setCellValue((String)obj);
		}
		if(obj instanceof Double){
			cell.setCellValue((Double)obj);
		}
		if(obj instanceof Integer){
			cell.setCellValue((Integer)obj);
		}
	}
	
	public static void insertBeanToRow(HSSFRow modelRow, HSSFRow targetRow, Object obj, String pattern){
		 HSSFCell tempCell = null;
		 HSSFCell targetCell = null;
		 String tempCellValue;
		 for(int j =0;j<modelRow.getLastCellNum();j++){
			 tempCell = modelRow.getCell(j);
			 targetCell=targetRow.getCell(j);
     		if(tempCell==null){continue;}
     		if(targetCell == null){
     			targetCell= targetRow.createCell(j);
     		}
     		tempCellValue =tempCell.getStringCellValue();
     		if(tempCellValue !=null){
     			inserCellValueBaseType(obj, targetCell, tempCellValue,pattern);
     		}
		 }
	}

	/**
	 * 用于根类$类型不同 调用不同插入方法
	 * @param obj
	 * @param targetCell
	 * @param tempCellValue
	 */
	private static void inserCellValueBaseType(Object obj, HSSFCell targetCell, String tempCellValue, String pattern) {
		if(tempCellValue.startsWith("${")){
			try {
				ExportUtils.setCellValue4StrongTurn(targetCell, ExportUtils.getLikeElValue4Field(tempCellValue,obj),pattern);
			} catch (Exception e) {
				//若出现异常。表示得不到 要取得对象为空。 那么 设置为"" 覆盖掉公式
				ExportUtils.setCellValue4StrongTurn(targetCell,"");
				e.printStackTrace();
			}
		}else if(tempCellValue.startsWith("$dict{")){
			try {
				ExportUtils.setCellValue4StrongTurn(targetCell, ExportUtils.getLikeElValue4Dict(tempCellValue,obj),pattern);
			} catch (Exception e) {
				//若出现异常。表示得不到 要取得对象为空。 那么 设置为"" 覆盖掉公式
				ExportUtils.setCellValue4StrongTurn(targetCell,"");
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * rowStart从1开始记（所以。）
	 * @param sheet
	 * @param rowStart 为excel中开始行号
	 * @param rowEnd	为excel中结束列号
	 * @param obj
	 */
	public  static void  insertBeanValueToExcel(HSSFSheet sheet, int rowStart, int rowEnd, Object obj, String pattern){
		HSSFRow row = null;
        HSSFCell cell = null;
        String tempCellValue;
        for(int i=rowStart -1;i<rowEnd;i++){
        	row = sheet.getRow(i);
        	if(row ==null){ continue;}
        	for(int j =0;j<row.getLastCellNum();j++){
        		cell = row.getCell(j);
        		if(cell==null){continue;}
        		tempCellValue =cell.getStringCellValue();
        		if(tempCellValue !=null){
        			inserCellValueBaseType(obj, cell, tempCellValue,pattern);
        		}
        	}
        }
	}
	
	
	
	/**
	 * 包含流程实例的插入
	 * rowStart从1开始记（所以。）
	 * @param sheet
	 * @param rowStart 为excel中开始行号
	 * @param rowEnd	为excel中结束列号
	 * @param obj
	 */
	public  static void  insertBeanValueToExcel(HSSFSheet sheet, int rowStart, int rowEnd, Object obj, List<Act> actList, String pattern){
		HSSFRow row = null;
		HSSFCell cell = null;
		String tempCellValue;
		
		
		boolean actFlag4unExcute = false;//该flag 用于判断 下面act是否需要注入值（如actFlag注入值失败 则不用继续注入值了）
		for(int i=rowStart -1;i<rowEnd;i++){
			row = sheet.getRow(i);
			if(row ==null){ continue;}
			for(int j =0;j<row.getLastCellNum();j++){
				cell = row.getCell(j);
				if(cell==null){continue;}
				tempCellValue =cell.getStringCellValue().trim();
				if (StringUtils.isEmpty(tempCellValue)) {continue;}
				if(tempCellValue !=null){
					
					inserCellValueBaseType(obj, cell, tempCellValue,pattern);
					if(tempCellValue.startsWith("$acts{")){
							if(actFlag4unExcute){
								continue;
							}
							try {
								ExportUtils.setCellValue4StrongTurn(cell, ExportUtils.getLikeElValue(tempCellValue,actList),pattern);
							} catch (Exception e) {
								//若出现异常。表示得不到 要取得对象为空。 那么 设置为"" 覆盖掉公式
								ExportUtils.setCellValue4StrongTurn(cell,"");
								e.printStackTrace();
								actFlag4unExcute = true;
							}
						}
					
					
				}
			}
		}
	}
	
	
	
	
	/**
	 * 
	 * @param response
	 * @param map
	 * @param actList 流程实例列表
	 * @return
	 */
	public static void export(HttpServletResponse response, Object bean, List<Act> actList, String workBookFileRealPathName, String fileReturnName, String datePattern) {
		
		OutputStream os = null;
		FileInputStream workBookFis = null;
		try {
			/**
			 * 下面设置 客户类型、客户行业、项目类型，读取字典表
			 */
			os =response.getOutputStream(); 
			//得到模板workbook
			workBookFis = new FileInputStream(workBookFileRealPathName);
			HSSFWorkbook wb =new HSSFWorkbook(workBookFis);
			
			HSSFSheet sheet = wb.getSheetAt(0);
			
			ExportUtils.insertBeanValueToExcel(sheet,2,sheet.getLastRowNum(),bean, actList,datePattern);
            
            
            String codedFileName = java.net.URLEncoder.encode(fileReturnName, "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + codedFileName + ".xls");  
            wb.write(os);
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
	
	
	public static void export4Statistic(HttpServletResponse response, Object obj, ArrayList<?> cycleObjectList, String workBookFileRealPathName, String fileReturnName, String datePattern){
		OutputStream os = null;
		FileInputStream workBookFis = null;
		
		try {
			/**
			 * 下面设置 客户类型、客户行业、项目类型，读取字典表
			 */
			os = response.getOutputStream();
			// 得到模板workbook
			workBookFis = new FileInputStream(workBookFileRealPathName);
			HSSFWorkbook wb = new HSSFWorkbook(workBookFis);
			HSSFSheet modelSheet = wb.getSheetAt(0);
			HSSFSheet targetSheet = null;
			targetSheet = wb.createSheet("工时统计总");
//			POIUtils.copySheet(wb, modelSheet, targetSheet, true);
			POIUtils.copeSheetCellWidth(modelSheet, targetSheet);
			
			
			
			
			int modelSheetRowIndex =0;
			HSSFRow modelSheetRow = null;
			HSSFCell modelSheetCell = null;
			String modelSheetValue;
			
			int targetSheetRowIndex =0;
			HSSFRow targetSheetRow = null;
			HSSFCell targetSheetCell = null;
			
			
			int cycleIndex4Start =0;//循环的起始
			int cycleIndex4End =0;//循环的行数
			
			
			//row(插入循环之前的数据)
			for(;modelSheetRowIndex<modelSheet.getLastRowNum();){
				modelSheetRow = modelSheet.getRow(modelSheetRowIndex);
				targetSheetRow = targetSheet.createRow(targetSheetRowIndex);

				if(modelSheetRow != null){
					modelSheetCell = modelSheetRow.getCell(0);
					if(modelSheetCell !=null){
						 if(!"${start_cycle}".equals(modelSheetCell.getStringCellValue())){
							 //cell
							 POIUtils.copyRow(wb, modelSheetRow, targetSheetRow, true);
							 for(int i =0;i<modelSheetRow.getLastCellNum();i++){
								 inserCellValueBaseType(obj, targetSheetRow.getCell(i), modelSheetRow.getCell(i).getStringCellValue(), datePattern);
							 }
						 }else{
							 modelSheetRowIndex++;
							 cycleIndex4Start = modelSheetRowIndex;
							 break;
						 }
					}
				}
				//行数共同迭代
				modelSheetRowIndex++;
				targetSheetRowIndex++;
			}
			
			
			//得到 待循环的行数
			for(;modelSheetRowIndex<modelSheet.getLastRowNum();){
				modelSheetRow = modelSheet.getRow(modelSheetRowIndex);
				if(modelSheetRow != null){
					modelSheetCell = modelSheetRow.getCell(0);
					if(modelSheetCell !=null){
						 if("${end_cycle}".equals(modelSheetCell.getStringCellValue())){
							 cycleIndex4End =modelSheetRowIndex - 1;
							 modelSheetRowIndex++;
							 break;
						 }
					}
				}
				modelSheetRowIndex++;
			}
			if(cycleIndex4End == 0){//没找到${end_cycle}标记
				throw new Exception("没找到 行的第一列值为${end_cycle}标记");
			}
			
			
			//插入循环数据
			for(int j=0;j<cycleObjectList.size();j++){
				for(int i =cycleIndex4Start;i<=cycleIndex4End;i++){
					targetSheetRow = targetSheet.createRow(targetSheetRowIndex);
					modelSheetRow = modelSheet.getRow(i);
					POIUtils.copyRow(wb, modelSheetRow, targetSheetRow, true);//拷贝行的样式与值
					insertBeanToRow(modelSheetRow, targetSheetRow, cycleObjectList.get(j),datePattern);//将列中公式转化为值
					targetSheetRowIndex++;
				}
			}
			
			//插入循环之后的数据
			for(;modelSheetRowIndex<modelSheet.getLastRowNum();){
				modelSheetRow = modelSheet.getRow(modelSheetRowIndex);
				targetSheetRow = targetSheet.createRow(targetSheetRowIndex);
				if(modelSheetRow != null){
					//cell
					POIUtils.copyRow(wb, modelSheetRow, targetSheetRow, true);//拷贝行的样式与值
					 for(int i =0;i<modelSheetRow.getLastCellNum();i++){
						 
						 inserCellValueBaseType(obj, targetSheetRow.getCell(i), modelSheetRow.getCell(i).getStringCellValue(), datePattern);//将列中公式转化为值
					 }
				}
				//行数共同迭代
				modelSheetRowIndex++;
				targetSheetRowIndex++;
			}
			
			//复制合并单元格格式
			POIUtils.mergerRegion(modelSheet,targetSheet);
			
			// 移除第一页的modelSheet
			wb.removeSheetAt(0);
			String codedFileName = java.net.URLEncoder.encode(fileReturnName, "UTF-8");
			response.setHeader("content-disposition", "attachment;filename=" + codedFileName + ".xls");
			wb.write(os);

		} catch (Exception e) {
			System.out.println(e);
			// addMessage(redirectAttributes, "导出用户失败！失败信息："+e.getMessage());
		} finally {

			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	
}
