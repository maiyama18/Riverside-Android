package com.muijp.riverside.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.muijp.riverside.data.models.EntriesFilter

@Composable
fun EntriesFilter(
    selectedFilter: EntriesFilter,
    onFilterSelected: (EntriesFilter) -> Unit,
) {
    var filterMenuExpanded by remember { mutableStateOf(false) }
    IconButton(onClick = { filterMenuExpanded = !filterMenuExpanded }) {
        Icon(imageVector = Icons.Default.FilterList, contentDescription = null)
        DropdownMenu(
            expanded = filterMenuExpanded,
            onDismissRequest = { filterMenuExpanded = false },
        ) {
            EntriesFilter.entries.map { filter ->
                DropdownMenuItem(
                    text = { Text(filter.displayName) },
                    trailingIcon = {
                        if (selectedFilter == filter) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                            )
                        }
                    },
                    onClick = {
                        onFilterSelected(filter)
                        filterMenuExpanded = false
                    },
                )
            }
        }
    }
}

