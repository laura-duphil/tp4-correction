package fr.irit.bastide.multiplayerbowling;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MultiPlayerBowlingTest {
	private MultiPlayerBowling multiGame;
	
	@Before
	public void setUp() {
		multiGame = new MultiPlayerBowling();
	}
	
	@Test (expected = Exception.class)
	public void needOnePlayer() throws Exception {
		String[] playerNames = {};
		String result = multiGame.startNewGame(playerNames);
	}

	@Test
	public void goodStartMessage() throws Exception {
		String[] playerNames = {"Zorglub", "Albator"};
		String result = multiGame.startNewGame(playerNames);
		assertEquals("Prochain tir : joueur Zorglub, tour n° 1, boule n° 1", result);
	}
	
	@Test
	public void testLancerStrike() throws Exception {
		String[] playerNames = {"Zorglub", "Albator"};
		multiGame.startNewGame(playerNames);
		String result = rollStrike();
		assertEquals("Prochain tir : joueur Albator, tour n° 1, boule n° 1", result);
	}

	@Test( expected = Exception.class )
	public void testGameFinishes() throws Exception {
		String[] playerNames = {"Zorglub", "Albator"};
		multiGame.startNewGame(playerNames);
		// Tout dans la rigole : n joueurs * 10 tours * 2 boules par tour
		String message = rollMany(playerNames.length * 10 * 2, 0);
		assertEquals("Partie terminée", message);
		// Un lancer de trop doit lever une exception
		multiGame.lancer(0);
	}
	
	@Test( expected = Exception.class )
	public void scoreForUnknownPlayer() throws Exception {
		String[] playerNames = {"Zorglub", "Albator"};
		multiGame.startNewGame(playerNames);
		int score = multiGame.scoreFor("Unknown");
	}

	@Test
	public void testRookieVersusChampion() throws Exception {
		String[] playerNames = {"Rookie", "Champion"};
		multiGame.startNewGame(playerNames);
		for (int tour = 0; tour <10; tour ++) {
			// Le rookie fait deux rigoles
			multiGame.lancer(0);			
			multiGame.lancer(0);			
			// Le champion fait un strike
			multiGame.lancer(10);
		}
		// Le champion a droit à deux boules de plus
		multiGame.lancer(10);
		multiGame.lancer(10);	
		assertEquals(  0, multiGame.scoreFor("Rookie"));
		assertEquals(300, multiGame.scoreFor("Champion"));
	}

	// Quelques methodes utilitaires pour faciliter l'écriture des tests
	private String rollMany(int n, int pins) throws Exception {
		String result = "";
		for (int i = 0; i < n; i++) {
			result= multiGame.lancer(pins);
		}
		return result;
	}

	private String rollSpare() throws Exception {
		multiGame.lancer(7);
		return multiGame.lancer(3);
	}

	private String rollStrike() throws Exception {
		return multiGame.lancer(10);
	}	
}
