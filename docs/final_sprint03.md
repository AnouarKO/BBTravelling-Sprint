# Sprint 03 - Final Report

Version: `3.0.0`  
Equipo: Anouar El Kabiri, Eloi Mora Palomino

## 1. Sprint Goal

Completar la capa persistente y de autenticacion de `BBTraveling` con:
- Persistencia local con `Room` para viajes, itinerario, perfiles y accesos
- Inyeccion de dependencias con `Hilt`
- Autenticacion `Firebase` con login, register, logout y recover password
- Adaptacion multiusuario para que cada cuenta vea solo sus datos
- Validaciones y testing del flujo funcional de Sprint 03

---

## 2. Sprint Backlog

| ID | Tarea | Responsable | Estado |
|----|------|------------|--------|
| T1.1 | Crear clase Room Database | Anouar | Completada |
| T1.2 | Definir Entities de Trip e ItineraryItem | Anouar y Eloi | Completada |
| T1.3 | Crear DAOs para operaciones de base de datos | Eloi | Completada |
| T1.4 | Implementar CRUD persistente con DAO para trips e itineraries | Anouar | Completada |
| T1.5 | Sustituir el flujo inMemory por Room en ViewModels y repositorios | Anouar | Completada |
| T1.6 | Integrar Hilt para inyeccion de DB, DAO y Repository | Eloi | Completada |
| T1.7 | Asegurar que la UI se actualiza al cambiar la base de datos | Anouar | Completada |
| T2.1 | Conectar la app con Firebase | Anouar | Completada |
| T2.2 | Disenar pantalla de login | Eloi | Completada |
| T2.3 | Implementar login con email y password | Anouar | Completada |
| T2.4 | Crear accion de logout en la app | Eloi | Completada |
| T2.5 | Anadir logs de operaciones y errores de autenticacion | Eloi | Completada |
| T3.1 | Disenar pantalla de registro | Eloi | Completada |
| T3.2 | Implementar registro con Firebase usando Repository | Anouar | Completada |
| T3.3 | Anadir verificacion de email y recover password | Anouar y Eloi | Completada |
| T4.1 | Persistir user info en tabla local con login, username, birthdate, address, country, phone y accept receive emails | Eloi | Completada |
| T4.2 | Adaptar tabla de trips para multiples usuarios | Anouar | Completada |
| T4.3 | Filtrar y mostrar solo trips del usuario logado | Anouar | Completada |
| T4.4 | Persistir accesos login/logout con userId y datetime | Eloi | Completada |
| T4.5 | Actualizar design.md con schema y uso de base de datos | Eloi | Completada |
| T5.1 | Escribir unit tests de DAO e interacciones de base de datos | Anouar | Completada |
| T5.2 | Validar datos persistidos y casos limite | Anouar | Completada |
| T5.3 | Anadir logs de operaciones y errores de Room | Eloi | Completada |
| T5.4 | Actualizar README y documentacion final del sprint | Eloi | Completada |
| T5.5 | Evidencia de video | Anouar | Pendiente de enlace final |

Resumen funcional del backlog:
- `Room` sustituye el almacenamiento in-memory para viajes, actividades, perfiles y logs de acceso.
- `Hilt` resuelve la inyeccion de `Database`, `DAO`, repositorios y ViewModels.
- El flujo de autenticacion incluye `login`, `register`, `logout`, `recover password` y verificacion de email con `Firebase Authentication`.
- El registro crea la cuenta Firebase y el perfil local asociado al correo autenticado.
- Todos los campos del registro son obligatorios salvo `accept receive emails`.
- El registro exige confirmacion de contrasena y valida que ambas contrasenas coincidan.
- El registro exige edad minima de 18 anos.
- La pantalla de acceso permite cambiar idioma y fondo sin cuenta.
- Cada usuario autenticado solo ve sus propios viajes y actividades.
- Se han anadido tests unitarios de `DAO`, repositorios, ViewModels y validaciones principales del sprint.

---

## 3. Definition of Done (DoD)

- [x] `Room` sustituye el almacenamiento in-memory para trips y actividades
- [x] Arquitectura `Repository` mantenida con `Hilt` configurado como DI
- [x] `Login`, `register`, `logout` y `recover password` funcionando con `Firebase`
- [x] Verificacion de email exigida antes del login funcional
- [x] Informacion local de usuario persistida con `username`, `birthdate`, `address`, `country`, `phone` y `accept receive emails`
- [x] Registro validado con `email`, `password`, `confirm password`, `username`, `birthdate`, `address`, `country` y `phone` obligatorios
- [x] Registro bloqueado para usuarios menores de 18 anos
- [x] `accept receive emails` mantenido como opcion no obligatoria
- [x] Cambio de idioma y tema disponible antes de iniciar sesion
- [x] Trips asociados al usuario logado y filtrados correctamente
- [x] Registro de accesos `login/logout` persistido en base de datos
- [x] `design.md`, `README.md` y documento final del sprint actualizados
- [x] Evidencia preparada en `doc/evidence/v3.0.0/`
- [x] Version final preparada como `v3.0.0`

Validacion tecnica:
- [x] `./gradlew.bat :app:assembleDebug --console=plain`
- [x] `./gradlew.bat :app:testDebugUnitTest --console=plain` (`46 tests`, `0 failures`, `0 errors`)
- [x] `./gradlew.bat :app:lintDebug --console=plain`

Tests ejecutados con `:app:testDebugUnitTest`:
- `ExampleUnitTest`
- `TripTest`
- `TripRepositoryImplTest`
- `TravelDatabaseTest`
- `RoomTripRepositoryTest`
- `RoomUserProfileRepositoryTest`
- `AuthViewModelTest`
- `SettingsViewModelTest`
- `TripsViewModelTest`
- `MainDispatcherRule` como regla de soporte de corrutinas

---

## 4. Riesgos identificados

- Configuracion de Firebase y conectividad del emulador  
  Mitigacion: `google-services.json` fuera del repositorio, plugin de Google Services configurado y comprobacion manual de conectividad del emulador.

- Regresiones al sustituir in-memory por `Room`  
  Mitigacion: repositorios especificos (`RoomTripRepository`, `RoomUserProfileRepository`) y tests de `DAO` y base de datos.

- Desajuste entre usuario Firebase y perfil local  
  Mitigacion: login bloqueado si no existe perfil local y perfil enlazado por correo normalizado.

- Validaciones incompletas en el registro  
  Mitigacion: validacion centralizada en `AuthViewModel` con comprobacion de campos obligatorios, edad minima y confirmacion de contrasena.

- Fugas de datos entre usuarios  
  Mitigacion: filtrado de viajes por usuario autenticado y persistencia separada por `userLogin`.

---

## 5. Evidencia de video

Ruta prevista:

```text
doc/evidence/v3.0.0/
```

Material preparado:
- `README.md` de evidencia
- enlace final del video pendiente de anadir
- flujo funcional listo para grabar sobre `register`, verificacion de email, `login`, `Room`, multiusuario y tests

---

## 6. Mejoras futuras

- Anadir tests instrumentados de navegacion y formularios `Compose`.
- Mejorar el feedback visual por campo en el formulario de autenticacion.
- Migrar el perfil local a una validacion mas rica por formato de telefono y direccion si el enunciado futuro lo exige.
