# 📅 Mi Horario Universitario (University Schedule)

## 📌 Acerca de la aplicación

**Mi Horario Universitario** es una aplicación Android desarrollada para ayudar a los estudiantes a **organizar su vida académica** de forma sencilla y eficiente.  

La app permite:  
- Registrar horarios de clases.  
- Gestionar cursos con detalles completos.  
- Guardar información de profesores.  
- Recibir notificaciones de próximos eventos académicos.  

Está construida bajo un enfoque **offline-first**: toda la información se guarda primero en la base de datos local del dispositivo y luego se sincroniza automáticamente con la nube cuando hay conexión a Internet.  
De esta manera, los estudiantes pueden acceder a su información desde cualquier dispositivo Android iniciando sesión con **Google** o con **usuario y contraseña**.

---

## ✨ Características principales

- 🗓️ **Gestión de horarios** → agrega, visualiza y organiza tus horarios de clase.  
- 📚 **Gestión de cursos** → administra tus cursos con detalles completos.  
- 👩‍🏫 **Directorio de profesores** → guarda y consulta la información de tus docentes.  
- ☁️ **Sincronización en la nube** → accede a tus cursos y horarios desde cualquier dispositivo.  
- 📶 **Modo offline-first** → los datos se almacenan localmente y se sincronizan automáticamente al reconectarse.  
- 🔔 **Notificaciones inteligentes** → recordatorios automáticos antes del inicio de cada clase.  
- 🎨 **Personalización** → formato de hora (12h/24h), día de inicio de semana, vistas en cuadrícula o lista, entre otras.  
- 🔐 **Inicio de sesión seguro** → autenticación con Google o correo/contraseña (Firebase Auth).  
- 📢 **Anuncios integrados** → monetización mediante Google AdMob.  
- 🛡️ **Gestión de fallos** → monitoreo de errores en producción con Firebase Crashlytics.  
- 🆕 **Control de versiones** → uso de Firebase Remote Config para forzar actualizaciones.  
- 🖌️ **UI moderna** → migración progresiva a **Jetpack Compose con Material 3**.  

---

## 🏗️ Arquitectura

La aplicación sigue las [guías oficiales de arquitectura de Android](https://developer.android.com/topic/architecture), asegurando una estructura escalable y fácil de mantener.  

- 🧩 **MVVM (Model-View-ViewModel)** → separación clara entre lógica de negocio y de UI.  
- 🎛️ **ViewModel + Flows + LiveData** → manejo de datos reactivos y observables.  
- 📚 **Repository Pattern** → acceso desacoplado a datos locales y remotos.  
- 💾 **Room Database** → almacenamiento local persistente.  
- 🔄 **WorkManager** → sincronización en segundo plano de operaciones pendientes.  

---

## 🧩 Modularización

El proyecto está organizado en **módulos independientes** para mejorar escalabilidad y mantenibilidad, siguiendo las [guías de modularización de Android](https://developer.android.com/topic/modularization).  

- **App Module** → punto de entrada de la app (Splash, RemoteConfigHelper, forzado de versiones).  
- **Core Modules**:  
  - 🛠️ **Common** → utilidades compartidas, manejo de sesiones con Firebase Auth.  
  - 🔗 **Data** → sincronización entre base local y nube con Workers.  
  - 💾 **Database** → operaciones locales con Room.  
  - ⚙️ **Datastore** → almacenamiento de preferencias.  
  - 🎨 **DesignSystem** → componentes de UI reutilizables.  
  - 📐 **Model** → modelos de datos con soporte offline-first.  
  - 🌐 **Network** → acceso a Firestore y servicios en la nube.  
  - 🖌️ **UI** → temas y componentes visuales (con soporte Compose).  
- **Feature Modules**:  
  - 📚 **Course** → gestión de cursos.  
  - 🗓️ **Schedule** → gestión y visualización de horarios.  
  - 🔐 **Login** → autenticación con Google y correo/contraseña.  
- **Sync Module** → sincronización entre la nube y la base local.  
- **Build-Logic** → configuraciones centralizadas de Gradle.  
- **Testing Modules** → pruebas unitarias, de integración y de UI.  

---

## 🛠️ Tecnologías principales

- **Kotlin**  
- **Jetpack Compose + Views (migración progresiva)**  
- **Firebase (Auth, Firestore, Crashlytics, Remote Config, Analytics)**  
- **Google AdMob**  
- **Room Database**  
- **WorkManager**  
- **Coroutines + Flows**  
- **Hilt (Inyección de dependencias)**  
- **JUnit, Robolectric, Espresso, MockK**  

---

## 🎨 Interfaz de Usuario

La app ofrece una interfaz **moderna y personalizable**:  
- Diseño con **Material 3**, soporte para tema claro/oscuro.  
- Visualización en **lista o cuadrícula** para horarios.  
- Pantallas de configuración flexibles.  
- Migración progresiva a **Compose** para una experiencia más fluida.  

---

## 🖼️ Capturas de Pantalla

*Visualización de horarios*  
<img src="screenshots/horarios-university-schedule-español.png" width="400" alt="Visualización de horarios">  

*Detalle de los horarios*  
<img src="screenshots/detalles-horarios-university-schedule-español.png" width="400" alt="Detalle de los horarios">  

*Agregar horario*  
<img src="screenshots/nuevo-horario-university-schedule-español.png" width="400" alt="Agregar horario">  

*Personalización de los horarios*  
<img src="screenshots/personalizacion-horario-university-schedule-español.png" width="400" alt="Personalización de los horarios">  

*Recordatorio de los horarios*  
<img src="screenshots/recordatorios-horios-university-schedule.png" width="400" alt="Recordatorio de los horarios">  

*Visualización de cursos*  
<img src="screenshots/cursos-university-schedule-español.png" width="400" alt="Visualización de cursos">  

*Agregar cursos*  
<img src="screenshots/add_course.jpg" width="400" alt="Agregar cursos">  

*Configuración del horario*  
<img src="screenshots/configuracion-horario-university-schedule-español.png" width="400" alt="Configuración del horario">  

---

📌 Proyecto desarrollado en **Kotlin**, con arquitectura limpia, modularización avanzada y una UI moderna basada en **Material 3 y Compose**.  
