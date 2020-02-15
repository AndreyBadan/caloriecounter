package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles
        Map<LocalDate, Integer> map = new HashMap<>();
        meals.forEach(meal -> {
            LocalDate ld = meal.getDateTime().toLocalDate();
            int calories = meal.getCalories();
            map.merge(ld, calories, Integer::sum);
        });

        List<UserMealWithExcess> mealWithExcesses = new ArrayList<>();
        meals.forEach(meal -> {
            LocalTime lt = meal.getDateTime().toLocalTime();
            if (TimeUtil.isBetweenInclusive(lt, startTime, endTime)) {
                LocalDate ldMeal = meal.getDateTime().toLocalDate();
                mealWithExcesses.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), map.get(ldMeal) > caloriesPerDay));
            }
        });
        return mealWithExcesses;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> map = meals.stream()
                .collect(Collectors.toMap(UserMeal::getDate, UserMeal::getCalories, Integer::sum));

        List<UserMealWithExcess> list = new ArrayList<>();
        meals.stream()
                .filter(meal -> TimeUtil.isBetweenInclusive(meal.getDateTime().toLocalTime(), startTime, endTime))
                .forEach(meal -> list.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), map.get(meal.getDate()) > caloriesPerDay)));
        return list;
    }
}
