#!/usr/bin/env python3
"""Export spec-conformant Android TV launcher-icon PNGs from the vector source.

Google's TV app icon guidelines want the square (1:1) launcher icon at >=160x160 (xhdpi)
up to 320x320 (xxxhdpi). Our legacy raster ic_launcher was phone-sized (xhdpi 96x96).
API 26+ TVs use the adaptive vector icon (mipmap-anydpi-v26, crisp at any density), but
the TV module's minSdk is 23, so API 23-25 still falls back to these rasters — regenerate
them from the same lamp vector at the TV density sizes.

The lamp paths come from ic_launcher_foreground.xml, but rendered at the legacy "fill"
scale (~0.642, matching the banner) rather than the adaptive foreground's padded 0.4107,
so the legacy icon fills the tile like the original webp did.

main  -> tv/src/main/res/mipmap-*/ic_launcher.png  (brand field, @color/launcher_background)
debug -> tv/src/debug/res/mipmap-*/ic_launcher.png (red field, debug colors override)

Requires an SVG rasterizer (cross-platform): brew install resvg / scoop install resvg /
cargo install resvg (falls back to rsvg-convert).
"""
from __future__ import annotations

import shutil
import subprocess
import sys
import tempfile
import xml.etree.ElementTree as ET
from pathlib import Path

ANDROID_NS = "http://schemas.android.com/apk/res/android"
REPO = Path(__file__).resolve().parent.parent
FOREGROUND = REPO / "tv/src/main/res/drawable/ic_launcher_foreground.xml"

# Square launcher-icon sizes from Google's TV app icon guidelines (1:1).
BUCKETS = {"mdpi": 80, "hdpi": 120, "xhdpi": 160, "xxhdpi": 240, "xxxhdpi": 320}

# Lamp placement in the 108x108 icon canvas: cap width matches the original at 0.642,
# centered. (The adaptive foreground pads to 0.4107 for mask/parallax; legacy icons fill.)
LAMP_SCALE, LAMP_TX, LAMP_TY = 0.642, 30.9, 18.0

VARIANTS = [
    ("main", REPO / "tv/src/main/res", [REPO / "tv/src/main/res/values/colors.xml"]),
    (
        "debug",
        REPO / "tv/src/debug/res",
        [REPO / "tv/src/main/res/values/colors.xml", REPO / "tv/src/debug/res/values/colors.xml"],
    ),
]
_CAP = {"butt": "butt", "round": "round", "square": "square"}
_JOIN = {"miter": "miter", "round": "round", "bevel": "bevel"}


def a(el: ET.Element, name: str) -> str | None:
    return el.get(f"{{{ANDROID_NS}}}{name}")


def load_color(paths: list[Path], name: str) -> str:
    value = None
    for p in paths:
        if p.exists():
            for c in ET.parse(p).getroot().findall("color"):
                if c.get("name") == name and c.text:
                    value = c.text.strip()  # later files override
    if value is None:
        raise SystemExit(f"color {name} not found")
    return value if len(value.lstrip("#")) == 6 else "#" + value.lstrip("#")[-6:]


def lamp_paths_svg() -> str:
    """The white lamp paths from the adaptive foreground, as SVG <path> elements."""
    group = ET.parse(FOREGROUND).getroot().find("group")
    out = []
    for p in group.findall("path"):
        attrs = [f'd="{a(p, "pathData")}"']
        if a(p, "fillColor"):
            attrs.append(f'fill="{a(p, "fillColor")}"')
        else:
            attrs.append('fill="none"')
        if a(p, "strokeColor"):
            attrs.append(f'stroke="{a(p, "strokeColor")}"')
            attrs.append(f'stroke-width="{a(p, "strokeWidth") or 1}"')
            if a(p, "strokeLineCap"):
                attrs.append(f'stroke-linecap="{_CAP[a(p, "strokeLineCap")]}"')
            if a(p, "strokeLineJoin"):
                attrs.append(f'stroke-linejoin="{_JOIN[a(p, "strokeLineJoin")]}"')
        out.append("    <path " + " ".join(attrs) + " />")
    return "\n".join(out)


def build_svg(field_hex: str, lamp: str) -> str:
    return (
        '<svg xmlns="http://www.w3.org/2000/svg" width="108" height="108" viewBox="0 0 108 108">\n'
        f'  <path fill="{field_hex}" d="M0,0h108v108h-108z" />\n'
        f'  <g transform="translate({LAMP_TX},{LAMP_TY}) scale({LAMP_SCALE})">\n{lamp}\n  </g>\n'
        "</svg>\n"
    )


def find_rasterizer() -> str:
    for tool in ("resvg", "rsvg-convert"):
        if shutil.which(tool):
            return tool
    raise SystemExit("No SVG rasterizer. Install: brew/scoop/cargo install resvg")


def rasterize(tool: str, svg: Path, png: Path, size: int) -> None:
    if tool == "resvg":
        cmd = ["resvg", "--width", str(size), "--height", str(size), str(svg), str(png)]
    else:
        cmd = ["rsvg-convert", "-w", str(size), "-h", str(size), "-o", str(png), str(svg)]
    subprocess.run(cmd, check=True, capture_output=True)


def main() -> int:
    if not FOREGROUND.exists():
        raise SystemExit(f"missing {FOREGROUND}")
    tool = find_rasterizer()
    lamp = lamp_paths_svg()
    with tempfile.TemporaryDirectory() as td:
        for variant, res_dir, color_files in VARIANTS:
            field = load_color(color_files, "launcher_background")
            svg = Path(td) / f"ic_{variant}.svg"
            svg.write_text(build_svg(field, lamp))
            print(f"{variant} (field {field}):")
            for bucket, size in BUCKETS.items():
                out = res_dir / f"mipmap-{bucket}" / "ic_launcher.png"
                out.parent.mkdir(parents=True, exist_ok=True)
                rasterize(tool, svg, out, size)
                # drop the stale phone-sized webp this PNG replaces
                webp = out.with_suffix(".webp")
                if webp.exists():
                    webp.unlink()
                print(f"  {bucket:8} {size}x{size:<4} -> {out.relative_to(REPO)}")
    print(f"\nDone via {tool}.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
