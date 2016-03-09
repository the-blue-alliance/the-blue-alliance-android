#! /usr/bin/env python
#
# Tests sending an upcoming_match notification via adb to The Blue Alliance
# Android app.

import test_notification


json_data = {"match_key": "2007cmp_sf1m3",
    "event_name": "Championship - Einstein Field",
    "team_keys": ["frc173","frc1319","frc1902","frc177","frc987","frc190"],
    "scheduled_time":12345,
    "predicted_time":122345}

if __name__ == '__main__':
    test_notification.upcoming_match_command(json_data)
