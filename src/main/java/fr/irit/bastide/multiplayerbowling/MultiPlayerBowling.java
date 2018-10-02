package fr.irit.bastide.multiplayerbowling;

import bowling.Frame;
import bowling.MultiPlayerGame;
import bowling.SinglePlayerGame;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MultiPlayerBowling implements MultiPlayerGame {

	private static final String DISPLAY  = "Prochain tir : joueur %s, tour n° %d, boule n° %d";
	private static final String FINISHED = "Partie terminée";	
	private final Map<String, SinglePlayerGame> games = new LinkedHashMap<>();
	private Iterator<String> playerIterator;
	private String currentPlayerName;
	private SinglePlayerGame currentGame;
	private boolean gameIsRunning = false;

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
		if (currentGame.getCurrentFrame().isFinished()) {
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
			if (currentGame.getCurrentFrame().getFrameNumber() == 10) { // On est au dernier tour
				return false;
			} else { // On démarre un nouveau tour
				playerIterator = games.keySet().iterator();
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
			Frame currentFrame = currentGame.getCurrentFrame();
			int tour = currentFrame.getFrameNumber();
			int ball = currentFrame.getBallsThrown() + 1;
			return String.format(DISPLAY, currentPlayerName, tour, ball);
		}
	}

	@Override
	public int scoreFor(String playerName) throws Exception {
		SinglePlayerGame game = games.get(playerName);
		
		if (game == null)
			throw new Exception("Unknown Player");
		
		return game.score();
	}

}
