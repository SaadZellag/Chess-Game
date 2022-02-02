#!/bin/bash
#Shell script to list the IPs on LAN
arp -n -a | awk '{print $2}' | tr -d '()'