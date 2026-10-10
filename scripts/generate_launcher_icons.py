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

        # Standard icon with subtle rounded corners
        resized = src.resize((size, size), Image.Resampling.LANCZOS)
        mask = Image.new("L", (size, size), 0)
        draw = ImageDraw.Draw(mask)
        corner_radius = int(size * 0.2)
        draw.rounded_rectangle([(0, 0), (size, size)], radius=corner_radius, fill=255)

        standard_icon = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        standard_icon.paste(resized, (0, 0), mask)
        standard_icon.save(os.path.join(out_dir, "ic_launcher.webp"), "WEBP")

        # Round icon with circular mask
        circle_mask = Image.new("L", (size, size), 0)
        draw_circle = ImageDraw.Draw(circle_mask)
        draw_circle.ellipse([(0, 0), (size, size)], fill=255)

        round_icon = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        round_icon.paste(resized, (0, 0), circle_mask)
        round_icon.save(os.path.join(out_dir, "ic_launcher_round.webp"), "WEBP")
        print(f"Generated {folder} ({size}x{size})")

if __name__ == "__main__":
    generate_icons()
