package com.intelizest.atm.controller;

import com.intelizest.atm.domain.Cash;
import com.intelizest.atm.domain.Money;
import com.intelizest.atm.domain.Note;
import com.intelizest.atm.enums.Denomination;
import com.intelizest.atm.service.ATMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This controller supports user interaction via the UI
 */
@Controller
@RequestMapping("/atm")
public class ATMController {

	ATMService atmService;



	@Autowired
	public ATMController(ATMService atmService) {
		this.atmService = atmService;
	}

	@RequestMapping(value="/initialise", method = RequestMethod.POST)
	public String initialise(@ModelAttribute Money money, BindingResult errors, Model model){
		String message = "Machine has already been initialised, new request ignored";
		List notes = new ArrayList<Note>(3);
		Note fifties = new Note(Denomination.FIFTY, money.getFifties());
		Note twenties = new Note(Denomination.TWENTY, money.getTwenties());
		Note tens = new Note(Denomination.TEN, money.getTens());
		Note fives = new Note(Denomination.FIVE, money.getFives());
		notes.add(fifties);
		notes.add(twenties);
		notes.add(tens);
		notes.add(fives);
		Cash cash = new Cash(notes);
		Boolean success = atmService.initialiseMachine(cash);
		if (success){
			message = "Machine has been initialised with "
					+fifties.getNumber()
					+ " fifty dollar notes and "
					+twenties.getNumber()
					+" twenty dollar notes"
					+tens.getNumber()
					+" tens dollar notes"
					+fives.getNumber()
					+" fives dollar notes";
		}
		model.addAttribute("image","/images/puffin1.jpg");
		model.addAttribute("initiliased", true);
		model.addAttribute("message",message);
		return "home/result";
	}

	@RequestMapping("/withdraw/{amount}")
	public String withDrawMoney(@PathVariable BigDecimal amount, Model model) {
		String message = "Thank-you for your patronage, please find your bank notes below.";
		List<Note> money = atmService.withDraw(amount);
		Boolean amountWasDispensed = atmService.checkAmount(money);
		model.addAttribute("fives", money.get(3).getNumber());
		model.addAttribute("tens",money.get(2).getNumber());
		model.addAttribute("twenties",money.get(1).getNumber());
		model.addAttribute("fifties",money.get(0).getNumber());
		if (amountWasDispensed) {
			model.addAttribute("displayAmount", true);

		} else {
			model.addAttribute("displayAmount", false);
			message = "We are currently unable to dispense the requested amount";
		}
		model.addAttribute("message", message);
		model.addAttribute("image","/images/puffin1.jpg");

		return "home/result";
	}

	@RequestMapping("/load/{type}/{amount}")
	public String loadMoney(@PathVariable int type,@PathVariable BigDecimal amount, Model model){
		 String message = "The following amount has been added to the machine:";
		Denomination denomination = Denomination.FIFTY;
		if (type == 20){
			denomination = Denomination.TWENTY;
		}
		if (type == 10){
			denomination = Denomination.TEN;
		}
		if (type == 5){
			denomination = Denomination.FIVE;
		}
		List<Note> money = atmService.loadMoney(denomination,amount);
		model.addAttribute("displayAmount", true);
		model.addAttribute("fives",money.get(3).getNumber());
		model.addAttribute("tens",money.get(2).getNumber());
		model.addAttribute("twenties",money.get(1).getNumber());
		model.addAttribute("fifties",money.get(0).getNumber());
		model.addAttribute("message", message);
		model.addAttribute("image","/images/puffin1.jpg");

		return "home/result";

	}

	 //Demonstrating alternative of using url params - not preferred option, favour PathVariables as these are Restful
	@RequestMapping("/check")
	public String checkNumberOfNotesAvailable(@RequestParam("type") int type, Model model) {
		Denomination denomination = Denomination.FIFTY;
		if (type == 20){
			 denomination = Denomination.TWENTY;
		}
		if (type == 10){
			denomination = Denomination.TEN;
		}
		if (type == 5){
			denomination = Denomination.FIVE;
		}
		int numberOfNotes = atmService.notesAvailable(denomination);
		model.addAttribute("message","Number of notes of type: "+denomination.value()+ " available is "+numberOfNotes);
		model.addAttribute("image","/images/puffin1.jpg");

		return "home/result";
	}



}
