package com.example.bbtraveling.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.bbtraveling.R
import com.example.bbtraveling.ui.viewmodel.AuthViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private enum class AuthMode {
    Login,
    Register,
    Recover
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    onAuthenticated: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    var mode by remember { mutableStateOf(AuthMode.Login) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var birthdate by remember { mutableStateOf<LocalDate?>(null) }
    var birthDatePickerVisible by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var acceptsReceiveEmails by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }
    val invalidEmailMessage = stringResource(R.string.auth_error_invalid_email)

    LaunchedEffect(uiState.currentUser) {
        if (uiState.currentUser != null) {
            onAuthenticated()
        }
    }

    LaunchedEffect(uiState.registrationCompleted) {
        if (uiState.registrationCompleted) {
            mode = AuthMode.Login
            password = ""
            confirmPassword = ""
            username = ""
            birthdate = null
            address = ""
            country = ""
            phone = ""
            acceptsReceiveEmails = false
            localError = null
            authViewModel.consumeRegistrationCompleted()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.title_auth)) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = when (mode) {
                            AuthMode.Login -> stringResource(R.string.title_login)
                            AuthMode.Register -> stringResource(R.string.title_register)
                            AuthMode.Recover -> stringResource(R.string.title_recover_password)
                        },
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = when (mode) {
                            AuthMode.Login -> stringResource(R.string.auth_subtitle)
                            AuthMode.Register -> stringResource(R.string.auth_register_hint)
                            AuthMode.Recover -> stringResource(R.string.auth_recover_hint)
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AuthStatusMessage(
                message = localError ?: uiState.error,
                isError = true
            )
            AuthStatusMessage(
                message = uiState.message,
                isError = false
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    localError = null
                    authViewModel.clearMessages()
                },
                label = { Text(stringResource(R.string.label_email)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (mode != AuthMode.Recover) {
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        localError = null
                        authViewModel.clearMessages()
                    },
                    label = { Text(stringResource(R.string.label_password)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            if (mode == AuthMode.Register) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        localError = null
                        authViewModel.clearMessages()
                    },
                    label = { Text(stringResource(R.string.label_confirm_password)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            if (mode == AuthMode.Register) {
                RegisterFields(
                    username = username,
                    onUsernameChange = { username = it },
                    birthdate = birthdate,
                    onOpenBirthdatePicker = { birthDatePickerVisible = true },
                    address = address,
                    onAddressChange = { address = it },
                    country = country,
                    onCountryChange = { country = it },
                    phone = phone,
                    onPhoneChange = { phone = it },
                    acceptsReceiveEmails = acceptsReceiveEmails,
                    onAcceptsReceiveEmailsChange = { acceptsReceiveEmails = it }
                )
            }

            Button(
                enabled = !uiState.loading,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    localError = null
                    when (mode) {
                        AuthMode.Login -> {
                            if (!email.trim().isValidEmail()) {
                                localError = invalidEmailMessage
                            } else {
                                authViewModel.login(email, password)
                            }
                        }
                        AuthMode.Register -> {
                            if (!email.trim().isValidEmail()) {
                                localError = invalidEmailMessage
                            } else {
                                authViewModel.register(
                                    email = email,
                                    password = password,
                                    confirmPassword = confirmPassword,
                                    username = username,
                                    birthdate = birthdate,
                                    address = address,
                                    country = country,
                                    phone = phone,
                                    acceptsReceiveEmails = acceptsReceiveEmails
                                )
                            }
                        }
                        AuthMode.Recover -> {
                            if (!email.trim().isValidEmail()) {
                                localError = invalidEmailMessage
                            } else {
                                authViewModel.recoverPassword(email)
                            }
                        }
                    }
                }
            ) {
                if (uiState.loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        when (mode) {
                            AuthMode.Login -> stringResource(R.string.action_login)
                            AuthMode.Register -> stringResource(R.string.action_register)
                            AuthMode.Recover -> stringResource(R.string.action_send_recovery)
                        }
                    )
                }
            }

            if (mode == AuthMode.Login) {
                TextButton(
                    onClick = {
                        mode = AuthMode.Recover
                        localError = null
                        authViewModel.clearMessages()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.action_recover_password))
                }
            }

            AuthModeSwitch(
                mode = mode,
                onModeChange = {
                    mode = it
                    localError = null
                    authViewModel.clearMessages()
                }
            )

            if (mode == AuthMode.Register) {
                Text(
                    text = stringResource(R.string.auth_email_verification_hint),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (birthDatePickerVisible) {
                BirthdatePickerDialog(
                    initial = birthdate,
                    onDismiss = { birthDatePickerVisible = false },
                    onConfirm = { selected ->
                        birthdate = selected
                        birthDatePickerVisible = false
                    }
                )
            }
        }
    }
}

@Composable
private fun RegisterFields(
    username: String,
    onUsernameChange: (String) -> Unit,
    birthdate: LocalDate?,
    onOpenBirthdatePicker: () -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    country: String,
    onCountryChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    acceptsReceiveEmails: Boolean,
    onAcceptsReceiveEmailsChange: (Boolean) -> Unit
) {
    OutlinedTextField(
        value = username,
        onValueChange = onUsernameChange,
        label = { Text(stringResource(R.string.label_username)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    OutlinedButton(
        onClick = onOpenBirthdatePicker,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            birthdate?.format(AUTH_DATE_FORMAT)
                ?: stringResource(R.string.label_birthdate_iso)
        )
    }
    OutlinedTextField(
        value = address,
        onValueChange = onAddressChange,
        label = { Text(stringResource(R.string.label_address)) },
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = country,
        onValueChange = onCountryChange,
        label = { Text(stringResource(R.string.label_country)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    OutlinedTextField(
        value = phone,
        onValueChange = onPhoneChange,
        label = { Text(stringResource(R.string.label_phone)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = acceptsReceiveEmails,
            onCheckedChange = onAcceptsReceiveEmailsChange
        )
        Text(stringResource(R.string.label_receive_emails))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthdatePickerDialog(
    initial: LocalDate?,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = initial.toEpochMillis())

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selected = state.selectedDateMillis?.toLocalDate()
                    if (selected != null) {
                        onConfirm(selected)
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text(stringResource(R.string.action_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    ) {
        DatePicker(state = state)
    }
}

@Composable
private fun AuthStatusMessage(
    message: String?,
    isError: Boolean
) {
    if (message.isNullOrBlank()) return

    Text(
        text = message,
        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun AuthModeSwitch(
    mode: AuthMode,
    onModeChange: (AuthMode) -> Unit
) {
    when (mode) {
        AuthMode.Login -> {
            TextButton(
                onClick = { onModeChange(AuthMode.Register) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${stringResource(R.string.auth_need_account)} ${stringResource(R.string.action_register)}")
            }
        }
        AuthMode.Register,
        AuthMode.Recover -> {
            TextButton(
                onClick = { onModeChange(AuthMode.Login) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (mode == AuthMode.Register) {
                        "${stringResource(R.string.auth_have_account)} ${stringResource(R.string.action_login)}"
                    } else {
                        stringResource(R.string.action_back_to_login)
                    }
                )
            }
        }
    }
}

private fun LocalDate?.toEpochMillis(): Long? {
    if (this == null) return null
    return atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

private fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
}

private fun String.isValidEmail(): Boolean {
    return matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
}

private val AUTH_DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
