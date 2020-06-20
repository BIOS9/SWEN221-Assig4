package swen221.cards.util;

import sun.awt.AWTAccessor;
import sun.security.ssl.HandshakeInStream;
import swen221.cards.core.Card;
import swen221.cards.core.Hand;
import swen221.cards.core.Player;
import swen221.cards.core.Trick;

import javax.swing.*;
import java.util.List;

/**
 * Implements a simple computer player who plays the highest card available when
 * the trick can still be won, otherwise discards the lowest card available. In
 * the special case that the player must win the trick (i.e. this is the last
 * card in the trick), then the player conservatively plays the least card
 * needed to win.
 * 
 * @author David J. Pearce
 * 
 */
public class SimpleComputerPlayer extends AbstractComputerPlayer {

	public SimpleComputerPlayer(Player player) {
		super(player);
	}

	@Override
	public Card getNextCard(Trick trick) {

		// Get lead suit
		Card.Suit leadSuit = null;
		Card leadCard = trick.getCardPlayed(trick.getLeadPlayer());
		if(leadCard != null) // Lead card has been played, get the suit
			leadSuit = leadCard.suit();

		Card.Suit trumps = trick.getTrumps();
		Hand hand = super.player.getHand();
		List<Card> played= trick.getCardsPlayed();
		boolean isLastCard = trick.getCardsPlayed().size() == 3;

		if(leadSuit == null) { // Lead card has not been played yet, lead with highest card
			// No trumps
			if (trumps == null)
				return getHighestCard(hand);

			// Trumps, and a valid trump card is available
			if (containsSuit(hand, trumps))
				return getHighestCard(hand, trumps);

			 // Trumps, but no card available
			return getHighestCard(hand);
		}

		// Check if the AI can win the trick/has a valid card

		// Check if trump card can be played
		if(trumps != null && (getHighestCard(played, leadSuit).rank() == Card.Rank.ACE || containsSuit(played, trumps))) {
			if(containsSuit(hand, trumps)) {
				Card highest;
				Card highestPlayed = getHighestCard(played, trumps);

				if(isLastCard)
					highest = getNextHighestCard(hand, highestPlayed, trumps, trumps);
				else
					highest = getHighestCard(hand, trumps);

				if(highestPlayed == null || highest.compareTo(highestPlayed) > 0)
					return highest;
			} else {
				boolean trumpPlayed = containsSuit(played, trumps);
				if(trumpPlayed) {
					if(containsOtherSuit(hand, trumps))
						return getLowestCardExcluding(hand, trumps);
					else
						return getLowestCard(hand);
				}
			}
		}

		// Check if player can follow lead suit
		if(containsSuit(hand, leadSuit)) {
			Card highestPlayed = getHighestCard(played, leadSuit);
			Card highest;
			if(isLastCard)
				highest = getNextHighestCard(hand, highestPlayed, leadSuit, trumps);
			else
				highest = getHighestCard(hand, leadSuit);


			if(highest.compareTo(highestPlayed) > 0)
				return highest;
			else
				return getLowestCard(hand, leadSuit);
		} else {
			Card highestPlayed;
			if(containsSuit(played, trumps))
				highestPlayed = getHighestCard(played, trumps);
			else
				highestPlayed = getHighestCard(played, leadSuit);
			Card highest;

			if(isLastCard)
				highest = getNextHighestCard(hand, highestPlayed, leadSuit, trumps);
			else
				highest = getHighestCard(hand, trumps);

			if(highest != null)
				return highest;
		}

		// Discord lowest value card in hand
		if(trumps != null && containsOtherSuit(hand, trumps))
			return getLowestCardExcluding(hand, trumps);
		else
			return getLowestCard(hand);
	}

	/**
	 * Checks if a hand contains any card with the specified suit
	 * @param hand
	 * @param suit
	 * @return
	 */
	private boolean containsSuit(Iterable<Card> hand, Card.Suit suit) {
		for(Card c : hand) {
			if(c.suit() == suit)
				return true;
		}

		return false;
	}

	private boolean containsOtherSuit(Iterable<Card> hand, Card.Suit suit) {
		for(Card c : hand) {
			if(c.suit() != suit)
				return true;
		}

		return false;
	}

	/**
	 * Returns the card in the hand that matches the suit and has the highest value
	 * If the hand contains no cards with the suit, null is returned.
	 * @param hand
	 * @param suit
	 * @return
	 */
	private Card getHighestCard(Iterable<Card> hand, Card.Suit suit) {
		Card highest = null;
		for(Card c : hand) {
			if(c.suit() == suit) {
				if (highest == null || c.compareTo(highest) > 0)
					highest = c;
			}
		}

		return  highest;
	}

	/**
	 * Gets the highest card in the hand
	 * @param hand
	 * @return
	 */
	private Card getHighestCard(Iterable<Card> hand) {
		Card highest = null;
		for(Card c : hand) {
			if (highest == null || c.rank().ordinal() > highest.rank().ordinal())
				highest = c;
			else if(c.rank() == highest.rank() && c.compareTo(highest) > 0) { // if card ranks are the same, get by highest suit
				highest = c;
			}
		}

		return  highest;
	}

	/**
	 * Returns most conservative card that is higher than the supplied card from the hand.
	 * @param hand
	 * @param card
	 * @return
	 */
	private Card getNextHighestCard(Iterable<Card> hand, Card card, Card.Suit suit, Card.Suit trumps) {
		Card nextHighest = null;

		// Attempt to find non trump card that is higher than card
		if(card.suit() != trumps) { // If card is a trump card, skip this section because cannot be beaten with normal card
			for(Card c : hand) {
				if(c.suit() != suit) continue;
				if(nextHighest == null) { nextHighest = c; continue; }

				if(c.rank().ordinal() > card.rank().ordinal() && c.rank().ordinal() < nextHighest.rank().ordinal())
					nextHighest = c;
			}
		}


		if(nextHighest == null && trumps != null) {
			for(Card c : hand) {
				if(c.suit() != trumps) continue;
				if(nextHighest == null) { nextHighest = c; continue; }

				if(c.rank().ordinal() > card.rank().ordinal()) {
					nextHighest = c;
				}
			}
		}

		return nextHighest;
	}

	/**
	 * Gets lowest card in the hand
	 * @param hand
	 * @return
	 */
	private Card getLowestCard(Iterable<Card> hand) {
		Card lowest = null;
		for(Card c : hand) {
			if (lowest == null || c.compareTo(lowest) < 0) //.rank().ordinal() < lowest.rank().ordinal()
				lowest = c;
		}
		return  lowest;
	}

	/**
	 * Gets the lowest card in the hand that matches the specified suit
	 * @param hand
	 * @param suit
	 * @return
	 */
	private Card getLowestCard(Iterable<Card> hand, Card.Suit suit) {
		Card lowest = null;
		for(Card c : hand) {
			if (c.suit() == suit && (lowest == null || c.compareTo(lowest) < 0)) //.rank().ordinal() < lowest.rank().ordinal()
				lowest = c;
		}
		return  lowest;
	}

	/**
	 * Gets lowest card in the hand that does not match the specified suit
	 * @param hand
	 * @param suit
	 * @return
	 */
	private Card getLowestCardExcluding(Iterable<Card> hand, Card.Suit suit) {
		Card lowest = null;
		for(Card c : hand) {
			if (c.suit() != suit && (lowest == null || c.compareTo(lowest) < 0))//.rank().ordinal() < lowest.rank().ordinal()
				lowest = c;
		}
		return  lowest;
	}
}
