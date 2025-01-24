package org.poseidonplugins.zcore

import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.poseidonplugins.zcore.api.Economy
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.mocks.MockPlayer
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.BalanceOutOfBoundsException
import org.poseidonplugins.zcore.util.NoFundsException
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class EconomyTest : ZCoreTest {

    @Test
    fun `test setting balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        player1.joinServer()
        val user1 = User.from(player1)

        Economy.setBalance(user1.uuid, 5000.0)
        assertEquals(5000.0, user1.balance)

        Economy.setBalance(user1.uuid, 2500.5)
        assertEquals(2500.5, user1.balance)

        Economy.setBalance(user1.uuid, 5999.994)
        assertEquals(5999.99, user1.balance)

        Economy.setBalance(user1.uuid, 6999.995)
        assertEquals(7000.0, user1.balance)

        Economy.setBalance(user1.uuid, -100.0)
        assertEquals(0.0, user1.balance)

        assertThrows<BalanceOutOfBoundsException> {
            Economy.setBalance(user1.uuid,
                Config.getDouble("maxBalance", 0.0, Economy.MAX_BALANCE) + 1.0
            )
        }
        assertDoesNotThrow {
            Economy.setBalance(user1.uuid,
                Config.getDouble("maxBalance", 0.0, Economy.MAX_BALANCE)
            )
        }
    }

    @Test
    fun `test adding balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        player1.joinServer()
        val user1 = User.from(player1)

        Economy.addBalance(user1.uuid, 5000.0)
        assertEquals(5000.0, user1.balance)

        Economy.addBalance(user1.uuid, 2500.5)
        assertEquals(7500.5, user1.balance)

        Economy.addBalance(user1.uuid, 5999.994)
        assertEquals(13500.49, user1.balance)

        Economy.addBalance(user1.uuid, 6999.995)
        assertEquals(20500.49, user1.balance)

        Economy.addBalance(user1.uuid, -100.0)
        assertEquals(20400.49, user1.balance)

        Economy.setBalance(user1.uuid,
            Config.getDouble("maxBalance", 0.0, Economy.MAX_BALANCE) - 100.0
        )
        assertThrows<BalanceOutOfBoundsException> {
            Economy.addBalance(user1.uuid, 100.005)
        }
        assertDoesNotThrow {
            Economy.addBalance(user1.uuid, 100.004)
        }
    }

    @Test
    fun `test subtracting balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        player1.joinServer()
        val user1 = User.from(player1)
        Economy.setBalance(user1.uuid, 50000.0)

        Economy.subtractBalance(user1.uuid, 5000.0)
        assertEquals(45000.0, user1.balance)

        Economy.subtractBalance(user1.uuid, 2500.5)
        assertEquals(42499.5, user1.balance)

        Economy.subtractBalance(user1.uuid, 5999.994)
        assertEquals(36499.51, user1.balance)

        Economy.subtractBalance(user1.uuid, 6999.995)
        assertEquals(29499.51, user1.balance)

        Economy.subtractBalance(user1.uuid, -100.0)
        assertEquals(29599.51, user1.balance)

        assertThrows<NoFundsException> {
            Economy.subtractBalance(user1.uuid, 29599.515)
        }
        assertDoesNotThrow {
            Economy.subtractBalance(user1.uuid, 29599.514)
        }
    }

    @Test
    fun `test transferring balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        val player2 = MockPlayer(UUID.randomUUID(), "Player2")
        player1.joinServer()
        player2.joinServer()

        val user1 = User.from(player1)
        val user2 = User.from(player2)
        Economy.setBalance(user1.uuid, 30000.0)
        Economy.setBalance(user2.uuid, 25000.0)

        Economy.transferBalance(user1.uuid, user2.uuid, 5000.0)
        assertEquals(25000.0, user1.balance)
        assertEquals(30000.0, user2.balance)

        Economy.transferBalance(user2.uuid, user1.uuid, 2500.5)
        assertEquals(27500.5, user1.balance)
        assertEquals(27499.5, user2.balance)

        Economy.transferBalance(user1.uuid, user2.uuid, 5999.994)
        assertEquals(21500.51, user1.balance)
        assertEquals(33499.49, user2.balance)

        Economy.transferBalance(user2.uuid, user1.uuid, 6999.995)
        assertEquals(28500.51, user1.balance)
        assertEquals(26499.49, user2.balance)

        Economy.transferBalance(user1.uuid, user2.uuid, -100.0)
        assertEquals(28600.51, user1.balance)
        assertEquals(26399.49, user2.balance)

        assertThrows<NoFundsException> {
            Economy.transferBalance(user1.uuid, user2.uuid, 28600.515)
        }
        assertDoesNotThrow {
            Economy.transferBalance(user1.uuid, user2.uuid, 28600.514)
        }

        Economy.setBalance(user1.uuid, 5000.0)
        Economy.setBalance(user2.uuid,
            Config.getDouble("maxBalance", 0.0, Economy.MAX_BALANCE) - 100.0
        )
        assertThrows<BalanceOutOfBoundsException> {
            Economy.transferBalance(user1.uuid, user2.uuid, 100.005)
        }
        assertDoesNotThrow {
            Economy.transferBalance(user1.uuid, user2.uuid, 100.004)
        }
    }
}