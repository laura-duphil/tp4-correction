package fr.irit.bastide.multiplayerbowling;

import bowling.MultiPlayerGame;
import bowling.SinglePlayerGame;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MultiPlayerBowling implements MultiPlayerGame {

	private static final String DISPLAY  = "Prochain tir : joueur %s, tour n° %d, boule n° %d";
	private static final String FINISHED = "Partie terminée";	
	// Associe chaque joueur à son jeu
	// note : on déclare la variable avec une interface (Map)
	private final Map<String, SinglePlayerGame> games;
	// Permet de parcourir les noms de joueurs
	private Iterator<String> playerIterator;
	// Le nom du joueur courant
	private String currentPlayerName;
	// Le jeu du joueur courant
	private SinglePlayerGame currentGame;
	// Est-ce que le jeu est en cours
	private boolean gameIsRunning = false;
	
	public MultiPlayerBowling() {
		// note : on initialise la variable en choisissant une implémentation (LinkedHashMap)
		games = new LinkedHashMap<>();
		//games = new TreeMap<>(); // Avec TreeMap ça ne marche pas, pourquoi ?
	}

	@Override
	public String startNewGame(String[] playerNames) throws Exception {
		if ((playerNames == null) || playerNames.length == 0) {
			throw new Exception("Need at least one player");
		}

		games.clear(); // On efface le jeu précédent
		
		// On associe à chaque joueur son jeu
		for (String name : playerNames) {
			games.put(name, new SinglePlayerGame());
		}

		// On initialise le premier joueur
		playerIterator = games.keySet().iterator();
		changeToNextPlayer();
		
		// C'est parti !
		gameIsRunning = true;

		return displayMessage();
	}

	@Override
	public String lancer(int nombreDeQuillesAbattues) throws Exception {
		if (!gameIsRunning) {
			throw new Exception(FINISHED);
		}
		
		// On enregistre le lancer courant
		currentGame.lancer(nombreDeQuillesAbattues);

		// Si le tour du joueur est terminé
		if (currentGame.isFinished() || currentGame.hasCompletedFrame()) {
			// On passe au joueur suivant
			gameIsRunning = changeToNextPlayer();
		}

		return displayMessage();
	}

	/**
	 * 
	 * @return true si le jeu doit continuer
	 */
	private boolean changeToNextPlayer() {
		if (!playerIterator.hasNext()) { // On a passé tous les joueurs
			if (currentGame.isFinished() ) { // Le dernier joueur a fini
				return false; // Le jeu est terminé
			} else { // On démarre un nouveau tour
				playerIterator = games.keySet().iterator(); // On réinitialise l'itérateur
			}
		}
		currentPlayerName = playerIterator.next();
		currentGame = games.get(currentPlayerName);
		return true;
	}

	/**
	 * 
	 * @return le message à afficher après chaque lancer
	 */
	private String displayMessage() {
		if (!gameIsRunning) {
			return FINISHED;
		} else {
			int tour = currentGame.getFrameNumber();
			int ball = currentGame.getNextBallNumber();
			return String.format(DISPLAY, currentPlayerName, tour, ball);
		}
	}

	@Override
	public int scoreFor(String playerName) throws Exception {
		// On trouve le jeu associé au nom du joueur
		SinglePlayerGame game = games.get(playerName);
		
		if (game == null)
			throw new Exception("Unknown Player");
		
		return game.score();
	}

}
