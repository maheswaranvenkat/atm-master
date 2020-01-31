package com.intelizest.atm.service;

import com.intelizest.atm.domain.Note;
import com.intelizest.atm.enums.Denomination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("fifty")
public class Dollar50DispenserServiceImpl implements DispenserService{


	private static final Logger log = LoggerFactory.getLogger(Dollar50DispenserServiceImpl.class);


	private DispenserService chain;

	@Override
	public void setNextChain(DispenserService nextChain) {
		this.chain = nextChain;

	}

	@Override
	public List<Note> dispense(int amount) {
		Note fifty = new Note(Denomination.FIFTY,0);
		List<Note> money = new ArrayList<Note>(2);
		if(amount >= 50){
			int num = amount/50;
			int remainder = amount % 50;
			fifty.setNumber(num);
			money.add(fifty);
			log.info("Dispensing "+num+" 50$ notes");
			if(remainder !=0) money.add(this.chain.dispense(remainder).get(0));
		}else{
			money.add(fifty);
			money.add(this.chain.dispense(amount).get(0));
		}
		return money;
	}
}
