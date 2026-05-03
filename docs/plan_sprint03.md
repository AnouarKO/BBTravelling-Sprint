# Sprint 03 - Planning Document

## 1. Sprint Goal

Implementar la capa persistente y de autenticación de la aplicación:
- Sustituir el almacenamiento inMemory por SQLite usando Room
- Mantener la arquitectura Repository y usar Hilt como librería DI
- Implementar login, register, logout y recover password con Firebase
- Persistir información local de usuario y accesos a la app
- Adaptar viajes e itinerarios para trabajar por usuario logado
- Añadir testing, logs y documentación técnica del sprint

---

## 2. Sprint Backlog

| ID | Tarea | Responsable | Estimación (h) | Prioridad |
|----|------|------------|---------------|----------|
| T1.1 | Crear clase Room Database | Anouar | 1 | Alta |
| T1.2 | Definir Entities de Trip e ItineraryItem con datetime, texto e integer | Anouar y Eloi | 1.5 | Alta |
| T1.3 | Crear DAOs para operaciones de base de datos | Eloi | 1.5 | Alta |
| T1.4 | Implementar CRUD persistente con DAO para trips e itineraries | Anouar | 2.5 | Alta |
| T1.5 | Sustituir el flujo inMemory por Room en ViewModels y repositorios | Anouar | 2 | Alta |
| T1.6 | Integrar Hilt para inyección de DB, DAO y Repository | Eloi | 1.5 | Alta |
| T1.7 | Asegurar que la UI se actualiza al cambiar la base de datos | Anouar | 1 | Alta |
| T2.1 | Conectar la app con Firebase | Anouar | 1 | Alta |
| T2.2 | Diseñar pantalla de login | Eloi | 1 | Media |
| T2.3 | Implementar login con email y password | Anouar | 1.5 | Alta |
| T2.4 | Crear acción de logout en la app | Eloi | 0.5 | Alta |
| T2.5 | Añadir logs de operaciones y errores de autenticación | Eloi | 0.5 | Media |
| T3.1 | Diseñar pantalla de registro | Eloi | 1 | Media |
| T3.2 | Implementar registro con Firebase usando Repository | Anouar | 2 | Alta |
| T3.3 | Añadir verificación de email y recover password | Anouar y Eloi | 1.5 | Alta |
| T4.1 | Persistir user info en tabla local con login, username, birthdate, address, country, phone y accept receive emails | Eloi | 2 | Alta |
| T4.2 | Adaptar tabla de trips para múltiples usuarios | Anouar | 2 | Alta |
| T4.3 | Filtrar y mostrar solo trips del usuario logado | Anouar | 1 | Alta |
| T4.4 | Persistir accesos login/logout con userId y datetime | Eloi | 1.5 | Media |
| T4.5 | Actualizar design.md con schema y uso de base de datos | Eloi | 1 | Media |
| T5.1 | Escribir unit tests de DAO e interacciones de base de datos | Anouar | 2 | Alta |
| T5.2 | Validar datos persistidos y casos límite | Anouar | 1 | Alta |
| T5.3 | Añadir logs de operaciones y errores de Room | Eloi | 0.5 | Media |
| T5.4 | Actualizar README y documentación final del sprint | Eloi | 1 | Media |
| T5.5 | Evidencia de vídeo | Anouar | 0.5 | Alta |

---

## 3. Definition of Done (DoD)

- Room sustituye el almacenamiento inMemory para trips y actividades
- Arquitectura Repository mantenida con Hilt configurado como DI
- Login, register, logout y recover password funcionando con Firebase
- Información local de usuario persistida con username único
- Trips asociados al usuario logado y filtrados correctamente
- Registro de accesos login/logout persistido en base de datos
- Validaciones y logs implementados para auth y operaciones DB
- Tests unitarios de DAO y base de datos ejecutados correctamente
- design.md actualizado con schema y estrategia de uso o migración
- README actualizado y release final publicada como v3.x.x

---

## 4. Riesgos identificados

- Firebase puede dar problemas al conectarlo o al configurar las credenciales
- El cambio de inMemory a Room puede romper parte de la UI o algunos ViewModels
- Hilt no está implementado en el proyecto actual
- Puede haber errores al definir las relaciones entre usuario, viaje y actividades en la base de datos
- Puede haber desajustes entre el usuario autenticado en Firebase y el usuario guardado en local
- Al añadir Room y Firebase pueden fallar casos de validación o persistencia
- Si no se cubren bien los DAO y repositorios con tests puede quedar algún fallo sin detectar
