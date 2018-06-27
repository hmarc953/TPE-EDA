package TPE.back;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

public class Undo {
	//Guarda los puntos del movimiento y el turno del jugador que hizo el movimiento
	private List<Point> points;
	private int turn;
	public Undo(int t) {
		turn = t;
		points = new LinkedList<Point>();
	}
	public void addPoint(Point p) {
		points.add(p);
	}
	public List<Point> points() {
		return points;
	}
	public int turn() {
		return turn;
	}
}
