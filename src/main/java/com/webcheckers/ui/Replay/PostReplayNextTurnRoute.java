package com.webcheckers.ui.Replay;

import com.google.gson.Gson;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.Board;
import com.webcheckers.model.Player;
import com.webcheckers.model.ReplayGame;
import com.webcheckers.ui.Home.GetHomeRoute;
import com.webcheckers.ui.PlayGame.GetGameRoute;
import com.webcheckers.util.Message;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class PostReplayNextTurnRoute implements Route {

    private static final Logger LOG = Logger.getLogger(PostReplayNextTurnRoute.class.getName());

    private final GameCenter gameCenter;
    private final Gson gson;

    public PostReplayNextTurnRoute(final GameCenter gameCenter, final Gson gson) {
        this.gameCenter = Objects.requireNonNull(gameCenter, "gameCenter is required");
        this.gson = gson;
    }

    @Override
    public Object handle(Request request, Response response) {
        LOG.finer("PostReplayNextTurnRoute is invoked.");

        String gameID = request.queryParams("gameID");
        ReplayGame replayGame = this.gameCenter.getReplayGame(gameID);
        Board nextTurn = replayGame.getNextTurn();

        // If clicking next button was successful
        Message successInfo;
        if( nextTurn != null ){
            successInfo = Message.info("true");
        }else{
            successInfo = Message.info("false");
        }

        return gson.toJson(successInfo);

    }


}
