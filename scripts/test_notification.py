#! /usr/bin/env python
#
# Tests sending a push notification via adb to The Blue Alliance Android app.
# This tests the app's notification response without having to make a debug TBA
# server on App Engine, set up GCM credentials, build a custom GCM RPC library,
# temporarily modify the app code for it, and other steps.
#
# This requires a Debug build of TBA Android which turns off the sender-
# permission requirement for its GCM IntentReceiver, thus accepting Intents
# from the Activity Manager tool "am".
# http://stackoverflow.com/questions/25931902/is-it-possible-to-simulate-a-gcm-receive-from-the-adb-shell-am-command-line-i/29425669#29425669
#
# NOTE: The shell quoting only works if "am broadcast ... 'JSON_TEXT'" is *one
# argument* to the adb shell command.
# http://stackoverflow.com/questions/27940118/sending-json-as-extra-data-in-an-android-broadcast-via-adb-gets-incorrectly-form/29428061#29428061
#
#
# Using this script from the command line (as opposed to importing it into
# another Python program) requires: pip install scriptine
#
# For usage info: "test_notification.py --help",
#     "test_notification.py upcoming_match -h", ...


import subprocess, json, random


# Sends a test push-notification over adb to TBA Android app.
#
# json can be a json-encoded string or a Python dict to json-encode.
#
# This should echo something like:
#    Broadcasting: Intent { act=com.google.android.c2dm.intent.RECEIVE cat=[com.thebluealliance.androidclient] (has extras) }
#    Broadcast completed: result=-1

def notify(message_type, json_data):
    if type(json_data) != str:
        json_data = json.dumps(json_data)

    # Quoting for "Chairman's".
    json_text = json_data.replace("'", """'"'"'""")

    template = """am broadcast -a com.google.android.c2dm.intent.RECEIVE \
        -c com.thebluealliance.androidclient \
        --es message_type %s \
        --es message_data '%s'"""
    command = template % (message_type, json_text)
    
    print "\nSending " + message_type + " broadcast"

    subprocess.call(["adb", "shell", command])

def get_notification_commands():
    commands = []

    commands.append('awards_posted_command')
    commands.append('schedule_updated_command')
    commands.append('alliance_selection_command')
    commands.append('event_down_command')
    commands.append('ping_command')
    commands.append('level_starting_command')
    commands.append('broadcast_command')
    commands.append('match_score_command')
    commands.append('upcoming_match_command')

    return commands


# ====== scriptine commands ======

upcoming_match_sample = {
    "match_key": "2014calb_qm17",
    "event_name": "Los Angeles Regional sponsored by The Roddenberry Foundation",
    "team_keys": ["frc971","frc148","frc254","frc1983","frc1114","frc1318"],
    "scheduled_time": 12345,
    "predicted_time": 123456
}

def upcoming_match_command(data=upcoming_match_sample):
    notify('upcoming_match', data)


match_score_sample = {
    "event_name": "New England FRC Region Championship",
    "match": {
      "comp_level": "f",
      "match_number": 1,
      "videos": [],
      "time_string": "3:18 PM",
      "set_number": 1,
      "key": "2014necmp_f1m1",
      "time": 1397330280,
      "score_breakdown": None,
      "alliances": {
        "blue": {"score": 154, "teams": ["frc177","frc230","frc4055"]},
        "red": {"score": 78, "teams": ["frc195","frc558","frc5122"]}
      },
      "event_key": "2014necmp"
    }
}

def match_score_command(data=match_score_sample):
    notify('match_score', data)


level_starting_sample = {
    "event_name": "Hawaii Regional",
    "comp_level": "f",
    "event_key": "2014hiho",
    "scheduled_time": 1397330280
}

def level_starting_command(data=level_starting_sample):
    notify('starting_comp_level', data)


alliance_selection_sample = {
    "event": {
      "key": "2014necmp",
      "end_date": "2014-04-12",
      "name": "New England FRC Region Championship",
      "short_name": "New England",
      "event_district_string": "New England",
      "event_district": 3,
      "location": "Boston, MA, USA",
      "event_code": "necmp",
      "year": 2014,
      "alliances": [
        {"picks": ["frc195","frc558","frc5122"]},
        {"picks": ["frc1153","frc125","frc4048"]},
        {"picks": ["frc230","frc177","frc4055"]},
        {"picks": ["frc716","frc78","frc811"]},
        {"picks": ["frc1519","frc3467","frc58"]},
        {"picks": ["frc131","frc175","frc1073"]},
        {"picks": ["frc228","frc3525","frc2168"]},
        {"picks": ["frc172","frc1058","frc2067"]}
      ],
      "event_type_string": "District Championship",
      "start_date": "2014-04-10",
      "event_type": 2
    }
}

def alliance_selection_command(data=alliance_selection_sample):
    notify('alliance_selection', data)


awards_posted_sample = {
    "event_name": "New England FRC Region Championship",
    "event_key": "2014necmp",
    "awards": [
      {
        "award_type": 0,
        "name": "Regional Chairman's Award",
        "recipient_list": [
          {"team_number": 2067, "awardee": None},
          {"team_number": 78, "awardee": None},
          {"team_number": 811, "awardee": None},
          {"team_number": 2648, "awardee": None}
        ],
        "year": 2014
      }
    ]
}

def awards_posted_command(data=awards_posted_sample):
    notify('awards_posted', data)


schedule_updated_sample = {
    "event_name": "Australia Regional",
    "first_match_time": 1397330280,
    "event_key": "2015ausy"
}

def schedule_updated_command(data=schedule_updated_sample):
    notify('schedule_updated', data)


district_points_updated_sample = {
    "district_name": "Pacific Northwest",
    "district_key": "2014pnw"
}

def district_points_updated_command(data=district_points_updated_sample):
    notify('district_points_updated', data)


ping_sample = {
    "title": "TBA Test Message",
    "desc": "This is a test message ensuring your device can receive push messages from The Blue Alliance",
    "url": "https://www.youtube.com/watch?v=RpSgUrsghv4"
}

def ping_command(data=ping_sample, url="", no_url=False):
    if url:
        data["url"] = url

    if no_url:
        del data["url"]
        
    notify('ping', data)

def broadcast_command(data=ping_sample, url="", no_url=False):
    if url:
        data["url"] = url

    if no_url:
        del data["url"]

    notify('broadcast', data)


sync_status_sample = {}

def sync_status_command(data=sync_status_sample):
    notify('sync_status', data)


event_down_sample = {
    "event_key": "2015cthar",
    "event_name": "Hartford"
}

def event_down_command(data=event_down_sample):
    notify('event_down', data)
    

def spam_command(count=10):
    commands = get_notification_commands()
    commandsToRun = random.sample(commands, min(count, len(commands)))

    for command in commandsToRun:
        globals()[command]()


def all_command():
    commands = get_notification_commands()

    for command in commands:
        globals()[command]()

# ====== main ======

if __name__ == '__main__':
    import scriptine
    scriptine.run()
