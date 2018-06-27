package TPE.back;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class MyBoard {
	private List<List<MyLine>> board;
	private Stack<Undo> stack;
	private Stack<Square> squares; 
	private Arbol treeMove;
	public MyBoard(int dim) {
		//Inicializa el tablero y sus attributos
		board = new LinkedList<List<MyLine>>();
		for(int i = 0; i < (2*dim)-1; i++) {
			board.add(new LinkedList<MyLine>());
		}
		stack = new Stack<Undo>();
		squares = new Stack<Square>();
		treeMove = null;
	}
	public int dim() {
		//Devuelve la cantidad de filas
		return board.size();
	}
	public int dim(int row) {
		//Devuelve la cantidad de columnas en esa fila
		return board.get(row).size();
	}
	public void add(MyLine line,int row) {
		//Agrega la linea al tablero
		board.get(row).add(line);
		return;
	}
	public void drawLine(int row, int column) {
		//Pinta la linea
		if(outOfBounds(row,column)) {
			return;
		}
		board.get(row).get(column).drawLine();
		return;
	}
	public void unDrawLine(int row,int column) {
		//Despinta la linea
		if(outOfBounds(row,column)) {
			return;
		}
		board.get(row).get(column).unDrawLine();
		return;
	}
	public boolean isDrawn(int row, int column) {
		//Se fija si la linea esta pintada
		if(outOfBounds(row,column)) {
			return false;
		}
		return board.get(row).get(column).isDraw();
	}
	public boolean isNotFull() {
		//Se fija si el tablero todavia tiene lineas para pintar
		for(int row = 0; row < board.size(); row++) {
			for(int column = 0; column < board.get(row).size(); column++) {
				if(!board.get(row).get(column).isDraw()) {
					return true;
				}
			}
		}
		return false;
	}
	public void move(List<Point> point,int turn) {
		//Crea el ultimo movimiento hecho por el jugador y lo agrega al stack
		if(point != null) {
			Undo undo = new Undo(turn);
			//Guarda todos las lineas
			for(Point p:point) {
				if(!isDrawn((int)p.getX(),(int)p.getY())) {
					undo.addPoint(p);
				}
			}
			stack.push(undo);
		}
		return;
	}
	public List<Point> lastMove() {
		//Devuelve el ultimo movimiento hecho sin deshacerlo
		if(stack.isEmpty()) {
			return null;
		}
		return stack.peek().points();
	}
	public Undo undoLastMove() {
		//Remueve el ultimo movimiento hecho y lo devuelve
		if(!stack.isEmpty()) {
			return stack.pop(); 
		}
		return null;
	}
	public void addSquare(Square square) {
		//Agrega el cuadrado al stack
		squares.push(square);
		return;
	}
	public int totalSquares() {
		//Devuelve la cantidad de cuadrados en el tablero
		return dim(0)*dim(0);
	}
	public Square undoSquare() {
		//Devuelve el ultimo cuadrado hecho
		if(squares.isEmpty()) {
			return null;
		}
		return squares.pop();
	}
	public boolean outOfBounds(int row,int column) {
		//Se fija si la linea pertenece al dominio del tablero
		if(row < 0 || row >= board.size()) {
			System.out.println("ROW OUT OF BOUNDS:");
			System.out.println("Min: 0" + "Max: " + board.size() + "But row is: " + row);
			return true;
		}
		if(column < 0 || column >= board.get(row).size()) {
			System.out.println("COLUMN OUT OF BOUNDS:");
			System.out.println("Min: 0" + "Max: " + board.get(row).size() + "But column is: " + row);
			return true;
		}
		return false;
	}
	public int squareUpCant(int row,int column) {
		if(!outOfBounds(row,column)) {
			//Suma la cantidad de lineas que hay pintadas en el cuadrado de arriba
			int total = 0;
			if(row == 0) {
				return total;
			}
			if(board.get(row-1).get(column).isDraw()) {
				total++;
			}
			if(board.get(row-1).get(column+1).isDraw()) {
				total++;
			}
			if(board.get(row-2).get(column).isDraw()) {
				total++;
			}
			return total;
		}
		return -1;
	}
	public int squareLeftCant(int row,int column) {
		//Suma la cantidad de lineas que hay pintadas en el cuadrado de la izquierda
		if(!outOfBounds(row,column)) {
			int total = 0;
			if(column == 0) {
				return total;
			}
			if(board.get(row-1).get(column-1).isDraw()) {
				total++;
			}
			if(board.get(row+1).get(column-1).isDraw()) {
				total++;
			} 
			if(board.get(row).get(column-1).isDraw()) {
				total++;
			}
			return total;
		}
		return -1;
	}
	public int squareDownCant(int row,int column) {
		if(!outOfBounds(row,column)) {
			//Suma la cantidad de lineas que hay pintadas en el cuadrado de abajo
			int total = 0;
			if(row == board.size() -1) {
				return total;
			}
			if(board.get(row+1).get(column).isDraw()) {
				total++;
			}
			if(board.get(row+1).get(column+1).isDraw()) {
				total++;
			}
			if(board.get(row+2).get(column).isDraw()) {
				total++;
			}
			return total;
		}
		return -1;
	}
	public int squareRightCant(int row,int column) {
		if(!outOfBounds(row,column)) {
			//Suma la cantidad de lineas que hay pintadas en el cuadrado de la derecha
			int total = 0;
			if(column == board.get(row).size() - 1) {
				return total;
			}
			if(board.get(row-1).get(column).isDraw()) {
				total++;
			}
			if(board.get(row+1).get(column).isDraw()) {
				total++;
			}
			if(board.get(row).get(column+1).isDraw()) {
				total++;
			}
			return total;
		}
		return -1;
	}
	public boolean squareUp(int row,int column) {
		//Si las otras 3 lineas del cuadrado de arriba no estan pintadas, devuelve false
		if(squareUpCant(row,column) != 3) {
			return false;
		}
		return true;
	}
	public boolean squareLeft(int row,int column) {
		//Si las otras 3 lineas del cuadrado de la izquierda no estan pintadas, devuelve false
		if(squareLeftCant(row,column) != 3) {
			return false;
		}
		return true;
	}
	public boolean squareDown(int row,int column) {
		//Si las otras 3 lineas del cuadrado de abajo no estan pintadas, devuelve false
		if(squareDownCant(row,column) != 3) {
			return false;
		}
		return true;
	}
	public boolean squareRight(int row,int column) {
		//Si las otras 3 lineas del cuadrado de la derecha no estan pintadas, devuelve false
		if(squareRightCant(row,column) != 3) {
			return false;
		}
		return true;
	}
	public void setTreeMove(Arbol arbol) {
		//Setea el Arbol de movimientos del AI
		treeMove = arbol;
		return;
	}
	public Arbol TreeMove() {
		//Devuelve el ultimo Arbol de movimientos del AI
		return treeMove;
	}
	public int standings(Player p) {
		//Devuelve cuantos cuadrados a favor o encontra tiene el jugador en el momento
		int total = 0;
		List<Square> list = new LinkedList<Square>();
		list.addAll(squares);
		for(Square square:list) {
			//Si coiciden los nombres suma uno
			if(square.player().name().equals(p.name())) {
				total++;
			//Si no resta uno
			} else {
				total--;
			}
		}
		return total;
	}
}
