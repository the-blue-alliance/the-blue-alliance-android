package com.thebluealliance.androidclient.models;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EventWeekTabTest {

  @Test
  public void addEventSetsStartEndWeek() {
    EventWeekTab tab = new EventWeekTab("label");
    Event event = new Event();
    event.setWeek(5);
    tab.addEvent(event);

    assertFalse(tab.includesWeek(4));
    assertTrue(tab.includesWeek(5));
    assertFalse(tab.includesWeek(6));
  }

  @Test
  public void addMultipleEvents_multipleWeeks() {
    EventWeekTab tab = new EventWeekTab("label");

    Event event = new Event();
    event.setWeek(5);
    tab.addEvent(event);

    Event event2 = new Event();
    event2.setWeek(7);
    tab.addEvent(event2);

    assertFalse(tab.includesWeek(4));
    assertTrue(tab.includesWeek(5));
    assertTrue(tab.includesWeek(6));
    assertTrue(tab.includesWeek(7));
    assertFalse(tab.includesWeek(8));
  }
}