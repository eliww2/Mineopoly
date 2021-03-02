package mineopoly_three.strategy;

import mineopoly_three.action.Action;
import mineopoly_three.action.MoveAction;
import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.item.ItemType;
import mineopoly_three.tiles.TileType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AssignmentStrategy implements MinePlayerStrategy {

    private PlayerBoardView board;
    private int MAX_CHARGE;
    private int BOARD_SIZE;

    @Override
    public void initialize(int boardSize, int maxInventorySize, int maxCharge, int winningScore,
                           PlayerBoardView startingBoard, Point startTileLocation, boolean isRedPlayer, Random random) {
        BOARD_SIZE = boardSize;
        MAX_CHARGE = maxCharge;
    }

    @Override
    public TurnAction getTurnAction(PlayerBoardView boardView, Economy economy, int currentCharge, boolean isRedTurn) {
       Action action = bestAction(currentCharge, economy);
       return null;
    }

    private Action bestAction(int currentCharge, Economy economy) {
        Point myLocation = board.getYourLocation();
        if (board.getTileTypeAtLocation(myLocation) == TileType.RECHARGE && currentCharge < MAX_CHARGE) {
            return null;
        } else if (true) {
            return null;
        }
        return null;
    }

    @Override
    public void onReceiveItem(InventoryItem itemReceived) {

    }

    @Override
    public void onSoldInventory(int totalSellPrice) {

    }

    @Override
    public String getName() { return "SwagMoney"; }

    @Override
    public void endRound(int pointsScored, int opponentPointsScored) {    }
}
