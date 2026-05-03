# BBTraveling

Proyecto Android de planificación de viajes para la asignatura **Applications for Mobile Devices**.

Versión actual: `3.0.0`

Sprint actual: `Sprint 03`

## Estado del proyecto

La versión actual implementa los requisitos principales del `LAB_SPRINT03`:

- Persistencia de viajes e itinerario con SQLite usando Room.
- Sustitución del almacenamiento in-memory en producción por `RoomTripRepository`.
- Arquitectura Repository mantenida.
- Hilt configurado como librería de inyección de dependencias.
- Firebase Authentication con email/password.
- Registro, login, logout y recuperación de contraseña.
- Verificación de email antes de permitir el acceso.
- Perfil local de usuario persistido en Room.
- Viajes asociados al usuario autenticado y filtrados por cuenta.
- Registro local de accesos login/logout.
- Validaciones, logs y tests unitarios por capas.

## Arquitectura

La app sigue una estructura MVVM:

```text
UI (Compose Screens)
  -> ViewModel
  -> Repository
  -> Room / Firebase / SharedPreferences
```

Piezas principales:

- `AuthViewModel`: coordina login, registro, recuperación y logout.
- `TripsViewModel`: coordina CRUD de viajes e itinerario.
- `SettingsViewModel`: combina ajustes globales con el perfil local del usuario autenticado.
- `FirebaseAuthRepository`: encapsula Firebase Auth y logs de acceso.
- `RoomTripRepository`: CRUD persistente de viajes e itinerario con Room.
- `RoomUserProfileRepository`: acceso al perfil local del usuario.
- `SharedPreferencesSettingsRepository`: idioma, tema y aceptación de términos.

## Persistencia local

Room usa la base de datos `bbtraveling.db` con estas tablas:

- `users`: perfil local del usuario (`login`, `username`, `birthdate`, `address`, `country`, `phone`, `acceptsReceiveEmails`).
- `trips`: viajes asociados a `ownerLogin`.
- `itinerary_items`: actividades asociadas a un viaje.
- `access_logs`: eventos `LOGIN` y `LOGOUT` con `userId` y fecha/hora.

## Firebase

El proyecto usa Firebase Authentication con proveedor Email/Password.

El archivo `app/google-services.json` es necesario en local, pero no se sube al repositorio porque está incluido en `.gitignore`.

Flujo implementado:

1. El usuario se registra con email, password y perfil local.
2. Firebase envía email de verificación.
3. La app vuelve a la pantalla de login y muestra el aviso de verificación.
4. El login solo permite entrar si el email está verificado.
5. Logout cierra la sesión y registra el acceso.

## Validaciones

- Campos obligatorios en viajes y actividades.
- Fechas futuras.
- Fecha de inicio anterior a fecha final.
- Actividades dentro del rango del viaje.
- Presupuesto y coste no negativos.
- Título de viaje no duplicado para el usuario.
- Username único antes del registro.
- Registro permitido solo para usuarios de 18 años o más.
- Email válido y mensajes de error localizados.

## Idiomas

Los textos se gestionan con recursos Android:

- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `app/src/main/res/values-ca/strings.xml`

Idiomas disponibles:

- Inglés
- Castellano
- Catalán

## Tests

La suite de unit tests que se ejecuta con `:app:testDebugUnitTest` incluye `46 tests`, con `0 failures` y `0 errors` en la verificación local.

- `ExampleUnitTest`: test base de entorno.
- `TripTest`: cálculos de dominio del viaje.
- `TripRepositoryImplTest`: CRUD in-memory, validaciones y título duplicado.
- `TravelDatabaseTest`: Room Database, DAOs, relaciones y persistencia básica.
- `RoomTripRepositoryTest`: CRUD persistente, filtrado por usuario y validaciones de trips.
- `RoomUserProfileRepositoryTest`: persistencia y observación del perfil local.
- `AuthViewModelTest`: login, registro, recuperación, email verification, confirmación de password y edad mínima.
- `SettingsViewModelTest`: perfil local autenticado, idioma, tema y preferencias sin usuario.
- `TripsViewModelTest`: carga, creación y validaciones desde ViewModel.
- `MainDispatcherRule`: regla de soporte para tests con corrutinas.

Comandos de verificación:

```powershell
./gradlew.bat :app:assembleDebug --console=plain
./gradlew.bat :app:testDebugUnitTest --console=plain
./gradlew.bat :app:lintDebug --console=plain
```

## Entrega

La entrega de Sprint 03 debe publicarse como release/tag `v3.x.x`.

Ruta prevista para evidencia:

```text
doc/evidence/v3.0.0/
```

## Equipo

- Anouar El Kabiri
- Eloi Mora Palomino
