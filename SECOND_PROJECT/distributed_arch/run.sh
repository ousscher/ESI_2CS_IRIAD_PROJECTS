#!/bin/bash

mpiexec --hostfile hosts.txt -n 7 ../../../.venv/bin/python3 distributed_clustering.py
