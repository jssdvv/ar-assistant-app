package com.jssdvv.ara.tools.presentation.destination.tools

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.PathParser
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.jssdvv.ara.core.domain.repository.FilesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val filesManager: FilesManager
) : ViewModel() {
    private val svgUriList = listOf(
        "file:///android_asset/vector/screwdriver/body_slotted_bit.svg",
        "file:///android_asset/vector/screwdriver/body_hexagon_bit.svg",
        "file:///android_asset/vector/screwdriver/body_phillips_bit.svg",
        "file:///android_asset/vector/screwdriver/body_pozidriv_bit.svg",
        "file:///android_asset/vector/pliers/body_round_nose.svg",
        "file:///android_asset/vector/pliers/body_flat_nose.svg",
        "file:///android_asset/vector/pliers/body_snipe_nose.svg",
    )

    private val _paths = MutableStateFlow<List<Path>>(emptyList())
    val paths = _paths.asStateFlow()

    private var currentIndex = 0

    init {
        _paths.value = loadSVG(svgUriList[currentIndex])
    }

    private fun loadSVG(uri: String): List<Path> {

        val pathStringArray = filesManager.getSvgTextPaths(uri.toUri())
        return pathStringArray.map { PathParser().parsePathString(it).toPath() }
    }

    fun changePath(){
        currentIndex = (currentIndex + 1) % svgUriList.size
        _paths.value = loadSVG(svgUriList[currentIndex])
    }
}