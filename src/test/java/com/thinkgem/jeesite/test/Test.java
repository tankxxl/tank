package com.thinkgem.jeesite.test;

import com.thinkgem.jeesite.common.utils.StringUtils;

public class Test {

	public static void main(String[] args) {
		boolean bo = StringUtils.isNumeric("123");
		// System.out.println(bo);

		int x = -10;
		x++;
		System.out.println(x);


		for (float b = -20; b <= 20; b = b + 0.5F) {
			for (float c = -20; c <= 20; c = c + 0.5F) {
				for (float d = -20; d <= 20; d = d + 0.5F) {
					if (
							(b + d == 8) && (c - d == 6) && (d + c == 13) && (b + d == 8)
							) {
						System.out.println(",b=" + b + ", c=" + c + ",d=" + d);
					}
				}
			}
		}

		System.out.println("asdfs");
	}
}
