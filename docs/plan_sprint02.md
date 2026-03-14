# Sprint 02 - Planning Document

## 1. Sprint Goal

Implementar la logica principal de la aplicacion:
- CRUD inMemory de viajes
- CRUD inMemory de actividades del itinerario
- Validaciones de fechas y campos obligatorios
- Persistencia de ajustes de usuario
- Multi-language
- Testing y documentacion del sprint

---

## 2. Sprint Backlog

| ID | Tarea | Responsable | Estimacion (h) | Prioridad |
|----|------|------------|---------------|----------|
| T1.1 | Implementar CRUD de trips en memoria | Anouar | 2 | Alta |
| T1.2 | Implementar CRUD de activities en memoria | Anouar | 2 | Alta |
| T1.3 | Validar fechas de trips y activities | Eloi | 1.5 | Alta |
| T1.4 | Persistir user settings con SharedPreferences | Eloi | 1.5 | Alta |
| T1.5 | Implementar multi-language en, es, ca | Anouar | 1.5 | Alta |
| T2.1 | Conectar UI con ViewModel y Repository | Anouar | 2 | Alta |
| T2.2 | Crear formularios de alta y edicion | Anouar y Eloi | 2 | Alta |
| T2.3 | Hacer que los cambios se reflejen dinamicamente | Anouar | 1 | Alta |
| T3.1 | Añadir logs para operaciones y errores | Eloi | 0.5 | Media |
| T3.2 | Crear unit tests de CRUD | Anouar | 1.5 | Alta |
| T3.3 | Actualizar README y docs del sprint | Eloi | 1 | Media |
| T3.4 | Preparar release v2.0.0 | Anouar | 0.5 | Alta |

---

## 3. Definition of Done (DoD)

- CRUD de viajes y actividades funcionando en memoria
- Validaciones implementadas en UI y ViewModel
- Settings persistidos y cargados al reiniciar la app
- Terms & conditions apareciendo por primera vez
- Recursos de idioma preparados en carpeta values, values-es y values-ca
- Tests unitarios ejecutados correctamente
- README actualizado

---

## 4. Riesgos identificados

- Problemas al mantener el estado entre pantallas
- Validaciones inconsistentes entre UI y Repository
- Errores al persistir ajustes de usuario
- Textos hardcoded que rompan el multi-language
- Falta de cobertura de tests en casos limite
