#!/usr/bin/env python3
"""Query Google Play Developer API for track and version info.

Usage:
    python3 scripts/play_api.py track-version <track> [--sa-path <path>]
    python3 scripts/play_api.py status [--sa-path <path>]

Outputs for track-version:
    <version_name>\t<version_code>

Requires either the `cryptography` package or `openssl` CLI for JWT signing.
"""

import argparse
import base64
import json
import os
import sys
import time
import urllib.error
import urllib.parse
import urllib.request

PACKAGE = "com.thebluealliance.androidclient"


def b64url(data: bytes) -> str:
    return base64.urlsafe_b64encode(data).rstrip(b"=").decode()


def get_access_token(sa_path: str) -> str:
    with open(sa_path) as f:
        sa = json.load(f)

    header = b64url(json.dumps({"alg": "RS256", "typ": "JWT"}).encode())
    now = int(time.time())
    claims = b64url(json.dumps({
        "iss": sa["client_email"],
        "scope": "https://www.googleapis.com/auth/androidpublisher",
        "aud": "https://oauth2.googleapis.com/token",
        "iat": now,
        "exp": now + 3600,
    }).encode())
    signing_input = f"{header}.{claims}".encode()

    try:
        from cryptography.hazmat.primitives import hashes, serialization
        from cryptography.hazmat.primitives.asymmetric import padding

        private_key = serialization.load_pem_private_key(
            sa["private_key"].encode(), password=None
        )
        signature = private_key.sign(
            signing_input, padding.PKCS1v15(), hashes.SHA256()
        )
    except ImportError:
        import subprocess
        import tempfile

        with tempfile.NamedTemporaryFile(
            mode="w", suffix=".pem", delete=False
        ) as kf:
            kf.write(sa["private_key"])
            kf_path = kf.name
        try:
            r = subprocess.run(
                ["openssl", "dgst", "-sha256", "-sign", kf_path],
                input=signing_input,
                capture_output=True,
                check=True,
            )
            signature = r.stdout
        finally:
            os.unlink(kf_path)

    jwt_token = f"{header}.{claims}.{b64url(signature)}"

    token_req = urllib.request.Request(
        "https://oauth2.googleapis.com/token",
        data=urllib.parse.urlencode({
            "grant_type": "urn:ietf:params:oauth:grant-type:jwt-bearer",
            "assertion": jwt_token,
        }).encode(),
        headers={"Content-Type": "application/x-www-form-urlencoded"},
    )
    with urllib.request.urlopen(token_req) as resp:
        return json.loads(resp.read())["access_token"]


def create_edit(access_token: str) -> str:
    req = urllib.request.Request(
        f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{PACKAGE}/edits",
        data=b"{}",
        headers={
            "Authorization": f"Bearer {access_token}",
            "Content-Type": "application/json",
        },
        method="POST",
    )
    with urllib.request.urlopen(req) as resp:
        return json.loads(resp.read())["id"]


def delete_edit(access_token: str, edit_id: str):
    url = f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{PACKAGE}/edits/{edit_id}"
    try:
        urllib.request.urlopen(
            urllib.request.Request(
                url,
                headers={"Authorization": f"Bearer {access_token}"},
                method="DELETE",
            )
        )
    except Exception:
        pass


def get_track(access_token: str, edit_id: str, track: str) -> dict:
    url = f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{PACKAGE}/edits/{edit_id}/tracks/{track}"
    req = urllib.request.Request(
        url, headers={"Authorization": f"Bearer {access_token}"}
    )
    with urllib.request.urlopen(req) as resp:
        return json.loads(resp.read())


def cmd_track_version(args):
    access_token = get_access_token(args.sa_path)
    edit_id = create_edit(access_token)
    try:
        track_data = get_track(access_token, edit_id, args.track)
    finally:
        delete_edit(access_token, edit_id)

    # Find the active release (completed or inProgress)
    for release in track_data.get("releases", []):
        if release.get("status") in ("completed", "inProgress"):
            name = release.get("name", "")
            codes = release.get("versionCodes", [])
            code = codes[0] if codes else ""
            print(f"{name}\t{code}")
            return

    # Fallback: first release
    releases = track_data.get("releases", [])
    if releases:
        name = releases[0].get("name", "")
        codes = releases[0].get("versionCodes", [])
        code = codes[0] if codes else ""
        print(f"{name}\t{code}")
    else:
        print("\t")


def cmd_status(args):
    access_token = get_access_token(args.sa_path)
    edit_id = create_edit(access_token)

    BOLD = "\033[1m"
    GREEN = "\033[0;32m"
    YELLOW = "\033[1;33m"
    RED = "\033[0;31m"
    DIM = "\033[2m"
    NC = "\033[0m"

    tracks = [
        ("production", "Production"),
        ("beta", "Open testing"),
        ("alpha", "Closed testing"),
        ("internal", "Internal testing"),
    ]

    try:
        for track_id, display_name in tracks:
            try:
                track_data = get_track(access_token, edit_id, track_id)
            except urllib.error.HTTPError as e:
                if e.code == 404:
                    print(
                        f"{BOLD}{display_name}{NC} {DIM}({track_id}){NC}: (empty)"
                    )
                    continue
                print(
                    f"Failed to fetch track {track_id}: {e.code} {e.read().decode()}",
                    file=sys.stderr,
                )
                continue

            releases = track_data.get("releases", [])
            print(f"{BOLD}{display_name}{NC} {DIM}({track_id}){NC}")
            if not releases:
                print("  (no releases)")
            for release in releases:
                status = release.get("status", "unknown")
                version_codes = release.get("versionCodes", [])
                name = release.get("name", "")
                fraction = release.get("userFraction")

                status_colors = {
                    "completed": GREEN,
                    "inProgress": YELLOW,
                    "draft": YELLOW,
                    "halted": RED,
                }
                color = status_colors.get(status, "")
                status_str = f"{color}{status}{NC}" if color else status

                version_str = ", ".join(str(v) for v in version_codes)
                line = (
                    f"  {status_str}  {BOLD}{name}{NC}"
                    if name
                    else f"  {status_str}"
                )
                if version_str:
                    line += f"  {DIM}(versionCode {version_str}){NC}"
                if fraction is not None:
                    line += f"  {YELLOW}{fraction:.0%} rollout{NC}"
                print(line)
            print()
    finally:
        delete_edit(access_token, edit_id)

    print(f"{DIM}Note: Google's review status is not available via the API.{NC}")
    print(f"{DIM}Check the Play Console for review progress.{NC}")


def resolve_sa_path(path: str) -> str:
    """Resolve service account path, checking local.properties if default."""
    if path != "play-service-account.json":
        return path
    # Check local.properties for override
    props_file = os.path.join(
        os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
        "local.properties",
    )
    if os.path.exists(props_file):
        with open(props_file) as f:
            for line in f:
                if line.startswith("play.service.account.key="):
                    return line.split("=", 1)[1].strip()
    return path


def main():
    parser = argparse.ArgumentParser(description="Google Play Developer API tools")
    parser.add_argument(
        "--sa-path",
        default="play-service-account.json",
        help="Path to service account JSON",
    )
    sub = parser.add_subparsers(dest="command")

    tv = sub.add_parser("track-version", help="Get version from a Play track")
    tv.add_argument("track", help="Track name (alpha, beta, production)")

    sub.add_parser("status", help="Show all track statuses")

    args = parser.parse_args()
    args.sa_path = resolve_sa_path(args.sa_path)

    if args.command == "track-version":
        cmd_track_version(args)
    elif args.command == "status":
        cmd_status(args)
    else:
        parser.print_help()
        sys.exit(1)


if __name__ == "__main__":
    main()
