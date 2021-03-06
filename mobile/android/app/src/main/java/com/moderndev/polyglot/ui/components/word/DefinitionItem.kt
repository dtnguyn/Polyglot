package com.moderndev.polyglot.ui.components.word

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moderndev.polyglot.model.Definition
import com.moderndev.polyglot.ui.theme.Typography
import com.moderndev.polyglot.util.UtilFunctions.generateRandomPastelColor
import com.moderndev.polyglot.R

@Composable
fun DefinitionItem(index: Int, definition: Definition) {

    Card(
        shape = RoundedCornerShape(15.dp),
        elevation = 4.dp,
        backgroundColor = generateRandomPastelColor(definition.meaning),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(15.dp)
            .clip(RoundedCornerShape(15.dp))
    ) {

        Column(
            Modifier.padding(15.dp)
        ) {
            Text(text = "${stringResource(id = R.string.definition)} ${index + 1}", style = Typography.h6, fontSize = 18.sp)
            Text(text = definition.partOfSpeech ?: "", style = Typography.subtitle1)
            Spacer(modifier = Modifier.padding(5.dp))
            Text(text = definition.meaning, style = Typography.h6)
            Spacer(modifier = Modifier.padding(10.dp))
            Text(text = stringResource(id = R.string.example), style = Typography.h6, fontSize = 18.sp)
            Text(text = definition.example ?: "", style = Typography.body1, fontSize = 18.sp)
        }
    }
}