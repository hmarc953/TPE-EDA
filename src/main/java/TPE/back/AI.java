package TPE.back;

import java.awt.Point;
import java.util.LinkedList;

public class AI implements Player{
	private final String name;
	private final int TurnOrder;
	private MyBoard board;
	private String mode;
	private int k;
	private boolean prune;
	public AI(String name,int t,MyBoard board,String mode,int k,boolean prune) {
		this.name = name;
		TurnOrder = t;
		this.board = board;
		this.mode = mode;
		this.k = k;
	}
	public String name() {
		return name;
	}
	public int turn() {
		return TurnOrder;
	}
	public boolean move() {
		Arbol aux = null;
		Arbol arbol = null;
		if(mode.equals("depth")) {
			//Crea el Arbol con todos los movimientos hasta la profundidad k
			arbol = new Arbol(board,new LinkedList<Point>(),true,null,this,k);
		}
		if(mode.equals("time")) {
			//Crea el Arbol con todos los movimientos hasta que el tiempo se agote
			arbol = new Arbol(board,new LinkedList<Point>(),true,null,this,0);
			arbol.time(board,this,k);
		}
		if(prune) {
			//Algoritmo con poda
			aux = arbol.Prune(arbol,-board.totalSquares(),board.totalSquares());
		} else {
			//Algoritmo sin poda
			aux = arbol.notPrune(arbol);
		}
		if(aux != null) {
			while(aux.parent != arbol) {
				aux.chosen = 1;
				aux = aux.parent;
			}
			aux.chosen = 1;
			board.setTreeMove(arbol);
			board.move(aux.nodeLines,TurnOrder);
			return true;
		}
		return false;
	}
}
