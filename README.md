# BBTraveling

Proyecto Android de planificacion de viajes para la asignatura **Applications for Mobile Devices**.

Version actual: `3.0.0`  
Sprint actual: `Sprint 03`

## Estado del proyecto

La version actual implementa los requisitos principales del `LAB_SPRINT03`:

- Persistencia de viajes e itinerario con SQLite usando Room.
- Sustitucion del almacenamiento in-memory en produccion por `RoomTripRepository`.
- Arquitectura Repository mantenida.
- Hilt configurado como libreria de inyeccion de dependencias.
- Firebase Authentication con email/password.
- Registro, login, logout y recuperacion de contrasena.
- Verificacion de email antes de permitir el acceso.
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

- `AuthViewModel`: coordina login, registro, recuperacion y logout.
- `TripsViewModel`: coordina CRUD de viajes e itinerario.
- `SettingsViewModel`: combina ajustes globales con el perfil local del usuario autenticado.
- `FirebaseAuthRepository`: encapsula Firebase Auth y logs de acceso.
- `RoomTripRepository`: CRUD persistente de viajes e itinerario con Room.
- `RoomUserProfileRepository`: acceso al perfil local del usuario.
- `SharedPreferencesSettingsRepository`: idioma, tema y aceptacion de terminos.

## Persistencia local

Room usa la base de datos `bbtraveling.db` con estas tablas:

- `users`: perfil local del usuario (`login`, `username`, `birthdate`, `address`, `country`, `phone`, `acceptsReceiveEmails`).
- `trips`: viajes asociados a `ownerLogin`.
- `itinerary_items`: actividades asociadas a un viaje.
- `access_logs`: eventos `LOGIN` y `LOGOUT` con `userId` y fecha/hora.

## Firebase

El proyecto usa Firebase Authentication con proveedor Email/Password.

El archivo `app/google-services.json` es necesario en local, pero no se sube al repositorio porque esta incluido en `.gitignore`.

Flujo implementado:

1. El usuario se registra con email, password y perfil local.
2. Firebase envia email de verificacion.
3. La app vuelve a la pantalla de login y muestra el aviso de verificacion.
4. El login solo permite entrar si el email esta verificado.
5. Logout cierra la sesion y registra el acceso.

## Validaciones

- Campos obligatorios en viajes y actividades.
- Fechas futuras.
- Fecha de inicio anterior a fecha final.
- Actividades dentro del rango del viaje.
- Presupuesto y coste no negativos.
- Titulo de viaje no duplicado para el usuario.
- Username unico antes del registro.
- Email valido y mensajes de error localizados.

## Idiomas

Los textos se gestionan con recursos Android:

- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `app/src/main/res/values-ca/strings.xml`

Idiomas disponibles:

- Ingles
- Castellano
- Catalan

## Tests

La suite de unit tests cubre:

- Dominio y validaciones de viajes.
- Repositorio in-memory usado como soporte de tests y previews.
- Room Database, DAOs y relaciones.
- `RoomTripRepository`.
- `RoomUserProfileRepository`.
- `AuthViewModel`.
- `SettingsViewModel`.
- `TripsViewModel`.

Comandos de verificacion:

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
