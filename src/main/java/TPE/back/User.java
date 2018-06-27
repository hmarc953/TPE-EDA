package TPE.back;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

public class User implements Player {
	private final String name;
	private final int TurnOrder;
	private MyBoard board;
	private Point p;
	public User(String name,int t,MyBoard board) {
		this.name = name;
		this.board = board;
		TurnOrder = t;
	}
	public String name() {
		return name;
	}
	public int turn() {
		return TurnOrder;
	}
	public void setLine(int chosenRow, int chosenColumn) {
		//Setea el punto elegido por el usuario
		p = new Point(chosenRow,chosenColumn);
		return;
	}
	public boolean move() {
		if(p!= null) {
			List<Point> myMove = new LinkedList<Point>();
			myMove.add(p);
			//Agrega el movimiento en el tablero
			board.move(myMove,TurnOrder);
			p = null;
			return true;
		}
		return false;
	}
}
