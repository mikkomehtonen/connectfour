package connectfour

import java.lang.IllegalArgumentException

enum class Disc(val char: Char) {
    Disc1('o'),
    Disc2('*'),
    Empty(' '),
}

class Board(columns: Int, rows: Int) {

    private val contents = BoardContents(columns = columns, rows = rows)

    fun addDisc(column: Int, disc: Disc) {
        if (column !in 1..contents.columns)  {
            throw IllegalArgumentException("The column number is out of range (1 - ${contents.columns})")
        }
        val row = firstFreeRow(column)
        if (row == NO_FREE_CELLS_ON_ROW) {
            throw IllegalArgumentException("Column $column is full")
        }
        contents.setDiscAt(disc, column, row)
    }

    fun isWinCondition(): Boolean {
        // Check columns
        for (column in 1..contents.columns) {
            val columnAsString = contents.getColumnAsString(column = column)
            if (winRegex.containsMatchIn(columnAsString)) {
                return true
            }
        }

        // check rows
        for (row in 1..contents.rows) {
            val rowAsString = contents.getRowAsString(row = row)
            if (winRegex.containsMatchIn(rowAsString)) {
                return true
            }
        }

        // Check diagonals
        for (row in 1..(contents.rows - 3)) {
            for (column in 1..(contents.columns - 3)) {
                val diagonalAsString = contents.getDownwardDiagonalAsString(startColumn = column, startRow = row)
                if (winRegex.containsMatchIn(diagonalAsString)) {
                    return true
                }
            }
        }
        for (row in contents.rows downTo 4) {
            for (column in 1..(contents.columns - 3)) {
                val diagonalAsString = contents.getUpwardsDiagonalAsString(startColumn = column, startRow = row)
                if (winRegex.containsMatchIn(diagonalAsString)) {
                    return true
                }
            }
        }
        return false
    }

    fun isBoardFull(): Boolean {
        for (row in 1..contents.rows) {
            for (column in 1..contents.columns) {
                if (contents.discAt(column = column, row = row) == Disc.Empty) {
                    return false
                }
            }
        }
        return true
    }

    fun printBoard() {
        for (i in 1..contents.columns) {
            print(" $i")
        }
        println()
        for (row in 1..contents.rows) {
            for (column in 1..contents.columns) {
                print("$verticalLine${contents.discAt(column = column, row = row).char}")
            }
            println(verticalLine)
        }
        print(leftCorner)
        for (i in 1 until contents.columns) {
            print("$horizontalLine$middleCorner")
        }
        println("$horizontalLine$rightCorner")
    }

    fun reset() {
        for (row in 1..contents.rows) {
            for (column in 1..contents.columns) {
                contents.setDiscAt(disc = Disc.Empty, column = column, row = row)
            }
        }
    }

    override fun toString(): String {
        return "${contents.rows} X ${contents.columns}"
    }

    private fun firstFreeRow(column: Int): Int {
        for (row in contents.rows downTo 1) {
            if (contents.discAt(column = column, row = row) == Disc.Empty) {
                return row
            }
        }
        return NO_FREE_CELLS_ON_ROW
    }

    companion object {
        private const val NO_FREE_CELLS_ON_ROW = -1

        private const val verticalLine = "║"
        private const val horizontalLine = "═"
        private const val leftCorner = "╚"
        private const val middleCorner = "╩"
        private const val rightCorner = "╝"

        private val winRegex = Regex("oooo|\\*\\*\\*\\*")

        const val MIN_SIZE = 5
        const val MAX_SIZE = 9
    }

    private class BoardContents(val columns: Int, val rows: Int) {
        private val contents = Array(columns) { Array(rows) { Disc.Empty } }

        fun discAt(column: Int, row: Int) = contents[column - 1][row - 1]

        fun setDiscAt(disc: Disc, column: Int, row: Int) {
            contents[column - 1][row - 1] = disc
        }

        fun getRowAsString(row: Int): String  {
            val sb = StringBuilder()
            for (column in 1..columns) {
                sb.append(discAt(column = column, row = row).char)
            }
            return sb.toString()
        }

        fun getColumnAsString(column: Int): String {
            val sb = StringBuilder()
            for (row in 1..rows) {
                sb.append(discAt(column = column, row = row).char)
            }
            return sb.toString()
        }

        fun getDownwardDiagonalAsString(startColumn: Int, startRow: Int): String {
            val sb = StringBuilder()
            var column = startColumn
            var row = startRow
            while (column <= columns && row <= rows) {
                sb.append(discAt(column = column, row = row).char)
                column++
                row++
            }
            return sb.toString()
        }

        fun getUpwardsDiagonalAsString(startColumn: Int, startRow: Int): String {
            val sb = StringBuilder()
            var column = startColumn
            var row = startRow
            while (column <= columns && row >= 1) {
                sb.append(discAt(column = column, row = row).char)
                column++
                row--
            }
            return sb.toString()
        }
    }
}
