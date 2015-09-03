package Game.Checkers;

import sun.awt.image.ImageWatched;
import sun.reflect.annotation.ExceptionProxy;

import java.util.LinkedList;

/**
 * Created by tinkie101 on 2015/06/07.
 */
public class Checkers {
    private int[][] board;

    private LinkedList<Integer[]> player1Pieces;
    private LinkedList<Integer[]> player2Pieces;

    /**
     * Player 1:    Man:    1
     *              King:   2
     *
     * Player 2:    Man:    3
     *              King:   4
     *
     * Empty Dark cell:         0
     * Irrelevant Light cell:   -1
     */
    public Checkers(){
        board = new int[8][8];

        player1Pieces = new LinkedList<Integer[]>();
        player2Pieces = new LinkedList<Integer[]>();


        boolean wasDark = false;
        int player1PieceCount = 12;
        int player2PieceCount = 12;
        int emptyCells = 8;

        Integer[] tempPos;

        for(int y = 0; y < board.length; y++)
        {
            boolean dark = wasDark;

            for(int x = 0; x < board[y].length; x++)
            {
                if(dark){
                    if(player2PieceCount > 0) {
                        board[y][x] = 3;
                        tempPos = new Integer[2];
                        tempPos[0] = y;
                        tempPos[1] = x;
                        player2Pieces.add(tempPos);
                        player2PieceCount--;
                    }
                    else if(emptyCells > 0){
                        board[y][x] = 0;
                        emptyCells--;
                    }
                    else if(player1PieceCount > 0){
                        board[y][x] = 1;
                        tempPos = new Integer[2];
                        tempPos[0] = y;
                        tempPos[1] = x;
                        player1Pieces.add(tempPos);
                        player1PieceCount--;
                    }
                }else
                {
                    board[y][x] = -1;
                }
                dark = !dark;
            }

            wasDark = !wasDark;
        }

    }

    public Checkers(int[][] board, LinkedList<Integer[]> player1Pieces, LinkedList<Integer[]> player2Pieces){
        this.board = board;
        this.player1Pieces = player1Pieces;
        this.player2Pieces = player2Pieces;
    }


    public Checkers clone(){
        int[][] tempBoard;
        tempBoard = new int[this.board.length][];

        for(int y = 0; y < this.board.length; y++){
            tempBoard[y] = new int[this.board[y].length];

            for(int x = 0; x < this.board[y].length; x++)
            {
                tempBoard[y][x] = board[y][x];
            }
        }

        LinkedList<Integer[]> tempPlayer1Pieces = new LinkedList<Integer[]>();

        for(int i = 0; i < player1Pieces.size(); i++){
            Integer[] temp = new Integer[player1Pieces.get(i).length];
            for(int l = 0; l < player1Pieces.get(i).length; l++){
                temp[l] =  player1Pieces.get(i)[l];
            }
            tempPlayer1Pieces.add(temp);
        }

        LinkedList<Integer[]> tempPlayer2Pieces = new LinkedList<Integer[]>();
        for(int i = 0; i < player2Pieces.size(); i++){
            Integer[] temp = new Integer[player2Pieces.get(i).length];
            for(int l = 0; l < player2Pieces.get(i).length; l++){
                temp[l] =  player2Pieces.get(i)[l];
            }
            tempPlayer2Pieces.add(temp);
        }
        return new Checkers(tempBoard, tempPlayer1Pieces, tempPlayer2Pieces);
    }

    public LinkedList<Integer[]>getAllPossibleMoves(int player) throws Exception{
        if(!isPlayer(player))
            throw new Exception("Invalid Player!");

        LinkedList<Integer[]> result = new LinkedList<Integer[]>();

        LinkedList<Integer[]> playerPieces;
        if(player == 1)
            playerPieces = player1Pieces;
        else
            playerPieces = player2Pieces;

        for(int i = 0; i < playerPieces.size(); i++)
        {
            Integer[] from = playerPieces.get(i);
            LinkedList<Integer[]> to  = getMoves(from);
            Integer[] temp;

            for(int l = 0; l < to.size(); l++) {
                temp = new Integer[4];
                temp[0] = from[0].intValue();
                temp[1] = from[1].intValue();
                temp[2] = to.get(l)[0].intValue();
                temp[3] = to.get(l)[1].intValue();
                result.add(temp);
            }
        }

        return result;
    }

    public LinkedList<Integer[]>getAllPossibleJumps(int player) throws Exception{
        if(!isPlayer(player))
            throw new Exception("Invalid Player!");

        LinkedList<Integer[]> result = new LinkedList<Integer[]>();

        LinkedList<Integer[]> playerPieces;
        if(player == 1)
            playerPieces = player1Pieces;
        else
            playerPieces = player2Pieces;

        for(int i = 0; i < playerPieces.size(); i++)
        {
            Integer[] from = playerPieces.get(i);
            LinkedList<Integer[]> to = getJumps(from);
            Integer[] temp;

            for(int l = 0; l < to.size(); l++){
                temp = new Integer[4];
                temp[0] = from[0].intValue();
                temp[1] = from[1].intValue();
                temp[2] = to.get(l)[0].intValue();
                temp[3] = to.get(l)[1].intValue();
                result.add(temp);
            }
        }

        return result;
    }

    public void printCurrentBoardState(){
        for(int y = 0; y < board.length; y++)
        {
            for(int x = 0; x < board[y].length; x++)
            {
                System.out.print("[" + board[y][x] + "] ");
            }
            System.out.println();
        }
    }

    public void printPlayerItems(){

        System.out.println("Player1: ");

        for(int i = 0; i < player1Pieces.size(); i++)
        {
            System.out.print("[");
            for(int l = 0; l < player1Pieces.get(i).length; l++)
            {
                System.out.print(player1Pieces.get(i)[l] + ", ");
            }
            System.out.print("] ");
        }

        System.out.println("\nPlayer2: ");

        for(int i = 0; i < player2Pieces.size(); i++)
        {
            System.out.print("[");
            for(int l = 0; l < player2Pieces.get(i).length; l++)
            {
                System.out.print(player2Pieces.get(i)[l] + ", ");
            }
            System.out.print("] ");
        }
        System.out.println();
    }

    public int[][] getCurrentBoardState(){

        int[][] temp = new int[board.length][];

        for(int i = 0; i < board.length; i++)
        {
            temp[i] = new int[board[i].length];

            for(int k = 0; k < temp[i].length; k++)
            {
                temp[i][k] = board[i][k];
            }
        }

        return temp;
    }

    public void setCurrentBoardState(int[][] newBoard) throws  Exception{

        if(newBoard.length != 8 || newBoard[0].length != 8)
            throw new Exception("Invalid new Board State!");

        player1Pieces = new LinkedList<Integer[]>();
        player2Pieces = new LinkedList<Integer[]>();

        board = new int[newBoard.length][];

        for(int i = 0; i < newBoard.length; i++)
        {
            board[i] = new int[newBoard[i].length];

            for(int k = 0; k < board[i].length; k++)
            {
                board[i][k] = newBoard[i][k];

                if(isPlayer(newBoard[i][k])) {
                    int player = getPlayer(newBoard[i][k]);
                    Integer[] tempPos = {i, k};

                    if (player == 1)
                        player1Pieces.add(tempPos);
                    else
                        player2Pieces.add(tempPos);
                }
            }
        }
    }

    public boolean hasLost(int player) throws Exception{
        if(player != 1 && player != 2)
            throw new Exception("Invalid player!");

		int playerPieceCount = getPlayerPieceCount(player);

		if(playerPieceCount <= 0)
			return true;

        if(player == 1){
            for(int i = 0; i < playerPieceCount; i++)
            {
                if(getMoves(player1Pieces.get(i)).size() > 0 || getJumps(player1Pieces.get(i)).size() > 0 )
                    return false;
            }

            return true;
        }
        else{
            for(int i = 0; i < player2Pieces.size(); i++)
            {
                if(getMoves(player2Pieces.get(i)).size() > 0 || getJumps(player2Pieces.get(i)).size() > 0 )
                    return false;
            }

            return true;
        }
    }

    public boolean gameOver() throws Exception{
        return (hasLost(1) || hasLost(2));
    }

    public int getPlayer(int cellValue) throws Exception{

        if(cellValue == 1 || cellValue == 2)
            return 1;
        else if(cellValue == 3 || cellValue == 4)
            return 2;

        throw new Exception("Invalid user at cell!");
    }

    public static int getOpponent(int player) throws Exception{

        if(player != 1 && player != 2)
            throw new Exception("Invalid Player!");

        if(player == 1)
            return player+1;
        else
            return player -1;
    }

    public int getPlayerPieceCount(int player) throws Exception{

        if(player != 1 && player != 2)
            throw new Exception("Invalid Player!");

        if(player == 1)
            return player1Pieces.size();
        else
            return player2Pieces.size();

    }

    public boolean isKing(int cellValue){
        if(cellValue == 2 || cellValue == 4)
            return  true;
        else
            return false;
    }

    public boolean isPlayer(int val){
        if(val > 0 && val < 5)
            return true;
        else
            return false;
    }

    public LinkedList<Integer[]> getMoves(Integer[] yx) throws Exception{
        if(yx.length != 2)
            throw new Exception("Invalid board position!");

        LinkedList<Integer[]> result = new LinkedList<Integer[]>();
        int y = yx[0];
        int x = yx[1];
        int val = board[y][x];

        int player = getPlayer(val);
        boolean isKing = isKing(val);

        Integer[] temp;

        //Move left
        if(x - 1 >= 0){
            if(player == 1 || isKing) {
                //forward
                if (y - 1 >= 0) {
                    if (board[y - 1][x - 1] == 0) {
                        //Empty cell, we can move
                        temp = new Integer[2];
                        temp[0] = y - 1;
                        temp[1] = x - 1;

                        result.add(temp);
                    }
                }
            }

            if(player == 2 || isKing){
                //backwards
                if(y + 1 < 8){
                    if(board[y+1][x-1] == 0){
                        //Empty cell, we can move
                        temp = new Integer[2];
                        temp[0] = y+1;
                        temp[1] = x-1;

                        result.add(temp);
                    }
                }
            }
        }

        //Move right
        if(x + 1 < 8) {
            if(player == 1 || isKing) {
                //forward
                if (y - 1 >= 0) {
                    if (board[y - 1][x + 1] == 0) {
                        //Empty cell, we can move
                        temp = new Integer[2];
                        temp[0] = y - 1;
                        temp[1] = x + 1;

                        result.add(temp);
                    }
                }
            }

            if(player == 2 || isKing){
                //backwards
                if (y + 1 < 8) {
                    if (board[y + 1][x + 1] == 0) {
                        //Empty cell, we can move
                        temp = new Integer[2];
                        temp[0] = y + 1;
                        temp[1] = x + 1;

                        result.add(temp);
                    }
                }
            }
        }

        return result;
    }

    public LinkedList<Integer[]> getJumps(Integer[] yx) throws Exception{
        if(yx.length != 2)
            throw new Exception("Invalid board position!");

        LinkedList<Integer[]> result = new LinkedList<Integer[]>();
        int y = yx[0];
        int x = yx[1];
        int val = board[y][x];

        int player = getPlayer(val);
        boolean isKing = isKing(val);

        Integer[] temp;

        //Jump left
        if(x - 2 >= 0){
            if(player == 1 || isKing) {
                //forward
                if (y - 2 >= 0) {
                    if (isPlayer(board[y - 1][x - 1]) && getPlayer(board[y - 1][x - 1]) != player && board[y - 2][x - 2] == 0) {
                        //Empty cell, we can jump over opponent
                        temp = new Integer[2];
                        temp[0] = y - 2;
                        temp[1] = x - 2;

                        result.add(temp);
                    }
                }
            }

            if(player == 2 || isKing){
                //backwards
                if(y + 2 < 8){
                    if(isPlayer(board[y+1][x-1]) && getPlayer(board[y+1][x-1]) != player && board[y+2][x-2] == 0){
                        //Empty cell, we can jump over opponent
                        temp = new Integer[2];
                        temp[0] = y+2;
                        temp[1] = x-2;

                        result.add(temp);
                    }
                }
            }
        }

        //Move right
        if(x + 2 < 8) {
            if(player == 1 || isKing) {
                //forward
                if (y - 2 >= 0) {
                    if (isPlayer(board[y - 1][x + 1]) && getPlayer(board[y - 1][x + 1]) != player && board[y - 2][x + 2] == 0) {
                        //Empty cell, we can jump over opponent
                        temp = new Integer[2];
                        temp[0] = y - 2;
                        temp[1] = x + 2;

                        result.add(temp);
                    }
                }
            }

            if(player == 2 || isKing){
                //backwards
                if (y + 2 < 8) {
                    if (isPlayer(board[y + 1][x + 1]) && getPlayer(board[y + 1][x + 1]) != player && board[y + 2][x + 2] == 0) {
                        //Empty cell, we can jump over opponent
                        temp = new Integer[2];
                        temp[0] = y + 2;
                        temp[1] = x + 2;

                        result.add(temp);
                    }
                }
            }
        }

        return result;
    }

    public void move(Integer[] move) throws Exception{

        if(move.length != 4)
            throw new Exception("Invalid From/To positions!");

        int fromY = move[0];
        int fromX = move[1];
        int toY = move[2];
        int toX = move[3];

        Integer[] toArray = {toY, toX};

        if(!isPlayer(board[fromY][fromX]) || !isValidOpenSpace(toArray))
            throw new Exception("Invalid From/To positions!");

        if(Math.abs(fromY - toY) != 1 || Math.abs(fromX - toX) != 1)
            throw new Exception("Invalid From/To positions!");

        int player = getPlayer(board[fromY][fromX]);


        boolean found = false;
        Integer[] temp = null;
        int size = -1;

        if(player == 1)
            size = player1Pieces.size();
        else if(player == 2)
            size = player2Pieces.size();

        for(int i = 0; i < size; i++)
        {
            if(player == 1)
                temp = player1Pieces.get(i);
            else if(player == 2)
                temp = player2Pieces.get(i);

            if(temp[0] == fromY && temp[1] == fromX)
            {
                temp[0] = toY;
                temp[1] = toX;
                board[toY][toX] = board[fromY][fromX];
                board[fromY][fromX] = 0;
                found = true;
                break;
            }

        }
        if(!found)
            throw new Exception("Invalid user piece!");
    }

    public void jump(Integer[] jump) throws Exception{

        if(jump.length != 4)
            throw new Exception("Invalid From/To positions!");

        int fromY = jump[0];
        int fromX = jump[1];
        int toY = jump[2];
        int toX = jump[3];
        int middleY = ((toY - fromY)/2) + fromY;
        int middleX = ((toX - fromX)/2) + fromX;

        Integer[] toArray = {toY, toX};

        if(!isPlayer(board[fromY][fromX]) || !isValidOpenSpace(toArray))
            throw new Exception("Invalid From/To positions!");

        if(Math.abs(fromY - toY) != 2 || Math.abs(fromX - toX) != 2)
            throw new Exception("Invalid From/To positions!");

        int player = getPlayer(board[fromY][fromX]);

        if(!isPlayer(board[middleY][middleX]) || getPlayer(board[middleY][middleX]) == player)
            throw new Exception("Invalid cell being jumped!");


        boolean found = false;
        Integer[] temp = null;
        Integer[] tempOther = null;

        int size = -1;
        int otherSize = -1;

        if(player == 1) {
            size = player1Pieces.size();
            otherSize = player2Pieces.size();
        }
        else if(player == 2) {
            size = player2Pieces.size();
            otherSize = player1Pieces.size();
        }

        for(int i = 0; i < size; i++)
        {
            if(player == 1) {
                temp = player1Pieces.get(i);
            }
            else if(player == 2) {
                temp = player2Pieces.get(i);
            }

            if(temp[0] == fromY && temp[1] == fromX)
            {
                temp[0] = toY;
                temp[1] = toX;
                board[toY][toX] = board[fromY][fromX];
                board[fromY][fromX] = 0;

                boolean foundMiddle = false;
                for(int t = 0; t < otherSize; t++) {

                    if(player == 1) {
                        tempOther = player2Pieces.get(t);
                    }
                    else if(player == 2) {
                        tempOther = player1Pieces.get(t);
                    }

                    if(tempOther[0] == middleY && tempOther[1] == middleX)
                    {
                        if(player == 1) {
                            player2Pieces.remove(t);
                        }
                        else if(player == 2) {
                            player1Pieces.remove(t);
                        }
                        board[middleY][middleX] = 0;
                        foundMiddle = true;
                        break;
                    }
                }

                if(!foundMiddle)
                    throw new Exception("Invalid middle cell!");

                found = true;
                break;
            }

        }
        if(!found)
            throw new Exception("Invalid user piece!");
    }

    public boolean isValidOpenSpace(Integer[] pos) throws Exception{
        if(pos.length != 2 )
            throw new Exception("Invalid position!");

        if(board[pos[0]][pos[1]] == 0)
            return true;
        else
            return false;

    }

    public boolean shouldKing(Integer[] pos) throws Exception{
        if(pos.length != 2 )
            throw new Exception("Invalid position!");

        if(!isPlayer(board[pos[0]][pos[1]]))
            throw new Exception("Invalid user piece!");

        if(isKing(board[pos[0]][pos[1]]))
            return false;

        int player = getPlayer(board[pos[0]][pos[1]]);

        if((player == 1 && pos[0] == 0) || (player == 2 && pos[0] == 7))
            return true;
        else
            return false;

    }

    public void king(Integer[] pos) throws Exception{
        if(pos.length != 2 )
            throw new Exception("Invalid position!");

        if(!isPlayer(board[pos[0]][pos[1]]))
            throw new Exception("Invalid user piece!");

        if(isKing(board[pos[0]][pos[1]]))
            throw new Exception("Already a king!");

        int player = getPlayer(board[pos[0]][pos[1]]);

        if(player == 1)
            board[pos[0]][pos[1]] = 2;
        else
            board[pos[0]][pos[1]] = 4;
    }

    public static void main(String[] args) throws Exception{
        Checkers game = new Checkers();

        int[][] tempBoard = game.getCurrentBoardState();

        for(int i = 0; i < tempBoard.length; i++)
        {
            for(int k = 0; k < tempBoard[i].length; k++)
            {
                if((i == 3 || i == 5) && (k == 2 || k == 4))
                {
                    tempBoard[i][k] = 4;
                }
                else if(i == 4 && k == 3){
                    tempBoard[i][k] = 2;
                }
                else if( tempBoard[i][k] != -1)
                    tempBoard[i][k] = 0;
            }
        }
        game.setCurrentBoardState(tempBoard);
        game.printCurrentBoardState();

        Integer[] pos = {3,2};
        LinkedList<Integer[]> moves = game.getMoves(pos);
        LinkedList<Integer[]> jumps = game.getJumps(pos);


        System.out.println("----------------------------\nMoves:");
        for(int i = 0; i < moves.size(); i++)
        {
            for(int k = 0; k < moves.get(i).length; k++){
                System.out.print("[" + moves.get(i)[k] + "],");
            }
            System.out.println();
        }

        System.out.println("----------------------------\nJumps:");
        for(int i = 0; i < jumps.size(); i++)
        {
            for(int k = 0; k < jumps.get(i).length; k++){
                System.out.print("[" + jumps.get(i)[k] + "],");
            }
            System.out.println();
        }

        Integer[] move = new Integer[4];
        move[0] = pos[0];
        move[1] = pos[1];
        move[2] = moves.get(0)[0];
        move[3] = moves.get(0)[1];

        game.move(move);
        game.printCurrentBoardState();

        pos[0] = 5;
        pos[1] = 4;
        moves = game.getMoves(pos);
        jumps = game.getJumps(pos);


        System.out.println("----------------------------\nMoves:");
        for(int i = 0; i < moves.size(); i++)
        {
            for(int k = 0; k < moves.get(i).length; k++){
                System.out.print("[" + moves.get(i)[k] + "],");
            }
            System.out.println();
        }

        System.out.println("----------------------------\nJumps:");
        for(int i = 0; i < jumps.size(); i++)
        {
            for(int k = 0; k < jumps.get(i).length; k++){
                System.out.print("[" + jumps.get(i)[k] + "],");
            }
            System.out.println();
        }
        Integer[] jump = new Integer[4];
        jump[0] = pos[0];
        jump[1] = pos[1];
        jump[2] = jumps.get(0)[0];
        jump[3] = jumps.get(0)[1];
        game.jump(jump);
        game.printCurrentBoardState();

        System.out.println("----------------------------\nLost?");
        System.out.println(game.hasLost(1) + ", " + game.hasLost(2));

    }
}
