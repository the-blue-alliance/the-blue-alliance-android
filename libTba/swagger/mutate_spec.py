#! /usr/bin/env python

import collections
import sys
import json
from optparse import OptionParser
from glob import glob

# Dict keys we merge into the same key in the real spec
MERGE_KEYS = ["paths", "definitions"]
HEADER_KEY = "headers"
ALL_PROPERTIES_KEY = "allProperties"


def update(d, u):
    for k, v in u.iteritems():
        if isinstance(v, collections.Mapping):
            r = update(d.get(k, {}), v)
            d[k] = r
        else:
            d[k] = u[k]
    return d


def main():
    parser = OptionParser()
    parser.add_option("-f", "--file", help="Swagger API spec file to append")
    parser.add_option("-j", "--json",
                      help="Directory containing json fragments")
    parser.add_option("-o", "--out",
                      help="File to write the modified swagger spec to")
    options, _ = parser.parse_args()

    swagger_data = None
    print("Using base file {}".format(options.file))
    with open(options.file, 'r') as datafile:
        swagger_data = json.loads(datafile.read())

    if not swagger_data or not isinstance(swagger_data, dict):
        print("Invalid swagger data")
        sys.exit(-1)

    for f in sorted(glob("{}/*.json".format(options.json))):
        data = None
        print("Reading from {}".format(f))
        with open(f, 'r') as datafile:
            data = json.loads(datafile.read())

        if not data or not isinstance(data, dict):
            print("Data file is invalid")
            sys.exit(-1)

        for key, value in data.iteritems():
            if key in MERGE_KEYS:
                if key not in swagger_data:
                    swagger_data[key] = {}
                update(swagger_data[key], value)
            elif key == HEADER_KEY:
                for path, obj in swagger_data["paths"].iteritems():
                    if not isinstance(obj.get("parameters", None), list):
                        obj["parameters"] = []
                    obj["parameters"].extend(value)
            elif key == ALL_PROPERTIES_KEY:
                for model, obj in swagger_data["definitions"].iteritems():
                    swagger_data["definitions"][model]["properties"].update(value)

    pretty = json.dumps(swagger_data, indent=2, sort_keys=True)
    print("Writing data back to {}".format(options.out))
    with open(options.out, 'w') as datafile:
        datafile.write(pretty)


if __name__ == "__main__":
    main()
