package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.api.model.IAwardRecipient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class AwardTest {
    private Award mTeamAward;
    private Award mIndividualAward;

    @Before
    public void readJsonData(){
        mIndividualAward = ModelMaker.getModel(Award.class, "award_individual");
        mTeamAward = ModelMaker.getModel(Award.class, "award_team");
    }

    @Test
    public void testTeamAward()  {
        assertNotNull(mTeamAward);
        assertEquals(mTeamAward.getEventKey(), "2015cthar");
        assertNotNull(mTeamAward.getEnum());
        assertEquals(mTeamAward.getEnum().intValue(), 17);
        assertEquals(mTeamAward.getName(), "Quality Award sponsored by Motorola");
        assertEquals(mTeamAward.getYear().intValue(), 2015);

        List<IAwardRecipient> recipientList = mTeamAward.getRecipientList();
        assertNotNull(recipientList);
        assertEquals(recipientList.size(), 1);

        IAwardRecipient recipient = recipientList.get(0);
        assertEquals(recipient.getTeamKey(), "frc195");
        assertNull(recipient.getAwardee());
    }

    @Test
    public void testIndividualAward()  {
        assertNotNull(mIndividualAward);
        assertEquals(mIndividualAward.getEventKey(), "2015necmp");
        assertNotNull(mIndividualAward.getEnum());
        assertEquals(mIndividualAward.getEnum().intValue(), 5);
        assertEquals(mIndividualAward.getName(), "Volunteer of the Year");
        assertEquals(mIndividualAward.getYear().intValue(), 2015);

        List<IAwardRecipient> recipientList = mIndividualAward.getRecipientList();
        assertNotNull(recipientList);
        assertEquals(recipientList.size(), 1);

        IAwardRecipient recipient = recipientList.get(0);
        assertEquals(recipient.getTeamKey(), "frc319");
        assertEquals(recipient.getAwardee(), "Ty Tremblay");
    }
}
