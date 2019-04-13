package com.webcheckers.ui.Replay;

import com.google.gson.Gson;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.Player;
import com.webcheckers.ui.Home.GetHomeRoute;
import com.webcheckers.ui.PlayGame.GetGameRoute;
import com.webcheckers.util.Message;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class PostReplayPreviousTurnRoute implements Route {

    private static final Logger LOG = Logger.getLogger(PostReplayPreviousTurnRoute.class.getName());

    private final PlayerLobby playerLobby;
    private final GameCenter gameCenter;
    private final TemplateEngine templateEngine;
    private final Gson gson;

    public PostReplayPreviousTurnRoute(final PlayerLobby playerLobby, final GameCenter gameCenter, final TemplateEngine templateEngine, final Gson gson) {
        this.gameCenter = Objects.requireNonNull(gameCenter, "gameCenter is required");
        this.playerLobby = Objects.requireNonNull(playerLobby, "playerLobby is required");
        this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
        this.gson = gson;
    }

    @Override
    public Object handle(Request request, Response response) {
        LOG.finer("PostReplayPreviousTurnRoute is invoked.");
        Map<String, Object> vm = new HashMap<>();

        Session currentSession = request.session();
        Player currentUser = currentSession.attribute(GetHomeRoute.CURRENT_USER_ATTR);

        String gameId = currentSession.attribute(GetGameRoute.GAME_ID_ATTR);


        //   MOVE THE GAME TO THE PREVIOUS STATE


        Message successInfo;

        if( 1 > 0 ){
            successInfo = Message.info("true");
        }else{
            successInfo = Message.info("false");
        }

        return gson.toJson(successInfo);
    }
}
