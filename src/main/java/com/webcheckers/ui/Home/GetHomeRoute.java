package com.webcheckers.ui.Home;

import java.util.*;
import java.util.logging.Logger;

import com.webcheckers.appl.*;
import com.webcheckers.model.*;
import com.webcheckers.ui.PlayGame.GetGameRoute;
import com.webcheckers.ui.WebServer;
import spark.*;

import com.webcheckers.util.Message;

import static spark.Spark.halt;

/**
 * The UI Controller to GET the Home page.
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 */
public class GetHomeRoute implements Route {
  private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());

  public static final Message WELCOME_MSG = Message.info("Welcome to the world of online Checkers.");
  public static final String CURRENT_USER_ATTR = "currentUser";
  public static final String VIEW_NAME = "home.ftl";
  public static final String ERROR_MSG = "homeErrorMessage";

  private final PlayerLobby playerLobby;
  private final GameCenter gameCenter;
  private final TemplateEngine templateEngine;

  /**
   * Create the Spark Route (UI controller) to handle all {@code GET /} HTTP requests.
   *
   * @param templateEngine
   *   the HTML template rendering engine
   */
  public GetHomeRoute(final PlayerLobby playerLobby, final GameCenter gameCenter, final TemplateEngine templateEngine) {
    this.playerLobby = Objects.requireNonNull(playerLobby, "playerLobby is required");
    this.gameCenter = Objects.requireNonNull(gameCenter, "gameCenter is required");
    this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
    //
    LOG.config("GetHomeRoute is initialized.");
  }

  /**
   * Render the WebCheckers Home page.
   *
   * @param request
   *   the HTTP request
   * @param response
   *   the HTTP response
   *
   * @return
   *   the rendered HTML for the Home page
   */
  @Override
  public Object handle(Request request, Response response) {
    LOG.finer("GetHomeRoute is invoked.");

    Session httpSession = request.session();

    final Map<String, Object> vm = new HashMap<>();

    vm.put("title", "Welcome!");

    // Displays an error message to the user, if any.
    if(httpSession.attribute(ERROR_MSG) != null && httpSession.attribute(ERROR_MSG) instanceof Message) {
        // Show the error message
        vm.put("message", (Message) httpSession.attribute(ERROR_MSG));
        // Remove the error message
        httpSession.removeAttribute(ERROR_MSG);
    } else {
        vm.put("message", WELCOME_MSG);
    }

    if(httpSession.attribute(CURRENT_USER_ATTR) != null) {
        Player currentUser = httpSession.attribute(CURRENT_USER_ATTR);

        vm.put(CURRENT_USER_ATTR, currentUser);
        vm.put("hasPlayers", playerLobby.hasPlayers());
        vm.put("players", playerLobby.getPlayers());
        vm.put("hasCurrentGames", gameCenter.hasCurrentGames());
        vm.put("currentGames", gameCenter.getCurrentGames());

        //if the player has been challenged to a game redirect them to the
        //GET Game Route, with the appropriate 'opponent' parameter
        if(httpSession.attribute(GetGameRoute.GAME_ID_ATTR) == null){
            if(currentUser.isPlaying()){
                String newGameId = gameCenter.getPlayerGameId(currentUser);
                httpSession.attribute(GetGameRoute.GAME_ID_ATTR, newGameId);

                response.redirect(WebServer.GAME_URL);
                halt();
                return null;
            }
        }else {
            String gameId = httpSession.attribute(GetGameRoute.GAME_ID_ATTR);
            Game game = gameCenter.getGame(gameId);

            if(game.isResigned()){
                currentUser.leaveGame();
                httpSession.removeAttribute(GetGameRoute.GAME_ID_ATTR);
            }
        }
    }else{
        int currentNumOfPlayers = playerLobby.getNumOfPlayers();
        if(currentNumOfPlayers != 1) {
            vm.put("numOfPlayersMsg", "There are currently " + currentNumOfPlayers +
                    " players who want to play WebCheckers with you!");
        }else {
            vm.put("numOfPlayersMsg", "There is currently 1 player who " +
                    "wants to play WebCheckers with you!");
        }
    }

    // render the View
    return templateEngine.render(new ModelAndView(vm , VIEW_NAME));
  }
}
