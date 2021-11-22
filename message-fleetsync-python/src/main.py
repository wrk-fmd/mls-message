#! /usr/bin/env python

import logging
import sys

import yaml

from fleetsync_service import FleetsyncService


def main(argv):
    if len(argv) <= 1:
        raise Exception('Usage: python main.py location/of/config.yml')

    with open(argv[1], 'r') as stream:
        config = yaml.safe_load(stream)

        if 'loglevel' in config:
            logging.basicConfig(level=config['loglevel'])

        service = None
        try:
            service = FleetsyncService(config)
            service.run()
        finally:
            if service is not None:
                service.close()


if __name__ == '__main__':
    main(sys.argv)
