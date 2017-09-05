package com.thinkgem.jeesite.modules.project.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;

import java.util.List;

public class MyDictUtils extends DictUtils {
	
	/**
	 * 合同金额是否太高，true：太高。
	 * @param contractAmount
	 * @return
	 */
	public static boolean isContractAmountHigh(String contractAmount) {
		return isContractAmountHigh(StringUtils.toDouble(contractAmount));
	}
	
	public static boolean isContractAmountHigh(Double contractAmount) {
		if (contractAmount == null || Double.isNaN(contractAmount))
			return true;
		Double dict_amount = StringUtils.toDouble(DictUtils.getDictValue("项目金额", "jic_prj_money", "0"));
		if (contractAmount > dict_amount) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 毛利率是否太低，true：太低。
	 * @param gpm
	 * @return
	 */
	public static boolean isGrossProfitMarginLow(String gpm) {
		return isGrossProfitMarginLow(StringUtils.toDouble(gpm));
	}
	
	public static boolean isGrossProfitMarginLow(Double gpm) {
		if (gpm == null || Double.isNaN(gpm))
			return true;
		Double dictValue = StringUtils.toDouble(DictUtils.getDictValue("毛利率", "jic_gross_profit_margin", "0"));
		if (gpm < dictValue) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	
	/**
	 * 合同金额高于2000W 或者 毛利率低于5%，需要总经理审批
	 * @param amount
	 * @param gpm
	 * @return
	 */
	public static boolean isBossAudit(String amount, String gpm) {
		if (StringUtils.isEmpty(amount) || StringUtils.isEmpty(gpm)) {
			return true;
		}
		return isBossAudit(StringUtils.toDouble(amount), StringUtils.toDouble(gpm));
	}
	
	public static boolean isBossAudit(Double amount, String gpm) {
		return isBossAudit(amount, StringUtils.toDouble(gpm));
	}

	public static boolean isBossAudit(String amount, Double gpm) {
		return isBossAudit(StringUtils.toDouble(amount), gpm);
	}
	
	public static boolean isBossAudit(Double amount, Double gpm) {
		if (isContractAmountHigh(amount) || isGrossProfitMarginLow(gpm)) 
			return true;
		else
			return false;
	}

    public static boolean isPurchaseAudit(String amount) {
        Double dict_amount = StringUtils.toDouble(DictUtils.getDictValue("采购金额", "jic_prj_money", "0"));
        Double purchase_amount = StringUtils.toDouble(amount);
        if (purchase_amount > dict_amount) {
            return true;
        } else {
            return false;
        }
    }

    public static int isPurchaseAuditInt(String amount) {
	    if (isPurchaseAudit(amount)) {
            return 0;
        } else {
            return 1;
        }
    }

    public static boolean isHrAudit(String contractType) {

//        1	销售合同	jic_contract_type
//        3	技术开发合同	jic_contract_type
//        2	技术服务合同  jic_contract_type
        if ("2".equals(contractType)) {
            return true;
        } else {
            return false;
        }
    }

    public static int isHrAuditInt(String contractType) {
	    if (isHrAudit(contractType)) {
            return 0;
        } else {
	        return 1;
        }
    }

    public static boolean isJxBossAudit(String prjType, String gpm) {
		try {
			double curD = Double.parseDouble(gpm);
			return isJxBossAudit(prjType, curD);
		} catch (Exception e) {
			return true;
		}
	}

	public static boolean isJxBossAudit(String prjType, double gpm) {
		// 缺少参数，需要总经理审批
		if (StringUtils.isEmpty(prjType)) {
			return true;
		}
		// 根据项目类型得到项目的目标毛利率
		String targetGpm = getDictRemarks(prjType, "pro_category", "0");
		// 用审批单中的毛利率跟目标毛利率对比，(不能低于目标毛利率)
		try {
			Double targetD = Double.parseDouble(targetGpm);
			// 如果实际数字比目标值小，则需要总经理审批
			return gpm < targetD;
		} catch (Exception e) {
			return true;
		}
	}


	// 根据value得到dict对象remarks属性值
	// DictUtil类中只能获取到dict对象的value、label属性值
	public static String getDictRemarks(String value, String type, String defaultValue){
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(value)){
			for (Dict dict : getDictList(type)){
				if (type.equals(dict.getType()) && value.equals(dict.getValue())){
					// return dict.getLabel();
					return dict.getRemarks();
				}
			}
		}
		return defaultValue;
	}

}
