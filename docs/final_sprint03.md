# Sprint 03 - Final Report

Versión: `3.0.0`

Equipo: Anouar El Kabiri, Eloi Mora Palomino

## 1. Sprint Goal

Completar la capa persistente y de autenticación de `BBTraveling` con:
- Persistencia local con `Room` para viajes, itinerario, perfiles y accesos
- Inyección de dependencias con `Hilt`
- Autenticación `Firebase` con login, register, logout y recover password
- Adaptación multiusuario para que cada cuenta vea solo sus datos
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
| T1.6 | Integrar Hilt para inyección de DB, DAO y Repository | Eloi | Completada |
| T1.7 | Asegurar que la UI se actualiza al cambiar la base de datos | Anouar | Completada |
| T2.1 | Conectar la app con Firebase | Anouar | Completada |
| T2.2 | Diseñar pantalla de login | Anouar y Eloi | Completada |
| T2.3 | Implementar login con email y password | Anouar | Completada |
| T2.4 | Crear acción de logout en la app | Anouar y Eloi | Completada |
| T2.5 | Añadir logs de operaciones y errores de autenticación | Anouar y Eloi | Completada |
| T3.1 | Diseñar pantalla de registro | Anouar y Eloi | Completada |
| T3.2 | Implementar registro con Firebase usando Repository | Anouar | Completada |
| T3.3 | Añadir verificación de email y recover password | Anouar y Eloi | Completada |
| T4.1 | Persistir user info en tabla local con login, username, birthdate, address, country, phone y accept receive emails | Anouar y Eloi | Completada |
| T4.2 | Adaptar tabla de trips para múltiples usuarios | Anouar | Completada |
| T4.3 | Filtrar y mostrar solo trips del usuario logado | Anouar | Completada |
| T4.4 | Persistir accesos login/logout con userId y datetime | Anouar y Eloi | Completada |
| T4.5 | Actualizar design.md con schema y uso de base de datos | Anouar y Eloi | Completada |
| T5.1 | Escribir unit tests de DAO e interacciones de base de datos | Anouar | Completada |
| T5.2 | Validar datos persistidos y casos límite | Anouar | Completada |
| T5.3 | Añadir logs de operaciones y errores de Room | Anouar y Eloi | Completada |
| T5.4 | Actualizar README y documentación final del sprint | Anouar | Completada |
| T5.5 | Evidencia de vídeo | Anouar | Completada |

Reparto final aplicado:
- Eloi subió la base de `Hilt`, módulos DI y parte de la integración inicial de Room.
- Anouar cerró Firebase Auth, validaciones de registro, flujo multiusuario, tests, evidencia y revisión final de documentación.

Resumen funcional del backlog:
- `Room` sustituye el almacenamiento in-memory para viajes, actividades, perfiles y logs de acceso.
- `Hilt` resuelve la inyección de `Database`, `DAO`, repositorios y ViewModels.
- El flujo de autenticación incluye `login`, `register`, `logout`, `recover password` y verificación de email con `Firebase Authentication`.
- El registro crea la cuenta Firebase y el perfil local asociado al correo autenticado.
- Todos los campos del registro son obligatorios salvo `accept receive emails`.
- El registro exige confirmación de contraseña y valida que ambas contraseñas coincidan.
- El registro exige edad mínima de 18 años.
- La pantalla de acceso permite cambiar idioma y fondo sin cuenta.
- Cada usuario autenticado solo ve sus propios viajes y actividades.
- Se han añadido tests unitarios de `DAO`, repositorios, ViewModels y validaciones principales del sprint.

---

## 3. Definition of Done (DoD)

- [x] `Room` sustituye el almacenamiento in-memory para trips y actividades
- [x] Arquitectura `Repository` mantenida con `Hilt` configurado como DI
- [x] `Login`, `register`, `logout` y `recover password` funcionando con `Firebase`
- [x] Verificación de email exigida antes del login funcional
- [x] Información local de usuario persistida con `username`, `birthdate`, `address`, `country`, `phone` y `accept receive emails`
- [x] Registro validado con `email`, `password`, `confirm password`, `username`, `birthdate`, `address`, `country` y `phone` obligatorios
- [x] Registro bloqueado para usuarios menores de 18 años
- [x] `accept receive emails` mantenido como opción no obligatoria
- [x] Cambio de idioma y tema disponible antes de iniciar sesión
- [x] Trips asociados al usuario logado y filtrados correctamente
- [x] Registro de accesos `login/logout` persistido en base de datos
- [x] `design.md`, `README.md` y documento final del sprint actualizados
- [x] Evidencia preparada en `doc/evidence/v3.0.0/`
- [x] Versión final preparada como `v3.0.0`

Validación técnica:
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

- Configuración de Firebase y conectividad del emulador
  Mitigación: `google-services.json` fuera del repositorio, plugin de Google Services configurado y comprobación manual de conectividad del emulador.

- Regresiones al sustituir in-memory por `Room`  
  Mitigación: repositorios específicos (`RoomTripRepository`, `RoomUserProfileRepository`) y tests de `DAO` y base de datos.

- Desajuste entre usuario Firebase y perfil local  
  Mitigación: login bloqueado si no existe perfil local y perfil enlazado por correo normalizado.

- Validaciones incompletas en el registro  
  Mitigación: validación centralizada en `AuthViewModel` con comprobación de campos obligatorios, edad mínima y confirmación de contraseña.

- Fugas de datos entre usuarios  
  Mitigación: filtrado de viajes por usuario autenticado y persistencia separada por `userLogin`.

---

## 5. Evidencia de vídeo

Ruta prevista:

```text
doc/evidence/v3.0.0/
```

Material preparado:
- `README.md` de evidencia
- enlace final del vídeo: https://youtu.be/JuswyTpdHYg
- flujo funcional grabado sobre `register`, verificación de email, `login`, `Room`, multiusuario y tests

---

## 6. Mejoras futuras

- Añadir tests instrumentados de navegación y formularios `Compose`.
- Mejorar el feedback visual por campo en el formulario de autenticación.
- Migrar el perfil local a una validación más rica por formato de teléfono y dirección si el enunciado futuro lo exige.
