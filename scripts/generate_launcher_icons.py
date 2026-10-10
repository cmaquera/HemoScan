import os
from PIL import Image, ImageDraw

def generate_icons(source_path="app_icon.jpg", res_dir="app/src/main/res"):
    if not os.path.exists(source_path):
        print(f"Error: {source_path} does not exist.")
        return

    src = Image.open(source_path).convert("RGBA")

    densities = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192,
    }

    for folder, size in densities.items():
        out_dir = os.path.join(res_dir, folder)
        os.makedirs(out_dir, exist_ok=True)

        resized = src.resize((size, size), Image.Resampling.LANCZOS)

        # Standard icon with subtle rounded corners
        mask_rect = Image.new("L", (size, size), 0)
        draw_rect = ImageDraw.Draw(mask_rect)
        radius = int(size * 0.20)
        draw_rect.rounded_rectangle([(0, 0), (size, size)], radius=radius, fill=255)

        standard = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        standard.paste(resized, (0, 0), mask_rect)
        standard.save(os.path.join(out_dir, "ic_launcher.webp"), "WEBP")

        # Round icon with circular mask
        mask_circle = Image.new("L", (size, size), 0)
        draw_circle = ImageDraw.Draw(mask_circle)
        draw_circle.ellipse([(0, 0), (size, size)], fill=255)

        round_icon = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        round_icon.paste(resized, (0, 0), mask_circle)
        round_icon.save(os.path.join(out_dir, "ic_launcher_round.webp"), "WEBP")
        print(f"Generated Google-style {folder} ({size}x{size})")

if __name__ == "__main__":
    generate_icons()
