package ru.mipt.bit.platformer;

import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.model.*;
import ru.mipt.bit.platformer.model.state.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class TankStateTest {
    private Tank tank(int health) {
        Tank tank = new Tank(new Cell(0, 0), 0.4f);
        tank.damage(100 - health);
        return tank;
    }

    @Test void selectsStatesAtExactHealthBoundaries() {
        for (int health : new int[] {100, 99, 71}) assertInstanceOf(LightTankState.class, tank(health).state());
        for (int health : new int[] {70, 50, 15}) assertInstanceOf(MediumTankState.class, tank(health).state());
        for (int health : new int[] {14, 1, 0}) assertInstanceOf(HeavyTankState.class, tank(health).state());
        assertInstanceOf(LightTankState.class, TankStates.forHealth(701, 1000));
        assertInstanceOf(MediumTankState.class, TankStates.forHealth(700, 1000));
        assertInstanceOf(MediumTankState.class, TankStates.forHealth(150, 1000));
        assertInstanceOf(HeavyTankState.class, TankStates.forHealth(149, 1000));
    }

    @Test void movesAtNormalHalfAndThirdSpeed() {
        int[] health = {100, 70, 14};
        float[] duration = {0.4f, 0.8f, 1.2f};
        for (int i = 0; i < health.length; i++) {
            Tank tank = tank(health[i]);
            tank.move(Direction.RIGHT, cell -> true);
            tank.update(duration[i] / 2);
            assertEquals(0.5f, tank.progress(), 0.00001f);
            assertEquals(new Cell(0, 0), tank.position());
            tank.update(duration[i] / 2 + 0.00001f);
            assertEquals(new Cell(1, 0), tank.position());
            assertEquals(1, tank.progress());
        }
    }

    @Test void damageChangesRemainingMovementWithoutResettingIt() {
        Tank tank = tank(100);
        tank.move(Direction.RIGHT, cell -> true);
        tank.update(0.1f);
        assertEquals(0.25f, tank.progress(), 0.00001f);
        tank.damage(30);
        assertEquals(0.25f, tank.progress(), 0.00001f);
        tank.update(0.2f);
        assertEquals(0.5f, tank.progress(), 0.00001f);
        tank.damage(56);
        tank.update(0.3f);
        assertEquals(0.75f, tank.progress(), 0.00001f);
        tank.update(0.31f);
        assertEquals(new Cell(1, 0), tank.position());
    }

    @Test void onlyHeavyStateDisablesShooting() {
        for (int health : new int[] {100, 71, 70, 15, 14, 1, 0}) {
            Tank tank = tank(health);
            List<GameObject> shots = new ArrayList<>();
            WorldAccess port = port(shots);
            tank.shoot(port);
            assertEquals(health >= 15 ? 1 : 0, shots.size(), "health=" + health);
        }
    }

    @Test void takingDamageDisablesNextShotButDoesNotRemoveExistingBullet() {
        Tank tank = tank(100);
        GameWorld world = new GameWorld(new Field(10, 2, List.of()), List.of(tank));
        tank.shoot(world);
        tank.damage(86);
        tank.update(1);
        tank.shoot(world);
        assertEquals(1, world.objects().stream().filter(Bullet.class::isInstance).count());
    }

    @Test void aNewStateCanChangeMovementAndReloadWithoutChangingTank() {
        Tank tank = tank(100);
        tank.changeState(new TankState() {
            public float movementSpeed() { return 2; }
            public float reloadSpeed() { return 2; }
        });
        List<GameObject> shots = new ArrayList<>();
        WorldAccess port = port(shots);
        tank.shoot(port);
        tank.move(Direction.RIGHT, port);
        tank.update(0.1f);
        assertEquals(0.5f, tank.progress(), 0.00001f);
        tank.shoot(port);
        assertEquals(1, shots.size());
        tank.update(0.1f);
        tank.shoot(port);
        assertEquals(2, shots.size());
        assertEquals(new Cell(1, 0), tank.position());
    }

    @Test void normalReloadIsUnchangedInLightAndMediumStates() {
        for (int health : new int[] {100, 70, 15}) {
            Tank tank = tank(health);
            List<GameObject> shots = new ArrayList<>();
            WorldAccess port = port(shots);
            tank.shoot(port);
            tank.update(0.2f);
            tank.shoot(port);
            assertEquals(1, shots.size());
            tank.update(0.2f);
            tank.shoot(port);
            assertEquals(2, shots.size());
        }
    }

    @Test void stateDoesNotBypassObstaclesOrDeath() {
        for (int health : new int[] {100, 70, 14}) {
            Tank tank = tank(health);
            tank.move(Direction.UP, cell -> false);
            tank.update(2);
            assertEquals(new Cell(0, 0), tank.position());
        }
        Tank tank = tank(0);
        tank.changeState(LightTankState.INSTANCE);
        tank.move(Direction.RIGHT, cell -> true);
        tank.update(2);
        List<GameObject> shots = new ArrayList<>();
        tank.shoot(port(shots));
        assertEquals(new Cell(0, 0), tank.position());
        assertTrue(shots.isEmpty());
    }

    @Test void rejectsInvalidHealthAndNullState() {
        assertThrows(IllegalArgumentException.class, () -> TankStates.forHealth(1, 0));
        assertThrows(IllegalArgumentException.class, () -> TankStates.forHealth(-1, 100));
        assertThrows(IllegalArgumentException.class, () -> TankStates.forHealth(101, 100));
        assertThrows(NullPointerException.class, () -> tank(100).changeState(null));
        Tank tank = tank(100);
        assertThrows(IllegalArgumentException.class, () -> tank.damage(-1));
        assertInstanceOf(LightTankState.class, tank.state());
    }

    private WorldAccess port(List<GameObject> objects) {
        return new WorldAccess() {
            public boolean contains(GameObject object) { return true; }
            public void add(GameObject object) { objects.add(object); }
            public void remove(GameObject object) { objects.remove(object); }
            public boolean collide(Bullet bullet) { return false; }
            public boolean canEnter(Cell cell) { return true; }
        };
    }
}
