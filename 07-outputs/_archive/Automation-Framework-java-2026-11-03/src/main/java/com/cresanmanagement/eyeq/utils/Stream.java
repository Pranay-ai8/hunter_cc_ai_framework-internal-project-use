package com.cresanmanagement.eyeq.utils;

import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Small java.util.stream helpers for the common "filter a list of WebElements/strings by text"
 * pattern that shows up repeatedly across list-heavy screens (Plate Scans feed, Vehicles registry,
 * Tickets list, Device list).
 */
public final class Stream {

    private Stream() {
    }

    public static List<String> textsOf(List<WebElement> elements) {
        return elements.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public static List<WebElement> filter(List<WebElement> elements, Predicate<WebElement> predicate) {
        return elements.stream().filter(predicate).collect(Collectors.toList());
    }

    public static boolean allMatch(List<String> values, Predicate<String> predicate) {
        return values.stream().allMatch(predicate);
    }

    public static boolean anyMatch(List<String> values, Predicate<String> predicate) {
        return values.stream().anyMatch(predicate);
    }

    public static long countMatching(List<String> values, Predicate<String> predicate) {
        return values.stream().filter(predicate).count();
    }
}
