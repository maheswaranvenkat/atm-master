package com.intelizest.atm.service;

import com.intelizest.atm.domain.Note;
import com.intelizest.atm.enums.Denomination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("ten")
public class Dollar10DispenserServiceImpl implements DispenserService {

	private static final Logger log = LoggerFactory.getLogger(Dollar10DispenserServiceImpl.class);

	private DispenserService chain;


	@Override
	public void setNextChain(DispenserService nextChain) {
		this.chain = nextChain;
	}

	@Override
	public List<Note> dispense(int amount) {
		List<Note> money = new ArrayList<Note>(1);

		Note dispensedAmount = new Note(Denomination.TEN, 0);
		int remainder = amount;
		if(amount >= 10){
			int num = amount/10;
			remainder = amount % 10;
			dispensedAmount.setNumber(num);
			log.info("Dispensing "+num+" 10$ notes");
			if(remainder !=0) log.info("There is a problem can't dispense "+remainder);
		}else{
		log.info("There is a problem can't dispense "+remainder);
		}
		money.add(dispensedAmount);
		return money;	}
}
