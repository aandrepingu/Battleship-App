package test.java;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import game.java.*;
import game.java.Ship.Direction;

public final class BattleshipPlayerTest {
	private BattleshipPlayer p, p2;
	@Test
	void placeMoveRemoveShips() {
		p = new BattleshipPlayer(10, 10, "player1");
		boolean a = p.placeShip(5, 0, 0, Direction.VERTICAL);
		assertTrue(a);
		for(int i = 0; i < 5; ++i) {
			assertTrue(p.shipData(i, 0) != 0);
		}
		boolean a2 = p.placeShip(5, 0, 0, Direction.HORIZONTAL);
		assertFalse(a2);
		boolean a3 = p.placeShip(2, 0, 0, Direction.VERTICAL);
		assertFalse(a3);
		
		
		boolean b = p.placeShip(5, 0, 1, Direction.HORIZONTAL);
		assertTrue(b);
		for(int i = 0; i < 5; ++i) {
			assertTrue(p.shipData(0, i + 1) != 0);
		}
		
		boolean b2 = p.placeShip(5, 0, 1, Direction.HORIZONTAL);
		assertFalse(b2);
		boolean b3 = p.placeShip(2, 0, 1, Direction.VERTICAL);
		assertFalse(b3);
		
		boolean c = p.moveShip(0, 0, 3, 0);
		assertTrue(c);
		for(int i = 3; i < 8; ++i) {
			assertTrue(p.shipData(i, 0)!= 0);
		}
		p.removeShip(0, 1);
		for(int i = 1; i < 6; ++i) {
			assertTrue(p.shipData(0, i) == 0);
		}
		
		assertFalse(p.placeShip(5, 0, 9, Direction.HORIZONTAL));
	}
	
	@Test
	void receiveShotsTest() {
		p = new BattleshipPlayer(10, 10,"player1");
		boolean a = p.placeShip(5, 0, 0, Direction.VERTICAL);
		assertTrue(a);
		p.ready("player1");
		p.ready("player2");
		for(int i = 0; i < 4; ++i) {
			int s = p.receiveShot(i, 0);
			assertTrue(s == 1);
			assertFalse(p.noShipsRemain());
		}
		int s = p.receiveShot(4, 0);
		assertTrue(s == 2);
		int ship = p.shipData(0, 0);
		assertTrue(ship == 2);
		assertTrue(p.noShipsRemain());
//		for(int[] coords : ship.getCoords()) {
//			assertTrue(ship.isHit(coords[0], coords[1]));
//		}
		
		
		
	}
	
	@Test
	void testClearResize() {
		p = new BattleshipPlayer(10, 10, "player1");
		boolean a = p.placeShip(5, 0, 0, Direction.VERTICAL);
		assertTrue(a);
		p.resize(5, 5);
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < 5; ++j) {
				assertTrue(p.shipData(0, 0) == 0);
			}
		}
	}
	
	@Test
	void hitGridTest() {
		p = new BattleshipPlayer(10,10, "player1");
		p.ready("player1");
		p.ready("player2");
		p.updateHitGrid(0, 0, 1);
		p.updateHitGrid(0, 1, 1);

		p.updateHitGrid(0, 2, 1);

		p.updateHitGrid(0, 3, 1);

		p.updateHitGrid(0, 4, 2);

	}
	

}
