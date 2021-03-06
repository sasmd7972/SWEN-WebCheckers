package com.webcheckers.ui.Replay;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.Player;
import com.webcheckers.ui.Home.GetHomeRoute;
import spark.*;

import java.util.*;
import java.util.logging.Logger;

public class GetReplayRoute implements Route {
    private static final Logger LOG = Logger.getLogger(GetReplayRoute.class.getName());

    public static final String VIEW_NAME = "replay.ftl";

    private final GameCenter gameCenter;
    private final TemplateEngine templateEngine;

    public GetReplayRoute(final GameCenter gameCenter, final TemplateEngine templateEngine) {
        this.gameCenter = Objects.requireNonNull(gameCenter, "gameCenter is required");
        this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");

        LOG.config("GetReplayRoute is initialized.");
    }

    @Override
    public Object handle(Request request, Response response) {
        LOG.finer("GetReplayRoute is invoked.");

        Session httpSession = request.session();
        final Map<String, Object> vm = new HashMap<>();
        Player currentUser = httpSession.attribute(GetHomeRoute.CURRENT_USER_ATTR);

        vm.put("title", "Replay Mode");
        vm.put("currentUser", currentUser);
        vm.put("hasPreviousGames", gameCenter.hasPreviousGames());
        vm.put("previousGames", gameCenter.sortPreviousGames());

        return templateEngine.render(new ModelAndView(vm , VIEW_NAME));
    }

}
