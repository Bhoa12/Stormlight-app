package com.stormlight.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class GameAct { ACT_1, ACT_2, ACT_3 }
enum class CardType { HERO, ANTAGONIST, CHALLENGE }
enum class Attribute { STR, AGI, INT, SPI }
enum class GameMode { COMPETITIVE, COOPERATIVE, SOLO }

data class Card(
    val id: String,
    val title: String,
    val act: GameAct,
    val type: CardType,
    val attribute: Attribute,
    val targetValue: Int,
    val reward: String,
    val isHolo: Boolean = false,
    val corruptionCost: Boolean = false
)

class GameViewModel : ViewModel() {
    var gameMode by mutableStateOf(GameMode.SOLO)
    var currentAct by mutableStateOf(GameAct.ACT_1)
    
    var strTokens by mutableStateOf(2)
    var agiTokens by mutableStateOf(2)
    var intTokens by mutableStateOf(2)
    var spiTokens by mutableStateOf(2)
    
    var stormlightCount by mutableStateOf(3)
    var corruptionCount by mutableStateOf(0)
    var idealLevel by mutableStateOf(0)

    val act1Deck = mutableListOf(
        Card("1", "Esquirla de Puente", GameAct.ACT_1, CardType.HERO, Attribute.STR, 4, "+1 Fuerza, +1 Luz Tormentosa"),
        Card("2", "Estudiante de Navani", GameAct.ACT_1, CardType.HERO, Attribute.INT, 5, "+1 Perspicacia, Avanza Ideal", true),
        Card("3", "Asesino en la Sombra", GameAct.ACT_1, CardType.ANTAGONIST, Attribute.AGI, 6, "+2 Puntos Victoria, +1 Corrupción", false, true)
    )
    
    var marketCards by mutableStateOf<List<Card>>(emptyList())
    var playerTableau by mutableStateOf<List<Card>>(emptyList())
    var discardPile by mutableStateOf<List<Card>>(emptyList())
    
    var activeCastingTokens by mutableStateOf<List<String>>(emptyList())
    var selectedCardForAttempt by mutableStateOf<Card?>(null)

    init {
        loadMarket()
    }

    fun loadMarket() {
        marketCards = act1Deck.take(3)
    }

    fun selectCardToAttempt(card: Card) {
        selectedCardForAttempt = card
        activeCastingTokens = emptyList()
    }

    fun toggleTokenForCasting(tokenName: String) {
        if (activeCastingTokens.contains(tokenName)) {
            activeCastingTokens = activeCastingTokens - tokenName
        } else {
            activeCastingTokens = activeCastingTokens + tokenName
        }
    }

    fun resolveAttempt() {
        val card = selectedCardForAttempt ?: return
        val powerRolled = activeCastingTokens.size * 2 + (1..6).random()

        if (powerRolled >= card.targetValue) {
            playerTableau = playerTableau + card
            marketCards = marketCards.filter { it.id != card.id }
            if (card.corruptionCost) corruptionCount++
            if (card.reward.contains("Ideal")) idealLevel++
        } else {
            discardPile = discardPile + card
            marketCards = marketCards.filter { it.id != card.id }
        }
        selectedCardForAttempt = null
        activeCastingTokens = emptyList()
    }
}
