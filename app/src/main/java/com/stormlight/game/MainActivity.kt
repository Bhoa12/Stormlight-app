package com.stormlight.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0D1B2A)
                ) {
                    GameMainScreen()
                }
            }
        }
    }
}

@Composable
fun GameMainScreen(viewModel: GameViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf("menu") }

    when (currentScreen) {
        "menu" -> MainMenu(
            onStartGame = { mode ->
                viewModel.gameMode = mode
                currentScreen = "board"
            },
            onOpenRules = { currentScreen = "rules" }
        )
        "board" -> GameBoardScreen(viewModel, onBackToMenu = { currentScreen = "menu" })
        "rules" -> RulesScreen(onBack = { currentScreen = "menu" })
    }
}

@Composable
fun MainMenu(onStartGame: (GameMode) -> Unit, onOpenRules: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("STORM-LIGHT", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00F5D4))
        Text("Call to Adventure", fontSize = 18.sp, color = Color.LightGray, modifier = Modifier.padding(bottom = 32.dp))

        Button(onClick = { onStartGame(GameMode.SOLO) }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text("Modo Individual / Solo")
        }
        Button(onClick = { onStartGame(GameMode.COOPERATIVE) }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text("Modo Cooperativo")
        }
        Button(onClick = { onStartGame(GameMode.COMPETITIVE) }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text("Modo Competitivo")
        }
        OutlinedButton(onClick = onOpenRules, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text("Instrucciones Oficiales", color = Color.White)
        }
    }
}

@Composable
fun GameBoardScreen(viewModel: GameViewModel, onBackToMenu: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Acto: ${viewModel.currentAct}", color = Color.Yellow, fontSize = 14.sp)
            Text("⚡ ${viewModel.stormlightCount} | 💀 ${viewModel.corruptionCount}", color = Color.Cyan, fontSize = 14.sp)
            Button(onClick = onBackToMenu, modifier = Modifier.height(32.dp)) {
                Text("Salir", fontSize = 10.sp)
            }
        }

        Column {
            Text("Cartas en el Tablero (Mercado):", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(viewModel.marketCards) { card ->
                    CardView(card = card) {
                        viewModel.selectCardToAttempt(card)
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .border(2.dp, Color(0xFF415A77), MaterialTheme.shapes.medium)
                .padding(8.dp)
        ) {
            Column {
                Text("Área de Lanzamiento (Fichas seleccionadas):", color = Color(0xFF00F5D4), fontSize = 12.sp)
                Text("Fichas en juego: ${viewModel.activeCastingTokens.joinToString(", ")}", color = Color.White, fontSize = 11.sp)
                
                if (viewModel.selectedCardForAttempt != null) {
                    Text("Intentando: ${viewModel.selectedCardForAttempt?.title} (Req: ${viewModel.selectedCardForAttempt?.targetValue})", color = Color.Yellow, fontSize = 11.sp)
                    Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { viewModel.toggleTokenForCasting("Runa Fuerza") }) { Text("+Runa") }
                        Button(onClick = { viewModel.resolveAttempt() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A9D8F))) {
                            Text("Lanzar / Resolver")
                        }
                    }
                } else {
                    Text("Selecciona una carta del mercado para intentar conseguirla.", color = Color.Gray, fontSize = 10.sp)
                }
            }
        }

        Column {
            Text("Tus Cartas Conquistadas (${viewModel.playerTableau.size}) | Ideal Nivel: ${viewModel.idealLevel}", color = Color.White, fontSize = 12.sp)
            Text("Descartes (Cartas fallidas): ${viewModel.discardPile.size}", color = Color.Red, fontSize = 12.sp)
        }
    }
}

@Composable
fun CardView(card: Card, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(110.dp).height(150.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(if (card.isHolo) Color(0xFF3A3A3A) else Color(0xFF1B263B)).padding(6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(card.title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 2)
            Text("Req: ${card.attribute} (${card.targetValue})", color = Color(0xFFFFD700), fontSize = 10.sp)
            if (card.isHolo) Text("✨ HOLO", color = Color.Cyan, fontSize = 8.sp)
        }
    }
}

@Composable
fun RulesScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Instrucciones Oficiales", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "1. Objetivo: Construir la historia definitiva superando retos, héroes y antagonistas.\n" +
            "2. Turno: Elige una carta del mercado, asigna tus fichas al Área de Lanzamiento y tira las runas.\n" +
            "3. Si superas el requisito, la carta pasa a tu tablero. Si fallas, se descarta automáticamente.\n" +
            "4. Avanza por los Ideales de los Caballeros Radiantes y vigila la Corrupción.",
            color = Color.LightGray,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) { Text("Volver al Menú") }
    }
}
