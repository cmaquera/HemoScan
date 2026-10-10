import os
from PIL import Image, ImageDraw

def generate_icons(source_path="app_icon.jpg", res_dir="app/src/main/res"):
    if not os.path.exists(source_path):
        print(f"Error: {source_path} does not exist.")
        return

    src = Image.open(source_path).convert("RGBA")

    # Scale down slightly to 82% to give padding so bracket corners are not clipped by round launchers
    scale_factor = 0.82
    new_w = int(1024 * scale_factor)
    new_h = int(1024 * scale_factor)
    scaled_logo = src.resize((new_w, new_h), Image.Resampling.LANCZOS)

    padded_canvas = Image.new("RGBA", (1024, 1024), (255, 255, 255, 255))
    offset = (1024 - new_w) // 2
    padded_canvas.paste(scaled_logo, (offset, offset))

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

        # Standard icon (with subtle squircle mask)
        resized = padded_canvas.resize((size, size), Image.Resampling.LANCZOS)
        mask_rect = Image.new("L", (size, size), 0)
        draw_rect = ImageDraw.Draw(mask_rect)
        radius = int(size * 0.20)
        draw_rect.rounded_rectangle([(0, 0), (size, size)], radius=radius, fill=255)

        standard = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        standard.paste(resized, (0, 0), mask_rect)
        standard.save(os.path.join(out_dir, "ic_launcher.webp"), "WEBP")

        # Round icon (circle mask)
        mask_circle = Image.new("L", (size, size), 0)
        draw_circle = ImageDraw.Draw(mask_circle)
        draw_circle.ellipse([(0, 0), (size, size)], fill=255)

        round_icon = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        round_icon.paste(resized, (0, 0), mask_circle)
        round_icon.save(os.path.join(out_dir, "ic_launcher_round.webp"), "WEBP")
        print(f"Generated {folder} ({size}x{size})")

if __name__ == "__main__":
    generate_icons()
