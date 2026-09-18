package org.phireox.ofa.feature.tool

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.phireox.ofa.data.model.Tool
import org.phireox.ofa.data.model.ToolRegistry
import org.phireox.ofa.data.model.ToolType
import org.phireox.ofa.engine.FileToolProcessor
import org.phireox.ofa.engine.ToolProcessor
import org.phireox.ofa.engine.ToolResult

data class ToolUiState(
    val tool: Tool? = null,
    val input: String = "",
    val output: String = "",
    val qrBitmap: Bitmap? = null,
    val loading: Boolean = false,
    val error: String? = null
)

class ToolViewModel(toolId: String, app: Application) : AndroidViewModel(app) {

    val state = mutableStateOf(ToolUiState(tool = ToolRegistry.byId(toolId)))

    fun updateInput(input: String) {
        state.value = state.value.copy(input = input, error = null)
    }

    fun process(params: Map<String, String> = emptyMap()) {
        val tool = state.value.tool ?: return
        state.value = state.value.copy(loading = true, error = null, output = "", qrBitmap = null)
        viewModelScope.launch(Dispatchers.Default) {
            try {
                when (tool.toolType) {
                    ToolType.QR_GENERATOR -> {
                        val bitmap = ToolProcessor.generateQrBitmap(state.value.input, 512)
                        withContext(Dispatchers.Main) {
                            state.value = state.value.copy(qrBitmap = bitmap, loading = false)
                        }
                    }
                    else -> {
                        val result = ToolProcessor.process(tool, state.value.input, params)
                        withContext(Dispatchers.Main) {
                            when (result) {
                                is ToolResult.Text -> state.value = state.value.copy(output = result.value, loading = false)
                                is ToolResult.Error -> state.value = state.value.copy(error = result.message, loading = false)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    state.value = state.value.copy(error = e.localizedMessage, loading = false)
                }
            }
        }
    }

    fun processFile(uri: Uri, params: Map<String, String> = emptyMap()) {
        val tool = state.value.tool ?: return
        state.value = state.value.copy(loading = true, error = null, output = "", qrBitmap = null)
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = FileToolProcessor.process(getApplication(), tool, uri, params)
                withContext(Dispatchers.Main) {
                    when (result) {
                        is ToolResult.Text -> state.value = state.value.copy(output = result.value, loading = false)
                        is ToolResult.Error -> state.value = state.value.copy(error = result.message, loading = false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    state.value = state.value.copy(error = e.localizedMessage, loading = false)
                }
            }
        }
    }
}
