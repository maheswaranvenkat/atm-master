package com.intelizest.atm.controller;

import com.intelizest.atm.domain.Cash;
import com.intelizest.atm.domain.Money;
import com.intelizest.atm.domain.Note;
import com.intelizest.atm.enums.Denomination;
import com.intelizest.atm.service.ATMChainService;
import com.intelizest.atm.service.ATMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This controller is for machine to machine communication
 */
@RestController
@RequestMapping("/api")
public class ApiController {


	ATMService atmService;

	//Added to try out the Chain Design Pattern
	ATMChainService atmChainService;


	@Autowired
	public ApiController(ATMService atmService, ATMChainService atmChainService) {
		this.atmService = atmService;
		this.atmChainService = atmChainService;
	}

	@RequestMapping(value="/initialise", method = RequestMethod.POST)
	public ResponseEntity<String> initialiseAtm(@RequestBody Money money){
		String message = "Machine has already been initialised, new request ignored";
		HttpStatus status = HttpStatus.PRECONDITION_FAILED;
		List notes = new ArrayList<Note>(2);
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
			status = HttpStatus.OK;
		}
		return new ResponseEntity<String>(message, status );
	}

	@RequestMapping(value="/withdraw/{amount}", method = RequestMethod.GET)
	public ResponseEntity<Cash> withDrawMoney(@PathVariable BigDecimal amount) {
		List<Note> money = atmService.withDraw(amount);
		Cash cash = new Cash(money);
		HttpStatus status = HttpStatus.PRECONDITION_FAILED;
		Boolean amountWasDispensed = atmService.checkAmount(money);
		if (amountWasDispensed) {
			status = HttpStatus.OK;
		}
			return new ResponseEntity<Cash>(cash, status);
	}

	@RequestMapping(value = "/load/{type}/{amount}", method = RequestMethod.PUT)
	public ResponseEntity<Cash> loadMoney(@PathVariable int type,@PathVariable BigDecimal amount, Model model){
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
		Cash cash = new Cash(money);
		return new ResponseEntity<Cash>(cash, HttpStatus.OK);
	}

	@RequestMapping(value = "/check/{type}", method = RequestMethod.GET)
	public ResponseEntity<Integer> checkNumberOfNotesAvailable(@PathVariable int type ) {
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
		return  new ResponseEntity<Integer>(numberOfNotes, HttpStatus.OK);
	}


	@RequestMapping(value="/chain/{amount}", method = RequestMethod.GET)
	public ResponseEntity<Cash> withDrawMoneyWithChain(@PathVariable BigDecimal amount)  {
		Note five = new Note(Denomination.FIVE, 0);
		Note ten = new Note(Denomination.TEN, 0);
		Note twenty = new Note(Denomination.TWENTY,0);
		Note fifty = new Note(Denomination.FIFTY,0);
		List<Note> money = new ArrayList<Note>(4);
		money.add(fifty);
		money.add(twenty);
		money.add(ten);
		money.add(five);
		if (atmService.checkEdgeCases(amount)){
			 money =  atmChainService.withDraw(amount);
		}
		  return buildResponse(money);
	}


	private ResponseEntity<Cash>buildResponse(List<Note> money){
		Cash cash = new Cash(money);
		HttpStatus status = HttpStatus.PRECONDITION_FAILED;
		Boolean amountWasDispensed = atmService.checkAmount(money);
		if (amountWasDispensed) {
			status = HttpStatus.OK;
		}
		return new ResponseEntity<Cash>(cash, status);

	}

	/**
	 * Sample JSON Money format required
	 * @return  Money object as JSON
	 */
	 @RequestMapping(value="/help/money", method=RequestMethod.GET)
	public Money moneyJSON(){
		Money money = new Money();
		money.setFifties(10);
		money.setTwenties(10);
		money.setTens(10);
		money.setFives(10);
		return money;
	 }

}
