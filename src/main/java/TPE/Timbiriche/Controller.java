package TPE.Timbiriche;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import TPE.back.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class Controller {
	private MyBoard board;
	private List<Player> players;
	private int turn;
	
	private final String text1 = "Turn: Player 1";
	private final String text2 = "Turn: Player 2";
	private final String hint = "Please do a MouseClick so AI can move";
	
	private GridPane grid;
	private List<Text> turnText;
	public Controller(MyBoard board,GridPane grid) {
		this.board = board;
		players = new LinkedList<Player>();
		this.grid = grid;
		turnText = new LinkedList<Text>();
		turnText.add(new Text(text1));
		turnText.add(new Text(""));
	}
	public void addPlayer(Player p) {
		//Agrega jugadores
		players.add(p);
		if(players.size() == 2) {
			for(Player player:players) {
				if(!(player instanceof AI)) {
					return;
				}
			}
			turnText.get(1).setText("Do MouseClicks for each AI player");
		}
		return;
	}
	public Player getPlayer(int turn) {
		return players.get(turn);
	}
	public List<Text> text() {
		return turnText;
	}
	public void setTurn(int turn) {
		this.turn = turn;
	}
	public int getTurn() {
		return turn;
	}
	public void setTurnText() {
		//En caso de undo, cambia el texto que ve el usuario para saber a quien le toca
		if(turn == 0) {
			if(players.get(turn) instanceof AI) {
				turnText.get(0).setText(text1);
				turnText.get(1).setText(hint);
			} else {
				turnText.get(0).setText(text1);
				turnText.get(1).setText("");
			}
		} else {
			if(players.get(turn) instanceof AI) {
				turnText.get(0).setText(text2);
				turnText.get(1).setText(hint);
			} else {
				turnText.get(0).setText(text2);
				turnText.get(1).setText("");
			}
		}
		return;
	}
	private void otherPlayersTurn() {
		//Cambia el turno del jugador
		if(turn == 0) {
			turnText.get(0).setText(text2);
			turnText.get(1).setText("");
			turn = 1;
		} else {
			turnText.get(0).setText(text1);
			turnText.get(1).setText("");
			turn = 0;
		}
		return;
	}
	private void squareComplete(int curRow,int curColumn,Player p) {
		boolean completed = false;
		//si la linea es horizontal
		if(curRow%2 == 0) {
			//Si completo el cuadrado de arriba, lo agrega
			if(board.squareUp(curRow, curColumn)) {
				Square t = new Square(p);
				grid.add(t,(2*curColumn)+1,curRow-1);
				board.addSquare(t);
				completed = true;
			}
			//Si completo el cuadrado de abajo, lo agrega
			if(board.squareDown(curRow, curColumn)) {
				Square t = new Square(p);
				grid.add(t,(2*curColumn)+1,curRow+1);
				board.addSquare(t);
				completed = true;
			}
		//Si la linea es vertical
		} else {
			//Si completo el cuadrado de la izquierda, lo agrega
			if(board.squareLeft(curRow, curColumn)) {
				Square t = new Square(p);
				grid.add(t,(2*curColumn)-1,curRow);
				board.addSquare(t);
				completed = true;
			}
			//Si completo el cuadrado de la derecha, lo agrega
			if(board.squareRight(curRow, curColumn)) {
				Square t = new Square(p);
				grid.add(t,(2*curColumn)+1,curRow);
				board.addSquare(t);
				completed = true;
			}
		}
		//Pinta la linia
		board.drawLine(curRow, curColumn);
		//Si no completo ningun cuadrado, pasa el turno al otro jugador
		if(!completed) {
			otherPlayersTurn();
		}
		return;
	}
	private void endGame() {
		//Abre una nueva ventana diciendo quien gano
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		String title = new String();
		String text = new String();
		//Cuenta los cuadrados para ver quien gano
		int p1 = board.standings(players.get(0));
		if(p1 > 0) {
			if(players.get(0) instanceof AI) {
				title = "Game Over";
			} else {
				title = "Congratulation";
			}
			text = "Player 1 wins!";
		} 
		if(p1 < 0) {
			if(players.get(1) instanceof AI) {
				title = "Game Over";
			} else {
				title = "Congratulation";
			}
			text = "Player 2 wins!";
		} 
		if(p1 == 0) {
			title = "Draw";
			text = "Player 1 and Player 2 have the same amount of Squares";
		}
		alert.setTitle(title);
		alert.setHeaderText(text);
		alert.setContentText("Press 'Aceptar' to close the game.");
		alert.showAndWait();
		//termina el juego
		Platform.exit();
	}
	public EventHandler<MouseEvent> interact() {
		EventHandler<MouseEvent> event = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e)  {				
				if(players.get(turn).move()) {					
					for(Point p:board.lastMove()) {
						//Si algunas de las lineas completo algun cuadrado
						squareComplete((int)p.getX(),((int)p.getY()),players.get(turn));
					}
				}
				//Si se completo el tablero con la ultima jugada, termina el juego
				if(!board.isNotFull()) {
					endGame();
				}
			}
		};
		return event;
	}
	
}

