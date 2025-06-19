package com.example.todo.data.model

import org.junit.Test
import org.junit.Assert.*

class PriorityTest {

    @Test
    fun `Priority enum should have correct values`() {
        val priorities = Priority.values()
        assertEquals(3, priorities.size)
        assertEquals(Priority.HIGH, priorities[0])
        assertEquals(Priority.NORMAL, priorities[1])
        assertEquals(Priority.LOW, priorities[2])
    }

    @Test
    fun `Priority HIGH should have correct display name and color`() {
        val priority = Priority.HIGH
        assertEquals("高", priority.displayName)
        assertEquals(android.R.color.holo_red_light, priority.colorRes)
    }

    @Test
    fun `Priority NORMAL should have correct display name and color`() {
        val priority = Priority.NORMAL
        assertEquals("普通", priority.displayName)
        assertEquals(android.R.color.holo_blue_light, priority.colorRes)
    }

    @Test
    fun `Priority LOW should have correct display name and color`() {
        val priority = Priority.LOW
        assertEquals("低", priority.displayName)
        assertEquals(android.R.color.holo_green_light, priority.colorRes)
    }

    @Test
    fun `Priority values should be ordinal correct`() {
        assertEquals(0, Priority.HIGH.ordinal)
        assertEquals(1, Priority.NORMAL.ordinal)
        assertEquals(2, Priority.LOW.ordinal)
    }

    @Test
    fun `Priority valueOf should work correctly`() {
        assertEquals(Priority.HIGH, Priority.valueOf("HIGH"))
        assertEquals(Priority.NORMAL, Priority.valueOf("NORMAL"))
        assertEquals(Priority.LOW, Priority.valueOf("LOW"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Priority valueOf should throw exception for invalid value`() {
        Priority.valueOf("INVALID")
    }
}