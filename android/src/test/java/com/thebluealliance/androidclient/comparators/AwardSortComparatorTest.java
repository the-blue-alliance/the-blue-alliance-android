package com.thebluealliance.androidclient.comparators;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.thebluealliance.androidclient.models.Award;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class AwardSortComparatorTest {

    @Test
    public void testPrioritizedAwards() {
        // Make sure all the prioritized awards are in the correct order.
        // We will add them in reverse order, and verify we get the correct order out.
        List<Award> awards = new ArrayList<>();
        awards.add(mockAward(2, ""));
        awards.add(mockAward(1, ""));
        awards.add(mockAward(4, ""));
        awards.add(mockAward(5, ""));
        awards.add(mockAward(3, ""));
        awards.add(mockAward(10, ""));
        awards.add(mockAward(9, ""));
        awards.add(mockAward(6, ""));
        awards.add(mockAward(0, ""));

        Collections.sort(awards, new AwardSortComparator());

        assertEquals(0, (int) awards.get(0).getAwardType());
        assertEquals(6, (int) awards.get(1).getAwardType());
        assertEquals(9, (int) awards.get(2).getAwardType());
        assertEquals(10, (int) awards.get(3).getAwardType());
        assertEquals(3, (int) awards.get(4).getAwardType());
        assertEquals(5, (int) awards.get(5).getAwardType());
        assertEquals(4, (int) awards.get(6).getAwardType());
        assertEquals(1, (int) awards.get(7).getAwardType());
        assertEquals(2, (int) awards.get(8).getAwardType());
    }

    @Test
    public void testUnprioritizedAwards() {
        // Make sure all the prioritized awards come before unprioritized awards, and all
        // awards without a priority are sorted alphabetically
        List<Award> awards = new ArrayList<>();
        awards.add(mockAward(99, "Zebra"));
        awards.add(mockAward(2, "Middle"));
        awards.add(mockAward(99, "Apple"));

        Collections.sort(awards, new AwardSortComparator());

        assertEquals("Middle", awards.get(0).getName());
        assertEquals("Apple", awards.get(1).getName());
        assertEquals("Zebra", awards.get(2).getName());
    }

    private Award mockAward(int awardType, String name) {
        Award award = Mockito.mock(Award.class);
        Mockito.when(award.getAwardType()).thenReturn(awardType);
        Mockito.when(award.getName()).thenReturn(name);
        return award;
    }

}
