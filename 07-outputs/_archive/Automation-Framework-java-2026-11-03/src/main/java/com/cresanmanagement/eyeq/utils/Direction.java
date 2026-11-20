package com.cresanmanagement.eyeq.utils;

/**
 * Directional enum used by gesture helpers (scroll/swipe) in {@link Base}.
 * EyeQ Monitoring's list-heavy screens (Plate Scans feed, Vehicles registry, Tickets list) are the
 * primary consumers of UP/DOWN scrolling; LEFT/RIGHT are kept for any horizontally-swipeable
 * carousels/tabs discovered during Page Object implementation.
 */
public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
}
