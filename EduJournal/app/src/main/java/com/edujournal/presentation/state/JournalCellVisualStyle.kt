package com.edujournal.presentation.state

enum class JournalCellTone {
    LOW,
    MID,
    HIGH,
    ABSENT,
    SICK,
    PASS,
    DEFAULT
}

data class JournalCellVisualStyle(
    val tone: JournalCellTone,
    val backgroundArgb: Int,
    val textArgb: Int
)

object JournalCellVisualStyles {

    private val low = JournalCellVisualStyle(
        tone = JournalCellTone.LOW,
        backgroundArgb = 0xFFFFDAD6.toInt(),
        textArgb = 0xFF7F1D1D.toInt()
    )
    private val mid = JournalCellVisualStyle(
        tone = JournalCellTone.MID,
        backgroundArgb = 0xFFFFECB3.toInt(),
        textArgb = 0xFF8A4B00.toInt()
    )
    private val high = JournalCellVisualStyle(
        tone = JournalCellTone.HIGH,
        backgroundArgb = 0xFFC8E6C9.toInt(),
        textArgb = 0xFF1B5E20.toInt()
    )
    private val absent = JournalCellVisualStyle(
        tone = JournalCellTone.ABSENT,
        backgroundArgb = 0xFFFFCC80.toInt(),
        textArgb = 0xFF6D3500.toInt()
    )
    private val sick = JournalCellVisualStyle(
        tone = JournalCellTone.SICK,
        backgroundArgb = 0xFF90CAF9.toInt(),
        textArgb = 0xFF0D47A1.toInt()
    )
    private val pass = JournalCellVisualStyle(
        tone = JournalCellTone.PASS,
        backgroundArgb = 0xFFB39DDB.toInt(),
        textArgb = 0xFF311B92.toInt()
    )
    val default = JournalCellVisualStyle(
        tone = JournalCellTone.DEFAULT,
        backgroundArgb = 0xFFFFFFFF.toInt(),
        textArgb = 0xFF000000.toInt()
    )

    fun forValue(value: String?): JournalCellVisualStyle {
        return when (value) {
            "1", "2", "3" -> low
            "4", "5", "6" -> mid
            "7", "8", "9", "10" -> high
            "Н" -> absent
            "З" -> sick
            "О" -> pass
            else -> default
        }
    }
}
