package TPE.Timbiriche;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Queue;

import TPE.back.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class App extends Application {
	private GridPane grid;
	private static int dim;
	private static int playersNumber;
	private static boolean prune;
	private static String mode;
	private static int k;
	private Controller control;
	
	private MyBoard board;
	
	private int row;
	private int column;
	
	private Button standings() {
		//Crea un boton para que el usuario pueda saber como va la partida
		Button squares = new Button("Standings");
		squares.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Standings");
				int player1Squares = board.standings(control.getPlayer(0));
				if(player1Squares < 0) {
					player1Squares = 0;
				}
				int player2Squares = board.standings(control.getPlayer(1));
				if(player2Squares < 0) {
					player2Squares = 0;
				}
				//Imprime la cantidad de cuadrados que tiene cada jugador en el momento
				alert.setHeaderText("Player 1: " + player1Squares + " squares" + "\n" + "Player 2: " + player2Squares + " squares");
				alert.setContentText("Press 'Aceptar' to resume the game.");
				alert.showAndWait();
			}
		});
		return squares;
	}
	private class QArbol {
		//clase privada para hacer la cola en el metodo printDot
		int id;
		Arbol arbol;
		QArbol(int id, Arbol a) {
			this.id = id;
			arbol = a;
		}
	}
	private void printDot(Arbol arbol) {
		//Se fija si es posible hacer un archivo
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("Timbiriche" + ".dot", "UTF-8");
		} catch (FileNotFoundException e) {
		    System.out.println("Creando archivo.");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Encoding no soportado.");
		}
		if(writer == null){
			System.out.println("No se pudo hacer el archivo .dot");
			return;
		}
		//Escribe en el archivo los nodos, y despues los une
		String max = new String(" [shape=box, height=0.18, fontsize=12, label=\"");
		String min = new String(" [height=0.18, fontsize=12, label=\"");
		String chosen = new String("\",style=filled, color=orange];");
		String poda = new String("\", style=filled, color=peachpuff4];");
		String labelend = new String("\"];");
		String arrow = new String(" -> ");
		writer.println("digraph Timbiriche {");
		
		//Una cola para imprimir todos los nodos, y poder unir el padre con sus hijos
		Queue<QArbol> q = new LinkedList<QArbol>();
		QArbol qAr = new QArbol(0,arbol);
		writer.println(qAr.id + max + "START " + qAr.arbol.value + chosen);
		q.offer(qAr);
		int next = 1;
		while(!q.isEmpty()) {
			QArbol qa = q.poll();
			if(!qa.arbol.kids.isEmpty()) {
				int i = next;
				for(Arbol a:qa.arbol.kids) {
					if(a.minimax) {
						writer.print(i + max + a.toString());
					} else {
						writer.print(i + min + a.toString());
					}
					if(a.chosen == 1) {
						writer.println(chosen);
					} else if(!a.visited) {
						writer.println(poda);
					} else {						
						writer.println(labelend);
					}
					if(a.visited) {
						q.offer(new QArbol(i,a));
					}
					i++;
				}
				writer.print(qa.id + arrow + "{");
				for(int j = next;j<i;j++) {
					writer.print(j);
					if(j!=i-1) {
						writer.print(",");
					}
				}
				writer.println("};");
				next = i;	
			}
		}
		writer.println("}");
		writer.close();
		System.out.println("El Archivo 'Timbiriche.dot' creado con exito");
		return;
	}
	private Button dotButton() {
		//Crea el boton generador del dot
		Button dotButton = new Button("DOT");
		dotButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(board.TreeMove() != null) {
					//Genera el archivo dot con el ultimo algoritmo minimax
					printDot(board.TreeMove());
				}
				return;
			}
		});
		return dotButton;
	}
	private void undoSquare(int curRow,int curColumn) {
		//Si la linea es horizontal
		if(curRow%2 == 0) {
			//Deshace el cuadrado de abajo, si hay uno
			if(board.squareDown(curRow, curColumn)) {
				Square square = board.undoSquare();
				if(square !=  null) {
					grid.getChildren().remove(square);
				}
			}
			//Deshace el cuadrado de arriba, si hay uno
			if(board.squareUp(curRow, curColumn)) {
				Square square = board.undoSquare();
				if(square !=  null) {
					grid.getChildren().remove(square);
				}
			}
		//SI la linea es vertical
		} else {
			//Deshace el cuadrado de la derecha, si hay uno
			if(board.squareRight(curRow, curColumn)) {
				Square square = board.undoSquare();
				if(square !=  null) {
					grid.getChildren().remove(square);
				}
			}
			//Deshace el cuadrado de la izquierda, si hay uno
			if(board.squareLeft(curRow, curColumn)) {
				Square square = board.undoSquare();
				if(square !=  null) {
					grid.getChildren().remove(square);
				}
			}
		}
	}
	private Button undoButton() {
		//Crea el boton undo
		Button buttonUndo = new Button("Undo");
		buttonUndo.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				//Deshace el ultimo movimiento registrado en el tablero
				Undo undo = board.undoLastMove();
				if(undo != null) {	
					//Cambia el turno si solo se hizo un movimiento
					control.setTurn(undo.turn());
					control.setTurnText();
					//Despinta todas las lineas pintadas
					for(int i=0;i<undo.points().size();i++) {
						Point p = undo.points().get(undo.points().size()-1-i);
						undoSquare((int)p.getX(),(int)p.getY());
						board.unDrawLine((int)p.getX(),(int)p.getY());
					}
				}
			}
		});
		return buttonUndo;
	}
	private HBox buttonBox() {
		//Inicializa la interfaz grafica de los buttones
		HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(5, 5, 5, 5));
        //Agruega los buttones
        buttonBox.getChildren().addAll(undoButton(), dotButton(),standings());
        return buttonBox;
	}
	private void ai() {
		//diferentes formas de interactuar con la interfaz dependiendo de que jugadores juegan
		if(playersNumber == 0 || playersNumber == 3) {
			grid.setOnMouseClicked(control.interact());
		} else {	
			grid.setOnMousePressed(control.interact());
			grid.setOnMouseReleased(control.interact());
		} 
		return;
	}
	private void lines() {
		//Inicializa las lineas y las agregua a la interfaz grafica y al tablero
		for(row = 0; row < board.dim() ; row++) {
			for(column = 0; column < dim-((row+1)%2) ; column++) {
				MyLine line = new MyLine(row%2 == 0);
				line.setOnMousePressed(new EventHandler<MouseEvent>() {
					private int lineRow = row;
					private int lineColumn = column;
					@Override
					public void handle(MouseEvent event) {
						if(control.getTurn() == 0) {
							if(control.getPlayer(0) instanceof User) {
								//Metodo para guardar la linea elegida por el usuario
								((User) control.getPlayer(0)).setLine(lineRow,lineColumn);
							}
						} else {
							if(control.getPlayer(1) instanceof User) {
								//Metodo para guardar la linea elegida por el usuario
								((User) control.getPlayer(1)).setLine(lineRow,lineColumn);
							}
						}
					}
				});
				grid.add(line, (2*column)+(row+1)%2, row);
				board.add(line, row);
			}
		}
		return;
	}
	private void dots() {
		//Inicializa los puntos y los agrega a la interfaz grafica
		for(int rowCircle = 0; rowCircle < dim*2; rowCircle=rowCircle+2) {
        	for(int columnCircle = 0; columnCircle < dim*2; columnCircle=columnCircle+2) {
        		Circle point = new Circle();
        		point.setRadius(5);
        		grid.add(point, columnCircle, rowCircle);
        	}
        }
		return;
	}
	private void createController() {
		control = new Controller(board,grid);
		//Las Distintas formas de jugar
		switch(playersNumber) {
		case 0:
			control.addPlayer(new User(" P1",0,board));
			control.addPlayer(new User(" P2",1,board));
			return;
		case 1:
			control.addPlayer(new AI(" AI",0,board,mode,k,prune));
			control.addPlayer(new User(" P1",1,board));
			return;
		case 2:
			control.addPlayer(new User(" P1",0,board));
			control.addPlayer(new AI(" AI",1,board,mode,k,prune));
			return;
		case 3: 
			control.addPlayer(new AI(" A1",0,board,mode,k,prune));
			control.addPlayer(new AI(" A2",1,board,mode,k,prune));
			return;
		}
		return;
	}
	private void inicialize() {
		grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setPadding(new Insets(20,20,20,20));
        
        //Crea el tablero
        board = new MyBoard(dim);
        
        //Crea el controlador
        createController();
        
        //Define el metodo de juego
        ai();
        
        //Crea los puntos en el tablero
        dots();
        
        //Crea las lineas de tablero
        lines();
        return;
	}
    @Override
    public void start(Stage primaryStage) {
    	primaryStage.setTitle("Timbiriche");
    	
    	//Inicializa la interfaz grafica y los jugadores
    	inicialize();      
    	
    	//Junta el tablero con los buttones
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(0,25,0,0));
        
        //Texto para que el usuario vea de quien es el turno
        vBox.getChildren().addAll(control.text());
        
        //El tablero
        vBox.getChildren().add(grid);
        
        //Los buttones
        vBox.getChildren().add(buttonBox());
        
        //Crea la escena
        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
    	primaryStage.show();
    }
    public static void main(String[] args) {
         //Inicializa los parametros dados
    	for(int i=0; i < args.length;i++) {
        	if(args[i].equals("-size")) {
        		dim = Integer.parseInt(args[++i]);
        	} else if(args[i].equals("-mode")) {
        		mode = args[++i];
        	} else if(args[i].equals("-ai")) {
        		playersNumber = Integer.parseInt(args[++i]);
        	} else if(args[i].equals("-param")) {
        		k = Integer.parseInt(args[++i]);
        	} else if(args[i].equals("-prune")) {
        		if(args[++i].equals("on")) {
        				prune = true;
        			} else {
        				prune = false;
        			}
        	}
        }
		launch(args);
    }
}
