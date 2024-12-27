package com.jssdvv.ara.inventory.presentation

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.jssdvv.ara.R
import com.jssdvv.ara.inventory.domain.models.InventoryTabItem
import com.jssdvv.ara.inventory.presentation.component.TabPager
import kotlinx.coroutines.launch

@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
) {

}

@Composable
fun InventoriesLoadingScreen() {

}

@Composable
fun InventoriesSuccessScreen() {
    val tabItems = listOf(
        InventoryTabItem(
            title = "Store",
            unselectedIconId = R.drawable.store_outlined,
            selectedIconId = R.drawable.store_filled
        ),
        InventoryTabItem(
            title = "Tools",
            unselectedIconId = R.drawable.tools_outlined,
            selectedIconId = R.drawable.tools_filled
        ),
    )
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState { tabItems.size }
    TabPager(
        pagerState = pagerState,
        tabs = {
            tabItems.forEachIndexed { index, tabItem ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    text = {
                        Text(
                            text = tabItem.title,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (pagerState.currentPage == index) tabItem.selectedIconId else tabItem.unselectedIconId
                            ),
                            contentDescription = null
                        )
                    }
                )
            }

        }
    ) { index ->
        when (index) {
            0 -> {}
            1 -> {}
        }
    }
}
