package com.webcheckers.ui.PlayGame;

import com.google.gson.Gson;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.*;
import com.webcheckers.util.Message;
import spark.*;

import java.util.Objects;
import java.util.logging.Logger;

public class PostValidateMoveRoute implements Route {
    private static final Logger LOG = Logger.getLogger(PostValidateMoveRoute.class.getName());

    private final GameCenter gameCenter;
    private final Gson gson;

    public PostValidateMoveRoute(final GameCenter gameCenter, final Gson gson ) {
        this.gameCenter = Objects.requireNonNull(gameCenter, "gameCenter is required");
        this.gson = Objects.requireNonNull(gson, "gson is required");
        //
        LOG.config("PostValidateMoveRoute is initialized.");
    }

    @Override
    public Object handle(Request request, Response response) {
        LOG.finer("PostValidateMoveRoute is invoked.");

//        Map<String, Object> vm = new HashMap<>();
        Session currentSession = request.session();
        Player currentUser = currentSession.attribute("currentUser");
        String gameId = currentSession.attribute(GetGameRoute.GAME_ID_ATTR);
        String moveAsJSONString = gson.toJson(request.queryParams("actionData"));

        //See createCorrectJSONFormat method for more information
        String correctJSONMoveString = createCorrectJSONFormat(moveAsJSONString);
        Move move = gson.fromJson(correctJSONMoveString, Move.class);
        boolean isValid = gameCenter.requestMove(gameId, move);

//        Game currentGame = gameCenter.getGame(gameId);
//        vm.put("redPlayer", currentGame.getRedPlayer());
//        vm.put("whitePlayer", currentGame.getWhitePlayer());
//        vm.put("activeColor", currentGame.getPlayerColor(currentUser));
//        vm.put("viewMode", currentGame.getViewMode());
//
//        boolean isRed = currentGame.getPlayerColor(currentUser) == Player.PlayerColor.RED;
//        BoardView boardView = new BoardView(currentGame.getRecentTurn(), isRed);
//        vm.put("board", boardView);

//        vm.put(GetHomeRoute.CURRENT_USER_ATTR, currentUser);
//        vm.put("title", "Enjoy Your Game!");

        Message moveInfo;
        if(isValid){
            moveInfo = Message.info("Outstanding move!");
        }else{
            moveInfo = Message.error("Not valid Move!");
        }

        return gson.toJson(moveInfo);
    }

    //The JSON String returned by 'actionData' is not immediately in the correct format to be
    //converted from a JSON Object into an instance of the Move Class. This method correctly
    //formats the JSON String so it can be immediately converted into an instance of the Move class.
    //Incorrect Format Example (the string received):
    //  "{\"start\":{\"row\":2,\"cell\":3},\"end\":{\"row\":3,\"cell\":2}}"
    //Correct Format Example (returned by this method):
    //  {'start':{'row':2,'cell':3},'end':{'row':3,'cell':2}}
    private static String createCorrectJSONFormat(String incorrectJSONString){
        //1.) Replace all backslashes (\) with apostrophes (')
        String correctJSONString = incorrectJSONString.replace("\\", "'");
        //2.) Replace all instances of an apostrophe followed by a double- quote ('") with a single
        //    apostrophe ('). This was the result of the replacements that occurred in Step 1
        correctJSONString = correctJSONString.replaceAll("'\"", "'");
        //3.) Replace all instances of only double- quotes(") with a the empty string (""),
        //    therefore removing all instances of only double-quotes. This occurs at the
        //    beginning and the end of the incorrect string recieved by 'actionData'
        correctJSONString = correctJSONString.replace("\"", "");

        //4.) Return the correctly formatted string
        return correctJSONString;
    }
}
