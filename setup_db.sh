#!/bin/bash
# SplitEase database setup script for Linux/macOS

export PGPASSWORD=${PGPASSWORD:-root}
echo "Creating database 'spliteasedb'..."
psql -U postgres -h localhost -c "CREATE DATABASE spliteasedb;"

echo "Done."
