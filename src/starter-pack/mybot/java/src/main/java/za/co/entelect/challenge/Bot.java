package za.co.entelect.challenge

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.CellType;
import za.co.entelect.challenge.enums.Directions;

import java.util.*;
import java.util.stream.Collectors;
import java.io.*; 

public class Bot {

	private GameState gameState;
	private Opponent opponent;
	private MyWorm currentWorm;
	private MyPlayer player;

	public Bot(GameState gameState){
		this.gameState = gameState;
		this.opponent = gameState.opponent;
		this.currentWorm = getCurrentWorm(gameState);
		this.player = gameState.myPlayer;
	}

	private MyWorm getCurrentWorm(GameState gameState) {
        return Arrays.stream(gameState.myPlayer.worms)
                .filter(myWorm -> myWorm.id == gameState.currentWormId)
                .findFirst()
                .get();
    }

    private List<Cell> shootableCell() {
    	ArrayList<Cell> cells = new ArrayList<>();
    	for(int i=0; i<3; i++){
    		if(opponent.worms[i].health > 0) {
	    		int posX = opponent.worms[i].position.x;
	    		int posY = opponent.worms[i].position.y;
		    	for(Direction direction: Direction.values()) {
		    		int dx = direction.x;
		    		int dy = direction.y;
		    		int x = posX;
		    		int y = posY;
		    		while(true){
		    			x += dx;
		    			y += dy;

		    			if(!isValidCoordinate(x,y)){
		    				break;
		    			}

		    			if(euclideanDistance(posX, posY, x, y) > 4){
		    				break;
		    			}

		    			Cell cell = gameState.map[y][x];
		    			if(cell.type != CellType.AIR){
		    				break;
		    			}

		    			if(isthereWorm(x,y)){
		    				cells.add(cell);
		    				break;
		    			}

		    			cells.add(cell);
		    		}
		    	}
		    }
	    }

	    return cells;
    }

    private boolean isthereWorm(int x, int y){
    	for(int i=0; i<3; i++){
    		if(opponent.worms[i].health > 0 || player.worms[i].health > 0){
    			if((opponent.worms[i].position.x == x && opponent.worms[i].position.y == y) || (player.worms[i].position.x == x && player.worms[i].position.y == y)){
    				return true;
    			}
    		}
    	}

    	return false;
    }

    private void runAway(Worm worm) {
    	// If in danger (lowHP) and in dangerous area (shootable or bananabomb threat) 
    	// run away then dont move there if no move then move furthest from enemy 
    	// Iterate through every possible escape route if not harmful go furthest from enemy
    	// If harmful go furthest from enemy

    	boolean noValidMove = true;
    	for(Direction direction: Direction.value()){
    		if(!isPosDangerous(direction.x, direction.y)){
    			return go_to_pos(direction.x, direction.y);
    		}

    		if(isValidMove(direction.x. direction.y)){
    			noValidMove = false;
    		}
    	}

    	if(!noValidMove){
    		return runFromEnemy(worm);
    	} else {
    		canShoot = canWormShoot(worm);
    		if(canShoot){
    			return shoot_lowest_health();
    		} else {
    			return DoNothing();
    		}
    	}
    }

    private void runFromEnemy(Worm worm){
    	//Find the furthest from shooting (dist <= 4) enemy (x,y)
    	//return go to pos(x,y)
    	int dist;
    	int maxdist = -1000;
    	int moveX, moveY;
    	for(Direction direction: Direction.values()) {
    		int dx = direction.x;
    		int dy = direction.y;
    		int x = worm.position.x;
    		int y = worm.position.y;
    		while(true){
    			if(euclideanDistance(worm.position.x, worm.position.y, x, y) > 4){
    				break;
    			}

    			dist = 0;
    			for(int i=0; i<3; i++){
    				if(opponent.worms[i].health > 0){
    					temp = euclideanDistance(x,y, opponent.worms[i].position.x, opponent.worms[i].position.y);
    					if(temp <= 4){
    						dist += temp;
    					}
    				}
    			}

    			if(dist > maxdist){
    				maxdist = dist;
    				moveX = x;
    				moveY = y;
    			}

    			x += dx;
    			y += dy;
    		}
    	}

    	return go_to_pos(moveX, moveY);
    }

    private boolean isPosDangerous(int x, int y){
    	List<Cell> shootableCell = shootableCell();
    	for(int i=0; i< ; i++){
    		if(shootableCell[i].x == x && shootableCell[y] == y){
    			return true;
    		}
    	}
    	return false;
    }

    private boolean isInDanger(Worm worm){
    	return isPosDangerous(worm.position.x, worm.position.y);
    }


	private void move_to_lowest_health(){
		int minHP = 150;
		int minDist = 1000;
		int id = 0;
		for(int i=0; i<3; i++){ 
			if(min > opponent.worms[i].health && opponent.worms[i].health > 0){
				minHP = opponent.worms[i].health;
				minDist = euclideanDistance(currentWorm.position.x, currentWorm.position.y,opponent.worms[i].position.x, opponent.worms[i].position.y);
				id = i+1;
					
			} else if(minHP == opponent.worms[i].health && opponent.worms[i].health > 0){
				int temp = euclideanDistance(currentWorm.position.x, currentWorm.position.y,opponent.worms[i].position.x, opponent.worms[i].position.y)
				if(minDist > temp){
					id = i+1;
					minDist = temp;	
				}
			}
		}
		int posX = opponent.worms[id-1].position.x;
		int posY = opponent.worms[id-1].position.y;
		return go_to_pos(gameState, posX, posY);
	}


	private boolean isValidMove(int x, int y){
		if(!isValidCoordinate(x,y)){
			return false;
		}

		Cell cell = gameState.map[y][x];
		if(cell.type == CellType.DEEP_SPACE){
			return false;
		}

		for(int i=0; i<3; i++){
			if((opponent.worms[i].position.x == x && opponent.worms.position.y == y)||(player.worms[i].position.x == x && player.worms.position.y == y)){
				return false;
			}
		}

		return true;
	}

	private void go_to_pos(int posX, int posY) {
		int min = 10000;
		int move_x, move_y;
		for(Direction direction: Direction.values()) {
			int x = currentWorm.position.x + direction.x;
			int y = currentWorm.position.y + direction.y;

			if(!isValidCoordinate(x,y)) { continue; }

			if(!isValidMove(x,y)) {continue; }

			int dis = euclideanDistance(x,y,posX,posY);
			if( dis < min) {
				min = dis;
				move_x = x;
				move_y = y;
			}
		}

		Cell cell = gameState.map[move_y][move_x];
		if(cell.type == CellType.AIR || cell.type == CellType.LAVA){
			return new MoveCommand(move_x,move_y);
		} else if(cell.type == CellType.DIRT){
			return new DigCommand(move_x,move_y);
		}
	}

	private void go_to_center(){
		return go_to_pos(gameState, 16, 16);
	}

    private int euclideanDistance(int aX, int aY, int bX, int bY) {
        return (int) (Math.sqrt(Math.pow(aX - bX, 2) + Math.pow(aY - bY, 2)));
    }

    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < gameState.mapSize
                && y >= 0 && y < gameState.mapSize;
    }

    private void DoNothing(){
    	return new DoNothingCommand();
    }

    public void Run(GameState gamestate){
    	
    }
}
