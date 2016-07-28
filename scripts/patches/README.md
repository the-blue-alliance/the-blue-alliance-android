# Patches

This directory contains patches that are used in automated modifications.

## Details
 - `endpoints_remove_sitevar.patch` is a patch that comments out sitevar declarations in `mobile_main.py` in the main TBA repo. This is so we can generate a Cloud Endpoints client library, which parses through the python script. This will fail if it tried to access sitevars.
