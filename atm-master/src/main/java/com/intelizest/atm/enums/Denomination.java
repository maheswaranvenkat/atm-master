package com.intelizest.atm.enums;

import java.math.BigDecimal;

public enum Denomination {
	FIVE(new BigDecimal(5)),
	TEN(new BigDecimal(10)),
	TWENTY(new BigDecimal(20)),
	FIFTY(new BigDecimal(50));

	private BigDecimal value;

	Denomination(BigDecimal amount)  {
		value = amount;
	}
	public BigDecimal value() {
		return value;
	  }
}
