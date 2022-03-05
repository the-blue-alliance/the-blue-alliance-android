package com.thebluealliance.androidclient.renderers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.listitems.CardedAwardListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Award;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

@RunWith(AndroidJUnit4.class)
public class AwardRendererTest {

    private static final String AWARD_INDIVIDUAL = "award_individual";
    private static final String AWARD_TEAM = "award_team";

    @Mock APICache mDatafeed;

    private AwardRenderer mRenderer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mRenderer = new AwardRenderer(mDatafeed);
    }

    @Test
    public void testRenderIndividualCarded() {
        Award award = ModelMaker.getModel(Award.class, AWARD_INDIVIDUAL);
        ListItem rendered = mRenderer.renderFromModel(award, new AwardRenderer.RenderArgs(new HashMap<>(), null));

        assertNotNull(rendered);
        assertTrue(rendered instanceof CardedAwardListElement);
    }

    @Test
    public void testRenderTeamCarded() {
        Award award = ModelMaker.getModel(Award.class, AWARD_TEAM);
        ListItem rendered = mRenderer.renderFromModel(award, new AwardRenderer.RenderArgs(new HashMap<>(), null));

        assertNotNull(rendered);
        assertTrue(rendered instanceof CardedAwardListElement);
    }

}