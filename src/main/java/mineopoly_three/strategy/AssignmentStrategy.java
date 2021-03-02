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

    @Override
    public void onReceiveItem(InventoryItem itemReceived) {
        itemsHeld++;
    }

    @Override
    public void onSoldInventory(int totalSellPrice) {
        itemsHeld = 0;
    }

    @Override
    public String getName() { return "SwagMoney"; }

    @Override
    public void endRound(int pointsScored, int opponentPointsScored) {    }
}
