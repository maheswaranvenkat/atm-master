package com.intelizest.atm.service;

import com.intelizest.atm.domain.Note;

import java.math.BigDecimal;
import java.util.List;

public interface ATMChainService {
	List<Note> withDraw(BigDecimal amount);
}
