package com.jzheng.tinytimer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jzheng.tinytimer.data.Constants.defaultPadding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

val myModifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)

@Composable
fun ButtonCard(
    desc: String,
    onAuthClick: () -> Unit,
    buttonText: String,
    buttonEnabled: Boolean = true
) {
    InteractiveCard(desc = desc) {
        Button(
            onClick = onAuthClick,
            enabled = buttonEnabled,
            modifier = Modifier
                .wrapContentHeight()
                .width(96.dp)
        ) {
            Text(text = buttonText)
        }
    }
}

@Composable
fun InputCard(
    desc: String,
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
) {
    var temporaryValue by remember { mutableStateOf(value) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(value) {
        temporaryValue = value
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = myModifier
    ) {
        Text(
            text = desc,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.width(10.dp))
        TextField(
            value = temporaryValue,
            onValueChange = { temporaryValue = it },
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onValueChange(temporaryValue)
                    focusManager.clearFocus() // This will hide the keyboard
                }
            )
        )
    }
}

@Composable
fun InputButtonCard(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    onAuthClick: () -> Unit,
    buttonText: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = myModifier
    ) {

        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Button(
            onClick = onAuthClick,
            modifier = Modifier
                .wrapContentHeight()
                .width(96.dp)
        ) {
            Text(text = buttonText)
        }
    }
}

@Composable
fun SwitchCard(
    desc: String,
    onCheckedChange: (Boolean) -> Unit,
    isChecked: Boolean,
    isEnabled: Boolean = true
) {
    InteractiveCard(desc = desc) {
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.wrapContentHeight(),
            enabled = isEnabled
        )
    }
}


@Composable
fun RadioButtonGroup(
    desc: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    isEnabled: Boolean = true
) {
    Column(
        modifier = myModifier
    ) {
        Text(
            text = desc, fontSize = 14.sp
        )
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (option == selectedOption),
                        onClick = { onOptionSelected(option) }
                    )
                    .padding(defaultPadding)
            ) {
                RadioButton(
                    selected = (option == selectedOption),
                    onClick = null, // null because we're handling the click on the entire row
                    enabled = isEnabled
                )
                Text(
                    text = option,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = defaultPadding)
                )
            }
        }
    }
}

@Composable
fun TitleText(
    title: String
) {
    Text(
        text = title,
        fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
        fontSize = 20.sp,
        modifier = Modifier.padding(vertical = defaultPadding / 2)
    )
}

@Composable
fun OptionCard(
    option: OptionItem,
    selected: Boolean,
    onOptionSelected: () -> Unit,
    onButtonClick: () -> Unit,
    onInputValueChange: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = myModifier
            .selectable(
                selected = selected,
                onClick = onOptionSelected
            )
            .padding(defaultPadding)
    ) {
        RadioButton(
            selected = selected,
            onClick = null // We handle the click on the entire row
        )
        Text(
            text = option.desc,
            fontSize = 14.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = defaultPadding)
        )
        TextField(
            value = option.textFieldValue,
            onValueChange = { newValue -> onInputValueChange(newValue) },
            label = { Text(option.textFieldLabel) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.width(defaultPadding * 2))
        Button(
            onClick = onButtonClick,
            modifier = Modifier
                .wrapContentHeight()
                .width(96.dp)
        ) {
            Text(text = option.buttonText)
        }
    }
}

data class OptionItem(
    val desc: String,
    val buttonText: String,
    var textFieldValue: String = "", // Add a field for the TextField value
    val textFieldLabel: String
)

@Composable
fun ComplexRadioGroup(
    desc: String,
    options: List<OptionItem>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    onButtonClick: (String) -> Unit,
    onTextFieldValueChange: (String, String) -> Unit,
) {
    Column(modifier = myModifier) {
        Text(text = desc, fontSize = 14.sp)
        options.forEach { option ->
            OptionCard(
                option = option,
                selected = option.desc == selectedOption,
                onOptionSelected = { onOptionSelected(option.desc) },
                onButtonClick = { onButtonClick(option.desc) },
                onInputValueChange = { newValue -> onTextFieldValueChange(option.desc, newValue) },
            )
        }
    }
}

@Composable
fun ThickDivider(thickness: Dp = 2.dp) {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.onBackground,
        thickness = thickness
    )
}

@Composable
fun InteractiveCard(
    desc: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = desc,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(defaultPadding))
        content()
    }
}


@Composable
fun ReviewCard(
    title: String,
    onAuthClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = defaultPadding / 2),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(
                horizontal = defaultPadding * 1.5f,
                vertical = defaultPadding / 2
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TitleText(title = title)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onAuthClick,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "open"
                    )
                }
            }
            content()
        }
    }
}


@Composable
fun ArrowCard(
    title: String = "Title",
    desc: String = "Desc",
    onAuthClick: () -> Unit = {},
    buttonEnabled: Boolean = true
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onAuthClick() }
            .padding(vertical = defaultPadding / 2),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(
                horizontal = defaultPadding * 1.5f,
                vertical = defaultPadding / 2
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onAuthClick,
                    enabled = buttonEnabled,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "open"
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc, fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun <T> usePollState(
    context: CoroutineContext = Dispatchers.Default,
    interval: Long = 1000L,
    getter: () -> T,
): MutableState<T> {
    val mutableState = remember { mutableStateOf(getter()) }
    LaunchedEffect(Unit) {
        withContext(context) {
            while (isActive) {
                delay(interval)
                mutableState.value = getter()
            }
        }
    }
    return mutableState
}
