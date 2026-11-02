"use strict";

const screens = [
  ["01-home-dashboard", "Inicio · Tu chequeo personal"],
  ["02-home-guides-actions", "Inicio · Guías y accesos rápidos"],
  ["03-visual-capture-guide", "Guía visual de captura"],
  ["04-nutrition-iron-guide", "Guía de alimentos con hierro"],
  ["05-scan-protocol", "Nuevo chequeo · Elige tu protocolo"],
  ["06-scan-samples-loaded", "Modo guiado · Cuatro fotos listas"],
  ["07-result-detail", "Resultado · Estimación de hemoglobina"],
  ["08-result-recommendations", "Resultado · Detalles y pasos sugeridos"],
  ["09-history-records", "Historial · Tus chequeos guardados"],
  ["10-settings-ai-engine", "Ajustes · Perfil y motor de IA"],
  ["11-settings-hardware-models", "Ajustes · Dispositivo y modelos"],
  ["12-result-normal-healthy", "Resultado · Ejemplo en rango normal"],
];

const screenPath = (index) => `assets/${screens[index][0]}.webp`;
const dialog = document.querySelector("#screenshot-dialog");
const dialogImage = document.querySelector("#dialog-image");
let currentScreen = 0;
let lastTrigger = null;

function updateDialog(index) {
  currentScreen = (index + screens.length) % screens.length;
  dialogImage.src = screenPath(currentScreen);
  dialogImage.alt = screens[currentScreen][1];
  document.querySelector("#dialog-title").textContent = screens[currentScreen][1];
  document.querySelector("#dialog-count").textContent = `${currentScreen + 1} / ${screens.length}`;
}

document.querySelector("#gallery").replaceChildren(...screens.map((screen, index) => {
  const button = document.createElement("button");
  button.type = "button";
  button.className = "gallery-item";
  button.dataset.screen = index;
  button.setAttribute("aria-label", `Ampliar: ${screen[1]}`);
  const img = document.createElement("img");
  img.src = screenPath(index);
  img.alt = screen[1];
  img.width = 1220;
  img.height = 2712;
  img.loading = "lazy";
  img.decoding = "async";
  const caption = document.createElement("span");
  caption.textContent = `${String(index + 1).padStart(2, "0")} · ${screen[1]}`;
  button.append(img, caption);
  return button;
}));

document.addEventListener("click", (event) => {
  const trigger = event.target.closest("[data-screen]");
  if (!trigger) return;
  lastTrigger = trigger;
  updateDialog(Number(trigger.dataset.screen));
  dialog.showModal();
  document.body.classList.add("modal-open");
  document.querySelector("#close-dialog").focus();
});

document.querySelectorAll("[data-feature]").forEach((button) => {
  button.addEventListener("click", () => {
    const index = Number(button.dataset.feature);
    document.querySelectorAll("[data-feature]").forEach((feature) => {
      const active = feature === button;
      feature.classList.toggle("active", active);
      feature.setAttribute("aria-pressed", String(active));
    });
    const preview = document.querySelector("#preview-image");
    preview.src = screenPath(index);
    preview.alt = screens[index][1];
    document.querySelector("#preview-caption").textContent = screens[index][1];
    document.querySelector("#preview-button").dataset.screen = index;
    document.querySelector("#preview-button").setAttribute("aria-label", `Ampliar: ${screens[index][1]}`);
  });
});

document.querySelector("#close-dialog").addEventListener("click", () => dialog.close());
document.querySelector("#previous-screen").addEventListener("click", () => updateDialog(currentScreen - 1));
document.querySelector("#next-screen").addEventListener("click", () => updateDialog(currentScreen + 1));
dialog.addEventListener("keydown", (event) => {
  if (event.key === "ArrowLeft") { event.preventDefault(); updateDialog(currentScreen - 1); }
  if (event.key === "ArrowRight") { event.preventDefault(); updateDialog(currentScreen + 1); }
});
dialog.addEventListener("click", (event) => {
  const bounds = dialog.getBoundingClientRect();
  if (event.target === dialog && (event.clientX < bounds.left || event.clientX > bounds.right || event.clientY < bounds.top || event.clientY > bounds.bottom)) dialog.close();
});
dialog.addEventListener("close", () => {
  document.body.classList.remove("modal-open");
  lastTrigger?.focus();
});
