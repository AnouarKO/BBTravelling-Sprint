# Sprint 04 - Planning Document

## 1. Sprint Goal

Implementar la persistencia remota de reservas hoteleras y la gestión de imágenes de `BBTraveling`:
- Integrar Retrofit para consumir la API REST de hoteles.
- Buscar hoteles disponibles en London, Paris o Barcelona usando ciudad, fecha inicial y fecha final.
- Mostrar hoteles, habitaciones, precios e imágenes devueltos por la API.
- Permitir reservar una habitación y guardar la reserva localmente con Room, asociada a un viaje.
- Listar y cancelar reservas locales, y cancelar también vía API si el endpoint lo requiere.
- Añadir galería de imágenes por viaje, guardando las imágenes de forma local.
- Mantener arquitectura `View -> ViewModel -> Repository -> Room/API`, Hilt como DI y tests por capas.

Material de referencia:
- Se reutilizará como base técnica la entrega de la actividad extra de Retrofit.
- Se usará solo como referencia para adaptar dependencias, estructura Retrofit, llamadas remotas y patrones de Repository/ViewModel al dominio de reservas hoteleras del Sprint04.

---

## 2. Sprint Backlog

| ID | Tarea | Responsable | Estimación (h) | Prioridad |
|----|------|------------|---------------|----------|
| T0.1 | Revisar `FakeTripDataSource` y eliminarlo o moverlo a test si queda como dead code | Anouar | 0.5 | Media |
| T0.2 | Revisar la actividad extra de Retrofit y extraer el patrón aplicable | Anouar | 1 | Alta |
| T1.1 | Añadir dependencias Retrofit, converter JSON y cliente HTTP | Eloi | 1 | Alta |
| T1.2 | Configurar acceso HTTP a `http://15.224.84.148:8090` si Android bloquea cleartext traffic | Eloi | 0.75 | Alta |
| T1.3 | Crear modelos DTO y API interface para disponibilidad, reserva y cancelación | Anouar | 2 | Alta |
| T1.4 | Crear repositorio remoto para abstraer el uso de la API | Eloi | 1.5 | Alta |
| T1.5 | Inyectar API, cliente y repositorio remoto con Hilt usando módulos coherentes con Sprint03 | Eloi | 1 | Alta |
| T1.6 | Crear unit tests del repositorio remoto mockeando respuestas de red | Anouar | 2 | Alta |
| T2.1 | Crear pantalla de búsqueda de hoteles con ciudad, fecha inicial y fecha final usando date pickers | Anouar | 2 | Alta |
| T2.2 | Mostrar lista de hoteles y habitaciones disponibles con precio, datos principales e imágenes | Eloi | 2 | Alta |
| T2.3 | Crear pantalla o sección de detalle de hotel/habitación con botón de reserva | Eloi | 1.5 | Alta |
| T2.4 | Implementar reserva de habitación y guardar la información localmente en Room asociada a un viaje | Anouar | 3 | Alta |
| T2.5 | Validar búsqueda y reserva: ciudad permitida, rango de fechas correcto, usuario autenticado y errores de API | Anouar | 1.5 | Alta |
| T3.1 | Diseñar modelo local para imágenes de viaje o estrategia de almacenamiento local | Eloi | 1.5 | Alta |
| T3.2 | Permitir adjuntar múltiples imágenes a un viaje desde la UI | Eloi | 2 | Alta |
| T3.3 | Guardar referencias o datos de imágenes localmente con Room/storage | Anouar | 2 | Alta |
| T3.4 | Mostrar galería específica por viaje dentro del detalle del viaje | Anouar | 1.5 | Alta |
| T4.1 | Crear pantalla de listado de reservas locales indicando el viaje relacionado | Anouar | 2 | Alta |
| T4.2 | Mostrar imágenes del hotel y habitación en el listado de reservas | Eloi | 1.5 | Media |
| T4.3 | Implementar cancelación local de reserva y eliminación/actualización del viaje asociado si corresponde | Anouar | 1.5 | Alta |
| T4.4 | Implementar cancelación vía API si el contrato del endpoint lo exige | Eloi | 1.5 | Media |
| T4.5 | Actualizar pantalla `My Trips` para indicar si un viaje incluye reserva hotelera y mostrar sus detalles | Anouar | 2 | Alta |
| T5.1 | Añadir tests de ViewModels para búsqueda, reserva, cancelación y galería | Anouar | 2 | Alta |
| T5.2 | Añadir tests de DAO/Room para reservas e imágenes | Eloi | 2 | Alta |
| T5.3 | Revisar strings en `values`, `values-es` y `values-ca` para todas las nuevas pantallas | Eloi | 1 | Media |
| T5.4 | Actualizar `README.md`, `docs/design.md` y documentación final del sprint | Anouar | 1.5 | Alta |
| T5.5 | Preparar evidencia de vídeo en `doc/evidence/v4.x.x/` y documentar comandos ejecutados | Anouar | 1 | Alta |
| T5.6 | Preparar release/tag final `v4.x.x` | Anouar y Eloi | 0.5 | Alta |

---

## 3. Definition of Done (DoD)

- Retrofit configurado y consumiendo la API de hoteles del profesor.
- La búsqueda permite London, Paris o Barcelona y usa date pickers para todas las fechas.
- La app muestra hoteles, habitaciones, precios e imágenes devueltos por la API.
- El usuario puede reservar una habitación y la reserva queda guardada localmente con Room.
- La reserva queda asociada a un viaje y visible desde `My Trips`.
- Existe listado de reservas locales con detalles de hotel, habitación, precio, fechas e imágenes.
- El usuario puede cancelar reservas localmente y vía API si procede.
- Cada viaje puede tener múltiples imágenes locales asociadas.
- La galería del detalle del viaje muestra solo las imágenes del viaje correspondiente.
- La arquitectura mantiene `View -> ViewModel -> Repository -> Room/API`.
- Hilt sigue siendo la librería de inyección de dependencias.
- Room sigue siendo la persistencia local principal.
- No queda `FakeTripDataSource` como dead code en producción.
- Todos los textos nuevos están localizados en inglés, castellano y catalán.
- Tests unitarios de repositorios, DAOs y ViewModels ejecutados correctamente.
- README, `design.md`, plan, final del sprint y evidencia quedan actualizados.
- Release final publicada como `v4.x.x`.

---

## 4. Riesgos identificados

- La API remota puede estar caída o cambiar respuestas.
  Mitigación: aislar llamadas detrás de Repository y añadir tests con respuestas mock.

- Android puede bloquear HTTP no seguro por usar `http://15.224.84.148:8090`.
  Mitigación: revisar `networkSecurityConfig` o cleartext traffic solo si es necesario para esta API académica.

- Las imágenes pueden complicar permisos, almacenamiento y rendimiento.
  Mitigación: guardar URIs/referencias locales y controlar tamaño/carga desde UI.

- La relación reserva-viaje puede duplicar datos o dejar estados inconsistentes.
  Mitigación: definir entidad o campos de reserva claros y tests de DAO/Repository.

- La cancelación local y remota puede quedar desincronizada.
  Mitigación: mostrar estado de error y no borrar localmente hasta confirmar la estrategia del endpoint.
