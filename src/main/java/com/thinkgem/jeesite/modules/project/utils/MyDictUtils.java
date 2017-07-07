package com.thinkgem.jeesite.modules.project.utils;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;

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

}
