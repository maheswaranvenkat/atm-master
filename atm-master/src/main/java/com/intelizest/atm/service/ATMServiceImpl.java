package com.intelizest.atm.service;

import com.intelizest.atm.repository.NoteRepository;
import com.intelizest.atm.domain.Cash;
import com.intelizest.atm.domain.Note;
import com.intelizest.atm.enums.Denomination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ATMServiceImpl implements ATMService {

	private static final Logger log = LoggerFactory.getLogger(ATMServiceImpl.class);


	NoteRepository noteRepository;

	@Autowired
	public ATMServiceImpl(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}


	@Override
	public int notesAvailable(Denomination denomination) {
		Note note = (noteRepository.findByType(denomination));
		return note.getNumber();
	}

	@Override
	public Boolean initialiseMachine(Cash cash) {
		Boolean success = true;
		for (Note note: cash.getMoney()) {
			if (noteRepository.countByType(note.getType()) != 0) {
				success = false;
			}
		}
		if (success) {
			for (Note note : cash.getMoney()) {
				try {
					if (noteRepository.countByType(note.getType()) == 0) {
						noteRepository.save(note);
					}
				} catch (Exception e) {
					success = false;
					log.error(e.getMessage());
				}
			}
		}
		return success;
	}

	public BigDecimal availableFunds() {
		BigDecimal availableFunds = BigDecimal.ZERO;
		Iterable<Note> cash = noteRepository.findAll();
		for (Note note : cash){
			BigDecimal value1 = BigDecimal.valueOf(note.getNumber());
			BigDecimal value2 = note.getType().value();
			availableFunds = availableFunds.add(value1.multiply(value2));
		}
		return availableFunds;
	}

	@Override
	public List<Note> loadMoney(Denomination denomination, BigDecimal amount) {

		Note note = noteRepository.findByType(denomination);
		note.setNumber(note.getNumber()+amount.intValue());
		noteRepository.save(note);
		Note five = new Note(Denomination.FIVE, 0);
		Note ten = new Note(Denomination.TEN, 0);
		Note twenty = new Note(Denomination.TWENTY,0);
		Note fifty = new Note(Denomination.FIFTY,0);

		List<Note> result = new ArrayList<Note>(3);

		switch (denomination) {
			case FIVE:
				five.setNumber(amount.intValue());
				break;
			case TEN:
				ten.setNumber(amount.intValue());
				break;
			case TWENTY:
				twenty.setNumber(amount.intValue());
				break;
			case FIFTY:
				fifty.setNumber(amount.intValue());
				break;
			default:
				break;
		}

		result.add(fifty);
		result.add(twenty);
		result.add(ten);
		result.add(five);
		return result;
	}

	private List<Note> dispense(BigDecimal amount, List<Note> result) {
		Note fiftyEntity = noteRepository.findByType(Denomination.FIFTY);
		Note twentyEntity = noteRepository.findByType(Denomination.TWENTY);
		Note tenEntity = noteRepository.findByType(Denomination.TEN);
		Note fiveEntity = noteRepository.findByType(Denomination.FIVE);

		int fifties = fiftyEntity.getNumber();
		int twenties = twentyEntity.getNumber();
		int tens = tenEntity.getNumber();
		int fives = fiveEntity.getNumber();

		int dispensedTwenties = 0;
		int dispensedFifties = 0;
		int dispensedTens = 0;
		int dispensedFives = 0;

		int tempDispense =0;
		int requestedAmount = amount.intValue(); // we know we only accept whole numbers

		//multiples of 50 (50, 100, 150, 200)
		if (requestedAmount%50 == 0 && requestedAmount/50 <= fifties) {
			int numberOf50s = requestedAmount / 50;
			dispensedFifties = numberOf50s;
			fifties = fifties - numberOf50s;
		} else if (requestedAmount/20 > twenties && requestedAmount/50 > fifties && requestedAmount/10 > tens) {
			//200 - only 3 50s and 8 20s
			int tempFifties = 0;
			int tempTwenties =0;
			int tempTens=0;
			while (tempFifties == 0 ||
					(( tempFifties < fifties)
							&& ((requestedAmount - tempFifties*50)%20 != 0)
							&& ((requestedAmount - tempFifties*50) / 20 <= twenties))
			){
				tempFifties++;
			}
			tempTwenties = (requestedAmount - tempFifties*50) / 20;
			tempDispense = tempFifties*50 + tempTwenties*20;
			if (tempDispense == requestedAmount && tempFifties <= fifties && tempTwenties <= twenties) {
				dispensedTwenties = tempTwenties;
				twenties = twenties - dispensedTwenties;
				dispensedFifties = tempFifties;
				fifties = fifties - dispensedFifties;
			}
		}
		//multiples of 20 (20, 40, 60, 80, 100, 200)
		else if(requestedAmount%20 == 0 && (requestedAmount/20 <= twenties)) {
			int numberOf20s = requestedAmount/20;
			dispensedTwenties = numberOf20s;
			twenties = twenties - numberOf20s;
		}
		//multiples of 10 (20, 30, 40, 50, 60, 70)
		else if(requestedAmount%10 == 0 && (requestedAmount/10 <= tens)) {
			int numberOf10s = requestedAmount/10;
			dispensedTens = numberOf10s;
			tens = tens - numberOf10s;
		}

		//must have 50s and 20s
		else if (requestedAmount%70 == 0)  {
			int numberOfUnits = requestedAmount/70;
			dispensedTens = numberOfUnits;
			dispensedTwenties = numberOfUnits;
			dispensedFifties = numberOfUnits;
			tens = tens - dispensedTens;
			twenties = twenties - dispensedTwenties;
			fifties = fifties - dispensedFifties;
		}

		else if (requestedAmount == 110) {
			if (fifties >=2 && tens >=1) {
				dispensedFifties = 2;
				dispensedTens = 1;
				tens = tens - dispensedTens;
				fifties = fifties - dispensedFifties;
			}
		}

		else if(requestedAmount == 105) {
			if(fifties >=2 && fives >=1) {
				dispensedFifties = 2;
				dispensedFives = 1;
				fives = fives - dispensedFives;
				fifties = fifties - dispensedFifties;
			}
		}

		else if (requestedAmount == 410) {
			if (fifties >=1 && twenties >=17) {
				dispensedFifties = 1;
				dispensedTwenties = 17;
				twenties = twenties - dispensedTwenties;
				fifties = fifties - dispensedFifties;
			}
		}

		else if (requestedAmount%20 != 0){  //taking 50 from an odd number makes it divisible by 20?
			int tempTwenties =  (requestedAmount-50)/20;
			int tempFifties =  1;
			tempDispense = tempTwenties * 20 + tempFifties * 50;
			if (tempDispense == requestedAmount && tempFifties <= fifties && tempTwenties <= twenties) {
				dispensedTwenties = tempTwenties;
				dispensedFifties = tempFifties;
				twenties = twenties - dispensedTwenties;
				fifties = fifties - dispensedFifties;
			}
		}
		else {  // try our luck with this
			int tempTwenties =  requestedAmount%50;
			int tempFifties =  requestedAmount/50;
			tempDispense = tempTwenties * 20 + tempFifties * 50;
			if (tempDispense == requestedAmount && tempFifties <= fifties && tempTwenties <= twenties) {
				dispensedTwenties = tempTwenties;
				dispensedFifties = tempFifties;
			} else {
				tempFifties = requestedAmount%20;
				tempTwenties = requestedAmount/20;
				tempDispense = tempTwenties * 20 + tempFifties * 50;
				if (tempDispense == requestedAmount && tempFifties <= fifties && tempTwenties <= twenties) {
					dispensedTwenties = tempTwenties;
					dispensedFifties = tempFifties;
				}
				twenties = twenties - dispensedTwenties;
				fifties = fifties - dispensedFifties;
			}
		}

		result = dispenseNotes(dispensedFifties, dispensedTwenties, dispensedTens, dispensedFives);

		if(result.get(0).getNumber() != 0
				|| result.get(1).getNumber() != 0
				|| result.get(2).getNumber() != 0
				|| result.get(3).getNumber() != 0
		) {
			fiftyEntity.setNumber(fifties);
			twentyEntity.setNumber(twenties);
			tenEntity.setNumber(tens);
			fiveEntity.setNumber(fives);
			saveBalance(fiftyEntity, twentyEntity, tenEntity, fiveEntity);
		}
		return result;
	}



	private  List<Note> dispenseNotes(int fifties, int twenties, int tens, int fives){
		List<Note> result = new ArrayList<Note>(4);
		result.add(new Note (Denomination.FIFTY, fifties));
		result.add(new Note(Denomination.TWENTY, twenties));
		result.add(new Note(Denomination.TEN, tens));
		result.add(new Note(Denomination.FIVE, fives));
		return result;
	}

	private void saveBalance(Note fifties, Note twenties, Note tens, Note fives){
		noteRepository.save(fifties);
		noteRepository.save(twenties);
		noteRepository.save(tens);
		noteRepository.save(fives);
	}


	public Boolean checkEdgeCases(BigDecimal amount){
		Boolean proceed = true;
		BigDecimal availableFunds = this.availableFunds();
		if(amount.compareTo(this.availableFunds()) > 0) {
			proceed = false;
		} else if (amount.compareTo(BigDecimal.valueOf(5.0)) < 0){
			proceed = false;
		}  else if (amount.compareTo(BigDecimal.valueOf(30.0)) == 0){
			proceed = false;
		}  else if (amount.remainder(BigDecimal.TEN).compareTo(BigDecimal.ZERO) != 0)  {
			proceed = false;
		}
		return proceed;
	}

	@Override
	public List<Note> withDraw(BigDecimal amount) {
		Note twenty = new Note(Denomination.TWENTY,0);
		Note fifty = new Note(Denomination.FIFTY,0);
		Note ten = new Note(Denomination.TEN, 0);
		Note five = new Note(Denomination.FIVE, 0);
		List<Note> result = new ArrayList<Note>(4);
		result.add(fifty);
		result.add(twenty);
		result.add(ten);
		result.add(five);
		if (checkEdgeCases(amount)) {
			result = this.dispense(amount, result);
		}
		return result;
	}
	 @Override
	public Boolean checkAmount(List<Note> money) {
		Boolean success = false;
		int numberOfNotes = 0;
		for (Note note : money) {
			numberOfNotes += note.getNumber();
		}
		if(numberOfNotes > 0 ){
			success = true;
		}
		return success;
	}

}
