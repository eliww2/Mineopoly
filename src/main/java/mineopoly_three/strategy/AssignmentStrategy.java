package mineopoly_three.strategy;

import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.item.ItemType;
import mineopoly_three.tiles.TileType;
import mineopoly_three.util.DistanceUtil;

import java.awt.*;
import java.util.Random;

public class AssignmentStrategy implements MinePlayerStrategy {

    private PlayerBoardView board;
    private int MAX_CHARGE, BOARD_SIZE, MAX_ITEMS;
    private int itemsHeld = 0;
    private Point charger, market, diamond, ruby, emerald;
    private int closestCharge, closestMarket, closestDiamond, closestRuby, closestEmerald;

    /**Takes in the variables so the robot knows what to do each turn and the state of everthing around him
     *
     * @param boardSize The length and width of the square game board
     * @param maxInventorySize The maximum number of items that your player can carry at one time
     * @param maxCharge The amount of charge your robot starts with (number of tile moves before needing to recharge)
     * @param winningScore The first player to reach this score wins the round
     * @param startingBoard A view of the GameBoard at the start of the game. You can use this to pre-compute fixed
     *                       information, like the locations of market or recharge tiles
     * @param startTileLocation A Point representing your starting location in (x, y) coordinates
     *                              (0, 0) is the bottom left and (boardSize - 1, boardSize - 1) is the top right
     * @param isRedPlayer True if this strategy is the red player, false otherwise
     * @param random A random number generator, if your strategy needs random numbers you should use this.
     */
    @Override
    public void initialize(int boardSize, int maxInventorySize, int maxCharge, int winningScore,
                           PlayerBoardView startingBoard, Point startTileLocation, boolean isRedPlayer, Random random) {
        BOARD_SIZE = boardSize;
        MAX_ITEMS = maxInventorySize;
        MAX_CHARGE = maxCharge;
        board = startingBoard;
        closestCharge = Integer.MAX_VALUE;
        closestMarket = Integer.MAX_VALUE;
        closestDiamond = Integer.MAX_VALUE;
        closestRuby = Integer.MAX_VALUE;
        closestEmerald = Integer.MAX_VALUE;
        closestItems();
    }

    /**Determines what needs to be done and what function needs to be called to do it.
     *
     * @param boardView A PlayerBoardView object representing all the information about the board and the other player
     *                   that your strategy is allowed to access
     * @param economy The GameEngine's economy object which holds current prices for resources
     * @param currentCharge The amount of charge your robot has (number of tile moves before needing to recharge)
     * @param isRedTurn For use when two players attempt to move to the same spot on the same turn
     *                   If true: The red player will move to the spot, and the blue player will do nothing
     *                   If false: The blue player will move to the spot, and the red player will do nothing
     * @return the action to take this turn.
     */
    @Override
    public TurnAction getTurnAction(PlayerBoardView boardView, Economy economy, int currentCharge, boolean isRedTurn) {
        Point myLocation = board.getYourLocation();
        board = boardView;
        closestItems();

        if (board.getTileTypeAtLocation(myLocation) == TileType.RECHARGE && currentCharge < MAX_CHARGE) {
            return null;
        } else if (itemsHeld == MAX_ITEMS) {
            return goToDestination(market);
        } else if (board.getItemsOnGround().get(myLocation).size() > 0) {
            return TurnAction.PICK_UP_RESOURCE;
        } else if (currentCharge < closestCharge) {
            return goToDestination(charger);
        } else if (
                board.getTileTypeAtLocation(myLocation) == TileType.RESOURCE_DIAMOND||
                board.getTileTypeAtLocation(myLocation) == TileType.RESOURCE_EMERALD ||
                board.getTileTypeAtLocation(myLocation) == TileType.RESOURCE_RUBY) {
            return TurnAction.MINE;
        }
        return goToDestination(bestResource(economy));
    }

    /** Modifies the itemsHeld variable so the robot knows how full his inventory is.
     *
     * @param itemReceived The item received from the player's TurnAction on their last turn
     */
    @Override
    public void onReceiveItem(InventoryItem itemReceived) {
        itemsHeld++;
    }

    /** Resets the itemsHeldVariable when items are sold.
     *  Since the auto-miner is no part of this strategy its okay to reset.
     *
     * @param totalSellPrice The combined sell price for all items in your strategy's inventory
     */
    @Override
    public void onSoldInventory(int totalSellPrice) {
        itemsHeld = 0;
    }

    /**
     *
     * @return name for tournament.
     */
    @Override
    public String getName() { return "SwagMoney"; }

    /** not used.
     *
     * @param pointsScored The total number of points this strategy scored
     * @param opponentPointsScored The total number of points the opponent's strategy scored
     */
    @Override
    public void endRound(int pointsScored, int opponentPointsScored) {    }

    /** Helper method that determines which gem point is most worth going to get
     *
     * @param economy stores the price of each gemstone.
     * @return which point the best resource is at.
     */
    private Point bestResource(Economy economy) {
        int diamondWorth = economy.getCurrentPrices().get(ItemType.DIAMOND);
        int emeraldWorth = economy.getCurrentPrices().get(ItemType.EMERALD);
        int rubyWorth = economy.getCurrentPrices().get(ItemType.RUBY);
        if (diamondWorth > emeraldWorth && diamondWorth > rubyWorth) {
            closestDiamond = Integer.MAX_VALUE;
            return diamond;
        } else if (emeraldWorth > diamondWorth && emeraldWorth > rubyWorth) {
            closestEmerald = Integer.MAX_VALUE;
            return emerald;
        } else if (rubyWorth > diamondWorth && rubyWorth > emeraldWorth) {
            closestRuby = Integer.MAX_VALUE;
            return ruby;
        }
        return null;
    }

    /** Takes in the destination and gives you the direction you need to go to get there.
     *
     * @param destination where you would like to go
     * @return the direction you would like to go.
     */
    private TurnAction goToDestination(Point destination) {
        if (destination == null) {
            return null;
        }
        if (board.getYourLocation().x < destination.x) {
            return TurnAction.MOVE_RIGHT;
        } else if (board.getYourLocation().x > destination.x) {
            return TurnAction.MOVE_LEFT;
        } else if (board.getYourLocation().y < destination.y) {
            return TurnAction.MOVE_UP;
        } else if (board.getYourLocation().y > destination.y) {
            return TurnAction.MOVE_DOWN;
        }
        return null;
    }

    /** This updates where the closest market, charger, and each of the gemstones are.
     */
    private void closestItems() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board.getTileTypeAtLocation(row, col) == TileType.RECHARGE
                        && DistanceUtil.getManhattanDistance(row, col, board.getYourLocation().x, board.getYourLocation().y)
                        < closestCharge) {
                    charger = new Point(row, col);
                    closestCharge = DistanceUtil.getManhattanDistance(
                            row, col, board.getYourLocation().x, board.getYourLocation().y
                    );
                }
                if (board.getTileTypeAtLocation(row, col) == TileType.RED_MARKET
                        && DistanceUtil.getManhattanDistance(row, col, board.getYourLocation().x, board.getYourLocation().y)
                        < closestMarket) {
                    market = new Point(row, col);
                    closestMarket = DistanceUtil.getManhattanDistance(
                            row, col, board.getYourLocation().x, board.getYourLocation().y
                    );
                }
                if (board.getTileTypeAtLocation(row, col) == TileType.RESOURCE_DIAMOND
                        && DistanceUtil.getManhattanDistance(row, col, board.getYourLocation().x, board.getYourLocation().y)
                        < closestDiamond) {
                    diamond = new Point(row, col);
                    closestDiamond = DistanceUtil.getManhattanDistance(
                            row, col, board.getYourLocation().x, board.getYourLocation().y
                    );
                }
                if (board.getTileTypeAtLocation(row, col) == TileType.RESOURCE_EMERALD
                        && DistanceUtil.getManhattanDistance(row, col, board.getYourLocation().x, board.getYourLocation().y)
                        < closestEmerald) {
                    emerald = new Point(row, col);
                    closestEmerald = DistanceUtil.getManhattanDistance(
                            row, col, board.getYourLocation().x, board.getYourLocation().y
                    );
                }
                if (board.getTileTypeAtLocation(row, col) == TileType.RESOURCE_RUBY
                        && DistanceUtil.getManhattanDistance(row, col, board.getYourLocation().x, board.getYourLocation().y)
                        < closestRuby) {
                    ruby = new Point(row, col);
                    closestRuby = DistanceUtil.getManhattanDistance(
                            row, col, board.getYourLocation().x, board.getYourLocation().y
                    );
                }
            }
        }
    }

}
