package it.unipd.footbyfoot

import it.unipd.footbyfoot.database.goal.Goal
import it.unipd.footbyfoot.database.userinfo.UserInfo
import it.unipd.footbyfoot.database.workout.Distance
import it.unipd.footbyfoot.fragments.Helpers
import org.junit.Test

import org.junit.Assert.*
import java.time.LocalDate

/**
 * Tests on Helpers class: we test only the methods that involve calculations, not the ones
 * that are more easily tested on device
 *
 */
class HelpersTest {
    @Test
    fun calculatePercentage() {
        assertEquals(54, Helpers.calculatePercentage(54.0, 100.0))
        assertEquals(33, Helpers.calculatePercentage(33.0, 100.0))
        assertEquals(100, Helpers.calculatePercentage(100.0, 100.0))
        assertEquals(0, Helpers.calculatePercentage(0.0, 100.0))
        assertEquals(0, Helpers.calculatePercentage(54.0, 0.0))
        assertEquals(9, Helpers.calculatePercentage(11.0, 121.0))
        assertEquals(100, Helpers.calculatePercentage(250.0, 100.0))
    }

    @Test
    fun calculateCalories() {
        assertEquals(4.9, Helpers.calculateCalories(54, 100), 0.0)
        assertEquals(84.2, Helpers.calculateCalories(120, 780), 0.0)
        assertEquals(0.0, Helpers.calculateCalories(0, 100), 0.0)
        assertEquals(0.0, Helpers.calculateCalories(54, 0), 0.0)
    }

    @Test
    fun calculateSteps() {
        assertEquals(134, Helpers.calculateSteps(180, 100))
        assertEquals(0, Helpers.calculateSteps(0, 100))
        assertEquals(0, Helpers.calculateSteps(180, 0))
        assertEquals(6053, Helpers.calculateSteps(100, 2500))
    }

    //The formatting strings (of date and time) are not tested as the dates depend on the locale and the others can be seen on the device

    @Test
    fun getSecondsFromTime() {
        assertEquals(9192, Helpers.getSecondsFromTime(2, 33, 12))
        assertEquals(0, Helpers.getSecondsFromTime(0, 0, 0))
        assertEquals(88392, Helpers.getSecondsFromTime(24, 33, 12))
        assertEquals(7992, Helpers.getSecondsFromTime(2, 13, 12))
        assertEquals(9181, Helpers.getSecondsFromTime(2, 33, 1))
    }

    @Test
    fun getSecondsMinutesHours() {
        //Seconds
        assertEquals(12, Helpers.getSeconds(Helpers.getSecondsFromTime(2, 33, 12)))
        assertEquals(0, Helpers.getSeconds(3600))
        assertEquals(0, Helpers.getSeconds(0))
        assertEquals(12, Helpers.getSeconds(9192))

        //Hours
        assertEquals(2, Helpers.getHours(Helpers.getSecondsFromTime(2, 33, 12)))
        assertEquals(1, Helpers.getHours(3600))
        assertEquals(0, Helpers.getHours(0))
        assertEquals(2, Helpers.getHours(9192))
        assertEquals(0, Helpers.getHours(60))

        //Minutes
        assertEquals(33, Helpers.getMinutes(Helpers.getSecondsFromTime(2, 33, 12)))
        assertEquals(0, Helpers.getMinutes(3600))
        assertEquals(0, Helpers.getMinutes(0))
        assertEquals(33, Helpers.getMinutes(9192))
        assertEquals(1, Helpers.getMinutes(60))
        assertEquals(1, Helpers.getMinutes(88))
    }

    @Test
    fun getGoalOfDate() {
        val list = listOf(
            Goal(2003, 20, 33, 33, 33),
            Goal(2002, 59, 1000, 1000, 1000),
            Goal(2002, 27, 500, 500, 500),
            Goal(2002, 24, 200, 200, 200),
            Goal(2002, 23, 100, 100, 100)
        )

        assertEquals(list[1], Helpers.getGoalOfDate(list, LocalDate.ofYearDay(2002, 60)))
        assertEquals(list[0], Helpers.getGoalOfDate(list, LocalDate.ofYearDay(2009, 11)))
        assertEquals(list[1], Helpers.getGoalOfDate(list, LocalDate.ofYearDay(2003, 19)))
        assertEquals(list[0], Helpers.getGoalOfDate(list, LocalDate.ofYearDay(2003, 20)))
        assertEquals(list[0], Helpers.getGoalOfDate(list, LocalDate.ofYearDay(2003, 21)))
        assertEquals(list[2], Helpers.getGoalOfDate(list, LocalDate.ofYearDay(2002, 28)))
        assertEquals(list[2], Helpers.getGoalOfDate(list, LocalDate.ofYearDay(2002, 27)))
        assertEquals(list[3], Helpers.getGoalOfDate(list, LocalDate.ofYearDay(2002, 25)))
        assertEquals(list[3], Helpers.getGoalOfDate(list, LocalDate.ofYearDay(2002, 24)))
        assertEquals(list[4], Helpers.getGoalOfDate(list, LocalDate.ofYearDay(2002, 23)))
        assertEquals(Helpers.defaultGoal, Helpers.getGoalOfDate(list, LocalDate.ofYearDay(2002, 22)))
        assertEquals(Helpers.defaultGoal, Helpers.getGoalOfDate(list, LocalDate.ofYearDay(20, 65)))
        assertEquals(Helpers.defaultGoal, Helpers.getGoalOfDate(list, LocalDate.ofYearDay(498, 1)))
    }

    @Test
    fun getInfoOfDate() {
        val list = listOf(
            UserInfo(2003, 20, 33, 33),
            UserInfo(2002, 59, 1000, 1000),
            UserInfo(2002, 27, 500, 500),
            UserInfo(2002, 24, 200, 200),
            UserInfo(2002, 23, 100, 100)
        )

        assertEquals(list[1], Helpers.getInfoOfDate(list, LocalDate.ofYearDay(2002, 60)))
        assertEquals(list[0], Helpers.getInfoOfDate(list, LocalDate.ofYearDay(2009, 11)))
        assertEquals(list[1], Helpers.getInfoOfDate(list, LocalDate.ofYearDay(2003, 19)))
        assertEquals(list[0], Helpers.getInfoOfDate(list, LocalDate.ofYearDay(2003, 20)))
        assertEquals(list[0], Helpers.getInfoOfDate(list, LocalDate.ofYearDay(2003, 21)))
        assertEquals(list[2], Helpers.getInfoOfDate(list, LocalDate.ofYearDay(2002, 28)))
        assertEquals(list[2], Helpers.getInfoOfDate(list, LocalDate.ofYearDay(2002, 27)))
        assertEquals(list[3], Helpers.getInfoOfDate(list, LocalDate.ofYearDay(2002, 25)))
        assertEquals(list[3], Helpers.getInfoOfDate(list, LocalDate.ofYearDay(2002, 24)))
        assertEquals(list[4], Helpers.getInfoOfDate(list, LocalDate.ofYearDay(2002, 23)))
    }

    @Test
    fun getDistanceOfDate() {
        val list = listOf(
            Distance(33, 2003, 20),
            Distance(1000, 2003, 19),
            Distance(500, 2003, 18),
            Distance(200, 2003, 17),
            Distance(100, 2003, 16),
            Distance(99, 2003, 15),
        )

        assertEquals(list[0].meters, Helpers.getDistanceMetersOfDateInWeek(list, LocalDate.ofYearDay(2003, 20)))
        assertEquals(list[0].meters, Helpers.getDistanceMetersOfDateInWeek(list, LocalDate.ofYearDay(2003, 20).plusDays(7L)))
        assertEquals(list[0].meters, Helpers.getDistanceMetersOfDateInWeek(list, LocalDate.ofYearDay(2003, 20).plusDays(994L)))
        assertEquals(list[1].meters, Helpers.getDistanceMetersOfDateInWeek(list, LocalDate.ofYearDay(2003, 19)))
        assertEquals(list[1].meters, Helpers.getDistanceMetersOfDateInWeek(list, LocalDate.ofYearDay(2003, 19).plusDays(868L)))
        assertEquals(list[2].meters, Helpers.getDistanceMetersOfDateInWeek(list, LocalDate.ofYearDay(2003, 18)))
        assertEquals(list[2].meters, Helpers.getDistanceMetersOfDateInWeek(list, LocalDate.ofYearDay(2003, 18).plusDays(21L)))
        assertEquals(list[3].meters, Helpers.getDistanceMetersOfDateInWeek(list, LocalDate.ofYearDay(2003, 17)))
        assertEquals(list[3].meters, Helpers.getDistanceMetersOfDateInWeek(list, LocalDate.ofYearDay(2003, 17).plusDays(42L)))
        assertEquals(list[4].meters, Helpers.getDistanceMetersOfDateInWeek(list, LocalDate.ofYearDay(2003, 16)))
        assertEquals(list[4].meters, Helpers.getDistanceMetersOfDateInWeek(list, LocalDate.ofYearDay(2003, 16).plusDays(7000L)))
        assertEquals(list[5].meters, Helpers.getDistanceMetersOfDateInWeek(list, LocalDate.ofYearDay(2003, 15)))
        assertEquals(list[5].meters, Helpers.getDistanceMetersOfDateInWeek(list, LocalDate.ofYearDay(2003, 15).plusDays(105L)))
        assertEquals(0, Helpers.getDistanceMetersOfDateInWeek(list, LocalDate.ofYearDay(2003, 14)))
        assertEquals(0, Helpers.getDistanceMetersOfDateInWeek(list, LocalDate.ofYearDay(2003, 14).plusDays(7L)))
        assertEquals(0, Helpers.getDistanceMetersOfDateInWeek(list, LocalDate.ofYearDay(2003, 14).plusDays(1015L)))
    }

    //The increments and decrements of views are not tested as they are easily seen on device

}