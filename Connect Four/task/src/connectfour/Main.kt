package connectfour

import java.lang.IllegalArgumentException
import java.lang.NumberFormatException

private val BOARD_SIZE_REGEX = Regex("\\s*\\d+\\s*[xX]\\s*\\d+\\s*")
private val NUMBER = Regex("\\d")

fun main() {
    println("Connect Four")

    println("First player's name:")
    Player.Player1.playerName = readln()
    println("Second player's name:")
    Player.Player2.playerName = readln()

    val board = generateBoard()
    val numberOfGames = readNumberOfGames()
    var gameNumber = 0

    println("${Player.Player1.playerName} VS ${Player.Player2.playerName}")
    println("$board board")
    if (numberOfGames == 1) {
        println("Single game")
    } else {
        println("Total $numberOfGames games")
    }

    var turn = Player.Player1
    var turnNumber = 0
    while (turnNumber < numberOfGames) {
        val previousStartTurn = turn
        gameNumber++
        if (numberOfGames > 1) {
            println("Game #$gameNumber")
        }
        board.printBoard()
        while (true) {
            println("${turn.playerName}'s turn:")
            try {
                playTurn(turn, board)
                board.printBoard()

                if (board.isWinCondition()) {
                    println("Player ${turn.playerName} won")
                    calculateScoreAndReset(turn, board, numberOfGames, false)
                    break
                } else if (board.isBoardFull()) {
                    println("It is a draw")
                    calculateScoreAndReset(turn, board, numberOfGames, true)
                    break
                }
                turn = turn.nextPlayer()
            } catch (e: NumberFormatException) {
                println("Incorrect column number")
            } catch (e: IllegalArgumentException) {
                println(e.message)
            } catch (e: GameOverException) {
                println("Game over!")
                return
            }
        }
        turnNumber++
        turn = previousStartTurn.nextPlayer()
    }
    println("Game over!")
}

fun playTurn(player: Player, board: Board) {
    val input = readln()
    if (input == "end") {
        throw GameOverException()
    }
    board.addDisc(input.toInt(), player.disc)
}

fun calculateScoreAndReset(turn: Player, board: Board, numberOfGames: Int, isDraw: Boolean) {
    if (numberOfGames > 1) {
        if (isDraw) {
            Player.Player1.score += 1
            Player.Player2.score += 1
        } else {
            turn.score += 2
        }
        printScore()
        board.reset()
    }
}

fun Player.nextPlayer() = when (this) {
    Player.Player1 -> Player.Player2
    Player.Player2 -> Player.Player1
}

fun readNumberOfGames(): Int {
    var numberOfGames: Int? = null
    while (numberOfGames == null) {
        println("Do you want to play single or multiple games?")
        println("For a single game, input 1 or press Enter")
        println("Input a number of games:")
        try {
            val input = readln()
            if (input.isEmpty()) {
                return 1
            }
            numberOfGames = input.toInt()
            if (numberOfGames < 1) {
                throw IllegalArgumentException()
            }
        } catch (e: Exception) {
            println("Invalid input")
            numberOfGames = null
        }
    }
    return numberOfGames
}

fun generateBoard(): Board {
    var board: Board? = null
    while (board == null) {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
        val input = readln()
        board = when {
            input.isEmpty() -> Board(columns = 7, rows = 6)
            BOARD_SIZE_REGEX.matches(input) -> parseBoard(input)
            else -> {
                println("Invalid input")
                null
            }
        }
    }
    return board
}

fun parseBoard(input: String): Board? {
    val (rows, columns) = NUMBER.findAll(input).toList().map { it.value.toInt() }
    return when {
        rows !in Board.MIN_SIZE..Board.MAX_SIZE -> {
            println("Board rows should be from ${Board.MIN_SIZE} to ${Board.MAX_SIZE}")
            null
        }
        columns !in Board.MIN_SIZE..Board.MAX_SIZE -> {
            println("Board columns should be from ${Board.MIN_SIZE} to ${Board.MAX_SIZE}")
            null
        }
        else -> Board(columns = columns, rows = rows)
    }
}

fun printScore() {
    println("Score")
    println("${Player.Player1.playerName}: ${Player.Player1.score} ${Player.Player2.playerName}: ${Player.Player2.score}")
}

enum class Player(val disc: Disc) {
    Player1(Disc.Disc1),
    Player2(Disc.Disc2),
    ;
    lateinit var playerName: String
    var score = 0
}

class GameOverException : RuntimeException()