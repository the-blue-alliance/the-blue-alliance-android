#!/usr/bin/env python3
"""Export spec-conformant Android TV banner PNGs from the lossless vector source.

The TV launcher rasterizes `android:banner` at the asset's intrinsic size and then
scales that bitmap when a tile is focused, so a single small banner granulates on
zoom (issue #1437). This renders the vector drawable to PNGs across every density
bucket (mdpi..xxxhdpi, 160x90..640x360 — the sizes Google's TV icon guidelines list)
so the launcher always has a crisp source to scale from.

The vector at tv/src/main/res/drawable/tv_banner.xml stays the source of truth; re-run
this whenever it changes. It writes two source-set variants:
  * main  -> tv/src/main/res/mipmap-*  (brand field from @color/launcher_background)
  * debug -> tv/src/debug/res/mipmap-* (red field, matching the debug colors override)

Requires an SVG rasterizer. Cross-platform, installable everywhere:
  macOS / Linux:  brew install resvg      (or: brew install librsvg)
  Windows:        scoop install resvg     (or grab resvg.exe, or cargo install resvg)

Usage:
  scripts/export-tv-banner.py                 # write main + debug PNGs
  scripts/export-tv-banner.py --check         # render to a temp dir, don't write res/
  scripts/export-tv-banner.py --color '#000'  # force one field color for every variant
"""
from __future__ import annotations

import argparse
import shutil
import subprocess
import sys
import tempfile
import xml.etree.ElementTree as ET
from pathlib import Path

ANDROID_NS = "http://schemas.android.com/apk/res/android"
REPO = Path(__file__).resolve().parent.parent
VECTOR = REPO / "tv/src/main/res/drawable/tv_banner.xml"

# Each variant resolves @color/ refs from its own source-set colors.xml (debug
# overlaid on main), so the debug field comes out red exactly like the live build.
VARIANTS = [
    ("main", REPO / "tv/src/main/res", [REPO / "tv/src/main/res/values/colors.xml"]),
    (
        "debug",
        REPO / "tv/src/debug/res",
        [REPO / "tv/src/main/res/values/colors.xml", REPO / "tv/src/debug/res/values/colors.xml"],
    ),
]

# Density bucket -> banner pixel size, straight from Google's TV app icon guidelines
# (16:9, xhdpi == the legacy 320x180 floor; xxxhdpi == 640x360).
BUCKETS = {
    "mdpi": (160, 90),
    "hdpi": (240, 135),
    "xhdpi": (320, 180),
    "xxhdpi": (480, 270),
    "xxxhdpi": (640, 360),
}


def a(el: ET.Element, name: str) -> str | None:
    return el.get(f"{{{ANDROID_NS}}}{name}")


def load_colors(paths: list[Path]) -> dict[str, str]:
    colors: dict[str, str] = {}
    for path in paths:
        if path.exists():
            for c in ET.parse(path).getroot().findall("color"):
                if c.get("name") and c.text:
                    colors[c.get("name")] = c.text.strip()  # later files override
    return colors


def resolve_color(value: str, colors: dict[str, str], override: str | None) -> tuple[str, float]:
    """Return (#rrggbb, opacity) for an Android color string or @color/ ref."""
    if value.startswith("@color/"):
        name = value.split("/", 1)[1]
        if override is not None:
            value = override
        elif name in colors:
            value = colors[name]
        else:
            raise SystemExit(f"Unresolved color reference: {value}")
    v = value.lstrip("#")
    if len(v) == 3:  # #rgb
        r, g, b = (ch * 2 for ch in v)
        return f"#{r}{g}{b}", 1.0
    if len(v) == 4:  # #argb
        aa, r, g, b = (ch * 2 for ch in v)
        return f"#{r}{g}{b}", int(aa, 16) / 255
    if len(v) == 6:  # #rrggbb
        return f"#{v}", 1.0
    if len(v) == 8:  # #aarrggbb
        return f"#{v[2:]}", int(v[:2], 16) / 255
    raise SystemExit(f"Unparseable color: {value}")


_CAP = {"butt": "butt", "round": "round", "square": "square"}
_JOIN = {"miter": "miter", "round": "round", "bevel": "bevel"}


def path_to_svg(el: ET.Element, colors: dict[str, str], override: str | None) -> str:
    attrs = [f'd="{a(el, "pathData")}"']
    fill = a(el, "fillColor")
    if fill:
        hexc, op = resolve_color(fill, colors, override)
        attrs.append(f'fill="{hexc}"')
        op *= float(a(el, "fillAlpha") or 1)
        if op < 1:
            attrs.append(f'fill-opacity="{op:g}"')
    else:
        attrs.append('fill="none"')
    if a(el, "fillType"):
        attrs.append(f'fill-rule="{ "evenodd" if a(el, "fillType") == "evenOdd" else "nonzero" }"')
    stroke = a(el, "strokeColor")
    if stroke:
        hexc, op = resolve_color(stroke, colors, override)
        attrs.append(f'stroke="{hexc}"')
        attrs.append(f'stroke-width="{a(el, "strokeWidth") or 1}"')
        op *= float(a(el, "strokeAlpha") or 1)
        if op < 1:
            attrs.append(f'stroke-opacity="{op:g}"')
        if a(el, "strokeLineCap"):
            attrs.append(f'stroke-linecap="{_CAP[a(el, "strokeLineCap")]}"')
        if a(el, "strokeLineJoin"):
            attrs.append(f'stroke-linejoin="{_JOIN[a(el, "strokeLineJoin")]}"')
        if a(el, "strokeMiterLimit"):
            attrs.append(f'stroke-miterlimit="{a(el, "strokeMiterLimit")}"')
    return "<path " + " ".join(attrs) + " />"


def group_transform(el: ET.Element) -> str:
    sx, sy = float(a(el, "scaleX") or 1), float(a(el, "scaleY") or 1)
    tx, ty = float(a(el, "translateX") or 0), float(a(el, "translateY") or 0)
    px, py = float(a(el, "pivotX") or 0), float(a(el, "pivotY") or 0)
    rot = float(a(el, "rotation") or 0)
    # Matches VectorDrawable VGroup matrix: T(tx+px,ty+py) R(rot) S(sx,sy) T(-px,-py)
    return (
        f"translate({tx + px:g},{ty + py:g}) rotate({rot:g}) "
        f"scale({sx:g},{sy:g}) translate({-px:g},{-py:g})"
    )


def vector_to_svg(colors: dict[str, str], override: str | None) -> str:
    root = ET.parse(VECTOR).getroot()
    vw, vh = a(root, "viewportWidth"), a(root, "viewportHeight")
    body: list[str] = []

    def walk(parent: ET.Element, indent: str):
        for child in parent:
            tag = child.tag.split("}")[-1]
            if tag == "path":
                body.append(indent + path_to_svg(child, colors, override))
            elif tag == "group":
                body.append(f'{indent}<g transform="{group_transform(child)}">')
                walk(child, indent + "  ")
                body.append(f"{indent}</g>")

    walk(root, "  ")
    return (
        f'<svg xmlns="http://www.w3.org/2000/svg" width="{vw}" height="{vh}" '
        f'viewBox="0 0 {vw} {vh}">\n' + "\n".join(body) + "\n</svg>\n"
    )


def find_rasterizer() -> str:
    for tool in ("resvg", "rsvg-convert"):
        if shutil.which(tool):
            return tool
    raise SystemExit(
        "No SVG rasterizer found. Install one:\n"
        "  brew install resvg        (macOS/Linux)\n"
        "  scoop install resvg       (Windows)\n"
        "  cargo install resvg       (any)"
    )


def rasterize(tool: str, svg: Path, png: Path, w: int, h: int) -> None:
    if tool == "resvg":
        cmd = ["resvg", "--width", str(w), "--height", str(h), str(svg), str(png)]
    else:  # rsvg-convert
        cmd = ["rsvg-convert", "-w", str(w), "-h", str(h), "-o", str(png), str(svg)]
    subprocess.run(cmd, check=True, capture_output=True)


def main() -> int:
    ap = argparse.ArgumentParser(description="Export TV banner PNGs from the vector source.")
    ap.add_argument("--color", help="Force one field color for every variant (e.g. '#770000')")
    ap.add_argument("--check", action="store_true", help="Render to temp dir, don't write res/")
    args = ap.parse_args()

    if not VECTOR.exists():
        raise SystemExit(f"Vector source not found: {VECTOR}")
    tool = find_rasterizer()

    with tempfile.TemporaryDirectory() as td:
        for variant, res_dir, color_files in VARIANTS:
            colors = load_colors(color_files)
            svg_path = Path(td) / f"tv_banner_{variant}.svg"
            svg_path.write_text(vector_to_svg(colors, args.color))
            print(f"{variant}:")
            for bucket, (w, h) in BUCKETS.items():
                if args.check:
                    out = Path(td) / f"{variant}-mipmap-{bucket}.png"
                else:
                    out = res_dir / f"mipmap-{bucket}" / "tv_banner.png"
                    out.parent.mkdir(parents=True, exist_ok=True)
                rasterize(tool, svg_path, out, w, h)
                rel = out if args.check else out.relative_to(REPO)
                print(f"  {bucket:8} {w}x{h:<4} -> {rel}")

    print(f"\nDone via {tool}. Manifest should point android:banner at @mipmap/tv_banner.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
