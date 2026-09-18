package org.phireox.ofa.feature.tool.form

enum class FieldType {
    TEXT,
    MULTILINE,
    NUMBER,
    INTEGER,
    BOOLEAN,
    DROPDOWN,
    DATE,
    FILE
}

data class FieldSpec(
    val key: String,
    val label: String,
    val type: FieldType = FieldType.TEXT,
    val defaultValue: String = "",
    val options: List<String> = emptyList()
)
