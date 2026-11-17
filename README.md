# BosqueFarma

Aplicación de escritorio en **Java Swing** para la gestión de una farmacia: funcionarios, inventario de medicamentos, turnos de atención, órdenes de pacientes y reportes.

Nació como proyecto académico y se está refactorizando por ciclos (ver [Roadmap](#roadmap)) para llevarlo a un código limpio, probado y documentado.

[![CI](https://github.com/Kesagaro/ProyectoMeditech/actions/workflows/ci.yml/badge.svg)](https://github.com/Kesagaro/ProyectoMeditech/actions/workflows/ci.yml) ![Java](https://img.shields.io/badge/Java-17%2B-orange) ![Maven](https://img.shields.io/badge/build-Maven-C71A36) ![Swing](https://img.shields.io/badge/UI-Swing-blue) ![Arquitectura](https://img.shields.io/badge/arquitectura-MVC-green)

## Funcionalidades

- Registro (cédula, nombre, correo) e inicio de sesión de funcionarios
- Inventario: agregar, actualizar, listar y eliminar medicamentos por ID
- Búsqueda de medicamentos por nombre con precio y stock
- Generación de turnos de atención asociados a la cédula del paciente
- Órdenes de pacientes que validan el medicamento y el stock disponible, y lo descuentan del inventario
- Listado y eliminación de funcionarios
- Reportes: Top 5 de medicamentos más expendidos, calculado con las órdenes registradas

## Capturas

| Inicio | Menú principal |
|---|---|
| ![Pantalla de inicio](docs/img/01-inicio.png) | ![Menú principal](docs/img/03-menu.png) |
| **Inventario** | **Búsqueda de medicamentos** |
| ![Inventario de medicamentos](docs/img/04-inventario.png) | ![Búsqueda de medicamentos por nombre](docs/img/05-busqueda.png) |
| **Órdenes de pacientes** | **Top ventas** |
| ![Registro y listado de órdenes](docs/img/06-ordenes.png) | ![Top 5 de medicamentos más expendidos](docs/img/07-top-ventas.png) |
| **Turnos** | **Funcionarios** |
| ![Turno asignado a un paciente](docs/img/08-turno.png) | ![Listado de funcionarios sin contraseñas](docs/img/09-funcionarios.png) |

Capturas tomadas de la aplicación con datos de ejemplo.

## Tecnologías

- Java 17+ (probado con JDK 17, 21 y 25)
- Swing (CardLayout, componentes personalizados)
- Persistencia en archivos: texto plano y serialización Java
- Maven (con Maven Wrapper) y JUnit 5
- GitHub Actions: compila y ejecuta las pruebas en cada push y pull request

## Ejecutar en VS Code

1. Instalar un **JDK 17 o superior** (por ejemplo [Temurin](https://adoptium.net/)).
2. Abrir la carpeta del proyecto en VS Code.
3. Instalar la extensión recomendada **Extension Pack for Java** (VS Code la sugiere al abrir el proyecto).
4. Pulsar **F5** y elegir **Ejecutar BosqueFarma**.

Usuario de demostración: cédula `112233`, contraseña `112233`.

### Desde la terminal

Desde la raíz del proyecto (los datos se guardan en `data/`, relativo a esa carpeta). No hace falta instalar Maven: `mvnw` lo descarga la primera vez (en Windows, `mvnw.cmd`).

```bash
./mvnw compile exec:java
```

## Pruebas

Las pruebas JUnit 5 están en `src/test/java` y cubren los turnos, las reglas de órdenes, stock y Top 5, y la persistencia de funcionarios (hash de contraseñas y migración), medicamentos y órdenes. Cada prueba trabaja en una carpeta temporal, sin tocar `data/`.

```bash
./mvnw test
```

En VS Code también aparecen en la vista **Testing** (ícono del matraz). GitHub Actions las ejecuta con Java 17 y 21 en cada push y pull request.

## Arquitectura (MVC)

```
src/main/java/co/edu/unbosque/
├── controller/          Main (punto de entrada) y Controller (eventos vista → modelo)
├── model/               Farmacia (fachada del modelo) y Turnos
│   └── persistence/     DAO y DTO de funcionarios, medicamentos y órdenes; interfaz Crud<T>
└── view/                View (JFrame + CardLayout) y un panel por pantalla

src/main/resources/            Logo de la aplicación
src/test/java/co/edu/unbosque/ Pruebas JUnit del modelo y la persistencia
```

### Diagrama de clases

![Diagrama de clases MVC](docs/diagramas/diagrama-de-clases.png)

Modelo editable en Astah: [`docs/diagramas/BosqueFarma.asta`](docs/diagramas/BosqueFarma.asta).

### Persistencia

| Archivo | Formato | Contenido |
|---|---|---|
| `data/usuarios.txt` | texto `cedula,hash,nombre,correo` | Funcionarios; la contraseña se guarda como hash PBKDF2 con sal |
| `data/Medicamentos.txt` | serialización Java | Inventario |
| `data/ordenes.txt` | texto `documento,entidad,medicamento,cantidad` | Órdenes |

## Roadmap

- [x] **Ciclo 1: Bugs críticos.** Listeners duplicados, turnos desde 1, actualizar por ID real, validación de entradas, cédulas únicas.
- [x] **Ciclo 2: Funciones incompletas.** Listar y eliminar inventario, guardar nombre y correo en el registro, cédula en turnos, botón "Medicina".
- [x] **Ciclo 3: Lógica de negocio.** Las órdenes validan y descuentan stock; Top 5 calculado con datos reales.
- [x] **Ciclo 4: Limpieza.** Nombres claros, código muerto fuera, recursos cargados con `getResource()`.
- [x] **Ciclo 5: Seguridad y datos.** Contraseñas con hash y sal; CSV robusto.
- [x] **Ciclo 6: Pruebas.** JUnit para DAO, turnos y reglas de negocio.

### Fase 2

- [x] **Ciclo 7: Maven y CI.** Estructura estándar de Maven, Maven Wrapper y GitHub Actions.
- [ ] **Ciclo 8: Release ejecutable.** `BosqueFarma.jar` publicado en GitHub Releases.
- [ ] **Ciclo 9: Reglas en el modelo.** Validaciones del controlador movidas al modelo, con pruebas.
- [ ] **Ciclo 10: Mejoras funcionales.** Órdenes con fecha y total, turnos persistentes, sesión del funcionario.
- [ ] **Ciclo 11: Presentación.** README en inglés y metadatos del repositorio.
- [ ] **Ciclo 12: Base de datos.** Persistencia en SQLite con JDBC.

## Documentación

- [`docs/`](docs/README.md): diagramas UML (Astah) y decisiones de diseño.
