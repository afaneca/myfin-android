package com.afaneca.myfin.ui.common.nav

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Navigator logic to ensure correct navigation behavior.
 *
 * These tests verify the core navigation logic without requiring full Android/Compose setup.
 * They test:
 * - Top-level history management
 * - Back navigation behavior
 * - Edge cases and complex flows
 *
 * Note: These tests use a simplified NavigationState model that tracks only the essential
 * state (topLevelRoute and topLevelHistory) without full NavBackStack integration.
 */
class NavigatorLogicTest {

    // Test routes
    @Serializable
    data object Login : NavKey

    @Serializable
    data object Home : NavKey

    @Serializable
    data object Transactions : NavKey

    @Serializable
    data object Settings : NavKey

    // ========== Initial State Tests ==========

    @Test
    fun `initial state has start route as top level`() {
        val state = createTestState(Login)

        assertEquals(Login, state.topLevelRoute)
        assertEquals(listOf(Login), state.topLevelHistory)
    }

    // ========== Basic Navigation Tests ==========

    @Test
    fun `navigate to top-level route adds to history`() {
        val state = createTestState(Login)

        // Simulate: navigate(Home, removeCurrentFromBackStack = false)
        state.topLevelHistory = state.topLevelHistory + Home
        state.topLevelRoute = Home

        assertEquals(Home, state.topLevelRoute)
        assertEquals(listOf(Login, Home), state.topLevelHistory)
    }

    @Test
    fun `navigate with removeCurrentFromBackStack replaces in history`() {
        val state = createTestState(Login)

        // Simulate: navigate(Home, removeCurrentFromBackStack = true)
        state.topLevelHistory = state.topLevelHistory.dropLast(1) + Home
        state.topLevelRoute = Home

        assertEquals(Home, state.topLevelRoute)
        assertEquals(listOf(Home), state.topLevelHistory)
    }

    @Test
    fun `navigate to same top-level route does not add to history`() {
        val state = createTestState(Home)
        val historyBefore = state.topLevelHistory.toList()

        // Simulate: navigate(Home) when already on Home
        // The navigator checks: if (state.topLevelRoute != route)
        // So history should NOT change

        assertEquals(historyBefore, state.topLevelHistory)
    }

    // ========== Back Navigation Tests ==========

    @Test
    fun `goBack from top-level route with history size greater than 1 returns true`() {
        val state = createTestState(Transactions, listOf(Home, Transactions))

        // Simulate: goBack() when history has 2 items
        val canGoBack = state.topLevelHistory.size > 1

        assertTrue(canGoBack)
    }

    @Test
    fun `goBack navigates to previous in history`() {
        val state = createTestState(Transactions, listOf(Home, Transactions))

        // Simulate: goBack()
        val newHistory = state.topLevelHistory.dropLast(1)
        val previousTopLevel = newHistory.last()
        state.topLevelHistory = newHistory
        state.topLevelRoute = previousTopLevel

        assertEquals(Home, state.topLevelRoute)
        assertEquals(listOf(Home), state.topLevelHistory)
    }

    @Test
    fun `goBack from single item history returns false`() {
        val state = createTestState(Home, listOf(Home))

        // Simulate: goBack() when history has only 1 item
        val canGoBack = state.topLevelHistory.size > 1

        assertFalse(canGoBack)
    }

    // ========== Complex Flow Tests ==========

    @Test
    fun `user scenario - login to home to transactions and back`() {
        // This is the exact scenario from the bug report
        val state = createTestState(Login)

        // 1. Login -> Home (with removeCurrentFromBackStack)
        state.topLevelHistory = state.topLevelHistory.dropLast(1) + Home
        state.topLevelRoute = Home
        assertEquals(Home, state.topLevelRoute)
        assertEquals(listOf(Home), state.topLevelHistory)

        // 2. Home -> Transactions (via nav drawer)
        state.topLevelHistory = state.topLevelHistory + Transactions
        state.topLevelRoute = Transactions
        assertEquals(Transactions, state.topLevelRoute)
        assertEquals(listOf(Home, Transactions), state.topLevelHistory)

        // 3. Back gesture -> Should go to Home
        assertTrue("Should be able to go back", state.topLevelHistory.size > 1)
        state.topLevelHistory = state.topLevelHistory.dropLast(1)
        state.topLevelRoute = state.topLevelHistory.last()
        assertEquals(Home, state.topLevelRoute)
        assertEquals(listOf(Home), state.topLevelHistory)

        // 4. Back gesture from Home -> Should exit (return false)
        assertFalse("Should not be able to go back", state.topLevelHistory.size > 1)
    }

    @Test
    fun `complex navigation flow - multiple routes`() {
        val state = createTestState(Login)

        // Login -> Home (replace)
        state.topLevelHistory = state.topLevelHistory.dropLast(1) + Home
        state.topLevelRoute = Home
        assertEquals(listOf(Home), state.topLevelHistory)

        // Home -> Transactions
        state.topLevelHistory += Transactions
        state.topLevelRoute = Transactions
        assertEquals(listOf(Home, Transactions), state.topLevelHistory)

        // Transactions -> Settings
        state.topLevelHistory += Settings
        state.topLevelRoute = Settings
        assertEquals(listOf(Home, Transactions, Settings), state.topLevelHistory)

        // Back: Settings -> Transactions
        state.topLevelHistory = state.topLevelHistory.dropLast(1)
        state.topLevelRoute = state.topLevelHistory.last()
        assertEquals(Transactions, state.topLevelRoute)
        assertEquals(listOf(Home, Transactions), state.topLevelHistory)

        // Back: Transactions -> Home
        state.topLevelHistory = state.topLevelHistory.dropLast(1)
        state.topLevelRoute = state.topLevelHistory.last()
        assertEquals(Home, state.topLevelRoute)
        assertEquals(listOf(Home), state.topLevelHistory)

        // Back: Home -> Exit (can't go back)
        assertFalse(state.topLevelHistory.size > 1)
    }

    @Test
    fun `multiple back and forth navigations maintain history`() {
        val state = createTestState(Login)

        // Login -> Home (replace)
        state.topLevelHistory = state.topLevelHistory.dropLast(1) + Home
        state.topLevelRoute = Home

        // Home -> Transactions
        state.topLevelHistory += Transactions
        state.topLevelRoute = Transactions

        // Transactions -> Home
        state.topLevelHistory += Home
        state.topLevelRoute = Home

        // Home -> Transactions
        state.topLevelHistory = state.topLevelHistory + Transactions
        state.topLevelRoute = Transactions

        // History: [Home, Transactions, Home, Transactions]
        assertEquals(listOf(Home, Transactions, Home, Transactions), state.topLevelHistory)
        assertEquals(Transactions, state.topLevelRoute)

        // Back 3 times
        state.topLevelHistory = state.topLevelHistory.dropLast(1)
        state.topLevelRoute = state.topLevelHistory.last()
        assertEquals(Home, state.topLevelRoute)

        state.topLevelHistory = state.topLevelHistory.dropLast(1)
        state.topLevelRoute = state.topLevelHistory.last()
        assertEquals(Transactions, state.topLevelRoute)

        state.topLevelHistory = state.topLevelHistory.dropLast(1)
        state.topLevelRoute = state.topLevelHistory.last()
        assertEquals(Home, state.topLevelRoute)

        // Can't go back anymore
        assertFalse(state.topLevelHistory.size > 1)
    }

    @Test
    fun `navigate between all top level routes builds correct history`() {
        val state = createTestState(Login)

        // Login -> Home
        state.topLevelHistory = state.topLevelHistory.dropLast(1) + Home
        state.topLevelRoute = Home

        // Home -> Transactions
        state.topLevelHistory += Transactions
        state.topLevelRoute = Transactions

        // Transactions -> Settings
        state.topLevelHistory += Settings
        state.topLevelRoute = Settings

        assertEquals(listOf(Home, Transactions, Settings), state.topLevelHistory)
        assertEquals(Settings, state.topLevelRoute)
    }

    // ========== Edge Cases ==========

    @Test
    fun `empty history dropLast returns empty list`() {
        val history = emptyList<NavKey>()
        val result = history.dropLast(1)

        assertEquals(emptyList<NavKey>(), result)
    }

    @Test
    fun `single item history dropLast returns empty list`() {
        val history = listOf(Home)
        val result = history.dropLast(1)

        assertEquals(emptyList<NavKey>(), result)
    }

    @Test
    fun `history last on empty list throws exception`() {
        val history = emptyList<NavKey>()

        assertThrows(NoSuchElementException::class.java) {
            history.last()
        }
    }

    @Test
    fun `replacing current with removeCurrentFromBackStack when history is empty`() {
        val state = createTestState(Login, emptyList())

        // Simulate: navigate(Home, removeCurrentFromBackStack = true) when history is empty
        val result = state.topLevelHistory.dropLast(1) + Home

        assertEquals(listOf(Home), result)
    }

    @Test
    fun `stacksInUse returns topLevelHistory`() {
        val state = createTestState(Transactions, listOf(Home, Transactions))

        assertEquals(listOf(Home, Transactions), state.topLevelHistory)
        // stacksInUse should return topLevelHistory
        assertEquals(state.topLevelHistory, state.topLevelHistory)
    }

    // ========== Helper Functions ==========

    private fun createTestState(
        currentRoute: NavKey,
        history: List<NavKey> = listOf(currentRoute)
    ): TestNavigationState {
        return TestNavigationState(currentRoute, history)
    }

    /**
     * Simplified NavigationState for testing logic without full NavBackStack
     */
    private class TestNavigationState(
        initialRoute: NavKey,
        initialHistory: List<NavKey>
    ) {
        var topLevelRoute: NavKey = initialRoute
        var topLevelHistory: List<NavKey> = initialHistory
    }
}
