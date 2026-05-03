# Sprint 02 - Final Report

Versión: `2.0.2`

Equipo: Anouar El Kabiri, Eloi Mora Palomino

## 1. Sprint Goal

Completar la capa funcional de `BBTraveling` con:
- Arquitectura `MVVM`
- CRUD inMemory de viajes
- CRUD inMemory de actividades del itinerario
- Validaciones de fechas, presupuesto y campos obligatorios
- Persistencia de ajustes de usuario con `SharedPreferences`
- Multi-language funcional (`en`, `es`, `ca`)
- Testing y documentación mínima de entrega

---

## 2. Sprint Backlog

| ID | Tarea | Responsable | Estado |
|----|------|------------|--------|
| T1.1 | Implementar CRUD de trips en memoria | Anouar | Completada |
| T1.2 | Implementar CRUD de activities en memoria | Anouar | Completada |
| T1.3 | Validar fechas de trips y activities | Anouar | Completada |
| T1.4 | Persistir user settings con SharedPreferences | Eloi | Completada |
| T1.5 | Implementar multi-language en, es, ca | Anouar | Completada |
| T2.1 | Conectar UI con ViewModel y Repository | Anouar y Eloi | Completada |
| T2.2 | Crear formularios de alta y edición | Anouar y Eloi | Completada |
| T2.3 | Hacer que los cambios se reflejen dinámicamente | Anouar | Completada |
| T3.1 | Añadir logs para operaciones y errores | Eloi | Completada |
| T3.2 | Crear unit tests de CRUD | Anouar | Completada |
| T3.3 | Actualizar README y docs del sprint | Eloi | Completada |
| T3.4 | Preparar y entregar release `v2.0.2` | Anouar | Completada |

Resumen funcional del backlog:
- Arquitectura `UI -> ViewModel -> Repository -> DataSource` implementada.
- CRUD completo de viajes y actividades en memoria con actualización dinámica en pantalla.
- Validaciones en `UI`, `ViewModel` y `Repository` para evitar datos inconsistentes.
- Ajustes persistidos: `username`, `dateOfBirth`, `darkMode`, `languageTag`, `termsAccepted`.
- Flujo de `Terms & Conditions` en primer arranque.
- Presupuesto real por viaje y coste real por actividad, todo en euros.
- Selector de ciudad y país para que los viajes creados manualmente sigan el mismo formato visual que los viajes mock.
- Categorías y plantillas predefinidas para agilizar la creación de actividades.
- Recursos multi-language separados en `values`, `values-es` y `values-ca`.
- Evidencia del sprint publicada mediante enlace externo en `doc/evidence/v2.0.2/`.

---

## 3. Definition of Done (DoD)

- [x] CRUD de viajes y actividades funcionando en memoria
- [x] Validaciones implementadas en UI, ViewModel y Repository
- [x] Settings persistidos y cargados al reiniciar la app
- [x] Terms & Conditions apareciendo al iniciar por primera vez
- [x] Cambio de idioma funcional en tiempo de ejecución
- [x] Recursos de idioma preparados en `values`, `values-es` y `values-ca`
- [x] Logs básicos de operaciones y errores
- [x] Evidencia del vídeo disponible en `doc/evidence/v2.0.2/`
- [x] Release final preparada (`v2.0.2`)

Validación técnica:
- [x] `./gradlew.bat :app:assembleDebug --console=plain`
- [x] `./gradlew.bat :app:testDebugUnitTest --console=plain`

---

## 4. Riesgos identificados

- Problemas al mantener el estado entre pantallas  
  Mitigación: uso de `StateFlow`, `ViewModel` y repositorio único para centralizar cambios.

- Validaciones inconsistentes entre UI y Repository  
  Mitigación: reglas centralizadas en `TravelValidator` y comprobación defensiva en varias capas.

- Errores al persistir ajustes de usuario  
  Mitigación: repositorio específico de settings con `SharedPreferences` y restauración al iniciar.

- Textos hardcoded que rompan el multi-language  
  Mitigación: textos movidos a recursos `strings.xml` por idioma y cambio de locale en runtime.

- Falta de cobertura de tests en casos límite
  Mitigación: tests unitarios de dominio y repositorio para CRUD, rangos de fechas y cálculo de gastos.

- Bloqueo circular de validación
  Ejemplo: para mover el viaje el sistema pedía cambiar antes las actividades, pero esas actividades seguían dependiendo del rango actual del viaje.

  Mitigación: se añadió la opción de mover también el itinerario junto con el viaje y validar el resultado final completo.

---

## 5. Mejoras futuras

- Mejorar mensajes de error con más contexto para el usuario.
  Ejemplo: si una actividad queda fuera del rango del viaje, mostrar también las fechas exactas del viaje para corregirlo más rápido.

- Ampliar el selector de países y hacerlo más cómodo con búsqueda o listas más grandes.

- Incorporar persistencia más avanzada.
