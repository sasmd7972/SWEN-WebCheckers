package com.webcheckers.model;

import com.webcheckers.appl.GameCenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Game {
    private Player redPlayer, whitePlayer, activePlayer, resignedPlayer;
    private Board checkerBoard;
    private Stack<Board> previousMoves;
    private boolean validTurn;
    private ArrayList<Board> previousTurns;
    private String gameId;
    private HashMap<Player, Board> spectators;

    /**
     * Creates a new game instance
     * @param redPlayer the red player
     * @param whitePlayer the white player
     */
    public Game(Player redPlayer, Player whitePlayer, String gameId){
        this.redPlayer = redPlayer;
        this.activePlayer = redPlayer;
        this.whitePlayer = whitePlayer;
        this.checkerBoard = setUpPreferences();
        this.previousMoves = new Stack<>();
        this.gameId = gameId;
        previousMoves.push(checkerBoard);
        this.resignedPlayer = null;
        this.validTurn = false;
        this.previousTurns = new ArrayList<>();
        previousTurns.add(checkerBoard);
        this.spectators = new HashMap<>();
    }


    public Board setUpPreferences() {
        Board setUp = new Board(false);
        String keyName = getKeyNames();

        switch (keyName) {
            case "END GAME":
                setUp.setUpEndGame();
                break;
            case "MULTI JUMP":
                setUp.setUpMultiJump();
                break;
            case "KING PIECE":
                setUp.setUpKingPiece();
                break;
            default:
                setUp = new Board();
                break;
        }
        return setUp;
    }

    //    Accessors

    /**
     * Returns the red player
     * @return the red player
     */
    public Player getRedPlayer() {
        return this.redPlayer;
    }

    /**
     * Returns the white player
     * @return the white player
     */
    public Player getWhitePlayer() {
        return this.whitePlayer;
    }

    /**
     * Gets the initial layout of the board before any player moves on their turn
     * @return the initial layout of the board
     */
    public Board getCheckerBoard(){
        return this.checkerBoard;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public String getGameId(){
        return gameId;
    }

    public boolean isInGame(Player player) {
        return ((this.whitePlayer.equals(player)) || (this.redPlayer.equals(player)));
    }

    /**
     * Return the move recent turn
     * @return the board object of the most recent turn
     */
    public Board getRecentTurn() { return this.previousMoves.peek(); }

    /**
     * Gets the current player's color
     * @param currentPlayer the current player
     * @return the color of the current player
     */
    public Player.PlayerColor getPlayerColor( Player currentPlayer ){
        return currentPlayer.getPlayerColor();
    }

    public ArrayList<Board> getPreviousTurns() {
        return this.previousTurns;
    }

    /**
     * Starts a game
     */
    public void initializeGame() {
        this.redPlayer.joinGame( Player.PlayerColor.RED );
        this.whitePlayer.joinGame( Player.PlayerColor.WHITE );
    }

    /**
     *
     * @param move
     * @return
     */
    public boolean makeMove( Move move ) {
        Board turn = new Board();
        //CHANGED: 'getRecentedTurn()' to 'this.checkerboard' so that multiple single moves
        //are not longer allowed
        turn.copyBoard( this.previousMoves.peek() );
        Piece selectPiece = turn.getBoard()[move.getStartRow()][move.getStartCell()].getPiece();
        Move.TYPE_OF_MOVE moveType = move.typeOfMove(selectPiece);

        if( moveType == Move.TYPE_OF_MOVE.ERROR ) {
            return false;
        }


        switch ( moveType ) {
            case SIMPLE:
                if( move.validSimpleMove( turn ) && ( this.previousMoves.size() == 1 ) ) {
                    movePiece( move, turn, selectPiece );
                    this.previousMoves.push(turn);
                    this.validTurn = validateTurn();
                    return true;
                }
                break;
            case JUMP:
                Position between = move.validSimpleJump( turn );
                if( between != null ) {
                    movePiece( move, turn, selectPiece );
                    turn.getBoard()[between.getRow()][between.getCell()].setPiece( null );
                    this.previousMoves.push(turn);

                    Piece endPiece = previousMoves.peek().getBoard()[move.getEndRow()][move.getEndCell()].getPiece();
                    this.validTurn = !(canJump(move.getEnd(), previousMoves.peek(), getNeighbors(endPiece, move.getEndRow(), move.getEndCell())));
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;

    }


    /**
     *
     * @param move
     * @param turn
     * @param piece
     */
    public void movePiece( Move move, Board turn, Piece piece ) {
        turn.getBoard()[move.getEndRow()][move.getEndCell()].setPiece( new Piece( piece.getType(), piece.getColor() ) );
        turn.getBoard()[move.getStartRow()][move.getStartCell()].setPiece( null );
    }

    public boolean canJump( Position startPos, Board turn, Position[] neighbors ) {
        for( Position position : neighbors ) {
            if( position.inBounds() ) {
                Move move = new Move( startPos, position );
                Position between = move.validSimpleJump( turn );
                if( between != null ) {
                    return true;
                }
            }
        }

        return false;
    }

    public Position[] getNeighbors(Piece piece, int row, int col){
        Piece.TYPE pieceType = piece.getType();
        Piece.COLOR pieceColor = piece.getColor();

        if (pieceType == Piece.TYPE.SINGLE ) {
            if (pieceColor == Piece.COLOR.RED) {
                return new Position[]{new Position(row + 2, col - 2),
                        new Position(row + 2, col + 2)};
            }
            if (pieceColor == Piece.COLOR.WHITE) {
                return new Position[]{new Position(row - 2, col - 2),
                        new Position(row - 2, col + 2)};
            }
        }else if( pieceType == Piece.TYPE.KING ) {
            return new Position[]{new Position(row + 2, col - 2),
                    new Position(row + 2, col + 2),
                    new Position(row - 2, col - 2),
                    new Position(row - 2, col + 2)};
        }

        return null;
    }

    public boolean validateTurn() {
        Space[][] board = this.checkerBoard.getBoard();
        for( int row = 0; row < 8; row++ ) {
            for( int col = 0; col < 8; col++ ) {
                Space selectedSpace = board[row][col];

                if( selectedSpace.validSpaceWithPiece( this.activePlayer ) ) {

                    Piece selectedPiece = selectedSpace.getPiece();
                    Position[] neighbors = getNeighbors(selectedPiece, row, col);
                    Position startPos = new Position( row, col );

                    if( canJump( startPos, checkerBoard, neighbors ) ){
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Submits a turn to be checked and resets the previous moves stack
     * @return if the turn was a valid turn
     */
    public boolean submitTurn() {
        if(this.validTurn) {
            this.checkerBoard = this.previousMoves.pop();
            this.checkerBoard.kingPieces();
            this.previousTurns.add(checkerBoard);
            this.previousMoves = new Stack<>();
            this.previousMoves.push(checkerBoard);
            this.validTurn = false;

            if (this.activePlayer.equals(this.redPlayer)) {
                this.activePlayer = this.whitePlayer;
            } else {
                this.activePlayer = this.redPlayer;
            }

            return true;
        }else{
            return false;
        }
    }

    /**
     * Backups a single move
     * @return if the turn was successfully backed-up
     */
    public boolean backup(){
        Board previousMove = this.previousMoves.pop();
        this.validTurn = validateTurn();

        return previousMove != null;
    }

    public boolean updateSpectator(Player spectator){
        this.spectators.put(spectator, this.checkerBoard);
        return true;
    }

    public Board getSpectatorBoard(Player spectator){
        return this.spectators.get(spectator);
    }

    public boolean isSpectatorUpdated(Player spectator){
        Board spectatorBoard = this.spectators.get(spectator);
        return spectatorBoard.equals(this.checkerBoard);
    }

    public boolean removeSpectator(Player spectator){
        this.spectators.remove(spectator);
        return true;
    }

    /**
     * Returns the player who won the game
     * @return the player who won
     */
    public Player completedGame() {
        if( this.checkerBoard.finishedGame() ) {
            Player.PlayerColor winnerColor = this.checkerBoard.winnerColor();
            if( winnerColor == Player.PlayerColor.RED ) {
                return this.redPlayer;
            } else {
                return this.whitePlayer;
            }
        }

        return null;
    }

    /**
     * Resigns a player
     * @param resignedPlayer the player to resign
     */
    public void playerResigned(Player resignedPlayer){
        this.resignedPlayer = resignedPlayer;
    }

    /**
     * Checks to see if there is a resigned player
     * @return if a player is resigned
     */
    public boolean isResigned(){
        return this.resignedPlayer != null;
    }

    /**
     * Returns the player that resigned
     * @return the resigned player
     */
    public Player getResignedPlayer(){
        return this.resignedPlayer;
    }

    public boolean arePlayersInGame(){
        return this.redPlayer.isPlaying() && this.whitePlayer.isPlaying();
    }

    public String getGameResult(Player currentUser){
        String gameResult;

        if(this.isResigned()){
            gameResult = this.resignedPlayer.getName() + " has resigned.";
        }else{
            Player winner = this.completedGame();
            Player loser;

            if(winner.equals(this.getRedPlayer())){
                loser = this.getWhitePlayer();
            }else{
                loser = this.getRedPlayer();
            }

            if(winner.equals(currentUser)){
                gameResult = "You have captured all of " + loser.getName()
                        + "'s pieces. Congratulations, you win!";
            }else if(loser.equals(currentUser)){
                gameResult = winner.getName() + " has captured all of your pieces. You lose.";
            }else{
                gameResult = winner.getName() + " has captured all of " + loser.getName() + "'s pieces "
                        + "and has won the game! Thank you for watching!";
            }
        }

        return gameResult;
    }

    public int getSpectatorNum(){
        return this.spectators.size();
    }


    private String getKeyNames() {
        String redName = this.redPlayer.getName();
        String whiteName = this.whitePlayer.getName();

        if( redOrWhitePlayerNameEquals( redName, whiteName, "END GAME" ) ) {
            return "END GAME";
        } else if( redOrWhitePlayerNameEquals( redName, whiteName, "MULTI JUMP" ) ) {
            return "MULTI JUMP";
        } else if( redOrWhitePlayerNameEquals( redName, whiteName, "KING PIECE" ) ) {
            return "KING PIECE";
        } else {
            return "NONE";
        }
    }
    private boolean redOrWhitePlayerNameEquals(String redName, String whiteName, String key) {
        return redName.equals(key) || whiteName.equals(key);
    }

}
