package swen221.cards.core;

import java.io.Serializable;

public class Card implements Comparable<Card>, Serializable {
	
	/**
	 * Represents a card suit.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public enum Suit {
		HEARTS,
		CLUBS,
		DIAMONDS,
		SPADES;
	}
	
	/**
	 * Represents the different card "numbers".
	 * 
	 * @author David J. Pearce
	 *
	 */
	public enum Rank {
		TWO,
		THREE,
		FOUR,
		FIVE,
		SIX,
		SEVEN,
		EIGHT,
		NINE,
		TEN,
		JACK,
		QUEEN,
		KING,
		ACE;
	}
	
	// =======================================================
	// Card stuff
	// =======================================================
	
	private Suit suit; // HEARTS, CLUBS, DIAMONDS, SPADES
	private Rank rank; // 2 <= number <= 14 (ACE)
	
	/**
	 * Construct a card in the given suit, with a given number
	 * 
	 * @param suit
	 *            --- between 0 (HEARTS) and 3 (SPADES)
	 * @param number
	 *            --- between 2 and 14 (ACE)
	 */
	public Card(Suit suit, Rank number) {				
		this.suit = suit;
		this.rank = number;
	}

	/**
	 * Get the suit of this card, between 0 (HEARTS) and 3 (SPADES).
	 * 
	 * @return
	 */
	public Suit suit() {
		return suit;
	}

	/**
	 * Get the number of this card, between 2 and 14 (ACE).
	 * 
	 * @return
	 */
	public Rank rank() {
		return rank;
	}	
		
	private static String[] suits = { "Hearts","Clubs","Diamonds","Spades"};
	private static String[] ranks = { "2 of ", "3 of ", "4 of ",
			"5 of ", "6 of ", "7 of ", "8 of ", "9 of ", "10 of ", "Jack of ",
			"Queen of ", "King of ", "Ace of " };
	
	public String toString() {
		return ranks[rank.ordinal()] + suits[suit.ordinal()];		
	}

	@Override
	public int compareTo(Card o) {
		if(suit.ordinal() > o.suit.ordinal())
			return 1;
		else if(suit.ordinal() < o.suit.ordinal())
			return -1;
		else {
			if(rank.ordinal() > o.rank.ordinal())
				return 1;
			else if(rank.ordinal() < o.rank.ordinal())
				return -1;
			else
				return 0;
		}
	}

	@Override
	public boolean equals(Object obj) {
		// Ensure object is not null and is a card
		if(obj == null)
			return false;
		if(obj.getClass() != this.getClass())
			return false;

		// Compare data of card object
		Card c = (Card)obj;
		if(c.rank != rank)
			return false;
		if(c.suit != suit)
			return false;

		return true;
	}

	/**
	 * Calculates the has code for this object.
	 * Uses prime numbers to somewhat mitigate collisions
	 * @return
	 */
	public int hashCode() {
		int prime = 199;
		int result = 1;
		result = prime * result + rank.ordinal();
		result = prime * result + suit.ordinal();
		return result;
	}
}
