package com.intelizest.atm.service;

import com.intelizest.atm.domain.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ATMChainServiceImpl implements ATMChainService {

	private DispenserService fiftyDispenser;

	private DispenserService twentyDispenser;

	private DispenserService tenDispenser;

	private DispenserService fiveDispenser;

	@Autowired
	public ATMChainServiceImpl(@Qualifier("fifty") DispenserService fiftyDispenser,
							   @Qualifier("twenty") DispenserService twentyDispenser,
							   @Qualifier("ten") DispenserService tenDispenser,
							   @Qualifier("five") DispenserService fiveDispenser) {
		this.fiftyDispenser = fiftyDispenser;
		this.twentyDispenser = twentyDispenser;
		this.tenDispenser = tenDispenser;
		this.fiveDispenser = fiveDispenser;
		this.fiftyDispenser.setNextChain(twentyDispenser);
		this.twentyDispenser.setNextChain(tenDispenser);
		this.tenDispenser.setNextChain(fiveDispenser);

	}


	@Override
	public List<Note> withDraw(BigDecimal amount) {
		return this.fiftyDispenser.dispense(amount.intValue());
	}
}
