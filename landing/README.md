# Landing de HemoScan AI

Página estática en español, reconstruida a partir del código Android v0.0.5 y de las 12 capturas reales de `assets/screenshots/`. Sin dependencias de JavaScript, fuentes remotas, analítica ni servicios de terceros para funcionar.

## Preparar y abrir

Desde la raíz del repositorio:

```powershell
python -m pip install Pillow  # Solo si Pillow no está instalado
python scripts/prepare_landing.py
python -m http.server 4173 --bind 127.0.0.1 --directory landing
```

Abre <http://127.0.0.1:4173>. También puedes abrir `index.html` directamente para revisar el diseño.

El script convierte las capturas originales a WebP conservando su resolución, prepara el icono y copia el APK local a `downloads/`. Los PNG originales permanecen intactos. El APK no se versiona, siguiendo la regla del repositorio para `*.apk`. Si falta el binario, el script termina con un mensaje para impedir que se publique una descarga rota. No compila ni certifica la versión del APK; copia el binario proporcionado en `releases/`.

También genera `aviso-medico.html` a partir de `docs/DISCLAIMER.md` y copia la licencia a `documents/LICENSE.txt`, de modo que ambos enlaces funcionan sin depender de que la reorganización de carpetas ya esté publicada en GitHub.

## Estructura y mantenimiento

- `index.html`: contenido, navegación, preguntas frecuentes y descarga.
- `styles.css`: diseño adaptable, estados de foco y preferencias de movimiento reducido.
- `app.js`: selector de funciones, galería de 12 capturas y ampliación mediante un diálogo nativo (Escape, flechas y retorno del foco).
- `assets/`: imágenes WebP derivadas, incluidas para poder previsualizar sin preparación previa.
- `downloads/`: APK preparado localmente, excluido de Git por `*.apk`.

Al reemplazar capturas, el APK o el aviso médico en sus carpetas canónicas, vuelve a ejecutar `prepare_landing.py`. Si cambian los nombres o el número de capturas, actualiza también `app.js`, las referencias en `index.html` y la validación del script.

Para alojarla, prepara los recursos y sube el **contenido completo de `landing/`**, incluido `downloads/`, a un alojamiento estático. No necesita un servidor de aplicación ni un proceso de compilación. Esta reconstrucción se mantiene local y no realiza publicaciones automáticas.

## Contenido verificado en el proyecto

La landing refleja captura guiada de cuatro fotos o rápida de una, selección de ojo/uña, análisis local, modelos adaptativos, historial local, guía nutricional y exportación manual del resumen. No anuncia respaldo en la nube: el árbol actual no contiene `CloudSyncManager` ni permiso de internet en el manifiesto, aunque el README general todavía lo menciona.

Presenta la app como herramienta experimental y las cifras de las capturas como ejemplos. No utiliza porcentajes de precisión ni promesas de diagnóstico o validación clínica. El mínimo Android 7.0 corresponde a `minSdk = 24`.
