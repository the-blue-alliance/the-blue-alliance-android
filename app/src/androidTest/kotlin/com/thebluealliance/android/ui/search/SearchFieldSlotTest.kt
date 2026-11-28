package com.thebluealliance.android.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalMaterial3Api::class)
@RunWith(AndroidJUnit4::class)
class SearchFieldSlotTest {
    @get:Rule
    val rule = createComposeRule()

    private var fit by mutableStateOf<SearchFieldFit?>(null)
    private lateinit var icon: LayoutCoordinates

    // Search's top bar: the field in a TopAppBar title, an icon button's place at its end.
    private fun setSearchBar() {
        rule.setContent {
            Box(Modifier.width(600.dp)) {
                TopAppBar(
                    title = {
                        SearchFieldSlot(fit) {
                            TextField(
                                state = rememberTextFieldState("254"),
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    Box(Modifier.size(48.dp).onGloballyPositioned { icon = it })
                                },
                            )
                        }
                    },
                )
            }
        }
    }

    private fun drawnLeft() = icon.boundsInWindow(clipBounds = false).left

    @Test
    fun anIconInAnUnfittedFieldReportsWhereItIsDrawn() {
        setSearchBar()
        rule.runOnIdle { assertEquals(drawnLeft(), icon.positionInWindow().x, 0.5f) }
    }

    @Test
    fun anIconInAFieldFittedAfterItsFirstLayoutReportsWhereItIsDrawn() {
        setSearchBar()
        rule.runOnIdle { fit = SearchFieldFit(start = 100.dp, maxWidth = 300.dp) }
        rule.runOnIdle { assertEquals(drawnLeft(), icon.positionInWindow().x, 0.5f) }
    }

    @Test
    fun anIconInAFittedFieldReportsTheMoveTheFitMade() {
        setSearchBar()
        val before = rule.runOnIdle { icon.positionInWindow().x }
        val drawnBefore = rule.runOnIdle { drawnLeft() }
        rule.runOnIdle { fit = SearchFieldFit(start = 100.dp, maxWidth = 300.dp) }
        rule.runOnIdle {
            assertEquals(drawnLeft() - drawnBefore, icon.positionInWindow().x - before, 0.5f)
        }
    }
}
