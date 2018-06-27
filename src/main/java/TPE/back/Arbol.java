package TPE.back;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class Arbol {
	public Arbol parent;
	public List<Arbol> kids;
	public List<Point> nodeLines;
	public List<Point> nextLines;
	
	public boolean minimax;
	public int value;
	
	public int chosen = 0;
	public boolean visited = false;
	public int totalSquares;
	
	public Arbol(MyBoard board,List<Point> drawnLines,boolean minimax,Arbol parent,Player p,int k) {
		this.parent = parent;
		kids = new LinkedList<Arbol>();
		nodeLines = drawnLines;
		this.minimax = minimax;
		totalSquares = board.totalSquares();
		nextLines = new LinkedList<Point>();
		if(k!=0) {
			creaHijos(board,p,k);
		} else {
			estimateValue(board,p);
		}
	}
	public void creaHijos(MyBoard board,Player p,int k) {
		if(board.isNotFull()) {
			for(int row = 0; row < board.dim(); row++) {
				for(int column = 0; column < board.dim(row); column++) {
					if(!board.isDrawn(row,column)) {
						nextLines.add(new Point(row,column));
						board.drawLine(row,column);
						if(squareCompleted(board,row,column,p)) {
							creaHijos(board,p,k);
						} else {
							kids.add(new Arbol(board,new LinkedList<Point>(nextLines),!minimax,this,p,k-1));
						}
						undoSquare(board,row,column);
						board.unDrawLine(row, column);
						nextLines.remove(nextLines.size()-1);
					}
				}
			}
		} else {
			if(!nextLines.isEmpty()) {
				kids.add(new Arbol(board,new LinkedList<Point>(nextLines),!minimax,this,p,k-1));
			}
		}
		return;
	}
	private boolean squareCompleted(MyBoard board,int row,int column,Player p) {
		Square square;
		if(minimax) {
			square = new Square(p);
		} else {
			square = new Square(new User("anotherPlayer",-1,board));
		}
		boolean squareComplete = false;
		if(row%2 == 0) {
			if(board.squareDown(row, column)) {
				board.addSquare(square);
				squareComplete = true;
			}
			if(board.squareUp(row, column)) {
				board.addSquare(square);
				squareComplete = true;
			}
		} else {
			if(board.squareRight(row, column)) {
				board.addSquare(square);
				squareComplete = true;
			}
			if(board.squareLeft(row, column)) {
				board.addSquare(square);
				squareComplete = true;
			}
		}
		return squareComplete;
	}
	private void undoSquare(MyBoard board,int row,int column) {
		if(row%2 == 0) {
			if(board.squareUp(row, column)) {
				board.undoSquare();
			}
			if(board.squareDown(row, column)) {
				board.undoSquare();
			}
		} else {
			if(board.squareLeft(row, column)) {
				board.undoSquare();
			}
			if(board.squareRight(row, column)) {
				board.undoSquare();
			}
		}
		return;
	}
	private void estimateValue(MyBoard board,Player p) {
		value = board.standings(p);
		return;
	}
	public void time(MyBoard board,Player p,int k) {
		Queue<Arbol> q = new LinkedList<Arbol>();
		q.offer(this);
		System.out.println("START HERE");
		int level = 0;
		boolean levels = true;
		long start = System.currentTimeMillis()/1000;
		while(!q.isEmpty() && System.currentTimeMillis()/1000 - start < k) {
			Arbol arbol = q.poll();
			if(levels && !arbol.minimax || !levels && arbol.minimax) {
				System.out.println("Level: " + ++level + " ");
				levels = arbol.minimax;
			}
			System.out.println(arbol);
			Arbol aux = arbol;
			Stack<Arbol> s = new Stack<Arbol>();
			while(aux != null) {
				s.push(aux);
				aux = aux.parent;
			}
			while(!s.isEmpty()) {
				aux = s.pop();
				for(Point point:aux.nodeLines) {
					board.drawLine(point.x,point.y);
					squareCompleted(board,point.x,point.y,p);
				}
			}
			arbol.creaHijos(board,p,1);
			for(Arbol a:arbol.kids) {
				q.offer(a);
			}
			aux = arbol;
			while(aux != null) {
				Stack<Point> sp = new Stack<Point>();
				for(Point point:aux.nodeLines) {
					sp.push(point);					
				}
				while(!sp.isEmpty()) {
					Point point = sp.pop();
					board.unDrawLine(point.x,point.y);
					undoSquare(board,point.x,point.y);
				}
				aux = aux.parent;
			}
		}
		return;
	}
	public Arbol Prune(Arbol a,int alpha,int beta) {
		a.visited = true;
		if(a.kids.isEmpty()) {
			return a;
		}
		Arbol found = null;
		if(a.minimax) {
			int max = -totalSquares;
			for(Arbol arbol:a.kids) {
				Arbol aux = Prune(arbol,alpha,beta);
				if(aux.value > max) {
					max = aux.value;
					found = aux;
				}
				alpha = Math.max(alpha,max);
				if(beta <= alpha) {
					return found;
				}
			}
		} else {
			int min = totalSquares;
			for(Arbol arbol:a.kids) {
				Arbol aux = Prune(arbol,alpha,beta);
				if(aux.value < min) {
					min = aux.value;
					found = aux;
				}
				beta = Math.min(beta,min);
				if(beta <= alpha) {
					return found;
				}
			}
		}
		return found;
	}
	public Arbol notPrune(Arbol a) {
		if(a.kids.isEmpty()) {
			return a;
		}
		Arbol found = notPrune(a.kids.get(0));
		for(int i=1;i<a.kids.size();i++) {
			Arbol aux = notPrune(a.kids.get(i));
			if(aux.value == found.value) {
				Random rand = new Random();
				if(0 == rand.nextInt(10)) {
					found = aux;
				}
			} else {
				if((a.minimax && aux.value > found.value) || (!a.minimax && aux.value < found.value)) {
					found = aux;
				}
			}
		}
		return found;
	}
	private String points(List<Point> list) {
		StringBuilder s = new StringBuilder();
		for(Point p:list) {
			s.append("(" + p.x + "," + p.y + ")");
		}
		return s.toString();
	}
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("[");
		s.append(points(nodeLines));
		s.append("] ");
		s.append(value);
		return s.toString();
	}
}

