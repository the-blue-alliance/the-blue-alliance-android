package com.thebluealliance.androidclient.di;

import com.thebluealliance.androidclient.renderers.AwardRenderer;
import com.thebluealliance.androidclient.renderers.DistrictPointBreakdownRenderer;
import com.thebluealliance.androidclient.renderers.DistrictRenderer;
import com.thebluealliance.androidclient.renderers.DistrictTeamRenderer;
import com.thebluealliance.androidclient.renderers.EventRenderer;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.renderers.MediaRenderer;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;
import com.thebluealliance.androidclient.renderers.MyTbaModelRenderer;
import com.thebluealliance.androidclient.renderers.RendererModule;
import com.thebluealliance.androidclient.renderers.TeamRenderer;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;

@TestInstallIn(components = SingletonComponent.class, replaces = RendererModule.class)
@Module
public class MockRendererModule  {
    @Provides @Singleton
    public MyTbaModelRenderer provideMyTbaModelRenderer() {
        return Mockito.mock(MyTbaModelRenderer.class);
    }

    @Provides @Singleton
    public EventRenderer provideEventRenderer() {
        return Mockito.mock(EventRenderer.class);
    }

    @Provides @Singleton
    public AwardRenderer provideAwardRenderer() {
        return Mockito.mock(AwardRenderer.class);
    }

    @Provides @Singleton
    public TeamRenderer provideTeamRenderer() {
        return Mockito.mock(TeamRenderer.class);
    }

    @Provides @Singleton
    public MatchRenderer provideMatchRenderer() {
        return Mockito.mock(MatchRenderer.class);
    }

    @Provides @Singleton
    public DistrictRenderer provideDistrictRenderer() {
        return Mockito.mock(DistrictRenderer.class);
    }

    @Provides @Singleton
    public MediaRenderer provideMediaRenderer() {
        return Mockito.mock(MediaRenderer.class);
    }

    @Provides @Singleton
    public DistrictPointBreakdownRenderer provideDistrictPointBreakdownRenderer() {
        return Mockito.mock(DistrictPointBreakdownRenderer.class);
    }

    @Provides @Singleton
    public DistrictTeamRenderer provideDistrictTeamRenderer() {
        return Mockito.mock(DistrictTeamRenderer.class);
    }

    @Provides @Singleton
    public ModelRendererSupplier provideModelRendererSupplier() {
        return Mockito.mock(ModelRendererSupplier.class);
    }
}
