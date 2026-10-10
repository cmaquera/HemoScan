import base64
import os

art_dir = r"C:\Users\cmaqu\.gemini\antigravity\brain\cec53108-3792-4b29-8438-54664d04ec5d"
workspace = r"c:\Users\cmaqu\workspace\anemia-detection"

def get_b64(fname):
    p = os.path.join(workspace, fname)
    with open(p, "rb") as f:
        return base64.b64encode(f.read()).decode("utf-8")

b64_home = get_b64("screenshot_home.png")
b64_settings = get_b64("screenshot_settings.png")
b64_scan = get_b64("screenshot_scan.png")

html = f"""<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <script src="https://www.gstatic.com/antigravity/web/dev/tailwindcss.min.js"></script>
</head>
<body class="bg-[var(--background)] text-[var(--foreground)] p-4 font-sans">
  <div class="max-w-4xl mx-auto space-y-6">
    <div class="border-b border-[var(--border)] pb-3 flex items-center justify-between">
      <div>
        <h2 class="text-xl font-bold flex items-center gap-2">
          <span>📱</span> Evidencias Reales en el Emulador Android (API 36)
        </h2>
        <p class="text-xs text-[var(--muted-foreground)] mt-1">
          Capturas reales tomadas directamente desde el emulador activo (emulator-5554) con la app abierta en primer plano.
        </p>
      </div>
      <span class="px-3 py-1 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-600 border border-emerald-500/20">
        App Visible y Activa
      </span>
    </div>

    <!-- Grid de capturas -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      
      <!-- Captura 1: Home -->
      <div class="rounded-2xl border border-[var(--border)] bg-[var(--card)] p-4 shadow-sm flex flex-col">
        <div class="flex items-center justify-between mb-3">
          <span class="text-xs font-bold uppercase tracking-wider text-[var(--primary)]">1. Pantalla Inicio</span>
          <span class="text-xs px-2 py-0.5 rounded bg-[var(--border)] text-[var(--muted-foreground)]">Dashboard</span>
        </div>
        <div class="rounded-xl overflow-hidden border border-[var(--border)] bg-black/5 flex-1 flex items-center justify-center">
          <img src="data:image/png;base64,{b64_home}" alt="Pantalla Principal" class="w-full h-auto object-contain max-h-[500px]" />
        </div>
        <div class="mt-3 text-xs space-y-1 text-[var(--muted-foreground)]">
          <p><strong class="text-[var(--foreground)]">Detección RAM:</strong> 2.4 GB (Gama Baja)</p>
          <p><strong class="text-[var(--foreground)]">Motor Activo:</strong> MobileNetV3 [Auto]</p>
          <p><strong class="text-[var(--foreground)]">Cálculo:</strong> 0.22 GFLOPs / 100% Offline</p>
        </div>
      </div>

      <!-- Captura 2: Settings -->
      <div class="rounded-2xl border border-[var(--border)] bg-[var(--card)] p-4 shadow-sm flex flex-col">
        <div class="flex items-center justify-between mb-3">
          <span class="text-xs font-bold uppercase tracking-wider text-[var(--primary)]">2. Configuración</span>
          <span class="text-xs px-2 py-0.5 rounded bg-[var(--border)] text-[var(--muted-foreground)]">Switch IA</span>
        </div>
        <div class="rounded-xl overflow-hidden border border-[var(--border)] bg-black/5 flex-1 flex items-center justify-center">
          <img src="data:image/png;base64,{b64_settings}" alt="Configuración y Diagnóstico" class="w-full h-auto object-contain max-h-[500px]" />
        </div>
        <div class="mt-3 text-xs space-y-1 text-[var(--muted-foreground)]">
          <p><strong class="text-[var(--foreground)]">Hardware:</strong> Google sdk_gphone64 (SDK 36)</p>
          <p><strong class="text-[var(--foreground)]">Selector:</strong> Auto / MobileNet / HemaViT</p>
          <p><strong class="text-[var(--foreground)]">Umbrales:</strong> Hombre, Mujer, Gestante</p>
        </div>
      </div>

      <!-- Captura 3: Scan -->
      <div class="rounded-2xl border border-[var(--border)] bg-[var(--card)] p-4 shadow-sm flex flex-col">
        <div class="flex items-center justify-between mb-3">
          <span class="text-xs font-bold uppercase tracking-wider text-[var(--primary)]">3. Captura y Escaneo</span>
          <span class="text-xs px-2 py-0.5 rounded bg-[var(--border)] text-[var(--muted-foreground)]">Protocolo</span>
        </div>
        <div class="rounded-xl overflow-hidden border border-[var(--border)] bg-black/5 flex-1 flex items-center justify-center">
          <img src="data:image/png;base64,{b64_scan}" alt="Captura y Detección" class="w-full h-auto object-contain max-h-[500px]" />
        </div>
        <div class="mt-3 text-xs space-y-1 text-[var(--muted-foreground)]">
          <p><strong class="text-[var(--foreground)]">Zonas:</strong> Conjuntiva Ocular vs Uñas</p>
          <p><strong class="text-[var(--foreground)]">Calidad:</strong> Verificación de flash y luz</p>
          <p><strong class="text-[var(--foreground)]">Disparador:</strong> Cámara nativa / Galería</p>
        </div>
      </div>

    </div>
  </div>
</body>
</html>"""

os.makedirs(art_dir, exist_ok=True)
out_path = os.path.join(art_dir, "evidence_gallery.html")
with open(out_path, "w", encoding="utf-8") as f:
    f.write(html)
print(f"evidence_gallery.html generado: {os.path.getsize(out_path)} bytes")
