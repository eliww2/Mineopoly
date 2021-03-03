package mineopoly_three;

import mineopoly_three.action.TurnAction;
import mineopoly_three.game.GameEngine;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.item.ItemType;
import mineopoly_three.strategy.AssignmentStrategy;
import mineopoly_three.strategy.MinePlayerStrategy;
import mineopoly_three.strategy.PlayerBoardView;
import mineopoly_three.strategy.RandomStrategy;
import mineopoly_three.tiles.TileType;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Enclosed.class)
public class MineopolyTest {

    private static AssignmentStrategy myStrategy;

    public static class Tests {
        @Before
        public void setUp() {
            myStrategy = new AssignmentStrategy();
        }

        @Test
        public void chargerTest() {
            TileType[][] boardTileTypes = new TileType[][]{
                    {TileType.RECHARGE, TileType.EMPTY, TileType.EMPTY},
                    {TileType.EMPTY,TileType.EMPTY, TileType.EMPTY},
                    {TileType.EMPTY,TileType.RESOURCE_RUBY, TileType.EMPTY}
            };
            Point myLocation = new Point(0,2);
            PlayerBoardView boardView = new PlayerBoardView(
                    boardTileTypes, new HashMap<>(), myLocation, new Point(3, 3), 0
            );
            Random random = new Random();
            myStrategy.initialize(3,5,80,1000, boardView, myLocation,true, random);
            assertNull(myStrategy.getTurnAction(boardView, null, 0, true));

        }

        @Test
        public void marketTest() {
            TileType[][] boardTileTypes = new TileType[][]{
                    {TileType.RECHARGE, TileType.EMPTY, TileType.EMPTY},
                    {TileType.EMPTY,TileType.EMPTY, TileType.EMPTY},
                    {TileType.RED_MARKET,TileType.RESOURCE_RUBY, TileType.EMPTY}
            };
            Point myLocation = new Point(0,2);
            PlayerBoardView boardView = new PlayerBoardView(
                    boardTileTypes, new HashMap<>(), myLocation, new Point(3, 3), 0
            );
            Random random = new Random();
            myStrategy.initialize(3,5,80,1000, boardView, myLocation,true, random);
            myStrategy.setItemsHeld(5);
            assertEquals(TurnAction.MOVE_DOWN , myStrategy.getTurnAction(boardView, null, 80, true));

        }

        @Test
        public void pickUpTest() {
            TileType[][] boardTileTypes = new TileType[][]{
                    {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                    {TileType.EMPTY,TileType.EMPTY, TileType.EMPTY},
                    {TileType.RECHARGE,TileType.RESOURCE_RUBY, TileType.EMPTY}
            };
            Point myLocation = new Point(0,2);
            Map<Point, List<InventoryItem>> map = new HashMap<>();
            List<InventoryItem> list = new ArrayList<>();
            list.add(null);
            map.put(myLocation, list);
            PlayerBoardView boardView = new PlayerBoardView(
                    boardTileTypes, map, myLocation, new Point(3, 3), 0
            );
            Random random = new Random();
            myStrategy.initialize(3,5,80,1000, boardView, myLocation,true, random);
            assertEquals(TurnAction.PICK_UP_RESOURCE , myStrategy.getTurnAction(boardView, null, 1, true));
        }

        @Test
        public void mineTest() {
            TileType[][] boardTileTypes = new TileType[][]{
                    {TileType.RESOURCE_RUBY, TileType.EMPTY, TileType.EMPTY},
                    {TileType.EMPTY,TileType.EMPTY, TileType.EMPTY},
                    {TileType.RECHARGE,TileType.RESOURCE_RUBY, TileType.EMPTY}
            };
            Point myLocation = new Point(0,2);
            Map<Point, List<InventoryItem>> map = new HashMap<>();
            List<InventoryItem> list = new ArrayList<>();
            map.put(myLocation, list);
            PlayerBoardView boardView = new PlayerBoardView(
                    boardTileTypes, map, myLocation, new Point(3, 3), 0
            );
            Random random = new Random();
            myStrategy.initialize(3,5,80,1000, boardView, myLocation,true, random);
            assertEquals(TurnAction.MINE , myStrategy.getTurnAction(boardView, null, 80, true));
        }

        @Test
        public void closeItemTest() {
            TileType[][] boardTileTypes = new TileType[][]{
                    {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                    {TileType.EMPTY,TileType.EMPTY, TileType.EMPTY},
                    {TileType.RECHARGE,TileType.RESOURCE_RUBY, TileType.EMPTY}
            };
            Point myLocation = new Point(0,2);
            Map<Point, List<InventoryItem>> map = new HashMap<>();
            List<InventoryItem> list = new ArrayList<>();
            map.put(myLocation, list);
            PlayerBoardView boardView = new PlayerBoardView(
                    boardTileTypes, map, myLocation, new Point(3, 3), 0
            );
            Random random = new Random();
            myStrategy.initialize(3,5,80,1000, boardView, myLocation,true, random);
            myStrategy.closestItems();
            assertEquals( 2, myStrategy.getClosestCharge());
        }

        @Test
        public void itemCountTest() {
            TileType[][] boardTileTypes = new TileType[][]{
                    {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                    {TileType.EMPTY,TileType.EMPTY, TileType.EMPTY},
                    {TileType.RECHARGE,TileType.RESOURCE_RUBY, TileType.EMPTY}
            };
            Point myLocation = new Point(0,2);
            Map<Point, List<InventoryItem>> map = new HashMap<>();
            List<InventoryItem> list = new ArrayList<>();
            map.put(myLocation, list);
            PlayerBoardView boardView = new PlayerBoardView(
                    boardTileTypes, map, myLocation, new Point(3, 3), 0
            );
            Random random = new Random();
            myStrategy.initialize(3,5,80,1000, boardView, myLocation,true, random);
            myStrategy.onReceiveItem(null);
            assertEquals( 1, myStrategy.getItemsHeld());
            myStrategy.onSoldInventory(10);
            assertEquals(0, myStrategy.getItemsHeld());
        }
    }
}
