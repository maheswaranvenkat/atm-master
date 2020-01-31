package com.intelizest.atm.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * This is a command object to back the initialisation form
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Money {
	int fives;
	int tens;
	int twenties;
	int fifties;

}
