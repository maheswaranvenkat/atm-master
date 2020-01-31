package com.intelizest.atm.service;

import com.intelizest.atm.domain.Note;

import java.util.List;

public interface DispenserService {


	void setNextChain(DispenserService nextChain);

	List<Note> dispense(int amount);
}
