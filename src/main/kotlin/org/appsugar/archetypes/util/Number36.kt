package org.appsugar.archetypes.util

data class Number36(val value: String = "0") {
    init {
        checkValue()
    }

    companion object {
        private val NUMBER_ARRAY = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'G', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')
        private val FIRST_NUMBER = NUMBER_ARRAY.first()
        private val LAST_NUMBER = NUMBER_ARRAY.last()
        private val NUMBER_POSITION_ARRAY = IntArray(123).apply {
            for (index in NUMBER_ARRAY.indices) {
                this[NUMBER_ARRAY[index].toInt()] = index
            }
        }

        fun valueOf(value: Long): Number36 {
            val sb = StringBuilder()
            val size = NUMBER_ARRAY.size
            var changedValue = value
            do {
                val position = changedValue % size
                sb.append(NUMBER_ARRAY[position.toInt()])
                changedValue /= size
                if (changedValue <= 0) break
            } while (true)
            return Number36(sb.reverse().toString())
        }
    }

    private fun checkValue() {
        val first = FIRST_NUMBER
        val last = LAST_NUMBER
        value.forEach { if (it !in first..last) throw IllegalArgumentException("$value is not a valid number36  invalid char is '$it'") }
    }

    fun toLong(): Long {
        val numberPosition = NUMBER_POSITION_ARRAY
        val v = value
        var total = numberPosition[value[value.lastIndex].toInt()].toLong()
        var times = 36
        for (i in v.length - 2 downTo 0) {
            val n = numberPosition[v[i].toInt()]
            total += n * times
            times *= 36
        }
        return total
    }

    fun increase(num: Int = 1) = valueOf(toLong() + num)
}