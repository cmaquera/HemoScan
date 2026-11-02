"""Prepare the standalone landing assets from the canonical project resources.

Run from any directory: python scripts/prepare_landing.py
Requires Pillow. Does not modify the Android app or original screenshots.
"""

from pathlib import Path
from html import escape
import re
import shutil

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
LANDING = ROOT / "landing"


def main():
    assets = LANDING / "assets"
    assets.mkdir(parents=True, exist_ok=True)
    screenshots = sorted((ROOT / "assets" / "screenshots").glob("*.png"))
    if len(screenshots) != 12:
        raise SystemExit(f"Expected 12 screenshots, found {len(screenshots)}. Check landing/app.js.")
    for source in screenshots:
        with Image.open(source) as image:
            image.convert("RGB").save(assets / f"{source.stem}.webp", "WEBP", quality=90, method=6)
    with Image.open(ROOT / "assets" / "branding" / "app_icon.jpg") as image:
        image.convert("RGB").resize((128, 128), Image.Resampling.LANCZOS).save(
            assets / "app-icon.webp", "WEBP", quality=95, method=6
        )
    apk = ROOT / "releases" / "HemoScan_AI_debug.apk"
    if not apk.is_file():
        raise SystemExit("Missing releases/HemoScan_AI_debug.apk. Build/copy the APK before publishing.")
    downloads = LANDING / "downloads"
    downloads.mkdir(exist_ok=True)
    shutil.copy2(apk, downloads / apk.name)
    documents = LANDING / "documents"
    documents.mkdir(exist_ok=True)
    shutil.copy2(ROOT / "LICENSE", documents / "LICENSE.txt")
    # Render the canonical disclaimer without introducing a Markdown dependency.
    disclaimer = (ROOT / "docs" / "DISCLAIMER.md").read_text(encoding="utf-8")
    paragraphs = []
    for block in re.split(r"\n\s*\n", disclaimer.strip()):
        if block.strip() == "---":
            continue
        safe = escape(block.strip())
        safe = re.sub(r"\*\*(.+?)\*\*", r"<strong>\1</strong>", safe, flags=re.S)
        safe = re.sub(r"\*(.+?)\*", r"<em>\1</em>", safe, flags=re.S)
        if safe.startswith("# "):
            paragraphs.append(f"<h1>{safe[2:]}</h1>")
        elif safe.startswith("## "):
            paragraphs.append(f"<h2>{safe[3:]}</h2>")
        else:
            paragraphs.append(f"<p>{safe}</p>")
    content = "\n".join(paragraphs)
    (LANDING / "aviso-medico.html").write_text(
        '<!doctype html>\n<html lang="es"><head><meta charset="utf-8">'
        '<meta name="viewport" content="width=device-width,initial-scale=1">'
        '<title>Aviso médico · HemoScan AI</title><link rel="stylesheet" href="styles.css">'
        '<link rel="icon" href="assets/app-icon.webp" type="image/webp">'
        '</head><body><main class="container legal-page">'
        '<a class="text-link" href="index.html">← Volver a HemoScan AI</a>'
        f'<article>{content}</article></main></body></html>\n', encoding="utf-8"
    )
    total = sum(path.stat().st_size for path in assets.glob("*.webp"))
    print(f"Prepared {len(screenshots)} screenshots + icon ({total / 1024:.0f} KiB).")
    print(f"APK copied ({apk.stat().st_size / 1024 / 1024:.1f} MiB). Landing ready: {LANDING}")


if __name__ == "__main__":
    main()
