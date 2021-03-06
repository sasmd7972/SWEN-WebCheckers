package com.webcheckers.appl;

import com.webcheckers.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

@Tag("Application-tier")
class GameCenterTest {

    private GameCenter CuT;

    private String GAME_ID_TEST = "Game 0";

    @BeforeEach
    void setup() {
        this.CuT = new GameCenter();
    }

    @Test
    void testNewGame() {
        Player player1 = new Player("redPlayer");
        Player player2 = new Player("whitePlayer");
        GameCenter.ViewMode viewMode = GameCenter.ViewMode.PLAY;

        assertEquals(GAME_ID_TEST, CuT.newGame(player1, player2) );
    }

    @Test
    void testGetGame() {
        Player player1 = new Player("redPlayer");
        Player player2 = new Player("whitePlayer");
        GameCenter.ViewMode viewMode = GameCenter.ViewMode.PLAY;

        CuT.newGame(player1, player2);

        assertSame(player1, CuT.getGame(GAME_ID_TEST).getRedPlayer());
        assertSame(player2, CuT.getGame(GAME_ID_TEST).getWhitePlayer());
    }

    @Test
    void testRedPlayerHasGame() {
        Player player1 = new Player("redPlayer");
        Player player2 = new Player("whitePlayer");
        GameCenter.ViewMode viewMode = GameCenter.ViewMode.PLAY;

        CuT.newGame(player1, player2);

        assertTrue( CuT.hasGame(player1, player2) );

    }

    @Test
    void testWhitePlayerHasGame(){
        Player player1 = new Player("redPlayer");
        Player player2 = new Player("whitePlayer");
        GameCenter.ViewMode viewMode = GameCenter.ViewMode.PLAY;

        CuT.newGame(player1, player2);

        assertTrue( CuT.hasGame(player1, player2) );
    }

    @Test
    void testDoesNotHasGame(){
        Player player1 = new Player("redPlayer");
        Player player2 = new Player("whitePlayer");
        assertFalse( CuT.hasGame(player1, player2) );
    }

    @Test
    void testRequestMove() {
        Player player1 = new Player("redPlayer");
        Player player2 = new Player("whitePlayer");

        String cutGame = CuT.newGame(player1, player2);

        Move move = new Move( new Position(2,1), new Position(3,2) );

        assertTrue(CuT.requestMove(cutGame, move));
    }

    @Test
    void testSubmitTurn() {
        Player player1 = new Player("redPlayer");
        Player player2 = new Player("whitePlayer");

        String cutGame = CuT.newGame(player1, player2);

        assertFalse(CuT.submitTurn(cutGame));
    }

    @Test
    void testBackupMove() {
        Player player1 = new Player("redPlayer");
        Player player2 = new Player("whitePlayer");

        String cutGame = CuT.newGame(player1, player2);

        assertTrue(CuT.backupMove(cutGame));
    }

    @Test
    void testIsMyTurnRED() {
        Player player1 = new Player("redPlayer");
        Player player2 = new Player("whitePlayer");

        CuT.newGame(player1, player2);

        assertSame(player1, CuT.getGame(GAME_ID_TEST).getRedPlayer());
        assertSame(player2, CuT.getGame(GAME_ID_TEST).getWhitePlayer());

        assertTrue( CuT.isMyTurn(GAME_ID_TEST, player1));
        assertFalse( CuT.isMyTurn(GAME_ID_TEST, player2));
    }

    @Test
    void testIsMyTurnWHITE() {
        Player player1 = new Player("redPlayer");
        Player player2 = new Player("whitePlayer");

        String game = CuT.newGame(player1, player2);

        assertSame(player1, CuT.getGame(GAME_ID_TEST).getRedPlayer());
        assertSame(player2, CuT.getGame(GAME_ID_TEST).getWhitePlayer());

        CuT.getGame(GAME_ID_TEST).submitTurn();

        assertTrue( CuT.isMyTurn(GAME_ID_TEST, player1) );
        assertFalse( CuT.isMyTurn(GAME_ID_TEST, player2) );
    }

    @Test
    void testIsInAnyGame() {

        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        Player p3 = new Player("p3");
        Player p4 = new Player("p4");
        Player p5 = new Player("p5");
        Player p6 = new Player("p6");

        String game0 = CuT.newGame(p1, p2);
        String game1 = CuT.newGame(p3, p4);

        assertTrue( CuT.isInAnyGame(p1) );
        assertTrue( CuT.isInAnyGame(p2) );
        assertTrue( CuT.isInAnyGame(p4) );
        assertFalse( CuT.isInAnyGame(p6) );

    }

    @Test
    void testIsMyTurn() {

        Player p1 = new Player("p1");
        Player p2 = new Player("p2");

        String cutGame = CuT.newGame(p1, p2);

        assertTrue( CuT.isMyTurn(cutGame, p1) );
        assertFalse( CuT.isMyTurn(cutGame, p2) );

    }

    @Test
    void testAddToPreviousGames() {

        Player p1 = new Player("p1");
        Player p2 = new Player("p2");

        String game0 = CuT.newGame(p1, p2);

        assertFalse(CuT.hasPreviousGames());

        CuT.addToPreviousGames( CuT.getGame(game0), "0");

        assertTrue(CuT.hasPreviousGames());


    }

    @Test
    void testGetCurrentGames() {
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");

        String game0 = CuT.newGame(p1,p2);

        Collection<Game> gameCollection = CuT.getCurrentGames();

        assertTrue( gameCollection.contains(CuT.getGame(game0)) );
    }

    @Test
    void sortPreviousGames() {

        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        Player p3 = new Player("p3");
        Player p4 = new Player("p4");
        Player p5 = new Player("p5");
        Player p6 = new Player("p6");

        String game0 = CuT.newGame(p1,p2);
        String game1 = CuT.newGame(p3,p4);
        String game2 = CuT.newGame(p5,p6);

        CuT.addToPreviousGames( CuT.getGame(game0), "0");
        CuT.addToPreviousGames( CuT.getGame(game1), "1");
        CuT.addToPreviousGames( CuT.getGame(game2) , "2");

        ArrayList<ReplayGame> arrayList = CuT.sortPreviousGames();

        assertEquals( arrayList.get(1).getGameId(), "Game #1" );
        assertEquals( arrayList.get(2).getGameId(), "Game #2" );
        assertEquals( arrayList.get(3).getGameId(), "Game #3" );

    }

    @Test
    void testRemoveGame() {
        Player player1 = new Player("redPlayer");
        Player player2 = new Player("whitePlayer");

        CuT.newGame(player1, player2);

        assertSame(player1, CuT.getGame(GAME_ID_TEST).getRedPlayer());
        assertSame(player2, CuT.getGame(GAME_ID_TEST).getWhitePlayer());

        CuT.removeGame(GAME_ID_TEST);

        assertNull(CuT.getGame(GAME_ID_TEST));
    }

    @Test
    void hasCurrentGames() {

        assertFalse(CuT.hasCurrentGames());

        Player player1 = new Player("redPlayer");
        Player player2 = new Player("whitePlayer");

        CuT.newGame(player1, player2);

        assertTrue(CuT.hasCurrentGames());

    }

    @Test
    void isSpectatorUpdated() {
        Player player1 = new Player("redPlayer");
        Player player2 = new Player("whitePlayer");
        Player spectator = new Player("Spec");
        Player spectator2 = new Player("Spec2");
        String game0 = CuT.newGame(player1, player2);

        CuT.updateSpectator(game0, spectator);

        assertTrue(CuT.isSpectatorUpdated(game0, spectator));

    }

    @Test
    void testRemoveSpectator() {
        Player player1 = new Player("redPlayer");
        Player player2 = new Player("whitePlayer");
        Player spectator = new Player("Spec");
        String game0 = CuT.newGame(player1, player2);

        CuT.updateSpectator(game0, spectator);

        assertTrue(CuT.isSpectatorUpdated(game0, spectator));
        assertTrue(CuT.removeSpectator(game0, spectator));


    }

    @Test
    void testUpdateSpectator() {
        Player player1 = new Player("redPlayer");
        Player player2 = new Player("whitePlayer");
        Player spectator = new Player("Spec");
        String game0 = CuT.newGame(player1, player2);

        assertTrue(CuT.updateSpectator(game0, spectator));
    }

    @Test
    void testGetPlayerGameId() {
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        Player p3 = new Player("p3");
        Player p4 = new Player("p4");
        Player p5 = new Player("p5");
        Player p6 = new Player("p6");

        String game0 = CuT.newGame(p1, p2);
        String game1 = CuT.newGame(p3, p4);
        //String game2 = CuT.newGame(p5, p6);

        assertEquals(game0, CuT.getPlayerGameId(p1));
        assertEquals(game0, CuT.getPlayerGameId(p2));
        assertNotEquals(game0, CuT.getPlayerGameId(p3));

        assertEquals(game1, CuT.getPlayerGameId(p4));

        assertNull( CuT.getPlayerGameId(p6) );
    }

    @Test
    void testGetReplayGame() {
        assertNull( CuT.getReplayGame("0"));

        Player p1 = new Player("p1");
        Player p2 = new Player("p2");

        String game0 = CuT.newGame(p1, p2);

        CuT.addToPreviousGames( CuT.getGame(game0) ,game0);

        assertNull(CuT.getReplayGame("Game #" + 0));

    }

    @Test
    void testHasGame() {
        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        Player p3 = new Player("p3");
        Player p4 = new Player("p4");
        Player p5 = new Player("p5");
        Player p6 = new Player("p6");

        CuT.newGame(p1,p2);
        CuT.newGame(p3,p4);

        assertTrue(CuT.hasGame(p1,p2));
        assertTrue(CuT.hasGame(p3,p4));
        assertFalse(CuT.hasGame(p5,p6));
        assertFalse(CuT.hasGame(p1, p5));
    }

//    @Test
//    void testEndGame() {
//        Player p1 = new Player("p1");
//        Player p2 = new Player("p2");
//
//        Game game = new Game(p1, p2, "0");
//        Board board = new Board(false);
//
//
//        String game0 = CuT.
//
//        CuT.endGame(game0, p1);
//    }





}