package org.phireox.ofa.feature.vault

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.phireox.ofa.core.security.TotpGenerator
import org.phireox.ofa.data.local.VaultRepository
import org.phireox.ofa.data.model.VaultItem
import org.phireox.ofa.data.model.VaultItemType

class VaultViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = VaultRepository(app)
    val items = mutableStateListOf<VaultItem>()
    val loading = mutableStateOf(true)
    val totpCodes = mutableMapOf<String, String>()
    val remainingSeconds = mutableStateOf(30)

    init {
        load()
        startTicker()
    }

    private fun load() {
        viewModelScope.launch(Dispatchers.Default) {
            val loaded = repository.load()
            withContext(Dispatchers.Main) {
                items.clear()
                items.addAll(loaded)
                loading.value = false
                refreshTotp()
            }
        }
    }

    fun save(item: VaultItem) {
        viewModelScope.launch(Dispatchers.Default) {
            val current = items.toMutableList()
            val index = current.indexOfFirst { it.id == item.id }
            if (index >= 0) current[index] = item else current.add(item)
            repository.save(current)
            withContext(Dispatchers.Main) {
                items.clear()
                items.addAll(current)
                refreshTotp()
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val current = items.filter { it.id != id }
            repository.save(current)
            withContext(Dispatchers.Main) {
                items.clear()
                items.addAll(current)
                refreshTotp()
            }
        }
    }

    private fun refreshTotp() {
        val map = mutableMapOf<String, String>()
        items.filter { it.type == VaultItemType.TOTP }.forEach { item ->
            map[item.id] = TotpGenerator.generate(item.secret)
        }
        totpCodes.clear()
        totpCodes.putAll(map)
        remainingSeconds.value = TotpGenerator.remainingSeconds()
    }

    private fun startTicker() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                refreshTotp()
            }
        }
    }
}
