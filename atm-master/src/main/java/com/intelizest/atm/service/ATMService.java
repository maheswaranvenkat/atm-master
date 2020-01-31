package com.intelizest.atm.service;

import com.intelizest.atm.domain.Cash;
import com.intelizest.atm.domain.Note;
import com.intelizest.atm.enums.Denomination;

import java.math.BigDecimal;
import java.util.List;

public interface ATMService {

	int notesAvailable(Denomination p);

	Boolean initialiseMachine(Cash p);

	List<Note> withDraw(BigDecimal p);

	BigDecimal availableFunds();

	List<Note> loadMoney(Denomination denomination, BigDecimal amount);

	 Boolean checkEdgeCases(BigDecimal amount);

	Boolean checkAmount(List<Note> money);
}
