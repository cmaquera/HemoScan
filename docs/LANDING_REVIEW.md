# Revisión del proyecto para reconstruir la landing

Revisión del árbol de trabajo local realizada el 26 de octubre de 2026. Su alcance es alinear la presentación web con la app actual, sin modificar la lógica Android ni realizar una auditoría de calidad o validación clínica.

## Estado observado

- App nativa Kotlin, Jetpack Compose, TensorFlow Lite. Versión declarada `0.0.5` (código 5), `minSdk = 24`, `targetSdk = 36`.
- Navegación actual: Inicio, Nuevo chequeo, Resultado, Historial y Ajustes.
- Captura rápida de una foto y guiada de cuatro; selección de interior del ojo o uña, revisión de calidad y análisis local.
- Modelos MobileNetV3 y HemaViT, con selección automática según memoria y CPU, selección manual y motor calibrado de respaldo.
- Historial en preferencias privadas, eliminación de registros y exportación manual de resumen desde Inicio. Guías visual y nutricional integradas.
- Recursos reorganizados: identidad en `assets/branding/`, 12 capturas en `assets/screenshots/` y documentos en `docs/`.

## Cambios visuales revisados frente a HEAD

| Área | Cambio observado | Aplicación en la landing |
| --- | --- | --- |
| Identidad | Nuevo logo y recursos de lanzador | Icono oficial en navegación, pie y favicon |
| Colores | Azul `#155EEF`, coral `#EF4B5A`, fondo `#EDF4FF`, texto `#142744` | Paleta compartida con la app |
| Inicio | Cabecera simplificada con logo; retirada de etiqueta y acceso redundante al perfil | Captura actual como imagen principal |
| Acentos | Corazón y gota del indicador de hemoglobina en coral | Acentos visuales puntuales |
| Último chequeo | Icono de resumen y ajustes de separación | Se muestra la captura real sin recrear la interfaz |

El diff observado de los archivos Kotlin se concentra en `Color.kt`, `HomeScreen.kt` y `HemoglobinGauge.kt`; también cambian la versión y los recursos de identidad. No se observaron cambios de lógica en el motor de inferencia, el repositorio del historial o la navegación dentro de ese diff. Esto no equivale a una prueba funcional de Android.

## Diferencias entre documentación y código

El README general todavía describe `CloudSyncManager`, sincronización opcional y etiquetas Local/Nube. El árbol actual no incluye esa clase y `AndroidManifest.xml` no declara permiso `INTERNET`. La landing describe análisis e historial locales y exportación manual del resumen. No anuncia sincronización en la nube.

Los porcentajes de precisión y las latencias de la documentación no se trasladan al contenido promocional: esta revisión no reproduce los experimentos ni valida esas cifras. Se conserva el carácter experimental y el aviso médico del proyecto.

## Resultado

Landing estática en `landing/`, con las 12 capturas ampliables, explorador de funciones, preguntas frecuentes, descarga del APK local y enlaces al proyecto. El script `scripts/prepare_landing.py` regenera imágenes WebP sin alterar los originales y prepara una copia del APK para alojamiento estático.

## Verificación

- Preparación de los 12 recursos WebP y el icono ejecutada correctamente.
- Sintaxis JavaScript comprobada con `node --check landing/app.js`.
- Referencias locales, anclas y recursos derivados comprobados contra el árbol del proyecto.
- Vista previa servida por HTTP local y recursos comprobados mediante peticiones HTTP.
- El entorno de automatización no expuso ningún navegador disponible. Queda pendiente la inspección visual y de interacciones en un navegador real, incluidas las vistas de escritorio y móvil.
- No se modificó ni compiló el código Android.
